package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BedBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerDyeColor;

/**
 * Java Bed Block Entity which has color.
 */
public class JavaLegacyBedBlockEntity extends BedBlockEntity {
    private ChunkerDyeColor color = ChunkerDyeColor.WHITE;

    /**
     * Create a new java bed block entity.
     */
    public JavaLegacyBedBlockEntity() {

    }

    /**
     * Create a new bedrock block entity from the Chunker type.
     *
     * @param original the original to copy properties from.
     */
    public JavaLegacyBedBlockEntity(BedBlockEntity original) {
        setX(original.getX());
        setY(original.getY());
        setZ(original.getZ());
        setMovable(original.isMovable());
        setCustomName(original.getCustomName());
    }

    /**
     * Convert this Java block entity to a normal chunker bed.
     *
     * @return the chunker bed.
     */
    public BedBlockEntity toChunker() {
        BedBlockEntity bedBlockEntity = new BedBlockEntity();
        bedBlockEntity.setX(getX());
        bedBlockEntity.setY(getY());
        bedBlockEntity.setZ(getZ());
        bedBlockEntity.setMovable(isMovable());
        bedBlockEntity.setCustomName(getCustomName());
        return bedBlockEntity;
    }

    /**
     * Get the color used for the bed.
     *
     * @return the color.
     */
    public ChunkerDyeColor getColor() {
        return color;
    }

    /**
     * Set the color to use for the bed.
     *
     * @param color the bed color.
     */
    public void setColor(ChunkerDyeColor color) {
        this.color = color;
    }
}
