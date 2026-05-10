package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatSpyCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final ChatChannelsPlugin plugin;

    public ChatSpyCommand(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("chatchannels.spy")) {
            player.sendMessage(MM.deserialize("<red>No tienes permiso."));
            return true;
        }
        plugin.getPlayerChannelManager().toggleSpy(player.getUniqueId());
        boolean nowSpy = plugin.getPlayerChannelManager().isSpy(player.getUniqueId());
        String msgKey = nowSpy ? "spy-enabled" : "spy-disabled";
        player.sendMessage(MM.deserialize(
            plugin.getMessagesConfig().getString(msgKey, nowSpy ? "<gold>Spy ON" : "<gold>Spy OFF")));
        return true;
    }
}
