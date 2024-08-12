package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.ShulkerBoxBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirection;

/**
 * Bedrock Shulker Block Entity which holds the rotation.
 */
public class BedrockShulkerBlockEntity extends ShulkerBoxBlockEntity {
    private FacingDirection facing;

    /**
     * Create a new shulker block entity.
     */
    public BedrockShulkerBlockEntity() {

    }

    /**
     * Create a new bedrock block entity from the Chunker type.
     *
     * @param original the original to copy properties from.
     */
    public BedrockShulkerBlockEntity(ShulkerBoxBlockEntity original) {
        setX(original.getX());
        setY(original.getY());
        setZ(original.getZ());
        setMovable(original.isMovable());
        setCustomName(original.getCustomName());
        setLootTable(original.getLootTable());
        getItems().putAll(original.getItems());
    }

    /**
     * Convert the block entity to the chunker variant.
     *
     * @return the non-bedrock block entity.
     */
    public ShulkerBoxBlockEntity toChunker() {
        ShulkerBoxBlockEntity shulkerBoxBlockEntity = new ShulkerBoxBlockEntity();
        shulkerBoxBlockEntity.setX(getX());
        shulkerBoxBlockEntity.setY(getY());
        shulkerBoxBlockEntity.setZ(getZ());
        shulkerBoxBlockEntity.setMovable(isMovable());
        shulkerBoxBlockEntity.setCustomName(getCustomName());
        shulkerBoxBlockEntity.setLootTable(getLootTable());
        shulkerBoxBlockEntity.getItems().putAll(getItems());
        return shulkerBoxBlockEntity;
    }

    /**
     * Get the direction the shulker is facing.
     *
     * @return the facing direction.
     */
    public FacingDirection getFacing() {
        return facing;
    }

    /**
     * Set the direction the shulker is facing.
     *
     * @param facing the facing direction.
     */
    public void setFacing(FacingDirection facing) {
        this.facing = facing;
    }
}
