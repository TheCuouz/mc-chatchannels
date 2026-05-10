package com.cristian.chatchannels.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DurationParserTest {

    @Test
    void parseMinutes() {
        assertEquals(600_000L, DurationParser.parseMillis("10m"));
    }

    @Test
    void parseHours() {
        assertEquals(3_600_000L, DurationParser.parseMillis("1h"));
    }

    @Test
    void parseDays() {
        assertEquals(172_800_000L, DurationParser.parseMillis("2d"));
    }

    @Test
    void invalidReturnsMinusOne() {
        assertEquals(-1L, DurationParser.parseMillis("abc"));
        assertEquals(-1L, DurationParser.parseMillis(""));
        assertEquals(-1L, DurationParser.parseMillis(null));
    }

    @Test
    void formatMinutes() {
        assertEquals("10m", DurationParser.format(600_000L));
    }

    @Test
    void formatHours() {
        assertEquals("1h", DurationParser.format(3_600_000L));
    }

    @Test
    void formatDays() {
        assertEquals("2d", DurationParser.format(172_800_000L));
    }

    @Test
    void formatPermanent() {
        assertEquals("permanente", DurationParser.format(-1L));
    }
}
