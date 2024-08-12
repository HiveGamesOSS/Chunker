package com.hivemc.chunker.conversion.handlers;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;

/**
 * Provides methods to submit intermediate column data to the writer.
 * The data goes through a pipeline and may not immediately go to the writer.
 */
public interface ColumnConversionHandler {
    /**
     * Called when a column has been read.
     *
     * @param column the column that has been read.
     */
    void convertColumn(ChunkerColumn column);

    /**
     * Called when a region (32 x 32 columns) has completed reading.
     *
     * @param regionCoordPair the co-ordinates of the region.
     */
    void flushRegion(RegionCoordPair regionCoordPair);

    /**
     * Called when all the column reading has completed.
     */
    void flushColumns();
}
