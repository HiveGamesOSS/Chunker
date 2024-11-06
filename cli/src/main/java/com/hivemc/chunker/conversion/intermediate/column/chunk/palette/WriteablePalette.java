package com.hivemc.chunker.conversion.intermediate.column.chunk.palette;

/**
 * A palette which allows modification.
 *
 * @param <T> the type held by the palette.
 */
public interface WriteablePalette<T> extends Palette<T> {
    /**
     * Get or create a key in the palette.
     *
     * @param entry the entry to create if it doesn't exist.
     * @return the index of the entry in the key list.
     */
    short getOrCreateKey(T entry);

    /**
     * Set a specific index in the palette to a value.
     *
     * @param x   the x position.
     * @param y   the y position.
     * @param z   the z position.
     * @param key the key to set at the value, creating it if it doesn't exist.
     */
    void set(int x, int y, int z, T key);

    /**
     * Set a specific index in the palette to a paletteIndex.
     *
     * @param x            the x position.
     * @param y            the y position.
     * @param z            the z position.
     * @param paletteIndex the index of the key which this entry points to.
     */
    void setPaletteIndex(int x, int y, int z, short paletteIndex);

    /**
     * Copy all the keys and values of the current palette.
     *
     * @return create a clone of the writeable palette, which ensures values and keys is a new array.
     */
    @Override
    WriteablePalette<T> copy();
}
