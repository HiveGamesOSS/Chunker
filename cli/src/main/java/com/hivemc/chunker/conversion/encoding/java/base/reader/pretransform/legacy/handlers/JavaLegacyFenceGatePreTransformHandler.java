package com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.legacy.handlers;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockGroups;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;

import java.util.Map;
import java.util.Set;

/**
 * Calculates the in_wall bit which is present when there is walls adjacent.
 */
public class JavaLegacyFenceGatePreTransformHandler implements BlockPreTransformHandler {
    @Override
    public Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        // Check adjacent edges
        return calculateEdges(x, y, z, Direction.getAdjacentFaces(blockIdentifier.getState(VanillaBlockStates.FACING_HORIZONTAL)));
    }

    @Override
    public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        for (Direction adjacent : Direction.getAdjacentFaces(blockIdentifier.getState(VanillaBlockStates.FACING_HORIZONTAL))) {
            ChunkerBlockIdentifier relative = getRelative(column, neighbours, x, y, z, adjacent);
            if (ChunkerVanillaBlockGroups.WALLS.contains(relative.getType())) {
                return blockIdentifier.copyWith(VanillaBlockStates.IN_WALL, Bool.TRUE);
            }
        }
        return blockIdentifier.copyWith(VanillaBlockStates.IN_WALL, Bool.FALSE);
    }
}
