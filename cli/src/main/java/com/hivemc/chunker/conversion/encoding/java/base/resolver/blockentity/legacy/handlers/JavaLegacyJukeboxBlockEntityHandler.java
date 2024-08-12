package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.JukeboxBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Jukebox Block Entities.
 */
public class JavaLegacyJukeboxBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, JukeboxBlockEntity> {
    public JavaLegacyJukeboxBlockEntityHandler() {
        super("RecordPlayer", JukeboxBlockEntity.class, JukeboxBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull JukeboxBlockEntity value) {
        CompoundTag recordItem = input.getCompound("RecordItem");
        if (recordItem != null) {
            value.setRecord(resolvers.readItem(recordItem));
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull JukeboxBlockEntity value) {
        if (value.getRecord() != null && !value.getRecord().getIdentifier().isAir()) {
            resolvers.writeItem(value.getRecord()).ifPresent(record -> output.put("RecordItem", record));
        }
    }
}
