package com.hivemc.chunker.conversion.encoding.bedrock.base.reader.pretransform.handlers;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockGroups;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Half;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the missing states for doors, fetching the other states from the other half of the door.
 */
public class BedrockDoorPreTransformHandler implements BlockPreTransformHandler {
    @Override
    public Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        // No edges needed
        return Collections.emptySet();
    }

    @Override
    public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        Half currentHalf = blockIdentifier.getState(VanillaBlockStates.HALF);
        ChunkerBlockIdentifier otherHalf = getRelative(
                column,
                neighbours,
                x,
                y,
                z,
                currentHalf == Half.TOP ? Direction.DOWN : Direction.UP
        );

        // Check it can connect
        if (!canConnect(blockIdentifier, otherHalf)) return blockIdentifier; // Can't merge states

        // If it's the bottom copy the hinge bit from the top
        if (currentHalf == Half.BOTTOM) {
            return blockIdentifier.copyWith(VanillaBlockStates.DOOR_HINGE, otherHalf.getState(VanillaBlockStates.DOOR_HINGE));
        } else {
            // If it's the top copy the open_bit and direction
            return blockIdentifier
                    .copyWith(VanillaBlockStates.OPEN, otherHalf.getState(VanillaBlockStates.OPEN))
                    .copyWith(VanillaBlockStates.FACING_HORIZONTAL, otherHalf.getState(VanillaBlockStates.FACING_HORIZONTAL));
        }
    }

    public boolean canConnect(ChunkerBlockIdentifier source, ChunkerBlockIdentifier relative) {
        return ChunkerVanillaBlockGroups.DOORS.contains(relative.getType());
    }
}
