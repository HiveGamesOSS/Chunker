package com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.legacy.JavaLegacyReaderPreTransformManager;
import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Half;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Calculate the top half states for a tall plant.
 */
public class JavaLegacyTallPlantsPreTransformHandler implements BlockPreTransformHandler {
    @Override
    public Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        // No edges needed
        return Collections.emptySet();
    }

    @Override
    public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        Half currentHalf = blockIdentifier.getState(VanillaBlockStates.HALF);

        // If it's the top copy the plant from the bottom
        if (currentHalf == Half.TOP) {
            ChunkerBlockIdentifier otherHalf = getRelative(
                    column,
                    neighbours,
                    x,
                    y,
                    z,
                    Direction.DOWN
            );

            // Check it can connect
            if (!canConnect(blockIdentifier, otherHalf)) return blockIdentifier; // Can't merge states

            // Copy the block and use this as top half
            return otherHalf.copyWith(VanillaBlockStates.HALF, Half.TOP);
        }
        return blockIdentifier;
    }

    public boolean canConnect(ChunkerBlockIdentifier source, ChunkerBlockIdentifier relative) {
        return JavaLegacyReaderPreTransformManager.TALL_PLANTS.contains(relative.getType());
    }
}
