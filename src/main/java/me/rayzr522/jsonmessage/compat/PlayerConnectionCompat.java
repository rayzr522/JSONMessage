package me.rayzr522.jsonmessage.compat;

import org.bukkit.entity.Player;

public interface PlayerConnectionCompat {
    void sendPacket(Object packet, Player... players);
}
