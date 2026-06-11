package com.hivemc.chunker.conversion.encoding.preview;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Lightweight tests for ChunkerWorldRegionRgbaSource. Full integration validation
 * against real fixture worlds is exercised manually via Task 20 smoke testing.
 */
public class ChunkerWorldRegionRgbaSourceTests {
    @Test
    public void testReturnsNullForMissingWorldDirectory(@TempDir Path tmp) throws Exception {
        File worldDir = tmp.resolve("does-not-exist").toFile();
        ChunkerWorldRegionRgbaSource source = new ChunkerWorldRegionRgbaSource(worldDir);
        assertNull(source.loadRegion("minecraft:overworld", 0, 0));
    }
}
