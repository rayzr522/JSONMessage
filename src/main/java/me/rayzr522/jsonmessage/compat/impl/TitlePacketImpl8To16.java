package me.rayzr522.jsonmessage.compat.impl;

import me.rayzr522.jsonmessage.ReflectionHelper;
import me.rayzr522.jsonmessage.compat.TitlePacketCompat;

import java.lang.reflect.Constructor;

public class TitlePacketImpl8To16 implements TitlePacketCompat {

    private final Constructor<?> titlePacketConstructor;
    private final Constructor<?> titleTimesPacketConstructor;
    private final Object enumActionTitle;
    private final Object enumActionSubtitle;

    public TitlePacketImpl8To16() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        Class<?> NMS_I_CHAT_BASE_COMPONENT = ReflectionHelper.getClass("{nms}.IChatBaseComponent");
        Class<?> NMS_PACKET_PLAY_OUT_TITLE = ReflectionHelper.getClass("{nms}.PacketPlayOutTitle");
        Class<?> NMS_TITLE_ACTION = ReflectionHelper.getClass("{nms}.PacketPlayOutTitle$EnumTitleAction");

        titlePacketConstructor = NMS_PACKET_PLAY_OUT_TITLE.getConstructor(NMS_TITLE_ACTION, NMS_I_CHAT_BASE_COMPONENT);
        titleTimesPacketConstructor = NMS_PACKET_PLAY_OUT_TITLE.getConstructor(int.class, int.class, int.class);

        enumActionTitle = NMS_TITLE_ACTION.getField("TITLE").get(null);
        enumActionSubtitle = NMS_TITLE_ACTION.getField("SUBTITLE").get(null);
    }

    @Override
    public Object createTitleTextPacket(Object chatComponent) {
        try {
            return titlePacketConstructor.newInstance(enumActionTitle, chatComponent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object createTitleTimesPacket(int fadeIn, int stay, int fadeOut) {
        try {
            return titleTimesPacketConstructor.newInstance(fadeIn, stay, fadeOut);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object createSubtitlePacket(Object chatComponent) {
        try {
            return titlePacketConstructor.newInstance(enumActionSubtitle, chatComponent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
