package com.hivemc.chunker.resolver.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A handler which defines how to read and write a property.
 *
 * @param <S> the state type which is used for reading/writing types to and from, usually CompoundTag.
 * @param <V> the value type of the property.
 */
public interface PropertyHandler<S, V> {
    /**
     * Attempt to read the value from the state.
     *
     * @param state the state to read the value from.
     * @return the optional with the value if it was present.
     */
    Optional<V> read(@NotNull S state);

    /**
     * Attempt to write the value to the state.
     *
     * @param state the current state value
     * @param value the value to write.
     */
    void write(@NotNull S state, @NotNull V value);

    /**
     * Get the default value to use for reading/writing.
     *
     * @param state the current state value
     * @return null if a value shouldn't be written, otherwise this parameter will be supplied to the write method.
     */
    @Nullable
    default V getDefaultValue(@NotNull S state) {
        return null; // By default, return null indicating no default value for writing
    }
}
