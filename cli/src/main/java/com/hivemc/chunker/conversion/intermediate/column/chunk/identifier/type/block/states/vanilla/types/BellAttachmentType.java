package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * The side which the bell attaches to nearby blocks.
 */
public enum BellAttachmentType implements BlockStateValue {
    FLOOR,
    CEILING,
    SINGLE_WALL,
    DOUBLE_WALL
}
