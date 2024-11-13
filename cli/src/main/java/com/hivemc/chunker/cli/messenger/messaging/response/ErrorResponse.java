package com.hivemc.chunker.cli.messenger.messaging.response;

import com.hivemc.chunker.cli.messenger.messaging.BasicMessage;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A message indicating that a request resulted in an error.
 */
public class ErrorResponse extends BasicMessage {
    private final boolean cancelled;
    private final String error;
    @Nullable
    private final String errorId;
    @Nullable
    private final String internalError;
    @Nullable
    private final String stackTrace;

    /**
     * Create a new error response to a request.
     *
     * @param requestId     the request which errored.
     * @param cancelled     whether the task was cancelled.
     * @param error         the error to show to the user.
     * @param errorId       the ID which can be provided for context.
     * @param internalError the internal error which can be used by the system.
     * @param stackTrace    the stackTrace output from the error (e.printStackTrace()).
     */
    public ErrorResponse(UUID requestId, boolean cancelled, String error, @Nullable String errorId, @Nullable String internalError, @Nullable String stackTrace) {
        super(requestId);
        this.cancelled = cancelled;
        this.error = error;
        this.errorId = errorId;
        this.internalError = internalError;
        this.stackTrace = stackTrace;
    }

    /**
     * Whether this error was caused by the task being cancelled.
     *
     * @return true if the cause was the task being cancelled by the user.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * The publicly facing error to show to the user.
     *
     * @return the error to show to the user.
     */
    public String getError() {
        return error;
    }

    /**
     * Get the error ID which can be provided to track the issue.
     *
     * @return the error ID or null if one isn't present.
     */
    @Nullable
    public String getErrorId() {
        return errorId;
    }

    /**
     * Get the internal error which can be used by the platform to provide useful context.
     *
     * @return the internal error (usually the java exception message).
     */
    @Nullable
    public String getInternalError() {
        return internalError;
    }

    /**
     * Get the stack trace which should be printed for the error.
     *
     * @return the java stack trace.
     */
    @Nullable
    public String getStackTrace() {
        return stackTrace;
    }
}
