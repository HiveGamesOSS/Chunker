package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerVanillaItemType;

import java.util.Objects;

/**
 * Represents a Decorated Pot Block Entity.
 */
public class DecoratedPotBlockEntity extends BlockEntity {
    private ChunkerItemStackIdentifier back = ChunkerVanillaItemType.BRICK;
    private ChunkerItemStackIdentifier left = ChunkerVanillaItemType.BRICK;
    private ChunkerItemStackIdentifier right = ChunkerVanillaItemType.BRICK;
    private ChunkerItemStackIdentifier front = ChunkerVanillaItemType.BRICK;

    /**
     * Get the sherd used for the back of the pot.
     *
     * @return the sherd as an item type, if absent it returns a brick.
     */
    public ChunkerItemStackIdentifier getBack() {
        return back;
    }

    /**
     * Set the sherd used for the back of the pot.
     *
     * @param back the sherd as an item type, if absent it should be a brick.
     */
    public void setBack(ChunkerItemStackIdentifier back) {
        this.back = back;
    }

    /**
     * Get the sherd used for the left side of the pot.
     *
     * @return the sherd as an item type, if absent it returns a brick.
     */
    public ChunkerItemStackIdentifier getLeft() {
        return left;
    }

    /**
     * Set the sherd used for the left side of the pot.
     *
     * @param left the sherd as an item type, if absent it should be a brick.
     */
    public void setLeft(ChunkerItemStackIdentifier left) {
        this.left = left;
    }

    /**
     * Get the sherd used for the right side of the pot.
     *
     * @return the sherd as an item type, if absent it returns a brick.
     */
    public ChunkerItemStackIdentifier getRight() {
        return right;
    }

    /**
     * Set the sherd used for the right side of the pot.
     *
     * @param right the sherd as an item type, if absent it should be a brick.
     */
    public void setRight(ChunkerItemStackIdentifier right) {
        this.right = right;
    }

    /**
     * Get the sherd used for the front of the pot.
     *
     * @return the sherd as an item type, if absent it returns a brick.
     */
    public ChunkerItemStackIdentifier getFront() {
        return front;
    }

    /**
     * Set the sherd used for the front of the pot.
     *
     * @param front the sherd as an item type, if absent it should be a brick.
     */
    public void setFront(ChunkerItemStackIdentifier front) {
        this.front = front;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecoratedPotBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getBack(), that.getBack()) && Objects.equals(getLeft(), that.getLeft()) && Objects.equals(getRight(), that.getRight()) && Objects.equals(getFront(), that.getFront());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBack(), getLeft(), getRight(), getFront());
    }
}
