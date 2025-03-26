package com.hivemc.chunker.conversion.encoding.java.base.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.reader.ColumnReader;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.biome.layout.ChunkerColumnBasedBiomes;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.column.heightmap.JavaLegacyHeightMap;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.array.ByteArrayTag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.ByteTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;

import java.util.*;

/**
 * A reader for Java columns.
 */
public class JavaColumnReader implements ColumnReader {
    /**
     * A list of chunk statuses which are counted as incomplete and should be ignored.
     */
    private static final Set<String> UNFINISHED_STATUSES = Set.of(
            "empty",
            "structure_starts",
            "structure_references",
            "biomes",
            "noise",
            "surface",
            "carvers",
            "liquid_carvers",
            "minecraft:empty",
            "minecraft:structure_starts",
            "minecraft:structure_references",
            "minecraft:biomes",
            "minecraft:noise",
            "minecraft:surface",
            "minecraft:carvers",
            "minecraft:liquid_carvers"
    );

    protected final Converter converter;
    protected final JavaResolvers resolvers;
    protected final Dimension dimension;
    protected final ChunkCoordPair columnCoords;
    protected CompoundTag columnNBT;

    /**
     * Create a new java column reader.
     *
     * @param converter    the converter instance.
     * @param resolvers    the resolvers to use.
     * @param dimension    the dimension this column is inside.
     * @param columnCoords the co-ordinates of the column.
     * @param columnNBT    the NBT of the column.
     */
    public JavaColumnReader(Converter converter, JavaResolvers resolvers, Dimension dimension, ChunkCoordPair columnCoords, CompoundTag columnNBT) {
        this.converter = converter;
        this.resolvers = resolvers;
        this.dimension = dimension;
        this.columnCoords = columnCoords;
        this.columnNBT = columnNBT;
    }

    @Override
    public void readColumn(ColumnConversionHandler columnConversionHandler) {
        if (columnNBT == null) return; // Ignore invalid chunks

        // Unwrap the level tag if it's provided (below 1.18)
        columnNBT = columnNBT.getCompound("Level", columnNBT);

        // Validate there is position data
        if (!columnNBT.contains("xPos") || !columnNBT.contains("zPos")) {
            return;
        }

        // Validate the file position is the same as the NBT
        int xPos = columnNBT.getInt("xPos");
        int zPos = columnNBT.getInt("zPos");
        if (xPos != columnCoords.chunkX() || zPos != columnCoords.chunkZ()) {
            converter.logNonFatalException(new Exception("Mislocated chunk, chunk states " + xPos + ", " + zPos + " but actually at " + columnCoords));
        }

        // Ensure the chunk isn't in a state where it's not populated
        if (columnNBT.contains("Status") && UNFINISHED_STATUSES.contains(columnNBT.getString("Status"))) {
            return;
        }

        // Create the column and start parsing
        ChunkerColumn column = new ChunkerColumn(columnCoords);

        // Load light populated
        if (columnNBT.contains("LightPopulated")) {
            column.setLightPopulated(columnNBT.getByte("LightPopulated") != (byte) 0);
        }

        // Load other parts of the column
        ArrayList<Task<Void>> processing = new ArrayList<>();
        if (converter.shouldProcessHeightMap()) {
            processing.add(Task.asyncConsume("Reading HeightMap", TaskWeight.NORMAL, this::readHeightMap, column));
        }
        if (converter.shouldProcessBiomes()) {
            processing.add(Task.asyncConsume("Reading Biomes", TaskWeight.NORMAL, this::readBiomes, column));
        }
        if (converter.shouldProcessEntities()) {
            processing.add(Task.asyncConsume("Reading Entities", TaskWeight.HIGH, this::readEntities, column));
        }
        if (converter.shouldProcessBlockEntities()) {
            processing.add(Task.asyncConsume("Reading Block Entities", TaskWeight.HIGH, this::readBlockEntities, column));
        }
        processing.add(Task.asyncConsume("Reading Chunks", TaskWeight.HIGHER, this::readChunks, column));

        // When they're done apply post-processing
        Task.join(processing)
                .then("Post-processing column", TaskWeight.HIGH, this::postProcess, column)
                .thenConsume("Submitting column", TaskWeight.LOW, columnConversionHandler::convertColumn);
    }

    /**
     * Read the height map data from the NBT.
     *
     * @param column the output to place the height map data into.
     */
    protected void readHeightMap(ChunkerColumn column) {
        if (!columnNBT.contains("HeightMap")) return;

        // Note: Removed in 1.13.2 and above
        short[][] heightMapOutput = new short[16][16];
        int[] heightMap = columnNBT.getIntArray("HeightMap");
        for (int i = 0; i < heightMap.length; i++) {
            heightMapOutput[i & 0xF][(i >> 4) & 0xF] = (short) heightMap[i];
        }
        column.setHeightMap(new JavaLegacyHeightMap(heightMapOutput));
    }

    /**
     * Read the biomes from the NBT.
     *
     * @param column the output to put the biomes into.
     */
    protected void readBiomes(ChunkerColumn column) {
        Tag<?> biomes = columnNBT.get("Biomes", Tag.class);
        if (biomes == null) return; // No biomes present

        if (biomes instanceof ByteArrayTag byteArrayTag && byteArrayTag.getValue() != null) {
            ChunkerBiome[] chunkerBiomeArray = new ChunkerBiome[256];
            byte[] value = byteArrayTag.getValue();

            // Read each byte and convert it to a chunker biome
            for (int i = 0; i < chunkerBiomeArray.length; i++) {
                chunkerBiomeArray[i] = resolvers.readBiome(value[i] & 0xFF, dimension);
            }
            column.setBiomes(new ChunkerColumnBasedBiomes(chunkerBiomeArray));
        }
    }

    /**
     * Read the entities from the NBT.
     *
     * @param column the output to add the entities to.
     */
    protected void readEntities(ChunkerColumn column) {
        ListTag<CompoundTag, Map<String, Tag<?>>> entities = columnNBT.getList("Entities", CompoundTag.class, null);
        if (entities != null) {
            for (CompoundTag entityTag : entities) {
                try {
                    // Process the tag
                    readEntity(column, entityTag);
                } catch (Exception e) {
                    converter.logNonFatalException(new Exception("Failed to process Entity " + entityTag, e));
                }
            }
        }
    }

    /**
     * Read an entity from a NBT tag.
     *
     * @param chunkerColumn the output column to add the entity to.
     * @param compoundTag   the input entity.
     */
    protected void readEntity(ChunkerColumn chunkerColumn, CompoundTag compoundTag) {
        Optional<Entity> entity = resolvers.entityResolver().to(compoundTag);
        if (entity.isPresent()) {
            chunkerColumn.getEntities().add(entity.get());
        } else {
            String identifier = resolvers.entityResolver().getKey(compoundTag).map(Object::toString).orElseGet(compoundTag::toString);
            converter.logMissingMapping(Converter.MissingMappingType.ENTITY, identifier);
        }
    }

    /**
     * Read the block entities from the NBT.
     *
     * @param column the output to add the block entities to.
     */
    protected void readBlockEntities(ChunkerColumn column) {
        ListTag<CompoundTag, Map<String, Tag<?>>> blockEntities = columnNBT.getList("TileEntities", CompoundTag.class, null);
        if (blockEntities != null) {
            for (CompoundTag blockEntityTag : blockEntities) {
                try {
                    // Process the tag
                    readBlockEntity(column, blockEntityTag);
                } catch (Exception e) {
                    converter.logNonFatalException(new Exception("Failed to process BlockEntity " + blockEntityTag, e));
                }
            }
        }
    }

    /**
     * Read a block entity from a NBT tag.
     *
     * @param chunkerColumn the output column to add the block entity to.
     * @param compoundTag   the input block entity.
     */
    protected void readBlockEntity(ChunkerColumn chunkerColumn, CompoundTag compoundTag) {
        Optional<BlockEntity> blockEntity = resolvers.blockEntityResolver().to(compoundTag);
        if (blockEntity.isPresent()) {
            chunkerColumn.getBlockEntities().add(blockEntity.get());
        } else {
            String identifier = resolvers.blockEntityResolver().getKey(compoundTag).orElseGet(compoundTag::toString);
            converter.logMissingMapping(Converter.MissingMappingType.BLOCK_ENTITY, identifier);
        }
    }

    /**
     * Read all the chunks inside the column.
     *
     * @param column the output for adding chunks to.
     */
    protected void readChunks(ChunkerColumn column) {
        ListTag<CompoundTag, Map<String, Tag<?>>> sections = columnNBT.getList("Sections", CompoundTag.class, null);
        if (sections == null) {
            // Try lowercase
            sections = columnNBT.getList("sections", CompoundTag.class, null);
            if (sections == null) {
                return; // No valid chunk data
            }
        }

        // Create a list of the tasks
        List<Task<ChunkerChunk>> tasks = new ArrayList<>(sections.size());

        // Loop through each chunk in the column
        for (CompoundTag section : sections) {
            Tag<?> tag = section.get("Y");
            if (tag == null || section.size() <= 1) continue;

            // Get the Y index of the sub-chunk
            byte y;
            if (tag instanceof ByteTag byteYTag) {
                y = byteYTag.getValue();
            } else if (tag instanceof IntTag intYTag) {
                // Allow Y to be an int, Minecraft still parses this and some software uses integers
                y = intYTag.getBoxedValue().byteValue();
            } else {
                throw new IllegalArgumentException("Invalid Section Y NBT Tag: " + tag);
            }

            // Create the chunk and add it to the column
            ChunkerChunk chunk = new ChunkerChunk(y);

            // Start the task
            tasks.add(Task.async("Creating Chunk Reader", TaskWeight.LOW, () -> createChunkReader(column, chunk))
                    .thenConsume("Reading Chunk", TaskWeight.HIGHER, (chunkReader) -> chunkReader.readChunk(section))
                    .then(chunk));
        }

        // Set the chunks on the column when they're done
        Task.join(tasks).thenConsume("Adding chunks to column", TaskWeight.LOW, (chunks) -> {
            for (ChunkerChunk chunk : chunks) {
                column.getChunks().put(chunk.getY(), chunk);
            }
        });
    }

    /**
     * Post-process the column after reading (sync).
     *
     * @param column the column which was read.
     * @return the column output to submit.
     */
    protected ChunkerColumn postProcess(ChunkerColumn column) {
        // Create any block entities / entities which are based on blocks
        for (ChunkerChunk chunk : column.getChunks().values()) {
            resolvers.blockEntityResolver().generateBeforeProcessBlockEntities(column, chunk);
            resolvers.entityResolver().generateBeforeProcessEntities(column, chunk);
        }

        // Update the column from the block entities
        List<BlockEntity> blockEntities = column.getBlockEntities();
        for (int i = 0; i < blockEntities.size(); i++) {
            BlockEntity blockEntity = blockEntities.get(i);
            BlockEntity replacement = resolvers.blockEntityResolver().updateBeforeProcess(
                    column,
                    blockEntity.getX(),
                    blockEntity.getY(),
                    blockEntity.getZ(),
                    blockEntity
            );

            // Apply replacement if needed
            if (replacement != blockEntity) {
                blockEntities.set(i, replacement);
            }
        }

        // Update the column from the entities
        List<Entity> entities = column.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            Entity replacement = resolvers.entityResolver().updateBeforeProcess(column, entity);

            // Apply replacement if needed
            if (replacement != entity) {
                entities.set(i, replacement);
            }
        }

        // Run any block entity removal logic
        column.getBlockEntities().removeIf(blockEntity -> resolvers.blockEntityResolver().shouldRemoveBeforeProcess(
                column,
                blockEntity.getX(),
                blockEntity.getY(),
                blockEntity.getZ(),
                blockEntity
        ));

        // Run any entity removal logic
        column.getEntities().removeIf(entity -> resolvers.entityResolver().shouldRemoveBeforeProcess(
                column,
                entity
        ));

        // Calculate any pre-transforms
        // Note: This will solve any connections which are already possible to keep as much async as possible
        resolvers.preTransformManager().solve(column, converter.shouldProcessColumnPreTransform());

        return column;
    }

    /**
     * Create a new chunk reader.
     *
     * @param column the column being read.
     * @param chunk  the chunk being read.
     * @return a newly created chunk reader.
     */
    public JavaChunkReader createChunkReader(ChunkerColumn column, ChunkerChunk chunk) {
        return new JavaChunkReader(converter, resolvers, column, chunk);
    }
}
