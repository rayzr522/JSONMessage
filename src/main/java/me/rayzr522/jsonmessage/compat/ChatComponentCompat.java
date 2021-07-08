package me.rayzr522.jsonmessage.compat;

import com.google.gson.JsonObject;

public interface ChatComponentCompat {
    /**
     * Creates a ChatComponentText from plain text
     *
     * @param message The text to convert to a chat component
     * @return The chat component
     */
    Object createComponent(String message);

    /**
     * Attempts to convert a JSON object into an NMS chat component
     *
     * @param json The JSON to create the chat component from
     * @return The chat component, or <code>null</code> if something went wrong
     */
    Object fromJson(JsonObject json);
}
