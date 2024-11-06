package com.hivemc.chunker.conversion.intermediate.column.chunk.palette;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A palette which uses shorts for indexing the palette keys and storing those keys.
 *
 * @param <T> the type held by the palette.
 */
public class ShortBasedPalette<T> implements WriteablePalette<T> {
    private final List<T> keys;
    private final short[][][] values;

    /**
     * Create a short based palette from a pre-indexed list of keys and values.
     *
     * @param keys   the keys.
     * @param values the values which point to indexes in the keys.
     */
    public ShortBasedPalette(List<T> keys, short[][][] values) {
        this.keys = keys;
        this.values = values;
    }

    /**
     * Create a new short based palette.
     *
     * @param keySizeHint   the hint to use for the size of the keys.
     * @param dimensionSize the dimension length, e.g. 16 for a chunk, to be used for the data.
     */
    public ShortBasedPalette(int keySizeHint, int dimensionSize) {
        this(new ArrayList<>(keySizeHint), new short[dimensionSize][dimensionSize][dimensionSize]);
    }

    /**
     * Get the backing value array which is storing the indexes of the keys.
     *
     * @return the backing short array.
     */
    public short[][][] getValues() {
        return values;
    }

    @Override
    public int getKeyCount() {
        return keys.size();
    }

    @Override
    public T getKey(int keyIndex, T defaultValue) {
        if (keyIndex < 0 || keyIndex >= keys.size()) return defaultValue;
        return keys.get(keyIndex);
    }

    @Override
    public T get(int x, int y, int z, T defaultValue) {
        return getKey(values[x][y][z], defaultValue);
    }

    @Override
    public short getOrCreateKey(T entry) {
        Preconditions.checkNotNull(entry);
        short index = (short) keys.indexOf(entry);
        if (index == -1) {
            index = (short) keys.size();
            keys.add(entry);
        }
        return index;
    }

    @Override
    public void set(int x, int y, int z, T key) {
        values[x][y][z] = getOrCreateKey(key);
    }

    @Override
    public void setPaletteIndex(int x, int y, int z, short paletteIndex) {
        values[x][y][z] = paletteIndex;
    }

    @Override
    public <U> Palette<U> map(Function<T, U> mappingFunction) {
        ArrayList<U> newKeys = new ArrayList<>(keys.size());
        for (T key : keys) {
            newKeys.add(mappingFunction.apply(key));
        }
        return new ShortBasedPalette<>(newKeys, values);
    }

    @Override
    public boolean containsValue(Predicate<T> predicate) {
        int size = keys.size();

        // If no keys, then it's empty
        if (size == 0) {
            return false;
        }

        // If single key, match against the first value
        if (size == 1) {
            return predicate.test(keys.get(0));
        }

        // Otherwise loop through the keys and identify indexes
        ShortSet matchedIndexes = null;
        for (short i = 0; i < size; i++) {
            T identifier = keys.get(i);
            if (predicate.test(identifier)) {
                // Create the HashSet
                if (matchedIndexes == null) {
                    matchedIndexes = new ShortOpenHashSet(1);
                }

                // Add the index
                matchedIndexes.add(i);
            }
        }

        // Exit early if no matching identifiers
        if (matchedIndexes == null) {
            return false;
        }

        for (short[][] yz : values) {
            for (short[] z : yz) {
                for (short entry : z) {
                    if (entry < 0 || entry >= size) continue; // Ignore invalid blocks

                    // Return if it's a match
                    if (matchedIndexes.contains(entry)) return true;
                }
            }
        }

        // No matches
        return false;
    }

    @Override
    public boolean containsKey(Predicate<T> predicate) {
        for (T key : keys) {
            if (predicate.test(key)) return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    @Override
    public WriteablePalette<T> asWriteable() {
        return this; // We should already be writeable
    }

    @Override
    public Palette<T> compact(T defaultValue) {
        // Quick shortcut for empty palette
        if (keys.isEmpty()) return EmptyPalette.instance(values.length);

        // Create a remapping array for old index -> new index for keys
        short[] paletteRemapping = new short[keys.size()];
        Arrays.fill(paletteRemapping, (short) -1);

        // Create a copy of the old keys
        List<T> oldKeys = new ArrayList<>(keys);
        keys.clear();

        // First collect all the used palette ids
        for (short[][] yz : values) {
            for (short[] z : yz) {
                for (int i = 0; i < z.length; i++) {
                    short oldKeyIndex = z[i];
                    boolean validKeyIndex = oldKeyIndex >= 0 && oldKeyIndex < oldKeys.size();
                    short newKeyIndex = validKeyIndex ? paletteRemapping[oldKeyIndex] : -1;

                    // If the newKeyIndex is -1, we need to check if this is already in our new keys or needs adding
                    if (newKeyIndex == -1) {
                        T oldValue = validKeyIndex ? oldKeys.get(oldKeyIndex) : defaultValue;
                        newKeyIndex = (short) keys.indexOf(oldValue);

                        // If it wasn't in the new keys, create a key for it
                        if (newKeyIndex == -1) {
                            newKeyIndex = (short) keys.size();
                            keys.add(oldValue);
                        }

                        // Update the mappings with this new old -> new index (if the key is valid)
                        if (validKeyIndex) {
                            paletteRemapping[oldKeyIndex] = newKeyIndex;
                        }
                    }

                    // Update the value
                    z[i] = newKeyIndex;
                }
            }
        }

        // Check if there's an easier way to represent this palette
        if (keys.size() == 1) return new SingleValuePalette<>(values.length, keys.get(0));

        // Otherwise return this
        return this;
    }

    @Override
    public ShortBasedPalette<T> copy() {
        // Loop through and copy each array
        short[][][] copiedValues = new short[values.length][][];
        for (int x = 0; x < values.length; x++) {
            copiedValues[x] = new short[values[x].length][];
            for (int y = 0; y < values[x].length; y++) {
                copiedValues[x][y] = values[x][y].clone();
            }
        }
        return new ShortBasedPalette<>(new ArrayList<>(keys), copiedValues);
    }
}
