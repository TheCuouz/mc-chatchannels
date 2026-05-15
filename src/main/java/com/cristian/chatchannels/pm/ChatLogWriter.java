package com.cristian.chatchannels.pm;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class ChatLogWriter {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final File logsDir;
    private final boolean enabled;
    private BufferedWriter writer;
    private LocalDate openDate;

    public ChatLogWriter(File dataFolder, boolean enabled) {
        this.logsDir = new File(dataFolder, "logs");
        this.enabled = enabled;
    }

    public void logPm(String from, String to, String server, String message) {
        if (!enabled) return;
        write("[PM] " + from + " → " + to + " (" + server + "): " + message);
    }

    public void logChannel(String channel, String player, String message) {
        if (!enabled) return;
        write("[CHANNEL:" + channel + "] " + player + ": " + message);
    }

    private synchronized void write(String entry) {
        try {
            ensureWriter();
            writer.write("[" + LocalTime.now().format(TIME_FMT) + "] " + entry);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            // Silent — log writer failure must never crash the server
        }
    }

    private void ensureWriter() throws IOException {
        LocalDate today = LocalDate.now();
        if (writer != null && today.equals(openDate)) return;
        close();
        if (!logsDir.exists()) logsDir.mkdirs();
        File file = new File(logsDir, "chat-" + today.format(DATE_FMT) + ".log");
        writer = new BufferedWriter(new FileWriter(file, true));
        openDate = today;
    }

    public synchronized void close() {
        if (writer == null) return;
        try { writer.close(); } catch (IOException ignored) {}
        writer = null;
        openDate = null;
    }
}
