package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a Campfire Block Entity.
 */
public class CampfireBlockEntity extends BlockEntity {
    private final ChunkerItemStack[] items = new ChunkerItemStack[4];
    private final int[] cookingTimes = new int[4];

    /**
     * Get the modifiable array of items held by this block.
     *
     * @return an array with a length of 4 where null/air indicates no item.
     */
    public ChunkerItemStack[] getItems() {
        return items;
    }

    /**
     * Get the modifiable array of cooking times of items held by this block.
     *
     * @return an array with a length of 4 of cooking times.
     */
    public int[] getCookingTimes() {
        return cookingTimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CampfireBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.deepEquals(getItems(), that.getItems()) && Objects.deepEquals(getCookingTimes(), that.getCookingTimes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), Arrays.hashCode(getItems()), Arrays.hashCode(getCookingTimes()));
    }
}
