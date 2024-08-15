package com.hivemc.chunker.conversion.encoding.bedrock.base.reader.pretransform.handlers;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.ConnectableBlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;

import java.util.Map;

/**
 * Calculations the directions for fire to be present.
 */
public class BedrockFirePreTransformHandler implements ConnectableBlockPreTransformHandler {
    final Map<Direction, BlockState<Bool>> DIRECTION_TO_STATE = Map.of(
            Direction.NORTH, VanillaBlockStates.NORTH,
            Direction.EAST, VanillaBlockStates.EAST,
            Direction.SOUTH, VanillaBlockStates.SOUTH,
            Direction.WEST, VanillaBlockStates.WEST,
            Direction.UP, VanillaBlockStates.UP
    );

    @Override
    public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        for (Direction direction : Direction.ALL) {
            ChunkerBlockIdentifier relative = getRelative(column, neighbours, x, y, z, direction);
            boolean connection = canConnect(blockIdentifier, direction, relative);

            // Update the state with the connection status
            if (direction == Direction.DOWN) {
                if (connection) {
                    // Use false for all states if there is a block below
                    return blockIdentifier.copyWith(VanillaBlockStates.NORTH, Bool.FALSE)
                            .copyWith(VanillaBlockStates.EAST, Bool.FALSE)
                            .copyWith(VanillaBlockStates.SOUTH, Bool.FALSE)
                            .copyWith(VanillaBlockStates.WEST, Bool.FALSE)
                            .copyWith(VanillaBlockStates.UP, Bool.FALSE);
                }
            } else {
                BlockState<Bool> state = DIRECTION_TO_STATE.get(direction);
                blockIdentifier = blockIdentifier.copyWith(state, connection ? Bool.TRUE : Bool.FALSE);
            }
        }

        // Return the block identifier to be updated
        return blockIdentifier;
    }

    public boolean canConnect(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative) {
        return relative.getType().isAllFacesSolid(); // If it's solid we allow fire to connect
    }
}
