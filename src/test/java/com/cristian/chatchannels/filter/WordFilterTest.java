package com.cristian.chatchannels.filter;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordFilterTest {

    @Test
    void replaceMode_replaces() {
        WordFilter filter = new WordFilter(WordFilter.Mode.REPLACE, List.of("badword"), "****");
        WordFilter.Result r = filter.apply("hello badword world");
        assertFalse(r.blocked());
        assertEquals("hello **** world", r.filtered());
    }

    @Test
    void replaceMode_caseInsensitive() {
        WordFilter filter = new WordFilter(WordFilter.Mode.REPLACE, List.of("badword"), "****");
        WordFilter.Result r = filter.apply("hello BADWORD world");
        assertFalse(r.blocked());
        assertEquals("hello **** world", r.filtered());
    }

    @Test
    void blockMode_blocks() {
        WordFilter filter = new WordFilter(WordFilter.Mode.BLOCK, List.of("badword"), "****");
        WordFilter.Result r = filter.apply("hello badword world");
        assertTrue(r.blocked());
    }

    @Test
    void cleanMessage_passes() {
        WordFilter filter = new WordFilter(WordFilter.Mode.REPLACE, List.of("badword"), "****");
        WordFilter.Result r = filter.apply("hello world");
        assertFalse(r.blocked());
        assertEquals("hello world", r.filtered());
    }

    @Test
    void wordBoundaryRespected() {
        WordFilter filter = new WordFilter(WordFilter.Mode.REPLACE, List.of("bad"), "****");
        WordFilter.Result r = filter.apply("badminton");
        assertFalse(r.blocked());
        assertEquals("badminton", r.filtered());
    }

    @Test
    void emptyWordList_alwaysPasses() {
        WordFilter filter = new WordFilter(WordFilter.Mode.BLOCK, List.of(), "****");
        WordFilter.Result r = filter.apply("any message");
        assertFalse(r.blocked());
        assertEquals("any message", r.filtered());
    }
}
