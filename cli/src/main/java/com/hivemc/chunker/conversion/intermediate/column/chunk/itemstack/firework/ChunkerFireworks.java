package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.firework;

import java.util.List;
import java.util.Objects;

/**
 * The firework properties.
 */
public class ChunkerFireworks {
    private byte flightDuration;
    private List<ChunkerFireworkExplosion> explosions;

    /**
     * Create a new set of firework properties.
     *
     * @param flightDuration the flight time.
     * @param explosions     a list of explosions.
     */
    public ChunkerFireworks(byte flightDuration, List<ChunkerFireworkExplosion> explosions) {
        this.flightDuration = flightDuration;
        this.explosions = explosions;
    }

    /**
     * Get the flight duration of the firework.
     *
     * @return the flight duration.
     */
    public byte getFlightDuration() {
        return flightDuration;
    }

    /**
     * Set the flight duration of the firework.
     *
     * @param flightDuration the flight duration.
     */
    public void setFlightDuration(byte flightDuration) {
        this.flightDuration = flightDuration;
    }

    /**
     * Get the explosions for this firework
     *
     * @return a list of the explosions.
     */
    public List<ChunkerFireworkExplosion> getExplosions() {
        return explosions;
    }

    /**
     * Set the explosions for this firework.
     *
     * @param explosions a list of explosions.
     */
    public void setExplosions(List<ChunkerFireworkExplosion> explosions) {
        this.explosions = explosions;
    }

    @Override
    public String toString() {
        return "ChunkerFireworks{" +
                "flightDuration=" + getFlightDuration() +
                ", explosions=" + getExplosions() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkerFireworks that)) return false;
        return getFlightDuration() == that.getFlightDuration() && Objects.equals(getExplosions(), that.getExplosions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFlightDuration(), getExplosions());
    }
}