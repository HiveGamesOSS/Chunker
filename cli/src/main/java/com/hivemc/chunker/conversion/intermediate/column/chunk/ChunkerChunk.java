package com.hivemc.chunker.conversion.intermediate.column.chunk;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.SingleValuePalette;
import org.jetbrains.annotations.Nullable;

/**
 * A chunk which is part of a ChunkerColumn.
 */
public class ChunkerChunk {
    private byte y;
    private Palette<ChunkerBlockIdentifier> palette = SingleValuePalette.chunk(ChunkerBlockIdentifier.AIR);
    private byte[][] @Nullable [] blockLight;
    private byte[][] @Nullable [] skyLight;

    /**
     * Create a new chunk.
     *
     * @param y the position of the chunk.
     */
    public ChunkerChunk(byte y) {
        this.y = y;
    }

    /**
     * Get the Y position of this chunk in the column.
     *
     * @return the Y position.
     */
    public byte getY() {
        return y;
    }

    /**
     * Set the Y position of this chunk in the column.
     *
     * @param y the Y position.
     */
    public void setY(byte y) {
        this.y = y;
    }

    /**
     * Get the palette used for this chunk.
     *
     * @return the palette used for this chunk, empty if there is no blocks.
     */
    public Palette<ChunkerBlockIdentifier> getPalette() {
        return palette;
    }

    /**
     * Set the palette used for this chunk.
     *
     * @param palette the palette used for this chunk, null if there is no blocks.
     */
    public void setPalette(Palette<ChunkerBlockIdentifier> palette) {
        this.palette = palette;
    }

    /**
     * Get the block light for the chunk.
     *
     * @return a 16x16x16 array or null if not present.
     */
    public byte[][] @Nullable [] getBlockLight() {
        return blockLight;
    }

    /**
     * Set the block light for the chunk.
     *
     * @param blockLight a 16x16x16 array or null if not present.
     */
    public void setBlockLight(byte[][] @Nullable [] blockLight) {
        this.blockLight = blockLight;
    }

    /**
     * Get the skylight for the chunk.
     *
     * @return a 16x16x16 array or null if not present.
     */
    public byte[][] @Nullable [] getSkyLight() {
        return skyLight;
    }

    /**
     * Set the skylight for the chunk.
     *
     * @param skyLight a 16x16x16 array or null if not present.
     */
    public void setSkyLight(byte[][] @Nullable [] skyLight) {
        this.skyLight = skyLight;
    }

    /**
     * Check whether this chunk is empty.
     *
     * @return true if the block only contains air.
     */
    public boolean isEmpty() {
        if (palette.isEmpty()) return true;

        // If the palette contains a not air block, it's not empty
        return !palette.containsValue(block -> !block.isAir());
    }
}
