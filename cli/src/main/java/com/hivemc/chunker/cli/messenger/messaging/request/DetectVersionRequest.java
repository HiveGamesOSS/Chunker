package com.hivemc.chunker.cli.messenger.messaging.request;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;

import java.util.UUID;

/**
 * Request indicating to return the version of an input world.
 */
public class DetectVersionRequest extends BasicMessage {
    private final UUID anonymousId;
    private final String inputPath;

    /**
     * Create a request to detect the version of a world.
     *
     * @param anonymousId the session ID for the user.
     * @param inputPath   the path to the world on the machine.
     */
    public DetectVersionRequest(UUID anonymousId, String inputPath) {
        this.anonymousId = anonymousId;
        this.inputPath = inputPath;
    }

    /**
     * Create a request to detect the version of a world.
     *
     * @param requestId   the request ID to use for this request.
     * @param anonymousId the session ID for the user.
     * @param inputPath   the path to the world on the machine.
     */
    public DetectVersionRequest(UUID requestId, UUID anonymousId, String inputPath) {
        super(requestId);
        this.anonymousId = anonymousId;
        this.inputPath = inputPath;
    }

    /**
     * The session ID used by the user.
     *
     * @return a random UUID used by the user.
     */
    public UUID getAnonymousId() {
        return anonymousId;
    }

    /**
     * The path on this machine to the world to check the version for.
     *
     * @return the relative path.
     */
    public String getInputPath() {
        return inputPath;
    }
}
