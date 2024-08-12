package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.CrafterBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for crafter block entity.
 */
public class BedrockCrafterBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, CrafterBlockEntity> {
    public BedrockCrafterBlockEntityHandler() {
        super("Crafter", CrafterBlockEntity.class, CrafterBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull CrafterBlockEntity value) {
        value.setDisabledSlots(input.getShort("disabled_slots", (short) 0));
        value.setCraftingTicksRemaining(input.getInt("crafting_ticks_remaining", 0));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull CrafterBlockEntity value) {
        output.put("disabled_slots", value.getDisabledSlots());
        output.put("crafting_ticks_remaining", value.getCraftingTicksRemaining());
    }
}
