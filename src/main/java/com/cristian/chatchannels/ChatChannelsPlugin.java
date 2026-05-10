package com.cristian.chatchannels;

import com.cristian.chatchannels.channel.ChannelRegistry;
import com.cristian.chatchannels.filter.SpamFilter;
import com.cristian.chatchannels.filter.WordFilter;
import com.cristian.chatchannels.manager.MuteManager;
import com.cristian.chatchannels.manager.PlayerChannelManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class ChatChannelsPlugin extends JavaPlugin {

    private static ChatChannelsPlugin instance;
    private ChannelRegistry channelRegistry;
    private PlayerChannelManager playerChannelManager;
    private MuteManager muteManager;
    private FileConfiguration messagesConfig;
    private @Nullable SpamFilter spamFilter;
    private WordFilter wordFilter;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        loadMessagesConfig();

        channelRegistry = new ChannelRegistry(this);
        channelRegistry.load();

        playerChannelManager = new PlayerChannelManager(this);
        playerChannelManager.load();

        muteManager = new MuteManager(this);
        muteManager.load();

        loadFilters();

        registerListeners();
        registerCommands();

        // bStats — placeholder ID, replace with real one before publishing
        new org.bstats.bukkit.Metrics(this, 12345);

        getSLF4JLogger().info("ChatChannels enabled.");
    }

    @Override
    public void onDisable() {
        if (playerChannelManager != null) playerChannelManager.save();
        if (muteManager != null) muteManager.save();
        getSLF4JLogger().info("ChatChannels disabled.");
    }

    public void reload() {
        reloadConfig();
        loadMessagesConfig();
        channelRegistry.load();
        loadFilters();
    }

    private void loadMessagesConfig() {
        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) saveResource("messages.yml", false);
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    private void loadFilters() {
        File channelsFile = new File(getDataFolder(), "channels.yml");
        if (!channelsFile.exists()) saveResource("channels.yml", false);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(channelsFile);

        boolean spamEnabled = cfg.getBoolean("filters.spam.enabled", true);
        if (spamEnabled) {
            long dupWindow = cfg.getLong("filters.spam.duplicate-window-seconds", 5) * 1000;
            int capsPct = cfg.getInt("filters.spam.caps-threshold-pct", 70);
            int capsMin = cfg.getInt("filters.spam.caps-min-length", 10);
            long floodWindow = cfg.getLong("filters.spam.flood-window-seconds", 5) * 1000;
            int floodMax = cfg.getInt("filters.spam.flood-max-messages", 4);
            spamFilter = new SpamFilter(dupWindow, capsPct, capsMin, floodWindow, floodMax);
        } else {
            spamFilter = null;
        }

        List<String> wordsList = cfg.getStringList("filters.words.list");
        String modeStr = cfg.getString("filters.words.mode", "REPLACE");
        String replacement = cfg.getString("filters.words.replacement", "****");
        WordFilter.Mode mode;
        try {
            mode = WordFilter.Mode.valueOf(modeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            mode = WordFilter.Mode.REPLACE;
        }
        wordFilter = new WordFilter(mode, wordsList, replacement);
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new com.cristian.chatchannels.listener.ChatListener(this), this);
        pm.registerEvents(new com.cristian.chatchannels.listener.PlayerListener(this), this);

        if (pm.isPluginEnabled("PlaceholderAPI")) {
            new com.cristian.chatchannels.integration.PapiHook(this).register();
            getSLF4JLogger().info("PlaceholderAPI hook registered.");
        }
    }

    private void registerCommands() {
        var channelCmd = getCommand("channel");
        if (channelCmd != null) {
            var handler = new com.cristian.chatchannels.command.ChannelCommand(this);
            channelCmd.setExecutor(handler);
            channelCmd.setTabCompleter(handler);
        }
        var channelsCmd = getCommand("channels");
        if (channelsCmd != null)
            channelsCmd.setExecutor(new com.cristian.chatchannels.command.ChannelsCommand(this));

        var muteCmd = getCommand("mute");
        if (muteCmd != null)
            muteCmd.setExecutor(new com.cristian.chatchannels.command.MuteCommand(this));

        var chatspyCmd = getCommand("chatspy");
        if (chatspyCmd != null)
            chatspyCmd.setExecutor(new com.cristian.chatchannels.command.ChatSpyCommand(this));

        var ccCmd = getCommand("cc");
        if (ccCmd != null)
            ccCmd.setExecutor(new com.cristian.chatchannels.command.ChatChannelsRootCommand(this));
    }

    public static ChatChannelsPlugin getInstance() { return instance; }
    public ChannelRegistry getChannelRegistry() { return channelRegistry; }
    public PlayerChannelManager getPlayerChannelManager() { return playerChannelManager; }
    public MuteManager getMuteManager() { return muteManager; }
    public FileConfiguration getMessagesConfig() { return messagesConfig; }
    public @Nullable SpamFilter getSpamFilter() { return spamFilter; }
    public WordFilter getWordFilter() { return wordFilter; }
}
