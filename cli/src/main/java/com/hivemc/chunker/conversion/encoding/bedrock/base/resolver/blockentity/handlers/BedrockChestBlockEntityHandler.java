package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockChestBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.ChestBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.TrappedChestBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.ChestType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirectionHorizontal;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Handler for chests, converts between a co-ordinate pair and setting the block state to left/right/single.
 * This also handles turning trapped chests into their own block entity when converting to chunker.
 */
public class BedrockChestBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockChestBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<BedrockResolvers, ChestBlockEntity>, UpdateBeforeProcessBlockEntityHandler<BedrockResolvers, ChestBlockEntity> {
    public BedrockChestBlockEntityHandler() {
        super("Chest", BedrockChestBlockEntity.class, BedrockChestBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockChestBlockEntity value) {
        if (input.contains("pairx") || input.contains("pairz")) {
            value.setPairX(input.getInt("pairx", value.getX()));
            value.setPairZ(input.getInt("pairz", value.getZ()));
        }
        if (input.contains("pairlead")) {
            value.setLead(input.getByte("pairlead", (byte) 0) == (byte) 1);
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockChestBlockEntity value) {
        // Write if this chest is the main chest of a pair
        if (value.getPairX() != null && value.getPairZ() != null) {
            output.put("pairlead", value.isLead() ? (byte) 1 : (byte) 0);
        }

        // Write pairs
        if (value.getPairX() != null) {
            output.put("pairx", value.getPairX());
        }
        if (value.getPairZ() != null) {
            output.put("pairz", value.getPairZ());
        }
    }

    @Override
    public ChestBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, ChestBlockEntity blockEntity) {
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Handle ChestType field
        if (blockEntity instanceof BedrockChestBlockEntity bedrockChestBlockEntity) {
            FacingDirectionHorizontal direction = blockIdentifier.getState(VanillaBlockStates.FACING_HORIZONTAL);
            ChestType chestType;
            if (bedrockChestBlockEntity.getPairX() != null && bedrockChestBlockEntity.getPairZ() != null && direction != null) {
                int pairX = bedrockChestBlockEntity.getPairX();
                int pairZ = bedrockChestBlockEntity.getPairZ();
                FacingDirectionHorizontal left = direction.rotateClockwise();
                FacingDirectionHorizontal right = direction.rotateAntiClockwise();

                // Check the co-ordinates of the pair
                if (x + left.getX() == pairX && z + left.getZ() == pairZ) {
                    chestType = ChestType.LEFT;
                } else if (x + right.getX() == pairX && z + right.getZ() == pairZ) {
                    chestType = ChestType.RIGHT;
                } else {
                    chestType = ChestType.SINGLE; // If the pair isn't valid, this is a single chest
                }
            } else {
                // Single chest
                chestType = ChestType.SINGLE;
            }

            // Only update if the block identifier isn't air
            if (!blockIdentifier.isAir()) {
                column.setBlock(x, y, z, blockIdentifier.copyWith(VanillaBlockStates.CHEST_TYPE, chestType));
            }
        }

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
        } else if (blockEntity instanceof BedrockChestBlockEntity bedrockChestBlockEntity) {
            // Return the chunker version if it wasn't a trapped chest
            return bedrockChestBlockEntity.toChunker();
        }
        return blockEntity;
    }

    @Override
    public ChestBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, ChestBlockEntity blockEntity) {
        // Ensure the item is written as the chunker type (not the bedrock one)
        if (chunkerItemStack.getIdentifier().getItemStackType() == ChunkerVanillaBlockType.TRAPPED_CHEST) {
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
        } else if (blockEntity instanceof BedrockChestBlockEntity bedrockChestBlockEntity) {
            // Return the chunker version if it wasn't a trapped chest
            return bedrockChestBlockEntity.toChunker();
        }
        return blockEntity;
    }

    @Override
    public ChestBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, ChestBlockEntity blockEntity) {
        // Turn into Bedrock Chest Block Entity (note this also turns trapped chests, as they should be the same type for Bedrock)
        BedrockChestBlockEntity bedrockChestBlockEntity = new BedrockChestBlockEntity(blockEntity);

        // Handle ChestType field
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);
        ChestType chestType = blockIdentifier.getState(VanillaBlockStates.CHEST_TYPE);
        if (chestType != null && chestType != ChestType.SINGLE) {
            // If it's not single we need to find the pair
            FacingDirectionHorizontal direction = Objects.requireNonNull(blockIdentifier.getState(VanillaBlockStates.FACING_HORIZONTAL));
            FacingDirectionHorizontal targetDirection = chestType == ChestType.LEFT ? direction.rotateClockwise() : direction.rotateAntiClockwise();

            // Use the target direction to determine the pair
            bedrockChestBlockEntity.setPairX(x + targetDirection.getX());
            bedrockChestBlockEntity.setPairZ(z + targetDirection.getZ());

            // The left chest is counted as the lead
            bedrockChestBlockEntity.setLead(chestType == ChestType.RIGHT);
        }
        // Return the new block entity
        return bedrockChestBlockEntity;
    }

    @Override
    public ChestBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, ChestBlockEntity blockEntity) {
        return new BedrockChestBlockEntity(blockEntity);
    }

    @Override
    public Class<ChestBlockEntity> getAdditionalHandledClass() {
        return ChestBlockEntity.class;
    }
}
