package com.cristian.chatchannels.crossserver;

import com.cristian.chatchannels.friends.FriendRequest;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

public final class DatabaseManager {

    private final HikariDataSource dataSource;
    private final Plugin plugin;

    public DatabaseManager(Plugin plugin, String host, int port, String database,
                           String username, String password, int poolSize) throws SQLException {
        this.plugin = plugin;
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database
            + "?useSSL=false&autoReconnect=true&characterEncoding=utf8");
        cfg.setUsername(username);
        cfg.setPassword(password);
        cfg.setMaximumPoolSize(poolSize);
        cfg.setConnectionTimeout(5_000);
        cfg.setPoolName("ChattyChannels");
        this.dataSource = new HikariDataSource(cfg);
        initialize();
    }

    private void initialize() throws SQLException {
        try (Connection c = dataSource.getConnection(); Statement s = c.createStatement()) {
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS cc_player_sessions (
                  uuid        VARCHAR(36)  NOT NULL PRIMARY KEY,
                  player_name VARCHAR(64)  NOT NULL,
                  server_name VARCHAR(64)  NOT NULL,
                  updated_at  BIGINT       NOT NULL
                )""");
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS cc_friends (
                  uuid_a     VARCHAR(36) NOT NULL,
                  uuid_b     VARCHAR(36) NOT NULL,
                  created_at BIGINT      NOT NULL,
                  PRIMARY KEY (uuid_a, uuid_b)
                )""");
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS cc_friend_requests (
                  sender_uuid   VARCHAR(36) NOT NULL,
                  receiver_uuid VARCHAR(36) NOT NULL,
                  sender_name   VARCHAR(64) NOT NULL,
                  sent_at       BIGINT      NOT NULL,
                  PRIMARY KEY (sender_uuid, receiver_uuid)
                )""");
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS cc_ignores (
                  ignorer_uuid VARCHAR(36) NOT NULL,
                  ignored_uuid VARCHAR(36) NOT NULL,
                  PRIMARY KEY (ignorer_uuid, ignored_uuid)
                )""");
        }
        runAsync(this::cleanStaleSessions);
    }

    // ── Sessions ──────────────────────────────────────────────────────────────

    public void upsertSession(UUID uuid, String name, String server) {
        runAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement("""
                     REPLACE INTO cc_player_sessions (uuid, player_name, server_name, updated_at)
                     VALUES (?, ?, ?, ?)""")) {
                ps.setString(1, uuid.toString());
                ps.setString(2, name);
                ps.setString(3, server);
                ps.setLong(4, System.currentTimeMillis());
                ps.executeUpdate();
            } catch (SQLException e) { logError("upsertSession", e); }
        });
    }

    public void removeSession(UUID uuid) {
        runAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM cc_player_sessions WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) { logError("removeSession", e); }
        });
    }

    public void getPlayerSession(UUID uuid, Consumer<Optional<String>> callback) {
        runAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "SELECT server_name FROM cc_player_sessions WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                Optional<String> result = rs.next()
                    ? Optional.of(rs.getString("server_name")) : Optional.empty();
                runSync(() -> callback.accept(result));
            } catch (SQLException e) {
                logError("getPlayerSession", e);
                runSync(() -> callback.accept(Optional.empty()));
            }
        });
    }

    private void cleanStaleSessions() {
        long cutoff = System.currentTimeMillis() - (10L * 60 * 1000);
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM cc_player_sessions WHERE updated_at < ?")) {
            ps.setLong(1, cutoff);
            int deleted = ps.executeUpdate();
            if (deleted > 0)
                plugin.getSLF4JLogger().info("Cleaned " + deleted + " stale session(s).");
        } catch (SQLException e) { logError("cleanStaleSessions", e); }
    }

    // ── Friends ───────────────────────────────────────────────────────────────

    public void addFriendship(UUID a, UUID b) {
        runAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT IGNORE INTO cc_friends (uuid_a, uuid_b, created_at) VALUES (?,?,?),(?,?,?)")) {
                long now = System.currentTimeMillis();
                ps.setString(1, a.toString()); ps.setString(2, b.toString()); ps.setLong(3, now);
                ps.setString(4, b.toString()); ps.setString(5, a.toString()); ps.setLong(6, now);
                ps.executeUpdate();
            } catch (SQLException e) { logError("addFriendship", e); }
        });
    }

    public void removeFriendship(UUID a, UUID b) {
        runAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM cc_friends WHERE (uuid_a=? AND uuid_b=?) OR (uuid_a=? AND uuid_b=?)")) {
                ps.setString(1, a.toString()); ps.setString(2, b.toString());
                ps.setString(3, b.toString()); ps.setString(4, a.toString());
                ps.executeUpdate();
            } catch (SQLException e) { logError("removeFriendship", e); }
        });
    }

    public void getFriends(UUID uuid, Consumer<List<UUID>> callback) {
        runAsync(() -> {
            List<UUID> list = new ArrayList<>();
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "SELECT uuid_b FROM cc_friends WHERE uuid_a = ?")) {
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    try { list.add(UUID.fromString(rs.getString(1))); }
                    catch (IllegalArgumentException ignored) {}
                }
            } catch (SQLException e) { logError("getFriends", e); }
            runSync(() -> callback.accept(list));
        });
    }

    // ── Friend Requests ───────────────────────────────────────────────────────

    public void addRequest(FriendRequest req) {
        runAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT IGNORE INTO cc_friend_requests "
                     + "(sender_uuid, receiver_uuid, sender_name, sent_at) VALUES (?,?,?,?)")) {
                ps.setString(1, req.senderUuid().toString());
                ps.setString(2, req.receiverUuid().toString());
                ps.setString(3, req.senderName());
                ps.setLong(4, req.sentAt());
                ps.executeUpdate();
            } catch (SQLException e) { logError("addRequest", e); }
        });
    }

    public void removeRequest(UUID senderUuid, UUID receiverUuid) {
        runAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM cc_friend_requests WHERE sender_uuid=? AND receiver_uuid=?")) {
                ps.setString(1, senderUuid.toString());
                ps.setString(2, receiverUuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) { logError("removeRequest", e); }
        });
    }

    // ── Ignores ───────────────────────────────────────────────────────────────

    public void addIgnore(UUID ignorer, UUID ignored) {
        runAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT IGNORE INTO cc_ignores (ignorer_uuid, ignored_uuid) VALUES (?,?)")) {
                ps.setString(1, ignorer.toString()); ps.setString(2, ignored.toString());
                ps.executeUpdate();
            } catch (SQLException e) { logError("addIgnore", e); }
        });
    }

    public void removeIgnore(UUID ignorer, UUID ignored) {
        runAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM cc_ignores WHERE ignorer_uuid=? AND ignored_uuid=?")) {
                ps.setString(1, ignorer.toString()); ps.setString(2, ignored.toString());
                ps.executeUpdate();
            } catch (SQLException e) { logError("removeIgnore", e); }
        });
    }

    public void close() {
        if (!dataSource.isClosed()) dataSource.close();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void runAsync(Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    private void runSync(Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    private void logError(String method, SQLException e) {
        plugin.getSLF4JLogger().error("DatabaseManager." + method + " failed", e);
    }
}
