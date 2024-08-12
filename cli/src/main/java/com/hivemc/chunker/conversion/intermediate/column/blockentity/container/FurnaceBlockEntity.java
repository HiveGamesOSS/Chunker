package com.hivemc.chunker.conversion.intermediate.column.blockentity.container;

import java.util.Objects;

/**
 * Represents a Furnace Block Entity.
 */
public class FurnaceBlockEntity extends ContainerBlockEntity {
    private short burnTime;
    private short cookTime;
    private short cookTimeTotal;

    /**
     * Get the amount of cooking ticks left on the fuel.
     *
     * @return the number of ticks.
     */
    public short getBurnTime() {
        return burnTime;
    }

    /**
     * Set the amount of cooking ticks left on the fuel.
     *
     * @param burnTime the number of ticks.
     */
    public void setBurnTime(short burnTime) {
        this.burnTime = burnTime;
    }

    /**
     * Get the amount of cooking time done for the item.
     *
     * @return the number of ticks.
     */
    public short getCookTime() {
        return cookTime;
    }

    /**
     * Set the amount of cooking time done for the item.
     *
     * @param cookTime the number of ticks.
     */
    public void setCookTime(short cookTime) {
        this.cookTime = cookTime;
    }

    /**
     * Get the total amount of ticks for cooking.
     *
     * @return the number of ticks.
     */
    public short getCookTimeTotal() {
        return cookTimeTotal;
    }

    /**
     * Set the total amount of ticks for cooking.
     *
     * @param cookTimeTotal the number of ticks.
     */
    public void setCookTimeTotal(short cookTimeTotal) {
        this.cookTimeTotal = cookTimeTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FurnaceBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return getBurnTime() == that.getBurnTime() && getCookTime() == that.getCookTime() && getCookTimeTotal() == that.getCookTimeTotal();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBurnTime(), getCookTime(), getCookTimeTotal());
    }
}
