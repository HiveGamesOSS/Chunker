package com.hivemc.chunker.cli.messenger.messaging.request;


import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;

import java.util.UUID;

/**
 * Request indicating all WorldConverterRequests from a session should be cancelled.
 */
public class KillRequest extends BasicMessage {
    private final UUID anonymousId;

    /**
     * Create a new kill request from a randomized session ID.
     *
     * @param anonymousId the session ID for which instances should be cancelled.
     */
    public KillRequest(UUID anonymousId) {
        this.anonymousId = anonymousId;
    }

    /**
     * Create a new kill request with a specific request ID.
     *
     * @param requestId   the request ID to use.
     * @param anonymousId the session ID for which instances should be cancelled.
     */
    public KillRequest(UUID requestId, UUID anonymousId) {
        super(requestId);
        this.anonymousId = anonymousId;
    }

    /**
     * Get the randomized session ID for which requests should be cancelled.
     *
     * @return the UUID.
     */
    public UUID getAnonymousId() {
        return anonymousId;
    }
}
