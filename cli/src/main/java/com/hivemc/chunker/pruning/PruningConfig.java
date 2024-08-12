package com.hivemc.chunker.pruning;

import java.util.ArrayList;
import java.util.List;

/**
 * The pruning configuration for a world on what regions should be kept/deleted.
 */
public class PruningConfig {
    private boolean include = true;
    private List<PruningRegion> regions = new ArrayList<>();

    /**
     * Create an empty pruning config.
     */
    public PruningConfig() {
    }

    /**
     * Create a pruning config for the world.
     *
     * @param include whether the regions should be included (or excluded if false).
     * @param regions the regions to use for the pruning config.
     */
    public PruningConfig(boolean include, List<PruningRegion> regions) {
        this.include = include;
        this.regions = regions;
    }

    /**
     * Whether the region should be included or excluded.
     *
     * @return true if the region should be included and outside of it should be excluded.
     */
    public boolean isInclude() {
        return include;
    }

    /**
     * Get a list of regions that should be kept/deleted.
     *
     * @return the list of the regions.
     */
    public List<PruningRegion> getRegions() {
        return regions;
    }
}
