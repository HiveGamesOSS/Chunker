package com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.legacy.handlers;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockGroups;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirectionHorizontal;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Half;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.StairShape;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Calculate the shape of the stair based on neighbouring stairs.
 */
public class JavaLegacyStairShapePreTransformHandler implements BlockPreTransformHandler {
    @Override
    public Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        // Require all edges, as this doesn't do an in-depth search of what edges are needed
        return Set.of(Edge.ALL_EDGES);
    }

    @Override
    public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        // Get block relative to direction
        FacingDirectionHorizontal facingDirection = blockIdentifier.getState(VanillaBlockStates.FACING_HORIZONTAL);
        ChunkerBlockIdentifier relative = getRelative(
                column,
                neighbours,
                x,
                y,
                z,
                Objects.requireNonNull(facingDirection).asDirection()
        );

        // Check if there is a target in the facing direction and the half value matches
        // Based on vanilla Java logic
        if (canConnect(blockIdentifier, relative)) {
            // Opposite face
            FacingDirectionHorizontal relativeFacing = relative.getState(VanillaBlockStates.FACING_HORIZONTAL);
            Direction relativeDirection = Objects.requireNonNull(relativeFacing).asDirection();

            // Fetch the relative opposite
            ChunkerBlockIdentifier relativeOpposite = getRelative(column, neighbours, x, y, z, relativeDirection);
            if (facingDirection.isAdjacent(relativeFacing) && canTakeShape(relative, relativeOpposite)) {
                StairShape stairShape = relativeFacing.equals(facingDirection.rotateAntiClockwise()) ? StairShape.OUTER_LEFT : StairShape.OUTER_RIGHT;
                return blockIdentifier.copyWith(VanillaBlockStates.STAIR_SHAPE, stairShape);
            }
        }

        // Check if the block in the opposite direction can connect
        ChunkerBlockIdentifier oppositeRelative = getRelative(
                column,
                neighbours,
                x,
                y,
                z,
                facingDirection.asDirection().getOpposite()
        );

        // Check if there is a target in the facing direction and the half value matches
        if (canConnect(blockIdentifier, oppositeRelative)) {
            FacingDirectionHorizontal relativeFacing = oppositeRelative.getState(VanillaBlockStates.FACING_HORIZONTAL);
            Direction relativeDirectionOpposite = Objects.requireNonNull(relativeFacing).asDirection().getOpposite();

            // Fetch the relative opposite
            ChunkerBlockIdentifier relativeOpposite = getRelative(column, neighbours, x, y, z, relativeDirectionOpposite);
            if (facingDirection.isAdjacent(relativeFacing) && canTakeShape(oppositeRelative, relativeOpposite)) {
                StairShape stairShape = relativeFacing.equals(facingDirection.rotateAntiClockwise()) ? StairShape.INNER_LEFT : StairShape.INNER_RIGHT;
                return blockIdentifier.copyWith(VanillaBlockStates.STAIR_SHAPE, stairShape);
            }
        }

        // Return default
        return blockIdentifier.copyWith(VanillaBlockStates.STAIR_SHAPE, StairShape.STRAIGHT);
    }

    public boolean canConnect(ChunkerBlockIdentifier source, ChunkerBlockIdentifier relative) {
        if (!ChunkerVanillaBlockGroups.STAIRS.contains(relative.getType())) return false;

        // Grab the half of the stairs
        Half sourceHalf = source.getState(VanillaBlockStates.HALF);

        // If they're the same slab type they can connect
        return sourceHalf == relative.getState(VanillaBlockStates.HALF);
    }

    public boolean canTakeShape(ChunkerBlockIdentifier source, ChunkerBlockIdentifier relative) {
        // It can take shape if it's not stairs
        if (!ChunkerVanillaBlockGroups.STAIRS.contains(relative.getType())) return true;

        // It can take shape if the direction differs
        FacingDirectionHorizontal sourceDirection = source.getState(VanillaBlockStates.FACING_HORIZONTAL);
        if (sourceDirection != relative.getState(VanillaBlockStates.FACING_HORIZONTAL)) return true;

        // Grab the half of the stairs
        Half sourceHalf = source.getState(VanillaBlockStates.HALF);

        // It can take shape if the half differs
        return sourceHalf != relative.getState(VanillaBlockStates.HALF);
    }
}
