package me.rayzr522.jsonmessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

class ReflectionHelper {

    private static final String version;
    private static Constructor<?> chatComponentText;

    private static Class<?> packetPlayOutChat;
    private static Field packetPlayOutChatComponent;
    private static Field packetPlayOutChatMessageType;
    private static Field packetPlayOutChatUuid;
    private static Object enumChatMessageTypeMessage;
    private static Object enumChatMessageTypeActionbar;

    private static Constructor<?> titlePacketConstructor;
    private static Constructor<?> titleTimesPacketConstructor;
    private static Object enumActionTitle;
    private static Object enumActionSubtitle;

    private static Field connection;
    private static MethodHandle GET_HANDLE;
    private static MethodHandle SEND_PACKET;
    private static MethodHandle STRING_TO_CHAT;
    private static boolean SETUP;
    private static int MAJOR_VER = -1;

    static {
        String[] split = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        version = split[split.length - 1];

        try {
            MAJOR_VER = Integer.parseInt(version.split("_")[1]);

            final Class<?> craftPlayer = getClass("{obc}.entity.CraftPlayer");
            Method getHandle = craftPlayer.getMethod("getHandle");
            connection = getHandle.getReturnType().getField("playerConnection");
            Method sendPacket = connection.getType().getMethod("sendPacket", getClass("{nms}.Packet"));

            chatComponentText = getClass("{nms}.ChatComponentText").getConstructor(String.class);

            final Class<?> iChatBaseComponent = getClass("{nms}.IChatBaseComponent");

            Method stringToChat;

            if (MAJOR_VER < 8) {
                stringToChat = getClass("{nms}.ChatSerializer").getMethod("a", String.class);
            } else {
                stringToChat = getClass("{nms}.IChatBaseComponent$ChatSerializer").getMethod("a", String.class);
            }

            GET_HANDLE = MethodHandles.lookup().unreflect(getHandle);
            SEND_PACKET = MethodHandles.lookup().unreflect(sendPacket);
            STRING_TO_CHAT = MethodHandles.lookup().unreflect(stringToChat);

            packetPlayOutChat = getClass("{nms}.PacketPlayOutChat");
            packetPlayOutChatComponent = getField(packetPlayOutChat, "a");
            packetPlayOutChatMessageType = getField(packetPlayOutChat, "b");
            packetPlayOutChatUuid = MAJOR_VER >= 16 ? getField(packetPlayOutChat, "c") : null;

            Class<?> packetPlayOutTitle = getClass("{nms}.PacketPlayOutTitle");
            Class<?> titleAction = getClass("{nms}.PacketPlayOutTitle$EnumTitleAction");

            titlePacketConstructor = packetPlayOutTitle.getConstructor(titleAction, iChatBaseComponent);
            titleTimesPacketConstructor = packetPlayOutTitle.getConstructor(int.class, int.class, int.class);

            enumActionTitle = titleAction.getField("TITLE").get(null);
            enumActionSubtitle = titleAction.getField("SUBTITLE").get(null);

            if (MAJOR_VER >= 12) {
                Method getChatMessageType = getClass("{nms}.ChatMessageType").getMethod("a", byte.class);

                enumChatMessageTypeMessage = getChatMessageType.invoke(null, (byte) 1);
                enumChatMessageTypeActionbar = getChatMessageType.invoke(null, (byte) 2);
            }

            SETUP = true;
        } catch (Exception e) {
            e.printStackTrace();
            SETUP = false;
        }
    }

    static void sendPacket(Object packet, Player... players) {
        assertIsSetup();

        if (packet == null) {
            return;
        }

        for (Player player : players) {
            try {
                SEND_PACKET.bindTo(connection.get(GET_HANDLE.bindTo(player).invoke())).invoke(packet);
            } catch (Throwable e) {
                System.err.println("Failed to send packet");
                e.printStackTrace();
            }
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
            Object packet = packetPlayOutChat.newInstance();
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
