package com.hivemc.chunker.scheduling.task;

import com.hivemc.chunker.scheduling.LoggedException;
import com.hivemc.chunker.scheduling.function.Invokable;
import com.hivemc.chunker.scheduling.task.executor.TaskExecutor;
import com.hivemc.chunker.util.SneakyThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A task which wraps an invokable function with a name and a weight.
 *
 * @param <I> the parameter type for the task.
 * @param <O> the return type for the task.
 */
public class WrappedTask<I, O> extends TrackedTask<O> implements Function<I, O>, Supplier<O> {
    protected Invokable<I, O> lambda;
    private CompletableFuture<O> future;

    /**
     * Create a new wrapped task.
     *
     * @param name   the name of the task.
     * @param weight the weight of the task.
     * @param lambda in the lambda to invoke when this task is scheduled.
     */
    public WrappedTask(String name, TaskWeight weight, Invokable<I, O> lambda) {
        super(name, weight);
        this.lambda = lambda;
    }

    /**
     * Set up the future for the task to use for future dependencies and free-ing of internal state.
     * Note: Ensure you call setupParent() before the task runs!
     *
     * @param future the future for after the current task has executed.
     */
    protected void setupFuture(CompletableFuture<O> future) {
        // Set up the future, after it's done we need to wait for children then free our self
        this.future = future.thenCompose(this::waitForChildren).handle((input, throwable) -> {
            // Free resources after children have completed
            free();

            // Re-throw any error, we've free'd our resources
            if (throwable != null) {
                // Unwrap completion exception
                if (throwable instanceof CompletionException) {
                    throwable = throwable.getCause();
                }

                // Check if it's not a logged exception
                if (!(throwable instanceof LoggedException)) {
                    // Call the exception handler then mark as logged
                    TaskExecutor.currentExecutor().handleException(throwable);
                    throwable = new LoggedException(throwable);
                }

                // Print the exception
                SneakyThrows.throwException(throwable);
            }

            // Return the input for the next function
            return input;
        });
    }

    @Override
    public CompletableFuture<O> future() {
        return future;
    }

    @Override
    public O get() {
        return apply(null);
    }

    @Override
    public O apply(I input) {
        start();
        try {
            return onSelfComplete(lambda.invoke(input));
        } catch (Throwable t) {
            // Mark it as failed with the exception
            onSelfFail(t);
            return null;
        } finally {
            lambda = null;
        }
    }

    @Override
    protected void free() {
        super.free();

        // To prevent memory leaks we'll clean up internal resources if they didn't get cleaned up
        lambda = null;
    }

    /**
     * Returns the priority to used when this task is scheduled for the TaskExecutor.
     *
     * @return an integer based on priority, larger meaning sooner to be scheduled.
     */
    public int getPriority() {
        return getDepth(); // Priority is just depth of the task, the more nested it is, the faster we should try to complete it
    }

    /**
     * A Wrapped Task which always uses the same input as a parameter.
     *
     * @param <T> the type which is used as a parameter.
     * @param <O> the return type for the task.
     */
    static class WrappedTaskConstant<T, O> extends WrappedTask<Object, O> {
        private T constant;

        @SuppressWarnings("unchecked")
        public WrappedTaskConstant(String name, TaskWeight weight, T constant, Invokable<T, O> function) {
            super(name, weight, (Invokable<Object, O>) function); // This technically does take type T, however we want to satisfy our generic
            this.constant = constant;
        }

        @Override
        public O apply(Object ignored) {
            try {
                return super.apply(constant);
            } finally {
                constant = null;
            }
        }

        @Override
        protected void free() {
            super.free();
            constant = null;
        }
    }
}
