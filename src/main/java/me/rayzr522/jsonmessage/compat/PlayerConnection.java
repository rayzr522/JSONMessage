package me.rayzr522.jsonmessage.compat;

import org.bukkit.entity.Player;

public interface PlayerConnection {
    void sendPacket(Object packet, Player... players);
}
