package com.cristian.chatchannels.manager;

import com.cristian.chatchannels.ChattyChannelsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerChannelManager {

    private final ChattyChannelsPlugin plugin;
    private final ConcurrentHashMap<UUID, String> activeChannel = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> lastMessageTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Boolean> spying = new ConcurrentHashMap<>();

    public PlayerChannelManager(ChattyChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "data.yml");
        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        var section = config.getConfigurationSection("active-channels");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                activeChannel.put(uuid, config.getString("active-channels." + key));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        File file = new File(plugin.getDataFolder(), "data.yml");
        YamlConfiguration config = new YamlConfiguration();
        activeChannel.forEach((uuid, channel) ->
            config.set("active-channels." + uuid, channel));
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getSLF4JLogger().error("Failed to save data.yml", e);
        }
    }

    public String getActiveChannel(UUID uuid) {
        return activeChannel.getOrDefault(uuid,
            plugin.getChannelRegistry().getDefaultChannelId());
    }

    public void setActiveChannel(UUID uuid, String channelId) {
        activeChannel.put(uuid, channelId);
    }

    public long getLastMessageTime(UUID uuid) {
        return lastMessageTime.getOrDefault(uuid, 0L);
    }

    public void setLastMessageTime(UUID uuid, long time) {
        lastMessageTime.put(uuid, time);
    }

    public boolean isSpy(UUID uuid) {
        return spying.getOrDefault(uuid, false);
    }

    public void toggleSpy(UUID uuid) {
        spying.compute(uuid, (k, v) -> v == null ? true : !v);
    }

    public java.util.Set<UUID> getSpyPlayers() {
        java.util.Set<UUID> result = new java.util.HashSet<>();
        spying.forEach((uuid, isSpy) -> { if (isSpy) result.add(uuid); });
        return result;
    }

    public void remove(UUID uuid) {
        lastMessageTime.remove(uuid);
        spying.remove(uuid);
    }
}
