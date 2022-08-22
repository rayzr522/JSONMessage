package me.rayzr522.jsonmessage.compat;

import me.rayzr522.jsonmessage.ReflectionHelper;
import me.rayzr522.jsonmessage.compat.impl.*;

public class CompatManager {
    private static CompatManager INSTANCE;

    private final ChatComponentCompat chatComponentCompat;
    private final ChatPacketCompat chatPacketCompat;
    private final PlayerConnectionCompat playerConnectionCompat;
    private final TitlePacketCompat titlePacketCompat;

    public CompatManager() {
        int version = ReflectionHelper.getVersion();

        chatComponentCompat = new ImplementationPicker<ChatComponentCompat>()
                .addImplementation(8, 16, ChatComponentImpl8To16::new)
                .addImplementation(17, 18, ChatComponentImpl17To18::new)
                .addImplementation(19, Integer.MAX_VALUE, ChatComponentImpl19ToFuture::new)
                .getImplementation(version)
                .orElseThrow(() -> new IllegalStateException(
                        "Missing ChatComponent implementation for major version: " + version
                ));

        chatPacketCompat = new ImplementationPicker<ChatPacketCompat>()
                .addImplementation(8, 11, ChatPacketImpl8To11::new)
                .addImplementation(12, 15, ChatPacketImpl12To15::new)
                .addImplementation(16, 16, ChatPacketImpl16::new)
                .addImplementation(17, 18, ChatPacketImpl17To18::new)
                .addImplementation(17, Integer.MAX_VALUE, ChatPacketImpl19ToFuture::new)
                .getImplementation(version)
                .orElseThrow(() -> new IllegalStateException(
                        "Missing ChatPacket implementation for major version: " + version
                ));

        playerConnectionCompat = new ImplementationPicker<PlayerConnectionCompat>()
                .addImplementation(8, 16, PlayerConnectionImpl8To16::new)
                .addImplementation(17, Integer.MAX_VALUE, PlayerConnectImpl17ToFuture::new)
                .getImplementation(version)
                .orElseThrow(() -> new IllegalStateException(
                        "Missing PlayerConnection implementation for major version: " + version
                ));

        titlePacketCompat = new ImplementationPicker<TitlePacketCompat>()
                .addImplementation(8, 16, TitlePacketImpl8To16::new)
                .addImplementation(17, Integer.MAX_VALUE, TitlePacketImpl17ToFuture::new)
                .getImplementation(version)
                .orElseThrow(() -> new IllegalStateException(
                        "Missing TitlePacket implementation for major version: " + version
                ));
    }

    private static CompatManager get() {
        if (INSTANCE == null) {
            INSTANCE = new CompatManager();
        }
        return INSTANCE;
    }

    public static ChatComponentCompat chatComponent() {
        return get().chatComponentCompat;
    }

    public static ChatPacketCompat chatPacket() {
        return get().chatPacketCompat;
    }

    public static PlayerConnectionCompat playerConnection() {
        return get().playerConnectionCompat;
    }

    public static TitlePacketCompat titlePacket() {
        return get().titlePacketCompat;
    }
}
