package me.rayzr522.jsonmessage;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class ReflectionHelper {

    private static final String version;

    private static Constructor<?> titlePacketConstructor;
    private static Constructor<?> titleTimesPacketConstructor;
    private static Object enumActionTitle;
    private static Object enumActionSubtitle;

    private static boolean SETUP;
    private static int MAJOR_VER = -1;

    static {
        String[] split = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        version = split[split.length - 1];

        try {
            MAJOR_VER = Integer.parseInt(version.split("_")[1]);

//            Class<?> NMS_PACKET_PLAY_OUT_TITLE = getClass("{nms}.PacketPlayOutTitle");
//            Class<?> NMS_ENUM_TITLE_ACTION;
//            Class<?> NMS_TITLE_ACTION = getClass("{nms}.PacketPlayOutTitle$EnumTitleAction");
//
//            titlePacketConstructor = NMS_PACKET_PLAY_OUT_TITLE.getConstructor(NMS_TITLE_ACTION, NMS_I_CHAT_BASE_COMPONENT);
//            titleTimesPacketConstructor = NMS_PACKET_PLAY_OUT_TITLE.getConstructor(int.class, int.class, int.class);
//
//            enumActionTitle = NMS_TITLE_ACTION.getField("TITLE").get(null);
//            enumActionSubtitle = NMS_TITLE_ACTION.getField("SUBTITLE").get(null);

            SETUP = true;
        } catch (Exception e) {
            e.printStackTrace();
            SETUP = false;
        }
    }

    static Object createTitlePacket(String message) {
        assertIsSetup();

        try {
//            return titlePacketConstructor.newInstance(enumActionTitle, fromJson(message));
            return null;
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
//            return titlePacketConstructor.newInstance(enumActionSubtitle, fromJson(message));
            return null;
        } catch (Exception e) {
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
                .filter(field -> field.getType().getSimpleName().equals(fieldTypeName))
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
