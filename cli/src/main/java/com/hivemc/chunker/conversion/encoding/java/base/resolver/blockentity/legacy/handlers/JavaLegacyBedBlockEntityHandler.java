package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.*;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.type.JavaLegacyBedBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BedBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerDyeColor;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Handler for Beds which updates the block with the color based on the NBT.
 */
public class JavaLegacyBedBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, JavaLegacyBedBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<JavaResolvers, BedBlockEntity>, UpdateBeforeProcessBlockEntityHandler<JavaResolvers, BedBlockEntity>, GenerateBeforeProcessBlockEntityHandler<JavaLegacyBedBlockEntity>, DoNotWriteBlockEntityHandler<BedBlockEntity> {
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
    private final boolean shouldRemove;

    /**
     * Create a new legacy bed block entity handler.
     *
     * @param shouldRemove whether the bed block entity should not be written.
     */
    public JavaLegacyBedBlockEntityHandler(boolean shouldRemove) {
        super("Bed", JavaLegacyBedBlockEntity.class, JavaLegacyBedBlockEntity::new);
        this.shouldRemove = shouldRemove;
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull JavaLegacyBedBlockEntity value) {
        value.setColor(ChunkerDyeColor.getColorByID((input.getInt("color", (byte) 0))).orElse(ChunkerDyeColor.WHITE));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull JavaLegacyBedBlockEntity value) {
        output.put("color", value.getColor().getID());
    }

    @Override
    public BedBlockEntity updateBeforeWrite(@NotNull JavaResolvers resolvers, ChunkerColumn column, int x, int y, int z, BedBlockEntity blockEntity) {
        JavaLegacyBedBlockEntity legacyBedBlockEntity = new JavaLegacyBedBlockEntity(blockEntity);

        // Use the identifier of the block for color
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Grab the bed color
        ChunkerDyeColor color = BED_TO_DYE.getOrDefault(blockIdentifier.getType(), ChunkerDyeColor.WHITE);
        legacyBedBlockEntity.setColor(color);

        return legacyBedBlockEntity;
    }

    @Override
    public Class<BedBlockEntity> getAdditionalHandledClass() {
        return BedBlockEntity.class;
    }


    @Override
    public BedBlockEntity updateBeforeProcess(@NotNull JavaResolvers resolvers, ChunkerColumn column, int x, int y, int z, BedBlockEntity blockEntity) {
        if (blockEntity instanceof JavaLegacyBedBlockEntity legacyBedBlockEntity) {
            ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

            // Don't update anything if the block is air
            if (blockIdentifier.isAir()) return blockEntity;

            // Grab the bed type
            ChunkerBlockType newType = BED_TO_DYE.inverse().getOrDefault(legacyBedBlockEntity.getColor(), ChunkerVanillaBlockType.WHITE_BED);

            // Set the new block type
            column.setBlock(x, y, z, new ChunkerBlockIdentifier(newType, blockIdentifier.getPresentStates(), blockIdentifier.getPreservedIdentifier()));

            // Return the chunker version
            return legacyBedBlockEntity.toChunker();
        }
        return blockEntity;
    }

    @Override
    public Set<ChunkerBlockType> getGenerateBeforeProcessBlockTypes() {
        return BED_TO_DYE.keySet();
    }

    @Override
    public void generateBeforeProcess(ChunkerColumn column, int x, int y, int z, JavaLegacyBedBlockEntity blockEntity, ChunkerBlockIdentifier blockIdentifier) {
        // Just needs an empty bed block entity to be present
        blockEntity.setColor(ChunkerDyeColor.RED);
    }

    @Override
    public boolean shouldRemoveBeforeWrite(ChunkerColumn column, int x, int y, int z, BedBlockEntity blockEntity) {
        return shouldRemove;
    }
}
