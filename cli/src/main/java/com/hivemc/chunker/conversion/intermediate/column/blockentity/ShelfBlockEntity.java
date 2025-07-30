package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a Shelf Block Entity.
 */
public class ShelfBlockEntity extends BlockEntity {
    private final ChunkerItemStack[] items = new ChunkerItemStack[3];

    /**
     * Get the modifiable array of items held by this block.
     *
     * @return an array with a length of 3 where null/air indicates a missing item.
     */
    public ChunkerItemStack[] getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShelfBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.deepEquals(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), Arrays.hashCode(getItems()));
    }
}
