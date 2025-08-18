package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.CopperGolemStatueBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.CopperGolemPose;

/**
 * Bedrock Copper Golem Statue Block Entity which has pose.
 */
public class BedrockCopperGolemStatueBlockEntity extends CopperGolemStatueBlockEntity {
    private CopperGolemPose pose = CopperGolemPose.STANDING;

    /**
     * Create a new bedrock copper golem statue block entity.
     */
    public BedrockCopperGolemStatueBlockEntity() {

    }

    /**
     * Create a new bedrock block entity from the Chunker type.
     *
     * @param original the original to copy properties from.
     */
    public BedrockCopperGolemStatueBlockEntity(CopperGolemStatueBlockEntity original) {
        setX(original.getX());
        setY(original.getY());
        setZ(original.getZ());
        setMovable(original.isMovable());
        setCustomName(original.getCustomName());
    }

    /**
     * Convert this Bedrock block entity to a normal chunker block entity.
     *
     * @return the chunker bed.
     */
    public CopperGolemStatueBlockEntity toChunker() {
        CopperGolemStatueBlockEntity copperGolemStatueBlockEntity = new CopperGolemStatueBlockEntity();
        copperGolemStatueBlockEntity.setX(getX());
        copperGolemStatueBlockEntity.setY(getY());
        copperGolemStatueBlockEntity.setZ(getZ());
        copperGolemStatueBlockEntity.setMovable(isMovable());
        copperGolemStatueBlockEntity.setCustomName(getCustomName());
        return copperGolemStatueBlockEntity;
    }

    /**
     * Get the pose for the statue.
     *
     * @return the pose.
     */
    public CopperGolemPose getPose() {
        return pose;
    }

    /**
     * Set the pose for the statue.
     *
     * @param pose the pose.
     */
    public void setPose(CopperGolemPose pose) {
        this.pose = pose;
    }
}
