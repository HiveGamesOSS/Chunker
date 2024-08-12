package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerDyeColor;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.banner.ChunkerBannerPattern;
import it.unimi.dsi.fastutil.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a Banner Block Entity.
 */
public class BannerBlockEntity extends BlockEntity {
    private final List<Pair<ChunkerDyeColor, ChunkerBannerPattern>> patterns = new ArrayList<>();
    private Optional<ChunkerDyeColor> base = Optional.empty(); // On Java base is only for shields

    /**
     * Get a list of patterns used on the banner.
     *
     * @return a list with a pair of the color to the pattern type.
     */
    public List<Pair<ChunkerDyeColor, ChunkerBannerPattern>> getPatterns() {
        return patterns;
    }

    /**
     * Get the base color.
     *
     * @return optional of the base color if it's present.
     */
    public Optional<ChunkerDyeColor> getBase() {
        return base;
    }

    /**
     * Set the base color of the banner.
     *
     * @param base an optional holding the color, if absent the base color isn't present.
     */
    public void setBase(Optional<ChunkerDyeColor> base) {
        this.base = base;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BannerBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getBase(), that.getBase()) && Objects.equals(getPatterns(), that.getPatterns());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBase(), getPatterns());
    }

    @Override
    public String toString() {
        return "BannerBlockEntity{" +
                "base=" + getBase() +
                ", patterns=" + getPatterns() +
                '}';
    }
}
