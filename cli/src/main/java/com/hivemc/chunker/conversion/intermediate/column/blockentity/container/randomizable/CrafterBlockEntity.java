package com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable;

import java.util.Objects;

/**
 * Represents a Crafter Block Entity.
 */
public class CrafterBlockEntity extends RandomizableContainerBlockEntity {
    private short disabledSlots;
    private int craftingTicksRemaining;

    /**
     * Get which slots are disabled.
     *
     * @return a bitmask of the disabled slots.
     */
    public short getDisabledSlots() {
        return disabledSlots;
    }

    /**
     * Set which slots are disabled.
     *
     * @param disabledSlots a bitmask of the disabled slots.
     */
    public void setDisabledSlots(short disabledSlots) {
        this.disabledSlots = disabledSlots;
    }

    /**
     * Get the number of ticks remaining to craft.
     *
     * @return the number of remaining ticks.
     */
    public int getCraftingTicksRemaining() {
        return craftingTicksRemaining;
    }

    /**
     * Set the number of ticks remaining to craft.
     *
     * @param craftingTicksRemaining the number of remaining ticks.
     */
    public void setCraftingTicksRemaining(int craftingTicksRemaining) {
        this.craftingTicksRemaining = craftingTicksRemaining;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrafterBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return getDisabledSlots() == that.getDisabledSlots() && getCraftingTicksRemaining() == that.getCraftingTicksRemaining();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDisabledSlots(), getCraftingTicksRemaining());
    }
}
