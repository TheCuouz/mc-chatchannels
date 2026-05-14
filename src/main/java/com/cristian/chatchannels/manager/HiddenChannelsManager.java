package com.cristian.chatchannels.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores per-player receive-side hidden channels. A player who hides a channel
 * stops receiving messages from it, but the channel itself remains live for
 * everyone else (compare to {@link MuteManager}, which silences a sender).
 *
 * Persisted as YAML:
 * <pre>
 * hidden:
 *   "uuid": ["channelA", "channelB"]
 * </pre>
 */
public class HiddenChannelsManager {

    private static final Logger LOG = Logger.getLogger(HiddenChannelsManager.class.getName());

    private final Map<UUID, Set<String>> hidden = new ConcurrentHashMap<>();
    private final File yamlFile;

    public HiddenChannelsManager(File yamlFile) {
        this.yamlFile = yamlFile;
    }

    public boolean isHidden(UUID uuid, String channelId) {
        Set<String> set = hidden.get(uuid);
        return set != null && set.contains(channelId);
    }

    /**
     * @return true if the channel was newly hidden, false if it was already hidden.
     */
    public boolean hide(UUID uuid, String channelId) {
        Set<String> set = hidden.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet());
        boolean added = set.add(channelId);
        if (added) save();
        return added;
    }

    /**
     * @return true if the channel was hidden and is now shown, false if it wasn't hidden.
     */
    public boolean show(UUID uuid, String channelId) {
        Set<String> set = hidden.get(uuid);
        if (set == null) return false;
        boolean removed = set.remove(channelId);
        if (removed) {
            if (set.isEmpty()) hidden.remove(uuid);
            save();
        }
        return removed;
    }

    public Set<String> getHidden(UUID uuid) {
        Set<String> set = hidden.get(uuid);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    public void load() {
        if (!yamlFile.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(yamlFile);
        ConfigurationSection sec = config.getConfigurationSection("hidden");
        if (sec == null) return;

        for (String key : sec.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException ex) {
                LOG.warning("Skipping invalid UUID in hidden_channels.yml: " + key);
                continue;
            }
            List<String> channels = sec.getStringList(key);
            if (channels.isEmpty()) continue;
            Set<String> set = ConcurrentHashMap.newKeySet();
            set.addAll(channels);
            hidden.put(uuid, set);
        }
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Set<String>> entry : hidden.entrySet()) {
            // Sorted list for stable on-disk ordering — easier to diff between sessions.
            List<String> sorted = new ArrayList<>(entry.getValue());
            Collections.sort(sorted);
            config.set("hidden." + entry.getKey().toString(), sorted);
        }
        try {
            File parent = yamlFile.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            config.save(yamlFile);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to save hidden_channels.yml", e);
        }
    }
}
