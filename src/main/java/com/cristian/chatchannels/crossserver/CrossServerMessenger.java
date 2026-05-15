package com.cristian.chatchannels.crossserver;

import org.bukkit.entity.Player;

/**
 * Stub — replaced in Task 14 with the real cross-server messaging implementation.
 */
public interface CrossServerMessenger {

    /** Routes a private message to a player on a different server. */
    void sendPM(Player sender, String targetName, String message, String targetServer);
}
