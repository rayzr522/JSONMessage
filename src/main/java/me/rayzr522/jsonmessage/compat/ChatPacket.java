package me.rayzr522.jsonmessage.compat;

public interface ChatPacket {
    Object createActionbarPacket(Object chatComponent);
    Object createTextPacket(Object chatComponent);
}
