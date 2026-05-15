package com.cristian.chatchannels.pm;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class IgnoreManager {

    private static final Logger LOG = Logger.getLogger(IgnoreManager.class.getName());

    private final Map<UUID, Set<UUID>> ignoreMap = new ConcurrentHashMap<>();
    private final File yamlFile;
    private final Plugin plugin;

    public IgnoreManager(File dataFolder, Plugin plugin) {
        this.yamlFile = new File(dataFolder, "ignores.yml");
        this.plugin = plugin;
    }

    public boolean isIgnoring(UUID ignorer, UUID ignored) {
        Set<UUID> set = ignoreMap.get(ignorer);
        return set != null && set.contains(ignored);
    }

    /** @return true if newly added, false if already ignored */
    public boolean addIgnore(UUID ignorer, UUID ignored) {
        Set<UUID> set = ignoreMap.computeIfAbsent(ignorer, k -> ConcurrentHashMap.newKeySet());
        boolean added = set.add(ignored);
        if (added) saveAsync();
        return added;
    }

    /** @return true if was ignored and now removed, false if wasn't ignored */
    public boolean removeIgnore(UUID ignorer, UUID ignored) {
        Set<UUID> set = ignoreMap.get(ignorer);
        if (set == null) return false;
        boolean removed = set.remove(ignored);
        if (removed) {
            if (set.isEmpty()) ignoreMap.remove(ignorer);
            saveAsync();
        }
        return removed;
    }

    public Set<UUID> getIgnored(UUID ignorer) {
        Set<UUID> set = ignoreMap.get(ignorer);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    public void load() {
        if (!yamlFile.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(yamlFile);
        ConfigurationSection sec = cfg.getConfigurationSection("ignores");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            try {
                UUID ignorer = UUID.fromString(key);
                List<String> list = sec.getStringList(key);
                Set<UUID> set = ConcurrentHashMap.newKeySet();
                for (String s : list) {
                    try { set.add(UUID.fromString(s)); } catch (IllegalArgumentException ignored) {}
                }
                if (!set.isEmpty()) ignoreMap.put(ignorer, set);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private void saveAsync() {
        if (plugin != null) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::save);
        } else {
            save();
        }
    }

    public void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Set<UUID>> entry : ignoreMap.entrySet()) {
            List<String> list = new ArrayList<>();
            for (UUID u : entry.getValue()) list.add(u.toString());
            Collections.sort(list);
            cfg.set("ignores." + entry.getKey(), list);
        }
        try {
            File parent = yamlFile.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            cfg.save(yamlFile);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to save ignores.yml", e);
        }
    }
}
