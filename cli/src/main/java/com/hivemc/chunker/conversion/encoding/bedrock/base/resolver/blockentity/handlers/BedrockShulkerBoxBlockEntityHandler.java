package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockShulkerBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.ShulkerBoxBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirection;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for shulker boxes which updates the block type with the facing direction from NBT.
 */
public class BedrockShulkerBoxBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockShulkerBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<BedrockResolvers, ShulkerBoxBlockEntity>, UpdateBeforeProcessBlockEntityHandler<BedrockResolvers, ShulkerBoxBlockEntity> {
    public static final BiMap<FacingDirection, Byte> FACING_DIRECTION_TO_BYTE = ImmutableBiMap.<FacingDirection, Byte>builder()
            .put(FacingDirection.NORTH, (byte) 2)
            .put(FacingDirection.EAST, (byte) 5)
            .put(FacingDirection.SOUTH, (byte) 3)
            .put(FacingDirection.WEST, (byte) 4)
            .put(FacingDirection.UP, (byte) 1)
            .put(FacingDirection.DOWN, (byte) 0)
            .build();

    public BedrockShulkerBoxBlockEntityHandler() {
        super("ShulkerBox", BedrockShulkerBlockEntity.class, BedrockShulkerBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockShulkerBlockEntity value) {
        byte facing = input.getByte("facing", (byte) 0);
        FacingDirection facingDirection = FACING_DIRECTION_TO_BYTE.inverse().getOrDefault(facing, FacingDirection.NORTH);
        value.setFacing(facingDirection);
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockShulkerBlockEntity value) {
        byte facing = FACING_DIRECTION_TO_BYTE.getOrDefault(value.getFacing(), (byte) 0);
        output.put("facing", facing);
    }

    @Override
    public ShulkerBoxBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, ShulkerBoxBlockEntity blockEntity) {
        BedrockShulkerBlockEntity bedrockShulkerBlockEntity = new BedrockShulkerBlockEntity(blockEntity);

        // Use the identifier of the block for rotation
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Grab the direction
        FacingDirection facingDirection = blockIdentifier.getState(VanillaBlockStates.FACING_ALL);
        if (facingDirection != null) {
            bedrockShulkerBlockEntity.setFacing(facingDirection);
        }

        return bedrockShulkerBlockEntity;
    }

    @Override
    public ShulkerBoxBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, ShulkerBoxBlockEntity blockEntity) {
        return new BedrockShulkerBlockEntity(blockEntity);
    }

    @Override
    public Class<ShulkerBoxBlockEntity> getAdditionalHandledClass() {
        return ShulkerBoxBlockEntity.class;
    }

    @Override
    public ShulkerBoxBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, ShulkerBoxBlockEntity blockEntity) {
        if (blockEntity instanceof BedrockShulkerBlockEntity bedrockShulkerBlockEntity) {
            ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

            // Don't update anything if the block is air
            if (blockIdentifier.isAir()) return blockEntity;

            // Apply facing direction
            blockIdentifier = blockIdentifier.copyWith(VanillaBlockStates.FACING_ALL, bedrockShulkerBlockEntity.getFacing());

            // Set the new block type
            column.setBlock(x, y, z, blockIdentifier);

            // Return the chunker version
            return bedrockShulkerBlockEntity.toChunker();
        }
        return blockEntity;
    }

    @Override
    public ShulkerBoxBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, ShulkerBoxBlockEntity blockEntity) {
        // Ensure the item is written as the chunker type (not the bedrock one)
        if (blockEntity instanceof BedrockShulkerBlockEntity bedrockShulkerBlockEntity) {
            return bedrockShulkerBlockEntity.toChunker();
        }
        return blockEntity;
    }
}
