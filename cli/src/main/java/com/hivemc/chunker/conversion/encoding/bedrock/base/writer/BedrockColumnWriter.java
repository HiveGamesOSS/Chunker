package com.hivemc.chunker.conversion.encoding.bedrock.base.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.writer.ColumnWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.util.ColumnUtil;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.column.heightmap.BedrockHeightMap;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.nbt.io.Writer;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * A writer for Bedrock columns.
 */
public class BedrockColumnWriter implements ColumnWriter {
    protected final BedrockWorldWriter parent;
    protected final Converter converter;
    protected final BedrockResolvers resolvers;
    protected final DB database;
    protected final Dimension dimension;

    /**
     * Create a new column writer.
     *
     * @param parent    the parent world writer.
     * @param converter the converter instance.
     * @param resolvers the resolvers being used.
     * @param database  the LevelDB database.
     * @param dimension the dimension of the column.
     */
    public BedrockColumnWriter(BedrockWorldWriter parent, Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension) {
        this.parent = parent;
        this.converter = converter;
        this.resolvers = resolvers;
        this.database = database;
        this.dimension = dimension;
    }

    @Override
    public void writeColumn(ChunkerColumn chunkerColumn) throws Exception {
        // Run any preprocessing
        preProcessColumn(chunkerColumn);

        // Compact any chunk palettes to ensure unused values are removed from pre-processing
        chunkerColumn.getChunks().values().forEach(chunk -> {
            chunk.setPalette(chunk.getPalette().compact(ChunkerBlockIdentifier.AIR));
        });

        // Write the chunk to NBT
        ArrayList<Task<Void>> processing = new ArrayList<>();
        processing.add(Task.asyncConsume("Writing Metadata", TaskWeight.LOW, this::writeMetadata, chunkerColumn));
        processing.add(Task.asyncConsume("Writing HeightMap/Biomes", TaskWeight.NORMAL, this::writeHeightMapBiomes, chunkerColumn));
        processing.add(Task.asyncConsume("Writing Entities", TaskWeight.HIGH, this::writeEntities, chunkerColumn));
        processing.add(Task.asyncConsume("Writing Block Entities", TaskWeight.HIGH, this::writeBlockEntities, chunkerColumn));
        processing.add(Task.asyncConsume("Writing Chunks", TaskWeight.HIGHER, this::writeChunks, chunkerColumn));

        // When they're done apply post-processing
        Task.join(processing).then("Post-processing column", TaskWeight.HIGH, () -> postProcessColumn(chunkerColumn));
    }

    @Override
    public @Nullable PreTransformManager getPreTransformManager() {
        return resolvers.preTransformManager();
    }

    /**
     * Write any version metadata for the column.
     *
     * @param chunkerColumn the column being written.
     * @throws Exception if it failed to write the metadata.
     */
    protected void writeMetadata(ChunkerColumn chunkerColumn) throws Exception {
        try (WriteBatch writeBatch = database.createWriteBatch()) {
            // Save Version (0x76)
            writeBatch.put(LevelDBKey.key(dimension, chunkerColumn.getPosition(), LevelDBChunkType.LEGACY_VERSION), new byte[]{7});

            // Write the batch
            database.write(writeBatch);
        }
    }

    /**
     * Called before columns are written (sync).
     *
     * @param column the column being pre-processed.
     */
    protected void preProcessColumn(ChunkerColumn column) {
        // Create any block entities / entities which are based on blocks
        for (ChunkerChunk chunk : column.getChunks().values()) {
            resolvers.blockEntityResolver().generateBeforeWriteBlockEntities(column, chunk);
            resolvers.entityResolver().generateBeforeWriteEntities(column, chunk);
        }

        // Update the column from the block entities
        List<BlockEntity> blockEntities = column.getBlockEntities();
        for (int i = 0; i < blockEntities.size(); i++) {
            BlockEntity blockEntity = blockEntities.get(i);
            BlockEntity replacement = resolvers.blockEntityResolver().updateBeforeWrite(
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
            Entity replacement = resolvers.entityResolver().updateBeforeWrite(
                    column,
                    entity
            );

            // Apply replacement if needed
            if (replacement != entity) {
                entities.set(i, replacement);
            }
        }

        // Run any block entity removal logic
        column.getBlockEntities().removeIf(blockEntity -> shouldRemoveBlockEntityBeforeWrite(column, blockEntity));

        // Run any entity removal logic
        column.getEntities().removeIf(entity -> resolvers.entityResolver().shouldRemoveBeforeWrite(
                column,
                entity
        ));
    }

    /**
     * Check whether a block entity should be removed before the data is written.
     *
     * @param column      the column writing the block entity.
     * @param blockEntity the block entity.
     * @return true if it should be removed.
     */
    protected boolean shouldRemoveBlockEntityBeforeWrite(ChunkerColumn column, BlockEntity blockEntity) {
        // Check the block entity doesn't resolve to air
        ChunkerBlockIdentifier blockIdentifier = column.getBlock(blockEntity.getX(), blockEntity.getY(), blockEntity.getZ());
        if (blockIdentifier.isAir())
            return true; // Don't write air block entities

        // Check the identifier after being resolved isn't air
        Optional<Identifier> result = resolvers.writeBlockIdentifier(blockIdentifier, false);
        if (result.isEmpty() || result.get().getIdentifier().equals("minecraft:air"))
            return true; // Don't write air block entities

        // Call the handlers for the specific block entity checks
        return resolvers.blockEntityResolver().shouldRemoveBeforeWrite(
                column,
                blockEntity.getX(),
                blockEntity.getY(),
                blockEntity.getZ(),
                blockEntity
        );
    }

    /**
     * Called after the column has been written.
     *
     * @param chunkerColumn the column that was written.
     */
    protected void postProcessColumn(ChunkerColumn chunkerColumn) {
    }

    /**
     * Generate a height map for the column.
     *
     * @param column the column.
     * @return the generated height map.
     */
    protected BedrockHeightMap generateHeightMap(ChunkerColumn column) {
        short[][] heightMap = new short[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                OptionalInt highest = ColumnUtil.getHighestLitOrSlabBlock(column, x, z);
                short value = (short) (highest.orElse(-1) + 1);
                heightMap[x][z] = value;
            }
        }
        return new BedrockHeightMap(heightMap);
    }

    /**
     * Write the height map and biomes for the column.
     *
     * @param column the column being written.
     * @throws Exception if it failed to write the height map and biomes.
     */
    protected void writeHeightMapBiomes(ChunkerColumn column) throws Exception {
        // If the heightmap isn't bedrock specific, we need to regenerate it
        BedrockHeightMap heightMap;
        if (!(column.getHeightMap() instanceof BedrockHeightMap bedrockHeightMap)) {
            heightMap = generateHeightMap(column);
        } else {
            heightMap = bedrockHeightMap;
        }

        // Write Data2D
        byte[] bytes;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            Writer writer = Writer.toBedrockWriter(dataOutputStream);
            short[][] heightMapValues = heightMap.getHeightMap();

            // Write height map (LE Short)
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    writer.writeShort(heightMapValues[x][z]);
                }
            }

            // Write biome ids (byte) if present
            if (column.getBiomes() != null) {
                ChunkerBiome[] biomes = column.getBiomes().asColumn(resolvers.getFallbackBiome(dimension));
                for (ChunkerBiome chunkerBiome : biomes) {
                    writer.writeByte(resolvers.writeBiomeID(chunkerBiome, dimension));
                }
            } else {
                // Use the fallback biome
                ChunkerBiome fallbackBiome = resolvers.getFallbackBiome(dimension);
                for (int i = 0; i < 256; i++) {
                    writer.writeByte(resolvers.writeBiomeID(fallbackBiome, dimension));
                }
            }

            bytes = byteArrayOutputStream.toByteArray();
        }

        // Save Data2D (0x2D) - Heightmap / Biome
        database.put(LevelDBKey.key(dimension, column.getPosition(), LevelDBChunkType.DATA_2D), bytes);
    }

    /**
     * Write the entities to LevelDB.
     *
     * @param column the column being written.
     * @throws Exception if it failed to write the entities.
     */
    protected void writeEntities(ChunkerColumn column) throws Exception {
        // Write entities
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
                 DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
                // Write each entity as a separate tag
                for (Entity entity : column.getEntities()) {
                    try {
                        // Process the tag
                        CompoundTag tag = writeEntity(column, entity);
                        if (tag != null) {
                            // Encode
                            Tag.encodeNamed(Writer.toBedrockWriter(writerStream), "", tag);
                        }
                    } catch (Exception e) {
                        converter.logNonFatalException(new Exception("Failed to process Entity " + entity, e));
                    }
                }
            }

            // Write the byte array to the key
            database.put(LevelDBKey.key(dimension, column.getPosition(), LevelDBChunkType.ENTITY), byteArrayOutputStream.toByteArray());
        }
    }

    /**
     * Write an entity to a compound tag.
     *
     * @param chunkerColumn the column being written.
     * @param entity        the entity being written.
     * @return the compound tag that was read, otherwise null if it could not be read.
     */
    @Nullable
    protected CompoundTag writeEntity(ChunkerColumn chunkerColumn, Entity entity) {
        Optional<CompoundTag> compoundTag = resolvers.entityResolver().from(entity);
        if (compoundTag.isPresent()) {
            return compoundTag.get();
        } else {
            // Log as missing
            converter.logMissingMapping(Converter.MissingMappingType.ENTITY, String.valueOf(entity.getEntityType()));
            return null;
        }
    }

    /**
     * Write the block entities to LevelDB.
     *
     * @param column the column being written.
     * @throws Exception if it failed to write the block entities.
     */
    protected void writeBlockEntities(ChunkerColumn column) throws Exception {
        // Write block entities
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
                 DataOutputStream writerStream = new DataOutputStream(bufferedOutputStream)) {
                // Write each block entity as a separate tag
                for (BlockEntity blockEntity : column.getBlockEntities()) {
                    try {
                        // Process the tag
                        CompoundTag tag = writeBlockEntity(column, blockEntity);
                        if (tag != null) {
                            // Encode
                            Tag.encodeNamed(Writer.toBedrockWriter(writerStream), "", tag);
                        }
                    } catch (Exception e) {
                        converter.logNonFatalException(new Exception("Failed to process BlockEntity " + blockEntity, e));
                    }
                }
            }

            // Write the byte array to the key
            database.put(LevelDBKey.key(dimension, column.getPosition(), LevelDBChunkType.BLOCK_ENTITY), byteArrayOutputStream.toByteArray());
        }
    }

    /**
     * Write a block entity to a compound tag.
     *
     * @param chunkerColumn the column being written.
     * @param blockEntity   the block entity being written.
     * @return the compound tag that was read, otherwise null if it could not be read.
     */
    @Nullable
    protected CompoundTag writeBlockEntity(ChunkerColumn chunkerColumn, BlockEntity blockEntity) {
        Optional<CompoundTag> compoundTag = resolvers.blockEntityResolver().from(blockEntity);
        if (compoundTag.isPresent()) {
            return compoundTag.get();
        } else {
            // Log as missing
            converter.logMissingMapping(Converter.MissingMappingType.BLOCK_ENTITY, blockEntity.getClass().getSimpleName());
            return null;
        }
    }

    /**
     * Write all the chunks inside the column.
     *
     * @param column the column being written.
     */
    protected void writeChunks(ChunkerColumn column) {
        // Create the writer for the chunks
        BedrockChunkWriter chunkWriter = createChunkWriter(column);

        // Schedule each chunk to be written
        Task.asyncConsumeForEach("Writing Chunk", TaskWeight.NORMAL, chunkWriter::writeChunk, column.getChunks().values());
    }

    /**
     * Create a chunk writer for a column.
     *
     * @param column the column being written.
     * @return the newly created chunk writer.
     */
    public BedrockChunkWriter createChunkWriter(ChunkerColumn column) {
        return new BedrockChunkWriter(converter, resolvers, database, dimension, column);
    }
}
