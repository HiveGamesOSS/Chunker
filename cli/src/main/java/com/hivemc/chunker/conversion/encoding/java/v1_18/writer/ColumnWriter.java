package com.hivemc.chunker.conversion.encoding.java.v1_18.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaChunkWriter;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.util.Objects;

public class ColumnWriter extends com.hivemc.chunker.conversion.encoding.java.v1_17.writer.ColumnWriter {
    public ColumnWriter(JavaWorldWriter parent, Converter converter, JavaResolvers resolvers, Dimension dimension) {
        super(parent, converter, resolvers, dimension);
    }

    @Override
    protected CompoundTag nestColumnNBT(CompoundTag columnNBT) {
        return columnNBT; // No nesting needed on 1.18
    }

    @Override
    protected TagWithName<?> writeChunks(ChunkerColumn column) {
        TagWithName<?> tag = super.writeChunks(column);

        // Use Sections with lowercase s
        return tag == null ? null : new TagWithName<>("sections", tag.tag());
    }

    @Override
    protected TagWithName<?> writeBlockEntities(ChunkerColumn column) {
        // Renamed to block_entities
        return new TagWithName<>("block_entities", Objects.requireNonNull(super.writeBlockEntities(column)).tag());
    }

    @Override
    protected TagWithName<?> writeBiomes(ChunkerColumn column) {
        // Not written in the column for 1.18, written in the chunk
        return null;
    }

    @Override
    public JavaChunkWriter createChunkWriter(ChunkerColumn column) {
        return new ChunkWriter(converter, resolvers, dimension, column);
    }
}
