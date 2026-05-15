package com.cristian.chatchannels.cfg;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration cfg;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.cfg = plugin.getConfig();
    }

    public FileConfiguration raw() { return cfg; }

    public String language()   { return cfg.getString("language", "es").toLowerCase(); }
    public String serverName() { return cfg.getString("server-name", "survival"); }

    public boolean mysqlEnabled()   { return cfg.getBoolean("mysql.enabled", false); }
    public String  mysqlHost()      { return cfg.getString("mysql.host", "localhost"); }
    public int     mysqlPort()      { return cfg.getInt("mysql.port", 3306); }
    public String  mysqlDatabase()  { return cfg.getString("mysql.database", "chatchannels"); }
    public String  mysqlUsername()  { return cfg.getString("mysql.username", "root"); }
    public String  mysqlPassword()  { return cfg.getString("mysql.password", ""); }
    public int     mysqlPoolSize()  { return cfg.getInt("mysql.pool-size", 5); }

    public boolean loggingEnabled() { return cfg.getBoolean("logging.enabled", true); }

    public int  friendsMaxFriends()    { return cfg.getInt("friends.max-friends", 50); }
    public int  friendsRequestTtlDays(){ return cfg.getInt("friends.request-ttl-days", 7); }
    public boolean friendsNotifyOnJoin(){ return cfg.getBoolean("friends.notify-on-join", true); }
}
