package com.hivemc.chunker.conversion.encoding.bedrock.base.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.reader.ColumnReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.encoding.bedrock.util.PaletteUtil;
import com.hivemc.chunker.conversion.handlers.ColumnConversionHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.biome.layout.ChunkerColumnBasedBiomes;
import com.hivemc.chunker.conversion.intermediate.column.biome.layout.ChunkerPaletteBasedBiomes;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.column.heightmap.BedrockHeightMap;
import com.hivemc.chunker.conversion.intermediate.column.heightmap.HeightMap;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.io.Reader;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import org.iq80.leveldb.DB;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A reader for Bedrock columns.
 */
public class BedrockColumnReader implements ColumnReader {
    protected final BedrockResolvers resolvers;
    protected final Converter converter;
    protected final Dimension dimension;
    protected final DB database;
    protected final ChunkCoordPair columnCoords;

    /**
     * Create a new bedrock column reader.
     *
     * @param resolvers    the resolvers to use.
     * @param converter    the converter instance.
     * @param database     the LevelDB database.
     * @param dimension    the dimension the column is inside.
     * @param columnCoords the co-ordinates of the column.
     */
    public BedrockColumnReader(BedrockResolvers resolvers, Converter converter, DB database, Dimension dimension, ChunkCoordPair columnCoords) {
        this.resolvers = resolvers;
        this.converter = converter;
        this.database = database;
        this.dimension = dimension;
        this.columnCoords = columnCoords;
    }

    @Override
    public void readColumn(ColumnConversionHandler columnConversionHandler) {
        // Create the column and start parsing
        ChunkerColumn column = new ChunkerColumn(columnCoords);

        // Load other parts of the column
        ArrayList<Task<Void>> processing = new ArrayList<>();
        if (converter.shouldProcessHeightMap() || converter.shouldProcessBiomes()) {
            processing.add(Task.asyncConsume("Reading Biome/HeightMap", TaskWeight.NORMAL, this::readBiomeHeightMap, column));
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
     * Read the biomes and the height map.
     *
     * @param column the column being read.
     */
    protected void readBiomeHeightMap(ChunkerColumn column) {
        try {
            // Read Data2D
            byte[] value = database.get(LevelDBKey.key(dimension, column.getPosition(), LevelDBChunkType.DATA_2D));
            if (value != null) {
                ByteBuffer buffer = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
                if (converter.shouldProcessHeightMap()) {
                    column.setHeightMap(readHeightMap(buffer));
                } else {
                    // Skip the bytes
                    buffer.position(buffer.position() + 256);
                }
                if (converter.shouldProcessBiomes()) {
                    column.setBiomes(readBiomesColumn(buffer));
                }
            }

            // No HeightMap/Biome data present
        } catch (Exception e) {
            converter.logNonFatalException(e);
        }
    }

    /**
     * Read the height map.
     *
     * @param buffer the buffer the height map is being read from.
     * @return the height map which was read.
     */
    protected HeightMap readHeightMap(ByteBuffer buffer) {
        short[][] heightMap = new short[16][16];

        // Loop through the short based height map values
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                heightMap[x][z] = buffer.getShort();
            }
        }
        return new BedrockHeightMap(heightMap);
    }

    /**
     * Read the extended biomes from a buffer.
     *
     * @param buffer the buffer being read.
     * @return the biomes in palettized form.
     * @throws Exception if it failed to read the biomes.
     */
    protected ChunkerPaletteBasedBiomes readBiomesExtended(ByteBuffer buffer) throws Exception {
        List<Palette<ChunkerBiome>> palettes = new ArrayList<>();

        // Read every palette that is present
        while (buffer.hasRemaining()) {
            Palette<ChunkerBiome> palette = PaletteUtil.readChunkPalette(buffer, (ignored) -> resolvers.readBiome(buffer.getInt(), dimension));
            palettes.add(palette);
        }

        // Prune any empty palettes which are at the end of the list (highest first)
        for (int i = palettes.size() - 1; i >= 0; i--) {
            if (palettes.get(i).isEmpty()) {
                palettes.remove(i);
            } else {
                break;
            }
        }

        // Return the newly created palettes
        return new ChunkerPaletteBasedBiomes(palettes);
    }

    /**
     * Read the column based biomes from a buffer.
     *
     * @param buffer the buffer being read.
     * @return the biomes in column form.
     */
    protected ChunkerColumnBasedBiomes readBiomesColumn(ByteBuffer buffer) {
        ChunkerBiome[] biomes = new ChunkerBiome[256];

        // Buffer is ordered by X then Z
        for (int i = 0; i < 256; i++) {
            biomes[i] = resolvers.readBiome(buffer.get() & 0xFF, dimension);
        }
        return new ChunkerColumnBasedBiomes(biomes);
    }

    /**
     * Read all the entities inside a column.
     *
     * @param column the column being read.
     * @throws Exception if it failed to read the entity data.
     */
    protected void readEntities(ChunkerColumn column) throws Exception {
        byte[] value = database.get(LevelDBKey.key(dimension, column.getPosition(), LevelDBChunkType.ENTITY));
        if (value == null) return;

        // While there is bytes loop and read the entities
        try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(value);
             DataInputStream readerStream = new DataInputStream(fileInputStream)) {
            Reader reader = Reader.toBedrockReader(readerStream);
            while (fileInputStream.available() > 0) {
                TagWithName<CompoundTag> pair = Tag.decodeNamed(reader, CompoundTag.class);
                if (pair == null) break;

                try {
                    // Process the tag
                    readEntity(column, pair.tag());
                } catch (Exception e) {
                    converter.logNonFatalException(new Exception("Failed to process Entity " + pair.tag(), e));
                }
            }
        }
    }

    /**
     * Read an entity inside the column.
     *
     * @param chunkerColumn the column being read.
     * @param compoundTag   the entity tag to read and add to the column.
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
     * Read all the block entities inside a column.
     *
     * @param column the column being read.
     * @throws Exception if it failed to read the block entity data.
     */
    protected void readBlockEntities(ChunkerColumn column) throws Exception {
        byte[] value = database.get(LevelDBKey.key(dimension, column.getPosition(), LevelDBChunkType.BLOCK_ENTITY));
        if (value == null) return;

        // While there is bytes loop and read the block entities
        try (ByteArrayInputStream fileInputStream = new ByteArrayInputStream(value);
             DataInputStream readerStream = new DataInputStream(fileInputStream)) {
            Reader reader = Reader.toBedrockReader(readerStream);
            while (fileInputStream.available() > 0) {
                TagWithName<CompoundTag> pair = Tag.decodeNamed(reader, CompoundTag.class);
                if (pair == null) break;

                try {
                    // Process the tag
                    readBlockEntity(column, pair.tag());
                } catch (Exception e) {
                    converter.logNonFatalException(new Exception("Failed to process BlockEntity " + pair.tag(), e));
                }
            }
        }
    }

    /**
     * Read a block entity inside the column.
     *
     * @param chunkerColumn the column being read.
     * @param compoundTag   the block entity tag to read and add to the column.
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
     * Read all the chunks inside a column.
     *
     * @param column the column being read.
     */
    protected void readChunks(ChunkerColumn column) {
        // Create a list of the tasks
        List<Task<ChunkerChunk>> tasks = new ArrayList<>();

        // Create a reusable key that can be used for each sub-chunk lookup
        byte[] key = LevelDBKey.key(dimension, column.getPosition(), (byte) 0, LevelDBChunkType.SUB_CHUNK_PREFIX);
        for (byte y = -64; y < 64; y++) {
            key[key.length - 1] = y; // The last byte is the Y

            // Lookup the chunk
            byte[] value = database.get(key);
            if (value == null) continue; // Skip if the chunk doesn't exist

            // Create the chunk and add it to the column
            ChunkerChunk chunk = new ChunkerChunk(y);

            // Start the task
            tasks.add(Task.async("Creating Chunk Reader", TaskWeight.LOW, this::createChunkReader, chunk)
                    .thenConsume("Reading Chunk", TaskWeight.HIGHER, (chunkReader) -> chunkReader.readChunk(value))
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
     * Run any postprocessing which should be done before submitting the column (sync).
     *
     * @param column the column being post processed.
     * @return the column which should be submitted.
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
            Entity replacement = resolvers.entityResolver().updateBeforeProcess(
                    column,
                    entity
            );

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
     * @param chunk the chunk being read.
     * @return the newly created chunk reader.
     */
    public BedrockChunkReader createChunkReader(ChunkerChunk chunk) {
        return new BedrockChunkReader(resolvers, converter, dimension, chunk);
    }
}
