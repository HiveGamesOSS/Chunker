package com.hivemc.chunker.conversion.encoding.base.resolver.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;

import java.util.Set;

/**
 * An interface to indicate the BlockEntityHandler generates a block entity for matching block types before they are
 * processed.
 * This is useful for block entities that are not present in the format and need generating before column submission.
 *
 * @param <T> the type of the block entity.
 */
public interface GenerateBeforeProcessBlockEntityHandler<T extends BlockEntity> {
    /**
     * The block types to check for that trigger generating a block entity.
     *
     * @return a set of block types to match against.
     */
    Set<ChunkerBlockType> getGenerateBeforeProcessBlockTypes();

    /**
     * Generate a block entity and fill in any data specific to the type.
     *
     * @param column          the column the block entity is inside.
     * @param x               the global X co-ordinate.
     * @param y               the global Y co-ordinate.
     * @param z               the global Z co-ordinate.
     * @param blockEntity     the block entity which was constructed with basic position data.
     * @param blockIdentifier the identifier of the block which was matched.
     */
    void generateBeforeProcess(ChunkerColumn column, int x, int y, int z, T blockEntity, ChunkerBlockIdentifier blockIdentifier);
}
