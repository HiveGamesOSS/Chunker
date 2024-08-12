package com.hivemc.chunker.cli.messenger.messaging.response;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;

import java.util.UUID;

/**
 * A message containing a progress update for a request.
 */
public class ProgressResponse extends BasicMessage {
    private final double percentage;

    /**
     * Create a new progress response message.
     *
     * @param requestId  the request which this is in response to.
     * @param percentage the progress between 0-1.
     */
    public ProgressResponse(UUID requestId, double percentage) {
        super(requestId);
        this.percentage = percentage;
    }

    /**
     * Get the percentage between 0-1 for the task.
     *
     * @return the percentage.
     */
    public double getPercentage() {
        return percentage;
    }
}
