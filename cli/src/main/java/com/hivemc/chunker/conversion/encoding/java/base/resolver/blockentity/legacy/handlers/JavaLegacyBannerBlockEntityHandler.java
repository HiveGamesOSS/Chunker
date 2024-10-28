package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeProcessBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.UpdateBeforeWriteBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers.BedrockBannerBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
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
import com.hivemc.chunker.util.JsonTextUtil;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Handler for Banners which also handles updating the block with the actual color of the banner based on data.
 */
public class JavaLegacyBannerBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, BannerBlockEntity> implements UpdateBeforeWriteBlockEntityHandler<JavaResolvers, BannerBlockEntity>, UpdateBeforeProcessBlockEntityHandler<JavaResolvers, BannerBlockEntity> {
    public JavaLegacyBannerBlockEntityHandler() {
        super("Banner", BannerBlockEntity.class, BannerBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull BannerBlockEntity value) {
        // Parse base
        value.setBase(input.getOptionalValue("Base", Integer.class).flatMap(ChunkerDyeColor::getColorByReversedID));

        // Parse patterns
        ListTag<CompoundTag, Map<String, Tag<?>>> patternTags = input.getList("patterns", CompoundTag.class, null);
        if (patternTags == null) {
            patternTags = input.getList("Patterns", CompoundTag.class, null);
        }

        if (patternTags != null) {
            // Loop through each pattern
            for (CompoundTag patternTag : patternTags) {
                Optional<ChunkerDyeColor> color;
                if (patternTag.contains("Color")) {
                    color = patternTag.getOptionalValue("Color", Integer.class)
                            .flatMap(ChunkerDyeColor::getColorByReversedID);
                } else {
                    color = patternTag.getOptionalValue("color", String.class)
                            .flatMap(ChunkerDyeColor::getColorByName);
                }
                Optional<ChunkerBannerPattern> pattern;
                if (patternTag.contains("Pattern")) {
                    pattern = patternTag.getOptionalValue("Pattern", String.class)
                            .flatMap(resolvers.bannerPatternShortNameResolver()::to);
                } else {
                    pattern = patternTag.getOptionalValue("pattern", String.class)
                            .flatMap(resolvers.bannerPatternResolver()::to);
                }
                // If the color and pattern are present
                if (color.isPresent() && pattern.isPresent()) {
                    value.getPatterns().add(Pair.of(color.get(), pattern.get()));
                }
            }
        }

        value.setCustomName(input.getOptionalValue("CustomName", String.class)
                .map(JsonTextUtil::fromJSON)
                .orElse(null));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull BannerBlockEntity value) {
        // Write base
        if (value.getBase().isPresent()) {
            output.put("Base", value.getBase().get().getReversedID());
        }

        // Write patterns
        ListTag<CompoundTag, Map<String, Tag<?>>> patternTags = new ListTag<>(TagType.COMPOUND, new ArrayList<>(value.getPatterns().size()));
        for (Pair<ChunkerDyeColor, ChunkerBannerPattern> pair : value.getPatterns()) {
            Optional<String> shortName = resolvers.bannerPatternShortNameResolver().from(pair.second());
            if (shortName.isPresent()) {
                CompoundTag patternTag = new CompoundTag();
                patternTag.put("Color", pair.first().getReversedID());
                patternTag.put("Pattern", shortName.get());
                patternTags.add(patternTag);
            }
        }
        output.put("Patterns", patternTags);

        if (value.getCustomName() != null) {
            output.put("CustomName", JsonTextUtil.toJSON(value.getCustomName()));
        }
    }

    @Override
    public BannerBlockEntity updateBeforeWrite(@NotNull JavaResolvers resolvers, ChunkerColumn column, int x, int y, int z, BannerBlockEntity blockEntity) {
        // Use the identifier of the block for color
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Grab the banner color
        Pair<Boolean, ChunkerDyeColor> color = BedrockBannerBlockEntityHandler.BANNER_TO_WALL_DYE.getOrDefault(blockIdentifier.getType(), null);
        blockEntity.setBase(Optional.ofNullable(color).map(Pair::value));

        return blockEntity;
    }

    @Override
    public BannerBlockEntity updateBeforeWrite(@NotNull JavaResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, BannerBlockEntity blockEntity) {
        return blockEntity; // This block entity doesn't need any updating when it's an item
    }

    @Override
    public Class<BannerBlockEntity> getAdditionalHandledClass() {
        return BannerBlockEntity.class;
    }

    @Override
    public BannerBlockEntity updateBeforeProcess(@NotNull JavaResolvers resolvers, ChunkerColumn column, int x, int y, int z, BannerBlockEntity blockEntity) {
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(x, y, z);

        // Don't update anything if the block is air
        if (blockIdentifier.isAir()) return blockEntity;

        // Grab the banner type
        boolean wall = blockIdentifier.getType().getStates().contains(VanillaBlockStates.FACING_HORIZONTAL);
        if (blockEntity.getBase().isPresent()) {
            // Get the new block type
            ChunkerBlockType newType = BedrockBannerBlockEntityHandler.BANNER_TO_WALL_DYE.inverse().getOrDefault(Pair.of(wall, blockEntity.getBase().get()), ChunkerVanillaBlockType.WHITE_BANNER);

            // Set the new block type
            column.setBlock(x, y, z, new ChunkerBlockIdentifier(newType, blockIdentifier.getPresentStates(), blockIdentifier.getPreservedIdentifier()));

            // Remove the base since it's now in the block
            blockEntity.setBase(Optional.empty());
        }
        return blockEntity;
    }

    @Override
    public BannerBlockEntity updateBeforeProcess(@NotNull JavaResolvers resolvers, CompoundTag itemCompoundTag, ChunkerItemStack chunkerItemStack, BannerBlockEntity blockEntity) {
        return blockEntity; // This block entity doesn't need any updating when it's an item
    }
}
