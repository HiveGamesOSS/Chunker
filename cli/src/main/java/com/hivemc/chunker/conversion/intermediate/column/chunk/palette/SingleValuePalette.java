package com.hivemc.chunker.conversion.intermediate.column.chunk.palette;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class SingleValuePalette<T> implements Palette<T> {
    private final int dimensionSize;
    private final T value;

    public SingleValuePalette(int dimensionSize, T value) {
        this.dimensionSize = dimensionSize;
        this.value = value;
    }

    public static <T> SingleValuePalette<T> chunk(T identifier) {
        return new SingleValuePalette<>(16, identifier);
    }

    @Override
    public int getKeyCount() {
        return 1;
    }

    @Override
    public T getKey(int keyIndex, T defaultValue) {
        return value;
    }

    @Override
    public T get(int x, int y, int z, T defaultValue) {
        return value;
    }

    @Override
    public <U> Palette<U> map(Function<T, U> mappingFunction) {
        return new SingleValuePalette<>(dimensionSize, mappingFunction.apply(value));
    }

    @Override
    public boolean containsValue(Predicate<T> predicate) {
        return predicate.test(value);
    }

    @Override
    public boolean containsKey(Predicate<T> predicate) {
        return predicate.test(value);
    }

    @Override
    public boolean isEmpty() {
        return false; // Always has a value
    }

    @Override
    public WriteablePalette<T> asWriteable() {
        List<T> keys = new ArrayList<>(1);
        keys.add(value);

        // Return a new ShortBasedPalette
        return new ShortBasedPalette<>(keys, new short[dimensionSize][dimensionSize][dimensionSize]);
    }

    @Override
    public SingleValuePalette<T> compact(T defaultValue) {
        return this; // Does nothing
    }

    @Override
    public Palette<T> copy() {
        return new SingleValuePalette<>(dimensionSize, value);
    }
}
