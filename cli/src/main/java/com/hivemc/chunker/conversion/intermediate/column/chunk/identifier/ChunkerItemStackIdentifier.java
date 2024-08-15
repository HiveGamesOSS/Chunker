package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerVanillaItemType;

/**
 * Represents a block (with states) or item as an identifier.
 */
public interface ChunkerItemStackIdentifier {
    /**
     * Get the item stack identifier type used for this identifier.
     *
     * @return the identifier type, e.g. {@link ChunkerVanillaBlockType}, {@link ChunkerVanillaItemType}
     */
    ChunkerItemStackIdentifierType getItemStackType();

    /**
     * Check whether this identifier is air.
     *
     * @return true if it is air.
     */
    default boolean isAir() {
        return getItemStackType() == ChunkerVanillaBlockType.AIR;
    }
}
