package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.cristian.chatchannels.channel.Channel;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChannelsCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final ChatChannelsPlugin plugin;

    public ChannelsCommand(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(MM.deserialize(
            plugin.getMessagesConfig().getString("channel-list-header",
                "<gold>Canales disponibles:")));

        String entryTemplate = plugin.getMessagesConfig()
            .getString("channel-list-entry",
                "  <gray>- <channel> <dark_gray>(<prefix>)</dark_gray>");

        for (Channel ch : plugin.getChannelRegistry().getAll()) {
            if (sender instanceof Player player && !player.hasPermission(ch.permission())) continue;
            String prefix = ch.quickPrefix().isEmpty() ? "sin prefijo" : ch.quickPrefix();
            sender.sendMessage(MM.deserialize(
                entryTemplate
                    .replace("<channel>", ch.displayName())
                    .replace("<prefix>", prefix)));
        }
        return true;
    }
}
