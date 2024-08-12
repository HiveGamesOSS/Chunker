package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionBottleType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionType;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;

/**
 * Represents a Cauldron Block Entity (Bedrock only).
 */
public class CauldronBlockEntity extends BlockEntity {
    private ChunkerPotionType potionType = ChunkerPotionType.EMPTY;
    private ChunkerPotionBottleType potionBottleType = ChunkerPotionBottleType.UNKNOWN;
    @Nullable
    private Color customColor;

    /**
     * Get the type of the potion which is in the cauldron.
     *
     * @return the type or EMPTY if there is none.
     */
    public ChunkerPotionType getPotionType() {
        return potionType;
    }

    /**
     * Set the type of the potion which is in the cauldron.
     *
     * @param potionType the type or EMPTY if there is none.
     */
    public void setPotionType(ChunkerPotionType potionType) {
        this.potionType = potionType;
    }

    /**
     * Get the type of bottle used on the cauldron.
     *
     * @return the bottle type or UNKNOWN if none.
     */
    public ChunkerPotionBottleType getPotionBottleType() {
        return potionBottleType;
    }

    /**
     * Set the type of bottle used on the cauldron.
     *
     * @param potionBottleType the bottle type or UNKNOWN if none.
     */
    public void setPotionBottleType(ChunkerPotionBottleType potionBottleType) {
        this.potionBottleType = potionBottleType;
    }

    /**
     * Get the custom color to use for this cauldron.
     *
     * @return the color if present otherwise null.
     */
    @Nullable
    public Color getCustomColor() {
        return customColor;
    }

    /**
     * Set the custom color for this cauldron.
     *
     * @param customColor the color otherwise null if not present.
     */
    public void setCustomColor(@Nullable Color customColor) {
        this.customColor = customColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CauldronBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return getPotionType() == that.getPotionType() && getPotionBottleType() == that.getPotionBottleType() && Objects.equals(getCustomColor(), that.getCustomColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPotionType(), getPotionBottleType(), getCustomColor());
    }
}
