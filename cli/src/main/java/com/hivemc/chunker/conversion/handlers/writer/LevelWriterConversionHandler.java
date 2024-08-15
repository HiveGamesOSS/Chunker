package com.hivemc.chunker.conversion.handlers.writer;

import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.handlers.LevelConversionHandler;
import com.hivemc.chunker.conversion.handlers.WorldConversionHandler;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import org.jetbrains.annotations.Nullable;

/**
 * LevelConversionHandler which delegates the methods asynchronously to the writer.
 */
public class LevelWriterConversionHandler implements LevelConversionHandler {
    protected final LevelWriter writer;

    /**
     * Create a new level writer conversion handler.
     *
     * @param writer the writer to delegate methods to.
     */
    public LevelWriterConversionHandler(LevelWriter writer) {
        this.writer = writer;
    }

    @Override
    public Task<WorldConversionHandler> convertLevel(ChunkerLevel level) {
        return Task.async("Writing Level", TaskWeight.NORMAL, writer::writeLevel, level)
                .then("Wrapping Conversion Handler", TaskWeight.NONE, this::wrapWorldWriter);
    }

    @Override
    public void flushLevel() {
        Task.async("Flushing Writer Level", TaskWeight.NORMAL, writer::flushLevel);
    }

    /**
     * Wrap the world writer with a conversion handler.
     *
     * @param writer the writer or null.
     * @return the wrapper handler or null.
     */
    @Nullable
    protected WorldConversionHandler wrapWorldWriter(@Nullable WorldWriter writer) {
        return writer == null ? null : new WorldWriterConversionHandler(writer);
    }
}
