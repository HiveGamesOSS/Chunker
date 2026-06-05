package com.hivemc.chunker.conversion.encoding.preview;

import com.hivemc.chunker.conversion.WorldConverter;
import com.hivemc.chunker.conversion.encoding.EncodingType;
import com.hivemc.chunker.conversion.encoding.base.reader.LevelReader;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.pruning.PruningConfig;
import com.hivemc.chunker.pruning.PruningRegion;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Production {@link RegionRgbaSource} that drives a fresh {@link WorldConverter} for each region
 * request, constraining conversion to a single Minecraft region via {@link PruningConfig}.
 *
 * <p>All non-block-data processing is disabled so each call is as fast as possible. The result is
 * captured by {@link RegionRgbaCapturingWriter} and returned as an {@code int[262144]} ARGB array
 * (row-major, 512 stride), or {@code null} when no chunks are present in the region.
 */
public class ChunkerWorldRegionRgbaSource implements RegionRgbaSource {

    /** Maximum time to wait for a single region conversion before giving up. */
    private static final long TIMEOUT_SECONDS = 30L;

    private final File worldDir;

    /**
     * Guards concurrent {@link #loadRegion} calls. Bedrock worlds back onto a single LevelDB
     * instance whose file lock (db/LOCK) the JVM refuses to acquire twice via
     * {@link java.nio.channels.OverlappingFileLockException}; without this mutex two preview
     * workers entering loadRegion concurrently both spin up a fresh BedrockLevelReader and the
     * second one fails immediately. Anvil worlds are unaffected by the lock contention but the
     * serialization cost there is negligible because the heavy work (downsampling, PNG encoding)
     * runs on the calling worker outside this method.
     */
    private final Object regionLoadLock = new Object();

    /**
     * Create a new source backed by the given world directory.
     *
     * @param worldDir the root folder of the Minecraft world.
     */
    public ChunkerWorldRegionRgbaSource(File worldDir) {
        this.worldDir = worldDir;
    }

    @Override
    public int[] loadRegion(String world, int rx, int rz) throws IOException {
        synchronized (regionLoadLock) {
            return loadRegionLocked(world, rx, rz);
        }
    }

    private int[] loadRegionLocked(String world, int rx, int rz) throws IOException {
        File dbDir = new File(worldDir, "db");
        if (dbDir.isDirectory()) {
            // Bedrock: every region opens the world's LevelDB afresh (the converter frees — and so
            // closes — the reader after each region). That open can transiently fail to acquire
            // db/LOCK, e.g. when the previous region's handle hasn't been released yet, so retry
            // briefly rather than dropping the tile.
            return PreviewLevelDb.withLockRetry(dbDir, () -> readRegion(world, rx, rz));
        }
        return readRegion(world, rx, rz);
    }

    private int[] readRegion(String world, int rx, int rz) throws IOException {
        // Compute the chunk range covered by this region
        int minCx = rx * 32;
        int minCz = rz * 32;
        int maxCx = minCx + 31;
        int maxCz = minCz + 31;

        // Fresh converter with a random session id — no persistent state needed
        WorldConverter worldConverter = new WorldConverter(UUID.randomUUID());

        // Resolve the requested dimension
        Dimension targetDimension = worldConverter.getDimensionRegistry().getByIdentifier(world);
        if (targetDimension == null) {
            return null;
        }

        // Constrain conversion to the single region
        worldConverter.setPruningConfigs(Map.of(
                targetDimension,
                new PruningConfig(true, List.of(new PruningRegion(minCx, minCz, maxCx, maxCz)))
        ));

        // Disable all processing that isn't needed for block-colour extraction
        worldConverter.setProcessMaps(false);
        worldConverter.setProcessLighting(false);
        worldConverter.setProcessHeightMap(false);
        worldConverter.setProcessBlockEntities(false);
        worldConverter.setProcessColumnPreTransform(false);
        worldConverter.setProcessEntities(false);
        worldConverter.setProcessBiomes(false);
        worldConverter.setProcessItems(false);

        // Destination pixel buffer
        int[] dst = new int[262144];
        RegionRgbaCapturingWriter writer = new RegionRgbaCapturingWriter(rx, rz, dst);

        // Attempt to detect a reader for this world directory
        Optional<? extends LevelReader> readerOpt = EncodingType.findReader(worldDir, worldConverter);
        if (readerOpt.isEmpty()) {
            return null;
        }

        // Drive the conversion synchronously with a timeout
        try {
            worldConverter.convert(readerOpt.get(), writer)
                    .future()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new IOException("Timed out reading region (" + rx + ", " + rz + ") from " + worldDir, e);
        } catch (Exception e) {
            // Unwrap and re-throw as IOException so callers have a stable exception contract
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new IOException("Failed to read region (" + rx + ", " + rz + ") from " + worldDir + ": " + cause.getMessage(), cause);
        }

        // Return null when no block data was written (all pixels are transparent/zero)
        for (int pixel : dst) {
            if (pixel != 0) {
                return dst;
            }
        }
        return null;
    }
}
