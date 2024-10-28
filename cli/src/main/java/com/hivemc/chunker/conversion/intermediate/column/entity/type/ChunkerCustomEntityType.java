package com.hivemc.chunker.conversion.intermediate.column.entity.type;

/**
 * A custom entity type that isn't included in Vanilla.
 */
public class ChunkerCustomEntityType implements ChunkerEntityType {
    private final String identifier;

    /**
     * Create a new custom entity type with a namespaced identifier.
     *
     * @param identifier the namespaced identifier.
     */
    public ChunkerCustomEntityType(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get a namespaced identifier for this entity type.
     *
     * @return the namespaced identifier in the format namespace:identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "ChunkerCustomEntityType{" +
                "identifier='" + getIdentifier() + '\'' +
                '}';
    }
}
