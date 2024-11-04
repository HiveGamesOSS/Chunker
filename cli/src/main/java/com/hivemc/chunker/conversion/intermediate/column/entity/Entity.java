package com.hivemc.chunker.conversion.intermediate.column.entity;

import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The base entity type with shared information present in all entities.
 */
public abstract class Entity {
    // Position
    private double positionX;
    private double positionY;
    private double positionZ;

    // Motion
    private double motionX;
    private double motionY;
    private double motionZ;

    // Yaw / Pitch
    private float yaw;
    private float pitch;

    @Nullable
    private CompoundTag originalNBT;

    /**
     * Get the X co-ordinate of the entity.
     *
     * @return the x co-ordinate.
     */
    public double getPositionX() {
        return positionX;
    }

    /**
     * Set the X co-ordinate of the entity.
     *
     * @param positionX the x co-ordinate.
     */
    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    /**
     * Get the Y co-ordinate of the entity.
     *
     * @return the y co-ordinate.
     */
    public double getPositionY() {
        return positionY;
    }

    /**
     * Set the Y co-ordinate of the entity.
     *
     * @param positionY the y co-ordinate.
     */
    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    /**
     * Get the Z co-ordinate of the entity.
     *
     * @return the z co-ordinate.
     */
    public double getPositionZ() {
        return positionZ;
    }

    /**
     * Set the Z co-ordinate of the entity.
     *
     * @param positionZ the z co-ordinate.
     */
    public void setPositionZ(double positionZ) {
        this.positionZ = positionZ;
    }

    /**
     * Get the entities motion in the X axis.
     *
     * @return the motion in the X axis (velocity).
     */
    public double getMotionX() {
        return motionX;
    }

    /**
     * Set the entities motion in the X axis.
     *
     * @param motionX the motion in the X axis (velocity).
     */
    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    /**
     * Get the entities motion in the Y axis.
     *
     * @return the motion in the Y axis (velocity).
     */
    public double getMotionY() {
        return motionY;
    }

    /**
     * Set the entities motion in the Y axis.
     *
     * @param motionY the motion in the Y axis (velocity).
     */
    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    /**
     * Get the entities motion in the Z axis.
     *
     * @return the motion in the Z axis (velocity).
     */
    public double getMotionZ() {
        return motionZ;
    }

    /**
     * Set the entities motion in the Z axis.
     *
     * @param motionZ the motion in the Z axis (velocity).
     */
    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }

    /**
     * Get the yaw rotation of the entity.
     *
     * @return the yaw rotation.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Set the yaw rotation of the entity.
     *
     * @param yaw the yaw rotation.
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * Get the pitch rotation of the entity.
     *
     * @return the pitch rotation.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Set the pitch rotation of the entity.
     *
     * @param pitch the pitch rotation.
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Get the original NBT tag for the entity.
     *
     * @return if present the original NBT tag.
     */
    public @Nullable CompoundTag getOriginalNBT() {
        return originalNBT;
    }

    /**
     * Set the original NBT tag for the entity.
     *
     * @param originalNBT the original NBT.
     */
    public void setOriginalNBT(@Nullable CompoundTag originalNBT) {
        this.originalNBT = originalNBT;
    }

    /**
     * Get the entity type represented by this type.
     *
     * @return the entity type which is represented by the class.
     */
    public abstract ChunkerEntityType getEntityType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity entity)) return false;
        return Double.compare(getPositionX(), entity.getPositionX()) == 0 && Double.compare(getPositionY(), entity.getPositionY()) == 0 && Double.compare(getPositionZ(), entity.getPositionZ()) == 0 && Double.compare(getMotionX(), entity.getMotionX()) == 0 && Double.compare(getMotionY(), entity.getMotionY()) == 0 && Double.compare(getMotionZ(), entity.getMotionZ()) == 0 && Float.compare(getYaw(), entity.getYaw()) == 0 && Float.compare(getPitch(), entity.getPitch()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPositionX(), getPositionY(), getPositionZ(), getMotionX(), getMotionY(), getMotionZ(), getYaw(), getPitch());
    }

    @Override
    public String toString() {
        return "Entity{" +
                "positionX=" + getPositionX() +
                ", positionY=" + getPositionY() +
                ", positionZ=" + getPositionZ() +
                ", motionX=" + getMotionX() +
                ", motionY=" + getMotionY() +
                ", motionZ=" + getMotionZ() +
                ", yaw=" + getYaw() +
                ", pitch=" + getPitch() +
                '}';
    }
}
