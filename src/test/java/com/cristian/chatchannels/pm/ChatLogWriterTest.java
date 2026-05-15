package com.cristian.chatchannels.pm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatLogWriterTest {

    @TempDir Path tempDir;

    @Test
    void logsPmLine() throws IOException {
        ChatLogWriter writer = new ChatLogWriter(tempDir.toFile(), true);
        writer.logPm("Pepe", "Maria", "local", "hola");
        writer.close();

        List<String> lines = readLog();
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("[PM] Pepe → Maria (local): hola"));
    }

    @Test
    void logsChannelLine() throws IOException {
        ChatLogWriter writer = new ChatLogWriter(tempDir.toFile(), true);
        writer.logChannel("global", "Pepe", "hello");
        writer.close();

        List<String> lines = readLog();
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("[CHANNEL:global] Pepe: hello"));
    }

    @Test
    void doesNothingWhenDisabled() throws IOException {
        ChatLogWriter writer = new ChatLogWriter(tempDir.toFile(), false);
        writer.logPm("A", "B", "s", "msg");
        writer.close();

        Path logDir = tempDir.resolve("logs");
        assertFalse(Files.exists(logDir) && Files.list(logDir).findAny().isPresent());
    }

    @Test
    void lineStartsWithTimestamp() throws IOException {
        ChatLogWriter writer = new ChatLogWriter(tempDir.toFile(), true);
        writer.logChannel("global", "X", "test");
        writer.close();

        String line = readLog().get(0);
        assertTrue(line.matches("\\[\\d{2}:\\d{2}:\\d{2}\\] .*"),
            "Expected timestamp prefix but got: " + line);
    }

    private List<String> readLog() throws IOException {
        Path logDir = tempDir.resolve("logs");
        String fileName = "chat-" + LocalDate.now() + ".log";
        return Files.readAllLines(logDir.resolve(fileName));
    }
}
