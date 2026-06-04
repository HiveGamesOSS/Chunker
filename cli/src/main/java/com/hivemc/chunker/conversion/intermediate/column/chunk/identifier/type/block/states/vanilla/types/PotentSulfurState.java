package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The current state of a potent sulfur block.
 */
public enum PotentSulfurState implements BlockStateValue {
    DRY,
    WET,
    DORMANT,
    ERUPTING,
    CONTINUOUS
}
