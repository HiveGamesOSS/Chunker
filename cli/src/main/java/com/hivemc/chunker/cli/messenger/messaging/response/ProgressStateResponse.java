package com.hivemc.chunker.cli.messenger.messaging.response;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A message containing a progress state update for a request.
 */
public class ProgressStateResponse extends BasicMessage {
    private final @Nullable String name;
    private final boolean animated;

    /**
     * Create a new progress response message.
     *
     * @param requestId    the request which this is in response to.
     * @param progressName the progress state name.
     * @param animated     if the progress is animated.
     */
    public ProgressStateResponse(UUID requestId, @Nullable String progressName, boolean animated) {
        super(requestId);
        this.name = progressName;
        this.animated = animated;
    }

    /**
     * Get the progress state for the task.
     *
     * @return the progress name.
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Get whether the progress state is animated.
     *
     * @return true if it's animated.
     */
    public boolean isAnimated() {
        return animated;
    }
}
