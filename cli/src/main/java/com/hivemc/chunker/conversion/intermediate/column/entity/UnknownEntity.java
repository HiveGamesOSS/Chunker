package com.hivemc.chunker.conversion.intermediate.column.entity;

import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an entity not known to Chunker but allowed by NBT copying.
 */
public class UnknownEntity extends Entity {
    @Nullable
    private final ChunkerVanillaEntityType type;

    /**
     * Create a new unknown entity.
     *
     * @param type the chunker type of the unknown entity.
     */
    public UnknownEntity(@Nullable ChunkerVanillaEntityType type) {
        this.type = type;
    }

    @Override
    public ChunkerVanillaEntityType getEntityType() {
        return type;
    }
}
