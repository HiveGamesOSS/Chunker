package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.FurnaceBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Furnace Block Entities.
 */
public class JavaFurnaceBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, FurnaceBlockEntity> {
    public JavaFurnaceBlockEntityHandler() {
        super("minecraft:furnace", FurnaceBlockEntity.class, FurnaceBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull FurnaceBlockEntity value) {
        value.setBurnTime(input.getShort("BurnTime", (short) 0));
        value.setCookTime(input.getShort("CookTime", (short) 0));
        value.setCookTimeTotal(input.getShort("CookTimeTotal", (short) 0));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull FurnaceBlockEntity value) {
        output.put("BurnTime", value.getBurnTime());
        output.put("CookTime", value.getCookTime());
        output.put("CookTimeTotal", value.getCookTimeTotal());
    }
}
