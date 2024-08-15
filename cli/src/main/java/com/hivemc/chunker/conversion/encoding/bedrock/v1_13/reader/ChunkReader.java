package com.hivemc.chunker.conversion.encoding.bedrock.v1_13.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockChunkReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;

public class ChunkReader extends BedrockChunkReader {
    public ChunkReader(BedrockResolvers resolvers, Converter converter, ChunkerChunk chunk) {
        super(resolvers, converter, chunk);
    }
}
