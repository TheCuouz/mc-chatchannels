package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ChatChannelsRootCommand implements CommandExecutor {

    private final ChatChannelsPlugin plugin;
    private final PluginIdentity identity;

    public ChatChannelsRootCommand(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
        this.identity = PluginIdentity.of(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("chatchannels.admin")) {
            ChatPrefix.error(sender, identity, "No tienes permiso.");
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reload();
            ChatPrefix.send(sender, identity,
                plugin.getMessagesConfig().getString("reload-success",
                    "<green>ChatChannels recargado correctamente."));
        } else {
            ChatPrefix.warn(sender, identity, "Uso: /cc reload");
        }
        return true;
    }
}
