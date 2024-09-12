package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockBedBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BedBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerDyeColor;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Beds which updates the block with the color based on the NBT.
 */
public class BedrockBedBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BedrockBedBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<BedrockResolvers, BedBlockEntity>, UpdateBeforeProcessBlockEntityHandler<BedrockResolvers, BedBlockEntity> {
    public static final BiMap<ChunkerBlockType, ChunkerDyeColor> BED_TO_DYE = ImmutableBiMap.<ChunkerBlockType, ChunkerDyeColor>builder()
            .put(ChunkerVanillaBlockType.WHITE_BED, ChunkerDyeColor.WHITE)
            .put(ChunkerVanillaBlockType.ORANGE_BED, ChunkerDyeColor.ORANGE)
            .put(ChunkerVanillaBlockType.MAGENTA_BED, ChunkerDyeColor.MAGENTA)
            .put(ChunkerVanillaBlockType.LIGHT_BLUE_BED, ChunkerDyeColor.LIGHT_BLUE)
            .put(ChunkerVanillaBlockType.YELLOW_BED, ChunkerDyeColor.YELLOW)
            .put(ChunkerVanillaBlockType.LIME_BED, ChunkerDyeColor.LIME)
            .put(ChunkerVanillaBlockType.PINK_BED, ChunkerDyeColor.PINK)
            .put(ChunkerVanillaBlockType.GRAY_BED, ChunkerDyeColor.GRAY)
            .put(ChunkerVanillaBlockType.LIGHT_GRAY_BED, ChunkerDyeColor.LIGHT_GRAY)
            .put(ChunkerVanillaBlockType.CYAN_BED, ChunkerDyeColor.CYAN)
            .put(ChunkerVanillaBlockType.PURPLE_BED, ChunkerDyeColor.PURPLE)
            .put(ChunkerVanillaBlockType.BLUE_BED, ChunkerDyeColor.BLUE)
            .put(ChunkerVanillaBlockType.BROWN_BED, ChunkerDyeColor.BROWN)
            .put(ChunkerVanillaBlockType.GREEN_BED, ChunkerDyeColor.GREEN)
            .put(ChunkerVanillaBlockType.RED_BED, ChunkerDyeColor.RED)
            .put(ChunkerVanillaBlockType.BLACK_BED, ChunkerDyeColor.BLACK)
            .build();

    public BedrockBedBlockEntityHandler() {
        super("Bed", BedrockBedBlockEntity.class, BedrockBedBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BedrockBedBlockEntity value) {
        value.setColor(ChunkerDyeColor.getColorByID((input.getByte("color", (byte) 0))).orElse(ChunkerDyeColor.WHITE));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BedrockBedBlockEntity value) {
        output.put("color", (byte) value.getColor().getID());
    }

    @Override
    public BedBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, BedBlockEntity blockEntity) {
        BedrockBedBlockEntity bedrockBedBlockEntity = new BedrockBedBlockEntity(blockEntity);

        // Use the identifier of the block for color
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Grab the bed color
        ChunkerDyeColor color = BED_TO_DYE.getOrDefault(blockIdentifier.getType(), ChunkerDyeColor.WHITE);
        bedrockBedBlockEntity.setColor(color);

        return bedrockBedBlockEntity;
    }

    @Override
    public Class<BedBlockEntity> getAdditionalHandledClass() {
        return BedBlockEntity.class;
    }


    @Override
    public BedBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, BedBlockEntity blockEntity) {
        if (blockEntity instanceof BedrockBedBlockEntity bedrockBedBlockEntity) {
            ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

            // Grab the bed type
            ChunkerBlockType newType = BED_TO_DYE.inverse().getOrDefault(bedrockBedBlockEntity.getColor(), ChunkerVanillaBlockType.WHITE_BED);

            // Set the new block type (if the bed is present)
            if (!blockIdentifier.isAir()) {
                column.setBlock(x, y, z, new ChunkerBlockIdentifier(newType, blockIdentifier.getPresentStates(), blockIdentifier.getPreservedIdentifier()));
            }

            // Return the chunker version
            return bedrockBedBlockEntity.toChunker();
        }
        return blockEntity;
    }
}
