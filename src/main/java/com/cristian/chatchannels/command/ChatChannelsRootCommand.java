package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChattyChannelsPlugin;
import com.cristian.chatchannels.channel.Channel;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ChatChannelsRootCommand implements CommandExecutor {

    private final ChattyChannelsPlugin plugin;
    private final PluginIdentity identity;

    public ChatChannelsRootCommand(ChattyChannelsPlugin plugin) {
        this.plugin = plugin;
        this.identity = PluginIdentity.of(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        return switch (sub) {
            case "reload" -> handleReload(sender);
            case "hide"   -> handleHide(sender, args);
            case "show"   -> handleShow(sender, args);
            case "hidden" -> handleHidden(sender);
            default -> {
                sendUsage(sender);
                yield true;
            }
        };
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("chatchannels.admin")) {
            ChatPrefix.error(sender, identity, "No tienes permiso.");
            return true;
        }
        plugin.reload();
        ChatPrefix.send(sender, identity,
            plugin.getMessagesConfig().getString("reload-success",
                "<green>ChattyChannels recargado correctamente."));
        return true;
    }

    private boolean handleHide(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            ChatPrefix.error(sender, identity, "Solo jugadores pueden ocultar canales.");
            return true;
        }
        if (!player.hasPermission("chatchannels.hide")) {
            ChatPrefix.error(sender, identity, "No tienes permiso.");
            return true;
        }
        if (args.length < 2) {
            ChatPrefix.warn(sender, identity, "Uso: /cc hide <#canal>");
            return true;
        }

        Optional<Channel> opt = resolveChannel(args[1]);
        if (opt.isEmpty()) {
            sendFormatted(sender, "channel-not-found",
                "<red>Canal desconocido: {channel}</red>",
                "{channel}", args[1]);
            return true;
        }
        Channel channel = opt.get();
        UUID uuid = player.getUniqueId();

        boolean added = plugin.getHiddenChannelsManager().hide(uuid, channel.id());
        String key = added ? "channel-hidden" : "channel-already-hidden";
        String fallback = added
            ? "<green>Canal {channel} oculto. No recibirás sus mensajes.</green>"
            : "<yellow>Ese canal ya estaba oculto.</yellow>";
        sendFormatted(sender, key, fallback, "{channel}", channel.displayName());
        return true;
    }

    private boolean handleShow(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            ChatPrefix.error(sender, identity, "Solo jugadores pueden mostrar canales.");
            return true;
        }
        if (!player.hasPermission("chatchannels.hide")) {
            ChatPrefix.error(sender, identity, "No tienes permiso.");
            return true;
        }
        if (args.length < 2) {
            ChatPrefix.warn(sender, identity, "Uso: /cc show <#canal>");
            return true;
        }

        Optional<Channel> opt = resolveChannel(args[1]);
        if (opt.isEmpty()) {
            sendFormatted(sender, "channel-not-found",
                "<red>Canal desconocido: {channel}</red>",
                "{channel}", args[1]);
            return true;
        }
        Channel channel = opt.get();
        UUID uuid = player.getUniqueId();

        boolean removed = plugin.getHiddenChannelsManager().show(uuid, channel.id());
        String key = removed ? "channel-shown" : "channel-not-hidden";
        String fallback = removed
            ? "<green>Canal {channel} visible de nuevo.</green>"
            : "<yellow>Ese canal no estaba oculto.</yellow>";
        sendFormatted(sender, key, fallback, "{channel}", channel.displayName());
        return true;
    }

    private boolean handleHidden(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            ChatPrefix.error(sender, identity, "Solo jugadores tienen lista de canales ocultos.");
            return true;
        }
        if (!player.hasPermission("chatchannels.hide")) {
            ChatPrefix.error(sender, identity, "No tienes permiso.");
            return true;
        }

        Set<String> hidden = plugin.getHiddenChannelsManager().getHidden(player.getUniqueId());
        if (hidden.isEmpty()) {
            ChatPrefix.send(sender, identity,
                plugin.getMessagesConfig().getString("hidden-list-empty",
                    "<gray>No tienes canales ocultos.</gray>"));
            return true;
        }

        StringBuilder joined = new StringBuilder();
        boolean first = true;
        for (String id : hidden) {
            if (!first) joined.append(", ");
            String displayName = plugin.getChannelRegistry().getById(id)
                .map(Channel::displayName)
                .orElse(id);
            joined.append(displayName);
            first = false;
        }
        sendFormatted(sender, "hidden-list",
            "<gray>Canales ocultos: {channels}</gray>",
            "{channels}", joined.toString());
        return true;
    }

    /**
     * Resolves a channel reference like {@code #trade} or {@code trade}.
     */
    private Optional<Channel> resolveChannel(String raw) {
        String id = raw.startsWith("#") ? raw.substring(1) : raw;
        return plugin.getChannelRegistry().getById(id);
    }

    private void sendUsage(CommandSender sender) {
        ChatPrefix.warn(sender, identity, "Uso: /cc <reload|hide|show|hidden>");
    }

    private void sendFormatted(CommandSender sender, String key, String fallback,
                               String placeholder, String value) {
        String raw = plugin.getMessagesConfig().getString(key, fallback);
        ChatPrefix.send(sender, identity, raw.replace(placeholder, value));
    }
}
