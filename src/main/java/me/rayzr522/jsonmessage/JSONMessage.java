package me.rayzr522.jsonmessage;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.rayzr522.jsonmessage.compat.CompatManager;
import me.rayzr522.jsonmessage.compat.PlayerConnection;
import me.rayzr522.jsonmessage.compat.TitlePacket;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 * This is a complete JSON message builder class. To create a new JSONMessage do
 * {@link #create(String)}
 *
 * @author Rayzr
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class JSONMessage {
    private static final BiMap<ChatColor, String> stylesToNames;
    private static final CompatManager compatManager = new CompatManager();

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


    private final List<MessagePart> parts = new ArrayList<>();
    private int centeringStartIndex = -1;

    /**
     * Creates a new {@link JSONMessage} object
     *
     * @param text The text to start with
     */
    private JSONMessage(String text) {
        parts.add(new MessagePart(text));
    }

    /**
     * Creates a new {@link JSONMessage} object
     *
     * @param text The text to start with
     * @return A new {@link JSONMessage} object
     */
    public static JSONMessage create(String text) {
        return new JSONMessage(text);
    }

    /**
     * Creates a new {@link JSONMessage} object
     *
     * @return A new {@link JSONMessage} object
     */
    public static JSONMessage create() {
        return create("");
    }

    /**
     * Sends an action bar message
     *
     * @param message The message to send
     * @param players The players you want to send it to
     */
    public static void actionbar(String message, Player... players) {
        JSONMessage.create(message).actionbar(players);
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
     * Converts this {@link JSONMessage} instance to actual JSON
     *
     * @return The JSON representation of this {@link JSONMessage}
     */
    public JsonObject toJSON() {
        JsonObject obj = new JsonObject();

        obj.addProperty("text", "");

        JsonArray array = new JsonArray();

        parts.stream()
                .map(MessagePart::toJSON)
                .forEach(array::add);

        obj.add("extra", array);

        return obj;
    }

    /**
     * Converts this {@link JSONMessage} object to a String representation of the JSON.
     * This is an alias of {@code toJSON().toString()}.
     */
    @Override
    public String toString() {
        return toJSON().toString();
    }

    /**
     * Converts this {@link JSONMessage} object to the legacy formatting system, which
     * uses formatting codes (like &amp;6, &amp;l, &amp;4, etc.)
     *
     * @return This {@link JSONMessage} instance {@link JSONMessage} in legacy format
     */
    public String toLegacy() {
        StringBuilder output = new StringBuilder();

        parts.stream()
                .map(MessagePart::toLegacy)
                .forEach(output::append);

        return output.toString();
    }

    /**
     * Sends this {@link JSONMessage} to all the players specified
     *
     * @param players The players you want to send this to
     */
    public void send(Player... players) {
        Object component = compatManager.getChatComponent().fromJson(toJSON());

        compatManager.getPlayerConnection().sendPacket(
                compatManager.getChatPacket().createTextPacket(component),
                players
        );
    }

    /**
     * Sends this as a title to all the players specified
     *
     * @param fadeIn  How many ticks to fade in
     * @param stay    How many ticks to stay
     * @param fadeOut How many ticks to fade out
     * @param players The players to send this to
     */
    public void title(int fadeIn, int stay, int fadeOut, Player... players) {
        PlayerConnection playerConnectionCompat = compatManager.getPlayerConnection();
        TitlePacket titlePacketCompat = compatManager.getTitlePacket();
        Object component = compatManager.getChatComponent().fromJson(toJSON());

        playerConnectionCompat.sendPacket(
                titlePacketCompat.createTitleTimesPacket(fadeIn, stay, fadeOut),
                players
        );
        playerConnectionCompat.sendPacket(
                titlePacketCompat.createTitleTextPacket(component),
                players
        );
    }

    /**
     * Sends this as a subtitle to all the players specified. Must be used after sending a {@link #title(int, int, int, Player...) title}.
     *
     * @param players The players to send this to
     */
    public void subtitle(Player... players) {
        Object component = compatManager.getChatComponent().fromJson(toJSON());

        compatManager.getPlayerConnection().sendPacket(
                compatManager.getTitlePacket().createSubtitlePacket(component),
                players
        );
    }

    /**
     * Sends an action bar message
     *
     * @param players The players you want to send this to
     */
    public void actionbar(Player... players) {
        Object component = compatManager.getChatComponent().createComponent(toLegacy());

        compatManager.getPlayerConnection().sendPacket(
                compatManager.getChatPacket().createActionbarPacket(component),
                players
        );
    }

    /**
     * Sets the color of the current message part.
     *
     * @param color The color to set
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage color(ChatColor color) {
        if (!color.isColor())
            throw new IllegalArgumentException(color.name() + " is not a color.");

        last().setColor(color);
        return this;
    }

    /**
     * Sets the color of the current message part.
     * <br>If the provided color is a hex color ({@code #rrggbb}) but the major version of MC is older than 1.16 will this
     * default to the color WHITE.
     *
     * @param color The color to set
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage color(String color) {
        return color(color, ChatColor.WHITE);
    }

    /**
     * Sets the color of the current message part.
     * <br>If the provided color is a hex color ({@code #rrggbb}) but the major version of MC is older than 1.16 will the provided
     * default ChatColor be used instead.
     *
     * @param color The color to set
     * @param def   The default ChatColor to use, when MC version is older than 1.16
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage color(String color, ChatColor def) {
        if (color.startsWith("#") && ReflectionHelper.getVersion() < 16)
            return color(def);

        last().setColor(color);
        return this;
    }

    /**
     * Sets the font of the current message part.
     * <br>When this is used on versions older than 1.16 will this do nothing.
     *
     * @param font The font to set
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage font(String font) {
        if (ReflectionHelper.getVersion() < 16)
            return this;

        last().setFont(font);
        return this;
    }

    /**
     * Adds a style to the current message part.
     *
     * @param style The style to add
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage style(ChatColor style) {
        last().addStyle(style);
        return this;
    }

    /**
     * Makes the text run a command.
     *
     * @param command The command to run
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage runCommand(String command) {
        last().setOnClick(ClickEvent.runCommand(command));
        return this;
    }

    /**
     * Makes the text suggest a command.
     *
     * @param command The command to suggest
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage suggestCommand(String command) {
        last().setOnClick(ClickEvent.suggestCommand(command));
        return this;
    }

    /**
     * Opens a URL.
     *
     * @param url The url to open
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage openURL(String url) {
        last().setOnClick(ClickEvent.openURL(url));
        return this;
    }

    /**
     * Copies the provided text to the Clipboard of the player.
     * <br>When this is used on versions older than 1.15 will this default to {@link #suggestCommand(String) suggestCommand(String)}.
     *
     * @param text The text to copy
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage copyText(String text) {
        last().setOnClick(ClickEvent.copyText(text));
        return this;
    }

    /**
     * Changes the page of a book. Using this in a non-book context is useless
     * and will probably error.
     *
     * @param page The page to change to
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage changePage(int page) {
        last().setOnClick(ClickEvent.changePage(page));
        return this;
    }

    /**
     * Shows text when you hover over it
     *
     * @param text The text to show
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage tooltip(String text) {
        last().setOnHover(HoverEvent.showText(text));
        return this;
    }

    /**
     * Shows text when you hover over it
     *
     * @param message The text to show
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage tooltip(JSONMessage message) {
        last().setOnHover(HoverEvent.showText(message));
        return this;
    }

    /**
     * Shows an achievement when you hover over it
     *
     * @param id The id of the achievement
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage achievement(String id) {
        last().setOnHover(HoverEvent.showAchievement(id));
        return this;
    }

    /**
     * Adds another part to this {@link JSONMessage}
     *
     * @param text The text to start the next {@link MessagePart} with
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage then(String text) {
        return then(new MessagePart(text));
    }

    /**
     * Adds another part to this {@link JSONMessage}
     *
     * @param nextPart The next {@link MessagePart}
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage then(MessagePart nextPart) {
        parts.add(nextPart);
        return this;
    }

    /**
     * Adds a horizontal bar to the message of the given length
     *
     * @param length The length of the horizontal bar
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage bar(int length) {
        return then(Strings.repeat("-", length)).color(ChatColor.DARK_GRAY).style(ChatColor.STRIKETHROUGH);
    }

    /**
     * Adds a horizontal bar to the message that's 53 characters long. This is
     * the default width of the player's chat window.
     *
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage bar() {
        return bar(53);
    }

    /**
     * Adds a blank line to the message
     *
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage newline() {
        return then("\n");
    }

    /**
     * Sets the starting point to begin centering JSONMessages.
     *
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage beginCenter() {
        // Start with the NEXT message part.
        centeringStartIndex = parts.size();
        return this;
    }

    /**
     * Ends the centering of the JSONMessage text.
     *
     * @return This {@link JSONMessage} instance
     */
    public JSONMessage endCenter() {
        int current = centeringStartIndex;

        while (current < parts.size()) {
            Vector<MessagePart> currentLine = new Vector<>();
            int totalLineLength = 0;

            for (; ; current++) {
                MessagePart part = current < parts.size() ? parts.get(current) : null;
                String raw = part == null ? null : ChatColor.stripColor(part.toLegacy());
                int rawLength = raw == null ? 0 : raw.length();

                if (current >= parts.size() || totalLineLength + rawLength >= 53) {
                    int padding = Math.max(0, (53 - totalLineLength) / 2);
                    currentLine.firstElement().setText(Strings.repeat(" ", padding) + currentLine.firstElement().getText());
                    currentLine.lastElement().setText(currentLine.lastElement().getText() + "\n");
                    currentLine.clear();
                    break;
                }

                totalLineLength += rawLength;
                currentLine.add(part);
            }
        }

        MessagePart last = parts.get(parts.size() - 1);
        last.setText(last.getText().substring(0, last.getText().length() - 1));

        centeringStartIndex = -1;

        return this;
    }

    ///////////////////////////
    // BEGIN UTILITY CLASSES //
    ///////////////////////////

    /**
     * Represents the JSON format that all click/hover events in JSON messages must follow.
     * <br>
     * <br>
     * <a href="http://minecraft.gamepedia.com/Commands#Raw_JSON_text">Reference</a>
     *
     * @author Rayzr
     */
    public static class MessageEvent {

        private String action;
        private Object value;

        public MessageEvent(String action, Object value) {
            this.action = action;
            this.value = value;
        }

        /**
         * @return A {@link JsonObject} representing the properties of this {@link MessageEvent}
         */
        public JsonObject toJSON() {
            JsonObject obj = new JsonObject();
            obj.addProperty("action", action);
            /*
             * MC 1.16 changed "value" to "contents", but only for Hover events... Don't ask why.
             * Since this lib only has tooltip and achievement can we simply check if action starts with "show_"
             */
            String valueType = (ReflectionHelper.getVersion() >= 16 && action.startsWith("show_")) ? "contents" : "value";

            if (value instanceof JsonElement) {
                obj.add(valueType, (JsonElement) value);
            } else {
                obj.addProperty(valueType, value.toString());
            }
            return obj;
        }

        /**
         * @return The action
         */
        public String getAction() {
            return action;
        }

        /**
         * @param action The action to set
         */
        public void setAction(String action) {
            this.action = action;
        }

        /**
         * @return The value
         */
        public Object getValue() {
            return value;
        }

        /**
         * @param value The value to set
         */
        public void setValue(Object value) {
            this.value = value;
        }

    }

    public static class ClickEvent {

        /**
         * Runs a command.
         *
         * @param command The command to run
         * @return The {@link MessageEvent}
         */
        public static MessageEvent runCommand(String command) {
            return new MessageEvent("run_command", command);
        }

        /**
         * Suggests a command by inserting it in chat.
         *
         * @param command The command to suggest
         * @return The {@link MessageEvent}
         */
        public static MessageEvent suggestCommand(String command) {
            return new MessageEvent("suggest_command", command);
        }

        /**
         * Requires web links to be enabled on the client.
         *
         * @param url The url to open
         * @return The {@link MessageEvent}
         */
        public static MessageEvent openURL(String url) {
            return new MessageEvent("open_url", url);
        }

        /**
         * Only used with written books.
         *
         * @param page The page to switch to
         * @return The {@link MessageEvent}
         */
        public static MessageEvent changePage(int page) {
            return new MessageEvent("change_page", page);
        }

        /**
         * Copies the provided text to the clipboard of the player.
         * <br>When used on versions older than 1.15 will this {@link #suggestCommand(String) suggest the text} instead.
         *
         * @param text The text to copy.
         * @return The {@link MessageEvent}
         */
        public static MessageEvent copyText(String text) {
            if (ReflectionHelper.getVersion() < 15)
                return suggestCommand(text);

            return new MessageEvent("copy_to_clipboard", text);
        }

    }

    public static class HoverEvent {

        /**
         * Shows text when you hover over it
         *
         * @param text The text to show
         * @return The {@link MessageEvent}
         */
        public static MessageEvent showText(String text) {
            return new MessageEvent("show_text", text);
        }

        /**
         * Shows text when you hover over it
         *
         * @param message The {@link JSONMessage} to show
         * @return The {@link MessageEvent}
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
         * @param id The id of the achievement
         * @return The {@link MessageEvent}
         */
        public static MessageEvent showAchievement(String id) {
            return new MessageEvent("show_achievement", id);
        }

    }

    /**
     * Defines a section of the message, and represents the format that all JSON messages must follow in Minecraft.
     * <br>
     * <br>
     * <a href="http://minecraft.gamepedia.com/Commands#Raw_JSON_text">Reference</a>
     *
     * @author Rayzr
     */
    public static class MessagePart {

        private final List<ChatColor> styles = new ArrayList<>();
        private MessageEvent onClick;
        private MessageEvent onHover;
        private String color;
        private ChatColor legacyColor;
        private String font;
        private String text;

        public MessagePart(String text) {
            this.text = text == null ? "null" : text;
        }

        /**
         * Converts this {@link MessagePart} into a {@link JsonObject}
         *
         * @return The Minecraft-compatible {@link JsonObject}
         */
        public JsonObject toJSON() {
            Objects.requireNonNull(text);

            JsonObject obj = new JsonObject();
            obj.addProperty("text", text);

            if (color != null && !color.isEmpty()) {
                obj.addProperty("color", color.toLowerCase());
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

            if (font != null) {
                obj.addProperty("font", font);
            }

            return obj;

        }

        /**
         * @return This {@link MessagePart} in legacy-style color/formatting codes
         */
        public String toLegacy() {
            StringBuilder output = new StringBuilder();
            ChatColor legacyColor = getColor();

            if (legacyColor != null) {
                output.append(legacyColor);
            }

            styles.stream()
                    .map(ChatColor::toString)
                    .forEach(output::append);

            return output.append(text).toString();
        }

        /**
         * @return The click event bound
         */
        public MessageEvent getOnClick() {
            return onClick;
        }

        /**
         * @param onClick The new click event to bind
         */
        public void setOnClick(MessageEvent onClick) {
            this.onClick = onClick;
        }

        /**
         * @return The hover event bound
         */
        public MessageEvent getOnHover() {
            return onHover;
        }

        /**
         * @param onHover The new hover event to bind
         */
        public void setOnHover(MessageEvent onHover) {
            this.onHover = onHover;
        }

        /**
         * @return The color
         */
        public String getColorValue() {
            return color;
        }

        /**
         * @return The color
         * @deprecated Use {@link #getColorValue()} instead
         */
        @SuppressWarnings("DeprecatedIsStillUsed")
        @Deprecated
        public ChatColor getColor() {
            if (legacyColor != null) {
                return legacyColor;
            }

            if (this.color.startsWith("#") && ReflectionHelper.getVersion() < 16)
                throw new IllegalStateException("Custom Hex colors can only be used in Minecraft 1.16 or newer!");

            try {
                return ChatColor.valueOf(this.color.toUpperCase());
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * @param color The color to set
         * @deprecated Use {@link #setColor(String)} instead
         */
        @SuppressWarnings("DeprecatedIsStillUsed")
        @Deprecated
        public void setColor(ChatColor color) {
            setColor(color == null ? null : color.name().toLowerCase());
            setLegacyColor(color);
        }

        /**
         * @param color The legacy ChatColor to set
         * @deprecated Use {@link #setColor(String)} instead
         */
        @Deprecated
        public void setLegacyColor(ChatColor color) {
            legacyColor = color;
        }

        /**
         * @param color The color to set
         */
        public void setColor(String color) {
            if (color != null && color.isEmpty()) {
                throw new IllegalArgumentException("Color cannot be null!");
            }
            this.color = color;
        }

        /**
         * @return The list of styles
         */
        public List<ChatColor> getStyles() {
            return styles;
        }

        /**
         * @param style The new style to add
         */
        public void addStyle(ChatColor style) {
            if (style == null) {
                throw new IllegalArgumentException("Style cannot be null!");
            }
            if (!style.isFormat()) {
                throw new IllegalArgumentException(style.name() + " is not a style!");
            }
            styles.add(style);
        }

        /**
         * @return The font used
         */
        public String getFont() {
            return font;
        }

        /**
         * @param font The font to use
         */
        public void setFont(String font) {
            this.font = font;
        }

        /**
         * @return The raw text
         */
        public String getText() {
            return text;
        }

        /**
         * @param text The raw text to set
         */
        public void setText(String text) {
            this.text = text;
        }

    }
}