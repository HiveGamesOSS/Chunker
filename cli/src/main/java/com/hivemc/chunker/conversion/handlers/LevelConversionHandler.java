package com.hivemc.chunker.conversion.handlers;

import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.scheduling.task.Task;

/**
 * Provides methods to submit intermediate level data to the writer.
 * The data goes through a pipeline and may not immediately go to the writer.
 */
public interface LevelConversionHandler {
    /**
     * Called when the initial level information has been read.
     *
     * @param level the level which was read.
     * @return a Task which when complete returns the handler for worlds, it is expected to chain this task to then
     * handle world reading.
     */
    Task<WorldConversionHandler> convertLevel(ChunkerLevel level);

    /**
     * Called when all the level reading has completed.
     */
    void flushLevel();
}
