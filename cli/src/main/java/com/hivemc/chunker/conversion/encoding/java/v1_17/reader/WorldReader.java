package com.hivemc.chunker.conversion.encoding.java.v1_17.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.reader.JavaColumnReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.io.File;

public class WorldReader extends com.hivemc.chunker.conversion.encoding.java.v1_16.reader.WorldReader {
    public WorldReader(Converter converter, JavaResolvers resolvers, File regionFolder, Dimension dimension) {
        super(converter, resolvers, regionFolder, dimension);
    }

    @Override
    protected File[] getMCAFolders() {
        // Scan the region & entities folder
        return new File[]{
                new File(dimensionFolder, "region"),
                new File(dimensionFolder, "entities")
        };
    }

    @Override
    protected CompoundTag combineColumnCompounds(CompoundTag[] compoundTags) {
        // Multiple compounds are combined for this version
        CompoundTag main = compoundTags[0];
        CompoundTag entities = compoundTags[1];

        // If only one element is present it may just be entities
        if (main == null && entities != null) {
            main = new CompoundTag();
            int[] position = entities.getIntArray("Position", null);
            if (position == null) return null; // No valid position

            main.put("xPos", position[0]);
            main.put("zPos", position[1]);
            Tag<?> entitiesTag = entities.get("Entities");
            if (entitiesTag != null) {
                main.put("Entities", entitiesTag);
            }
        } else if (main != null && entities != null) {
            // Merge entities into main
            Tag<?> entitiesTag = entities.get("Entities");
            if (entitiesTag != null) {
                main.getCompound("Level", main).put("Entities", entitiesTag);
            }
        }
        return main;
    }

    @Override
    public JavaColumnReader createColumnReader(ChunkCoordPair worldChunkCoords, CompoundTag columnNBT) {
        return new ColumnReader(converter, resolvers, dimension, worldChunkCoords, columnNBT);
    }
}
