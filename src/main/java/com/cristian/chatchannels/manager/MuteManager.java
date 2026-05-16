package com.cristian.chatchannels.manager;

import com.cristian.chatchannels.ChattyChannelsPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MuteManager {

    // key: "uuid:channelId"  or  "uuid:*" for all channels
    private final ConcurrentHashMap<String, Long> mutes = new ConcurrentHashMap<>();
    private final ChattyChannelsPlugin plugin;

    public MuteManager(ChattyChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "mutes.yml");
        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sec = config.getConfigurationSection("mutes");
        if (sec == null) return;

        long now = System.currentTimeMillis();
        for (String key : sec.getKeys(false)) {
            long expiresAt = sec.getLong(key);
            if (expiresAt == -1 || expiresAt > now) {
                mutes.put(key, expiresAt);
            }
        }
    }

    public void save() {
        File file = new File(plugin.getDataFolder(), "mutes.yml");
        YamlConfiguration config = new YamlConfiguration();
        mutes.forEach((key, val) -> config.set("mutes." + key, val));
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getSLF4JLogger().error("Failed to save mutes.yml", e);
        }
    }

    public void mute(UUID target, String channelId, long expiresAt) {
        mutes.put(target + ":" + channelId, expiresAt);
        save();
    }

    public boolean isMuted(UUID uuid, String channelId) {
        return isMutedByKey(uuid + ":" + channelId) || isMutedByKey(uuid + ":*");
    }

    private boolean isMutedByKey(String key) {
        Long expiresAt = mutes.get(key);
        if (expiresAt == null) return false;
        if (expiresAt == -1) return true;
        if (System.currentTimeMillis() < expiresAt) return true;
        mutes.remove(key);
        return false;
    }
}
