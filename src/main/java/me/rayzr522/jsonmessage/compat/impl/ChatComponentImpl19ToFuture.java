package me.rayzr522.jsonmessage.compat.impl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.rayzr522.jsonmessage.ReflectionHelper;
import me.rayzr522.jsonmessage.compat.ChatComponentCompat;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;

public class ChatComponentImpl19ToFuture implements ChatComponentCompat {
    private final MethodHandle fromText;
    private final MethodHandle fromJson;

    public ChatComponentImpl19ToFuture() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        Class<?> IChatBaseComponent = ReflectionHelper.getClass("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer");
        Class<?> IChatBaseComponent_ChatSerializer = ReflectionHelper.getClass("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer");

        fromText = MethodHandles.lookup().unreflect(IChatBaseComponent.getMethod("b", String.class));
        fromJson = MethodHandles.lookup().unreflect(IChatBaseComponent_ChatSerializer.getMethod("a", JsonElement.class, Type.class, JsonDeserializationContext.class));
    }

    @Override
    public Object createComponent(String message) {
        try {
            return fromText.invoke(message);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object fromJson(JsonObject json) {
        try {
            return fromJson.invoke(json, null, null);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
