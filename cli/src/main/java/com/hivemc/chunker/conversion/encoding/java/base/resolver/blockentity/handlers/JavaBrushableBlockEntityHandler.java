package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BrushableBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Brushable Block Entities (Suspicious Sand / Gravel).
 */
public class JavaBrushableBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, BrushableBlockEntity> {
    public JavaBrushableBlockEntityHandler() {
        super("minecraft:brushable_block", BrushableBlockEntity.class, BrushableBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull BrushableBlockEntity value) {
        value.setBrushDirection(input.getByte("hit_direction", (byte) 6));

        CompoundTag item = input.getCompound("item");
        if (item != null) {
            value.setItem(resolvers.readItem(item));
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull BrushableBlockEntity value) {
        // 6 is special value which means not defined
        if (value.getBrushDirection() != 6) {
            output.put("hit_direction", value.getBrushDirection());
        }
        if (value.getItem() != null && !value.getItem().getIdentifier().isAir()) {
            resolvers.writeItem(value.getItem()).ifPresent(item -> output.put("item", item));
        }
    }
}
