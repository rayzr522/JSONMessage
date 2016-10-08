/**
 * 
 */
package com.perceivedev.jsonmessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * This is a complete JSON message builder class. To create a new JSONMessage do
 * {@link #create(String)}
 * 
 * @author Rayzr
 *
 */
public class JSONMessage {

    private static final BiMap<ChatColor, String> stylesToNames;

    static {
        ImmutableBiMap.Builder<ChatColor, String> builder = ImmutableBiMap.builder();
        for (final ChatColor style : ChatColor.values()) {
            if (!style.isFormat()) {
                continue;
            }

            String styleName;
            switch (style) {
            case MAGIC:
                styleName = "obfuscated";
                break;
            case UNDERLINE:
                styleName = "underlined";
                break;
            default:
                styleName = style.name().toLowerCase();
                break;
            }

            builder.put(style, styleName);
        }
        stylesToNames = builder.build();
    }

    private List<MessagePart> parts = new ArrayList<MessagePart>();

    /**
     * Creates a new JSONChat object
     * 
     * @param text the text to start with
     */
    private JSONMessage(String text) {
        parts.add(new MessagePart(text));
    }

    /**
     * Creates a new JSONChat object
     * 
     * @param text the text to start with
     */
    public static JSONMessage create(String text) {
        return new JSONMessage(text);
    }

    /**
     * Creates a new JSONChat object
     */
    public static JSONMessage create() {
        return create("");
    }

    /**
     * Sends an action bar message
     * 
     * @param players the players you want to send it to
     */
    public static void actionbar(String message, Player... players) {

        ReflectionHelper.sendPacket(ReflectionHelper.createActionbarPacket(ChatColor.translateAlternateColorCodes('&', message)), players);

    }

    /**
     * @return The latest {@link MessagePart}
     * @throws ArrayIndexOutOfBoundsException If {@code parts.size() <= 0}.
     */
    public MessagePart last() {
        if (parts.size() <= 0) {
            throw new ArrayIndexOutOfBoundsException("No MessageParts exist!");
        }
        return parts.get(parts.size() - 1);
    }

    /**
     * Converts this JSONChat instance to actual JSON
     * 
     * @return The JSON representation of this
     */
    public JsonObject toJSON() {

        JsonObject obj = new JsonObject();

        obj.addProperty("text", "");

        JsonArray array = new JsonArray();
        for (MessagePart part : parts) {
            array.add(part.toJSON());
        }
        obj.add("extra", array);

        return obj;
    }

    /**
     * Converts this JSONMessage object to a String representation of the JSON.
     * This is an alias of {@code toJSON().toString()}.
     */
    @Override
    public String toString() {
        return toJSON().toString();
    }
    
    /**
     * Converts this JSONMessage object to the legacy formatting system, which uses formatting codes (like &6, &l, &4, etc.)
     * 
     * @return This JSONMessage in legacy format
     */
    public String toLegacy() {
        StringBuilder output = new StringBuilder();
        for (MessagePart part : parts) {
            output.append(part.toLegacy());
        }
        return output.toString();
    }

    /**
     * Sends this JSONMessage to all the players specified
     * 
     * @param players the players you want to send this to
     */
    public void send(Player... players) {

        ReflectionHelper.sendPacket(ReflectionHelper.createTextPacket(toString()), players);

    }

    /**
     * Sends this as a title to all the players specified
     * 
     * @param fadeIn how many ticks to fade in
     * @param stay how many ticks to stay
     * @param fadeOut how many ticks to fade out
     * @param players the players to send it to
     */
    public void title(int fadeIn, int stay, int fadeOut, Player... players) {

        ReflectionHelper.sendPacket(ReflectionHelper.createTitleTimesPacket(fadeIn, stay, fadeOut), players);
        ReflectionHelper.sendPacket(ReflectionHelper.createTitlePacket(toString()), players);

    }

    /**
     * Sends this as a subtitle to all the players specified
     * 
     * @param fadeIn how many ticks to fade in
     * @param stay how many ticks to stay
     * @param fadeOut how many ticks to fade out
     * @param players the players to send it to
     */
    public void subtitle(Player... players) {

        ReflectionHelper.sendPacket(ReflectionHelper.createSubtitlePacket(toString()), players);

    }
    
    /**
     * Sends an action bar message
     * 
     * @param players the players you want to send this to
     */
    public void actionbar(Player... players) {
        actionbar(toLegacy(), players);
    }

    /**
     * Sets the color of the current message part.
     * 
     * @param color the color to set
     * @return this
     */
    public JSONMessage color(ChatColor color) {
        last().setColor(color);
        return this;
    }

    /**
     * Adds a style to the current message part.
     * 
     * @param style the style to add
     * @return this
     */
    public JSONMessage style(ChatColor style) {
        last().addStyle(style);
        return this;
    }

    /**
     * Makes the text run a command.
     * 
     * @param cmd the command to run
     * @return this
     */
    public JSONMessage runCommand(String cmd) {
        last().setOnClick(ClickEvent.runCommand(cmd));
        return this;
    }

    /**
     * Makes the text suggest a command.
     * 
     * @param cmd the command to suggest
     * @return this
     */
    public JSONMessage suggestCommand(String cmd) {
        last().setOnClick(ClickEvent.suggestCommand(cmd));
        return this;
    }

    /**
     * Opens a URL.
     * 
     * @param url the url to open
     * @return this
     */
    public JSONMessage openURL(String url) {
        last().setOnClick(ClickEvent.openURL(url));
        return this;
    }

    /**
     * Changes the page of a book. Using this in a non-book context is useless
     * and will probably error.
     * 
     * @param page the page to change to
     * @return this
     */
    public JSONMessage changePage(int page) {
        last().setOnClick(ClickEvent.changePage(page));
        return this;
    }

    /**
     * Shows text when you hover over it
     * 
     * @param text the text to show
     * @return this
     */
    public JSONMessage tooltip(String text) {
        last().setOnHover(HoverEvent.showText(text));
        return this;
    }

    /**
     * Shows text when you hover over it
     * 
     * @param message the text to show
     * @return this
     */
    public JSONMessage tooltip(JSONMessage message) {
        last().setOnHover(HoverEvent.showText(message));
        return this;
    }

    /**
     * Shows an achievement when you hover over it
     * 
     * @param id the id of the achievement
     * @return
     */
    public JSONMessage achievement(String id) {
        last().setOnHover(HoverEvent.showAchievement(id));
        return this;
    }

    /**
     * Adds another part to this JSONChat
     * 
     * @param text the text to start with
     * @return this
     */
    public JSONMessage then(String text) {
        return then(new MessagePart(text));
    }

    /**
     * Adds another part to this JSONChat
     * 
     * @param text the next part
     * @return this
     */
    public JSONMessage then(MessagePart nextPart) {
        parts.add(nextPart);
        return this;
    }

    /**
     * Adds a horizontal bar to the message of the given length
     * 
     * @param length the length of the horizontal bar
     * @return this
     */
    public JSONMessage bar(int length) {
        return then(Strings.repeat("-", length)).color(ChatColor.DARK_GRAY).style(ChatColor.STRIKETHROUGH);
    }

    /**
     * Adds a horizontal bar to the message that's 53 characters long. This is
     * the default width of the player's chat window.
     * 
     * @return this
     */
    public JSONMessage bar() {
        return bar(53);
    }

    /**
     * Adds a blank line to the message
     * 
     * @return this
     */
    public JSONMessage newline() {
        return then("\n");
    }

    ///////////////////////////
    // BEGIN UTILITY CLASSES //
    ///////////////////////////
    /**
     * Defines a section of the message.
     * 
     * @author Rayzr
     *
     */
    public class MessagePart {

        private MessageEvent    onClick;
        private MessageEvent    onHover;
        private List<ChatColor> styles = new ArrayList<ChatColor>();
        private ChatColor       color;
        private String          text;

        public MessagePart(String text) {
            this.text = text == null ? "null" : text;
        }

        public JsonObject toJSON() {
            Objects.requireNonNull(text);

            JsonObject obj = new JsonObject();
            obj.addProperty("text", text);

            if (color != null) {
                obj.addProperty("color", color.name().toLowerCase());
            }

            for (ChatColor style : styles) {
                obj.addProperty(stylesToNames.get(style), true);
            }

            if (onClick != null) {
                obj.add("clickEvent", onClick.toJSON());
            }

            if (onHover != null) {
                obj.add("hoverEvent", onHover.toJSON());
            }

            return obj;

        }

        /**
         * @return
         */
        public String toLegacy() {
            StringBuilder output = new StringBuilder();
            if (color != null) {
                output.append(color.toString());
            }
            for (ChatColor style : styles) {
                output.append(style.toString());
            }
            return output.append(text).toString();
        }

        /**
         * @return the onClick event
         */
        public MessageEvent getOnClick() {
            return onClick;
        }

        /**
         * @param onClick the onClick event to set
         */
        public void setOnClick(MessageEvent onClick) {
            this.onClick = onClick;
        }

        /**
         * @return the onHover event
         */
        public MessageEvent getOnHover() {
            return onHover;
        }

        /**
         * @param onHover the onHover event to set
         */
        public void setOnHover(MessageEvent onHover) {
            this.onHover = onHover;
        }

        /**
         * @return the color
         */
        public ChatColor getColor() {
            return color;
        }

        /**
         * @param color the color to set
         */
        public void setColor(ChatColor color) {
            if (!color.isColor()) {
                throw new IllegalArgumentException(color.name() + " is not a color!");
            }
            this.color = color;
        }

        /**
         * @return the styles
         */
        public List<ChatColor> getStyles() {
            return styles;
        }

        /**
         * Adds a style
         * 
         * @param style the style to add
         */
        public void addStyle(ChatColor style) {
            if (style == null) {
                throw new IllegalArgumentException("Style cannot be null!");
            }
            if (!style.isFormat()) {
                throw new IllegalArgumentException(color.name() + " is not a style!");
            }
            styles.add(style);
        }

        /**
         * @return the text
         */
        public String getText() {
            return text;
        }

        /**
         * @param text the text to set
         */
        public void setText(String text) {
            this.text = text;
        }

    }

    public static class MessageEvent {

        private String action;
        private Object value;

        public MessageEvent(String action, Object value) {

            this.action = action;
            this.value = value;

        }

        /**
         * @return
         */
        public JsonObject toJSON() {
            JsonObject obj = new JsonObject();
            obj.addProperty("action", action);
            if (value instanceof JsonElement) {
                obj.add("value", (JsonElement) value);
            } else {
                obj.addProperty("value", value.toString());
            }
            return obj;
        }

        /**
         * @return the action
         */
        public String getAction() {
            return action;
        }

        /**
         * @param action the action to set
         */
        public void setAction(String action) {
            this.action = action;
        }

        /**
         * @return the value
         */
        public Object getValue() {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(Object value) {
            this.value = value;
        }

    }

    public static class ClickEvent {

        /**
         * Runs a command.
         * 
         * @param cmd the command to run
         * @return The MessageEvent
         */
        public static MessageEvent runCommand(String cmd) {
            return new MessageEvent("run_command", cmd);
        }

        /**
         * Suggests a command by putting inserting it in chat.
         * 
         * @param cmd the command to suggest
         * @return The MessageEvent
         */
        public static MessageEvent suggestCommand(String cmd) {
            return new MessageEvent("suggest_command", cmd);
        }

        /**
         * Requires web links to be enabled on the client.
         * 
         * @param url the url to open
         * @return The MessageEvent
         */
        public static MessageEvent openURL(String url) {
            return new MessageEvent("open_url", url);
        }

        /**
         * Only used with written books.
         * 
         * @param page the page to switch to
         * @return The MessageEvent
         */
        public static MessageEvent changePage(int page) {
            return new MessageEvent("change_page", page);
        }

    }

    public static class HoverEvent {

        /**
         * Shows text when you hover over it
         * 
         * @param text the text to show
         * @return The MessageEvent
         */
        public static MessageEvent showText(String text) {
            return new MessageEvent("show_text", text);
        }

        /**
         * Shows text when you hover over it
         * 
         * @param chat the JSON message to show
         * @return The MessageEvent
         */
        public static MessageEvent showText(JSONMessage message) {
            JsonArray arr = new JsonArray();
            arr.add(new JsonPrimitive(""));
            arr.add(message.toJSON());
            return new MessageEvent("show_text", arr);
        }

        /**
         * Shows an achievement when you hover over it
         * 
         * @param id the id over the achievement
         * @return
         */
        public static MessageEvent showAchievement(String id) {
            return new MessageEvent("show_achievement", id);
        }

    }

    private static class ReflectionHelper {

        private static Class<?>       craftPlayer;

        private static Constructor<?> chatComponentText;
        private static Class<?>       packetPlayOutChat;
        private static Class<?>       packetPlayOutTitle;
        private static Class<?>       iChatBaseComponent;
        private static Class<?>       titleAction;

        private static Field          connection;
        private static Method         getHandle;
        private static Method         sendPacket;
        private static Method         stringToChat;

        private static Object         actionTitle;
        private static Object         actionSubtitle;

        private static String         version;

        private static boolean        SETUP = false;

        static {

            if (!SETUP) {

                String[] split = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
                version = split[split.length - 1];

                try {

                    SETUP = true;

                    craftPlayer = getClass("{obc}.entity.CraftPlayer");
                    getHandle = craftPlayer.getMethod("getHandle");
                    connection = getHandle.getReturnType().getField("playerConnection");
                    sendPacket = connection.getType().getMethod("sendPacket", getClass("{nms}.Packet"));

                    chatComponentText = getClass("{nms}.ChatComponentText").getConstructor(String.class);

                    iChatBaseComponent = getClass("{nms}.IChatBaseComponent");

                    if (getVersion() > 7) {
                        stringToChat = getClass("{nms}.IChatBaseComponent$ChatSerializer").getMethod("a", String.class);
                    } else {
                        stringToChat = getClass("{nms}.ChatSerializer").getMethod("a", String.class);
                    }

                    packetPlayOutChat = getClass("{nms}.PacketPlayOutChat");
                    packetPlayOutTitle = getClass("{nms}.PacketPlayOutTitle");

                    titleAction = getClass("{nms}.PacketPlayOutTitle$EnumTitleAction");

                    actionTitle = titleAction.getField("TITLE").get(null);
                    actionSubtitle = titleAction.getField("SUBTITLE").get(null);

                } catch (Exception e) {
                    e.printStackTrace();
                    SETUP = false;
                }

            }

        }

        public static void sendPacket(Object packet, Player... players) {
            if (!SETUP) {
                throw new IllegalStateException("ReflectionHelper is not set up!");
            }
            if (packet == null) {
                return;
            }

            for (Player player : players) {
                try {
                    sendPacket.invoke(connection.get(getHandle.invoke(player)), packet);
                } catch (Exception e) {
                    System.err.println("Failed to send packet");
                    e.printStackTrace();
                }
            }

        }

        public static Object createActionbarPacket(String message) {
            if (!SETUP) {
                throw new IllegalStateException("ReflectionHelper is not set up!");
            }
            Object packet = createTextPacket(message);
            set("b", packet, (byte) 2);
            return packet;
        }

        public static Object createTextPacket(String message) {
            if (!SETUP) {
                throw new IllegalStateException("ReflectionHelper is not set up!");
            }
            try {
                Object packet = packetPlayOutChat.newInstance();
                set("a", packet, fromJson(message));
                set("b", packet, (byte) 1);
                return packet;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        public static Object createTitlePacket(String message) {
            if (!SETUP) {
                throw new IllegalStateException("ReflectionHelper is not set up!");
            }
            try {
                Object packet = packetPlayOutTitle.getConstructor(titleAction, iChatBaseComponent).newInstance(actionTitle, fromJson(message));
                return packet;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        public static Object createSubtitlePacket(String message) {
            if (!SETUP) {
                throw new IllegalStateException("ReflectionHelper is not set up!");
            }
            try {
                Object packet = packetPlayOutTitle.getConstructor(titleAction, iChatBaseComponent).newInstance(actionSubtitle, fromJson(message));
                return packet;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        public static Object createTitleTimesPacket(int fadeIn, int stay, int fadeOut) {
            if (!SETUP) {
                throw new IllegalStateException("ReflectionHelper is not set up!");
            }
            try {
                Object packet = packetPlayOutTitle.getConstructor(int.class, int.class, int.class).newInstance(fadeIn, stay, fadeOut);
                return packet;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        public static Object componentText(String msg) {
            if (!SETUP) {
                throw new IllegalStateException("ReflectionHelper is not set up!");
            }
            try {
                return chatComponentText.newInstance(msg);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        public static Object fromJson(String json) {
            if (!SETUP) {
                throw new IllegalStateException("ReflectionHelper is not set up!");
            }
            if (!json.trim().startsWith("{")) {
                return componentText(json);
            }

            try {
                return stringToChat.invoke(null, json);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        public static Class<?> getClass(String path) throws ClassNotFoundException {
            if (!SETUP) {
                throw new IllegalStateException("ReflectionHelper is not set up!");
            }
            return Class.forName(path.replace("{nms}", "net.minecraft.server." + version).replace("{obc}", "org.bukkit.craftbukkit." + version));
        }

        public static void set(String field, Object o, Object v) {
            try {
                Field f = o.getClass().getDeclaredField(field);
                f.setAccessible(true);
                f.set(o, v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static int getVersion() {
            if (!SETUP) {
                throw new IllegalStateException("ReflectionHelper is not set up!");
            }
            try {
                return Integer.parseInt(version.split("_")[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 10;
            }

        }

    }

}
