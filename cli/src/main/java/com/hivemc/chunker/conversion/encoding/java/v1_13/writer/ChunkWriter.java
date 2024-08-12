package com.hivemc.chunker.conversion.encoding.java.v1_13.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.util.PaletteUtil;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
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

import java.util.List;
import java.util.Map;

public class ChunkWriter extends com.hivemc.chunker.conversion.encoding.java.v1_11.writer.ChunkWriter {
    public ChunkWriter(Converter converter, JavaResolvers resolvers, Dimension dimension, ChunkerColumn chunkerColumn) {
        super(converter, resolvers, dimension, chunkerColumn);
    }

    @Override
    protected void writeBlockPalette(ChunkerChunk chunk, List<TagWithName<?>> output) {
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
        output.add(new TagWithName<>("Palette", keys));

        // Only fetch the values if it's a short based, null should be used when there are no values to write
        // e.g. empty palette or palette with the same value
        short[][][] values = null;
        if (palette instanceof ShortBasedPalette<ChunkerBlockIdentifier> shortBasedPalette) {
            values = shortBasedPalette.getValues();
        }
        writeBlockPaletteValues(keyCount, values, output);
    }

    protected void writeBlockPaletteValues(int keyCount, short[][][] values, List<TagWithName<?>> output) {
        // Encode the values as a long then write them
        long[] encodedValues = PaletteUtil.writePaletteValues1_13(PaletteUtil.MINIMUM_BITS_PER_ENTRY_BLOCKS, 16, keyCount, values);
        output.add(new TagWithName<>("BlockStates", new LongArrayTag(encodedValues)));
    }
}
