package com.hivemc.chunker.conversion.intermediate.column.blockentity.container;

import java.util.Objects;

/**
 * Represents a Brewing Stand Block Entity.
 */
public class BrewingStandBlockEntity extends ContainerBlockEntity {
    private short brewTime;
    private short fuel;
    private short fuelTotal;

    /**
     * Get the brewing time.
     *
     * @return the amount of time in ticks.
     */
    public short getBrewTime() {
        return brewTime;
    }

    /**
     * Set the brewing time.
     *
     * @param brewTime the amount of time in ticks.
     */
    public void setBrewTime(short brewTime) {
        this.brewTime = brewTime;
    }

    /**
     * Get the amount of fuel used in the brewing stand.
     *
     * @return the amount of fuel.
     */
    public short getFuel() {
        return fuel;
    }

    /**
     * Set the amount of fuel used in the brewing stand.
     *
     * @param fuel the amount of fuel.
     */
    public void setFuel(short fuel) {
        this.fuel = fuel;
    }

    /**
     * Get the total amount of fuel in the brewing stand.
     *
     * @return the amount of fuel.
     */
    public short getFuelTotal() {
        return fuelTotal;
    }

    /**
     * Set the total amount of fuel in the brewing stand.
     *
     * @param fuelTotal the amount of fuel.
     */
    public void setFuelTotal(short fuelTotal) {
        this.fuelTotal = fuelTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrewingStandBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return getBrewTime() == that.getBrewTime() && getFuel() == that.getFuel() && getFuelTotal() == that.getFuelTotal();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBrewTime(), getFuel(), getFuelTotal());
    }
}
