package com.hivemc.chunker.conversion.intermediate.column.blockentity;

/**
 * Represents a block entity not known to Chunker but allowed by NBT copying.
 */
public class UnknownBlockEntity extends BlockEntity {
    private final String type;

    /**
     * Create a new unknown block entity.
     *
     * @param type the chunker type of the unknown block entity.
     */
    public UnknownBlockEntity(String type) {
        this.type = type;
    }

    /**
     * Get the format specific type identifier for the block identifier.
     *
     * @return the format specific name.
     */
    public String getType() {
        return type;
    }
}
