package com.hivemc.chunker.mapping.identifier.states;

import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.primitive.ByteTag;

/**
 * Represents a StateValue which has a boolean value of either true or false.
 */
public class StateValueBoolean implements StateValue<Boolean> {
    /**
     * A constant value representing a false value.
     */
    public static final StateValueBoolean FALSE = new StateValueBoolean(false);
    /**
     * A constant value representing a true value.
     */
    public static final StateValueBoolean TRUE = new StateValueBoolean(true);
    private final boolean value;

    private StateValueBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateValueBoolean that = (StateValueBoolean) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value ? 1 : 0;
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }

    /**
     * Get the value held by this StateValue.
     *
     * @return the value being held.
     */
    public boolean getValue() {
        return value;
    }

    @Override
    public Boolean getBoxed() {
        return value;
    }

    @Override
    public Tag<?> toNBT() {
        return new ByteTag((byte) (value ? 1 : 0));
    }
}
