package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChattyChannelsPlugin;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearChatCommand implements CommandExecutor {

    private static final int BLANK_LINES = 100;
    private static final String BYPASS_PERMISSION = "chatchannels.clearchat.bypass";

    private final ChattyChannelsPlugin plugin;
    private final PluginIdentity identity;

    public ClearChatCommand(ChattyChannelsPlugin plugin) {
        this.plugin = plugin;
        this.identity = PluginIdentity.of(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("chatchannels.clearchat")) {
            ChatPrefix.error(sender, identity, plugin.getMessages().get("no-permission"));
            return true;
        }

        String senderName = (sender instanceof Player p) ? p.getName() : "Consola";
        Component blank = Component.empty();
        String announceTpl = plugin.getMessagesConfig().getString("clearchat-broadcast",
            "<gold>Chat limpiado por <yellow><sender></yellow>.")
            .replace("<sender>", senderName);
        Component announce = MiniMessage.miniMessage().deserialize(announceTpl);

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.hasPermission(BYPASS_PERMISSION)) continue;
            for (int i = 0; i < BLANK_LINES; i++) {
                target.sendMessage(blank);
            }
        }

        Bukkit.broadcast(announce);

        ChatPrefix.send(sender, identity,
            plugin.getMessagesConfig().getString("clearchat-confirm",
                "<green>Chat limpiado."));
        return true;
    }
}
