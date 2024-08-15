package com.hivemc.chunker.conversion.intermediate.level;

import com.hivemc.chunker.conversion.intermediate.level.map.ChunkerMap;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * A level containing useful metadata about the world including settings, player data, portals, maps.
 */
public class ChunkerLevel {
    private ChunkerLevelSettings settings;
    private @Nullable ChunkerLevelPlayer player;
    private List<ChunkerMap> maps;
    private @Nullable CompoundTag originalLevelData;
    private List<ChunkerPortal> portals;

    /**
     * Create a new level.
     *
     * @param settings          the settings used for the world.
     * @param player            the local player in the world (null if not present).
     * @param maps              a list of in-game maps.
     * @param originalLevelData the original level data used for copying (null if not present).
     * @param portals           a list of portals inside the world.
     */
    public ChunkerLevel(ChunkerLevelSettings settings, @Nullable ChunkerLevelPlayer player, List<ChunkerMap> maps, @Nullable CompoundTag originalLevelData, List<ChunkerPortal> portals) {
        this.settings = settings;
        this.player = player;
        this.maps = maps;
        this.originalLevelData = originalLevelData;
        this.portals = portals;
    }

    /**
     * Create a new ChunkerLevel.
     */
    public ChunkerLevel() {
    }

    /**
     * Get the settings used for the level.
     *
     * @return the settings to be used.
     */
    public ChunkerLevelSettings getSettings() {
        return settings;
    }

    /**
     * Set the settings to be used for the level.
     *
     * @param settings the settings to be encoded.
     */
    public void setSettings(ChunkerLevelSettings settings) {
        this.settings = settings;
    }

    /**
     * Set the local player for this level.
     *
     * @return the local player or null if one is not present.
     */
    @Nullable
    public ChunkerLevelPlayer getPlayer() {
        return player;
    }

    /**
     * Set the local player for this level.
     *
     * @param player the player or null if one is not present.
     */
    public void setPlayer(@Nullable ChunkerLevelPlayer player) {
        this.player = player;
    }

    /**
     * A list of all the item maps used inside the world.
     *
     * @return a list of the maps.
     */
    public List<ChunkerMap> getMaps() {
        return maps;
    }

    /**
     * Set the item maps used in the level.
     *
     * @param maps a list of the maps.
     */
    public void setMaps(List<ChunkerMap> maps) {
        this.maps = maps;
    }

    /**
     * Get the original level data (used for NBT copying).
     *
     * @return the original data, null if not present.
     */
    @Nullable
    public CompoundTag getOriginalLevelData() {
        return originalLevelData;
    }

    /**
     * Set the original level data NBT, used for if NBT copying is enabled.
     *
     * @param originalLevelData the original data.
     */
    public void setOriginalLevelData(@Nullable CompoundTag originalLevelData) {
        this.originalLevelData = originalLevelData;
    }

    /**
     * Get the portals used inside the level.
     *
     * @return a list of the portals.
     */
    public List<ChunkerPortal> getPortals() {
        return portals;
    }

    /**
     * Set the portals used inside the level.
     *
     * @param portals the portals.
     */
    public void setPortals(List<ChunkerPortal> portals) {
        this.portals = portals;
    }

    /**
     * Find the map index based on a mapId.
     *
     * @param originalMapId the mapId to look for.
     * @return an integer index of the map otherwise empty if one was not found.
     */
    public Optional<Integer> findMapIndexByOriginalID(long originalMapId) {
        for (int i = 0; i < maps.size(); i++) {
            ChunkerMap map = maps.get(i);
            if (map.getOriginalId() != null && originalMapId == map.getOriginalId()) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    /**
     * Get a map by a specific array index.
     *
     * @param index the index in the map array.
     * @return the map or empty if there isn't a map at the index.
     */
    public Optional<ChunkerMap> getMapByIndex(int index) {
        if (index < 0 || index >= maps.size()) return Optional.empty();
        return Optional.ofNullable(maps.get(index));
    }

    @Override
    public String toString() {
        return "ChunkerLevel{" +
                "settings=" + getSettings() +
                ", player=" + getPlayer() +
                ", maps=" + getMaps() +
                ", originalLevelData=" + getOriginalLevelData() +
                ", portals=" + getPortals() +
                '}';
    }
}
