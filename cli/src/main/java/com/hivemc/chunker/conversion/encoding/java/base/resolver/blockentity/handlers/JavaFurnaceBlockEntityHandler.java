package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers.JavaLegacyFurnaceBlockEntityHandler;
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
        if (input.contains("lit_time_remaining")) {
            value.setBurnTime(input.getShort("lit_time_remaining", (short) 0));
        } else {
            value.setCookTime(JavaLegacyFurnaceBlockEntityHandler.getShortOrInt(input, "BurnTime", (short) 0));
        }
        if (input.contains("cooking_time_spent")) {
            value.setCookTime(input.getShort("cooking_time_spent", (short) 0));
        } else {
            value.setCookTime(JavaLegacyFurnaceBlockEntityHandler.getShortOrInt(input, "CookTime", (short) 0));
        }
        if (input.contains("lit_total_time")) {
            value.setCookTimeTotal(input.getShort("lit_total_time", (short) 0));
        } else {
            value.setCookTimeTotal(JavaLegacyFurnaceBlockEntityHandler.getShortOrInt(input, "CookTimeTotal", (short) 0));
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull FurnaceBlockEntity value) {
        // In 1.21.4 the fields for the furnace burn times got renamed
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 21, 4)) {
            output.put("lit_time_remaining", value.getBurnTime());
            output.put("cooking_time_spent", value.getCookTime());
            output.put("lit_total_time", value.getCookTimeTotal());
        } else {
            output.put("BurnTime", value.getBurnTime());
            output.put("CookTime", value.getCookTime());
            output.put("CookTimeTotal", value.getCookTimeTotal());
        }
    }
}
