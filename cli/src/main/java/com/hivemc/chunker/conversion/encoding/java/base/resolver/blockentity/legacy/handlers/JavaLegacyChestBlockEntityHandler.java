package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.ChestBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.TrappedChestBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for chests which turns trapped chest into a different block entity.
 */
public class JavaLegacyChestBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, ChestBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<JavaResolvers, ChestBlockEntity>, UpdateBeforeProcessBlockEntityHandler<JavaResolvers, ChestBlockEntity> {
    public JavaLegacyChestBlockEntityHandler() {
        super("Chest", ChestBlockEntity.class, ChestBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull ChestBlockEntity value) {
        // Doesn't read anything extra
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull ChestBlockEntity value) {
        // Doesn't write anything extra
    }

    @Override
    public ChestBlockEntity updateBeforeProcess(@NotNull JavaResolvers resolvers, ChunkerColumn column, int x, int y, int z, ChestBlockEntity blockEntity) {
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Handle trapped chest
        if (blockIdentifier.getType() == ChunkerVanillaBlockType.TRAPPED_CHEST) {
            // Turn into a trapped chest
            TrappedChestBlockEntity trappedChestBlockEntity = new TrappedChestBlockEntity();
            trappedChestBlockEntity.setX(blockEntity.getX());
            trappedChestBlockEntity.setY(blockEntity.getY());
            trappedChestBlockEntity.setZ(blockEntity.getZ());
            trappedChestBlockEntity.setMovable(blockEntity.isMovable());
            trappedChestBlockEntity.setLootTable(blockEntity.getLootTable());
            trappedChestBlockEntity.setCustomName(blockEntity.getCustomName());
            trappedChestBlockEntity.getItems().putAll(blockEntity.getItems());
            return trappedChestBlockEntity;
        }
        return blockEntity;
    }

    @Override
    public ChestBlockEntity updateBeforeWrite(@NotNull JavaResolvers resolvers, ChunkerColumn column, int x, int y, int z, ChestBlockEntity blockEntity) {
        // Turn into a chest
        if (blockEntity instanceof TrappedChestBlockEntity) {
            ChestBlockEntity chestBlockEntity = new ChestBlockEntity();
            chestBlockEntity.setX(blockEntity.getX());
            chestBlockEntity.setY(blockEntity.getY());
            chestBlockEntity.setZ(blockEntity.getZ());
            chestBlockEntity.setMovable(blockEntity.isMovable());
            chestBlockEntity.setLootTable(blockEntity.getLootTable());
            chestBlockEntity.setCustomName(blockEntity.getCustomName());
            chestBlockEntity.getItems().putAll(blockEntity.getItems());
            return chestBlockEntity;
        }

        // Return the old block entity
        return blockEntity;
    }

    @Override
    public Class<ChestBlockEntity> getAdditionalHandledClass() {
        return ChestBlockEntity.class;
    }
}
