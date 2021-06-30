package me.rayzr522.jsonmessage;

import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class ReflectionHelper {

    private static final String version;
    private static Constructor<?> chatComponentText;

    private static Constructor<?> chatPacketContructor;
    private static Field packetPlayOutChatComponent;
    private static Field packetPlayOutChatMessageType;
    private static Field packetPlayOutChatUuid;
    private static Object enumChatMessageTypeMessage;
    private static Object enumChatMessageTypeActionbar;

    private static Constructor<?> titlePacketConstructor;
    private static Constructor<?> titleTimesPacketConstructor;
    private static Object enumActionTitle;
    private static Object enumActionSubtitle;

    private static MethodHandle STRING_TO_CHAT;
    private static boolean SETUP;
    private static int MAJOR_VER = -1;

    static {
        String[] split = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        version = split[split.length - 1];

        try {
            MAJOR_VER = Integer.parseInt(version.split("_")[1]);

            Class<?> NMS_CHAT_COMPONENT_TEXT;
            Class<?> NMS_I_CHAT_BASE_COMPONENT;
            Class<?> NMS_CHAT_SERIALIZER;
            Class<?> NMS_PACKET_PLAY_OUT_CHAT;
            Class<?> NMS_PACKET_PLAY_OUT_TITLE;
            Class<?> NMS_ENUM_TITLE_ACTION;
            Class<?> NMS_CHAT_MESSAGE_TYPE;

            if (MAJOR_VER <= 16) {
                NMS_CHAT_COMPONENT_TEXT = getClass("{nms}.ChatComponentText");
                NMS_I_CHAT_BASE_COMPONENT = getClass("{nms}.IChatBaseComponent");
                NMS_PACKET_PLAY_OUT_CHAT = getClass("{nms}.PacketPlayOutChat");
                NMS_PACKET_PLAY_OUT_TITLE = getClass("{nms}.PacketPlayOutTitle");

                if (MAJOR_VER < 8) {
                    NMS_CHAT_SERIALIZER = getClass("{nms}.ChatSerializer");
                } else {
                    NMS_CHAT_SERIALIZER = getClass("{nms}.IChatBaseComponent$ChatSerializer");
                }

                if (MAJOR_VER >= 12) {
                    NMS_CHAT_MESSAGE_TYPE = getClass("{nms}.ChatMessageType");
                } else {
                    NMS_CHAT_MESSAGE_TYPE = null;
                }
            } else {
                NMS_CHAT_COMPONENT_TEXT = getClass("net.minecraft.network.chat.ChatComponentText");
                NMS_I_CHAT_BASE_COMPONENT = getClass("net.minecraft.network.chat.IChatBaseComponent");
                NMS_CHAT_SERIALIZER = getClass("net.minecraft.network.chat.ChatSerializer.IChatBaseComponent$ChatSerializer");
                NMS_PACKET_PLAY_OUT_CHAT = getClass("net.minecraft.network.protocol.game.PacketPlayOutChat");
                NMS_PACKET_PLAY_OUT_TITLE = getClass("{nms}.PacketPlayOutTitle");
                NMS_CHAT_MESSAGE_TYPE = getClass("net.minecraft.network.chat.ChatMessageType");
            }

            chatComponentText = NMS_CHAT_COMPONENT_TEXT.getConstructor(String.class);

            final Class<?> iChatBaseComponent = NMS_I_CHAT_BASE_COMPONENT;

            Method stringToChat = NMS_CHAT_SERIALIZER.getMethod("a", String.class);

            STRING_TO_CHAT = MethodHandles.lookup().unreflect(stringToChat);

            chatPacketContructor = NMS_PACKET_PLAY_OUT_CHAT.getConstructor();
            packetPlayOutChatComponent = getField(NMS_PACKET_PLAY_OUT_CHAT, "a");
            packetPlayOutChatMessageType = getField(NMS_PACKET_PLAY_OUT_CHAT, "b");
            packetPlayOutChatUuid = MAJOR_VER >= 16 ? getField(NMS_PACKET_PLAY_OUT_CHAT, "c") : null;

            Class<?> titleAction = getClass("{nms}.PacketPlayOutTitle$EnumTitleAction");

            titlePacketConstructor = NMS_PACKET_PLAY_OUT_TITLE.getConstructor(titleAction, iChatBaseComponent);
            titleTimesPacketConstructor = NMS_PACKET_PLAY_OUT_TITLE.getConstructor(int.class, int.class, int.class);

            enumActionTitle = titleAction.getField("TITLE").get(null);
            enumActionSubtitle = titleAction.getField("SUBTITLE").get(null);

            if (NMS_CHAT_MESSAGE_TYPE != null) {
                Method getChatMessageType = NMS_CHAT_MESSAGE_TYPE.getMethod("a", byte.class);

                enumChatMessageTypeMessage = getChatMessageType.invoke(null, (byte) 1);
                enumChatMessageTypeActionbar = getChatMessageType.invoke(null, (byte) 2);
            }

            SETUP = true;
        } catch (Exception e) {
            e.printStackTrace();
            SETUP = false;
        }
    }

    static Object createActionbarPacket(String message) {
        assertIsSetup();

        Object packet = createTextPacket(message);
        setType(packet, (byte) 2);
        return packet;
    }

    static Object createTextPacket(String message) {
        assertIsSetup();

        try {
            Object packet = chatPacketContructor.newInstance();
            setFieldValue(packetPlayOutChatComponent, packet, fromJson(message));
            setFieldValue(packetPlayOutChatUuid, packet, UUID.randomUUID());
            setType(packet, (byte) 1);
            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static Object createTitlePacket(String message) {
        assertIsSetup();

        try {
            return titlePacketConstructor.newInstance(enumActionTitle, fromJson(message));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static Object createTitleTimesPacket(int fadeIn, int stay, int fadeOut) {
        assertIsSetup();

        try {
            return titleTimesPacketConstructor.newInstance(fadeIn, stay, fadeOut);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static Object createSubtitlePacket(String message) {
        assertIsSetup();

        try {
            return titlePacketConstructor.newInstance(enumActionSubtitle, fromJson(message));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void setType(Object chatPacket, byte type) {
        assertIsSetup();

        if (MAJOR_VER < 12) {
            setFieldValue(packetPlayOutChatMessageType, chatPacket, type);
            return;
        }

        switch (type) {
            case 1:
                setFieldValue(packetPlayOutChatMessageType, chatPacket, enumChatMessageTypeMessage);
                break;
            case 2:
                setFieldValue(packetPlayOutChatMessageType, chatPacket, enumChatMessageTypeActionbar);
                break;
            default:
                throw new IllegalArgumentException("type must be 1 or 2");
        }
    }

    /**
     * Creates a ChatComponentText from plain text
     *
     * @param message The text to convert to a chat component
     * @return The chat component
     */
    static Object componentText(String message) {
        assertIsSetup();

        try {
            return chatComponentText.newInstance(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Attempts to convert a String representing a JSON message into a usable object
     *
     * @param json The JSON to attempt to parse
     * @return The object representing the text in JSON form, or <code>null</code> if something went wrong converting the String to JSON data
     */
    public static Object fromJson(String json) {
        assertIsSetup();

        if (!json.trim().startsWith("{")) {
            return componentText(json);
        }

        try {
            return STRING_TO_CHAT.invoke(json);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void assertIsSetup() {
        if (!SETUP) {
            throw new IllegalStateException("JSONMessage.ReflectionHelper is not set up yet!");
        }
    }

    public static Class<?> getClass(String path) throws ClassNotFoundException {
        return Class.forName(path.replace("{nms}", "net.minecraft.server." + version).replace("{obc}", "org.bukkit.craftbukkit." + version));
    }

    public static Optional<Field> findFieldByTypeName(Class<?> targetClass, String fieldTypeName) {
        return Arrays.stream(targetClass.getDeclaredFields())
                .filter(field -> field.getType().getName().equals(fieldTypeName))
                .findFirst();
    }

    public static Field getFieldByTypeName(Class<?> targetClass, String fieldTypeName) {
        return findFieldByTypeName(targetClass, fieldTypeName)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Could not find field with type '%s' in class %s",
                        fieldTypeName, targetClass.getCanonicalName()
                )));
    }

    public static void setFieldValue(Field field, Object instance, Object value) {
        if (field == null) {
            // useful for fields that might not exist
            return;
        }

        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Field getField(Class<?> classObject, String fieldName) {
        try {
            Field field = classObject.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getVersion() {
        return MAJOR_VER;
    }
}
