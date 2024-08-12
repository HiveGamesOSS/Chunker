package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The half of the slab which is displayed.
 */
public enum SlabType implements BlockStateValue {
    /**
     * Just the top half is displayed.
     */
    TOP,
    /**
     * The bottom half is displayed.
     */
    BOTTOM,
    /**
     * Both halves are displayed.
     */
    DOUBLE
}
