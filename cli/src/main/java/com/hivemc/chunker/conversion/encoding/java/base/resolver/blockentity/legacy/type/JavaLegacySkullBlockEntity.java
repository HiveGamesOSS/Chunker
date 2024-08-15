package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.SkullBlockEntity;

/**
 * Java Skull Block Entity which holds the rotation and the type.
 */
public class JavaLegacySkullBlockEntity extends SkullBlockEntity {
    private byte rotation;
    private byte skullType;

    /**
     * Create a new skull block entity.
     */
    public JavaLegacySkullBlockEntity() {

    }

    /**
     * Create a new java block entity from the Chunker type.
     *
     * @param original the original to copy properties from.
     */
    public JavaLegacySkullBlockEntity(SkullBlockEntity original) {
        setX(original.getX());
        setY(original.getY());
        setZ(original.getZ());
        setMovable(original.isMovable());
        setCustomName(original.getCustomName());
        setOwnerId(original.getOwnerId());
        setOwnerName(original.getOwnerName());
        setTexture(original.getTexture());
        setTextureSignature(original.getTextureSignature());
    }

    /**
     * Convert the block entity to the chunker variant.
     *
     * @return the non-bedrock block entity.
     */
    public SkullBlockEntity toChunker() {
        SkullBlockEntity skullBlockEntity = new SkullBlockEntity();
        skullBlockEntity.setX(getX());
        skullBlockEntity.setY(getY());
        skullBlockEntity.setZ(getZ());
        skullBlockEntity.setMovable(isMovable());
        skullBlockEntity.setCustomName(getCustomName());
        skullBlockEntity.setOwnerId(getOwnerId());
        skullBlockEntity.setOwnerName(getOwnerName());
        skullBlockEntity.setTexture(getTexture());
        skullBlockEntity.setTextureSignature(getTextureSignature());
        return skullBlockEntity;
    }

    /**
     * Get the rotation 0-15 which this skull is rotated.
     *
     * @return the rotation value.
     */
    public byte getRotation() {
        return rotation;
    }

    /**
     * Set the rotation 0-15 which this skull is rotated.
     *
     * @param rotation the rotation value.
     */
    public void setRotation(byte rotation) {
        this.rotation = rotation;
    }

    /**
     * Get the skull type.
     *
     * @return the skull type as a byte.
     */
    public byte getSkullType() {
        return skullType;
    }

    /**
     * Set the skull type.
     *
     * @param skullType the skull type as a byte.
     */
    public void setSkullType(byte skullType) {
        this.skullType = skullType;
    }
}
