package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.ChestBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Bedrock Chest Block Entity which has pair co-ordinates and if the chest is the leader.
 */
public class BedrockChestBlockEntity extends ChestBlockEntity {
    @Nullable
    private Integer pairX;
    @Nullable
    private Integer pairZ;
    private boolean isLead;

    /**
     * Create a new chest block entity.
     */
    public BedrockChestBlockEntity() {

    }

    /**
     * Create a new bedrock block entity from the Chunker type.
     *
     * @param original the original to copy properties from.
     */
    public BedrockChestBlockEntity(ChestBlockEntity original) {
        setX(original.getX());
        setY(original.getY());
        setZ(original.getZ());
        setMovable(original.isMovable());
        setCustomName(original.getCustomName());
        setLootTable(original.getLootTable());
        getItems().putAll(original.getItems());
    }

    /**
     * Convert the Bedrock specific chest to a Chunker variant.
     *
     * @return the chunker variant.
     */
    public ChestBlockEntity toChunker() {
        ChestBlockEntity chestBlockEntity = new ChestBlockEntity();
        chestBlockEntity.setX(getX());
        chestBlockEntity.setY(getY());
        chestBlockEntity.setZ(getZ());
        chestBlockEntity.setMovable(isMovable());
        chestBlockEntity.setCustomName(getCustomName());
        chestBlockEntity.setLootTable(getLootTable());
        chestBlockEntity.getItems().putAll(getItems());
        return chestBlockEntity;
    }

    /**
     * Get the X co-ordinates of the other half of the chest.
     *
     * @return the x or null if one isn't present.
     */
    @Nullable
    public Integer getPairX() {
        return pairX;
    }

    /**
     * Set the X co-ordinates of the other half of the chest.
     *
     * @param pairX the x or null if one isn't present.
     */
    public void setPairX(@Nullable Integer pairX) {
        this.pairX = pairX;
    }

    /**
     * Get the Z co-ordinates of the other half of the chest.
     *
     * @return the z or null if one isn't present.
     */
    @Nullable
    public Integer getPairZ() {
        return pairZ;
    }

    /**
     * Set the Z co-ordinates of the other half of the chest.
     *
     * @param pairZ the z or null if one isn't present.
     */
    public void setPairZ(@Nullable Integer pairZ) {
        this.pairZ = pairZ;
    }

    /**
     * Whether this chest is the leader (top chest).
     *
     * @return true if it is.
     */
    public boolean isLead() {
        return isLead;
    }

    /**
     * Set whether this chest is the leader (top chest).
     *
     * @param lead true if it should be the leader.
     */
    public void setLead(boolean lead) {
        isLead = lead;
    }
}
