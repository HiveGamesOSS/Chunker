package com.hivemc.chunker.conversion.encoding.bedrock.util;

import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;

import java.util.AbstractSet;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Compact set of the chunks present in one Bedrock region.
 *
 * <p>A region has exactly 1,024 possible chunk positions, so a bit set avoids retaining one object and one hash-table
 * slot for every chunk in large worlds. Chunk coordinate objects are created only while the region is being processed.</p>
 */
public final class BedrockChunkCoordinateSet extends AbstractSet<ChunkCoordPair> {
    private static final int REGION_SIZE = 32;
    private final RegionCoordPair region;
    private final BitSet chunks = new BitSet(REGION_SIZE * REGION_SIZE);

    public BedrockChunkCoordinateSet(RegionCoordPair region) {
        this.region = region;
    }

    /**
     * Add a world chunk position without allocating a coordinate object.
     */
    public boolean add(int chunkX, int chunkZ) {
        if ((chunkX >> 5) != region.regionX() || (chunkZ >> 5) != region.regionZ()) {
            throw new IllegalArgumentException("Chunk is outside region " + region);
        }

        int index = localIndex(chunkX, chunkZ);
        boolean changed = !chunks.get(index);
        chunks.set(index);
        return changed;
    }

    @Override
    public boolean add(ChunkCoordPair position) {
        return add(position.chunkX(), position.chunkZ());
    }

    @Override
    public boolean contains(Object value) {
        if (!(value instanceof ChunkCoordPair position)) return false;
        if ((position.chunkX() >> 5) != region.regionX() || (position.chunkZ() >> 5) != region.regionZ()) return false;
        return chunks.get(localIndex(position.chunkX(), position.chunkZ()));
    }

    @Override
    public boolean remove(Object value) {
        if (!contains(value)) return false;
        ChunkCoordPair position = (ChunkCoordPair) value;
        chunks.clear(localIndex(position.chunkX(), position.chunkZ()));
        return true;
    }

    @Override
    public void clear() {
        chunks.clear();
    }

    @Override
    public Iterator<ChunkCoordPair> iterator() {
        return new Iterator<>() {
            private int next = chunks.nextSetBit(0);
            private int lastReturned = -1;

            @Override
            public boolean hasNext() {
                return next >= 0;
            }

            @Override
            public ChunkCoordPair next() {
                if (next < 0) throw new NoSuchElementException();

                int current = next;
                lastReturned = current;
                next = chunks.nextSetBit(current + 1);
                return region.getChunk(current >>> 5, current & 31);
            }

            @Override
            public void remove() {
                if (lastReturned < 0) throw new IllegalStateException();
                chunks.clear(lastReturned);
                lastReturned = -1;
            }
        };
    }

    @Override
    public int size() {
        return chunks.cardinality();
    }

    private static int localIndex(int chunkX, int chunkZ) {
        return ((chunkX & 31) << 5) | (chunkZ & 31);
    }
}
