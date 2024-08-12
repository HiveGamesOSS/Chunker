package com.hivemc.chunker.conversion.handlers.pretransform;

import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import org.jetbrains.annotations.Nullable;

/**
 * A neighbouring column edge.
 */
public enum Edge {
    POSITIVE_X(1, 0),
    NEGATIVE_X(-1, 0),
    POSITIVE_Z(0, 1),
    NEGATIVE_Z(0, -1);

    /**
     * An array of all the edges
     */
    public static final Edge[] ALL_EDGES = new Edge[]{POSITIVE_X, NEGATIVE_X, POSITIVE_Z, NEGATIVE_Z};

    private final int x;
    private final int z;

    Edge(int x, int z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Create an edge from an offset.
     *
     * @param x the offset in X (-1 to 1).
     * @param z the offset in Z (-1 to 1).
     * @return null if the edge is not supported, otherwise the edge.
     */
    @Nullable
    public static Edge fromOffset(int x, int z) {
        for (Edge edge : ALL_EDGES) {
            if (x == edge.getX() && z == edge.getZ()) return edge;
        }

        // Not supported
        return null;
    }

    /**
     * Get the X offset for this edge direction.
     *
     * @return the offset x.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the Z offset for this edge direction.
     *
     * @return the offset z.
     */
    public int getZ() {
        return z;
    }

    /**
     * Get the co-ordinates of a chunk in the direction of this edge.
     *
     * @param pair the input.
     * @return the relative chunk.
     */
    public ChunkCoordPair getRelative(ChunkCoordPair pair) {
        return new ChunkCoordPair(pair.chunkX() + x, pair.chunkZ() + z);
    }

    /**
     * Get the edge opposite.
     *
     * @return the opposite edge.
     */
    public Edge getOpposite() {
        return switch (this) {
            case POSITIVE_X -> NEGATIVE_X;
            case POSITIVE_Z -> NEGATIVE_Z;
            case NEGATIVE_X -> POSITIVE_X;
            case NEGATIVE_Z -> POSITIVE_Z;
        };
    }
}
