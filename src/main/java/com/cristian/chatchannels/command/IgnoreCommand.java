package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class IgnoreCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final ChatChannelsPlugin plugin;
    private final PluginIdentity identity;

    public IgnoreCommand(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
        this.identity = PluginIdentity.of(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            ChatPrefix.error(sender, identity, "Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("chatchannels.ignore")) {
            ChatPrefix.error(player, identity, "No tienes permiso.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            Set<UUID> ignored = plugin.getIgnoreManager().getIgnored(player.getUniqueId());
            if (ignored.isEmpty()) {
                ChatPrefix.send(player, identity,
                    plugin.getMessages().get("ignore-list-empty"));
                return true;
            }
            player.sendMessage(MM.deserialize(
                plugin.getMessages().get("ignore-list-header", "count", String.valueOf(ignored.size()))));
            for (UUID uuid : ignored) {
                var offline = Bukkit.getOfflinePlayer(uuid);
                String name = offline.getName() != null ? offline.getName() : uuid.toString();
                player.sendMessage(MM.deserialize(
                    plugin.getMessages().get("ignore-list-entry", "player", name)));
            }
            return true;
        }

        if (args.length < 1) {
            ChatPrefix.send(player, identity, plugin.getMessages().get("ignore-usage"));
            return true;
        }

        String targetName = args[0];
        var offline = Bukkit.getOfflinePlayerIfCached(targetName);
        if (offline == null) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("pm-offline", "player", targetName));
            return true;
        }
        UUID targetUuid = offline.getUniqueId();

        var ignoreManager = plugin.getIgnoreManager();
        if (ignoreManager.isIgnoring(player.getUniqueId(), targetUuid)) {
            ignoreManager.removeIgnore(player.getUniqueId(), targetUuid);
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("ignore-removed", "player", targetName));
        } else {
            ignoreManager.addIgnore(player.getUniqueId(), targetUuid);
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("ignore-added", "player", targetName));
        }
        return true;
    }
}
