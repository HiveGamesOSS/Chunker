package com.hivemc.chunker.conversion.encoding.settings;

import com.hivemc.chunker.conversion.encoding.base.writer.ColumnWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

import java.util.Set;

/**
 * Records world data then submits it to the parent.
 */
public class SettingsWorldWriter implements WorldWriter {
    private final SettingsLevelWriter parent;

    /**
     * Create a new world writer.
     *
     * @param parent the parent to send the world data to.
     */
    public SettingsWorldWriter(SettingsLevelWriter parent) {
        this.parent = parent;
    }

    @Override
    public ColumnWriter writeWorld(ChunkerWorld chunkerWorld) {
        // Create the world entry
        WorldData worldData = new WorldData();
        worldData.dimension = chunkerWorld.getDimension();
        worldData.regions = chunkerWorld.getRegions();

        // Add it to the list
        parent.add(worldData);

        // Return a null writer, this will mean that the columns don't get read
        return null;
    }

    /**
     * World data containing the regions and dimension.
     */
    public static class WorldData {
        public Set<RegionCoordPair> regions;
        public Dimension dimension;
    }
}
