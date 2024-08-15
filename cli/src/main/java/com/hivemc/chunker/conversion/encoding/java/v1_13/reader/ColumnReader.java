package com.hivemc.chunker.conversion.encoding.java.v1_13.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaChunkReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.biome.layout.ChunkerColumnBasedBiomes;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.array.IntArrayTag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

public class ColumnReader extends com.hivemc.chunker.conversion.encoding.java.v1_11.reader.ColumnReader {
    public ColumnReader(Converter converter, JavaResolvers resolvers, Dimension dimension, ChunkCoordPair columnCoords, CompoundTag columnNBT) {
        super(converter, resolvers, dimension, columnCoords, columnNBT);
    }

    @Override
    protected void readHeightMap(ChunkerColumn column) {
        // Ignored as HeightMap changed
    }

    @Override
    protected void readBiomes(ChunkerColumn column) {
        Tag<?> biomes = columnNBT.get("Biomes", Tag.class);
        if (biomes instanceof IntArrayTag intArrayTag) {
            int[] value = intArrayTag.getValue();

            // The length of the array hints us at what types of biomes these are
            if (value != null && value.length == 256) {
                // Column based integer biomes
                ChunkerBiome[] chunkerBiomeArray = new ChunkerBiome[256];

                // Read each int and convert it to a chunker biome
                for (int i = 0; i < chunkerBiomeArray.length; i++) {
                    chunkerBiomeArray[i] = resolvers.readBiome(value[i], dimension);
                }
                column.setBiomes(new ChunkerColumnBasedBiomes(chunkerBiomeArray));
            }
        }

        // Fallback to legacy biome reading
        super.readBiomes(column);
    }

    @Override
    public JavaChunkReader createChunkReader(ChunkerColumn column, ChunkerChunk chunk) {
        return new ChunkReader(converter, resolvers, column, chunk);
    }
}
