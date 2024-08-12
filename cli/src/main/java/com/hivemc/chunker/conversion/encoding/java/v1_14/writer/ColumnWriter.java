package com.hivemc.chunker.conversion.encoding.java.v1_14.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaChunkWriter;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

public class ColumnWriter extends com.hivemc.chunker.conversion.encoding.java.v1_13.writer.ColumnWriter {
    public ColumnWriter(JavaWorldWriter parent, Converter converter, JavaResolvers resolvers, Dimension dimension) {
        super(parent, converter, resolvers, dimension);
    }

    @Override
    public JavaChunkWriter createChunkWriter(ChunkerColumn column) {
        return new ChunkWriter(converter, resolvers, dimension, column);
    }

    @Override
    protected void writeColumnStatus(ChunkerColumn column, CompoundTag columnNBT) {
        columnNBT.put("Status", "full");
    }
}
