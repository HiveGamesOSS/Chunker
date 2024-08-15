package com.hivemc.chunker.conversion.encoding.java.v1_17.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

public class ChunkWriter extends com.hivemc.chunker.conversion.encoding.java.v1_16.writer.ChunkWriter {
    public ChunkWriter(Converter converter, JavaResolvers resolvers, Dimension dimension, ChunkerColumn chunkerColumn) {
        super(converter, resolvers, dimension, chunkerColumn);
    }

    @Override
    protected boolean isChunkHeightSupported(byte chunkY) {
        return true; // Extended height is supported
    }
}
