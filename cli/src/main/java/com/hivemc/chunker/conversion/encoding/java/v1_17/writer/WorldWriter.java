package com.hivemc.chunker.conversion.encoding.java.v1_17.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaLevelReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.writer.JavaColumnWriter;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;

import java.io.File;

public class WorldWriter extends com.hivemc.chunker.conversion.encoding.java.v1_16.writer.WorldWriter {
    public WorldWriter(File outputFolder, Converter converter, JavaResolvers resolvers) {
        super(outputFolder, converter, resolvers);
    }

    @Override
    public void writeMCAData(Dimension dimension, ChunkCoordPair chunkCoordPair, CompoundTag chunkData) throws Exception {
        // Write entities to a different file (if there are entities)
        CompoundTag root = chunkData.getCompound("Level", chunkData);
        Tag<?> entities = root.remove("Entities");
        if (entities instanceof ListTag<?, ?> entitiesList && entitiesList.size() > 0) {
            // Write the entities to a different region file
            CompoundTag entitiesData = new CompoundTag(3);
            entitiesData.put("Entities", entities);
            entitiesData.put("DataVersion", resolvers.dataVersion().getDataVersion());
            entitiesData.put("Position", new int[]{chunkCoordPair.chunkX(), chunkCoordPair.chunkZ()});

            File entityDirectory = new File(JavaLevelReader.getDimensionBaseDirectory(outputFolder, dimension), "entities");
            if (!entityDirectory.exists()) {
                entityDirectory.mkdirs();
            }
            File entityRegionFile = new File(entityDirectory, "r." + chunkCoordPair.regionX() + "." + chunkCoordPair.regionZ() + ".mca");
            writeMCAData(entityRegionFile, chunkCoordPair, entitiesData);
        }

        // Write main region file
        super.writeMCAData(dimension, chunkCoordPair, chunkData);
    }

    @Override
    public JavaColumnWriter createColumnWriter(Dimension dimension) {
        return new ColumnWriter(this, converter, resolvers, dimension);
    }
}
