package me.rayzr522.jsonmessage.compat;

import me.rayzr522.jsonmessage.ReflectionHelper;
import me.rayzr522.jsonmessage.compat.impl.PlayerConnectImpl17ToFuture;
import me.rayzr522.jsonmessage.compat.impl.PlayerConnectionImpl8To16;

public class CompatManager {
    private final ChatComponent chatComponent;
    private final ChatPacket chatPacket;
    private final PlayerConnection playerConnection;
    private final TitlePacket titlePacket;

    public CompatManager() {
        chatComponent = null;
        chatPacket = null;
        playerConnection = new ImplementationPicker<PlayerConnection>()
                .addImplementation(8, 16, PlayerConnectionImpl8To16::new)
                .addImplementation(17, Integer.MAX_VALUE, PlayerConnectImpl17ToFuture::new)
                .getImplementation(ReflectionHelper.getVersion())
                .orElse(null);
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
