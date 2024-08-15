package com.hivemc.chunker.conversion.intermediate.level;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap;

/**
 * A player inside the world (currently only used for the local player).
 */
public class ChunkerLevelPlayer {
    private final Dimension dimension;
    private final double positionX;
    private final double positionY;
    private final double positionZ;
    private final double motionX;
    private final double motionY;
    private final double motionZ;
    private final float yaw;
    private final float pitch;
    private final Byte2ObjectAVLTreeMap<ChunkerItemStack> inventory;
    private final int gameType;

    /**
     * Create a new player.
     *
     * @param dimension the dimension the player is inside.
     * @param positionX the position of the feet in the X axis.
     * @param positionY the position of the feet in the Y axis.
     * @param positionZ the position of the feet in the Z axis.
     * @param motionX   the current motion in the X axis.
     * @param motionY   the current motion in the Y axis.
     * @param motionZ   the current motion in the Z axis.
     * @param yaw       the yaw rotation of the player.
     * @param pitch     the pitch rotation of the player.
     * @param inventory the players inventory (including armor and off-hand).
     * @param gameType  the current gamemode of the player.
     */
    public ChunkerLevelPlayer(Dimension dimension, double positionX, double positionY, double positionZ, double motionX, double motionY, double motionZ, float yaw, float pitch, Byte2ObjectAVLTreeMap<ChunkerItemStack> inventory, int gameType) {
        this.dimension = dimension;
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.inventory = inventory;
        this.gameType = gameType;
    }

    /**
     * Get the dimension the player is inside.
     *
     * @return the dimension.
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * Get the X position of the player.
     *
     * @return the X position of the player at feet level.
     */
    public double getPositionX() {
        return positionX;
    }

    /**
     * Get the Y position of the player.
     *
     * @return the Y position of the player at feet level.
     */
    public double getPositionY() {
        return positionY;
    }

    /**
     * Get the Z position of the player.
     *
     * @return the Z position of the player at feet level.
     */
    public double getPositionZ() {
        return positionZ;
    }

    /**
     * Get any applied motion in the X axis.
     *
     * @return any applied motion in the X.
     */
    public double getMotionX() {
        return motionX;
    }

    /**
     * Get any applied motion in the Y axis.
     *
     * @return any applied motion in the Y.
     */
    public double getMotionY() {
        return motionY;
    }

    /**
     * Get any applied motion in the Z axis.
     *
     * @return any applied motion in the Z.
     */
    public double getMotionZ() {
        return motionZ;
    }

    /**
     * Get the yaw rotation of the player.
     *
     * @return the yaw.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Get the pitch rotation of the player.
     *
     * @return the pitch.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Get the contents of the inventory ordered by slot as a byte.
     * Where 100-103 is the armor and 150 is the off-hand.
     *
     * @return the inventory as a map.
     */
    public Byte2ObjectAVLTreeMap<ChunkerItemStack> getInventory() {
        return inventory;
    }

    /**
     * Get the gamemode of the player as an integer.
     *
     * @return the gamemode of the player.
     */
    public int getGameType() {
        return gameType;
    }

    @Override
    public String toString() {
        return "ChunkerLevelPlayer{" +
                "dimension=" + getDimension() +
                ", positionX=" + getPositionX() +
                ", positionY=" + getPositionY() +
                ", positionZ=" + getPositionZ() +
                ", motionX=" + getMotionX() +
                ", motionY=" + getMotionY() +
                ", motionZ=" + getMotionZ() +
                ", yaw=" + getYaw() +
                ", pitch=" + getPitch() +
                ", inventory=" + getInventory() +
                ", gameType=" + getGameType() +
                '}';
    }
}
