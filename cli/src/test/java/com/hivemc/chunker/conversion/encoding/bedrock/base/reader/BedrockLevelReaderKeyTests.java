package com.hivemc.chunker.conversion.encoding.bedrock.base.reader;

import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BedrockLevelReaderKeyTests {
    private static final ChunkCoordPair POSITION = new ChunkCoordPair(-1234, 5678);

    @Test
    public void testVersionKeysAreAccepted() {
        assertTrue(LevelDBKey.isColumnVersionKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.VERSION)));
        assertTrue(LevelDBKey.isColumnVersionKey(LevelDBKey.key(Dimension.NETHER, POSITION, LevelDBChunkType.VERSION)));
        assertTrue(LevelDBKey.isColumnVersionKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.LEGACY_VERSION)));
        assertTrue(LevelDBKey.isColumnVersionKey(LevelDBKey.key(Dimension.THE_END, POSITION, LevelDBChunkType.LEGACY_VERSION)));
    }

    @Test
    public void testLeftoverColumnDataIsRejected() {
        // Without a version key Bedrock treats the column as absent and regenerates it, so it must not be discovered
        assertFalse(LevelDBKey.isColumnVersionKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.DATA_3D)));
        assertFalse(LevelDBKey.isColumnVersionKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.DATA_2D)));
        assertFalse(LevelDBKey.isColumnVersionKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.BLOCK_ENTITY)));
        assertFalse(LevelDBKey.isColumnVersionKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.ENTITY)));
        assertFalse(LevelDBKey.isColumnVersionKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.FINALIZED_STATE)));
        assertFalse(LevelDBKey.isColumnVersionKey(LevelDBKey.key(
                Dimension.NETHER,
                POSITION,
                (byte) 0,
                LevelDBChunkType.SUB_CHUNK_PREFIX
        )));
    }

    @Test
    public void testNonColumnKeysAreRejected() {
        assertFalse(LevelDBKey.isColumnVersionKey("map_123456789".getBytes(StandardCharsets.UTF_8)));
        assertFalse(LevelDBKey.isColumnVersionKey(LevelDBKey.LOCAL_PLAYER));
        assertFalse(LevelDBKey.isColumnVersionKey("thirteenbytes!".getBytes(StandardCharsets.UTF_8)));
    }
}
