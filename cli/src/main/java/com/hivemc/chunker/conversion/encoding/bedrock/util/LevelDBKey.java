package com.hivemc.chunker.conversion.encoding.bedrock.util;

import com.google.common.primitives.Bytes;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to create keys for the LevelDB database.
 */
public class LevelDBKey {
    public static final byte[] ACTOR_PREFIX = "actorprefix".getBytes(StandardCharsets.UTF_8);
    public static final byte[] DIGP_PREFIX = "digp".getBytes(StandardCharsets.UTF_8);
    public static final byte[] LOCAL_PLAYER = "~local_player".getBytes(StandardCharsets.UTF_8);
    public static final byte[] MAP_PREFIX = "map_".getBytes(StandardCharsets.UTF_8);
    public static final byte[] PORTALS = "portals".getBytes(StandardCharsets.UTF_8);

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
    public static byte[] key(Dimension dimension, ChunkCoordPair chunkCoordPair, byte y, byte type) {
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
    public static byte[] key(Dimension dimension, ChunkCoordPair chunkCoordPair, byte type) {
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
    public static byte[] key(byte[] prefix, Dimension dimension, ChunkCoordPair chunkCoordPair) {
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
