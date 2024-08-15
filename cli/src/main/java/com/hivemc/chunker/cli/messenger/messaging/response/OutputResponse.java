package com.hivemc.chunker.cli.messenger.messaging.response;

import com.google.gson.JsonElement;
import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;

import java.util.UUID;

/**
 * A message containing the json output for a request.
 */
public class OutputResponse extends BasicMessage {
    private final JsonElement output;

    /**
     * Create a new output response message.
     *
     * @param requestId the request which this is in response to.
     * @param output    the JSON object used for the output.
     */
    public OutputResponse(UUID requestId, JsonElement output) {
        super(requestId);
        this.output = output;
    }

    /**
     * The JSON representing the output.
     *
     * @return the output JSON.
     */
    public JsonElement getOutput() {
        return output;
    }
}
