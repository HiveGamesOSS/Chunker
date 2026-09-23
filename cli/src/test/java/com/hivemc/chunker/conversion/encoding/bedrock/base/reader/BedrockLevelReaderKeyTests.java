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
    public void testCurrentColumnKeysAreAccepted() {
        assertTrue(LevelDBKey.isColumnKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.DATA_3D)));
        assertTrue(LevelDBKey.isColumnKey(LevelDBKey.key(Dimension.NETHER, POSITION, LevelDBChunkType.DATA_3D)));
        assertTrue(LevelDBKey.isColumnKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.BLOCK_ENTITY)));
        assertTrue(LevelDBKey.isColumnKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.ENTITY)));
    }

    @Test
    public void testSubChunkTypePrecedesYCoordinate() {
        assertTrue(LevelDBKey.isColumnKey(LevelDBKey.key(
                Dimension.OVERWORLD,
                POSITION,
                (byte) 0,
                LevelDBChunkType.SUB_CHUNK_PREFIX
        )));
        assertTrue(LevelDBKey.isColumnKey(LevelDBKey.key(
                Dimension.NETHER,
                POSITION,
                (byte) -4,
                LevelDBChunkType.SUB_CHUNK_PREFIX
        )));
    }

    @Test
    public void testNonColumnKeysAreRejected() {
        assertFalse(LevelDBKey.isColumnKey("map_123456789".getBytes(StandardCharsets.UTF_8)));
        assertFalse(LevelDBKey.isColumnKey(LevelDBKey.LOCAL_PLAYER));
        assertFalse(LevelDBKey.isColumnKey("thirteenbytes!".getBytes(StandardCharsets.UTF_8)));
        assertFalse(LevelDBKey.isColumnKey(LevelDBKey.key(Dimension.OVERWORLD, POSITION, LevelDBChunkType.VERSION)));
    }
}
