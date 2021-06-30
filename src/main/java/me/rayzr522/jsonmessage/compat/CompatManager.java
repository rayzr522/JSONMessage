package me.rayzr522.jsonmessage.compat;

import me.rayzr522.jsonmessage.ReflectionHelper;
import me.rayzr522.jsonmessage.compat.impl.*;

public class CompatManager {
    private final ChatComponent chatComponent;
    private final ChatPacket chatPacket;
    private final PlayerConnection playerConnection;
    private final TitlePacket titlePacket;

    public CompatManager() {
        int version = ReflectionHelper.getVersion();

        chatComponent = null;
        chatPacket = new ImplementationPicker<ChatPacket>()
                .addImplementation(8, 11, ChatPacketImpl8To11::new)
                .addImplementation(12, 15, ChatPacketImpl12To15::new)
                .addImplementation(16, 16, ChatPacketImpl16::new)
                .addImplementation(17, Integer.MAX_VALUE, ChatPacketImpl17ToFuture::new)
                .getImplementation(version)
                .orElseThrow(() -> new IllegalStateException(
                        "Missing ChatPacket implementation for major version: " + version
                ));
        playerConnection = new ImplementationPicker<PlayerConnection>()
                .addImplementation(8, 16, PlayerConnectionImpl8To16::new)
                .addImplementation(17, Integer.MAX_VALUE, PlayerConnectImpl17ToFuture::new)
                .getImplementation(version)
                .orElseThrow(() -> new IllegalStateException(
                        "Missing PlayerConnection implementation for major version: " + version
                ));
        titlePacket = null;
    }

    public ChatComponent getChatComponent() {
        return chatComponent;
    }

    public ChatPacket getChatPacket() {
        return chatPacket;
    }

    public PlayerConnection getPlayerConnection() {
        return playerConnection;
    }

    public TitlePacket getTitlePacket() {
        return titlePacket;
    }
}
