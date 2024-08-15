package com.hivemc.chunker.conversion.encoding.base.writer;

import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;

/**
 * A writer which can write world data. A single instance is used for all the worlds in a map.
 */
public interface WorldWriter {
    /**
     * Called when the initial world information should be written.
     *
     * @param chunkerWorld the world which was read.
     * @return the column writer to use.
     * @throws Exception if something went wrong.
     */
    ColumnWriter writeWorld(ChunkerWorld chunkerWorld) throws Exception;

    /**
     * Called when a world has finished being written.
     *
     * @param chunkerWorld the world which has finished being written.
     * @throws Exception if something went wrong.
     */
    default void flushWorld(ChunkerWorld chunkerWorld) throws Exception {
        // It isn't required to implement this method, it can be useful for knowing when a world is done
    }

    /**
     * Called when all the worlds have been written.
     *
     * @throws Exception if something went wrong.
     */
    default void flushWorlds() throws Exception {
        // It isn't required to implement this method, it can be useful for knowing when all worlds have been written
    }
}
