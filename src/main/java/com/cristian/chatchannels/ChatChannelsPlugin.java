package com.cristian.chatchannels;

import com.cristian.chatchannels.cfg.ConfigManager;
import com.cristian.chatchannels.cfg.MessageManager;
import com.cristian.chatchannels.channel.ChannelRegistry;
import com.cristian.chatchannels.command.FriendCommand;
import com.cristian.chatchannels.crossserver.CrossServerMessenger;
import com.cristian.chatchannels.crossserver.DatabaseManager;
import com.cristian.chatchannels.filter.SpamFilter;
import com.cristian.chatchannels.filter.WordFilter;
import com.cristian.chatchannels.friends.FriendManager;
import com.cristian.chatchannels.manager.HiddenChannelsManager;
import com.cristian.chatchannels.manager.MuteManager;
import com.cristian.chatchannels.manager.PlayerChannelManager;
import com.cristian.chatchannels.pm.ChatLogWriter;
import com.cristian.chatchannels.pm.IgnoreManager;
import com.cristian.chatchannels.pm.PrivateMessageManager;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.console.ConsoleBanner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public final class ChatChannelsPlugin extends JavaPlugin {

    private static ChatChannelsPlugin instance;

    // ── Existing managers ─────────────────────────────────────────────────────
    private ConfigManager configManager;
    private MessageManager messageManager;
    private ChannelRegistry channelRegistry;
    private PlayerChannelManager playerChannelManager;
    private MuteManager muteManager;
    private HiddenChannelsManager hiddenChannelsManager;
    private @Nullable SpamFilter spamFilter;
    private WordFilter wordFilter;

    // ── New managers ──────────────────────────────────────────────────────────
    private ChatLogWriter chatLogWriter;
    private IgnoreManager ignoreManager;
    private PrivateMessageManager privateMessageManager;
    private FriendManager friendManager;
    private @Nullable DatabaseManager databaseManager;
    private @Nullable CrossServerMessenger crossServerMessenger;
    private @Nullable FriendCommand friendCommand;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        instance = this;

        configManager = new ConfigManager(this);
        configManager.reload();
        messageManager = new MessageManager(this, configManager);
        messageManager.reload();

        channelRegistry = new ChannelRegistry(this);
        channelRegistry.load();

        playerChannelManager = new PlayerChannelManager(this);
        playerChannelManager.load();

        muteManager = new MuteManager(this);
        muteManager.load();

        hiddenChannelsManager = new HiddenChannelsManager(
            new File(getDataFolder(), "hidden_channels.yml"));
        hiddenChannelsManager.load();

        chatLogWriter = new ChatLogWriter(getDataFolder(), configManager.loggingEnabled());
        ignoreManager = new IgnoreManager(getDataFolder());
        ignoreManager.load();

        friendManager = new FriendManager(getDataFolder().toPath(),
            configManager.friendsMaxFriends(),
            configManager.friendsRequestTtlDays());
        friendManager.load();

        privateMessageManager = new PrivateMessageManager(this, ignoreManager, chatLogWriter);

        if (configManager.mysqlEnabled()) {
            try {
                databaseManager = new DatabaseManager(this,
                    configManager.mysqlHost(), configManager.mysqlPort(),
                    configManager.mysqlDatabase(), configManager.mysqlUsername(),
                    configManager.mysqlPassword(), configManager.mysqlPoolSize());
                crossServerMessenger = new CrossServerMessenger(this);
                crossServerMessenger.register();
                getSLF4JLogger().info("Cross-server mode enabled.");
            } catch (SQLException e) {
                getSLF4JLogger().error("MySQL connection failed — running in single-server mode.", e);
                databaseManager = null;
                crossServerMessenger = null;
            }
        } else {
            getSLF4JLogger().info("Cross-server disabled — configure MySQL in config.yml to enable.");
        }

        loadFilters();
        registerListeners();
        registerCommands();

        new org.bstats.bukkit.Metrics(this, 12345);

        ConsoleBanner.enable(this, PluginIdentity.of(this))
            .status(channelRegistry.getAll().size() + " channels")
            .hook(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") ? "PAPI" : "no PAPI")
            .hook(databaseManager != null ? "MySQL" : "single-server")
            .ready(java.time.Duration.ofMillis(System.currentTimeMillis() - startTime))
            .emit();
    }

    @Override
    public void onDisable() {
        if (playerChannelManager != null) playerChannelManager.save();
        if (muteManager != null) muteManager.save();
        if (hiddenChannelsManager != null) hiddenChannelsManager.save();
        if (ignoreManager != null) ignoreManager.save();
        if (friendManager != null) friendManager.save();
        if (chatLogWriter != null) chatLogWriter.close();
        if (crossServerMessenger != null) crossServerMessenger.unregister();
        if (databaseManager != null) databaseManager.close();
        ConsoleBanner.disable(this, PluginIdentity.of(this)).emit();
    }

    public void reload() {
        configManager.reload();
        messageManager.reload();
        channelRegistry.load();
        loadFilters();
        if (chatLogWriter != null) chatLogWriter.close();
        chatLogWriter = new ChatLogWriter(getDataFolder(), configManager.loggingEnabled());
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
        try { mode = WordFilter.Mode.valueOf(modeStr.toUpperCase()); }
        catch (IllegalArgumentException e) { mode = WordFilter.Mode.REPLACE; }
        wordFilter = new WordFilter(mode, wordsList, replacement);
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new com.cristian.chatchannels.listener.ChatListener(this), this);
        pm.registerEvents(new com.cristian.chatchannels.listener.PlayerListener(this), this);
        if (pm.isPluginEnabled("PlaceholderAPI"))
            new com.cristian.chatchannels.integration.PapiHook(this).register();
    }

    private void registerCommands() {
        var channelCmd = getCommand("channel");
        if (channelCmd != null) {
            var handler = new com.cristian.chatchannels.command.ChannelCommand(this);
            channelCmd.setExecutor(handler); channelCmd.setTabCompleter(handler);
        }
        setExecutor("channels", new com.cristian.chatchannels.command.ChannelsCommand(this));
        setExecutor("mute",     new com.cristian.chatchannels.command.MuteCommand(this));
        setExecutor("chatspy",  new com.cristian.chatchannels.command.ChatSpyCommand(this));
        setExecutor("cc",       new com.cristian.chatchannels.command.ChatChannelsRootCommand(this));

        setExecutor("msg",    new com.cristian.chatchannels.command.MsgCommand(this));
        setExecutor("reply",  new com.cristian.chatchannels.command.ReplyCommand(this));
        setExecutor("ignore", new com.cristian.chatchannels.command.IgnoreCommand(this));
        friendCommand = new FriendCommand(this);
        setExecutor("friend", friendCommand);
    }

    private void setExecutor(String name, org.bukkit.command.CommandExecutor exec) {
        var cmd = getCommand(name);
        if (cmd != null) cmd.setExecutor(exec);
    }

    public static ChatChannelsPlugin getInstance()                { return instance; }
    public ConfigManager getConfigManager()                       { return configManager; }
    public MessageManager getMessages()                           { return messageManager; }
    public MessageManager getMessagesConfig()                     { return messageManager; }
    public ChannelRegistry getChannelRegistry()                   { return channelRegistry; }
    public PlayerChannelManager getPlayerChannelManager()         { return playerChannelManager; }
    public MuteManager getMuteManager()                           { return muteManager; }
    public HiddenChannelsManager getHiddenChannelsManager()       { return hiddenChannelsManager; }
    public @Nullable SpamFilter getSpamFilter()                   { return spamFilter; }
    public WordFilter getWordFilter()                             { return wordFilter; }
    public ChatLogWriter getChatLogWriter()                        { return chatLogWriter; }
    public IgnoreManager getIgnoreManager()                       { return ignoreManager; }
    public PrivateMessageManager getPrivateMessageManager()       { return privateMessageManager; }
    public FriendManager getFriendManager()                       { return friendManager; }
    public @Nullable DatabaseManager getDatabaseManager()         { return databaseManager; }
    public @Nullable CrossServerMessenger getCrossServerMessenger() { return crossServerMessenger; }
    public @Nullable FriendCommand getFriendCommand()             { return friendCommand; }
}
