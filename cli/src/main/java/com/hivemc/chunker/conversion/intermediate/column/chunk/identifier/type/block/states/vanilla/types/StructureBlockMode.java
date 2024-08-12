package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The current mode for a structure block.
 */
public enum StructureBlockMode implements BlockStateValue {
    SAVE,
    LOAD,
    CORNER,
    DATA,
    INVALID,
    EXPORT
}
