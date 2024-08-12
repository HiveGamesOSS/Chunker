package com.hivemc.chunker.conversion.encoding.base.writer;

import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import org.jetbrains.annotations.Nullable;

/**
 * A writer which can write columns. A single instance is used per world.
 */
public interface ColumnWriter {
    /**
     * Called when a column needs to be written.
     *
     * @param chunkerColumn the column that has been read.
     * @throws Exception if something went wrong.
     */
    void writeColumn(ChunkerColumn chunkerColumn) throws Exception;

    /**
     * Called when all the column writing has completed.
     *
     * @throws Exception if something went wrong.
     */
    default void flushColumns() throws Exception {
        // It isn't required to handle this method, it can be useful for freeing / flushing resources
    }

    /**
     * Called when a region (32 x 32 columns) has completed writing.
     *
     * @param regionCoordPair the co-ordinates of the region.
     */
    default void flushRegion(RegionCoordPair regionCoordPair) {
        // It isn't required to handle this method, it can be useful for freeing / flushing regions
    }

    /**
     * Get the pre-transform manager which is called before the writer is invoked. This allows columns neighbouring to
     * be processed together if needed.
     *
     * @return the pre-transform manager or null if one is not needed.
     */
    @Nullable
    default PreTransformManager getPreTransformManager() {
        return null;
    }
}
