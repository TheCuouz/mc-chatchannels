package com.cristian.chatchannels.friends;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FriendManager {

    private static final Logger LOG = Logger.getLogger(FriendManager.class.getName());

    private final Map<UUID, Set<UUID>> friends = new ConcurrentHashMap<>();
    /** senderUuid → Map<receiverUuid, FriendRequest> */
    private final Map<UUID, Map<UUID, FriendRequest>> pendingBySender = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> notifyPrefs = new ConcurrentHashMap<>();
    private final File dataFolder;
    private final int maxFriends;
    private final int requestTtlDays;

    public FriendManager(File dataFolder, int maxFriends, int requestTtlDays) {
        this.dataFolder = dataFolder;
        this.maxFriends = maxFriends;
        this.requestTtlDays = requestTtlDays;
    }

    public boolean areFriends(UUID a, UUID b) {
        Set<UUID> set = friends.get(a);
        return set != null && set.contains(b);
    }

    public boolean canAddFriend(UUID uuid) {
        Set<UUID> set = friends.get(uuid);
        return set == null || set.size() < maxFriends;
    }

    public Set<UUID> getFriends(UUID uuid) {
        Set<UUID> set = friends.get(uuid);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    /** @return false if already friends or duplicate pending request */
    public boolean addRequest(FriendRequest request) {
        if (areFriends(request.senderUuid(), request.receiverUuid())) return false;
        Map<UUID, FriendRequest> senderMap =
            pendingBySender.computeIfAbsent(request.senderUuid(), k -> new ConcurrentHashMap<>());
        if (senderMap.containsKey(request.receiverUuid())) return false;
        senderMap.put(request.receiverUuid(), request);
        saveRequests();
        return true;
    }

    public boolean hasPendingRequest(UUID senderUuid, UUID receiverUuid) {
        Map<UUID, FriendRequest> map = pendingBySender.get(senderUuid);
        return map != null && map.containsKey(receiverUuid);
    }

    /** Returns all requests where receiverUuid is the receiver (for /friend requests) */
    public List<FriendRequest> getPendingRequestsFor(UUID receiverUuid) {
        List<FriendRequest> result = new ArrayList<>();
        for (Map<UUID, FriendRequest> map : pendingBySender.values()) {
            for (FriendRequest req : map.values()) {
                if (req.receiverUuid().equals(receiverUuid) && !req.isExpired(requestTtlDays))
                    result.add(req);
            }
        }
        return result;
    }

    public void acceptRequest(UUID senderUuid, UUID receiverUuid) {
        Map<UUID, FriendRequest> map = pendingBySender.get(senderUuid);
        if (map == null || !map.containsKey(receiverUuid)) return;
        map.remove(receiverUuid);
        if (map.isEmpty()) pendingBySender.remove(senderUuid);
        // Add symmetric friendship
        friends.computeIfAbsent(senderUuid, k -> ConcurrentHashMap.newKeySet()).add(receiverUuid);
        friends.computeIfAbsent(receiverUuid, k -> ConcurrentHashMap.newKeySet()).add(senderUuid);
        save();
    }

    public void denyRequest(UUID senderUuid, UUID receiverUuid) {
        Map<UUID, FriendRequest> map = pendingBySender.get(senderUuid);
        if (map == null) return;
        map.remove(receiverUuid);
        if (map.isEmpty()) pendingBySender.remove(senderUuid);
        saveRequests();
    }

    public void removeFriendship(UUID a, UUID b) {
        Set<UUID> setA = friends.get(a);
        if (setA != null) { setA.remove(b); if (setA.isEmpty()) friends.remove(a); }
        Set<UUID> setB = friends.get(b);
        if (setB != null) { setB.remove(a); if (setB.isEmpty()) friends.remove(b); }
        save();
    }

    public boolean getNotify(UUID uuid, boolean defaultValue) {
        return notifyPrefs.getOrDefault(uuid, defaultValue);
    }

    public void setNotify(UUID uuid, boolean value) {
        notifyPrefs.put(uuid, value);
        saveNotify();
    }

    public void load() {
        loadFriends();
        loadRequests();
        loadNotify();
    }

    public void save() {
        saveFriends();
        saveRequests();
        saveNotify();
    }

    private void loadFriends() {
        File f = new File(dataFolder, "friends.yml");
        if (!f.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection sec = cfg.getConfigurationSection("friends");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                Set<UUID> set = ConcurrentHashMap.newKeySet();
                for (String s : sec.getStringList(key)) {
                    try { set.add(UUID.fromString(s)); } catch (IllegalArgumentException ignored) {}
                }
                if (!set.isEmpty()) friends.put(uuid, set);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private void saveFriends() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Set<UUID>> e : friends.entrySet()) {
            List<String> list = new ArrayList<>();
            e.getValue().forEach(u -> list.add(u.toString()));
            Collections.sort(list);
            cfg.set("friends." + e.getKey(), list);
        }
        saveFile(new File(dataFolder, "friends.yml"), cfg);
    }

    private void loadRequests() {
        File f = new File(dataFolder, "friend_requests.yml");
        if (!f.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection sec = cfg.getConfigurationSection("requests");
        if (sec == null) return;
        for (String senderKey : sec.getKeys(false)) {
            try {
                UUID sender = UUID.fromString(senderKey);
                ConfigurationSection sub = sec.getConfigurationSection(senderKey);
                if (sub == null) continue;
                for (String receiverKey : sub.getKeys(false)) {
                    try {
                        UUID receiver = UUID.fromString(receiverKey);
                        String senderName = sub.getString(receiverKey + ".sender_name", "?");
                        long sentAt = sub.getLong(receiverKey + ".sent_at", 0L);
                        FriendRequest req = new FriendRequest(sender, senderName, receiver, sentAt);
                        if (!req.isExpired(requestTtlDays)) {
                            pendingBySender
                                .computeIfAbsent(sender, k -> new ConcurrentHashMap<>())
                                .put(receiver, req);
                        }
                    } catch (IllegalArgumentException ignored) {}
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private void saveRequests() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<UUID, FriendRequest>> e : pendingBySender.entrySet()) {
            for (Map.Entry<UUID, FriendRequest> re : e.getValue().entrySet()) {
                String path = "requests." + e.getKey() + "." + re.getKey();
                cfg.set(path + ".sender_name", re.getValue().senderName());
                cfg.set(path + ".sent_at", re.getValue().sentAt());
            }
        }
        saveFile(new File(dataFolder, "friend_requests.yml"), cfg);
    }

    private void loadNotify() {
        File f = new File(dataFolder, "friend_notify.yml");
        if (!f.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection sec = cfg.getConfigurationSection("notify");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            try { notifyPrefs.put(UUID.fromString(key), sec.getBoolean(key)); }
            catch (IllegalArgumentException ignored) {}
        }
    }

    private void saveNotify() {
        YamlConfiguration cfg = new YamlConfiguration();
        notifyPrefs.forEach((uuid, val) -> cfg.set("notify." + uuid, val));
        saveFile(new File(dataFolder, "friend_notify.yml"), cfg);
    }

    private void saveFile(File file, YamlConfiguration cfg) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            cfg.save(file);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to save " + file.getName(), e);
        }
    }
}
