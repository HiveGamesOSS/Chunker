package com.hivemc.chunker.conversion.encoding.bedrock.base.reader.pretransform.handlers;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;

import java.util.Map;
import java.util.Set;

/**
 * Connect a stem to a valid nearby growing block.
 */
public class BedrockStemPreTransformHandler implements BlockPreTransformHandler {
    private final Set<ChunkerBlockType> connectable;

    /**
     * Create a new stem pre transform handler.
     *
     * @param connectable the blocks the stem can connect to.
     */
    public BedrockStemPreTransformHandler(Set<ChunkerBlockType> connectable) {
        this.connectable = connectable;
    }

    @Override
    public Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        return calculateEdges(x, y, z, Direction.ALL_HORIZONTAL);
    }

    @Override
    public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        // Connect the sides if they're needed
        for (Direction direction : Direction.ALL_HORIZONTAL) {
            ChunkerBlockIdentifier relative = getRelative(column, neighbours, x, y, z, direction);

            // Connect to the first face
            if (canConnect(blockIdentifier, direction, relative)) {
                return blockIdentifier.copyWith(VanillaBlockStates.FACING_HORIZONTAL, direction.asFacingDirectionHorizontal());
            }
        }

        // Return the block identifier to be updated
        return blockIdentifier;
    }

    public boolean canConnect(ChunkerBlockIdentifier source, Direction direction, ChunkerBlockIdentifier relative) {
        return connectable.contains(relative.getType());
    }
}
