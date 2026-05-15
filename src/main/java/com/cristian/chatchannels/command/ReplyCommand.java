package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReplyCommand implements CommandExecutor {

    private final ChatChannelsPlugin plugin;
    private final PluginIdentity identity;

    public ReplyCommand(ChatChannelsPlugin plugin) {
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
        if (args.length < 1) {
            ChatPrefix.send(sender, identity, plugin.getMessages().get("reply-usage"));
            return true;
        }
        var lastUuid = plugin.getPrivateMessageManager().getLastMessaged(player.getUniqueId());
        if (lastUuid == null) {
            ChatPrefix.send(sender, identity, plugin.getMessages().get("reply-no-target"));
            return true;
        }
        Player target = Bukkit.getPlayer(lastUuid);
        if (target == null) {
            ChatPrefix.send(sender, identity, plugin.getMessages().get("reply-offline"));
            return true;
        }
        String message = String.join(" ", args);
        plugin.getPrivateMessageManager().send(player, target.getName(), message);
        return true;
    }
}
