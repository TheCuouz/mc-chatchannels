package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChattyChannelsPlugin;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MsgCommand implements CommandExecutor {

    private final ChattyChannelsPlugin plugin;
    private final PluginIdentity identity;

    public MsgCommand(ChattyChannelsPlugin plugin) {
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
        if (args.length < 2) {
            ChatPrefix.send(sender, identity,
                plugin.getMessages().get("pm-usage"));
            return true;
        }
        String targetName = args[0];
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        plugin.getPrivateMessageManager().send(player, targetName, message);
        return true;
    }
}
