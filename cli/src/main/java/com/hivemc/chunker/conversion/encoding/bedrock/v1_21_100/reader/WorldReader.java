package com.hivemc.chunker.conversion.encoding.bedrock.v1_21_100.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockColumnReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import org.iq80.leveldb.DB;

import java.util.Map;
import java.util.Set;

public class WorldReader extends com.hivemc.chunker.conversion.encoding.bedrock.v1_21_90.reader.WorldReader {
    public WorldReader(BedrockResolvers resolvers, Converter converter, DB database, Map<RegionCoordPair, Set<ChunkCoordPair>> presentRegions, Dimension dimension) {
        super(resolvers, converter, database, presentRegions, dimension);
    }

    @Override
    public BedrockColumnReader createColumnReader(ChunkCoordPair worldChunkCoords) {
        return new ColumnReader(resolvers, converter, database, dimension, worldChunkCoords);
    }
}
