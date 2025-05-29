package com.hivemc.chunker.conversion.encoding.bedrock.v1_21_90.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

public class ChunkReader extends com.hivemc.chunker.conversion.encoding.bedrock.v1_21_80.reader.ChunkReader {
    public ChunkReader(BedrockResolvers resolvers, Converter converter, Dimension dimension, ChunkerChunk chunk) {
        super(resolvers, converter, dimension, chunk);
    }
}
