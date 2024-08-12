package com.hivemc.chunker.conversion.encoding.java.v1_18.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaChunkReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.util.PaletteUtil;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.biome.layout.ChunkerClusterPaletteBasedBiomes;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.EmptyPalette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.ShortBasedPalette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.SingleValuePalette;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColumnReader extends com.hivemc.chunker.conversion.encoding.java.v1_17.reader.ColumnReader {
    public ColumnReader(Converter converter, JavaResolvers resolvers, Dimension dimension, ChunkCoordPair columnCoords, CompoundTag columnNBT) {
        super(converter, resolvers, dimension, columnCoords, columnNBT);
    }

    @Override
    protected void readBiomes(ChunkerColumn column) {
        // Read the biomes from each chunk section
        ListTag<CompoundTag, Map<String, Tag<?>>> sections = columnNBT.getList("sections", CompoundTag.class, null);
        if (sections != null) {
            List<Palette<ChunkerBiome>> chunks = new ArrayList<>(sections.size());

            // Attempt to loop through each chunk
            for (CompoundTag section : sections) {
                try {
                    section = section.getCompound("biomes", section); // 1.18 nests the palette

                    long[] values = section.getLongArray("data", null);
                    List<String> keys = section.getListValues("palette", StringTag.class, null);

                    // Use the empty palette if there are no keys/values
                    if (keys == null || keys.isEmpty()) {
                        chunks.add(EmptyPalette.instance(4));
                        continue;
                    }

                    // Remap keys
                    List<ChunkerBiome> biomeKeys = new ArrayList<>(keys.size());
                    for (String key : keys) {
                        biomeKeys.add(resolvers.readBiome(key, dimension));
                    }

                    // If the values is null or there is only 1 key, use a single value palette
                    if (values == null || biomeKeys.size() == 1) {
                        chunks.add(new SingleValuePalette<>(4, biomeKeys.get(0)));
                        continue;
                    }

                    // Add the palette to the chunk
                    chunks.add(new ShortBasedPalette<>(
                            biomeKeys,
                            PaletteUtil.readPaletteValues(PaletteUtil.MINIMUM_BITS_PER_ENTRY_BIOMES, 4, keys.size(), values)
                    ));

                } catch (Exception e) {
                    converter.logNonFatalException(e);

                    // Add an empty palette for this section
                    chunks.add(EmptyPalette.instance(4));
                }
            }

            // Add to biomes
            column.setBiomes(new ChunkerClusterPaletteBasedBiomes(chunks));
            return;
        }

        // Attempt to use legacy biome reading in the case this is an older chunk
        super.readBiomes(column);
    }

    @Override
    protected void readBlockEntities(ChunkerColumn column) {
        ListTag<CompoundTag, Map<String, Tag<?>>> blockEntities = columnNBT.getList("block_entities", CompoundTag.class, null);
        if (blockEntities != null) {
            for (CompoundTag blockEntityTag : blockEntities) {
                try {
                    // Process the tag
                    readBlockEntity(column, blockEntityTag);
                } catch (Exception e) {
                    converter.logNonFatalException(new Exception("Failed to process BlockEntity " + blockEntityTag, e));
                }
            }
        } else {
            // Try legacy method
            super.readBlockEntities(column);
        }
    }

    @Override
    public JavaChunkReader createChunkReader(ChunkerColumn column, ChunkerChunk chunk) {
        return new ChunkReader(converter, resolvers, column, chunk);
    }
}
