package com.hivemc.chunker.conversion.encoding.bedrock.v1_17_30.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

import java.nio.ByteBuffer;

public class ChunkReader extends com.hivemc.chunker.conversion.encoding.bedrock.v1_17.reader.ChunkReader {
    public ChunkReader(BedrockResolvers resolvers, Converter converter, Dimension dimension, ChunkerChunk chunk) {
        super(resolvers, converter, dimension, chunk);
    }

    @Override
    protected void readPalette(byte version, ByteBuffer buffer) {
        // Only version 9 is currently supported by this reader
        if (version != 9) {
            // Try super
            super.readPalette(version, buffer);
            return;
        }

        // Grab how many layers are present
        byte layers = buffer.get();

        // Version 9 contains a signed-byte indicating what Y value this chunk is
        chunk.setY(buffer.get());

        // Loop through each layer and read the palettes
        readLayers(buffer, layers);
    }

    @Override
    protected void applyChunkOffset() {
        // No offset needed for 1.17.30 since the Y is set in the readPalette
    }
}
