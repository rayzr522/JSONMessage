package me.rayzr522.jsonmessage.compat.impl;

import me.rayzr522.jsonmessage.ReflectionHelper;
import me.rayzr522.jsonmessage.compat.PlayerConnectionCompat;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerConnectImpl17ToFuture implements PlayerConnectionCompat {
    private static final Logger LOGGER = Logger.getLogger("PlayerConnectImpl17ToFuture");

    private final Field playerConnectionField;
    private final MethodHandle GET_HANDLE;
    private final MethodHandle SEND_PACKET;

    public PlayerConnectImpl17ToFuture() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        Class<?> CRAFT_PLAYER = ReflectionHelper.getClass("{obc}.entity.CraftPlayer");
        Class<?> NMS_PACKET = ReflectionHelper.getClass("net.minecraft.network.protocol.Packet");
        Class<?> NMS_ENTITY_PLAYER = ReflectionHelper.getClass("net.minecraft.server.level.EntityPlayer");

        playerConnectionField = ReflectionHelper.getFieldByTypeName(NMS_ENTITY_PLAYER, "PlayerConnection");

        Method getHandle = CRAFT_PLAYER.getMethod("getHandle");
        Method sendPacket = playerConnectionField.getType().getMethod("sendPacket", NMS_PACKET);

        GET_HANDLE = MethodHandles.lookup().unreflect(getHandle);
        SEND_PACKET = MethodHandles.lookup().unreflect(sendPacket);
    }

    @Override
    public void sendPacket(Object packet, Player... players) {
        for (Player player : players) {
            try {
                SEND_PACKET.bindTo(playerConnectionField.get(GET_HANDLE.bindTo(player).invoke())).invoke(packet);
            } catch (Throwable e) {
                LOGGER.log(Level.SEVERE, "Failed to send packet:", e);
            }
        }
    }
}
