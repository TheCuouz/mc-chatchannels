package com.cristian.chatchannels.cfg;

import com.ttsstudio.sdk.i18n.LocaleManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * Thin facade around the SDK's {@link LocaleManager}. Existing call sites
 * continue to use {@code plugin.getMessagesConfig().getString(...)} without change.
 */
public final class MessageManager {

    private static final String FALLBACK_LOCALE = "en";

    private final JavaPlugin plugin;
    private final ConfigManager config;
    private LocaleManager locales;

    public MessageManager(JavaPlugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void reload() {
        this.locales = new LocaleManager(plugin, config.language(), FALLBACK_LOCALE);
        locales.reload();
    }

    public String raw(String key)                                   { return locales.raw(key); }
    public String get(String key)                                   { return locales.get(key); }
    public String get(String key, Map<String, String> placeholders) { return locales.get(key, placeholders); }
    public String get(String key, String p1, String v1)             { return locales.get(key, p1, v1); }
    public String get(String key, String p1, String v1, String p2, String v2) {
        return locales.get(key, p1, v1, p2, v2);
    }

    /**
     * Back-compat bridge for legacy call sites that used
     * {@code plugin.getMessagesConfig().getString("key", "")}.
     */
    public String getString(String key, @SuppressWarnings("unused") String def) {
        return locales.get(key);
    }
}
