package com.cristian.chatchannels.chat;

import java.util.regex.Pattern;

/** Small helpers for channel format strings. Kept Bukkit-free so it can be unit-tested. */
public final class ChatFormat {

    /** PlaceholderAPI-style tokens: %luckperms_prefix%, %vault_rank%, … */
    private static final Pattern PLACEHOLDER = Pattern.compile("%[A-Za-z0-9_.\\-]+%");

    private ChatFormat() {}

    /**
     * Drops placeholders nobody resolved. Without this, a server that configures
     * {@code %luckperms_prefix%} but has no LuckPerms (or no PlaceholderAPI at all) prints the
     * raw token in every chat line — it looked broken in the very first screenshot we took.
     *
     * <p>Only meant for the channel FORMAT, never for player-typed text.
     */
    public static String stripUnresolved(String format) {
        if (format == null || format.indexOf('%') < 0) return format;
        String cleaned = PLACEHOLDER.matcher(format).replaceAll("");
        return cleaned.replaceAll("  +", " ");
    }
}
