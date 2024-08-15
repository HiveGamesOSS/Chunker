package com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable;

/**
 * Represents a Chest Block Entity.
 */
public class ChestBlockEntity extends RandomizableContainerBlockEntity {
    @Override
    public String toString() {
        return "ChestBlockEntity{" +
                "customName=" + getCustomName() +
                ", movable=" + isMovable() +
                ", z=" + getZ() +
                ", y=" + getY() +
                ", x=" + getX() +
                ", items=" + getItems() +
                ", lootTable='" + getLootTable() + '\'' +
                '}';
    }
}
