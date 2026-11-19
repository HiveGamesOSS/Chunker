package com.hivemc.chunker.conversion.encoding.bedrock.v1_26_50.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockChunkWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockWorldWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.util.BedrockChunkMetaData;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;

public class ColumnWriter extends com.hivemc.chunker.conversion.encoding.bedrock.v1_26_40.writer.ColumnWriter {
    public ColumnWriter(BedrockWorldWriter parent, Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension) {
        super(parent, converter, resolvers, database, dimension);
    }

    @Override
    protected void writeMetadata(ChunkerColumn chunkerColumn) throws Exception {
        super.writeMetadata(chunkerColumn);

        BedrockChunkMetaData chunkMetaData = BedrockChunkMetaData.getMetaForVersion(resolvers.dataVersion().getVersion());
        if (chunkMetaData == null) return;

        // Save MetaDataHash (0x3f)
        database.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), LevelDBChunkType.METADATA_HASH), chunkMetaData.getHashBytes());
    }

    @Override
    public BedrockChunkWriter createChunkWriter(ChunkerColumn column) {
        return new ChunkWriter(converter, resolvers, database, dimension, column);
    }
}
