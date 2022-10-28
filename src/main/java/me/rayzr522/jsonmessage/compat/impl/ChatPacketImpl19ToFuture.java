package me.rayzr522.jsonmessage.compat.impl;

import me.rayzr522.jsonmessage.ReflectionHelper;
import me.rayzr522.jsonmessage.compat.ChatPacketCompat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ChatPacketImpl19ToFuture implements ChatPacketCompat {
    private final Constructor<?> chatPacketContructor;
    private static int enumChatMessageTypeMessage;
    private static int enumChatMessageTypeActionbar;

    public ChatPacketImpl19ToFuture() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> NMS_I_CHAT_BASE_COMPONENT = ReflectionHelper.getClass("net.minecraft.network.chat.IChatBaseComponent");
        // Class<?> NMS_CHAT_MESSAGE_TYPE = ReflectionHelper.getClass("net.minecraft.network.chat.ChatMessageType");

        // TODO: looks like it might need to use  ClientboundPlayerChatPacket instead?
        //   here's the signature, unsure how to use it as of yet tho:
        //   public ClientboundPlayerChatPacket(PlayerChatMessage playerChatMessage, ChatMessageType.b boundNetwork) {
        //    this.a = playerChatMessage;
        //    this.b = boundNetwork;
        //  }
        Class<?> NMS_PACKET_PLAY_OUT_CHAT = ReflectionHelper.getClass("net.minecraft.network.protocol.game.ClientboundSystemChatPacket");

        chatPacketContructor = NMS_PACKET_PLAY_OUT_CHAT.getConstructor(
                NMS_I_CHAT_BASE_COMPONENT,
                int.class
        );

        // Method getChatMessageType = NMS_CHAT_MESSAGE_TYPE.getMethod("a", byte.class);

        enumChatMessageTypeMessage = 0;  // getChatMessageType.invoke(null, (byte) 1);
        enumChatMessageTypeActionbar = 0;  // getChatMessageType.invoke(null, (byte) 2);

    }

    private Object createPacket(Object chatComponent, int type) {
        try {
            return chatPacketContructor.newInstance(
                    chatComponent,
                    type
            );
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
