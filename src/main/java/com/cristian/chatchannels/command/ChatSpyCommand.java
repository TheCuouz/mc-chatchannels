package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatSpyCommand implements CommandExecutor {

    private final ChatChannelsPlugin plugin;
    private final PluginIdentity identity;

    public ChatSpyCommand(ChatChannelsPlugin plugin) {
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
        if (!player.hasPermission("chatchannels.spy")) {
            ChatPrefix.error(player, identity, "No tienes permiso.");
            return true;
        }
        plugin.getPlayerChannelManager().toggleSpy(player.getUniqueId());
        boolean nowSpy = plugin.getPlayerChannelManager().isSpy(player.getUniqueId());
        String msgKey = nowSpy ? "spy-enabled" : "spy-disabled";
        ChatPrefix.send(player, identity,
            plugin.getMessagesConfig().getString(msgKey, nowSpy ? "<gold>Spy ON" : "<gold>Spy OFF"));
        return true;
    }
}
