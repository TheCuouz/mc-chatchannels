package com.cristian.chatchannels.chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatFormatTest {

    @Test void dropsUnresolvedPlaceholders() {
        assertEquals("<gray>[<gold>G</gold>]</gray> <player>: <message>",
            ChatFormat.stripUnresolved("<gray>[<gold>G</gold>]</gray> %luckperms_prefix%<player>: <message>"));
    }

    @Test void collapsesTheGapLeftBehind() {
        assertEquals("[G] <player>: <message>",
            ChatFormat.stripUnresolved("[G] %vault_rank% <player>: <message>"));
    }

    @Test void leavesFormatsWithoutPlaceholdersAlone() {
        String f = "<gray>[<green>L</green>]</gray> <player>: <message>";
        assertEquals(f, ChatFormat.stripUnresolved(f));
    }

    @Test void keepsLoneNonPlaceholderPercent() {
        assertEquals("<player> is 100% done: <message>",
            ChatFormat.stripUnresolved("<player> is 100% done: <message>"));
    }
}
