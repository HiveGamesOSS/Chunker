package com.hivemc.chunker.conversion.intermediate.column.blockentity.end;

import java.util.Objects;

/**
 * Represents an End Gateway Block Entity.
 */
public class EndGatewayBlockEntity extends EndPortalBlockEntity {
    private long age;
    private int exitX;
    private int exitY;
    private int exitZ;

    /**
     * The age in ticks which the block entity has existed.
     *
     * @return the number of ticks.
     */
    public long getAge() {
        return age;
    }

    /**
     * Set the age in ticks which the block entity has existed.
     *
     * @param age the number of ticks.
     */
    public void setAge(long age) {
        this.age = age;
    }

    /**
     * Get the X co-ordinate for the exit.
     *
     * @return the x position.
     */
    public int getExitX() {
        return exitX;
    }

    /**
     * Set the X co-ordinate for the exit.
     *
     * @param exitX the x position.
     */
    public void setExitX(int exitX) {
        this.exitX = exitX;
    }

    /**
     * Get the Y co-ordinate for the exit.
     *
     * @return the y position.
     */
    public int getExitY() {
        return exitY;
    }

    /**
     * Set the Y co-ordinate for the exit.
     *
     * @param exitY the y position.
     */
    public void setExitY(int exitY) {
        this.exitY = exitY;
    }

    /**
     * Get the Z co-ordinate for the exit.
     *
     * @return the z position.
     */
    public int getExitZ() {
        return exitZ;
    }

    /**
     * Set the Z co-ordinate for the exit.
     *
     * @param exitZ the z position.
     */
    public void setExitZ(int exitZ) {
        this.exitZ = exitZ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EndGatewayBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return getAge() == that.getAge() && getExitX() == that.getExitX() && getExitY() == that.getExitY() && getExitZ() == that.getExitZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAge(), getExitX(), getExitY(), getExitZ());
    }
}
