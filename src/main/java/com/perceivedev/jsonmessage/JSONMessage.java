/**
 * 
 */
package com.perceivedev.jsonmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.ChatColor;

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
     * Converts this JSONChat object to a String representation of the JSON.
     * This is an alias of {@code toJSON().toString()}.
     */
    @Override
    public String toString() {
        return toJSON().toString();
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
            this.text = text;
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
            if (!color.isFormat()) {
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

}
