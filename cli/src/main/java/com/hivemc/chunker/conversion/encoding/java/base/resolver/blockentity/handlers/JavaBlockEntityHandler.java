package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Handler for base inventories (converting items).
 */
public class JavaBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, BlockEntity> {
    public JavaBlockEntityHandler() {
        super("minecraft:block_entity", BlockEntity.class, () -> {
            throw new IllegalArgumentException("Unable to construct BlockEntity, invalid type!");
        });
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull BlockEntity blockEntity) {
        // Save original NBT
        if (resolvers.converter().shouldAllowNBTCopying()) {
            blockEntity.setOriginalNBT(input);
        }

        // Position
        blockEntity.setX(input.getInt("x", 0));
        blockEntity.setY(input.getInt("y", 0));
        blockEntity.setZ(input.getInt("z", 0));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull BlockEntity blockEntity) {
        // Restore original NBT
        if (resolvers.converter().shouldAllowNBTCopying() && blockEntity.getOriginalNBT() != null) {
            // Copy all the original tags
            for (Map.Entry<String, Tag<?>> namedPair : blockEntity.getOriginalNBT()) {
                output.put(namedPair.getKey(), namedPair.getValue().clone());
            }
        }

        // Position
        output.put("x", blockEntity.getX());
        output.put("y", blockEntity.getY());
        output.put("z", blockEntity.getZ());
    }
}
