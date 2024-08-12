package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.SkullBlockEntity;

/**
 * Bedrock Skull Block Entity which holds the rotation, if it's powered and the type.
 */
public class BedrockSkullBlockEntity extends SkullBlockEntity {
    private byte rotation;
    private boolean movingMouth;
    private byte skullType;

    /**
     * Create a new skull block entity.
     */
    public BedrockSkullBlockEntity() {

    }

    /**
     * Create a new bedrock block entity from the Chunker type.
     *
     * @param original the original to copy properties from.
     */
    public BedrockSkullBlockEntity(SkullBlockEntity original) {
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
     * Whether the mouth is moving (powered).
     *
     * @return true if the mouth is moving.
     */
    public boolean isMovingMouth() {
        return movingMouth;
    }

    /**
     * Get whether the mouth is moving (powered).
     *
     * @param movingMouth true if the mouth is moving.
     */
    public void setMovingMouth(boolean movingMouth) {
        this.movingMouth = movingMouth;
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
