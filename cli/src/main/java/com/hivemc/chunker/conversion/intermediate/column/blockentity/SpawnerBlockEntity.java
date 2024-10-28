package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a Spawner Block Entity.
 */
public class SpawnerBlockEntity extends BlockEntity {
    private short delay;
    private short spawnRange;
    private short maxSpawnDelay;
    private short minSpawnDelay;
    private short spawnCount;
    private short requiredPlayerRange;
    private short maxNearbyEntities;
    @Nullable
    private ChunkerEntityType entityType = null;

    /**
     * Get the delay until the next spawn.
     *
     * @return the delay in ticks until next spawn.
     */
    public short getDelay() {
        return delay;
    }

    /**
     * Set the delay until the next spawn.
     *
     * @param delay the delay in ticks until next spawn.
     */
    public void setDelay(short delay) {
        this.delay = delay;
    }

    /**
     * Get the block range from the spawner that entities will spawn.
     *
     * @return the range in blocks.
     */
    public short getSpawnRange() {
        return spawnRange;
    }

    /**
     * Set the block range from the spawner that entities will spawn.
     *
     * @param spawnRange the range in blocks.
     */
    public void setSpawnRange(short spawnRange) {
        this.spawnRange = spawnRange;
    }

    /**
     * The maximum delay between activations.
     *
     * @return the maximum delay between activations in ticks.
     */
    public short getMaxSpawnDelay() {
        return maxSpawnDelay;
    }

    /**
     * Set the maximum delay between activations.
     *
     * @param maxSpawnDelay the maximum delay between activations in ticks.
     */
    public void setMaxSpawnDelay(short maxSpawnDelay) {
        this.maxSpawnDelay = maxSpawnDelay;
    }

    /**
     * The minimum delay between activations.
     *
     * @return the minimum delay between activations in ticks.
     */
    public short getMinSpawnDelay() {
        return minSpawnDelay;
    }

    /**
     * Set the minimum delay between activations.
     *
     * @param minSpawnDelay the minimum delay between activations in ticks.
     */
    public void setMinSpawnDelay(short minSpawnDelay) {
        this.minSpawnDelay = minSpawnDelay;
    }

    /**
     * Get the number of entities to spawn.
     *
     * @return the number of entities to spawn when the spawner activates.
     */
    public short getSpawnCount() {
        return spawnCount;
    }

    /**
     * Set the number of entities to spawn.
     *
     * @param spawnCount the number of entities to spawn when the spawner activates.
     */
    public void setSpawnCount(short spawnCount) {
        this.spawnCount = spawnCount;
    }

    /**
     * Get the range a player has to be within for the spawner to activate.
     *
     * @return the distance in blocks from the spawner.
     */
    public short getRequiredPlayerRange() {
        return requiredPlayerRange;
    }

    /**
     * Set the range a player has to be within for the spawner to activate.
     *
     * @param requiredPlayerRange the distance in blocks from the spawner.
     */
    public void setRequiredPlayerRange(short requiredPlayerRange) {
        this.requiredPlayerRange = requiredPlayerRange;
    }

    /**
     * The maximum number of entities that can be nearby.
     *
     * @return the number of entities which the spawner will deactivate.
     */
    public short getMaxNearbyEntities() {
        return maxNearbyEntities;
    }

    /**
     * Set the maximum number of entities that can be nearby.
     *
     * @param maxNearbyEntities the number of entities which the spawner will deactivate.
     */
    public void setMaxNearbyEntities(short maxNearbyEntities) {
        this.maxNearbyEntities = maxNearbyEntities;
    }

    /**
     * Get the entity type this spawner spawns.
     *
     * @return the entity type or null if it is not set.
     */
    @Nullable
    public ChunkerEntityType getEntityType() {
        return entityType;
    }

    /**
     * Set the entity type that this spawner spawns.
     *
     * @param entityType the type this spawner spawns or null if not set.
     */
    public void setEntityType(@Nullable ChunkerEntityType entityType) {
        this.entityType = entityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpawnerBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return getDelay() == that.getDelay() && getSpawnRange() == that.getSpawnRange() && getMaxSpawnDelay() == that.getMaxSpawnDelay() && getMinSpawnDelay() == that.getMinSpawnDelay() && getSpawnCount() == that.getSpawnCount() && getRequiredPlayerRange() == that.getRequiredPlayerRange() && getMaxNearbyEntities() == that.getMaxNearbyEntities() && getEntityType() == that.getEntityType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDelay(), getSpawnRange(), getMaxSpawnDelay(), getMinSpawnDelay(), getSpawnCount(), getRequiredPlayerRange(), getMaxNearbyEntities(), getEntityType());
    }
}
