package com.hivemc.chunker.scheduling.task;

import java.util.concurrent.CompletableFuture;

/**
 * A wrapped CompletableFuture as a task.
 *
 * @param <T> the type which the completable future returns.
 */
public class FutureTask<T> implements Task<T> {
    private final CompletableFuture<T> future;

    /**
     * Create a new task which wraps a future.
     *
     * @param future the future to wrap and use as the return value for .future();
     */
    public FutureTask(CompletableFuture<T> future) {
        this.future = future;
    }

    @Override
    public CompletableFuture<T> future() {
        return future;
    }
}
