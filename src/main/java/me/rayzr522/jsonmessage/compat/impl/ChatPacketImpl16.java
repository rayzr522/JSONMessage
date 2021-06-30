package me.rayzr522.jsonmessage.compat.impl;

import me.rayzr522.jsonmessage.ReflectionHelper;
import me.rayzr522.jsonmessage.compat.ChatPacketCompat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ChatPacketImpl16 implements ChatPacketCompat {
    private final Constructor<?> chatPacketContructor;
    private final Field packetPlayOutChatComponent;
    private final Field packetPlayOutChatMessageType;
    private final Field packetPlayOutChatUuid;
    private static Object enumChatMessageTypeMessage;
    private static Object enumChatMessageTypeActionbar;

    public ChatPacketImpl16() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> NMS_PACKET_PLAY_OUT_CHAT = ReflectionHelper.getClass("{nms}.PacketPlayOutChat");

        chatPacketContructor = NMS_PACKET_PLAY_OUT_CHAT.getConstructor();
        packetPlayOutChatComponent = ReflectionHelper.getField(NMS_PACKET_PLAY_OUT_CHAT, "a");
        packetPlayOutChatMessageType = ReflectionHelper.getField(NMS_PACKET_PLAY_OUT_CHAT, "b");
        packetPlayOutChatUuid = ReflectionHelper.getField(NMS_PACKET_PLAY_OUT_CHAT, "c");

        Class<?> NMS_CHAT_MESSAGE_TYPE = ReflectionHelper.getClass("{nms}.ChatMessageType");

        Method getChatMessageType = NMS_CHAT_MESSAGE_TYPE.getMethod("a", byte.class);

        enumChatMessageTypeMessage = getChatMessageType.invoke(null, (byte) 1);
        enumChatMessageTypeActionbar = getChatMessageType.invoke(null, (byte) 2);

    }

    private Object createPacket(Object chatComponent, Object type) {
        try {
            Object packet = chatPacketContructor.newInstance();
            ReflectionHelper.setFieldValue(packetPlayOutChatComponent, packet, chatComponent);
            ReflectionHelper.setFieldValue(packetPlayOutChatMessageType, packet, type);
            ReflectionHelper.setFieldValue(packetPlayOutChatUuid, packet, UUID.randomUUID());
            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object createActionbarPacket(Object chatComponent) {
        return createPacket(chatComponent, enumChatMessageTypeActionbar);
    }

    @Override
    public Object createTextPacket(Object chatComponent) {
        return createPacket(chatComponent, enumChatMessageTypeMessage);
    }
}
