package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The part of the chest which this is.
 */
public enum ChestType implements BlockStateValue {
    /**
     * The chest is just a single chest and not a double chest.
     */
    SINGLE,
    /**
     * Relative to the direction this is the left side.
     */
    LEFT,
    /**
     * Relative to the direction this is the right side.
     */
    RIGHT
}
