package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * Whether the block is connected to another shelf.
 */
public enum SideChain implements BlockStateValue {
    UNCONNECTED,
    RIGHT,
    CENTER,
    LEFT
}
