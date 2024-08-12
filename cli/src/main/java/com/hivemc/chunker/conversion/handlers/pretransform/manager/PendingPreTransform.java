package com.hivemc.chunker.conversion.handlers.pretransform.manager;

import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.entity.EntityPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;

/**
 * A pre-transformation that needs to be run when neighbouring columns have been loaded.
 */
public interface PendingPreTransform {
    /**
     * Pending block transformation.
     *
     * @param x                   the global X co-ordinate.
     * @param y                   the global Y co-ordinate.
     * @param z                   the global Z co-ordinate.
     * @param preTransformHandler the handler to call when the required columns are loaded.
     */
    record PendingBlockPreTransform(int x, int y, int z,
                                    BlockPreTransformHandler preTransformHandler) implements PendingPreTransform {
    }

    /**
     * Pending entity transformation.
     *
     * @param entity              the entity to run the transformation on.
     * @param preTransformHandler the handler to call when the required columns are loaded.
     * @param <T>                 the type of the entity.
     */
    record PendingEntityPreTransform<T extends Entity>(T entity,
                                                       EntityPreTransformHandler<T> preTransformHandler) implements PendingPreTransform {
    }
}

