package com.hivemc.chunker.conversion.encoding.preview;

import com.hivemc.chunker.conversion.encoding.EncodingType;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.writer.ColumnWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.LevelWriter;
import com.hivemc.chunker.conversion.encoding.base.writer.WorldWriter;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.world.ChunkerWorld;
import it.unimi.dsi.fastutil.Pair;

import java.util.Set;

/**
 * A minimal LevelWriter that captures per-column ARGB pixels into a flat int[262144] array
 * representing a single 512×512 region image (row-major, 512 stride).
 *
 * <p>Only columns whose chunk position falls inside the target region {@code (rx, rz)} are
 * processed. All other writer lifecycle methods are no-ops.
 */
public class RegionRgbaCapturingWriter implements LevelWriter {

    private final int rx;
    private final int rz;
    private final int[] dst;

    /**
     * Create a new capturing writer.
     *
     * @param rx  the region X coordinate to capture.
     * @param rz  the region Z coordinate to capture.
     * @param dst a pre-allocated {@code int[262144]} destination array (row-major, 512 stride).
     */
    public RegionRgbaCapturingWriter(int rx, int rz, int[] dst) {
        this.rx = rx;
        this.rz = rz;
        this.dst = dst;
    }

    @Override
    public WorldWriter writeLevel(ChunkerLevel chunkerLevel) {
        return new CapturingWorldWriter();
    }

    @Override
    public EncodingType getEncodingType() {
        return EncodingType.PREVIEW;
    }

    @Override
    public Version getVersion() {
        return new Version(1, 0, 0);
    }

    @Override
    public Set<ChunkerBiome.ChunkerVanillaBiome> getSupportedBiomes() {
        return Set.of(); // Biomes aren't used in the preview capturing writer
    }

    // -------------------------------------------------------------------------
    // Inner classes implementing the writer chain
    // -------------------------------------------------------------------------

    private class CapturingWorldWriter implements WorldWriter {
        @Override
        public ColumnWriter writeWorld(ChunkerWorld chunkerWorld) {
            return new CapturingColumnWriter();
        }
    }

    private class CapturingColumnWriter implements ColumnWriter {
        @Override
        public void writeColumn(ChunkerColumn chunkerColumn) {
            // Only process columns that belong to the target region
            if (chunkerColumn.getPosition().regionX() != rx
                    || chunkerColumn.getPosition().regionZ() != rz) {
                return;
            }

            int chunkX = chunkerColumn.getPosition().chunkX();
            int chunkZ = chunkerColumn.getPosition().chunkZ();

            // Pixel origin of this 16×16 chunk block inside the 512×512 image
            int pxBaseX = (chunkX & 31) << 4;  // column in the image [0, 511]
            int pxBaseZ = (chunkZ & 31) << 4;  // row in the image    [0, 511]

            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    Pair<Integer, ChunkerBlockIdentifier> block =
                            chunkerColumn.getHighestBlock(localX, localZ, ChunkerBlockIdentifier::hasRGBColor);
                    if (block != null) {
                        int rgb = block.value().getRGBColor();
                        int argb = rgb == 0 ? 0 : 0xFF000000 | rgb;
                        // Row-major layout: row = (pxBaseZ + localZ), col = (pxBaseX + localX)
                        dst[(pxBaseZ + localZ) * 512 + (pxBaseX + localX)] = argb;
                    }
                }
            }
        }
    }
}
