package com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;

import java.util.Map;
import java.util.Set;

/**
 * A handler which connects every horizontal direction.
 */
public interface HorizontalConnectableBlockPreTransformHandler extends BlockPreTransformHandler {
    /**
     * A map of direction to the vanilla block state.
     */
    Map<BlockPreTransformHandler.Direction, BlockState<Bool>> DIRECTION_TO_STATE = Map.of(
            BlockPreTransformHandler.Direction.NORTH, VanillaBlockStates.NORTH,
            BlockPreTransformHandler.Direction.EAST, VanillaBlockStates.EAST,
            BlockPreTransformHandler.Direction.SOUTH, VanillaBlockStates.SOUTH,
            BlockPreTransformHandler.Direction.WEST, VanillaBlockStates.WEST
    );

    @Override
    default Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        // We need to check every direction
        return calculateEdges(x, y, z, Direction.ALL_HORIZONTAL);
    }

    @Override
    default ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        for (Direction direction : Direction.ALL_HORIZONTAL) {
            ChunkerBlockIdentifier relative = getRelative(column, neighbours, x, y, z, direction);
            boolean connection = canConnect(blockIdentifier, direction, relative);

            // Update the state with the connection status
            BlockState<Bool> state = DIRECTION_TO_STATE.get(direction);
            blockIdentifier = blockIdentifier.copyWith(state, connection ? Bool.TRUE : Bool.FALSE);
        }

        // Return the block identifier to be updated
        return blockIdentifier;
    }

    /**
     * Whether this block can connect to another.
     *
     * @param source    the source block.
     * @param direction the direction of connection.
     * @param relative  the relative block.
     * @return true if the block connects.
     */
    boolean canConnect(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative);
}
