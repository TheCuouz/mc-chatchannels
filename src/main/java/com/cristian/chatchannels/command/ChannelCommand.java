package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.cristian.chatchannels.channel.Channel;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final ChatChannelsPlugin plugin;

    public ChannelCommand(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length == 0) {
            String current = plugin.getPlayerChannelManager().getActiveChannel(player.getUniqueId());
            plugin.getChannelRegistry().getById(current).ifPresent(ch ->
                player.sendMessage(MM.deserialize("<gold>Canal activo: " + ch.displayName())));
            return true;
        }
        String id = args[0].toLowerCase();
        var optChannel = plugin.getChannelRegistry().getById(id);
        if (optChannel.isEmpty()) {
            player.sendMessage(MM.deserialize(
                plugin.getMessagesConfig().getString("channel-not-found",
                    "<red>Canal '<id>' no encontrado.")
                    .replace("<id>", id)));
            return true;
        }
        Channel channel = optChannel.get();
        if (!player.hasPermission(channel.permission())) {
            player.sendMessage(MM.deserialize(
                plugin.getMessagesConfig().getString("channel-no-permission",
                    "<red>No tienes permiso para usar ese canal.")));
            return true;
        }
        plugin.getPlayerChannelManager().setActiveChannel(player.getUniqueId(), id);
        player.sendMessage(MM.deserialize(
            plugin.getMessagesConfig().getString("channel-switched",
                "<green>Cambiaste al canal <channel>.")
                .replace("<channel>", channel.displayName())));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            return plugin.getChannelRegistry().getAll().stream()
                .filter(ch -> player.hasPermission(ch.permission()))
                .map(Channel::id)
                .filter(id -> id.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        return List.of();
    }
}
