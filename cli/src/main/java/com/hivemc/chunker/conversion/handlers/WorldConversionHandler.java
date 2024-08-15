package com.hivemc.chunker.conversion.handlers;

import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.scheduling.task.Task;

/**
 * Provides methods to submit intermediate world data to the writer.
 * The data goes through a pipeline and may not immediately go to the writer.
 */
public interface WorldConversionHandler {
    /**
     * Called when the initial world information has been read.
     *
     * @param world the world which was read.
     * @return a Task which when complete returns the handler for columns, it is expected to chain this task to then
     * handle column reading.
     */
    Task<ColumnConversionHandler> convertWorld(ChunkerWorld world);

    /**
     * Called when a world has finished being read.
     *
     * @param world the world which has finished being read.
     */
    void flushWorld(ChunkerWorld world);

    /**
     * Called when all the worlds have been read.
     */
    void flushWorlds();
}
