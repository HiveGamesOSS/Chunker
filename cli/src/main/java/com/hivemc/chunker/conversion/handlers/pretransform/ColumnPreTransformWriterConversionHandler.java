package com.hivemc.chunker.conversion.handlers.pretransform;

import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;

import java.util.function.Supplier;

/**
 * A Column handler which calls the writer pre-transform method to ensure that the column knows which edges need
 * solving before the actual pre-transform handler is called.
 */
public class ColumnPreTransformWriterConversionHandler implements ColumnConversionHandler {
    private final Supplier<PreTransformManager> preTransformManagerGetter;
    private final ColumnConversionHandler delegate;
    private final boolean preTransformAllowed;

    /**
     * Create a new column pre transform writer handler.
     *
     * @param preTransformManagerGetter the getter to call for getting the pre-transform manager (it can return null).
     * @param delegate                  the delegate to call after transformation.
     * @param preTransformAllowed       whether transform is allowed.
     */
    public ColumnPreTransformWriterConversionHandler(Supplier<PreTransformManager> preTransformManagerGetter, ColumnConversionHandler delegate, boolean preTransformAllowed) {
        this.preTransformManagerGetter = preTransformManagerGetter;
        this.delegate = delegate;
        this.preTransformAllowed = preTransformAllowed;
    }

    @Override
    public void convertColumn(ChunkerColumn column) {
        // Call pre-transform solver
        PreTransformManager preTransformManager = preTransformManagerGetter.get();
        if (preTransformManager != null) {
            preTransformManager.solve(column, preTransformAllowed);
        }

        // Call delegate
        delegate.convertColumn(column);
    }

    @Override
    public void flushRegion(RegionCoordPair regionCoordPair) {
        // Call delegate
        delegate.flushRegion(regionCoordPair);
    }

    @Override
    public void flushColumns() {
        // Call delegate
        delegate.flushColumns();
    }
}
