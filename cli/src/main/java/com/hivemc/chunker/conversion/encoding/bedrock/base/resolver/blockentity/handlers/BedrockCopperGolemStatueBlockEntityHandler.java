package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockCopperGolemStatueBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.CopperGolemStatueBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.CopperGolemPose;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Copper Golem Statues which updates the block with the pose based on the NBT.
 */
public class BedrockCopperGolemStatueBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockCopperGolemStatueBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<BedrockResolvers, CopperGolemStatueBlockEntity>, UpdateBeforeProcessBlockEntityHandler<BedrockResolvers, CopperGolemStatueBlockEntity> {
    public static final BiMap<CopperGolemPose, Integer> POSE_TO_ID = ImmutableBiMap.<CopperGolemPose, Integer>builder()
            .put(CopperGolemPose.STANDING, 0)
            .put(CopperGolemPose.SITTING, 1)
            .put(CopperGolemPose.RUNNING, 2)
            .put(CopperGolemPose.STAR, 3)
            .build();

    public BedrockCopperGolemStatueBlockEntityHandler() {
        super("CopperGolemStatue", BedrockCopperGolemStatueBlockEntity.class, BedrockCopperGolemStatueBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockCopperGolemStatueBlockEntity value) {
        value.setPose(POSE_TO_ID.inverse().getOrDefault(input.getInt("Pose", 0), CopperGolemPose.STANDING));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockCopperGolemStatueBlockEntity value) {
        output.put("Pose", POSE_TO_ID.getOrDefault(value.getPose(), 0));
    }

    @Override
    public CopperGolemStatueBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, CopperGolemStatueBlockEntity blockEntity) {
        BedrockCopperGolemStatueBlockEntity bedrockCopperGolemStatueBlockEntity = new BedrockCopperGolemStatueBlockEntity(blockEntity);

        // Use the identifier of the block for color
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Grab the bed color
        CopperGolemPose pose = blockIdentifier.getState(VanillaBlockStates.COPPER_GOLEM_POSE);
        bedrockCopperGolemStatueBlockEntity.setPose(pose);

        return bedrockCopperGolemStatueBlockEntity;
    }

    @Override
    public CopperGolemStatueBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, CopperGolemStatueBlockEntity blockEntity) {
        // Turn it into the Bedrock type
        return new BedrockCopperGolemStatueBlockEntity(blockEntity);
    }

    @Override
    public Class<CopperGolemStatueBlockEntity> getAdditionalHandledClass() {
        return CopperGolemStatueBlockEntity.class;
    }

    @Override
    public CopperGolemStatueBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, CopperGolemStatueBlockEntity blockEntity) {
        if (blockEntity instanceof BedrockCopperGolemStatueBlockEntity bedrockCopperGolemStatueBlockEntity) {
            ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

            // Set the new block type (if the statue is present)
            if (!blockIdentifier.isAir()) {
                column.setBlock(x, y, z, blockIdentifier.copyWith(VanillaBlockStates.COPPER_GOLEM_POSE, bedrockCopperGolemStatueBlockEntity.getPose()));
            }

            // Return the chunker version
            return bedrockCopperGolemStatueBlockEntity.toChunker();
        }
        return blockEntity;
    }

    @Override
    public CopperGolemStatueBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, CopperGolemStatueBlockEntity blockEntity) {
        // Ensure the item is written as the chunker type (not the bedrock one)
        if (blockEntity instanceof BedrockCopperGolemStatueBlockEntity bedrockCopperGolemStatueBlockEntity) {
            return bedrockCopperGolemStatueBlockEntity.toChunker();
        }
        return blockEntity;
    }
}
