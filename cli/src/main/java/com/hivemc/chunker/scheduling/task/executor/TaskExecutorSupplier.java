package com.hivemc.chunker.scheduling.task.executor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Wrapper for task to be executed, which holds the priority, task runnable (supplier) and the future to call upon
 * completion / exception.
 *
 * @param <T> the return type of the task.
 */
class TaskExecutorSupplier<T> implements PriorityRunnable {
    private final int priority;
    private Supplier<? extends T> supplier;
    private CompletableFuture<T> future;

    /**
     * Create a new task executor supplier wrapper.
     *
     * @param priority the priority of the task (ordered by highest).
     * @param supplier the task as a supplier.
     * @param future   the future which should be used.
     */
    TaskExecutorSupplier(int priority, Supplier<? extends T> supplier, CompletableFuture<T> future) {
        this.priority = priority;
        this.supplier = supplier;
        this.future = future;
    }

    @Override
    public void run() {
        Supplier<? extends T> localSupplier = supplier;
        CompletableFuture<T> localFuture = future;
        if (localSupplier != null && localFuture != null) {
            // Set both fields to null to avoid any leaking of references
            supplier = null;
            future = null;

            // Fire the task if it's not been run
            if (!localFuture.isDone()) {
                try {
                    localFuture.complete(localSupplier.get());
                } catch (Throwable t) {
                    localFuture.completeExceptionally(t);
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
