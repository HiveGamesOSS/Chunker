package com.hivemc.chunker.conversion.encoding.java.v26_4.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaChunkReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.biome.layout.ChunkerClusterPaletteBasedBiomes;
import com.hivemc.chunker.conversion.intermediate.column.biome.layout.ChunkerPaletteBasedBiomes;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.EmptyPalette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColumnReader extends com.hivemc.chunker.conversion.encoding.java.v26_3.reader.ColumnReader {
    public ColumnReader(Converter converter, JavaResolvers resolvers, Dimension dimension, ChunkCoordPair columnCoords, CompoundTag columnNBT) {
        super(converter, resolvers, dimension, columnCoords, columnNBT);
    }

    @Override
    public JavaChunkReader createChunkReader(ChunkerColumn column, ChunkerChunk chunk) {
        return new ChunkReader(converter, resolvers, column, chunk);
    }

    @Override
    protected void readBiomes(ChunkerColumn column) {
        // 26.4 stores per block biomes (16x16x16) as "biomes" and the old 4x4x4 biomes as "noise_biomes"
        ListTag<CompoundTag, Map<String, Tag<?>>> sections = columnNBT.getList("sections", CompoundTag.class, null);
        if (sections == null || !isPerBlockBiomes(sections)) {
            // Attempt to use older biome reading instead
            super.readBiomes(column);
            return;
        }

        List<Palette<ChunkerBiome>> chunks = new ArrayList<>(sections.size());
        for (CompoundTag section : sections) {
            try {
                CompoundTag biomes = section.getCompound("biomes", null);
                if (biomes != null) {
                    chunks.add(readBiomePalette(biomes, 16));
                    continue;
                }

                // Upgraded chunks may only have noise biomes until they're regenerated
                CompoundTag noiseBiomes = section.getCompound("noise_biomes", null);
                if (noiseBiomes != null) {
                    chunks.add(ChunkerClusterPaletteBasedBiomes.toPalette(readBiomePalette(noiseBiomes, 4)));
                    continue;
                }

                chunks.add(EmptyPalette.instance(16));
            } catch (Exception e) {
                converter.logNonFatalException(e);

                // Add an empty palette for this section
                chunks.add(EmptyPalette.instance(16));
            }
        }

        column.setBiomes(new ChunkerPaletteBasedBiomes(chunks));
    }

    /**
     * Check whether the sections use the per block biome format.
     *
     * @param sections the sections of the chunk.
     * @return true if it is using per block biomes.
     */
    protected boolean isPerBlockBiomes(ListTag<CompoundTag, Map<String, Tag<?>>> sections) {
        for (CompoundTag section : sections) {
            if (section.contains("noise_biomes")) return true;

            // 16x16x16 at least 64 longs (4096 entries at 1 bit)
            CompoundTag biomes = section.getCompound("biomes", null);
            long[] data = biomes == null ? null : biomes.getLongArray("data", null);
            if (data != null && data.length >= 64) return true;
        }

        // Likely old format
        return false;
    }
}
