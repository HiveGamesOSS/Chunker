package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.BrewingStandBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Brewing Stands.
 */
public class BedrockBrewingStandBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BrewingStandBlockEntity> {
    public BedrockBrewingStandBlockEntityHandler() {
        super("BrewingStand", BrewingStandBlockEntity.class, BrewingStandBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BrewingStandBlockEntity value) {
        value.setFuel(input.getShort("FuelAmount", (short) 0));
        value.setFuelTotal(input.getShort("FuelTotal", (short) 0));
        value.setBrewTime(input.getShort("CookTime", (short) 0));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BrewingStandBlockEntity value) {
        output.put("FuelAmount", value.getFuel());
        output.put("FuelTotal", value.getFuelTotal());
        output.put("CookTime", value.getBrewTime());
    }
}
