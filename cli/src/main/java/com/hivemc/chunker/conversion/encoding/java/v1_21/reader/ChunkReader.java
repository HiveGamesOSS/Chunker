package com.hivemc.chunker.conversion.encoding.java.v1_21.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;

public class ChunkReader extends com.hivemc.chunker.conversion.encoding.java.v1_20_5.reader.ChunkReader {
    public ChunkReader(Converter converter, JavaResolvers resolvers, ChunkerColumn column, ChunkerChunk chunk) {
        super(converter, resolvers, column, chunk);
    }
}
