package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.DoNotProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.GenerateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers.BedrockFlowerPotBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.type.JavaLegacyFlowerPotBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Handler for the Java only block entity for flower pots which converts the NBT identifier to the potted block type.
 */
public class JavaLegacyFlowerPotBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, JavaLegacyFlowerPotBlockEntity> implements GenerateBeforeWriteBlockEntityHandler<JavaLegacyFlowerPotBlockEntity>, DoNotProcessBlockEntityHandler<JavaLegacyFlowerPotBlockEntity> {
    public JavaLegacyFlowerPotBlockEntityHandler() {
        super("FlowerPot", JavaLegacyFlowerPotBlockEntity.class, JavaLegacyFlowerPotBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull JavaLegacyFlowerPotBlockEntity value) {
        String identifier = input.getString("Item", null);
        int data = input.getInt("Data", 0);
        if (identifier != null && !identifier.isBlank()) {
            value.setPlant(resolvers.readBlockIdentifier(Identifier.fromData(identifier, OptionalInt.of(data))));
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull JavaLegacyFlowerPotBlockEntity value) {
        if (value.getPlant() != null && !value.getPlant().isAir()) {
            Optional<Identifier> identifier = resolvers.writeBlockIdentifier(value.getPlant(), true);
            if (identifier.isPresent()) {
                output.put("Item", identifier.get().getIdentifier());
                output.put("Data", identifier.get().getDataValue().orElse(0));
            } else {
                output.put("Data", 0); // Data seems to always be written
            }
        } else {
            output.put("Data", 0); // Data seems to always be written
        }
    }

    @Override
    public Set<ChunkerBlockType> getGenerateBeforeWriteBlockTypes() {
        return BedrockFlowerPotBlockEntityHandler.POTTED_TO_PLANT.keySet();
    }

    @Override
    public void generateBeforeWrite(ChunkerColumn column, int x, int y, int z, JavaLegacyFlowerPotBlockEntity blockEntity, ChunkerBlockIdentifier blockIdentifier) {
        ChunkerBlockType newPlantType = BedrockFlowerPotBlockEntityHandler.POTTED_TO_PLANT.get(blockIdentifier.getType());
        if (newPlantType != null) {
            blockEntity.setPlant(new ChunkerBlockIdentifier(
                    newPlantType,
                    Map.of(
                            VanillaBlockStates.WATERLOGGED,
                            Objects.requireNonNull(blockIdentifier.getState(VanillaBlockStates.WATERLOGGED))
                    )
            ));
        }
    }

    @Override
    public boolean shouldRemoveBeforeProcess(ChunkerColumn column, int x, int y, int z, JavaLegacyFlowerPotBlockEntity blockEntity) {
        if (blockEntity.getPlant() != null && !blockEntity.getPlant().isAir()) {
            ChunkerBlockType potType = BedrockFlowerPotBlockEntityHandler.POTTED_TO_PLANT.inverse().get(blockEntity.getPlant().getType());
            if (potType != null) {
                ChunkerBlockIdentifier oldBlockIdentifier = column.getBlock(x, y, z);

                // Set the block in the column to the potted identifier
                column.setBlock(x, y, z, new ChunkerBlockIdentifier(
                        potType,
                        Map.of(
                                VanillaBlockStates.WATERLOGGED,
                                Objects.requireNonNull(oldBlockIdentifier.getState(VanillaBlockStates.WATERLOGGED))
                        )
                ));
            }
        }

        // Remove before processing
        return true;
    }
}
