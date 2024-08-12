package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.ComparatorBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Comparator Block Entities.
 */
public class JavaLegacyComparatorBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, ComparatorBlockEntity> {
    public JavaLegacyComparatorBlockEntityHandler() {
        super("Comparator", ComparatorBlockEntity.class, ComparatorBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull ComparatorBlockEntity value) {
        value.setOutputSignal(input.getInt("OutputSignal", 0));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull ComparatorBlockEntity value) {
        output.put("OutputSignal", value.getOutputSignal());
    }
}
