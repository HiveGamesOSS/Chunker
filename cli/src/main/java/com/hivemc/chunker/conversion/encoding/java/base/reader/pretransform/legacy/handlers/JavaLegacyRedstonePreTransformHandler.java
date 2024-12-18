package com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.legacy.handlers;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockGroups;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirectionHorizontal;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.RedstoneConnection;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Calculate the directions that redstone should connect to.
 */
public class JavaLegacyRedstonePreTransformHandler implements BlockPreTransformHandler {
    final Map<Direction, BlockState<RedstoneConnection>> DIRECTION_TO_STATE = Map.of(
            Direction.NORTH, VanillaBlockStates.REDSTONE_NORTH,
            Direction.EAST, VanillaBlockStates.REDSTONE_EAST,
            Direction.SOUTH, VanillaBlockStates.REDSTONE_SOUTH,
            Direction.WEST, VanillaBlockStates.REDSTONE_WEST
    );

    @Override
    public Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        return Set.of(Edge.ALL_EDGES);
    }

    @Override
    public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        for (Direction direction : Direction.ALL_HORIZONTAL) {
            RedstoneConnection redstoneConnection = calculateConnection(blockIdentifier, direction, column, neighbours, x, y, z);

            // Update the state with the connection status
            BlockState<RedstoneConnection> state = DIRECTION_TO_STATE.get(direction);
            blockIdentifier = blockIdentifier.copyWith(state, redstoneConnection);
        }

        // Get the states which were applied
        boolean north = blockIdentifier.getState(VanillaBlockStates.REDSTONE_NORTH) != RedstoneConnection.NONE;
        boolean east = blockIdentifier.getState(VanillaBlockStates.REDSTONE_EAST) != RedstoneConnection.NONE;
        boolean south = blockIdentifier.getState(VanillaBlockStates.REDSTONE_SOUTH) != RedstoneConnection.NONE;
        boolean west = blockIdentifier.getState(VanillaBlockStates.REDSTONE_WEST) != RedstoneConnection.NONE;
        boolean northSouthNone = !north && !south;
        boolean eastWestNone = !east && !west;

        // Apply any additional connections (based on Java edition logic)
        if (!north && eastWestNone) {
            blockIdentifier = blockIdentifier.copyWith(VanillaBlockStates.REDSTONE_NORTH, RedstoneConnection.SIDE);
        }
        if (!east && northSouthNone) {
            blockIdentifier = blockIdentifier.copyWith(VanillaBlockStates.REDSTONE_EAST, RedstoneConnection.SIDE);
        }
        if (!south && eastWestNone) {
            blockIdentifier = blockIdentifier.copyWith(VanillaBlockStates.REDSTONE_SOUTH, RedstoneConnection.SIDE);
        }
        if (!west && northSouthNone) {
            blockIdentifier = blockIdentifier.copyWith(VanillaBlockStates.REDSTONE_WEST, RedstoneConnection.SIDE);
        }

        // Return the block identifier to be updated
        return blockIdentifier;
    }

    public RedstoneConnection calculateConnection(ChunkerBlockIdentifier source, Direction direction, ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z) {
        // Get relative block
        ChunkerBlockIdentifier relative = getRelative(column, neighbours, x, y, z, direction);
        if (canConnect(source, direction, relative)) {
            return RedstoneConnection.SIDE;
        } else {
            // Check if the connection goes upwards (requires the above block to be transparent)
            ChunkerBlockIdentifier target = getRelative(column, neighbours, x, y + 1, z, direction);
            ChunkerBlockIdentifier above = getRelative(column, neighbours, x, y, z, Direction.UP);
            if (canConnect(source, direction, target) && !above.getItemStackType().isAllFacesSolid()) {
                return RedstoneConnection.UP;
            }

            // Check if the connection goes downward (requires the relative block to be transparent)
            target = getRelative(column, neighbours, x, y - 1, z, direction);
            if (canConnect(source, direction, target) && !relative.getItemStackType().isAllFacesSolid()) {
                return RedstoneConnection.SIDE;
            }
        }

        return RedstoneConnection.NONE;
    }

    public boolean canConnect(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative) {
        if (!ChunkerVanillaBlockGroups.REDSTONE_CONNECTABLE.contains(relative.getType())) return false;

        // Special case for repeater (needs to be same axis as repeater)
        if (relative.getType() == ChunkerVanillaBlockType.REPEATER) {
            FacingDirectionHorizontal repeaterDirection = relative.getState(VanillaBlockStates.FACING_HORIZONTAL);
            return !Objects.requireNonNull(repeaterDirection).isAdjacent(direction.asFacingDirectionHorizontal());
        }

        // Direction needs to match observer direction
        if (relative.getType() == ChunkerVanillaBlockType.OBSERVER) {
            return direction.asFacingDirection() == relative.getState(VanillaBlockStates.FACING_ALL);
        }

        // Otherwise, return true
        return true;
    }
}
