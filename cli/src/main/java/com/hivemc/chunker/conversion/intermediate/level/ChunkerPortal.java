package com.hivemc.chunker.conversion.intermediate.level;

import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.util.BlockPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A nether portal inside the world.
 */
public class ChunkerPortal {
    private Dimension dimension;
    private int x;
    private int y;
    private int z;
    private byte width;
    private byte xa;
    private byte za;

    /**
     * Create a new portal.
     *
     * @param dimension the dimension the portal is inside.
     * @param x         the bottom corner X of the portal.
     * @param y         the bottom corner Y of the portal.
     * @param z         the bottom corner Z of the portal.
     * @param width     the width of the portal.
     * @param xa        the direction of the portal in X.
     * @param za        the direction of the portal in Z.
     */
    public ChunkerPortal(Dimension dimension, int x, int y, int z, byte width, byte xa, byte za) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.xa = xa;
        this.za = za;
    }

    /**
     * The dimension the portal is inside.
     *
     * @return the dimension.
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * Set the dimension the portal is inside.
     *
     * @param dimension the dimension.
     */
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    /**
     * The position of a bottom corner of the portal.
     *
     * @return the co-ordinate of the bottom corner in the X axis.
     */
    public int getX() {
        return x;
    }

    /**
     * Set the X co-ordinate of the bottom corner of the portal.
     *
     * @param x the x co-ordinate.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * The position of a bottom corner of the portal.
     *
     * @return the co-ordinate of the bottom corner in the Y axis.
     */
    public int getY() {
        return y;
    }

    /**
     * Set the Y co-ordinate of the bottom corner of the portal.
     *
     * @param y the y co-ordinate.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * The position of a bottom corner of the portal.
     *
     * @return the co-ordinate of the bottom corner in the Z axis.
     */
    public int getZ() {
        return z;
    }

    /**
     * Set the Z co-ordinate of the bottom corner of the portal.
     *
     * @param z the z co-ordinate.
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * Get the width of the portal in the direction of the portal.
     *
     * @return the number of blocks wide the portal is.
     */
    public byte getWidth() {
        return width;
    }

    /**
     * Set the width of the portal in blocks.
     *
     * @param width the width of the portal (number of nether portal blocks).
     */
    public void setWidth(byte width) {
        this.width = width;
    }

    /**
     * Get the direction of the portal in the X axis (1, 0, -1).
     *
     * @return the direction of the portal in the X axis.
     */
    public byte getXa() {
        return xa;
    }

    /**
     * Set the direction of the portal in the X axis (1, 0, -1).
     *
     * @param xa the direction, either 1, 0 or -1.
     */
    public void setXa(byte xa) {
        this.xa = xa;
    }

    /**
     * Get the direction of the portal in the Z axis (1, 0, -1).
     *
     * @return the direction of the portal in the Z axis.
     */
    public byte getZa() {
        return za;
    }

    /**
     * Set the direction of the portal in the Z axis (1, 0, -1).
     *
     * @param za the direction, either 1, 0 or -1.
     */
    public void setZa(byte za) {
        this.za = za;
    }

    /**
     * Given a chunk get a list of all the horizontal blocks which the portal can be present in.
     *
     * @param chunkCoordPair the input chunk.
     * @return a list of horizontal blocks (height is unknown of the portal).
     */
    public List<BlockPosition> getHorizontalBlocks(ChunkCoordPair chunkCoordPair) {
        int originChunkX = x >> 4;
        int originChunkZ = z >> 4;
        int offsetChunkX = (x + ((width - 1) * getXa())) >> 4;
        int offsetChunkZ = (z + ((width - 1) * getZa())) >> 4;

        // Calculate the min/max chunk range of this portal
        int minChunkX = Math.min(originChunkX, offsetChunkX);
        int minChunkZ = Math.min(originChunkZ, offsetChunkZ);
        int maxChunkX = Math.max(originChunkX, offsetChunkX);
        int maxChunkZ = Math.max(originChunkZ, offsetChunkZ);

        // Ensure it's within bounds
        if (chunkCoordPair.chunkX() < minChunkX || chunkCoordPair.chunkZ() < minChunkZ) return Collections.emptyList();
        if (chunkCoordPair.chunkX() > maxChunkX || chunkCoordPair.chunkZ() > maxChunkZ) return Collections.emptyList();

        // Calculate all the block positions this portal is present in width
        List<BlockPosition> blockPositions = new ArrayList<>();

        // Calculate the otherside of the portal horizontally
        int offsetX = x + ((width - 1) * getXa());
        int offsetZ = z + ((width - 1) * getZa());

        // Calculate the min/max range of this portal
        int minX = Math.min(x, offsetX);
        int minZ = Math.min(z, offsetZ);
        int maxX = Math.max(x, offsetX);
        int maxZ = Math.max(z, offsetZ);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!chunkCoordPair.isInside(x, z)) continue;

                // Add position
                blockPositions.add(new BlockPosition(x, y, z));
            }
        }
        return blockPositions;
    }
}
