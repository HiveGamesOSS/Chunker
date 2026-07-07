package com.hivemc.chunker.conversion.encoding.preview;

import java.util.Objects;

/**
 * Identifies a single tile in the preview tile pyramid.
 *
 * @param world the dimension identifier (e.g. "minecraft:overworld")
 * @param lod   the level-of-detail (0 = native, negative = downsampled)
 * @param tx    tile x at this LOD
 * @param tz    tile z at this LOD
 */
public record PreviewTileKey(String world, int lod, int tx, int tz) {
    public PreviewTileKey {
        Objects.requireNonNull(world, "world");
    }

    /**
     * @return the file name used when persisting this tile under the preview folder.
     */
    public String toFileName() {
        return world.replace(':', '_') + "." + lod + "." + tx + "." + tz + ".png";
    }
}
