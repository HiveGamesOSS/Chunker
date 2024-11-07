package com.hivemc.chunker.conversion.encoding.bedrock.v1_17.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerGeneratorType;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

public class ChunkReader extends com.hivemc.chunker.conversion.encoding.bedrock.v1_16.reader.ChunkReader {
    public ChunkReader(BedrockResolvers resolvers, Converter converter, Dimension dimension, ChunkerChunk chunk) {
        super(resolvers, converter, dimension, chunk);
    }

    @Override
    public void readChunk(byte[] subChunkData) {
        // Call super
        super.readChunk(subChunkData);

        // Offset chunk
        applyChunkOffset();
    }

    protected void applyChunkOffset() {
        if (converter.level().map(level -> level.getSettings().CavesAndCliffs).orElse(true)
                && dimension == Dimension.OVERWORLD
                && converter.level().map(level -> level.getSettings().GeneratorType).orElse(ChunkerGeneratorType.CUSTOM) == ChunkerGeneratorType.NORMAL) {
            chunk.setY((byte) (chunk.getY() - 4)); // Move down 4 for Caves & Cliffs in older versions
        }
    }
}
