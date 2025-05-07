package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers.BedrockSkullBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.type.JavaLegacySkullBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.SkullBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
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
public class JavaLegacySkullBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, JavaLegacySkullBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<JavaResolvers, SkullBlockEntity>, UpdateBeforeProcessBlockEntityHandler<JavaResolvers, SkullBlockEntity> {
    public JavaLegacySkullBlockEntityHandler() {
        super("Skull", JavaLegacySkullBlockEntity.class, JavaLegacySkullBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull JavaLegacySkullBlockEntity value) {
        value.setSkullType(input.getByte("SkullType", (byte) 0));
        value.setRotation(input.getByte("Rot", (byte) 0));

        // Extra type is skull name
        if (input.contains("ExtraType")) {
            value.setOwnerName(input.getString("ExtraType", null));
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull JavaLegacySkullBlockEntity value) {
        output.put("SkullType", value.getSkullType());
        output.put("Rot", value.getRotation());

        // Add name tag if present
        if (value.getOwnerName() != null) {
            output.put("ExtraType", value.getOwnerName());
        }
    }

    @Override
    public SkullBlockEntity updateBeforeWrite(@NotNull JavaResolvers resolvers, ChunkerColumn column, int x, int y, int z, SkullBlockEntity blockEntity) {
        JavaLegacySkullBlockEntity legacySkullBlockEntity = new JavaLegacySkullBlockEntity(blockEntity);
        // Use the identifier of the block for SkullType / Rotation
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Grab the skull type
        Pair<Byte, Boolean> result = BedrockSkullBlockEntityHandler.SKULL_TO_ID.get(blockIdentifier.getType());
        legacySkullBlockEntity.setSkullType(result != null ? result.key() : 0);

        // Grab the rotation (if it's a non-wall skull)
        Rotation rotation = blockIdentifier.getState(VanillaBlockStates.ROTATION);
        if (rotation != null) {
            legacySkullBlockEntity.setRotation((byte) rotation.ordinal());
        }

        return legacySkullBlockEntity;
    }

    @Override
    public SkullBlockEntity updateBeforeWrite(@NotNull JavaResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, SkullBlockEntity blockEntity) {
        return new JavaLegacySkullBlockEntity(blockEntity);
    }

    @Override
    public Class<SkullBlockEntity> getAdditionalHandledClass() {
        return SkullBlockEntity.class;
    }

    @Override
    public SkullBlockEntity updateBeforeProcess(@NotNull JavaResolvers resolvers, ChunkerColumn column, int x, int y, int z, SkullBlockEntity blockEntity) {
        if (blockEntity instanceof JavaLegacySkullBlockEntity legacySkullBlockEntity) {
            ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

            // Don't update anything if the block is air
            if (blockIdentifier.isAir()) return blockEntity;

            // Grab the skull type
            boolean wallSkull = blockIdentifier.getType().getStates().contains(VanillaBlockStates.FACING_HORIZONTAL);
            ChunkerBlockType newType = BedrockSkullBlockEntityHandler.SKULL_TO_ID.inverse().get(Pair.of(legacySkullBlockEntity.getSkullType(), wallSkull));
            if (newType == null) {
                newType = blockIdentifier.getType();
            }

            Map<BlockState<?>, BlockStateValue> newBlockStates = new Object2ObjectOpenHashMap<>(blockIdentifier.getPresentStates());
            if (!wallSkull) {
                // Apply rotation
                Rotation[] constants = Rotation.class.getEnumConstants();
                int normalizedRotation = (((legacySkullBlockEntity.getRotation() % constants.length) + constants.length) % constants.length);
                newBlockStates.put(VanillaBlockStates.ROTATION, constants[normalizedRotation]);
            }

            // Set the new block type
            column.setBlock(x, y, z, new ChunkerBlockIdentifier(newType, newBlockStates, blockIdentifier.getPreservedIdentifier()));

            // Return the chunker version
            return legacySkullBlockEntity.toChunker();
        }
        return blockEntity;
    }

    @Override
    public SkullBlockEntity updateBeforeProcess(@NotNull JavaResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, SkullBlockEntity blockEntity) {
        // Ensure the item is written as the chunker type (not the legacy java one)
        if (blockEntity instanceof JavaLegacySkullBlockEntity legacySkullBlockEntity) {
            return legacySkullBlockEntity.toChunker();
        }
        return blockEntity;
    }
}
