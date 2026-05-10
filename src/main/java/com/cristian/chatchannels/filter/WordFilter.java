package com.cristian.chatchannels.filter;

import java.util.List;
import java.util.regex.Pattern;

public class WordFilter {

    public enum Mode { REPLACE, BLOCK }

    public record Result(boolean blocked, String filtered) {}

    private final Mode mode;
    private final String replacement;
    private final Pattern pattern;

    public WordFilter(Mode mode, List<String> words, String replacement) {
        this.mode = mode;
        this.replacement = replacement;
        if (words.isEmpty()) {
            this.pattern = null;
        } else {
            String joined = words.stream()
                .map(Pattern::quote)
                .reduce((a, b) -> a + "|" + b)
                .orElse("");
            this.pattern = Pattern.compile("\\b(" + joined + ")\\b", Pattern.CASE_INSENSITIVE);
        }
    }

    public Result apply(String message) {
        if (pattern == null) return new Result(false, message);

        var matcher = pattern.matcher(message);
        if (!matcher.find()) return new Result(false, message);

        if (mode == Mode.BLOCK) return new Result(true, message);
        return new Result(false, matcher.replaceAll(replacement));
    }
}
