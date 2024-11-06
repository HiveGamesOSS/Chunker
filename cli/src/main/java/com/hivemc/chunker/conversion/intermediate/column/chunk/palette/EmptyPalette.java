package com.hivemc.chunker.conversion.intermediate.column.chunk.palette;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A palette which contains no entries.
 *
 * @param <T> the type held by the palette.
 */
public class EmptyPalette<T> implements Palette<T> {
    private static final EmptyPalette<?> CHUNK_INSTANCE = new EmptyPalette<>(16);
    private final int dimensionSize;

    /**
     * Create a new empty palette.
     *
     * @param dimensionSize the dimension size, used for creating writable palettes.
     */
    private EmptyPalette(int dimensionSize) {
        this.dimensionSize = dimensionSize;
    }

    /**
     * Get an instance of the empty palette for chunks (16x16x16).
     *
     * @param <T> the type held by the palette.
     * @return an empty palette for chunks.
     */
    @SuppressWarnings("unchecked")
    public static <T> EmptyPalette<T> chunk() {
        return (EmptyPalette<T>) EmptyPalette.CHUNK_INSTANCE;
    }

    /**
     * Get an instance of the empty palette.
     *
     * @param dimensionSize the dimension size of the palette, used for creating writeable palettes.
     * @param <T>           the type held by the palette.
     * @return an empty palette with the dimension size.
     */
    public static <T> EmptyPalette<T> instance(int dimensionSize) {
        return new EmptyPalette<>(dimensionSize);
    }

    @Override
    public int getKeyCount() {
        return 0;
    }

    @Override
    public T getKey(int keyIndex, T defaultValue) {
        return defaultValue;
    }

    @Override
    public T get(int x, int y, int z, T defaultValue) {
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Palette<U> map(Function<T, U> mappingFunction) {
        return (Palette<U>) this; // Dimensions stay the same
    }

    @Override
    public boolean containsValue(Predicate<T> predicate) {
        return false;
    }

    @Override
    public boolean containsKey(Predicate<T> predicate) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true; // Always empty
    }

    @Override
    public WriteablePalette<T> asWriteable() {
        return new ShortBasedPalette<>(0, dimensionSize);
    }

    @Override
    public EmptyPalette<T> compact(T defaultValue) {
        return this; // Does nothing
    }

    @Override
    public Palette<T> copy() {
        return this; // Does nothing
    }
}
