package com.hivemc.chunker.scheduling.task.executor;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * TaskExecutor handles the execution of asynchronous tasks, these are scheduled by priority and then polled by the
 * threads of the executor. ThreadLocals are used to assign the TaskExecutor so after creating a TaskExecutor the
 * {@link #setCurrentThreadExecutor()} method should be called in the relevant thread to ensure tasks are scheduled.
 */
public class TaskExecutor {
    private static final ThreadLocal<TaskExecutor> EXECUTORS = new InheritableThreadLocal<>();
    private static final Comparator<PriorityRunnable> COMPARATOR = Comparator.comparing(PriorityRunnable::getPriority).reversed();
    private final PriorityBlockingQueue<PriorityRunnable> tasks = new PriorityBlockingQueue<>(100, COMPARATOR);
    private final Thread[] pool;
    private final Consumer<Throwable> exceptionHandler;
    @Nullable
    private final BiConsumer<String, Object> signalConsumer;

    /**
     * Create a new TaskExecutor to handle new tasks.
     *
     * @param threads          the number of threads to use and start.
     * @param exceptionHandler the handler if present to use for exceptions that occur.
     * @param signalConsumer   a consumer which can accept signals from tasks.
     */
    public TaskExecutor(int threads, @Nullable Consumer<Throwable> exceptionHandler, @Nullable BiConsumer<String, Object> signalConsumer) {
        pool = new Thread[threads];
        for (int i = 0; i < pool.length; i++) {
            pool[i] = new Thread(this::threadLoop, "Task Processor " + i);
            pool[i].setUncaughtExceptionHandler(this::handleUncaughtException);
            pool[i].start();
        }
        this.exceptionHandler = exceptionHandler;
        this.signalConsumer = signalConsumer;
    }

    /**
     * Handle a thread uncaught exception.
     *
     * @param thread    the thread handling the exception.
     * @param throwable the exception (note: if it's an out of memory exception then be careful not to allocate).
     */
    protected void handleUncaughtException(Thread thread, Throwable throwable) {
        // Ensure OutOfMemory is properly handled
        if (throwable instanceof OutOfMemoryError) {
            try {
                throwable.printStackTrace();
            } catch (OutOfMemoryError e2) {
                // We tried printing it
            }

            // Use error code 12 for OOM
            System.exit(12);
        }

        // Call the handler
        handleException(throwable);
    }

    /**
     * Get the current TaskExecutor for the current thread.
     *
     * @return the task executor, an exception will be thrown if one isn't present.
     */
    @NotNull
    public static TaskExecutor currentExecutor() {
        TaskExecutor executor = EXECUTORS.get();
        Preconditions.checkNotNull(executor, "There is no current executor, either an Environment hasn't been made or this thread is outside the scope.");

        // Return the value
        return executor;
    }

    /**
     * Set the executor for the current thread as this task executor.
     */
    public void setCurrentThreadExecutor() {
        // Setup thread local
        EXECUTORS.set(this);
    }

    /**
     * Clear the task executor set for the current thread.
     */
    public void clearCurrentThreadExecutor() {
        // Only remove if it's this instance
        if (EXECUTORS.get() == this) {
            EXECUTORS.remove();
        }
    }

    /**
     * Handle an exception which occurred during the TaskExecutor.
     * If a handler is present it will be called otherwise it will be printed.
     *
     * @param throwable the exception.
     */
    public void handleException(@NotNull Throwable throwable) {
        if (exceptionHandler != null) {
            exceptionHandler.accept(throwable);
        } else {
            throwable.printStackTrace(); // Print if no handler
        }
    }

    /**
     * Execute a task with a priority.
     *
     * @param supplier the supplier which when executed runs the task and returns a result.
     * @param priority the priority in relation to other tasks which it should be executed (highest first).
     * @param <T>      the type that the task returns when executed.
     * @return the completable future for monitoring the task completion.
     */
    public <T> CompletableFuture<T> execute(Supplier<T> supplier, int priority) {
        // Create the future and wrapper
        CompletableFuture<T> future = new CompletableFuture<>();
        TaskExecutorSupplier<T> wrapper = new TaskExecutorSupplier<>(priority, supplier, future);

        // Schedule the task
        tasks.add(wrapper);

        // Return the future so it can be used
        return future;
    }

    /**
     * Shutdown all the threads in the task executor (interrupt) and clear the queue.
     */
    public void shutdown() {
        for (Thread thread : pool) {
            thread.interrupt();
        }

        // Clear any tasks
        tasks.clear();
    }

    /**
     * Send a signal to the environment.
     *
     * @param signalName  the name of the signal.
     * @param signalValue the value that is being signalled.
     */
    public void signal(String signalName, Object signalValue) {
        if (signalConsumer == null) return; // Don't consume signals if there is no consumer

        // Run the consumer
        try {
            signalConsumer.accept(signalName, signalValue);
        } catch (Throwable t) {
            // Handle exceptions without interrupting the call.
            handleException(t);
        }
    }

    /**
     * Used by the workers as the main thread loop.
     * <p>
     * When the thread is interrupted it will no longer poll tasks.
     */
    protected void threadLoop() {
        // Setup thread local
        setCurrentThreadExecutor();

        // Main loop
        while (!Thread.currentThread().isInterrupted()) {
            PriorityRunnable task = tasks.poll();
            if (task != null) {
                task.run();
            }
        }

        // Clear thread local
        clearCurrentThreadExecutor();
    }
}
