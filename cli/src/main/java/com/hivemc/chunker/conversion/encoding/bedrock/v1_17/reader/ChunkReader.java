package com.hivemc.chunker.conversion.encoding.bedrock.v1_17.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;

public class ChunkReader extends com.hivemc.chunker.conversion.encoding.bedrock.v1_16.reader.ChunkReader {
    public ChunkReader(BedrockResolvers resolvers, Converter converter, ChunkerChunk chunk) {
        super(resolvers, converter, chunk);
    }

    @Override
    public void readChunk(byte[] subChunkData) {
        // Call super
        super.readChunk(subChunkData);

        // Offset chunk
        applyChunkOffset();
    }

    protected void applyChunkOffset() {
        if (converter.level().map(level -> level.getSettings().CavesAndCliffs).orElse(true)) {
            chunk.setY((byte) (chunk.getY() - 4)); // Move down 4 for Caves & Cliffs in older versions
        }
    }
}
