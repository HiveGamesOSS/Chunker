package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The shape of a stair, the default being straight.
 */
public enum StairShape implements BlockStateValue {
    STRAIGHT,
    INNER_LEFT,
    INNER_RIGHT,
    OUTER_LEFT,
    OUTER_RIGHT
}
