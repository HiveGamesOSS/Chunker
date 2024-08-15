package com.hivemc.chunker.conversion.encoding.java.v1_16.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.util.PaletteUtil;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.array.LongArrayTag;

import java.util.List;

public class ChunkWriter extends com.hivemc.chunker.conversion.encoding.java.v1_15.writer.ChunkWriter {
    public ChunkWriter(Converter converter, JavaResolvers resolvers, Dimension dimension, ChunkerColumn chunkerColumn) {
        super(converter, resolvers, dimension, chunkerColumn);
    }

    @Override
    protected void writeBlockPaletteValues(int keyCount, short[][][] values, List<TagWithName<?>> output) {
        // Encode the values as a long then write them in 1.16 format
        long[] encodedValues = PaletteUtil.writePaletteValues1_16(PaletteUtil.MINIMUM_BITS_PER_ENTRY_BLOCKS, 16, keyCount, values);
        output.add(new TagWithName<>("BlockStates", new LongArrayTag(encodedValues)));
    }
}
