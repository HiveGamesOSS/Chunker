package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BrushableBlockEntity;

/**
 * Bedrock Brushable Block Entity which has the block type.
 */
public class BedrockBrushableBlockEntity extends BrushableBlockEntity {
    private String type;

    /**
     * Create a new Bedrock Brushable Block Entity.
     */
    public BedrockBrushableBlockEntity() {

    }

    /**
     * Create a new bedrock block entity from the Chunker type.
     *
     * @param original the original to copy properties from.
     */
    public BedrockBrushableBlockEntity(BrushableBlockEntity original) {
        setX(original.getX());
        setY(original.getY());
        setZ(original.getZ());
        setMovable(original.isMovable());
        setCustomName(original.getCustomName());
        setBrushDirection(original.getBrushDirection());
        setBrushCount(original.getBrushCount());
        setItem(original.getItem());
    }

    /**
     * Convert this Bedrock block entity to a normal chunker brushable block entity.
     *
     * @return the chunker brushable block entity.
     */
    public BrushableBlockEntity toChunker() {
        BrushableBlockEntity brushableBlockEntity = new BrushableBlockEntity();
        brushableBlockEntity.setX(getX());
        brushableBlockEntity.setY(getY());
        brushableBlockEntity.setZ(getZ());
        brushableBlockEntity.setMovable(isMovable());
        brushableBlockEntity.setCustomName(getCustomName());
        brushableBlockEntity.setBrushDirection(getBrushDirection());
        brushableBlockEntity.setBrushCount(getBrushCount());
        brushableBlockEntity.setItem(getItem());
        return brushableBlockEntity;
    }

    /**
     * Get the identifier type for this block.
     *
     * @return the identifier type of the block.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the identifier type for this block.
     *
     * @param type the identifier type of the block.
     */
    public void setType(String type) {
        this.type = type;
    }
}
