package com.hivemc.chunker.conversion.intermediate.column.chunk.palette;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A palette represents a storage of values (in three dimensions) and a list of keys which the values reference to.
 *
 * @param <T> the key type being used by the palette.
 */
public interface Palette<T> {
    /**
     * Get the number of keys used inside this palette.
     *
     * @return the number of keys used inside the palette.
     */
    int getKeyCount();

    /**
     * Get a key from an index in the palette.
     *
     * @param keyIndex     the index of the key to fetch.
     * @param defaultValue the value to return if the key is outside the keys held by the palette.
     * @return the value or the default value if it wasn't in the keys.
     */
    T getKey(int keyIndex, @Nullable T defaultValue);

    /**
     * Get a key from an index in the palette.
     *
     * @param keyIndex the index of the key to fetch.
     * @return the value or null if it wasn't in the keys.
     */
    @Nullable
    default T getKey(int keyIndex) {
        return getKey(keyIndex, null);
    }

    /**
     * Get the key used at a specific location in the palette.
     *
     * @param x            the first dimension of the palette.
     * @param y            the second dimension of the palette.
     * @param z            the third dimension of the palette.
     * @param defaultValue the default value if a value isn't found.
     * @return the key if it was found otherwise the default value.
     */
    T get(int x, int y, int z, @Nullable T defaultValue);

    /**
     * Get the key used at a specific location in the palette.
     *
     * @param x the first dimension of the palette.
     * @param y the second dimension of the palette.
     * @param z the third dimension of the palette.
     * @return the key if it was found otherwise null.
     */
    @Nullable
    default T get(int x, int y, int z) {
        return get(x, y, z, null);
    }

    /**
     * Map the current palette to a new palette using a mapping function.
     *
     * @param mappingFunction the function to use when transforming each key in the palette.
     * @param <U>             the new type being used for the palette.
     * @return a new copy of the palette using the mapping function (note the values may use the same array reference).
     */
    <U> Palette<U> map(Function<T, U> mappingFunction);

    /**
     * Test whether the data of this palette contains a value.
     * This ensures that the key is used somewhere in the palette while testing.
     *
     * @param key the key to check for.
     * @return true if a value is present somewhere in the palette.
     */
    default boolean containsValue(T key) {
        return containsValue(entry -> entry.equals(key));
    }

    /**
     * Test whether the data of this palette contains a value matching a predicate.
     * This ensures that the key is used somewhere in the palette while testing.
     *
     * @param predicate the predicate to test each key against then find a value.
     * @return true if a value is present somewhere in the palette.
     */
    boolean containsValue(Predicate<T> predicate);

    /**
     * Test whether the keys of this palette contain a value.
     * Note: the key matching may not be used, use {{@link #containsValue(T)}} if that is required.
     *
     * @param key the key to check for.
     * @return true if a single key matches.
     */
    default boolean containsKey(T key) {
        return containsKey(entry -> entry.equals(key));
    }

    /**
     * Test whether the keys of this palette contain a value matching a predicate.
     * Note: the key matching may not be used, use {{@link #containsValue(Predicate)}} if that is required.
     *
     * @param predicate the predicate to test each key against.
     * @return true if a single key matches.
     */
    boolean containsKey(Predicate<T> predicate);

    /**
     * Check if the palette is empty.
     *
     * @return true if the palette has no keys.
     */
    boolean isEmpty();

    /**
     * Get a version of the current palette which ensures it can be modified.
     *
     * @return the current instance if it's already writeable, otherwise an allocated copy.
     */
    WriteablePalette<T> asWriteable();

    /**
     * Attempt to compact the current palette, removing any keys which are not used and removing invalid values.
     *
     * @param defaultValue the value to be used if an invalid value is replaced.
     * @return the instance after compaction, it may be the same object or a different one.
     */
    Palette<T> compact(T defaultValue);

    /**
     * Create a new instance of the current palette with copied keys and values.
     *
     * @return create a clone of the palette, which ensures values and keys are new arrays.
     */
    Palette<T> copy();
}
