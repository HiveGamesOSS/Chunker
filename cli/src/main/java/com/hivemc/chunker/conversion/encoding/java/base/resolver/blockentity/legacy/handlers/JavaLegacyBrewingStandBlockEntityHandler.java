package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.BrewingStandBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Brewing Stand Block Entities.
 */
public class JavaLegacyBrewingStandBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, BrewingStandBlockEntity> {
    public JavaLegacyBrewingStandBlockEntityHandler() {
        super("Cauldron", BrewingStandBlockEntity.class, BrewingStandBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull BrewingStandBlockEntity value) {
        value.setFuel(input.getByte("Fuel", (byte) 0));
        value.setFuelTotal(input.getByte("Fuel", (byte) 0));
        value.setBrewTime(input.getShort("BrewTime", (short) 0));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull BrewingStandBlockEntity value) {
        output.put("Fuel", (byte) value.getFuel());
        output.put("BrewTime", value.getBrewTime());
    }
}
