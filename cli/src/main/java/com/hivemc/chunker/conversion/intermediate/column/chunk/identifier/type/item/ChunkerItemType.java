package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifierType;

/**
 * Interface to represent any item type (not block), since items don't have states, this also can be used as an
 * identifier.
 */
public interface ChunkerItemType extends ChunkerItemStackIdentifierType, ChunkerItemStackIdentifier {
    @Override
    default ChunkerItemStackIdentifierType getItemStackType() {
        return this; // The item can be used as its own type since it doesn't have state.
    }
}
