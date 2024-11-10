package com.hivemc.chunker.conversion.encoding.bedrock.v1_18_30.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockChunkWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockWorldWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ColumnWriter extends com.hivemc.chunker.conversion.encoding.bedrock.v1_18.writer.ColumnWriter {
    public ColumnWriter(BedrockWorldWriter parent, Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension) {
        super(parent, converter, resolvers, database, dimension);
    }

    public static byte[] generateStorageKeyForEntity(long uniqueEntityID) {
        // Turn the unique ID into a storage key
        // The top 32 bits get flipped, and then 0x100000000 gets added
        long storageKey = 0x100000000L + (uniqueEntityID ^ 0xFFFFFFFF00000000L);

        // Write to a buffer (Bedrock uses little endian)
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(storageKey);
        return buffer.array();
    }

    @Override
    protected void writeEntities(ChunkerColumn column) throws Exception {
        // Write modern entities
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
                 DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
                // Write each entity as keyed entity with the keys in the DIGP entry
                for (Entity entity : column.getEntities()) {
                    try {
                        // Write the entity
                        byte[] key = writeKeyedEntity(column, entity);

                        // Write the key
                        if (key != null) {
                            writerStream.write(key);
                        }
                    } catch (Exception e) {
                        converter.logNonFatalException(new Exception("Failed to process Entity " + entity, e));
                    }
                }
            }

            // Write the byte array to the key
            database.put(LevelDBKey.key(LevelDBKey.DIGP_PREFIX, dimension, column.getPosition()), byteArrayOutputStream.toByteArray());
        }
    }

    protected byte @Nullable [] writeKeyedEntity(ChunkerColumn column, Entity entity) throws Exception {
        byte[] key = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
                 DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
                // Process the tag
                CompoundTag tag = writeEntity(column, entity);
                if (tag != null) {
                    // Generate a unique ID for the entity if it doesn't have one
                    long uniqueEntityID;
                    if (!tag.contains("UniqueID")) {
                        uniqueEntityID = parent.generateUniqueEntityID();
                        tag.put("UniqueID", uniqueEntityID);
                    } else {
                        uniqueEntityID = tag.getLong("UniqueID");
                    }

                    // Generate the storage key to be used for lookup (unique for the world start count)
                    key = generateStorageKeyForEntity(uniqueEntityID);

                    // Encode
                    Tag.encodeNamed(Writer.toBedrockWriter(writerStream), "", tag);
                }
            }

            // Write the byte array to the actor prefix + key
            if (key != null) {
                database.put(LevelDBKey.key(LevelDBKey.ACTOR_PREFIX, key), byteArrayOutputStream.toByteArray());
            }

            return key;
        }
    }

    protected byte getBlendingVersion() {
        return 0;
    }

    protected byte getChunkVersion() {
        return 40;
    }

    @Override
    protected void writeMetadata(ChunkerColumn chunkerColumn) throws Exception {
        try (WriteBatch writeBatch = database.createWriteBatch()) {
            if (isWriteBlendingData()) {
                // Save Caves and Cliffs Blending (0x3d) (legacy)
                writeBatch.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), LevelDBChunkType.GENERATED_PRE_CAVES_AND_CLIFFS_BLENDING), new byte[]{0});

                // Save new version (40) - New entity storage
                writeBatch.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), LevelDBChunkType.VERSION), new byte[]{getChunkVersion()});

                // Save BlendingVersion (0x40) - If not correctly versioned, this will cause the map to generate badly made edges
                writeBatch.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), LevelDBChunkType.BLENDING_DATA), new byte[]{0, getBlendingVersion()});
            }

            // Save legacy version
            writeBatch.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), LevelDBChunkType.LEGACY_VERSION), new byte[]{7});

            // Write the batch
            database.write(writeBatch);
        }
    }

    @Override
    public BedrockChunkWriter createChunkWriter(ChunkerColumn column) {
        return new ChunkWriter(converter, resolvers, database, dimension, column);
    }
}
