package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.ComparatorBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for comparators.
 */
public class BedrockComparatorBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, ComparatorBlockEntity> {
    public BedrockComparatorBlockEntityHandler() {
        super("Comparator", ComparatorBlockEntity.class, ComparatorBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull ComparatorBlockEntity value) {
        value.setOutputSignal(input.getInt("OutputSignal", 0));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull ComparatorBlockEntity value) {
        output.put("OutputSignal", value.getOutputSignal());
    }
}
