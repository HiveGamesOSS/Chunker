package com.hivemc.chunker.conversion.intermediate.column.entity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerDyeColor;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;

import java.util.Objects;

/**
 * Represents a Cushion Entity.
 */
public class CushionEntity extends Entity {
    private ChunkerDyeColor color = ChunkerDyeColor.WHITE;

    /**
     * Get the color of the cushion.
     *
     * @return the dye color of the cushion.
     */
    public ChunkerDyeColor getColor() {
        return color;
    }

    /**
     * Set the color of the cushion.
     *
     * @param color the dye color of the cushion.
     */
    public void setColor(ChunkerDyeColor color) {
        this.color = color;
    }

    @Override
    public ChunkerEntityType getEntityType() {
        return ChunkerVanillaEntityType.CUSHION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CushionEntity that)) return false;
        if (!super.equals(o)) return false;
        return getColor() == that.getColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getColor());
    }

    @Override
    public String toString() {
        return "CushionEntity{" +
                "color=" + getColor() +
                ", positionX=" + getPositionX() +
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
