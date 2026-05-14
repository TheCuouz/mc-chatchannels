package com.cristian.chatchannels.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HiddenChannelsManagerTest {

    @TempDir
    Path tmp;

    private File yamlFile;
    private HiddenChannelsManager manager;

    private static final UUID U1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID U2 = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        yamlFile = tmp.resolve("hidden_channels.yml").toFile();
        manager = new HiddenChannelsManager(yamlFile);
    }

    @AfterEach
    void tearDown() {
        if (yamlFile.exists()) yamlFile.delete();
    }

    @Test
    void hide_setsChannelHiddenForUuid() {
        manager.hide(U1, "trade");

        assertTrue(manager.isHidden(U1, "trade"));
        assertFalse(manager.isHidden(U1, "global"));
        assertFalse(manager.isHidden(U2, "trade"));
    }

    @Test
    void show_removesHiddenChannel() {
        manager.hide(U1, "trade");
        assertTrue(manager.isHidden(U1, "trade"));

        boolean removed = manager.show(U1, "trade");

        assertTrue(removed);
        assertFalse(manager.isHidden(U1, "trade"));
    }

    @Test
    void show_returnsFalseWhenNotHidden() {
        assertFalse(manager.show(U1, "trade"));
    }

    @Test
    void getHidden_emptyByDefault() {
        Set<String> hidden = manager.getHidden(U1);

        assertNotNull(hidden);
        assertTrue(hidden.isEmpty());
    }

    @Test
    void getHidden_returnsHiddenChannels() {
        manager.hide(U1, "trade");
        manager.hide(U1, "local");

        Set<String> hidden = manager.getHidden(U1);

        assertEquals(2, hidden.size());
        assertTrue(hidden.contains("trade"));
        assertTrue(hidden.contains("local"));
    }

    @Test
    void hide_isIdempotent() {
        boolean first = manager.hide(U1, "trade");
        boolean second = manager.hide(U1, "trade");

        assertTrue(first);
        assertFalse(second);
        assertEquals(1, manager.getHidden(U1).size());
    }

    @Test
    void saveAndLoad_roundTripPreservesState() {
        manager.hide(U1, "trade");
        manager.hide(U1, "local");
        manager.hide(U2, "staff");
        manager.save();

        HiddenChannelsManager reloaded = new HiddenChannelsManager(yamlFile);
        reloaded.load();

        assertTrue(reloaded.isHidden(U1, "trade"));
        assertTrue(reloaded.isHidden(U1, "local"));
        assertFalse(reloaded.isHidden(U1, "staff"));
        assertTrue(reloaded.isHidden(U2, "staff"));
        assertFalse(reloaded.isHidden(U2, "trade"));

        assertEquals(2, reloaded.getHidden(U1).size());
        assertEquals(1, reloaded.getHidden(U2).size());
    }

    @Test
    void show_removesUuidEntryWhenSetEmpty() {
        manager.hide(U1, "trade");
        manager.show(U1, "trade");

        assertTrue(manager.getHidden(U1).isEmpty());
        // Confirm there is no leftover after round-trip either
        manager.save();
        HiddenChannelsManager reloaded = new HiddenChannelsManager(yamlFile);
        reloaded.load();
        assertTrue(reloaded.getHidden(U1).isEmpty());
    }

    @Test
    void getHidden_isUnmodifiable() {
        manager.hide(U1, "trade");
        Set<String> hidden = manager.getHidden(U1);

        assertThrows(UnsupportedOperationException.class, () -> hidden.add("global"));
    }

    @Test
    void load_onMissingFile_isNoOp() {
        File missing = tmp.resolve("does_not_exist.yml").toFile();
        HiddenChannelsManager m = new HiddenChannelsManager(missing);

        assertDoesNotThrow(m::load);
        assertTrue(m.getHidden(U1).isEmpty());
    }
}
