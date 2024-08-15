package com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.banner.ChunkerBannerPattern;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.InvertibleMap;

import java.util.Optional;

/**
 * Banner Pattern resolver, used for Java short names.
 */
public class JavaBannerPatternShortNameResolver implements Resolver<String, ChunkerBannerPattern> {
    private final InvertibleMap<ChunkerBannerPattern, String> mapping = InvertibleMap.enumKeys(ChunkerBannerPattern.class);

    /**
     * Create a new java banner pattern resolver.
     *
     * @param version the game version being used, as certain banners are only available after specific versions.
     */
    public JavaBannerPatternShortNameResolver(Version version) {
        mapping.put(ChunkerBannerPattern.BASE, "b");
        mapping.put(ChunkerBannerPattern.SQUARE_BOTTOM_LEFT, "bl");
        mapping.put(ChunkerBannerPattern.SQUARE_BOTTOM_RIGHT, "br");
        mapping.put(ChunkerBannerPattern.SQUARE_TOP_LEFT, "tl");
        mapping.put(ChunkerBannerPattern.SQUARE_TOP_RIGHT, "tr");
        mapping.put(ChunkerBannerPattern.STRIPE_BOTTOM, "bs");
        mapping.put(ChunkerBannerPattern.STRIPE_TOP, "ts");
        mapping.put(ChunkerBannerPattern.STRIPE_LEFT, "ls");
        mapping.put(ChunkerBannerPattern.STRIPE_RIGHT, "rs");
        mapping.put(ChunkerBannerPattern.STRIPE_CENTER, "cs");
        mapping.put(ChunkerBannerPattern.STRIPE_MIDDLE, "ms");
        mapping.put(ChunkerBannerPattern.STRIPE_DOWNRIGHT, "drs");
        mapping.put(ChunkerBannerPattern.STRIPE_DOWNLEFT, "dls");
        mapping.put(ChunkerBannerPattern.STRIPE_SMALL, "ss");
        mapping.put(ChunkerBannerPattern.CROSS, "cr");
        mapping.put(ChunkerBannerPattern.STRAIGHT_CROSS, "sc");
        mapping.put(ChunkerBannerPattern.TRIANGLE_BOTTOM, "bt");
        mapping.put(ChunkerBannerPattern.TRIANGLE_TOP, "tt");
        mapping.put(ChunkerBannerPattern.TRIANGLES_BOTTOM, "bts");
        mapping.put(ChunkerBannerPattern.TRIANGLES_TOP, "tts");
        mapping.put(ChunkerBannerPattern.DIAGONAL_LEFT, "ld");
        mapping.put(ChunkerBannerPattern.DIAGONAL_RIGHT, "rd");
        mapping.put(ChunkerBannerPattern.DIAGONAL_LEFT_MIRROR, "lud");
        mapping.put(ChunkerBannerPattern.DIAGONAL_RIGHT_MIRROR, "rud");
        mapping.put(ChunkerBannerPattern.CIRCLE_MIDDLE, "mc");
        mapping.put(ChunkerBannerPattern.RHOMBUS_MIDDLE, "mr");
        mapping.put(ChunkerBannerPattern.HALF_VERTICAL, "vh");
        mapping.put(ChunkerBannerPattern.HALF_HORIZONTAL, "hh");
        mapping.put(ChunkerBannerPattern.HALF_VERTICAL_MIRROR, "vhr");
        mapping.put(ChunkerBannerPattern.HALF_HORIZONTAL_MIRROR, "hhb");
        mapping.put(ChunkerBannerPattern.BORDER, "bo");
        mapping.put(ChunkerBannerPattern.CURLY_BORDER, "cbo");
        mapping.put(ChunkerBannerPattern.GRADIENT, "gra");
        mapping.put(ChunkerBannerPattern.GRADIENT_UP, "gru");
        mapping.put(ChunkerBannerPattern.BRICKS, "bri");
        mapping.put(ChunkerBannerPattern.GLOBE, "glb");
        mapping.put(ChunkerBannerPattern.CREEPER, "cre");
        mapping.put(ChunkerBannerPattern.SKULL, "sku");
        mapping.put(ChunkerBannerPattern.FLOWER, "flo");
        mapping.put(ChunkerBannerPattern.MOJANG, "moj");
        mapping.put(ChunkerBannerPattern.PIGLIN, "pig");
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
