package com.hivemc.chunker.conversion.encoding.base.resolver.entity;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;

import java.util.Set;

/**
 * An interface to indicate the EntityHandler generates an entity for matching block types before they are processed.
 * This is useful for entities that are not present in the format and need generating before column submission.
 *
 * @param <T> the type of the entity.
 */
public interface GenerateBeforeProcessEntityHandler<T extends Entity> {
    /**
     * The block types to check for that trigger generating an entity.
     *
     * @return a set of block types to match against.
     */
    Set<ChunkerBlockType> getGenerateBeforeProcessBlockTypes();

    /**
     * Generate an entity and fill in any data specific to the type.
     *
     * @param column          the column the entity is inside.
     * @param x               the global X co-ordinate.
     * @param y               the global Y co-ordinate.
     * @param z               the global Z co-ordinate.
     * @param entity          the entity which was constructed with basic position data.
     * @param blockIdentifier the identifier of the block which was matched.
     */
    void generateBeforeProcess(ChunkerColumn column, int x, int y, int z, T entity, ChunkerBlockIdentifier blockIdentifier);
}
