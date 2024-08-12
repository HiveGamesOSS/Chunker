package com.hivemc.chunker.scheduling;

/**
 * An exception which has already been logged and doesn't need further logging.
 */
public class LoggedException extends Exception {
    /**
     * Wrap the exception as logged.
     *
     * @param cause the exception.
     */
    public LoggedException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        // Don't fill in stack trace
        return this;
    }
}
