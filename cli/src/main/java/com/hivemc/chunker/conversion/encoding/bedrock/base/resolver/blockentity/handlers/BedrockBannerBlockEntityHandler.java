package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BannerBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerDyeColor;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.banner.ChunkerBannerPattern;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Handler for Banners which also handles updating the block with the actual color of the banner based on data.
 */
public class BedrockBannerBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, BannerBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<BedrockResolvers, BannerBlockEntity>, UpdateBeforeProcessBlockEntityHandler<BedrockResolvers, BannerBlockEntity> {
    public static final BiMap<ChunkerBlockType, Pair<Boolean, ChunkerDyeColor>> BANNER_TO_WALL_DYE = ImmutableBiMap.<ChunkerBlockType, Pair<Boolean, ChunkerDyeColor>>builder()
            .put(ChunkerVanillaBlockType.WHITE_BANNER, Pair.of(false, ChunkerDyeColor.WHITE))
            .put(ChunkerVanillaBlockType.ORANGE_BANNER, Pair.of(false, ChunkerDyeColor.ORANGE))
            .put(ChunkerVanillaBlockType.MAGENTA_BANNER, Pair.of(false, ChunkerDyeColor.MAGENTA))
            .put(ChunkerVanillaBlockType.LIGHT_BLUE_BANNER, Pair.of(false, ChunkerDyeColor.LIGHT_BLUE))
            .put(ChunkerVanillaBlockType.YELLOW_BANNER, Pair.of(false, ChunkerDyeColor.YELLOW))
            .put(ChunkerVanillaBlockType.LIME_BANNER, Pair.of(false, ChunkerDyeColor.LIME))
            .put(ChunkerVanillaBlockType.PINK_BANNER, Pair.of(false, ChunkerDyeColor.PINK))
            .put(ChunkerVanillaBlockType.GRAY_BANNER, Pair.of(false, ChunkerDyeColor.GRAY))
            .put(ChunkerVanillaBlockType.LIGHT_GRAY_BANNER, Pair.of(false, ChunkerDyeColor.LIGHT_GRAY))
            .put(ChunkerVanillaBlockType.CYAN_BANNER, Pair.of(false, ChunkerDyeColor.CYAN))
            .put(ChunkerVanillaBlockType.PURPLE_BANNER, Pair.of(false, ChunkerDyeColor.PURPLE))
            .put(ChunkerVanillaBlockType.BLUE_BANNER, Pair.of(false, ChunkerDyeColor.BLUE))
            .put(ChunkerVanillaBlockType.BROWN_BANNER, Pair.of(false, ChunkerDyeColor.BROWN))
            .put(ChunkerVanillaBlockType.GREEN_BANNER, Pair.of(false, ChunkerDyeColor.GREEN))
            .put(ChunkerVanillaBlockType.RED_BANNER, Pair.of(false, ChunkerDyeColor.RED))
            .put(ChunkerVanillaBlockType.BLACK_BANNER, Pair.of(false, ChunkerDyeColor.BLACK))
            .put(ChunkerVanillaBlockType.WHITE_WALL_BANNER, Pair.of(true, ChunkerDyeColor.WHITE))
            .put(ChunkerVanillaBlockType.ORANGE_WALL_BANNER, Pair.of(true, ChunkerDyeColor.ORANGE))
            .put(ChunkerVanillaBlockType.MAGENTA_WALL_BANNER, Pair.of(true, ChunkerDyeColor.MAGENTA))
            .put(ChunkerVanillaBlockType.LIGHT_BLUE_WALL_BANNER, Pair.of(true, ChunkerDyeColor.LIGHT_BLUE))
            .put(ChunkerVanillaBlockType.YELLOW_WALL_BANNER, Pair.of(true, ChunkerDyeColor.YELLOW))
            .put(ChunkerVanillaBlockType.LIME_WALL_BANNER, Pair.of(true, ChunkerDyeColor.LIME))
            .put(ChunkerVanillaBlockType.PINK_WALL_BANNER, Pair.of(true, ChunkerDyeColor.PINK))
            .put(ChunkerVanillaBlockType.GRAY_WALL_BANNER, Pair.of(true, ChunkerDyeColor.GRAY))
            .put(ChunkerVanillaBlockType.LIGHT_GRAY_WALL_BANNER, Pair.of(true, ChunkerDyeColor.LIGHT_GRAY))
            .put(ChunkerVanillaBlockType.CYAN_WALL_BANNER, Pair.of(true, ChunkerDyeColor.CYAN))
            .put(ChunkerVanillaBlockType.PURPLE_WALL_BANNER, Pair.of(true, ChunkerDyeColor.PURPLE))
            .put(ChunkerVanillaBlockType.BLUE_WALL_BANNER, Pair.of(true, ChunkerDyeColor.BLUE))
            .put(ChunkerVanillaBlockType.BROWN_WALL_BANNER, Pair.of(true, ChunkerDyeColor.BROWN))
            .put(ChunkerVanillaBlockType.GREEN_WALL_BANNER, Pair.of(true, ChunkerDyeColor.GREEN))
            .put(ChunkerVanillaBlockType.RED_WALL_BANNER, Pair.of(true, ChunkerDyeColor.RED))
            .put(ChunkerVanillaBlockType.BLACK_WALL_BANNER, Pair.of(true, ChunkerDyeColor.BLACK))
            .build();

    public BedrockBannerBlockEntityHandler() {
        super("Banner", BannerBlockEntity.class, BannerBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull BannerBlockEntity value) {
        // Parse base
        value.setBase(input.getOptionalValue("Base", Integer.class).flatMap(ChunkerDyeColor::getColorByReversedID));

        // Parse patterns
        ListTag<CompoundTag, Map<String, Tag<?>>> patternTags = input.getList("Patterns", CompoundTag.class, null);
        if (patternTags != null) {
            // Loop through each pattern
            for (CompoundTag patternTag : patternTags) {
                Optional<ChunkerDyeColor> color = patternTag.getOptionalValue("Color", Integer.class)
                        .flatMap(ChunkerDyeColor::getColorByReversedID);
                Optional<ChunkerBannerPattern> pattern = patternTag.getOptionalValue("Pattern", String.class)
                        .flatMap(resolvers.bannerPatternResolver()::to);

                // If the color and pattern are present
                if (color.isPresent() && pattern.isPresent()) {
                    value.getPatterns().add(Pair.of(color.get(), pattern.get()));
                }
            }
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull BannerBlockEntity value) {
        // Write base
        if (value.getBase().isPresent()) {
            output.put("Base", value.getBase().get().getReversedID());
        }

        // Write patterns
        ListTag<CompoundTag, Map<String, Tag<?>>> patternTags = new ListTag<>(TagType.COMPOUND, new ArrayList<>(value.getPatterns().size()));
        for (Pair<ChunkerDyeColor, ChunkerBannerPattern> pair : value.getPatterns()) {
            Optional<String> patternShortName = resolvers.bannerPatternResolver().from(pair.second());
            if (patternShortName.isPresent()) {
                CompoundTag patternTag = new CompoundTag();
                patternTag.put("Color", pair.first().getReversedID());
                patternTag.put("Pattern", patternShortName.get());
                patternTags.add(patternTag);
            }
        }
        output.put("Patterns", patternTags);
    }

    @Override
    public BannerBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, BannerBlockEntity blockEntity) {
        // Use the identifier of the block for color
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Grab the banner color
        Pair<Boolean, ChunkerDyeColor> color = BANNER_TO_WALL_DYE.getOrDefault(blockIdentifier.getType(), null);
        blockEntity.setBase(Optional.ofNullable(color).map(Pair::value));

        return blockEntity;
    }

    @Override
    public BannerBlockEntity updateBeforeWrite(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, BannerBlockEntity blockEntity) {
        return blockEntity; // This block entity doesn't need any updating when it's an item
    }

    @Override
    public Class<BannerBlockEntity> getAdditionalHandledClass() {
        return BannerBlockEntity.class;
    }

    @Override
    public BannerBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, ChunkerColumn column, int x, int y, int z, BannerBlockEntity blockEntity) {
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Grab the banner type
        boolean wall = blockIdentifier.getType().getStates().contains(VanillaBlockStates.FACING_HORIZONTAL);
        if (blockEntity.getBase().isPresent()) {
            // Get the new block type
            ChunkerBlockType newType = BANNER_TO_WALL_DYE.inverse().getOrDefault(Pair.of(wall, blockEntity.getBase().get()), ChunkerVanillaBlockType.WHITE_BANNER);

            // Set the new block type (if the banner is present)
            if (!blockIdentifier.isAir()) {
                column.setBlock(x, y, z, new ChunkerBlockIdentifier(newType, blockIdentifier.getPresentStates(), blockIdentifier.getPreservedIdentifier()));
            }

            // Remove the base since it's now in the block
            blockEntity.setBase(Optional.empty());
        }
        return blockEntity;
    }

    @Override
    public BannerBlockEntity updateBeforeProcess(@NotNull BedrockResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, BannerBlockEntity blockEntity) {
        return blockEntity; // This block entity doesn't need any updating when it's an item
    }
}
