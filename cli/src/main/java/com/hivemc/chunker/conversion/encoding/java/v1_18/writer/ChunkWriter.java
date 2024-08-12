package com.hivemc.chunker.conversion.encoding.java.v1_18.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.util.PaletteUtil;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.ShortBasedPalette;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.array.LongArrayTag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChunkWriter extends com.hivemc.chunker.conversion.encoding.java.v1_17.writer.ChunkWriter {
    public ChunkWriter(Converter converter, JavaResolvers resolvers, Dimension dimension, ChunkerColumn chunkerColumn) {
        super(converter, resolvers, dimension, chunkerColumn);
    }

    @Override
    protected List<Task<Void>> createChunkTasks(ChunkerChunk chunkerChunk, List<TagWithName<?>> outputs) {
        List<Task<Void>> tasks = super.createChunkTasks(chunkerChunk, outputs);
        tasks.add(Task.async("Writing Biomes", TaskWeight.MEDIUM, () -> writeBiomes(chunkerChunk, outputs)));
        return tasks;
    }

    @Override
    protected void writeBlockPalette(ChunkerChunk chunk, List<TagWithName<?>> output) {
        List<TagWithName<?>> temp = new ArrayList<>();
        Palette<ChunkerBlockIdentifier> palette = chunk.getPalette();

        // Create the keys and convert from Chunker to Java NBT
        int keyCount = 1;
        ListTag<CompoundTag, Map<String, Tag<?>>> keys = new ListTag<>(TagType.COMPOUND);
        if (!palette.isEmpty()) {
            keyCount = palette.getKeyCount();
            for (int i = 0; i < keyCount; i++) {
                ChunkerBlockIdentifier blockIdentifier = palette.getKey(i, ChunkerBlockIdentifier.AIR);
                keys.add(resolvers.writeBlock(blockIdentifier));
            }
        } else {
            // Write air for empty chunks
            keys.add(resolvers.writeBlock(ChunkerBlockIdentifier.AIR));
        }
        temp.add(new TagWithName<>("palette", keys));

        // Only fetch the values if it's a short based, null should be used when there are no values to write
        // e.g. empty palette or palette with the same value
        short[][][] values = null;
        if (palette instanceof ShortBasedPalette<ChunkerBlockIdentifier> shortBasedPalette) {
            values = shortBasedPalette.getValues();
        }
        writeBlockPaletteValues(keyCount, values, temp);

        // Combine temp list values to nest the key
        CompoundTag blockStates = new CompoundTag();
        for (TagWithName<?> input : temp) {
            if (input == null) continue;
            blockStates.put(input.name(), input.tag());
        }
        output.add(new TagWithName<>("block_states", blockStates));
    }

    @Override
    protected void writeBlockPaletteValues(int keyCount, short[][][] values, List<TagWithName<?>> output) {
        // Encode the values as a long then write them in 1.16 format
        long[] encodedValues = PaletteUtil.writePaletteValues1_16(PaletteUtil.MINIMUM_BITS_PER_ENTRY_BLOCKS, 16, keyCount, values);
        output.add(new TagWithName<>("data", new LongArrayTag(encodedValues)));
    }

    protected void writeBiomes(ChunkerChunk chunkerChunk, List<TagWithName<?>> outputs) {
        if (chunkerColumn.getBiomes() == null) return; // Don't write biomes if they're not present

        // Add biome data to each section (offset by 4 as it's 0 based)
        boolean cavesAndCliffs = converter.level().map(level -> level.getSettings().CavesAndCliffs).orElse(false);
        Palette<ChunkerBiome> biomePalette = chunkerColumn.getBiomes().as4X4Palette(chunkerChunk.getY() + (cavesAndCliffs ? 4 : 0));

        // Generate the palette to write
        CompoundTag biomes = new CompoundTag();

        // Create the keys and convert from Chunker to Java NBT
        int keyCount = 1;
        ListTag<StringTag, String> keys = new ListTag<>(TagType.STRING);
        if (biomePalette != null && !biomePalette.isEmpty()) {
            keyCount = biomePalette.getKeyCount();
            for (int i = 0; i < keyCount; i++) {
                ChunkerBiome chunkerBiome = biomePalette.getKey(i, null);
                if (chunkerBiome == null) {
                    chunkerBiome = resolvers.getFallbackBiome(dimension);
                }
                keys.add(new StringTag(resolvers.writeBiome(chunkerBiome, dimension)));
            }
        } else {
            // Write air for empty chunks
            keys.add(new StringTag(resolvers.writeBiome(resolvers.getFallbackBiome(dimension), dimension)));
        }
        biomes.put("palette", keys);

        // Write the values if it has more than 1 key
        if (keyCount > 1) {
            short[][][] values = null;
            if (biomePalette instanceof ShortBasedPalette<ChunkerBiome> shortBasedPalette) {
                values = shortBasedPalette.getValues();
            }
            // Encode the values as a long then write them in 1.16 format
            long[] encodedValues = PaletteUtil.writePaletteValues1_16(PaletteUtil.MINIMUM_BITS_PER_ENTRY_BIOMES, 4, keyCount, values);
            biomes.put("data", new LongArrayTag(encodedValues));
        }

        // Add the nbt
        outputs.add(new TagWithName<>("biomes", biomes));
    }
}
