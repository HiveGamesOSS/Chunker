package com.hivemc.chunker.conversion.encoding.bedrock.v1_18_30.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockChunkReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.iq80.leveldb.DB;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Arrays;

public class ColumnReader extends com.hivemc.chunker.conversion.encoding.bedrock.v1_18.reader.ColumnReader {
    public ColumnReader(BedrockResolvers resolvers, Converter converter, DB database, Dimension dimension, ChunkCoordPair columnCoords) {
        super(resolvers, converter, database, dimension, columnCoords);
    }

    @Override
    protected void readEntities(ChunkerColumn column) throws Exception {
        // Modern format for entities, a list of IDs is provided by digp which is then looked up
        byte[] value = database.get(LevelDBKey.key(LevelDBKey.DIGP_PREFIX, dimension, column.getPosition()));
        if (value == null) {
            // Fallback to legacy entity reading
            super.readEntities(column);
            return;
        }

        // While there is bytes loop and read the entities
        try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(value);
             DataInputStream readerStream = new DataInputStream(fileInputStream)) {
            Reader reader = Reader.toBedrockReader(readerStream);

            byte[] entityKey = new byte[8];
            while (fileInputStream.available() > 0) {
                try {
                    // Read the key into our array
                    reader.readBytes(entityKey);

                    // Lookup the entity and read it
                    readEntity(column, entityKey);
                } catch (Exception e) {
                    converter.logNonFatalException(new Exception("Failed to process Entity ID " + Arrays.toString(entityKey), e));
                }
            }
        }
    }

    protected void readEntity(ChunkerColumn column, byte[] entityKey) throws Exception {
        // Lookup the entry for the key
        byte[] entityValue = database.get(LevelDBKey.key(LevelDBKey.ACTOR_PREFIX, entityKey));
        if (entityValue == null) return; // Skip if the key wasn't found

        try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(entityValue);
             DataInputStream readerStream = new DataInputStream(fileInputStream)) {
            Reader reader = Reader.toBedrockReader(readerStream);
            TagWithName<CompoundTag> pair = Tag.decodeNamed(reader, CompoundTag.class);
            if (pair == null) return;

            try {
                // Process the tag
                readEntity(column, pair.tag());
            } catch (Exception e) {
                converter.logNonFatalException(new Exception("Failed to process Entity " + pair.tag(), e));
            }
        }
    }

    @Override
    public BedrockChunkReader createChunkReader(ChunkerChunk chunk) {
        return new ChunkReader(resolvers, converter, dimension, chunk);
    }
}
