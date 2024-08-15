package com.hivemc.chunker.conversion.encoding.bedrock.base.reader.pretransform.handlers;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.block.BlockPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Calculate whether the plant connects to a block below.
 */
public class BedrockHangingPlantPreTransformHandler implements BlockPreTransformHandler {
    private final boolean hanging;
    private final Function<ChunkerBlockIdentifier, ChunkerBlockIdentifier> outputFunction;
    private final Set<ChunkerBlockType> connectable;

    /**
     * Create a new hanging plant handler.
     *
     * @param hanging        whether the block is checked below or above, true for below.
     * @param outputFunction the function to calculate the new identifier.
     * @param connectable    blocks which can be connected in the hanging direction.
     */
    public BedrockHangingPlantPreTransformHandler(boolean hanging, Function<ChunkerBlockIdentifier, ChunkerBlockIdentifier> outputFunction, Set<ChunkerBlockType> connectable) {
        this.hanging = hanging;
        this.outputFunction = outputFunction;
        this.connectable = connectable;
    }

    @Override
    public Set<Edge> getRequiredEdges(ChunkerColumn column, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        return Collections.emptySet();
    }

    @Override
    public ChunkerBlockIdentifier handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, int x, int y, int z, ChunkerBlockIdentifier blockIdentifier) {
        // Fetch the relative block
        ChunkerBlockIdentifier relative = getRelative(column, neighbours, x, y, z, hanging ? Direction.DOWN : Direction.UP);

        // Check if it's a valid connection
        if (connectable.contains(relative.getType())) {
            // Update with the function
            return outputFunction.apply(blockIdentifier);
        } else {
            return blockIdentifier; // Return original
        }
    }
}
