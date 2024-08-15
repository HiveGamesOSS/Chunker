package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The direction this block was placed in, with multiple rotations per face.
 */
public enum Orientation implements BlockStateValue {
    DOWN_EAST,
    DOWN_NORTH,
    DOWN_SOUTH,
    DOWN_WEST,
    UP_EAST,
    UP_NORTH,
    UP_SOUTH,
    UP_WEST,
    WEST_UP,
    EAST_UP,
    NORTH_UP,
    SOUTH_UP
}
