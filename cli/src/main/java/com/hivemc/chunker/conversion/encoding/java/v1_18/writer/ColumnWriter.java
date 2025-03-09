package com.hivemc.chunker.conversion.encoding.java.v1_18.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaChunkWriter;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaWorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;

import java.util.Map;
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
    protected void postProcessColumn(ChunkerColumn column, CompoundTag columnNBT) {
        // Write the lowest section Y as the yPos
        // This is never used by the client, but it used by other tools
        ListTag<CompoundTag, Map<String, Tag<?>>> sections = columnNBT.getList(
                "sections",
                CompoundTag.class,
                null
        );
        if (sections != null && sections.size() > 0) {
            columnNBT.put("yPos", (int) sections.get(0).getByte("Y", (byte) 0));
        } else {
            columnNBT.put("yPos", 0);
        }
    }

    @Override
    public JavaChunkWriter createChunkWriter(ChunkerColumn column) {
        return new ChunkWriter(converter, resolvers, dimension, column);
    }
}
