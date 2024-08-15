package com.hivemc.chunker.scheduling.task;

import com.hivemc.chunker.util.SneakyThrows;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

/**
 * TrackedTask is a task which can track its depth and have children. It implements progressive task allowing progress
 * to be reported.
 *
 * @param <O> the type returned from the task.
 */
public abstract class TrackedTask<O> implements ProgressiveTask<O> {
    private static final ThreadLocal<Stack<TrackedTask<?>>> currentTaskStack = ThreadLocal.withInitial(Stack::new);
    private final String name;
    private final int weight;
    private Task.TaskStatus status;
    private List<ProgressiveTask<?>> children;
    private int depth;

    /**
     * Create a new TrackedTask.
     *
     * @param name   the name of the task.
     * @param weight the weight of the task.
     */
    public TrackedTask(String name, TaskWeight weight) {
        this.name = name;
        this.weight = weight.getWeight();
        status = TaskStatus.PENDING;
    }

    /**
     * Mark the task as started.
     */
    protected void start() {
        synchronized (this) {
            // Ensure the task isn't invalid for safety
            if (status != TaskStatus.PENDING) {
                throw new IllegalArgumentException("Unable to start task which has already been started!");
            }

            // Push ourselves as the currentTask
            currentTaskStack.get().push(this);

            // Mark this start as started
            status = TaskStatus.STARTED;
        }
    }

    /**
     * Set up the parent of this task to know about this task, it is vital to call this before the task is scheduled as
     * otherwise it may not fetch the correct currentTask.
     */
    protected void setupParent() {
        // If this task was created while a parent is present, it is a child
        // Even if it's a "then", the progress of the "then" belongs to the branch it was made in.
        TrackedTask<?> parentTask = currentTaskStack.get().peek();
        if (parentTask == null) {
            throw new RuntimeException("Tried to launch a task without a parent, was this called from a thread outside the task system? Wrap your stuff in try(Task.environment()){}!");
        }

        // Add this task as a child
        synchronized (parentTask) {
            // Ensure the parent isn't DONE
            if (parentTask.status == TaskStatus.DONE) {
                throw new RuntimeException("Tried to launch a task after the parent completed, this shouldn't be possible.");
            }
            if (parentTask.children == null) {
                parentTask.children = new LinkedList<>();
            }
            parentTask.children.add(this);
        }

        // Setup depth
        depth = parentTask.getDepth() + 1;
    }

    /**
     * Free the resources used by this task and mark it as done.
     * Note: This may not be the same thread as start() as this is done after children have been processed.
     * If you wish to free thread locals, this should be done in @link{{@link #onSelfComplete(Object)}}
     */
    protected void free() {
        synchronized (this) {
            // Ensure the free isn't invalid for safety
            if (status == TaskStatus.PENDING) {
                throw new IllegalArgumentException("Unable to free task which has not started");
            }

            // Ensure the free isn't invalid for safety
            if (status == TaskStatus.DONE) {
                throw new IllegalArgumentException("Unable to free task which has already been freed!");
            }

            // Mark this task as stopped
            status = TaskStatus.DONE;

            // To prevent memory leaks we'll clean up internal resources
            children = null;
        }
    }

    /**
     * Called just after the current task completes in the same thread.
     * Note: This is used to clear ThreadLocals.
     *
     * @param result the result from the task.
     * @return the result forwarded for the next task.
     */
    protected O onSelfComplete(O result) {
        // Remove ourselves as the current task, as this thread will be used by other tasks
        if (currentTaskStack.get().pop() != this) {
            throw new IllegalArgumentException("This task did not remove itself? Something went wrong in handling completion");
        }

        // Return the result
        return result;
    }

    /**
     * Called just after the current task fails in the same thread.
     * Note: This is used to clear ThreadLocals.
     *
     * @param ex the exception from the task.
     */
    protected void onSelfFail(Throwable ex) {
        // Remove ourselves as the current task, as this thread will be used by other tasks
        if (currentTaskStack.get().pop() != this) {
            throw new IllegalArgumentException("This task did not remove itself? Something went wrong in handling completion");
        }

        // Throw the exception
        SneakyThrows.throwException(ex);
    }

    /**
     * Wait until all the children of a task has completed and then return the result.
     *
     * @param result the result to return when completed.
     * @return a completable future which is called upon completion (may already be completed upon return).
     */
    protected CompletableFuture<O> waitForChildren(O result) {
        synchronized (this) {
            if (status == TaskStatus.DONE) {
                // Return a completed future as this task is done
                return CompletableFuture.completedFuture(result);
            }

            // Ensure that all children are completed
            if (children == null || children.isEmpty()) {
                // Return a completed future
                return CompletableFuture.completedFuture(result);
            } else {
                return Task.join(children).then(result).future();
            }
        }
    }

    @Override
    public double getProgress() {
        synchronized (this) {
            if (status == TaskStatus.PENDING) {
                return 0; // No progress
            } else if (status == TaskStatus.STARTED) {
                if (children != null) {
                    int totalWeight = getSelfWeight();
                    double weightedProgressTotal = 0D;
                    for (ProgressiveTask<?> task : children) {
                        weightedProgressTotal += task.getProgress() * task.getWeight();
                        totalWeight += task.getWeight();
                    }
                    return totalWeight == 0 ? 0 : weightedProgressTotal / totalWeight;
                } else {
                    return 0D; // There are no children so we can't infer progress
                }
            } else if (status == TaskStatus.DONE) {
                return 1D; // done
            }

            throw new IllegalArgumentException("Unknown TaskStatus");
        }
    }

    @Override
    public String getDetailedProgress() {
        synchronized (this) {
            String padding = "-".repeat(getDepth());
            String details = (padding + " " + getName() + " - " + String.format("%.2f%%", getProgress() * 100D) + " - Weight: " + getWeight() + "\n");

            // Print children
            if (children != null) {
                for (ProgressiveTask<?> task : children) {
                    details = details.concat(task.getDetailedProgress());
                }
            }
            return details;
        }
    }

    /**
     * Get the name of this task.
     *
     * @return the name of the task.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the depth of this task (how many parents the task has).
     *
     * @return the depth of the task, which is made from the parent depth + 1.
     */
    public int getDepth() {
        return depth;
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
