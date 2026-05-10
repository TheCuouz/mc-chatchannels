package com.cristian.chatchannels.filter;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpamFilter {

    public enum Reason { DUPLICATE, CAPS, FLOOD }

    private final long dupWindowMs;
    private final int capsThresholdPct;
    private final int capsMinLen;
    private final long floodWindowMs;
    private final int floodMax;

    private final ConcurrentHashMap<UUID, String> lastMessage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> lastMessageTime = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Deque<Long>> floodTimestamps = new ConcurrentHashMap<>();

    public SpamFilter(long dupWindowMs, int capsThresholdPct, int capsMinLen,
                      long floodWindowMs, int floodMax) {
        this.dupWindowMs = dupWindowMs;
        this.capsThresholdPct = capsThresholdPct;
        this.capsMinLen = capsMinLen;
        this.floodWindowMs = floodWindowMs;
        this.floodMax = floodMax;
    }

    public @Nullable Reason check(UUID uuid, String message) {
        long now = System.currentTimeMillis();

        // Duplicate check
        String last = lastMessage.get(uuid);
        Long lastTime = lastMessageTime.get(uuid);
        if (last != null && last.equalsIgnoreCase(message)
                && lastTime != null && (now - lastTime) < dupWindowMs) {
            return Reason.DUPLICATE;
        }

        // Caps check
        if (message.length() >= capsMinLen) {
            long caps = message.chars().filter(Character::isUpperCase).count();
            long letters = message.chars().filter(Character::isLetter).count();
            if (letters > 0 && (caps * 100 / letters) >= capsThresholdPct) {
                return Reason.CAPS;
            }
        }

        // Flood check
        Deque<Long> timestamps = floodTimestamps.computeIfAbsent(uuid, k -> new ArrayDeque<>());
        synchronized (timestamps) {
            timestamps.addLast(now);
            while (!timestamps.isEmpty() && (now - timestamps.peekFirst()) > floodWindowMs) {
                timestamps.pollFirst();
            }
            if (timestamps.size() > floodMax) {
                return Reason.FLOOD;
            }
        }

        lastMessage.put(uuid, message);
        lastMessageTime.put(uuid, now);
        return null;
    }

    public void remove(UUID uuid) {
        lastMessage.remove(uuid);
        lastMessageTime.remove(uuid);
        floodTimestamps.remove(uuid);
    }
}
