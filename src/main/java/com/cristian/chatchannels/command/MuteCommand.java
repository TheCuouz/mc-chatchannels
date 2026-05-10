package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.cristian.chatchannels.util.DurationParser;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MuteCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final ChatChannelsPlugin plugin;

    public MuteCommand(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("chatchannels.mute")) {
            sender.sendMessage(MM.deserialize("<red>No tienes permiso."));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(MM.deserialize(
                plugin.getMessagesConfig().getString("mute-usage",
                    "<red>Uso: /mute <jugador> [canal] [duración: 10m, 1h, 2d]")));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MM.deserialize(
                plugin.getMessagesConfig().getString("mute-not-found",
                    "<red>Jugador '<target>' no encontrado.")
                    .replace("<target>", args[0])));
            return true;
        }

        String channelId = args.length >= 2 ? args[1] : "*";
        String durationStr = args.length >= 3 ? args[2] : null;

        long expiresAt = -1L;
        if (durationStr != null) {
            long millis = DurationParser.parseMillis(durationStr);
            if (millis < 0) {
                sender.sendMessage(MM.deserialize("<red>Duración inválida. Usa: 10m, 1h, 2d"));
                return true;
            }
            expiresAt = System.currentTimeMillis() + millis;
        }

        plugin.getMuteManager().mute(target.getUniqueId(), channelId, expiresAt);

        String channelDisplay = channelId.equals("*") ? "todos los canales" : channelId;
        final long finalExpiresAt = expiresAt;
        String durationDisplay = finalExpiresAt < 0
            ? ""
            : " por " + DurationParser.format(finalExpiresAt - System.currentTimeMillis());

        String msgKey = finalExpiresAt < 0 ? "mute-permanent" : "mute-applied";
        String msg = plugin.getMessagesConfig().getString(msgKey,
            "<green><target> silenciado en <channel>.")
            .replace("<target>", target.getName())
            .replace("<channel>", channelDisplay)
            .replace("<duration>", durationDisplay);
        sender.sendMessage(MM.deserialize(msg));
        return true;
    }
}
