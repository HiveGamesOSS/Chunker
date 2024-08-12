package com.hivemc.chunker.mapping.identifier.states;

import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;

/**
 * Represents a StateValue which has an integer value.
 */
public class StateValueInt implements StateValue<Integer> {
    public static final StateValueInt ZERO = new StateValueInt(0);
    private final int value;

    /**
     * Create a new StateValueInt with a given value.
     *
     * @param value the value to use.
     */
    public StateValueInt(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateValueInt that = (StateValueInt) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Get the value held by this StateValue.
     *
     * @return the value being held.
     */
    public int getValue() {
        return value;
    }

    @Override
    public Integer getBoxed() {
        return value;
    }

    @Override
    public Tag<?> toNBT() {
        return new IntTag(value);
    }
}
