package com.cristian.chatchannels.listener;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.cristian.chatchannels.channel.Channel;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;

public class ChatListener implements Listener {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private final ChatChannelsPlugin plugin;
    private final PluginIdentity identity;

    public ChatListener(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
        this.identity = PluginIdentity.of(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        String rawMessage = PLAIN.serialize(event.originalMessage());

        // Detect quick prefix — check prefixes in defined order
        Channel targetChannel = null;
        String messageContent = rawMessage;

        for (Map.Entry<String, Channel> entry : plugin.getChannelRegistry().getPrefixMap().entrySet()) {
            String prefix = entry.getKey();
            if (rawMessage.startsWith(prefix)) {
                targetChannel = entry.getValue();
                messageContent = rawMessage.substring(prefix.length()).stripLeading();
                break;
            }
        }

        if (targetChannel == null) {
            String activeId = plugin.getPlayerChannelManager().getActiveChannel(uuid);
            targetChannel = plugin.getChannelRegistry().getById(activeId)
                .orElseGet(() -> plugin.getChannelRegistry().getDefault());
        }

        // Permission check
        if (!player.hasPermission(targetChannel.permission())) {
            ChatPrefix.send(player, identity,
                plugin.getMessagesConfig().getString("channel-no-permission",
                    "<red>No tienes permiso para usar ese canal."));
            event.setCancelled(true);
            return;
        }

        // Mute check
        if (plugin.getMuteManager().isMuted(uuid, targetChannel.id())) {
            ChatPrefix.send(player, identity,
                plugin.getMessagesConfig().getString("channel-muted",
                    "<red>Estás silenciado en ese canal."));
            event.setCancelled(true);
            return;
        }

        // Cooldown check
        if (targetChannel.cooldownSeconds() > 0
                && !player.hasPermission("chatchannels.bypass.cooldown")) {
            long now = System.currentTimeMillis();
            long lastTime = plugin.getPlayerChannelManager().getLastMessageTime(uuid);
            long cooldownMs = targetChannel.cooldownSeconds() * 1000L;
            long elapsed = now - lastTime;
            if (elapsed < cooldownMs) {
                long remaining = (cooldownMs - elapsed + 999) / 1000;
                ChatPrefix.send(player, identity,
                    plugin.getMessagesConfig()
                        .getString("channel-cooldown", "<red>Espera <seconds>s.")
                        .replace("<seconds>", String.valueOf(remaining)));
                event.setCancelled(true);
                return;
            }
            plugin.getPlayerChannelManager().setLastMessageTime(uuid, now);
        }

        // Spam + word filters
        if (!player.hasPermission("chatchannels.bypass.filter")) {
            var spamFilter = plugin.getSpamFilter();
            if (spamFilter != null) {
                var reason = spamFilter.check(uuid, messageContent);
                if (reason != null) {
                    String msgKey = switch (reason) {
                        case DUPLICATE -> "filter-spam-duplicate";
                        case CAPS      -> "filter-spam-caps";
                        case FLOOD     -> "filter-spam-flood";
                    };
                    ChatPrefix.send(player, identity,
                        plugin.getMessagesConfig().getString(msgKey, "<red>Mensaje bloqueado."));
                    event.setCancelled(true);
                    return;
                }
            }

            var wordResult = plugin.getWordFilter().apply(messageContent);
            if (wordResult.blocked()) {
                ChatPrefix.send(player, identity,
                    plugin.getMessagesConfig().getString("filter-word-block",
                        "<red>Tu mensaje contiene palabras no permitidas."));
                event.setCancelled(true);
                return;
            }
            messageContent = wordResult.filtered();
        }

        // Build audience
        final Channel finalChannel = targetChannel;
        final String finalMessage = messageContent;
        Collection<? extends Player> online = Bukkit.getOnlinePlayers();

        Set<Audience> viewers = new HashSet<>();
        for (Player p : online) {
            if (!p.hasPermission(finalChannel.permission())) continue;
            if (plugin.getMuteManager().isMuted(p.getUniqueId(), finalChannel.id())) continue;
            // Self-hide (receive-side): skip recipients who hid this channel.
            // Sender always sees their own message, so they're added unconditionally below.
            if (!p.getUniqueId().equals(player.getUniqueId())
                    && plugin.getHiddenChannelsManager().isHidden(p.getUniqueId(), finalChannel.id())) continue;
            if (!finalChannel.isGlobal()) {
                if (!p.getWorld().equals(player.getWorld())) continue;
                double distSq = p.getLocation().distanceSquared(player.getLocation());
                if (distSq > (double) finalChannel.range() * finalChannel.range()) continue;
            }
            viewers.add(p);
        }

        // Deliver spy-prefixed copy to spies not already in audience
        String spyPrefixRaw = plugin.getMessagesConfig().getString("spy-prefix", "<dark_gray>[SPY] ");
        for (Player p : online) {
            if (!viewers.contains(p) && plugin.getPlayerChannelManager().isSpy(p.getUniqueId())) {
                p.sendMessage(buildMessage(finalChannel, player, finalMessage, spyPrefixRaw));
            }
        }

        // Override default viewers and renderer
        Component formatted = buildMessage(finalChannel, player, finalMessage, null);
        event.viewers().clear();
        event.viewers().addAll(viewers);
        event.renderer((source, sourceDisplayName, message, audience) -> formatted);
    }

    private Component buildMessage(Channel channel, Player player, String message, String spyPrefix) {
        String format = (spyPrefix != null ? spyPrefix : "") + channel.format();

        // PAPI placeholders
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            format = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, format);
        }

        format = format
            .replace("<player>", player.getName())
            .replace("<message>", escapeForMiniMessage(message));

        return MM.deserialize(format);
    }

    private String escapeForMiniMessage(String message) {
        return message.replace("<", "\\<");
    }
}
