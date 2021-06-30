package me.rayzr522.jsonmessage.compat;

public interface ChatPacketCompat {
    Object createActionbarPacket(Object chatComponent);
    Object createTextPacket(Object chatComponent);
}
