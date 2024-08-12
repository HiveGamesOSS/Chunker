package com.hivemc.chunker.conversion.encoding.bedrock.v1_17.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockChunkWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockWorldWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

public class ColumnWriter extends com.hivemc.chunker.conversion.encoding.bedrock.v1_16.writer.ColumnWriter {
    public ColumnWriter(BedrockWorldWriter parent, Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension) {
        super(parent, converter, resolvers, database, dimension);
    }

    protected boolean isWriteBlendingData() {
        return converter.level().map(level -> level.getSettings().CavesAndCliffs).orElse(true);
    }

    @Override
    protected void writeMetadata(ChunkerColumn chunkerColumn) throws Exception {
        try (WriteBatch writeBatch = database.createWriteBatch()) {
            if (isWriteBlendingData()) {
                // Save Caves and Cliffs Blending (0x3d) (legacy)
                writeBatch.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), LevelDBChunkType.GENERATED_PRE_CAVES_AND_CLIFFS_BLENDING), new byte[]{0});

                // Save new version (37)
                writeBatch.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), LevelDBChunkType.VERSION), new byte[]{37});
            }

            // Save legacy version (0x76)
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
