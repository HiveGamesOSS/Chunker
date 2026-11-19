package com.hivemc.chunker.conversion.encoding.bedrock.util;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import net.jpountz.xxhash.XXHashFactory;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Chunk MetaData used by Bedrock to indicate extra versioning for specific features (like flattening of properties).
 */
public final class BedrockChunkMetaData {
    private static final BedrockChunkMetaData V1_26_50 = new BedrockChunkMetaData(4);
    private static final BedrockChunkMetaData V1_26_60 = new BedrockChunkMetaData(6);

    private final CompoundTag entry = new CompoundTag(4);
    private final long hash;

    private BedrockChunkMetaData(int neighborAwareBlockUpgradeVersion) {
        // Add the properties
        entry.put("NeighborAwareBlockUpgradeVersion", neighborAwareBlockUpgradeVersion);
        entry.put("SkullFlatteningPerformed", (short) 1);
        entry.put("UnderwaterLavaLakeFixed", (short) 1);
        entry.put("WorldGenBelowZeroFixed", (short) 1);

        // Write the tag then generate the hash (needed to prevent it being ignored)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            Tag.encodeNamed(Writer.toBedrockNetworkWriter(dataOutputStream), "", entry);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        hash = XXHashFactory.fastestJavaInstance().hash64().hash(bytes, 0, bytes.length, 0);
    }

    /**
     * Get the metadata to write for a Bedrock version.
     *
     * @param version the version being written.
     * @return the metadata or null if none should be written.
     */
    @Nullable
    public static BedrockChunkMetaData getMetaForVersion(Version version) {
        if (version.isGreaterThanOrEqual(1, 26, 60)) return V1_26_60;
        if (version.isGreaterThanOrEqual(1, 26, 50)) return V1_26_50;
        return null;
    }

    /**
     * Encode the LevelChunkMetaDataDictionary value.
     *
     * @return the encoded dictionary.
     * @throws IOException if it failed to encode.
     */
    public byte[] encodeDictionary() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            Writer writer = Writer.toBedrockWriter(dataOutputStream);
            writer.writeInt(1); // Entry count
            writer.writeLong(hash);
            Tag.encodeNamed(writer, "", entry);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Get the MetaDataHash value to write for each chunk.
     *
     * @return the hash as little endian bytes.
     */
    public byte[] getHashBytes() {
        return ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(hash).array();
    }
}
