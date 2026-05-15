package com.cristian.chatchannels.friends;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FriendManagerTest {

    @TempDir Path tempDir;

    private FriendManager manager() {
        return new FriendManager(tempDir.toFile(), 50, 7);
    }

    @Test
    void notFriendsByDefault() {
        FriendManager m = manager();
        assertFalse(m.areFriends(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void addAndAcceptRequest() {
        FriendManager m = manager();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        m.addRequest(new FriendRequest(a, "A", b, System.currentTimeMillis()));
        assertTrue(m.hasPendingRequest(a, b));
        m.acceptRequest(a, b);
        assertTrue(m.areFriends(a, b));
        assertTrue(m.areFriends(b, a)); // symmetric
        assertFalse(m.hasPendingRequest(a, b));
    }

    @Test
    void denyRequestClearsPending() {
        FriendManager m = manager();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        m.addRequest(new FriendRequest(a, "A", b, System.currentTimeMillis()));
        m.denyRequest(a, b);
        assertFalse(m.hasPendingRequest(a, b));
        assertFalse(m.areFriends(a, b));
    }

    @Test
    void removeFriendship() {
        FriendManager m = manager();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        m.addRequest(new FriendRequest(a, "A", b, System.currentTimeMillis()));
        m.acceptRequest(a, b);
        m.removeFriendship(a, b);
        assertFalse(m.areFriends(a, b));
        assertFalse(m.areFriends(b, a));
    }

    @Test
    void maxFriendsEnforced() {
        FriendManager m = new FriendManager(tempDir.toFile(), 2, 7);
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID(), c = UUID.randomUUID();
        m.addRequest(new FriendRequest(a, "A", b, System.currentTimeMillis()));
        m.acceptRequest(a, b);
        m.addRequest(new FriendRequest(a, "A", c, System.currentTimeMillis()));
        m.acceptRequest(a, c);
        // Now at max
        assertFalse(m.canAddFriend(a));
    }

    @Test
    void requestToAlreadyFriendReturnsFalse() {
        FriendManager m = manager();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        m.addRequest(new FriendRequest(a, "A", b, System.currentTimeMillis()));
        m.acceptRequest(a, b);
        assertFalse(m.addRequest(new FriendRequest(a, "A", b, System.currentTimeMillis())));
    }

    @Test
    void duplicateRequestReturnsFalse() {
        FriendManager m = manager();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        assertTrue(m.addRequest(new FriendRequest(a, "A", b, System.currentTimeMillis())));
        assertFalse(m.addRequest(new FriendRequest(a, "A", b, System.currentTimeMillis())));
    }

    @Test
    void persistsAcrossReload() {
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        FriendManager m = new FriendManager(tempDir.toFile(), 50, 7);
        m.addRequest(new FriendRequest(a, "A", b, System.currentTimeMillis()));
        m.acceptRequest(a, b);
        m.save();

        FriendManager m2 = new FriendManager(tempDir.toFile(), 50, 7);
        m2.load();
        assertTrue(m2.areFriends(a, b));
    }
}
