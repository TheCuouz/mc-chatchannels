package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ChatChannelsRootCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final ChatChannelsPlugin plugin;

    public ChatChannelsRootCommand(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("chatchannels.admin")) {
            sender.sendMessage(MM.deserialize("<red>No tienes permiso."));
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reload();
            sender.sendMessage(MM.deserialize(
                plugin.getMessagesConfig().getString("reload-success",
                    "<green>ChatChannels recargado correctamente.")));
        } else {
            sender.sendMessage(MM.deserialize("<yellow>Uso: /cc reload"));
        }
        return true;
    }
}
