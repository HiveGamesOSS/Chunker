package com.hivemc.chunker.scheduling.task;

import com.google.common.base.Preconditions;
import com.hivemc.chunker.scheduling.task.executor.TaskExecutor;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Environment is a wrapped task which can hold all async tasks, this also allows progress monitoring.
 */
public class Environment extends TrackedTask<Void> implements Closeable {
    private final TaskExecutor executor;
    private CompletableFuture<Void> future;

    /**
     * Create an environment and start threads relating to the environment.
     *
     * @param name             the name of the environment used for the task.
     * @param threads          the number of threads to use for execution of tasks.
     * @param exceptionHandler the handler to use for exceptions that occur.
     * @param signalConsumer   a consumer which can accept signals from tasks.
     */
    public Environment(String name, int threads, Consumer<Throwable> exceptionHandler, @Nullable BiConsumer<String, Object> signalConsumer) {
        super(name, TaskWeight.NONE);
        executor = new TaskExecutor(threads, exceptionHandler, signalConsumer);
    }

    @Override
    protected void start() {
        // Call start
        super.start();

        // Setup executor
        executor.setCurrentThreadExecutor();
    }

    @Override
    public void close() {
        // Record the future
        future = waitForChildren(null).toCompletableFuture();

        // Clear executor
        executor.clearCurrentThreadExecutor();

        // Schedule call to free
        future().thenRun(this::free);
    }

    @Override
    public int getWeight() {
        return 0; // No weight to the task itself, only children
    }

    /**
     * Cancel the environment and any tasks.
     *
     * @param exception the exception to use for the future of this environment.
     */
    public void cancel(@Nullable Throwable exception) {
        try {
            // Call shutdown
            executor.shutdown();
        } finally {
            if (exception != null) {
                // Complete the future with an exception
                future.completeExceptionally(exception);
            } else {
                // Cancel
                future.cancel(true);
            }
        }
    }

    /**
     * Get the future which is completed after all the tasks scheduled have finished.
     * Note: If this is called before the environment has scheduled any tasks, it may complete early as it simply
     * depends upon any children being complete. It is recommended to call this after any tasks have been scheduled.
     *
     * @return a new future which completes when all children have completed.
     */
    @Override
    public CompletableFuture<Void> future() {
        Preconditions.checkNotNull(future, "It is not possible to wait for an environment which tasks can still be queued, please call after .close()!");

        // This tasks future simply waits for the children
        return future;
    }

    @Override
    protected void free() {
        // Call super
        super.free();

        // Close down thread-pool
        executor.shutdown();
    }
}
