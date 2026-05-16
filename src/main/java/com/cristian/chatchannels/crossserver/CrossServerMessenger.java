package com.cristian.chatchannels.crossserver;

import com.cristian.chatchannels.ChattyChannelsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class CrossServerMessenger implements PluginMessageListener {

    public static final String CHANNEL = "chatchannels:pm";

    private final ChattyChannelsPlugin plugin;

    public CrossServerMessenger(ChattyChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, CHANNEL, this);
    }

    public void unregister() {
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, CHANNEL);
        plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, CHANNEL);
    }

    public void sendPM(Player sender, String toName, String message, String targetServer) {
        String json = buildPmPayload(
            sender.getUniqueId().toString(), sender.getName(),
            "", toName, message, plugin.getConfigManager().serverName());
        sendRaw(sender, json);
    }

    public void sendFriendNotify(Player player, String event) {
        String json = buildFriendNotifyPayload(
            player.getUniqueId().toString(), player.getName(),
            event, plugin.getConfigManager().serverName());
        Player carrier = plugin.getServer().getOnlinePlayers().stream().findFirst().orElse(null);
        if (carrier != null) sendRaw(carrier, json);
    }

    private void sendRaw(Player carrier, String json) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeUTF(json);
            carrier.sendPluginMessage(plugin, CHANNEL, baos.toByteArray());
        } catch (IOException e) {
            plugin.getSLF4JLogger().error("CrossServerMessenger send failed", e);
        }
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (!CHANNEL.equals(channel)) return;
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String json = in.readUTF();
            Payload p = parsePayload(json);
            if (p == null) return;

            switch (p.type()) {
                case "PM" -> {
                    Player target = plugin.getServer().getPlayerExact(p.toName());
                    if (target != null) {
                        plugin.getPrivateMessageManager().deliverIncoming(
                            p.fromName(),
                            java.util.UUID.fromString(p.fromUuid()),
                            target, p.message());
                    }
                }
                case "FRIEND_NOTIFY" -> {
                    java.util.UUID playerUuid = java.util.UUID.fromString(p.playerUuid());
                    plugin.getFriendManager().getFriends(playerUuid).forEach(friendUuid -> {
                        Player friend = plugin.getServer().getPlayer(friendUuid);
                        if (friend == null) return;
                        if (!plugin.getFriendManager().getNotify(friend.getUniqueId())) return;
                        String msgKey = "JOIN".equals(p.event())
                            ? "friend-connected" : "friend-disconnected";
                        friend.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage
                            .miniMessage().deserialize(
                                plugin.getMessages().get(msgKey, "player", p.playerName())));
                    });
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            plugin.getSLF4JLogger().warn("Malformed plugin message on " + CHANNEL, e);
        }
    }

    // ── Static helpers (testable without Bukkit) ─────────────────────────────

    public static String buildPmPayload(String fromUuid, String fromName,
                                        String toUuid, String toName,
                                        String message, String server) {
        return "{\"type\":\"PM\","
            + "\"from_uuid\":\"" + fromUuid + "\","
            + "\"from_name\":\"" + esc(fromName) + "\","
            + "\"to_uuid\":\"" + toUuid + "\","
            + "\"to_name\":\"" + esc(toName) + "\","
            + "\"message\":\"" + esc(message) + "\","
            + "\"origin_server\":\"" + esc(server) + "\"}";
    }

    public static String buildFriendNotifyPayload(String playerUuid, String playerName,
                                                  String event, String server) {
        return "{\"type\":\"FRIEND_NOTIFY\","
            + "\"player_uuid\":\"" + playerUuid + "\","
            + "\"player_name\":\"" + esc(playerName) + "\","
            + "\"event\":\"" + event + "\","
            + "\"server\":\"" + esc(server) + "\"}";
    }

    @Nullable
    public static Payload parsePayload(String json) {
        try {
            String type = extract(json, "type");
            if (type == null) return null;
            return switch (type) {
                case "PM" -> new Payload(type,
                    extract(json, "from_uuid"), extract(json, "from_name"),
                    null, extract(json, "to_name"),
                    extract(json, "message"), extract(json, "origin_server"),
                    null, null);
                case "FRIEND_NOTIFY" -> new Payload(type,
                    null, null,
                    extract(json, "player_uuid"), null, null,
                    extract(json, "server"),
                    extract(json, "player_name"), extract(json, "event"));
                default -> null;
            };
        } catch (Exception e) { return null; }
    }

    @Nullable
    private static String extract(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start < 0) return null;
        start += search.length();
        int end = json.indexOf('"', start);
        if (end < 0) return null;
        return json.substring(start, end).replace("\\\\", "\\").replace("\\\"", "\"");
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public record Payload(
        String type,
        String fromUuid, String fromName,
        String playerUuid, String toName,
        String message, String server,
        String playerName, String event) {}
}
