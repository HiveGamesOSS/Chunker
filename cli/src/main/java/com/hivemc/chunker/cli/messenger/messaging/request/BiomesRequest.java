package com.hivemc.chunker.cli.messenger.messaging.request;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;

/**
 * Request indicating to return the supported biome identifiers for an input and output format.
 */
public class BiomesRequest extends BasicMessage {
    private final String inputPath;
    private final String outputType;

    /**
     * Create a request to fetch valid biome identifiers.
     *
     * @param inputPath  the path to the input world (used to detect the input format).
     * @param outputType the encoded output format ID.
     */
    public BiomesRequest(String inputPath, String outputType) {
        this.inputPath = inputPath;
        this.outputType = outputType;
    }

    /**
     * The path to the input world on this machine, used to detect the input format/version.
     *
     * @return the relative path.
     */
    public String getInputPath() {
        return inputPath;
    }

    /**
     * The encoded output format ID.
     *
     * @return the format ID string.
     */
    public String getOutputType() {
        return outputType;
    }
}
