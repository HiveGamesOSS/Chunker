package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The height of a wall.
 */
public enum WallHeight implements BlockStateValue {
    /**
     * No connection to the nearby block.
     */
    NONE,
    /**
     * Partial connection to a nearby block.
     */
    LOW,
    /**
     * Tall connection to the nearby block (usually when there is a wall above).
     */
    TALL
}
