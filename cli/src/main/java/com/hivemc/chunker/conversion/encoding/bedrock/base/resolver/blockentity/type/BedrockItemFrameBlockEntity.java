package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.column.entity.GlowItemFrameEntity;
import com.hivemc.chunker.conversion.intermediate.column.entity.ItemFrameEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Bedrock Item Frame Block Entity (an Entity in chunker format)
 */
public class BedrockItemFrameBlockEntity extends BlockEntity {
    private byte itemRotation;
    @Nullable
    private ChunkerItemStack item;

    /**
     * Get the rotation of the item in the frame.
     *
     * @return the rotation of the item as a byte 0-15.
     */
    public byte getItemRotation() {
        return itemRotation;
    }

    /**
     * Set the rotation of the item in the frame.
     *
     * @param itemRotation the rotation of the item as a byte 0-15.
     */
    public void setItemRotation(byte itemRotation) {
        this.itemRotation = itemRotation;
    }

    /**
     * Get the item displayed by the item frame.
     *
     * @return the item or null/air.
     */
    @Nullable
    public ChunkerItemStack getItem() {
        return item;
    }

    /**
     * Set the item displayed by the item frame.
     *
     * @param item the item or null/air.
     */
    public void setItem(@Nullable ChunkerItemStack item) {
        this.item = item;
    }

    /**
     * Convert the item frame to a chunker entity.
     *
     * @param blockIdentifier the block identifier of the block.
     * @return the entity instance with transferred properties.
     */
    public ItemFrameEntity toChunker(ChunkerBlockIdentifier blockIdentifier) {
        ItemFrameEntity itemFrameEntity = this instanceof BedrockGlowItemFrameBlockEntity ? new GlowItemFrameEntity() : new ItemFrameEntity();
        itemFrameEntity.setPositionX(getX());
        itemFrameEntity.setPositionY(getY());
        itemFrameEntity.setPositionZ(getZ());
        itemFrameEntity.setTileX(getX());
        itemFrameEntity.setTileY(getY());
        itemFrameEntity.setTileZ(getZ());
        itemFrameEntity.setItem(getItem());
        itemFrameEntity.setItemRotation(getItemRotation());

        // Use block properties
        itemFrameEntity.setDirection(blockIdentifier.getState(VanillaBlockStates.FACING_ALL));
        itemFrameEntity.setMap(blockIdentifier.getState(VanillaBlockStates.BEDROCK_FRAME_MAP_BIT) == Bool.TRUE);
        itemFrameEntity.setPhoto(blockIdentifier.getState(VanillaBlockStates.BEDROCK_FRAME_PHOTO_BIT) == Bool.TRUE);

        // Return the new item frame entity
        return itemFrameEntity;
    }
}
