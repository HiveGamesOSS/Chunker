package com.hivemc.chunker.conversion.encoding.bedrock.v1_19_80.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockChunkReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;

public class ColumnReader extends com.hivemc.chunker.conversion.encoding.bedrock.v1_19.reader.ColumnReader {
    public ColumnReader(BedrockResolvers resolvers, Converter converter, DB database, Dimension dimension, ChunkCoordPair columnCoords) {
        super(resolvers, converter, database, dimension, columnCoords);
    }

    @Override
    public BedrockChunkReader createChunkReader(ChunkerChunk chunk) {
        return new ChunkReader(resolvers, converter, dimension, chunk);
    }
}
