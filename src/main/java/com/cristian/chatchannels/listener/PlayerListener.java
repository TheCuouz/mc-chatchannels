package com.cristian.chatchannels.listener;

import com.cristian.chatchannels.ChatChannelsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final ChatChannelsPlugin plugin;

    public PlayerListener(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        var db = plugin.getDatabaseManager();
        if (db != null) {
            db.upsertSession(player.getUniqueId(), player.getName(),
                plugin.getConfigManager().serverName());
        }

        var friendCmd = plugin.getFriendCommand();
        if (friendCmd != null && plugin.getFriendManager() != null) {
            plugin.getFriendManager()
                .getPendingRequestsFor(player.getUniqueId())
                .forEach(req -> friendCmd.deliverRequest(player, req.senderName()));
        }

        notifyFriends(player, "JOIN");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerChannelManager().remove(player.getUniqueId());

        var db = plugin.getDatabaseManager();
        if (db != null) db.removeSession(player.getUniqueId());

        notifyFriends(player, "QUIT");
    }

    private void notifyFriends(Player player, String event) {
        var fm = plugin.getFriendManager();
        if (fm == null) return;

        String msgKey = "JOIN".equals(event) ? "friend-connected" : "friend-disconnected";
        var component = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
            .deserialize(plugin.getMessages().get(msgKey, "player", player.getName()));

        fm.getFriends(player.getUniqueId()).forEach(friendUuid -> {
            var friend = plugin.getServer().getPlayer(friendUuid);
            if (friend == null) return;
            if (fm.getNotify(friend.getUniqueId()))
                friend.sendMessage(component);
        });

        var messenger = plugin.getCrossServerMessenger();
        if (messenger != null) messenger.sendFriendNotify(player, event);
    }
}
