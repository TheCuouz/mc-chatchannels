package com.cristian.chatchannels.pm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IgnoreManagerTest {

    @TempDir Path tempDir;

    private IgnoreManager manager() {
        return new IgnoreManager(tempDir.toFile());
    }

    @Test
    void notIgnoredByDefault() {
        IgnoreManager m = manager();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        assertFalse(m.isIgnoring(a, b));
    }

    @Test
    void addIgnoreWorks() {
        IgnoreManager m = manager();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        assertTrue(m.addIgnore(a, b));
        assertTrue(m.isIgnoring(a, b));
    }

    @Test
    void addIgnoreTwiceReturnsFalse() {
        IgnoreManager m = manager();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        m.addIgnore(a, b);
        assertFalse(m.addIgnore(a, b));
    }

    @Test
    void removeIgnoreWorks() {
        IgnoreManager m = manager();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        m.addIgnore(a, b);
        assertTrue(m.removeIgnore(a, b));
        assertFalse(m.isIgnoring(a, b));
    }

    @Test
    void removeNonExistentReturnsFalse() {
        IgnoreManager m = manager();
        assertFalse(m.removeIgnore(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void ignoreIsUnidirectional() {
        IgnoreManager m = manager();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        m.addIgnore(a, b);
        assertFalse(m.isIgnoring(b, a));
    }

    @Test
    void persistsAcrossReload() {
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        IgnoreManager m = new IgnoreManager(tempDir.toFile());
        m.addIgnore(a, b);
        m.save();

        IgnoreManager m2 = new IgnoreManager(tempDir.toFile());
        m2.load();
        assertTrue(m2.isIgnoring(a, b));
    }

    @Test
    void getIgnoreListReturnsAllIgnored() {
        IgnoreManager m = manager();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID(), c = UUID.randomUUID();
        m.addIgnore(a, b);
        m.addIgnore(a, c);
        assertEquals(2, m.getIgnored(a).size());
    }
}
