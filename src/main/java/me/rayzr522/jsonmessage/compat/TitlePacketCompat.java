package me.rayzr522.jsonmessage.compat;

public interface TitlePacketCompat {

    Object createTitleTextPacket(Object chatComponent);

    Object createTitleTimesPacket(int fadeIn, int stay, int fadeOut);

    Object createSubtitlePacket(Object chatComponent);
}
