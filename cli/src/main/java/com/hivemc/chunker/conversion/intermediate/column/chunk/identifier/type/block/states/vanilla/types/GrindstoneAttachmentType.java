package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;

/**
 * How the grindstone is attached to blocks around it.
 */
public enum GrindstoneAttachmentType implements BlockStateValue {
    FLOOR,
    WALL,
    CEILING,
    MULTIPLE
}
