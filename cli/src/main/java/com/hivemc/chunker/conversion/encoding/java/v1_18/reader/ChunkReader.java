package com.hivemc.chunker.conversion.encoding.java.v1_18.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.util.PaletteUtil;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.EmptyPalette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.ShortBasedPalette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.SingleValuePalette;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChunkReader extends com.hivemc.chunker.conversion.encoding.java.v1_17.reader.ChunkReader {
    public ChunkReader(Converter converter, JavaResolvers resolvers, ChunkerColumn column, ChunkerChunk chunk) {
        super(converter, resolvers, column, chunk);
    }

    @Override
    public void readPalette(CompoundTag nbt) {
        // 1.18+ wraps the values in block_states
        nbt = nbt.getCompound("block_states", nbt);

        // Get the keys (palette/ShortBasedPalette, can be lowercase P depending on version)
        ListTag<CompoundTag, Map<String, Tag<?>>> keysList = nbt.getList("palette", CompoundTag.class, null);

        // Get the values (data/BlockStates, depending on version)
        long[] encodedValues = nbt.getLongArray("data", null);

        // If the keys aren't named as palette then it's likely an older chunk (if the data tag is missing it's likely a single value palette)
        if (keysList == null) {
            super.readPalette(nbt);
            return;
        }

        // Create the keys
        List<ChunkerBlockIdentifier> keys = new ArrayList<>(keysList.size());
        for (CompoundTag key : keysList) {
            keys.add(resolvers.readBlock(key));
        }

        // Special conditions to skip the full reading of the palette
        if (keys.isEmpty()) {
            chunk.setPalette(EmptyPalette.chunk());
            return;
        } else if (keys.size() == 1 || encodedValues == null) {
            chunk.setPalette(SingleValuePalette.chunk(keys.iterator().next()));
            return;
        }

        // Decode the values
        short[][][] values = PaletteUtil.readPaletteValues(PaletteUtil.MINIMUM_BITS_PER_ENTRY_BLOCKS, 16, keys.size(), encodedValues);

        // Create the palette and assign it
        chunk.setPalette(new ShortBasedPalette<>(keys, values));
    }
}
