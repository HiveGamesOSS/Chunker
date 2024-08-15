package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * Whether this block connects to other adjacent redstone.
 */
public enum RedstoneConnection implements BlockStateValue {
    /**
     * The redstone connection goes upwards.
     */
    UP,
    /**
     * The redstone connection is on the same level.
     */
    SIDE,
    /**
     * There is no redstone connection.
     */
    NONE
}
