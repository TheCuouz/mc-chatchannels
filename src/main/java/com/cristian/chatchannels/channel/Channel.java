package com.cristian.chatchannels.channel;

public record Channel(
    String id,
    String displayName,
    String quickPrefix,
    int range,
    String permission,
    String format,
    int cooldownSeconds
) {
    public boolean isGlobal() {
        return range < 0;
    }
}
