package com.hivemc.chunker.conversion.encoding.bedrock.util;

import com.google.common.primitives.Bytes;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Utility class to create keys for the LevelDB database.
 */
public class LevelDBKey {
    public static final byte[] ACTOR_PREFIX = "actorprefix".getBytes(StandardCharsets.UTF_8);
    public static final byte[] BIOME_IDS_TABLE = "BiomeIdsTable".getBytes(StandardCharsets.UTF_8);
    public static final byte[] DIGP_PREFIX = "digp".getBytes(StandardCharsets.UTF_8);
    public static final byte[] DIMENSION_NAME_ID_TABLE = "DimensionNameIdTable".getBytes(StandardCharsets.UTF_8);
    public static final byte[] LOCAL_PLAYER = "~local_player".getBytes(StandardCharsets.UTF_8);
    public static final byte[] MAP_PREFIX = "map_".getBytes(StandardCharsets.UTF_8);
    public static final byte[] PORTALS = "portals".getBytes(StandardCharsets.UTF_8);
    public static final byte[] POS_TRACK_DB = "PosTrackDB-0x".getBytes(StandardCharsets.UTF_8);
    public static final byte[] POS_TRACK_DB_LAST_ID = "PositionTrackDB-LastId".getBytes(StandardCharsets.UTF_8);

    /**
     * Check whether a database key identifies column data used during chunk discovery.
     *
     * @param key the database key.
     * @return true if the key has a supported column type and layout.
     */
    public static boolean isColumnKey(byte[] key) {
        int length = key.length;
        if (length != 9 && length != 10 && length != 13 && length != 14) return false;
        if (LevelDBKey.startsWith(key, LevelDBKey.MAP_PREFIX)
                || LevelDBKey.startsWith(key, LevelDBKey.ACTOR_PREFIX)
                || LevelDBKey.startsWith(key, LevelDBKey.DIGP_PREFIX)
                || Arrays.equals(key, LevelDBKey.LOCAL_PLAYER)) {
            return false;
        }

        boolean subChunk = length == 10 || length == 14;
        byte type = key[subChunk ? length - 2 : length - 1];
        if (subChunk) return type == LevelDBChunkType.SUB_CHUNK_PREFIX.getId();

        return type == LevelDBChunkType.DATA_2D.getId()
                || type == LevelDBChunkType.DATA_3D.getId()
                || type == LevelDBChunkType.ENTITY.getId()
                || type == LevelDBChunkType.BLOCK_ENTITY.getId();
    }

    /**
     * Read a signed little-endian integer without allocating a buffer.
     *
     * @param input the bytes containing the integer.
     * @param offset the first of four bytes to read.
     * @return the decoded integer.
     */
    public static int readLittleEndianInt(byte[] input, int offset) {
        return (input[offset] & 0xFF)
                | (input[offset + 1] & 0xFF) << 8
                | (input[offset + 2] & 0xFF) << 16
                | input[offset + 3] << 24;
    }

    /**
     * Check if a key starts with a prefix.
     *
     * @param input      the input.
     * @param startsWith the prefix.
     * @return true if the first bytes match the startsWith parameter.
     */
    public static boolean startsWith(byte[] input, byte[] startsWith) {
        if (input.length < startsWith.length) return false;
        for (int i = 0; i < startsWith.length; i++) {
            if (input[i] != startsWith[i]) return false;
        }

        // Equal
        return true;
    }

    /**
     * Extract a String suffix from an input given a prefix.
     *
     * @param input  the input bytes.
     * @param prefix the prefix to remove (only the length is used).
     * @return the string as UTF-8 with the prefix removed.
     */
    public static String extractSuffix(byte[] input, byte[] prefix) {
        return new String(input, prefix.length, input.length - prefix.length, StandardCharsets.UTF_8);
    }

    /**
     * Create a sub-chunk based key with a type.
     *
     * @param dimension      the dimension for key.
     * @param chunkCoordPair the co-ordinates of the column.
     * @param y              the y of the sub-chunk.
     * @param type           the type of the chunk key.
     * @return the composed key.
     */
    public static byte[] key(Dimension dimension, ChunkCoordPair chunkCoordPair, byte y, LevelDBChunkType type) {
        return key(dimension, chunkCoordPair, y, type.getId());
    }

    /**
     * Create a sub-chunk based key with a type.
     *
     * @param dimension      the dimension for key.
     * @param chunkCoordPair the co-ordinates of the column.
     * @param y              the y of the sub-chunk.
     * @param type           the type of the chunk key.
     * @return the composed key.
     */
    public static byte[] key(@NotNull Dimension dimension, ChunkCoordPair chunkCoordPair, byte y, byte type) {
        // Dimension is absent from the key if it's overworld
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + (dimension == Dimension.OVERWORLD ? 0 : 4) + 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Write the fields
        buffer.putInt(chunkCoordPair.chunkX());
        buffer.putInt(chunkCoordPair.chunkZ());

        // Write the dimension
        if (dimension != Dimension.OVERWORLD) {
            buffer.putInt(dimension.getBedrockID());
        }

        // Finally write the type and Y
        buffer.put(type);
        buffer.put(y);

        // Return the array
        return buffer.array();
    }

    /**
     * Create a chunk based key with a type.
     *
     * @param dimension      the dimension for key.
     * @param chunkCoordPair the co-ordinates of the column.
     * @param type           the type of the chunk key.
     * @return the composed key.
     */
    public static byte[] key(Dimension dimension, ChunkCoordPair chunkCoordPair, LevelDBChunkType type) {
        return key(dimension, chunkCoordPair, type.getId());
    }

    /**
     * Create a chunk based key with a type.
     *
     * @param dimension      the dimension for key.
     * @param chunkCoordPair the co-ordinates of the column.
     * @param type           the type of the chunk key.
     * @return the composed key.
     */
    public static byte[] key(@NotNull Dimension dimension, ChunkCoordPair chunkCoordPair, byte type) {
        // Dimension is absent from the key if it's overworld
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + (dimension == Dimension.OVERWORLD ? 0 : 4) + 1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Write the fields
        buffer.putInt(chunkCoordPair.chunkX());
        buffer.putInt(chunkCoordPair.chunkZ());

        // Write the dimension
        if (dimension != Dimension.OVERWORLD) {
            buffer.putInt(dimension.getBedrockID());
        }

        // Finally write the type
        buffer.put(type);

        // Return the array
        return buffer.array();
    }

    /**
     * Create a chunk based key with a prefix.
     *
     * @param prefix         the prefix for the key.
     * @param dimension      the dimension for key.
     * @param chunkCoordPair the co-ordinates of the column.
     * @return the composed key.
     */
    public static byte[] key(byte[] prefix, @NotNull Dimension dimension, ChunkCoordPair chunkCoordPair) {
        // Dimension is absent from the key if it's overworld
        ByteBuffer buffer = ByteBuffer.allocate(prefix.length + 4 + 4 + (dimension == Dimension.OVERWORLD ? 0 : 4));
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Write the prefix
        buffer.put(prefix);

        // Write the fields
        buffer.putInt(chunkCoordPair.chunkX());
        buffer.putInt(chunkCoordPair.chunkZ());

        // Write the dimension
        if (dimension != Dimension.OVERWORLD) {
            buffer.putInt(dimension.getBedrockID());
        }

        // Return the array
        return buffer.array();
    }

    /**
     * Concatenate two byte arrays.
     *
     * @param prefix the prefix.
     * @param value  the value to add to the prefix.
     * @return the combined byte array.
     */
    public static byte[] key(byte[] prefix, byte[] value) {
        return Bytes.concat(prefix, value);
    }
}
