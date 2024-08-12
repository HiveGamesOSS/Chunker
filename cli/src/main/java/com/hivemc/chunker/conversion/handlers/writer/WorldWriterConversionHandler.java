package com.hivemc.chunker.conversion.handlers.writer;

import com.hivemc.chunker.conversion.encoding.base.writer.ColumnWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.handlers.WorldConversionHandler;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import org.jetbrains.annotations.Nullable;

/**
 * WorldWriterConversionHandler which delegates the methods asynchronously to the writer.
 */
public class WorldWriterConversionHandler implements WorldConversionHandler {
    protected final WorldWriter writer;

    /**
     * Create a new world writer conversion handler.
     *
     * @param writer the writer to delegate methods to.
     */
    public WorldWriterConversionHandler(WorldWriter writer) {
        this.writer = writer;
    }

    @Override
    public Task<ColumnConversionHandler> convertWorld(ChunkerWorld world) {
        return Task.async("Writing World", TaskWeight.NORMAL, writer::writeWorld, world)
                .then("Wrapping Conversion Handler", TaskWeight.NONE, this::wrapColumnWriter);
    }

    @Override
    public void flushWorld(ChunkerWorld world) {
        Task.asyncConsume("Flushing Writer World", TaskWeight.NORMAL, writer::flushWorld, world);
    }

    @Override
    public void flushWorlds() {
        Task.async("Flushing Writer Worlds", TaskWeight.NORMAL, writer::flushWorlds);
    }

    /**
     * Wrap the column writer with a conversion handler.
     *
     * @param writer the writer or null.
     * @return the wrapper handler or null.
     */
    @Nullable
    protected ColumnConversionHandler wrapColumnWriter(@Nullable ColumnWriter writer) {
        return writer == null ? null : new ColumnWriterConversionHandler(writer);
    }
}
