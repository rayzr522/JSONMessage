package me.rayzr522.jsonmessage;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class ReflectionHelper {

    private static final String version;
    private static final int MAJOR_VER;

    static {
        String[] split = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        version = split[split.length - 1];
        MAJOR_VER = Integer.parseInt(version.split("_")[1]);
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

    public static Optional<Method> getMethodWithParameters(Class<?> mainClass, Class<?>... params) {
        return Arrays.stream(mainClass.getMethods())
                .filter(method -> Arrays.equals(method.getParameterTypes(), params))
                .findFirst();
    }
}
