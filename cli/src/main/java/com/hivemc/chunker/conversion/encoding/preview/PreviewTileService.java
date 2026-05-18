package com.hivemc.chunker.conversion.encoding.preview;

import com.hivemc.chunker.cli.messenger.messaging.response.TileErrorResponse;
import com.hivemc.chunker.cli.messenger.messaging.response.TileReadyResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Long-lived per-session service that lazily generates preview tile PNGs on demand.
 * Owns a worker pool, a bounded LRU of decoded region ARGB arrays, and a dedup set so
 * repeated client requests for the same tile do not cause duplicate work.
 */
public final class PreviewTileService {
    public interface EventListener {
        void onTileReady(TileReadyResponse r);
        void onTileError(TileErrorResponse r);
    }

    private final File outputFolder;
    private final PreviewMapBin mapBin;
    private final RegionRgbaSource source;
    private final PreviewTileCache cache;
    private final ExecutorService workers;
    private final Set<PreviewTileKey> inFlight = ConcurrentHashMap.newKeySet();
    private final Set<PreviewTileKey> writtenTiles = ConcurrentHashMap.newKeySet();
    private volatile EventListener listener = NULL_LISTENER;

    public PreviewTileService(File outputFolder, PreviewMapBin mapBin, RegionRgbaSource source,
                              PreviewTileCache cache, int workerCount) {
        this.outputFolder = outputFolder;
        this.mapBin = mapBin;
        this.source = source;
        this.cache = cache;
        this.workers = Executors.newFixedThreadPool(workerCount, runnable -> {
            Thread t = new Thread(runnable, "preview-tile-worker");
            t.setDaemon(true);
            return t;
        });
    }

    public void setEventListener(EventListener listener) {
        this.listener = listener == null ? NULL_LISTENER : listener;
    }

    public void enqueueRange(String world, int lod, int minTx, int minTz, int maxTx, int maxTz) {
        for (int tx = minTx; tx <= maxTx; tx++) {
            for (int tz = minTz; tz <= maxTz; tz++) {
                if (mapBin.isTileEmpty(world, lod, tx, tz)) continue;
                PreviewTileKey key = new PreviewTileKey(world, lod, tx, tz);
                if (writtenTiles.contains(key)) {
                    emitReady(key);
                    continue;
                }
                if (!inFlight.add(key)) continue; // dedup
                workers.submit(() -> processTile(key));
            }
        }
    }

    public void cancel(String world, Integer lod) {
        inFlight.removeIf(k -> k.world().equals(world) && (lod == null || k.lod() == lod));
    }

    public void shutdown() {
        workers.shutdownNow();
    }

    private void processTile(PreviewTileKey key) {
        try {
            if (!inFlight.contains(key)) return; // cancelled before start
            int[] rgba = loadOrAggregate(key);
            if (rgba == null) {
                emitError(key, "missing-data");
                return;
            }
            File outFile = new File(outputFolder, key.toFileName());
            writePng(rgba, outFile);
            writtenTiles.add(key);
            cache.put(key, rgba);
            emitReady(key);
        } catch (Throwable t) {
            emitError(key, t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage());
        } finally {
            inFlight.remove(key);
        }
    }

    private int[] loadOrAggregate(PreviewTileKey key) throws IOException {
        int[] cached = cache.get(key);
        if (cached != null) return cached;
        if (key.lod() == 0) {
            return loadLodZero(key.world(), key.tx(), key.tz());
        }
        int childLod = key.lod() + 1;
        int cx = key.tx() << 1;
        int cz = key.tz() << 1;
        int[] tl = ensureChild(key.world(), childLod, cx,     cz);
        int[] tr = ensureChild(key.world(), childLod, cx + 1, cz);
        int[] bl = ensureChild(key.world(), childLod, cx,     cz + 1);
        int[] br = ensureChild(key.world(), childLod, cx + 1, cz + 1);
        if (tl == null && tr == null && bl == null && br == null) return null;
        int[] empty = new int[PreviewTileDownsampler.TILE_SIZE * PreviewTileDownsampler.TILE_SIZE];
        return PreviewTileDownsampler.aggregate(
                tl == null ? empty : tl,
                tr == null ? empty : tr,
                bl == null ? empty : bl,
                br == null ? empty : br
        );
    }

    private int[] ensureChild(String world, int lod, int tx, int tz) throws IOException {
        if (mapBin.isTileEmpty(world, lod, tx, tz)) return null;
        PreviewTileKey ck = new PreviewTileKey(world, lod, tx, tz);
        int[] cached = cache.get(ck);
        if (cached != null) return cached;
        int[] data = loadOrAggregate(ck);
        if (data != null) cache.put(ck, data);
        return data;
    }

    private int[] loadLodZero(String world, int rx, int rz) throws IOException {
        PreviewTileKey key = new PreviewTileKey(world, 0, rx, rz);
        int[] cached = cache.get(key);
        if (cached != null) return cached;
        int[] data = source.loadRegion(world, rx, rz);
        if (data == null) return null;
        cache.put(key, data);
        return data;
    }

    private void writePng(int[] argb, File outFile) throws IOException {
        int size = PreviewTileDownsampler.TILE_SIZE;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, size, size, argb, 0, size);
        ImageIO.write(image, "png", outFile);
    }

    private void emitReady(PreviewTileKey key) {
        listener.onTileReady(new TileReadyResponse(
                null, key.world(), key.lod(), key.tx(), key.tz(), key.toFileName()
        ));
    }

    private void emitError(PreviewTileKey key, String reason) {
        listener.onTileError(new TileErrorResponse(
                null, key.world(), key.lod(), key.tx(), key.tz(), reason
        ));
    }

    private static final EventListener NULL_LISTENER = new EventListener() {
        @Override public void onTileReady(TileReadyResponse r) {}
        @Override public void onTileError(TileErrorResponse r) {}
    };
}
