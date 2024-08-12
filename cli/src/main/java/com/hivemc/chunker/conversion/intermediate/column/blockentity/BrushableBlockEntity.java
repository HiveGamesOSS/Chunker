package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a Brushable Block Entity (Suspicious Sand / Gravel).
 */
public class BrushableBlockEntity extends BlockEntity {
    private byte brushDirection;
    private int brushCount;
    @Nullable
    private ChunkerItemStack item;

    /**
     * Get the face which is being brushed.
     *
     * @return the face or 6 if no face is being brushed.
     */
    public byte getBrushDirection() {
        return brushDirection;
    }

    /**
     * Set the face which is being brushed.
     *
     * @param brushDirection the face or 6 if no face is being brushed.
     */
    public void setBrushDirection(byte brushDirection) {
        this.brushDirection = brushDirection;
    }

    /**
     * Get the number of brush uses.
     *
     * @return the number of brush uses.
     */
    public int getBrushCount() {
        return brushCount;
    }

    /**
     * Set the number of brush uses.
     *
     * @param brushCount the number of brush uses.
     */
    public void setBrushCount(int brushCount) {
        this.brushCount = brushCount;
    }

    /**
     * Get the item revealed after this block is brushed.
     *
     * @return the item, null/air if not present.
     */
    @Nullable
    public ChunkerItemStack getItem() {
        return item;
    }

    /**
     * Set the item revealed after this block is brushed.
     *
     * @param item the item, null/air if not present.
     */
    public void setItem(@Nullable ChunkerItemStack item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrushableBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return getBrushDirection() == that.getBrushDirection() && getBrushCount() == that.getBrushCount() && Objects.equals(getItem(), that.getItem());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBrushDirection(), getBrushCount(), getItem());
    }
}
