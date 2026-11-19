package com.hivemc.chunker.conversion.encoding.java.v26_4.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.layout.ChunkerBiomes;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.TagWithName;

import java.util.List;

public class ChunkWriter extends com.hivemc.chunker.conversion.encoding.java.v26_3.writer.ChunkWriter {
    public ChunkWriter(Converter converter, JavaResolvers resolvers, Dimension dimension, ChunkerColumn chunkerColumn) {
        super(converter, resolvers, dimension, chunkerColumn);
    }

    @Override
    protected void writeBiomes(ChunkerChunk chunkerChunk, List<TagWithName<?>> outputs) {
        ChunkerBiomes biomes = chunkerColumn.getBiomes();
        if (biomes == null) return; // Don't write biomes if they're not present

        // Add biome data to each section (offset by 4 as it's 0 based)
        boolean cavesAndCliffs = converter.level().map(level -> level.getSettings().CavesAndCliffs).orElse(false);
        int chunkYIndex = chunkerChunk.getY() + (cavesAndCliffs ? 4 : 0);

        // 26.4 stores the 4x4x4 biomes as "noise_biomes" and per block biomes as "biomes"
        outputs.add(new TagWithName<>("noise_biomes", writeBiomePalette(biomes.as4X4Palette(chunkYIndex), 4)));
        outputs.add(new TagWithName<>("biomes", writeBiomePalette(biomes.asPalette(chunkYIndex), 16)));
    }
}
