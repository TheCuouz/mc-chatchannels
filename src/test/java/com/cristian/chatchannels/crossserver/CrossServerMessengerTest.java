package com.cristian.chatchannels.crossserver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CrossServerMessengerTest {

    @Test
    void pmPayloadRoundTrip() {
        String json = CrossServerMessenger.buildPmPayload(
            "uuid-a", "Pepe", "uuid-b", "Maria", "hola mundo", "survival");
        CrossServerMessenger.Payload p = CrossServerMessenger.parsePayload(json);
        assertNotNull(p);
        assertEquals("PM", p.type());
        assertEquals("Pepe", p.fromName());
        assertEquals("Maria", p.toName());
        assertEquals("hola mundo", p.message());
        assertEquals("survival", p.server());
    }

    @Test
    void friendNotifyPayloadRoundTrip() {
        String json = CrossServerMessenger.buildFriendNotifyPayload(
            "uuid-x", "Carlos", "JOIN", "lobby");
        CrossServerMessenger.Payload p = CrossServerMessenger.parsePayload(json);
        assertNotNull(p);
        assertEquals("FRIEND_NOTIFY", p.type());
        assertEquals("Carlos", p.playerName());
        assertEquals("JOIN", p.event());
        assertEquals("lobby", p.server());
    }

    @Test
    void parseInvalidJsonReturnsNull() {
        assertNull(CrossServerMessenger.parsePayload("not json"));
    }
}
