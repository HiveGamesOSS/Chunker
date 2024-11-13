package com.hivemc.chunker.conversion.encoding.java.v1_21_4.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaColumnReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.io.File;

public class WorldReader extends com.hivemc.chunker.conversion.encoding.java.v1_21_2.reader.WorldReader {
    public WorldReader(Converter converter, JavaResolvers resolvers, File regionFolder, Dimension dimension) {
        super(converter, resolvers, regionFolder, dimension);
    }

    @Override
    public JavaColumnReader createColumnReader(ChunkCoordPair worldChunkCoords, CompoundTag columnNBT) {
        return new ColumnReader(converter, resolvers, dimension, worldChunkCoords, columnNBT);
    }
}
