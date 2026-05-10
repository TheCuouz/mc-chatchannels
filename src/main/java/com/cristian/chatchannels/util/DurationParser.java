package com.cristian.chatchannels.util;

import org.jetbrains.annotations.Nullable;

public final class DurationParser {

    private DurationParser() {}

    public static long parseMillis(@Nullable String input) {
        if (input == null || input.isBlank()) return -1L;
        try {
            char unit = input.charAt(input.length() - 1);
            long value = Long.parseLong(input.substring(0, input.length() - 1));
            return switch (unit) {
                case 'm' -> value * 60_000L;
                case 'h' -> value * 3_600_000L;
                case 'd' -> value * 86_400_000L;
                default  -> -1L;
            };
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return -1L;
        }
    }

    public static String format(long millis) {
        if (millis < 0) return "permanente";
        long minutes = millis / 60_000L;
        if (minutes < 60) return minutes + "m";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h";
        return (hours / 24) + "d";
    }
}
