package com.cristian.chatchannels;

import org.bukkit.plugin.java.JavaPlugin;

public final class ChatChannelsPlugin extends JavaPlugin {

    private static ChatChannelsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getSLF4JLogger().info("ChatChannels enabled.");
    }

    @Override
    public void onDisable() {
        getSLF4JLogger().info("ChatChannels disabled.");
    }

    public static ChatChannelsPlugin getInstance() {
        return instance;
    }
}
