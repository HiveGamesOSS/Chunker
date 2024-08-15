package com.hivemc.chunker.scheduling.task;

/**
 * A task which can report its current progress towards completion.
 *
 * @param <O> the type which the task returns when complete.
 */
public interface ProgressiveTask<O> extends Task<O> {
    /**
     * Get the progress of this task between 0-1D which represents its own completion and the completion of its
     * children.
     *
     * @return a percentage between 0 and 1.
     */
    double getProgress();

    /**
     * Print the detailed progress for this task and any children.
     *
     * @return a representation of the progress break-down, used for debugging.
     */
    String getDetailedProgress();

    /**
     * Get the weight of this task for when compared to other sibling tasks executed in the same scope.
     *
     * @return the weight when compared to other tasks also submitted in the same scope.
     */
    int getWeight();

    /**
     * Get the weight for this task that accounts for work actually done and not delegated. This is used to balance the
     * weight between this task and it's children.
     *
     * @return the weight to be used when compared against scheduled children.
     */
    default int getSelfWeight() {
        return 1;
    }
}
