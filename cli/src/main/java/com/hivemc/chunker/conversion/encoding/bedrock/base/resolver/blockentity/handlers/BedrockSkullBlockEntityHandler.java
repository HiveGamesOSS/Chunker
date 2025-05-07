package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockSkullBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.SkullBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Rotation;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Handler for skulls which updates the block with the rotation, type and if it's powered based on NBT.
 */
public class BedrockSkullBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockSkullBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<BedrockResolvers, SkullBlockEntity>, UpdateBeforeProcessBlockEntityHandler<BedrockResolvers, SkullBlockEntity> {
    public static final BiMap<ChunkerBlockType, Pair<Byte, Boolean>> SKULL_TO_ID = ImmutableBiMap.<ChunkerBlockType, Pair<Byte, Boolean>>builder()
            .put(ChunkerVanillaBlockType.SKELETON_SKULL, Pair.of((byte) 0, false))
            .put(ChunkerVanillaBlockType.SKELETON_WALL_SKULL, Pair.of((byte) 0, true))
            .put(ChunkerVanillaBlockType.WITHER_SKELETON_SKULL, Pair.of((byte) 1, false))
            .put(ChunkerVanillaBlockType.WITHER_SKELETON_WALL_SKULL, Pair.of((byte) 1, true))
            .put(ChunkerVanillaBlockType.ZOMBIE_HEAD, Pair.of((byte) 2, false))
            .put(ChunkerVanillaBlockType.ZOMBIE_WALL_HEAD, Pair.of((byte) 2, true))
            .put(ChunkerVanillaBlockType.PLAYER_HEAD, Pair.of((byte) 3, false))
            .put(ChunkerVanillaBlockType.PLAYER_WALL_HEAD, Pair.of((byte) 3, true))
            .put(ChunkerVanillaBlockType.CREEPER_HEAD, Pair.of((byte) 4, false))
            .put(ChunkerVanillaBlockType.CREEPER_WALL_HEAD, Pair.of((byte) 4, true))
            .put(ChunkerVanillaBlockType.DRAGON_HEAD, Pair.of((byte) 5, false))
            .put(ChunkerVanillaBlockType.DRAGON_WALL_HEAD, Pair.of((byte) 5, true))
            .put(ChunkerVanillaBlockType.PIGLIN_HEAD, Pair.of((byte) 6, false))
            .put(ChunkerVanillaBlockType.PIGLIN_WALL_HEAD, Pair.of((byte) 6, true))
            .build();

    public BedrockSkullBlockEntityHandler() {
        super("Skull", BedrockSkullBlockEntity.class, BedrockSkullBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockSkullBlockEntity value) {
        value.setSkullType(input.getByte("SkullType", (byte) 0));
        if (input.contains("Rotation")) {
            // Convert from degrees to a byte
            value.setRotation((byte) Math.round(((input.getFloat("Rotation", (byte) 0) + 360) % 360) / 22.5F));
        } else {
            value.setRotation(input.getByte("Rot", (byte) 0));
        }
        value.setMovingMouth(input.getByte("DoingAnimation", (byte) 0) == (byte) 1);
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockSkullBlockEntity value) {
        output.put("SkullType", value.getSkullType());
        output.put("Rotation", value.getRotation() * 22.5F);
        output.put("DoingAnimation", value.isMovingMouth() ? (byte) 1 : (byte) 0);
    }

    @Override
    public SkullBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, SkullBlockEntity blockEntity) {
        BedrockSkullBlockEntity bedrockSkullBlockEntity = new BedrockSkullBlockEntity(blockEntity);
        // Use the identifier of the block for SkullType / Rotation
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Grab the skull type
        Pair<Byte, Boolean> result = SKULL_TO_ID.get(blockIdentifier.getType());
        bedrockSkullBlockEntity.setSkullType(result != null ? result.key() : 0);

        // Grab the rotation (if it's a non-wall skull)
        Rotation rotation = blockIdentifier.getState(VanillaBlockStates.ROTATION);
        if (rotation != null) {
            bedrockSkullBlockEntity.setRotation((byte) rotation.ordinal());
        }

        // Grab the power
        Bool powered = blockIdentifier.getState(VanillaBlockStates.POWERED);
        if (powered != null) {
            bedrockSkullBlockEntity.setMovingMouth(powered == Bool.TRUE);
        }

        return bedrockSkullBlockEntity;
    }

    @Override
    public SkullBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, SkullBlockEntity blockEntity) {
        return new BedrockSkullBlockEntity(blockEntity);
    }

    @Override
    public Class<SkullBlockEntity> getAdditionalHandledClass() {
        return SkullBlockEntity.class;
    }

    @Override
    public SkullBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, SkullBlockEntity blockEntity) {
        if (blockEntity instanceof BedrockSkullBlockEntity bedrockSkullBlockEntity) {
            ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

            // Don't update anything if the block is air
            if (blockIdentifier.isAir()) return blockEntity;

            // Grab the skull type
            boolean wallSkull = blockIdentifier.getType().getStates().contains(VanillaBlockStates.FACING_HORIZONTAL);
            ChunkerBlockType newType = SKULL_TO_ID.inverse().get(Pair.of(bedrockSkullBlockEntity.getSkullType(), wallSkull));
            if (newType == null) {
                newType = blockIdentifier.getType();
            }

            Map<BlockState<?>, BlockStateValue> newBlockStates = new Object2ObjectOpenHashMap<>(blockIdentifier.getPresentStates());
            if (!wallSkull) {
                // Apply rotation
                Rotation[] constants = Rotation.class.getEnumConstants();
                int normalizedRotation = (((bedrockSkullBlockEntity.getRotation() % constants.length) + constants.length) % constants.length);
                newBlockStates.put(VanillaBlockStates.ROTATION, constants[normalizedRotation]);
            }

            // Apply powered
            newBlockStates.put(VanillaBlockStates.POWERED, bedrockSkullBlockEntity.isMovingMouth() ? Bool.TRUE : Bool.FALSE);

            // Set the new block type
            column.setBlock(x, y, z, new ChunkerBlockIdentifier(newType, newBlockStates, blockIdentifier.getPreservedIdentifier()));

            // Return the chunker version
            return bedrockSkullBlockEntity.toChunker();
        }
        return blockEntity;
    }

    @Override
    public SkullBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, SkullBlockEntity blockEntity) {
        // Ensure the item is written as the chunker type (not the bedrock one)
        if (blockEntity instanceof BedrockSkullBlockEntity bedrockSkullBlockEntity) {
            return bedrockSkullBlockEntity.toChunker();
        }
        return blockEntity;
    }
}
