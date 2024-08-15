package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;

import java.util.Optional;

/**
 * Represents the type behind a ChunkerItemStackIdentifier which is the type of item / block.
 */
public interface ChunkerItemStackIdentifierType {
    /**
     * Get the block entity class that can be held by this type as an item / block.
     *
     * @return the block entity class if present, if absent this item doesn't support any.
     */
    Optional<Class<? extends BlockEntity>> getBlockEntityClass();
}
