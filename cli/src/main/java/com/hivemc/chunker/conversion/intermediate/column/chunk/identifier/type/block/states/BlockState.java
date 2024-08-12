package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states;

import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.ComparableItemProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A block state is a property for a block that can have one of several values. A value is always required for the
 * property otherwise a default is used.
 *
 * @param <T> the type held by the block state.
 */
public class BlockState<T extends BlockStateValue> implements ComparableItemProperty<T> {
    private final String name;
    private final T defaultValue;
    private final T[] values;

    /**
     * Create a new block state.
     *
     * @param name         the name of the block state (used for debug and comparison), this should be unique for the
     *                     values.
     * @param defaultValue the default value for the state if it's not present.
     * @param values       an array of valid values that can be used for this block state.
     */
    public BlockState(String name, T defaultValue, T[] values) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.values = values;
    }

    /**
     * Create a new block state.
     *
     * @param name         the name of the block state (used for debug and comparison), this should be unique for the
     *                     values.
     * @param defaultValue the default value for the state if it's not present.
     * @param values       a supplier which provides a list of values for the state.
     */
    public BlockState(String name, T defaultValue, Supplier<T[]> values) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.values = values.get();
    }

    /**
     * Get the name of this block state, this is the internal name and not necessarily the name used in
     * Java/Bedrock.
     *
     * @return the name of this block state.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the values which are supported by this block state.
     *
     * @return an array of the possible values.
     */
    public T[] getValues() {
        return values;
    }

    @Override
    public int compareTo(@NotNull ComparableItemProperty<?> other) {
        if (other instanceof BlockState<?> o) {
            // Compare name
            int value = getName().compareTo(o.getName());
            if (value != 0) return value;
        }

        // Compare class name
        return getClass().getName().compareTo(other.getClass().getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockState<?> that)) return false;
        return Objects.equals(getName(), that.getName()) && Objects.deepEquals(getValues(), that.getValues());
    }

    @Override
    public int hashCode() {
        // Values are not included in hashCode as name should be unique (but this is enforced for equals)
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "BlockState{" +
                "name='" + name + '\'' +
                ", default='" + defaultValue + '\'' +
                ", values=" + Arrays.toString(values) +
                '}';
    }

    /**
     * Get the default value which should be used for this state if it's not present.
     *
     * @return the default value which is within the values.
     */
    public T getDefault() {
        return defaultValue;
    }
}
