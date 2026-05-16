package com.cristian.chatchannels.integration;

import com.cristian.chatchannels.ChattyChannelsPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PapiHook extends PlaceholderExpansion {

    private final ChattyChannelsPlugin plugin;

    public PapiHook(ChattyChannelsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() { return "chatchannels"; }

    @Override
    public @NotNull String getAuthor() { return "Cristian"; }

    @Override
    public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }

    @Override
    public boolean persist() { return true; }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        return switch (params) {
            case "active" -> plugin.getPlayerChannelManager().getActiveChannel(player.getUniqueId());
            case "muted" -> {
                String active = plugin.getPlayerChannelManager().getActiveChannel(player.getUniqueId());
                yield String.valueOf(plugin.getMuteManager().isMuted(player.getUniqueId(), active));
            }
            default -> null;
        };
    }
}
