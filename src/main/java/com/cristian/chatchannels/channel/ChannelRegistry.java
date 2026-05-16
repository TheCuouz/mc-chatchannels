package com.cristian.chatchannels.channel;

import com.cristian.chatchannels.ChattyChannelsPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ChannelRegistry {

    private final ChattyChannelsPlugin plugin;
    private final Map<String, Channel> byId = new LinkedHashMap<>();
    private final Map<String, Channel> byPrefix = new HashMap<>();
    private String defaultChannelId;

    public ChannelRegistry(ChattyChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        byId.clear();
        byPrefix.clear();

        File file = new File(plugin.getDataFolder(), "channels.yml");
        if (!file.exists()) {
            plugin.saveResource("channels.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        defaultChannelId = config.getString("default-channel", "local");

        ConfigurationSection channels = config.getConfigurationSection("channels");
        if (channels == null) {
            plugin.getSLF4JLogger().warn("No channels defined in channels.yml!");
            return;
        }

        for (String id : channels.getKeys(false)) {
            ConfigurationSection sec = channels.getConfigurationSection(id);
            if (sec == null) continue;

            Channel channel = new Channel(
                id,
                sec.getString("display-name", id),
                sec.getString("quick-prefix", ""),
                sec.getInt("range", -1),
                sec.getString("permission", "chatchannels.use." + id),
                sec.getString("format", "<player>: <message>"),
                sec.getInt("cooldown-seconds", 0)
            );
            byId.put(id, channel);
            if (!channel.quickPrefix().isEmpty()) {
                byPrefix.put(channel.quickPrefix(), channel);
            }
        }

        plugin.getSLF4JLogger().info("Loaded {} channels.", byId.size());
    }

    public Optional<Channel> getById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Optional<Channel> getByPrefix(String prefix) {
        return Optional.ofNullable(byPrefix.get(prefix));
    }

    public Channel getDefault() {
        return byId.getOrDefault(defaultChannelId,
            byId.isEmpty() ? null : byId.values().iterator().next());
    }

    public String getDefaultChannelId() {
        return defaultChannelId;
    }

    public Collection<Channel> getAll() {
        return Collections.unmodifiableCollection(byId.values());
    }

    public Map<String, Channel> getPrefixMap() {
        return Collections.unmodifiableMap(byPrefix);
    }
}
