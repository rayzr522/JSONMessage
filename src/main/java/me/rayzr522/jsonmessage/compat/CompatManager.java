package me.rayzr522.jsonmessage.compat;

public class CompatManager {
    private final ChatComponent chatComponent;
    private final ChatPacket chatPacket;
    private final PlayerConnection playerConnection;
    private final TitlePacket titlePacket;

    public CompatManager() {
        chatComponent = null;
        chatPacket = null;
        playerConnection = null;
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
