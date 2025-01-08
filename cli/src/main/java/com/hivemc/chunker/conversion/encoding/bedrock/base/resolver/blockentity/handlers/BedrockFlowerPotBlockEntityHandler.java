package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.DoNotProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.GenerateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockFlowerPotBlockEntity;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockCompoundTag;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Handler for the Bedrock only block entity for flower pots which converts the NBT identifier to the potted block type.
 */
public class BedrockFlowerPotBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockFlowerPotBlockEntity> implements GenerateBeforeWriteBlockEntityHandler<BedrockFlowerPotBlockEntity>, DoNotProcessBlockEntityHandler<BedrockFlowerPotBlockEntity> {
    public static final BiMap<ChunkerBlockType, ChunkerBlockType> POTTED_TO_PLANT = ImmutableBiMap.<ChunkerBlockType, ChunkerBlockType>builder()
            .put(ChunkerVanillaBlockType.FLOWER_POT, ChunkerVanillaBlockType.AIR)
            .put(ChunkerVanillaBlockType.POTTED_TORCHFLOWER, ChunkerVanillaBlockType.TORCHFLOWER)
            .put(ChunkerVanillaBlockType.POTTED_OAK_SAPLING, ChunkerVanillaBlockType.OAK_SAPLING)
            .put(ChunkerVanillaBlockType.POTTED_SPRUCE_SAPLING, ChunkerVanillaBlockType.SPRUCE_SAPLING)
            .put(ChunkerVanillaBlockType.POTTED_BIRCH_SAPLING, ChunkerVanillaBlockType.BIRCH_SAPLING)
            .put(ChunkerVanillaBlockType.POTTED_JUNGLE_SAPLING, ChunkerVanillaBlockType.JUNGLE_SAPLING)
            .put(ChunkerVanillaBlockType.POTTED_ACACIA_SAPLING, ChunkerVanillaBlockType.ACACIA_SAPLING)
            .put(ChunkerVanillaBlockType.POTTED_CHERRY_SAPLING, ChunkerVanillaBlockType.CHERRY_SAPLING)
            .put(ChunkerVanillaBlockType.POTTED_DARK_OAK_SAPLING, ChunkerVanillaBlockType.DARK_OAK_SAPLING)
            .put(ChunkerVanillaBlockType.POTTED_PALE_OAK_SAPLING, ChunkerVanillaBlockType.PALE_OAK_SAPLING)
            .put(ChunkerVanillaBlockType.POTTED_MANGROVE_PROPAGULE, ChunkerVanillaBlockType.MANGROVE_PROPAGULE)
            .put(ChunkerVanillaBlockType.POTTED_FERN, ChunkerVanillaBlockType.FERN)
            .put(ChunkerVanillaBlockType.POTTED_DANDELION, ChunkerVanillaBlockType.DANDELION)
            .put(ChunkerVanillaBlockType.POTTED_POPPY, ChunkerVanillaBlockType.POPPY)
            .put(ChunkerVanillaBlockType.POTTED_BLUE_ORCHID, ChunkerVanillaBlockType.BLUE_ORCHID)
            .put(ChunkerVanillaBlockType.POTTED_ALLIUM, ChunkerVanillaBlockType.ALLIUM)
            .put(ChunkerVanillaBlockType.POTTED_AZURE_BLUET, ChunkerVanillaBlockType.AZURE_BLUET)
            .put(ChunkerVanillaBlockType.POTTED_RED_TULIP, ChunkerVanillaBlockType.RED_TULIP)
            .put(ChunkerVanillaBlockType.POTTED_ORANGE_TULIP, ChunkerVanillaBlockType.ORANGE_TULIP)
            .put(ChunkerVanillaBlockType.POTTED_WHITE_TULIP, ChunkerVanillaBlockType.WHITE_TULIP)
            .put(ChunkerVanillaBlockType.POTTED_PINK_TULIP, ChunkerVanillaBlockType.PINK_TULIP)
            .put(ChunkerVanillaBlockType.POTTED_OXEYE_DAISY, ChunkerVanillaBlockType.OXEYE_DAISY)
            .put(ChunkerVanillaBlockType.POTTED_CORNFLOWER, ChunkerVanillaBlockType.CORNFLOWER)
            .put(ChunkerVanillaBlockType.POTTED_LILY_OF_THE_VALLEY, ChunkerVanillaBlockType.LILY_OF_THE_VALLEY)
            .put(ChunkerVanillaBlockType.POTTED_WITHER_ROSE, ChunkerVanillaBlockType.WITHER_ROSE)
            .put(ChunkerVanillaBlockType.POTTED_RED_MUSHROOM, ChunkerVanillaBlockType.RED_MUSHROOM)
            .put(ChunkerVanillaBlockType.POTTED_BROWN_MUSHROOM, ChunkerVanillaBlockType.BROWN_MUSHROOM)
            .put(ChunkerVanillaBlockType.POTTED_DEAD_BUSH, ChunkerVanillaBlockType.DEAD_BUSH)
            .put(ChunkerVanillaBlockType.POTTED_CACTUS, ChunkerVanillaBlockType.CACTUS)
            .put(ChunkerVanillaBlockType.POTTED_BAMBOO, ChunkerVanillaBlockType.BAMBOO)
            .put(ChunkerVanillaBlockType.POTTED_CRIMSON_FUNGUS, ChunkerVanillaBlockType.CRIMSON_FUNGUS)
            .put(ChunkerVanillaBlockType.POTTED_CRIMSON_ROOTS, ChunkerVanillaBlockType.CRIMSON_ROOTS)
            .put(ChunkerVanillaBlockType.POTTED_WARPED_FUNGUS, ChunkerVanillaBlockType.WARPED_FUNGUS)
            .put(ChunkerVanillaBlockType.POTTED_WARPED_ROOTS, ChunkerVanillaBlockType.WARPED_ROOTS)
            .put(ChunkerVanillaBlockType.POTTED_AZALEA_BUSH, ChunkerVanillaBlockType.AZALEA)
            .put(ChunkerVanillaBlockType.POTTED_FLOWERING_AZALEA_BUSH, ChunkerVanillaBlockType.FLOWERING_AZALEA)
            .put(ChunkerVanillaBlockType.POTTED_OPEN_EYEBLOSSOM, ChunkerVanillaBlockType.OPEN_EYEBLOSSOM)
            .put(ChunkerVanillaBlockType.POTTED_CLOSED_EYEBLOSSOM, ChunkerVanillaBlockType.CLOSED_EYEBLOSSOM)
            .build();

    public BedrockFlowerPotBlockEntityHandler() {
        super("FlowerPot", BedrockFlowerPotBlockEntity.class, BedrockFlowerPotBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockFlowerPotBlockEntity value) {
        CompoundTag plantTag = input.getCompound("PlantBlock");
        if (plantTag != null) {
            value.setPlant(resolvers.readBlock(new BedrockBlockCompoundTag(plantTag, false)));
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockFlowerPotBlockEntity value) {
        if (value.getPlant() != null && !value.getPlant().isAir()) {
            output.put("PlantBlock", resolvers.writeBlock(value.getPlant()).compoundTag());
        }
    }

    @Override
    public Set<ChunkerBlockType> getGenerateBeforeWriteBlockTypes() {
        return POTTED_TO_PLANT.keySet();
    }

    @Override
    public void generateBeforeWrite(ChunkerColumn column, int x, int y, int z, BedrockFlowerPotBlockEntity blockEntity, ChunkerBlockIdentifier blockIdentifier) {
        ChunkerBlockType newPlantType = POTTED_TO_PLANT.get(blockIdentifier.getType());
        blockEntity.setPlant(new ChunkerBlockIdentifier(
                newPlantType,
                Map.of(
                        VanillaBlockStates.WATERLOGGED,
                        Objects.requireNonNull(blockIdentifier.getState(VanillaBlockStates.WATERLOGGED))
                )
        ));
    }

    @Override
    public boolean shouldRemoveBeforeProcess(ChunkerColumn column, int x, int y, int z, BedrockFlowerPotBlockEntity blockEntity) {
        if (blockEntity.getPlant() != null && !blockEntity.getPlant().isAir()) {
            ChunkerBlockType potType = POTTED_TO_PLANT.inverse().get(blockEntity.getPlant().getType());
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
