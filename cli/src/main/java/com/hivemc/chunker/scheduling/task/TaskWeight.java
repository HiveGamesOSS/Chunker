package com.hivemc.chunker.scheduling.task;

/**
 * TaskWeight handles the weight of Tasks which is used to predict the percentage a Task is complete.
 * Various weights are predefined, though in some circumstances you may need to construct your own to represent specific
 * ratios.
 */
public class TaskWeight {
    /**
     * The weight if this task shouldn't be included in any percentage calculation.
     */
    public static final TaskWeight NONE = new TaskWeight(0);

    /**
     * The weight if this task is insignificant, this should be considered for combination tasks / constant time tasks.
     */
    public static final TaskWeight LOW = new TaskWeight(1);
    public static final TaskWeight MEDIUM = new TaskWeight(50);

    /**
     * The default TaskWeight, this should be used if the Task has no other children, and you just want a base weight or
     * if the task has equally weighted children.
     */
    public static final TaskWeight NORMAL = new TaskWeight(100);
    public static final TaskWeight HIGH = new TaskWeight(200);
    public static final TaskWeight HIGHER = new TaskWeight(500);
    public static final TaskWeight HIGHEST = new TaskWeight(1000);

    private final int weight;

    /**
     * Create a new TaskWeight from an integer.
     *
     * @param weight the weight (higher indicating heavier).
     */
    public TaskWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Get the integer weight represented by this object.
     *
     * @return the integer, higher indicating a heavier weight for execution.
     */
    public int getWeight() {
        return weight;
    }
}
