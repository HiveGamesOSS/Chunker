package com.hivemc.chunker.conversion.encoding.preview;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the PreviewTileKey value type used to identify a tile in the preview pyramid.
 */
public class PreviewTileKeyTests {
    @Test
    public void testEqualityByValue() {
        PreviewTileKey a = new PreviewTileKey("minecraft:overworld", -3, 2, -1);
        PreviewTileKey b = new PreviewTileKey("minecraft:overworld", -3, 2, -1);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testInequalityOnAnyField() {
        PreviewTileKey base = new PreviewTileKey("minecraft:overworld", -3, 2, -1);
        assertNotEquals(base, new PreviewTileKey("minecraft:nether", -3, 2, -1));
        assertNotEquals(base, new PreviewTileKey("minecraft:overworld", -2, 2, -1));
        assertNotEquals(base, new PreviewTileKey("minecraft:overworld", -3, 3, -1));
        assertNotEquals(base, new PreviewTileKey("minecraft:overworld", -3, 2, 0));
    }

    @Test
    public void testToFileNameReplacesColonAndIncludesSignedLod() {
        PreviewTileKey k = new PreviewTileKey("minecraft:overworld", -3, 2, -1);
        assertEquals("minecraft_overworld.-3.2.-1.png", k.toFileName());
    }

    @Test
    public void testNullWorldRejected() {
        assertThrows(NullPointerException.class, () -> new PreviewTileKey(null, 0, 0, 0));
    }
}
