package com.cristian.chatchannels.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SpamFilterTest {

    private SpamFilter filter;
    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        filter = new SpamFilter(5000, 70, 10, 5000, 4);
    }

    @Test
    void firstMessageAlwaysPasses() {
        assertNull(filter.check(uuid, "hello world"));
    }

    @Test
    void duplicateMessageBlocked() {
        filter.check(uuid, "hello world");
        assertEquals(SpamFilter.Reason.DUPLICATE, filter.check(uuid, "hello world"));
    }

    @Test
    void differentMessageAfterDuplicatePasses() {
        filter.check(uuid, "hello world");
        assertNull(filter.check(uuid, "different message here"));
    }

    @Test
    void capsShortMessageIgnored() {
        assertNull(filter.check(uuid, "HELLO"));
    }

    @Test
    void capsLongMessageBlocked() {
        assertEquals(SpamFilter.Reason.CAPS, filter.check(uuid, "HELLO WORLD THIS IS ALL CAPS"));
    }

    @Test
    void floodBlocksAfterMaxMessages() {
        for (int i = 0; i < 4; i++) {
            assertNull(filter.check(uuid, "message " + i));
        }
        assertEquals(SpamFilter.Reason.FLOOD, filter.check(uuid, "message 4"));
    }
}
