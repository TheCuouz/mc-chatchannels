package com.cristian.chatchannels.friends;

import java.util.UUID;

public record FriendRequest(UUID senderUuid, String senderName,
                             UUID receiverUuid, long sentAt) {

    public boolean isExpired(int ttlDays) {
        long ttlMs = (long) ttlDays * 86_400_000L;
        return System.currentTimeMillis() - sentAt > ttlMs;
    }
}
