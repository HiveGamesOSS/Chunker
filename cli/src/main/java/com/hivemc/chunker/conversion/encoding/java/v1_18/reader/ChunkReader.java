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
import com.hivemc.chunker.nbt.tags.primitive.StringTag;

import java.util.ArrayList;
import java.util.List;

public class ChunkReader extends com.hivemc.chunker.conversion.encoding.java.v1_17.reader.ChunkReader {
    public ChunkReader(Converter converter, JavaResolvers resolvers, ChunkerColumn column, ChunkerChunk chunk) {
        super(converter, resolvers, column, chunk);
    }

    @Override
    public void readPalette(CompoundTag nbt) {
        // 1.18+ wraps the values in block_states
        nbt = nbt.getCompound("block_states", nbt);

        // Older chunks name the keys Palette instead, which the previous version reads
        if (!(nbt.get("palette") instanceof ListTag<?, ?> keysList)) {
            super.readPalette(nbt);
            return;
        }

        // Get the values (data/BlockStates, depending on version)
        long[] encodedValues = nbt.getLongArray("data", null);

        // Create the keys
        List<ChunkerBlockIdentifier> keys = new ArrayList<>(keysList.size());
        for (Tag<?> key : keysList) {
            keys.add(resolvers.readBlock(convertKeyToCompoundTag(key)));
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

    /**
     * Turn a palette entry into the compound form used by the block resolver.
     *
     * @param key the palette entry.
     * @return the compound holding the block state.
     */
    protected CompoundTag convertKeyToCompoundTag(Tag<?> key) {
        // Mixed lists wrap each entry in a compound which uses an empty name
        if (key instanceof CompoundTag compoundTag && compoundTag.size() == 1) {
            Tag<?> unwrapped = compoundTag.get("");
            if (unwrapped != null) {
                key = unwrapped;
            }
        }

        // Block states which have properties use a compound
        if (key instanceof CompoundTag compoundTag) return compoundTag;

        // 26.3+ writes the default block state as just the identifier
        if (key instanceof StringTag stringTag) {
            String identifier = stringTag.getValue();
            if (identifier == null) throw new IllegalArgumentException("Palette key is missing an identifier");

            CompoundTag blockState = new CompoundTag(1);
            blockState.put("id", identifier);
            return blockState;
        }

        throw new IllegalArgumentException("Unsupported palette key type: " + key.getClass().getName());
    }
}
