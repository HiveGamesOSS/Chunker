package com.hivemc.chunker.cli.messenger.messaging.request;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;
import com.hivemc.chunker.cli.messenger.messaging.DimensionPruningList;
import com.hivemc.chunker.cli.messenger.messaging.InvokesWorldConverterRequest;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * Request indicating to render a preview of the world.
 */
public class PreviewRequest extends BasicMessage implements InvokesWorldConverterRequest {
    private final UUID anonymousId;
    private final String inputPath;
    private final String outputPath;
    @Nullable
    private final Map<Dimension, Dimension> inputToOutputDimension;
    @Nullable
    private final DimensionPruningList pruningList;

    /**
     * Create a new preview request for a world.
     *
     * @param anonymousId            the session ID for the user.
     * @param inputPath              the path of the world on this machine.
     * @param outputPath             the path to write the output on this machine.
     * @param inputToOutputDimension if present, dimension mapping on how to remap dimensions.
     * @param pruningList            a list of pruning rules to apply to the input world.
     */
    public PreviewRequest(UUID anonymousId, String inputPath, String outputPath, @Nullable Map<Dimension, Dimension> inputToOutputDimension, @Nullable DimensionPruningList pruningList) {
        this.anonymousId = anonymousId;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.inputToOutputDimension = inputToOutputDimension;
        this.pruningList = pruningList;
    }

    /**
     * Create a new preview request for a world.
     *
     * @param requestId              the ID to use for the request.
     * @param anonymousId            the session ID for the user.
     * @param inputPath              the path of the world on this machine.
     * @param outputPath             the path to write the output on this machine.
     * @param inputToOutputDimension if present, dimension mapping on how to remap dimensions.
     * @param pruningList            a list of pruning rules to apply to the input world.
     */
    public PreviewRequest(UUID requestId, UUID anonymousId, String inputPath, String outputPath, @Nullable Map<Dimension, Dimension> inputToOutputDimension, @Nullable DimensionPruningList pruningList) {
        super(requestId);
        this.anonymousId = anonymousId;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.inputToOutputDimension = inputToOutputDimension;
        this.pruningList = pruningList;
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

    /**
     * A mapping of dimension type to output dimension type. If the dimension isn't present in the map, it won't be
     * processed.
     *
     * @return null if the input should map to the output otherwise a map of dimensions.
     */
    @Nullable
    public Map<Dimension, Dimension> getInputToOutputDimension() {
        return inputToOutputDimension;
    }

    /**
     * Get a list of pruning rules to run for each dimension.
     *
     * @return pruning rules otherwise null if none are present.
     */
    @Nullable
    public DimensionPruningList getPruningList() {
        return pruningList;
    }
}
