package com.cristian.chatchannels;

import com.cristian.chatchannels.channel.ChannelRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatChannelsPlugin extends JavaPlugin {

    private static ChatChannelsPlugin instance;
    private ChannelRegistry channelRegistry;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        channelRegistry = new ChannelRegistry(this);
        channelRegistry.load();

        getSLF4JLogger().info("ChatChannels enabled.");
    }

    @Override
    public void onDisable() {
        getSLF4JLogger().info("ChatChannels disabled.");
    }

    public static ChatChannelsPlugin getInstance() { return instance; }
    public ChannelRegistry getChannelRegistry() { return channelRegistry; }
}
