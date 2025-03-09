package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The current mode for a test block.
 */
public enum TestBlockMode implements BlockStateValue {
    START,
    LOG,
    FAIL,
    ACCEPT
}
