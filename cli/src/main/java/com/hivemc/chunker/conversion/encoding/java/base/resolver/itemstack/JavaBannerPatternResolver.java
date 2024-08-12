package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.banner.ChunkerBannerPattern;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Banner Pattern resolver, used for Java identifiers.
 */
public class JavaBannerPatternResolver implements Resolver<String, ChunkerBannerPattern> {
    private final InvertibleMap<ChunkerBannerPattern, String> mapping = InvertibleMap.enumKeys(ChunkerBannerPattern.class);

    /**
     * Create a new java banner pattern resolver.
     *
     * @param version the game version being used, as certain banners are only available after specific versions.
     */
    public JavaBannerPatternResolver(Version version) {
        mapping.put(ChunkerBannerPattern.BASE, "minecraft:base");
        mapping.put(ChunkerBannerPattern.SQUARE_BOTTOM_LEFT, "minecraft:square_bottom_left");
        mapping.put(ChunkerBannerPattern.SQUARE_BOTTOM_RIGHT, "minecraft:square_bottom_right");
        mapping.put(ChunkerBannerPattern.SQUARE_TOP_LEFT, "minecraft:square_top_left");
        mapping.put(ChunkerBannerPattern.SQUARE_TOP_RIGHT, "minecraft:square_top_right");
        mapping.put(ChunkerBannerPattern.STRIPE_BOTTOM, "minecraft:stripe_bottom");
        mapping.put(ChunkerBannerPattern.STRIPE_TOP, "minecraft:stripe_top");
        mapping.put(ChunkerBannerPattern.STRIPE_LEFT, "minecraft:stripe_left");
        mapping.put(ChunkerBannerPattern.STRIPE_RIGHT, "minecraft:stripe_right");
        mapping.put(ChunkerBannerPattern.STRIPE_CENTER, "minecraft:stripe_center");
        mapping.put(ChunkerBannerPattern.STRIPE_MIDDLE, "minecraft:stripe_middle");
        mapping.put(ChunkerBannerPattern.STRIPE_DOWNRIGHT, "minecraft:stripe_downright");
        mapping.put(ChunkerBannerPattern.STRIPE_DOWNLEFT, "minecraft:stripe_downleft");
        mapping.put(ChunkerBannerPattern.STRIPE_SMALL, "minecraft:small_stripes");
        mapping.put(ChunkerBannerPattern.CROSS, "minecraft:cross");
        mapping.put(ChunkerBannerPattern.STRAIGHT_CROSS, "minecraft:straight_cross");
        mapping.put(ChunkerBannerPattern.TRIANGLE_BOTTOM, "minecraft:triangle_bottom");
        mapping.put(ChunkerBannerPattern.TRIANGLE_TOP, "minecraft:triangle_top");
        mapping.put(ChunkerBannerPattern.TRIANGLES_BOTTOM, "minecraft:triangles_bottom");
        mapping.put(ChunkerBannerPattern.TRIANGLES_TOP, "minecraft:triangles_top");
        mapping.put(ChunkerBannerPattern.DIAGONAL_LEFT, "minecraft:diagonal_left");
        mapping.put(ChunkerBannerPattern.DIAGONAL_RIGHT, "minecraft:diagonal_up_right");
        mapping.put(ChunkerBannerPattern.DIAGONAL_LEFT_MIRROR, "minecraft:diagonal_up_left");
        mapping.put(ChunkerBannerPattern.DIAGONAL_RIGHT_MIRROR, "minecraft:diagonal_right");
        mapping.put(ChunkerBannerPattern.CIRCLE_MIDDLE, "minecraft:circle");
        mapping.put(ChunkerBannerPattern.RHOMBUS_MIDDLE, "minecraft:rhombus");
        mapping.put(ChunkerBannerPattern.HALF_VERTICAL, "minecraft:half_vertical");
        mapping.put(ChunkerBannerPattern.HALF_HORIZONTAL, "minecraft:half_horizontal");
        mapping.put(ChunkerBannerPattern.HALF_VERTICAL_MIRROR, "minecraft:half_vertical_right");
        mapping.put(ChunkerBannerPattern.HALF_HORIZONTAL_MIRROR, "minecraft:half_horizontal_bottom");
        mapping.put(ChunkerBannerPattern.BORDER, "minecraft:border");
        mapping.put(ChunkerBannerPattern.CURLY_BORDER, "minecraft:curly_border");
        mapping.put(ChunkerBannerPattern.GRADIENT, "minecraft:gradient");
        mapping.put(ChunkerBannerPattern.GRADIENT_UP, "minecraft:gradient_up");
        mapping.put(ChunkerBannerPattern.BRICKS, "minecraft:bricks");
        mapping.put(ChunkerBannerPattern.GLOBE, "minecraft:globe");
        mapping.put(ChunkerBannerPattern.CREEPER, "minecraft:creeper");
        mapping.put(ChunkerBannerPattern.SKULL, "minecraft:skull");
        mapping.put(ChunkerBannerPattern.FLOWER, "minecraft:flower");
        mapping.put(ChunkerBannerPattern.MOJANG, "minecraft:mojang");
        mapping.put(ChunkerBannerPattern.PIGLIN, "minecraft:piglin");

        if (version.isGreaterThanOrEqual(1, 21, 0)) {
            mapping.put(ChunkerBannerPattern.FLOW, "minecraft:flow");
            mapping.put(ChunkerBannerPattern.GUSTER, "minecraft:guster");
        }
    }

    @Override
    public Optional<String> from(ChunkerBannerPattern input) {
        return Optional.ofNullable(mapping.forward().get(input));
    }

    @Override
    public Optional<ChunkerBannerPattern> to(String input) {
        return Optional.ofNullable(mapping.inverse().get(input));
    }
}
