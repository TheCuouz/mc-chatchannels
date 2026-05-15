package com.cristian.chatchannels.pm;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PrivateMessageManager {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final ChatChannelsPlugin plugin;
    private final PluginIdentity identity;
    private final IgnoreManager ignoreManager;

    /** uuid → uuid of the last player who messaged them (for /reply) */
    private final Map<UUID, UUID> lastMessaged = new ConcurrentHashMap<>();

    public PrivateMessageManager(ChatChannelsPlugin plugin,
                                  IgnoreManager ignoreManager,
                                  ChatLogWriter logWriter) {
        this.plugin = plugin;
        this.identity = PluginIdentity.of(plugin);
        this.ignoreManager = ignoreManager;
        // logWriter is resolved lazily via plugin.getChatLogWriter() so reload() stays in sync
    }

    /**
     * Sends a PM from sender to the named target.
     * Returns true if the message was delivered or routed cross-server.
     */
    public boolean send(Player sender, String targetName, String message) {
        if (!sender.hasPermission("chatchannels.pm.send")) {
            ChatPrefix.error(sender, identity,
                plugin.getMessages().get("pm-no-permission"));
            return false;
        }

        // Resolve local player first
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            // Try cross-server via DatabaseManager
            var db = plugin.getDatabaseManager();
            if (db != null) {
                db.getPlayerSession(getOfflineUuid(targetName), optSession -> {
                    if (optSession.isPresent()) {
                        var messenger = plugin.getCrossServerMessenger();
                        if (messenger != null) {
                            messenger.sendPM(sender, targetName, message, optSession.get());
                            deliverSenderCopy(sender, targetName, message);
                            plugin.getChatLogWriter().logPm(sender.getName(), targetName,
                                optSession.get(), message);
                            broadcastSpy(sender.getUniqueId(), null, targetName, message);
                        }
                    } else {
                        Bukkit.getScheduler().runTask(plugin, () ->
                            ChatPrefix.send(sender, identity,
                                plugin.getMessages().get("pm-offline", "player", targetName)));
                    }
                });
                return true;
            }
            ChatPrefix.send(sender, identity,
                plugin.getMessages().get("pm-offline", "player", targetName));
            return false;
        }

        // Check ignore
        if (!sender.hasPermission("chatchannels.pm.bypass-ignore")) {
            if (ignoreManager.isIgnoring(target.getUniqueId(), sender.getUniqueId())) {
                ChatPrefix.send(sender, identity,
                    plugin.getMessages().get("pm-ignored-by-them", "player", targetName));
                return false;
            }
        }
        if (ignoreManager.isIgnoring(sender.getUniqueId(), target.getUniqueId())) {
            ChatPrefix.send(sender, identity,
                plugin.getMessages().get("pm-ignored-by-you", "player", targetName));
            return false;
        }

        // Deliver local
        String serverName = plugin.getConfigManager().serverName();
        deliverToTarget(sender, target, message);
        deliverSenderCopy(sender, target.getName(), message);
        lastMessaged.put(target.getUniqueId(), sender.getUniqueId());
        lastMessaged.put(sender.getUniqueId(), target.getUniqueId());
        plugin.getChatLogWriter().logPm(sender.getName(), target.getName(), serverName, message);
        broadcastSpy(sender.getUniqueId(), target.getUniqueId(), target.getName(), message);
        return true;
    }

    /** Delivers an incoming cross-server PM to a local player. */
    public void deliverIncoming(String fromName, UUID fromUuid, Player target, String message) {
        String raw = plugin.getMessages().get("pm-received", "from", fromName, "message", message);
        target.sendMessage(MM.deserialize(raw));
        lastMessaged.put(target.getUniqueId(), fromUuid);
    }

    public UUID getLastMessaged(UUID uuid) {
        return lastMessaged.get(uuid);
    }

    /** Resolves the UUID of a possibly-offline player via Bukkit cache. */
    private UUID getOfflineUuid(String name) {
        var offline = Bukkit.getOfflinePlayerIfCached(name);
        return offline != null ? offline.getUniqueId() : UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
    }

    private void deliverToTarget(Player sender, Player target, String message) {
        String raw = plugin.getMessages().get("pm-received", "from", sender.getName(), "message", message);
        target.sendMessage(MM.deserialize(raw));
    }

    private void deliverSenderCopy(Player sender, String targetName, String message) {
        String raw = plugin.getMessages().get("pm-sent", "to", targetName, "message", message);
        sender.sendMessage(MM.deserialize(raw));
    }

    private void broadcastSpy(UUID senderUuid, UUID targetUuid, String targetName, String message) {
        Player senderPlayer = Bukkit.getPlayer(senderUuid);
        String senderName = senderPlayer != null ? senderPlayer.getName() : "?";
        String raw = plugin.getMessages().get("pm-spy", java.util.Map.of(
            "from", senderName, "to", targetName, "message", message));
        var spyComponent = MM.deserialize(raw);
        for (UUID spyUuid : plugin.getPlayerChannelManager().getSpyPlayers()) {
            if (spyUuid.equals(senderUuid) || spyUuid.equals(targetUuid)) continue;
            Player spy = Bukkit.getPlayer(spyUuid);
            if (spy != null) spy.sendMessage(spyComponent);
        }
    }
}
