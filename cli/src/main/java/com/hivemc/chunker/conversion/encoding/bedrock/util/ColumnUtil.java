package com.hivemc.chunker.conversion.encoding.bedrock.util;

import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

import java.util.OptionalInt;

/**
 * Utilities for Chunker Columns for Bedrock.
 */
public class ColumnUtil {
    /**
     * Get the highest block which has light or is a slab.
     *
     * @param column the input column.
     * @param x      the local position X to look at.
     * @param z      the local position Z to look at.
     * @return an integer if a block was found of the Y otherwise empty.
     */
    public static OptionalInt getHighestLitOrSlabBlock(ChunkerColumn column, int x, int z) {
        // Start from the top
        ObjectSortedSet<Byte2ObjectMap.Entry<ChunkerChunk>> chunkSet = column.getChunks().byte2ObjectEntrySet();
        if (chunkSet.isEmpty()) return OptionalInt.empty(); // Empty chunk

        // Iterate through the chunk
        ObjectBidirectionalIterator<Byte2ObjectMap.Entry<ChunkerChunk>> iterator = chunkSet.iterator(chunkSet.last());
        while (iterator.hasPrevious()) {
            ChunkerChunk chunk = iterator.previous().getValue();
            if (!chunk.isEmpty()) {
                byte[][][] blockLight = chunk.getBlockLight();
                byte[][][] skyLight = chunk.getSkyLight();
                int y = 15;
                while (y >= 0) {
                    ChunkerBlockIdentifier identifier = chunk.getPalette().get(x, y, z, ChunkerBlockIdentifier.AIR);
                    if (!identifier.isAir()) {
                        // Fetch the light or if it's a slab
                        int light = (blockLight == null ? 0 : blockLight[x][y][z]) + (skyLight == null ? 0 : skyLight[x][y][z]);
                        boolean litOrSlab = (light != 0 || identifier.getType().getStates().contains(VanillaBlockStates.SLAB_TYPE));
                        if (litOrSlab) {
                            // Return this height
                            return OptionalInt.of((chunk.getY() << 4) | y);
                        }
                    }
                    y--;
                }
            }
        }

        return OptionalInt.empty(); // Empty chunk
    }
}
