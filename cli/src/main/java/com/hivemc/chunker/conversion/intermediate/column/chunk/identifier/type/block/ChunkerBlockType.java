package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifierType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;

import java.util.Set;

/**
 * The type of block, e.g. STONE
 */
public interface ChunkerBlockType extends ChunkerItemStackIdentifierType {
    /**
     * The RGB color of the block to use for preview.
     *
     * @return the RGB color as an integer.
     */
    int getRGBColor();

    /**
     * Whether all faces of the block are solid.
     *
     * @return true if all faces are solid.
     */
    boolean isAllFacesSolid();

    /**
     * Get the states supported by this block.
     *
     * @return a set of the block states.
     */
    Set<BlockState<?>> getStates();
}
