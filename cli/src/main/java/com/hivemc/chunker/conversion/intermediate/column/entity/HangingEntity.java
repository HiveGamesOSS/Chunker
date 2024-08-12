package com.hivemc.chunker.conversion.intermediate.column.entity;

import java.util.Objects;

/**
 * An entity which attaches to the face of a block.
 */
public abstract class HangingEntity extends Entity {
    private int tileX;
    private int tileY;
    private int tileZ;

    /**
     * Get the block X which this entity is attached to.
     *
     * @return the x co-ordinate.
     */
    public int getTileX() {
        return tileX;
    }

    /**
     * Set the block X which this entity is attached to.
     *
     * @param tileX the x co-ordinate.
     */
    public void setTileX(int tileX) {
        this.tileX = tileX;
    }

    /**
     * Get the block Y which this entity is attached to.
     *
     * @return the y co-ordinate.
     */
    public int getTileY() {
        return tileY;
    }

    /**
     * Set the block Y which this entity is attached to.
     *
     * @param tileY the y co-ordinate.
     */
    public void setTileY(int tileY) {
        this.tileY = tileY;
    }

    /**
     * Get the block Z which this entity is attached to.
     *
     * @return the z co-ordinate.
     */
    public int getTileZ() {
        return tileZ;
    }

    /**
     * Set the block Z which this entity is attached to.
     *
     * @param tileZ the z co-ordinate.
     */
    public void setTileZ(int tileZ) {
        this.tileZ = tileZ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HangingEntity that)) return false;
        if (!super.equals(o)) return false;
        return getTileX() == that.getTileX() && getTileY() == that.getTileY() && getTileZ() == that.getTileZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTileX(), getTileY(), getTileZ());
    }

    @Override
    public String toString() {
        return "HangingEntity{" +
                "motionY=" + getMotionY() +
                ", tileX=" + getTileX() +
                ", tileY=" + getTileY() +
                ", tileZ=" + getTileZ() +
                ", positionX=" + getPositionX() +
                ", positionY=" + getPositionY() +
                ", positionZ=" + getPositionZ() +
                ", motionX=" + getMotionX() +
                ", motionZ=" + getMotionZ() +
                ", yaw=" + getYaw() +
                ", pitch=" + getPitch() +
                '}';
    }
}
