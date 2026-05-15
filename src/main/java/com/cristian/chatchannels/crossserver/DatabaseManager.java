package com.cristian.chatchannels.crossserver;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Stub — replaced in Task 14 with the real cross-server database implementation.
 */
public interface DatabaseManager {

    /** Asynchronously fetches the server name where the given player last connected. */
    void getPlayerSession(UUID playerUuid, Consumer<Optional<String>> callback);
}
