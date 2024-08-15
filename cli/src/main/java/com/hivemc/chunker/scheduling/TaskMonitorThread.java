package com.hivemc.chunker.scheduling;

import com.hivemc.chunker.scheduling.task.ProgressiveTask;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/**
 * Thread to Monitor a specific task and report progress and completion.
 */
public class TaskMonitorThread extends Thread {
    private final ProgressiveTask<?> task;
    private final DoubleConsumer progressChangeHandler;
    private final Consumer<Optional<Throwable>> completionHandler;
    private final CompletableFuture<?> taskFuture;

    /**
     * Create the new monitor thread (not automatically started).
     *
     * @param task                  the task to be monitored (the future should be ready to use).
     * @param progressChangeHandler the consumer to use for progress updates which are reported at 0.01% minimum.
     * @param completionHandler     the consumer to call when the task has completed with exception if one occurred.
     */
    public TaskMonitorThread(ProgressiveTask<?> task, DoubleConsumer progressChangeHandler, Consumer<Optional<Throwable>> completionHandler) {
        super("Task Monitor Thread");
        this.task = task;
        this.progressChangeHandler = progressChangeHandler;
        this.completionHandler = completionHandler;
        taskFuture = task.future();
    }

    @Override
    public void run() {
        double lastProgress = -1D;
        while (!taskFuture.isDone() && lastProgress < 1D) {
            double progress = task.getProgress();

            // If the progress change is more than 0.01%, update the handler
            if (Math.abs(progress - lastProgress) >= 0.0001D) {
                lastProgress = progress;
                progressChangeHandler.accept(lastProgress);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                return; // Interrupted, cancel
            }
        }

        // Mark as complete
        taskFuture.whenComplete((result, throwable) -> {
            completionHandler.accept(Optional.ofNullable(throwable));
        });
    }
}
