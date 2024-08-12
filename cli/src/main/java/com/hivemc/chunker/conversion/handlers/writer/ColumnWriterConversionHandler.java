package com.hivemc.chunker.conversion.handlers.writer;

import com.hivemc.chunker.conversion.encoding.base.writer.ColumnWriter;
import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;

/**
 * ColumnConversionHandler which delegates the methods asynchronously to the writer.
 */
public class ColumnWriterConversionHandler implements ColumnConversionHandler {
    protected final ColumnWriter writer;

    /**
     * Create a new column writer conversion handler.
     *
     * @param writer the writer to delegate methods to.
     */
    public ColumnWriterConversionHandler(ColumnWriter writer) {
        this.writer = writer;
    }

    @Override
    public void convertColumn(ChunkerColumn column) {
        Task.asyncConsume("Writing Column", TaskWeight.NORMAL, writer::writeColumn, column);
    }

    @Override
    public void flushRegion(RegionCoordPair regionCoordPair) {
        // Writers don't currently have this behaviour
        Task.asyncConsume("Flushing Region", TaskWeight.NORMAL, writer::flushRegion, regionCoordPair);
    }

    @Override
    public void flushColumns() {
        Task.async("Flushing Writer Regions", TaskWeight.NORMAL, writer::flushColumns);
    }
}
