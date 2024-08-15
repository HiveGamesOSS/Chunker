package com.hivemc.chunker.mapping.identifier.states;

import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;

import java.util.Objects;

/**
 * Represents a StateValue which has a String value.
 */
public class StateValueString implements StateValue<String> {
    private final String value;

    /**
     * Create a new StateValueString with a given value.
     *
     * @param value the value to use.
     */
    public StateValueString(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateValueString that = (StateValueString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Get the value held by this StateValue.
     *
     * @return the value being held.
     */
    public String getValue() {
        return value;
    }

    @Override
    public String getBoxed() {
        return value;
    }

    @Override
    public Tag<?> toNBT() {
        return new StringTag(value);
    }
}
