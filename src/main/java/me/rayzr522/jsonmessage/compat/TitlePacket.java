package me.rayzr522.jsonmessage.compat;

public interface TitlePacket {

    Object createTitlePacket(String message);

    Object createTitleTimesPacket(int fadeIn, int stay, int fadeOut);

    Object createSubtitlePacket(Object chatComponent);

    Object createSubtitleTimesPacket(int fadeIn, int stay, int fadeOut);
}
