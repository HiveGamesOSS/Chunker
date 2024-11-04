package com.hivemc.chunker.conversion.intermediate.column.entity;

import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockGlowItemFrameBlockEntity;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockItemFrameBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirection;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an Item Frame entity.
 */
public class ItemFrameEntity extends HangingEntity {
    @Nullable
    private ChunkerItemStack item;
    private FacingDirection direction;
    private byte itemRotation;
    private boolean map;
    private boolean photo;

    /**
     * Get the item inside the frame.
     *
     * @return the item or null/air if not present
     */
    @Nullable
    public ChunkerItemStack getItem() {
        return item;
    }

    /**
     * Set the item inside the frame.
     *
     * @param item the item or null/air if not present
     */
    public void setItem(@Nullable ChunkerItemStack item) {
        this.item = item;
    }

    /**
     * Get the direction that the frame is facing.
     *
     * @return the direction.
     */
    public FacingDirection getDirection() {
        return direction;
    }

    /**
     * Set the direction that the frame is facing.
     *
     * @param direction the direction.
     */
    public void setDirection(FacingDirection direction) {
        this.direction = direction;
    }

    /**
     * Set the rotation of the item in the frame.
     *
     * @return the rotation between 0-15.
     */
    public byte getItemRotation() {
        return itemRotation;
    }

    /**
     * Get the rotation of the item in the frame.
     *
     * @param itemRotation the rotation between 0-15.
     */
    public void setItemRotation(byte itemRotation) {
        this.itemRotation = itemRotation;
    }

    /**
     * Whether the item frame is displaying as a map (Bedrock).
     *
     * @return true if it's displaying as a map.
     */
    public boolean isMap() {
        return map;
    }

    /**
     * Set whether the item frame is displaying as a map (Bedrock).
     *
     * @param map true if it's displaying as a map.
     */
    public void setMap(boolean map) {
        this.map = map;
    }

    /**
     * Whether the item frame is displaying as a photo (Bedrock EDU).
     *
     * @return true if it's displaying as a photo.
     */
    public boolean isPhoto() {
        return photo;
    }

    /**
     * Set whether the item frame is displaying as a photo (Bedrock EDU).
     *
     * @param photo true if it's displaying as a photo.
     */
    public void setPhoto(boolean photo) {
        this.photo = photo;
    }

    @Override
    public ChunkerEntityType getEntityType() {
        return ChunkerVanillaEntityType.ITEM_FRAME;
    }

    /**
     * Convert the item frame entity into a Bedrock block entity.
     *
     * @return a new block entity with converted properties.
     */
    public BedrockItemFrameBlockEntity toBedrockBlockEntity() {
        BedrockItemFrameBlockEntity blockEntity = this instanceof GlowItemFrameEntity ? new BedrockGlowItemFrameBlockEntity() : new BedrockItemFrameBlockEntity();
        blockEntity.setX(getTileX());
        blockEntity.setY(getTileY());
        blockEntity.setZ(getTileZ());
        blockEntity.setItem(getItem());
        blockEntity.setItemRotation(getItemRotation());

        return blockEntity;
    }
}
