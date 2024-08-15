package com.hivemc.chunker.scheduling.task.executor;

/**
 * A runnable which has a priority that can be used to indicate execution order.
 */
public interface PriorityRunnable extends Runnable {
    /**
     * The priority of the task, higher indicating it should be executed sooner.
     *
     * @return an integer priority relative to other tasks.
     */
    int getPriority();
}
