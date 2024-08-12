package com.hivemc.chunker.util;

import java.util.Comparator;

/**
 * Block Position data class.
 *
 * @param x the X position.
 * @param y the Y position.
 * @param z the Z position.
 */
public record BlockPosition(int x, int y, int z) {
    /**
     * Comparator which sorts BlockPosition by Y then X then Z.
     */
    public static final Comparator<BlockPosition> BY_Y_X_Z = Comparator.comparing(BlockPosition::y)
            .thenComparing(BlockPosition::x)
            .thenComparing(BlockPosition::z);
}
