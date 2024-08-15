package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.FurnaceBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for furnaces.
 */
public class BedrockFurnaceBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, FurnaceBlockEntity> {
    public BedrockFurnaceBlockEntityHandler() {
        super("Furnace", FurnaceBlockEntity.class, FurnaceBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull FurnaceBlockEntity value) {
        value.setBurnTime(input.getShort("BurnTime", (short) 0));
        value.setCookTime(input.getShort("CookTime", (short) 0));
        value.setCookTimeTotal(input.getShort("BurnDuration", (short) 0));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull FurnaceBlockEntity value) {
        output.put("BurnTime", value.getBurnTime());
        output.put("CookTime", value.getCookTime());
        output.put("BurnDuration", value.getCookTimeTotal());
    }
}
