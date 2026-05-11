package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.cristian.chatchannels.channel.Channel;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChannelsCommand implements CommandExecutor {

    private final ChatChannelsPlugin plugin;
    private final PluginIdentity identity;

    public ChannelsCommand(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
        this.identity = PluginIdentity.of(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        ChatPrefix.send(sender, identity,
            plugin.getMessagesConfig().getString("channel-list-header",
                "<gold>Canales disponibles:"));

        String entryTemplate = plugin.getMessagesConfig()
            .getString("channel-list-entry",
                "  <gray>- <channel> <dark_gray>(<prefix>)</dark_gray>");

        for (Channel ch : plugin.getChannelRegistry().getAll()) {
            if (sender instanceof Player player && !player.hasPermission(ch.permission())) continue;
            String prefix = ch.quickPrefix().isEmpty() ? "sin prefijo" : ch.quickPrefix();
            ChatPrefix.send(sender, identity,
                entryTemplate
                    .replace("<channel>", ch.displayName())
                    .replace("<prefix>", prefix));
        }
        return true;
    }
}
