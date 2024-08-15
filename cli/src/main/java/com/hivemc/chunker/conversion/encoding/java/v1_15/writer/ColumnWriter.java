package com.hivemc.chunker.conversion.encoding.java.v1_15.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaChunkWriter;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.array.IntArrayTag;

public class ColumnWriter extends com.hivemc.chunker.conversion.encoding.java.v1_14.writer.ColumnWriter {
    public ColumnWriter(JavaWorldWriter parent, Converter converter, JavaResolvers resolvers, Dimension dimension) {
        super(parent, converter, resolvers, dimension);
    }

    @Override
    public JavaChunkWriter createChunkWriter(ChunkerColumn column) {
        return new ChunkWriter(converter, resolvers, dimension, column);
    }

    @Override
    protected TagWithName<?> writeBiomes(ChunkerColumn column) {
        if (column.getBiomes() == null) return null; // Skip if not present

        // Written as 4x4 clusters
        ChunkerBiome[] columnBiomes = column.getBiomes().as4X4(resolvers.getFallbackBiome(dimension));

        // Loop through each biome and convert it to an int
        int[] biomes = new int[columnBiomes.length];
        for (int i = 0; i < biomes.length; i++) {
            biomes[i] = resolvers.writeBiomeID(columnBiomes[i], dimension);
        }

        return new TagWithName<>("Biomes", new IntArrayTag(biomes));
    }
}
