package me.rayzr522.jsonmessage.compat;

import com.google.gson.JsonObject;

public interface ChatComponent {
    /**
     * Creates a ChatComponentText from plain text
     *
     * @param message The text to convert to a chat component
     * @return The chat component
     */
    Object createComponent(String message);

    /**
     * Attempts to convert a String representing a JSON message into a usable object
     *
     * @param json The JSON to attempt to parse
     * @return The object representing the text in JSON form, or <code>null</code> if something went wrong converting the String to JSON data
     */
    Object fromJson(JsonObject json);
}
