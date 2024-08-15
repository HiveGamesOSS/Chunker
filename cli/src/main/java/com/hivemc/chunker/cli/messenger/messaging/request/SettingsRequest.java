package com.hivemc.chunker.cli.messenger.messaging.request;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;
import com.hivemc.chunker.cli.messenger.messaging.InvokesWorldConverterRequest;

import java.util.UUID;

/**
 * Request indicating to fetch the settings of a world.
 */
public class SettingsRequest extends BasicMessage implements InvokesWorldConverterRequest {
    private final UUID anonymousId;
    private final String inputPath;
    private final String outputPath;

    /**
     * Create a new settings request.
     *
     * @param anonymousId the user session ID.
     * @param inputPath   the path to the input world.
     * @param outputPath  the path to where to write the output world.
     */
    public SettingsRequest(UUID anonymousId, String inputPath, String outputPath) {
        this.anonymousId = anonymousId;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    /**
     * Create a new settings request.
     *
     * @param requestId   the request ID to use.
     * @param anonymousId the user session ID.
     * @param inputPath   the path to the input world.
     * @param outputPath  the path to where to write the output world.
     */
    public SettingsRequest(UUID requestId, UUID anonymousId, String inputPath, String outputPath) {
        super(requestId);
        this.anonymousId = anonymousId;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
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
     * The path to the input world.
     *
     * @return a path to the input world on the current system.
     */
    public String getInputPath() {
        return inputPath;
    }

    /**
     * The path which the output should be written.
     *
     * @return a path to where the output world should be written on the current system.
     */
    public String getOutputPath() {
        return outputPath;
    }
}
