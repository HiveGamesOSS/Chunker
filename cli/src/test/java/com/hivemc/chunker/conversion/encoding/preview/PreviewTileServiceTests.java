package com.hivemc.chunker.conversion.encoding.preview;

import com.hivemc.chunker.cli.messenger.messaging.response.TileErrorResponse;
import com.hivemc.chunker.cli.messenger.messaging.response.TileReadyResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the lazy preview tile service at LOD 0: enqueueRange, dedup, empty-tile skipping.
 */
public class PreviewTileServiceTests {

    private static class FakeSource implements RegionRgbaSource {
        final Map<String, int[]> data = new HashMap<>();
        final AtomicInteger loads = new AtomicInteger();

        @Override
        public int[] loadRegion(String world, int rx, int rz) {
            loads.incrementAndGet();
            return data.get(world + ":" + rx + ":" + rz);
        }
    }

    private static int[] solid(int argb) {
        int[] a = new int[262144];
        Arrays.fill(a, argb);
        return a;
    }

    private static PreviewMapBin oneRegionMap(Path tmp) throws Exception {
        PreviewMapBin.Builder b = new PreviewMapBin.Builder();
        BitSet present = new BitSet(1024);
        present.set(0);
        b.addWorld(0, "minecraft:overworld", 0, 0, 0, 0).addRegion(0, 0, 0, present);
        File out = tmp.resolve("map.bin").toFile();
        b.writeTo(out);
        return PreviewMapBin.read(out);
    }

    @Test
    public void testEmitsTileReadyForLodZeroRequest(@TempDir Path tmp) throws Exception {
        FakeSource src = new FakeSource();
        src.data.put("minecraft:overworld:0:0", solid(0xFF00FF00));

        File out = tmp.resolve("preview").toFile();
        assertTrue(out.mkdirs());
        BlockingQueue<TileReadyResponse> events = new LinkedBlockingQueue<>();

        PreviewTileService svc = new PreviewTileService(out, oneRegionMap(tmp), src, new PreviewTileCache(8), 2);
        svc.setEventListener(new PreviewTileService.EventListener() {
            @Override public void onTileReady(TileReadyResponse r) { events.add(r); }
            @Override public void onTileError(TileErrorResponse r) { }
        });

        svc.enqueueRange("minecraft:overworld", 0, 0, 0, 0, 0);

        TileReadyResponse r = events.poll(2, TimeUnit.SECONDS);
        assertNotNull(r);
        assertEquals("minecraft:overworld", r.world());
        assertEquals(0, r.lod());
        assertEquals(0, r.tx());
        assertEquals(0, r.tz());
        assertTrue(new File(out, "minecraft_overworld.0.0.0.png").exists());
        svc.shutdown();
    }

    @Test
    public void testSkipsTilesEmptyPerMapBin(@TempDir Path tmp) throws Exception {
        FakeSource src = new FakeSource();
        File out = tmp.resolve("preview").toFile();
        assertTrue(out.mkdirs());
        BlockingQueue<TileReadyResponse> events = new LinkedBlockingQueue<>();

        PreviewMapBin map = oneRegionMap(tmp);
        PreviewTileService svc = new PreviewTileService(out, map, src, new PreviewTileCache(8), 2);
        svc.setEventListener(new PreviewTileService.EventListener() {
            @Override public void onTileReady(TileReadyResponse r) { events.add(r); }
            @Override public void onTileError(TileErrorResponse r) { }
        });

        // Region (1,0) is empty. Requesting tile (lod=0, tx=1, tz=0) must not load anything.
        svc.enqueueRange("minecraft:overworld", 0, 1, 0, 1, 0);
        Thread.sleep(150);
        assertEquals(0, src.loads.get());
        assertTrue(events.isEmpty());
        svc.shutdown();
    }

    @Test
    public void testDedupsConcurrentRequestsForSameTile(@TempDir Path tmp) throws Exception {
        FakeSource src = new FakeSource();
        src.data.put("minecraft:overworld:0:0", solid(0xFFFF0000));
        File out = tmp.resolve("preview").toFile();
        assertTrue(out.mkdirs());

        PreviewTileService svc = new PreviewTileService(out, oneRegionMap(tmp), src, new PreviewTileCache(8), 2);
        for (int i = 0; i < 10; i++) {
            svc.enqueueRange("minecraft:overworld", 0, 0, 0, 0, 0);
        }
        Thread.sleep(300);
        assertEquals(1, src.loads.get());
        svc.shutdown();
    }

    @Test
    public void testLodMinusOneAggregatesFourChildren(@TempDir Path tmp) throws Exception {
        FakeSource src = new FakeSource();
        src.data.put("minecraft:overworld:0:0", solid(0xFFFF0000));
        src.data.put("minecraft:overworld:1:0", solid(0xFF00FF00));
        src.data.put("minecraft:overworld:0:1", solid(0xFF0000FF));
        src.data.put("minecraft:overworld:1:1", solid(0xFFFFFFFF));

        PreviewMapBin.Builder b = new PreviewMapBin.Builder();
        BitSet present = new BitSet(1024);
        present.set(0);
        b.addWorld(0, "minecraft:overworld", 0, 0, 1, 1);
        b.addRegion(0, 0, 0, present);
        b.addRegion(0, 1, 0, present);
        b.addRegion(0, 0, 1, present);
        b.addRegion(0, 1, 1, present);
        File mapFile = tmp.resolve("map.bin").toFile();
        b.writeTo(mapFile);
        PreviewMapBin map = PreviewMapBin.read(mapFile);

        File out = tmp.resolve("preview").toFile();
        assertTrue(out.mkdirs());
        BlockingQueue<TileReadyResponse> events = new LinkedBlockingQueue<>();

        PreviewTileService svc = new PreviewTileService(out, map, src, new PreviewTileCache(16), 2);
        svc.setEventListener(new PreviewTileService.EventListener() {
            @Override public void onTileReady(TileReadyResponse r) { events.add(r); }
            @Override public void onTileError(TileErrorResponse r) { }
        });

        svc.enqueueRange("minecraft:overworld", -1, 0, 0, 0, 0);

        // Expect the LOD -1 tile_ready (children may also be emitted; drain until we see it).
        boolean sawAggregated = false;
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline) {
            TileReadyResponse r = events.poll(100, TimeUnit.MILLISECONDS);
            if (r != null && r.lod() == -1) { sawAggregated = true; break; }
        }
        assertTrue(sawAggregated, "Expected a tile_ready at LOD -1");
        assertTrue(new File(out, "minecraft_overworld.-1.0.0.png").exists());
        svc.shutdown();
    }
}
