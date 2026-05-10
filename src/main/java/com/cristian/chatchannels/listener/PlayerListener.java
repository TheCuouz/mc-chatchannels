package com.cristian.chatchannels.listener;

import com.cristian.chatchannels.ChatChannelsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final ChatChannelsPlugin plugin;

    public PlayerListener(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerChannelManager().remove(event.getPlayer().getUniqueId());
    }
}
