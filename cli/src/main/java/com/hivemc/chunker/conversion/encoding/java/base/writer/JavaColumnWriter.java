package com.hivemc.chunker.conversion.encoding.java.base.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.writer.ColumnWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.util.ColumnUtil;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.column.heightmap.JavaLegacyHeightMap;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerLevel;
import com.hivemc.chunker.conversion.intermediate.level.ChunkerPortal;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.array.ByteArrayTag;
import com.hivemc.chunker.nbt.tags.array.IntArrayTag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.scheduling.task.FutureTask;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import com.hivemc.chunker.util.BlockPosition;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A java column writer.
 */
public class JavaColumnWriter implements ColumnWriter {
    protected final JavaWorldWriter parent;
    protected final Converter converter;
    protected final JavaResolvers resolvers;
    protected final Dimension dimension;

    /**
     * Create a new column writer.
     *
     * @param parent    the parent world writer (for writing MCA files).
     * @param converter the converter instance.
     * @param resolvers the resolvers to use.
     * @param dimension the dimension of the column.
     */
    public JavaColumnWriter(JavaWorldWriter parent, Converter converter, JavaResolvers resolvers, Dimension dimension) {
        this.parent = parent;
        this.converter = converter;
        this.resolvers = resolvers;
        this.dimension = dimension;
    }

    @Override
    public void writeColumn(ChunkerColumn chunkerColumn) throws Exception {
        CompoundTag root = new CompoundTag();

        // Write base details
        root.put("xPos", chunkerColumn.getPosition().chunkX());
        root.put("zPos", chunkerColumn.getPosition().chunkZ());

        // Mark as populated
        root.put("LightPopulated", chunkerColumn.isLightPopulated() ? (byte) 1 : (byte) 0);
        root.put("TerrainPopulated", (byte) 1);

        // Run any preprocessing
        preProcessColumn(chunkerColumn, root);

        // Compact any chunk palettes to ensure unused values are removed from pre-processing
        chunkerColumn.getChunks().values().forEach(chunk -> {
            chunk.setPalette(chunk.getPalette().compact(ChunkerBlockIdentifier.AIR));
        });

        // Write the chunk to NBT
        ArrayList<Task<TagWithName<?>>> processing = new ArrayList<>();
        processing.add(Task.async("Writing HeightMap", TaskWeight.NORMAL, this::writeHeightMap, chunkerColumn));
        processing.add(Task.async("Writing Biomes", TaskWeight.NORMAL, this::writeBiomes, chunkerColumn));
        processing.add(Task.async("Writing Entities", TaskWeight.HIGH, this::writeEntities, chunkerColumn));
        processing.add(Task.async("Writing Block Entities", TaskWeight.HIGH, this::writeBlockEntities, chunkerColumn));
        processing.add(Task.async("Writing Chunks", TaskWeight.HIGHER, this::writeChunks, chunkerColumn));

        // Write POI
        Task.asyncConsume("Writing POI", TaskWeight.LOW, this::writePOI, chunkerColumn);

        // When they're done apply post-processing
        Task.join(processing)
                .thenConsume("Combining NBT", TaskWeight.LOW, (result) -> combineNBT(root, result))
                .then("Post-processing column", TaskWeight.HIGH, () -> postProcessColumn(chunkerColumn, root))
                .then("Writing column NBT", TaskWeight.LOW, () -> writeNBT(chunkerColumn, root));
    }

    @Override
    public @Nullable PreTransformManager getPreTransformManager() {
        return resolvers.preTransformManager();
    }

    /**
     * Combine NBT tags into an output.
     *
     * @param output the output to merge the inputs into.
     * @param inputs the inputs to iterate through.
     */
    protected void combineNBT(CompoundTag output, List<TagWithName<?>> inputs) {
        for (TagWithName<?> input : inputs) {
            if (input == null) continue;
            output.put(input.name(), input.tag());
        }
    }

    /**
     * Write the height map to an NBT tag.
     *
     * @param column the column being written.
     * @return the height-map as NBT.
     */
    @Nullable
    protected TagWithName<?> writeHeightMap(ChunkerColumn column) {
        // If the heightmap isn't bedrock specific, we need to regenerate it
        JavaLegacyHeightMap heightMap;
        if (!(column.getHeightMap() instanceof JavaLegacyHeightMap javaHeightMap)) {
            heightMap = generateHeightMap(column);
        } else {
            heightMap = javaHeightMap;
        }

        short[][] heightMapValues = heightMap.getHeightMap();
        int[] heightMapArray = new int[256];
        for (int i = 0; i < heightMapArray.length; i++) {
            heightMapArray[i] = heightMapValues[i & 0xF][(i >> 4) & 0xF];
        }

        return new TagWithName<>("HeightMap", new IntArrayTag(heightMapArray));
    }

    /**
     * Generate a legacy height map for the column.
     *
     * @param column the column.
     * @return the generated height map.
     */
    protected JavaLegacyHeightMap generateHeightMap(ChunkerColumn column) {
        short[][] heightMap = new short[16][16];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                OptionalInt highest = ColumnUtil.getHighestLitOrSlabBlock(column, x, z);
                short value = (short) (highest.orElse(-1) + 1);
                heightMap[x][z] = value;
            }
        }
        return new JavaLegacyHeightMap(heightMap);
    }

    /**
     * Write the biomes to a tag for this column.
     *
     * @param column the column to write.
     * @return a tag with the biomes or null.
     */
    @Nullable
    protected TagWithName<?> writeBiomes(ChunkerColumn column) {
        if (column.getBiomes() == null) return null; // Skip if not present

        // Written as columns
        ChunkerBiome[] columnBiomes = column.getBiomes().asColumn(resolvers.getFallbackBiome(dimension));

        // Loop through each biome and convert it to a byte
        byte[] biomes = new byte[columnBiomes.length];
        for (int i = 0; i < biomes.length; i++) {
            biomes[i] = (byte) resolvers.writeBiomeID(columnBiomes[i], dimension);
        }

        return new TagWithName<>("Biomes", new ByteArrayTag(biomes));
    }

    /**
     * Write the entities for the column to NBT.
     *
     * @param column the input column to get the entities from.
     * @return the NBT tag of the encoded entities.
     */
    @Nullable
    protected TagWithName<?> writeEntities(ChunkerColumn column) {
        ListTag<CompoundTag, Map<String, Tag<?>>> outputList = new ListTag<>(TagType.COMPOUND);
        // Write each entity as a separate tag
        for (Entity entity : column.getEntities()) {
            try {
                // Process the tag
                CompoundTag tag = writeEntity(column, entity);
                if (tag != null) {
                    // Add to entities
                    outputList.add(tag);
                }
            } catch (Exception e) {
                converter.logNonFatalException(new Exception("Failed to process Entity " + entity, e));
            }
        }

        return new TagWithName<>("Entities", outputList);
    }

    /**
     * Write an entity to NBT.
     *
     * @param chunkerColumn the column.
     * @param entity        the entity to write.
     * @return the written tag or null if it wasn't possible.
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
     * Write the block entities for the column to NBT.
     *
     * @param column the input column to get the block entities from.
     * @return the NBT tag of the encoded block entities.
     */
    @Nullable
    protected TagWithName<?> writeBlockEntities(ChunkerColumn column) {
        ListTag<CompoundTag, Map<String, Tag<?>>> outputList = new ListTag<>(TagType.COMPOUND);
        // Write each block entity as a separate tag
        for (BlockEntity blockEntity : column.getBlockEntities()) {
            try {
                // Process the tag
                CompoundTag tag = writeBlockEntity(column, blockEntity);
                if (tag != null) {
                    // Add to block entities
                    outputList.add(tag);
                }
            } catch (Exception e) {
                converter.logNonFatalException(new Exception("Failed to process BlockEntity " + blockEntity, e));
            }
        }

        return new TagWithName<>("TileEntities", outputList);
    }

    /**
     * Write a block entity to NBT.
     *
     * @param chunkerColumn the column.
     * @param blockEntity   the block entity to write.
     * @return the written tag or null if it wasn't possible.
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
     * Write all the chunks in the column.
     *
     * @param column the input column.
     * @return a tag containing the chunks.
     */
    @Nullable
    protected TagWithName<?> writeChunks(ChunkerColumn column) {
        // Sections are created here, so we can return them but filled async
        ListTag<CompoundTag, Map<String, Tag<?>>> sections = new ListTag<>(TagType.COMPOUND, new ArrayList<>(column.getChunks().size()));

        // Create the writer for the chunks
        JavaChunkWriter chunkWriter = createChunkWriter(column);

        // Schedule each chunk to be written
        FutureTask<List<CompoundTag>> outputs = Task.asyncForEach("Writing Chunk", TaskWeight.NORMAL, chunkWriter::writeChunk, column.getChunks().values());

        // Turn the results into sections (they may be null if it did not yield a chunk)
        outputs.thenConsume("Writing Sections", TaskWeight.LOW, (chunks) -> {
            for (CompoundTag tag : chunks) {
                if (tag == null) continue;

                // Add the tag
                sections.add(tag);
            }
        });

        // Below 1.18 used Sections with capital S
        return new TagWithName<>("Sections", sections);
    }

    /**
     * Called before columns are written (sync).
     *
     * @param column    the column being pre-processed.
     * @param columnNBT the output NBT for the column.
     */
    protected void preProcessColumn(ChunkerColumn column, CompoundTag columnNBT) {
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
            Entity replacement = resolvers.entityResolver().updateBeforeWrite(column, entity);

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

        // Check whether an additional chunk needs to be added to prevent Y biome blending
        if (converter.shouldPreventYBiomeBlending() && !column.getChunks().isEmpty()) {
            Byte2ObjectMap.Entry<ChunkerChunk> last = column.getChunks().byte2ObjectEntrySet().last();

            // Only add if the last chunk isn't empty and the world isn't the top chunk
            int highestChunkY = converter.level().map(a -> a.getSettings().CavesAndCliffs).orElse(false) ? 23 : 15;
            if (!last.getValue().isEmpty() && last.getByteKey() < highestChunkY) {
                ChunkerChunk emptyChunk = new ChunkerChunk((byte) (last.getByteKey() + 1));
                emptyChunk.setPalette(ChunkerBlockIdentifier.AIR.asFilledChunkPalette());
                column.getChunks().put(emptyChunk.getY(), emptyChunk);
            }
        }
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
     * @param column    the column that was written.
     * @param columnNBT the output column NBT.
     */
    protected void postProcessColumn(ChunkerColumn column, CompoundTag columnNBT) {

    }

    /**
     * Nest the NBT data before writing it.
     *
     * @param columnNBT the input tag.
     * @return the nested NBT tag if it needs nesting for the version.
     */
    protected CompoundTag nestColumnNBT(CompoundTag columnNBT) {
        // Below 1.18 requires the tag inside a Level tag
        CompoundTag root = new CompoundTag();
        root.put("Level", columnNBT);
        return root;
    }

    /**
     * Write the NBT data from the column to disk.
     *
     * @param column    the column being written.
     * @param columnNBT the data to write.
     * @throws Exception if it failed to write the region data.
     */
    protected void writeNBT(ChunkerColumn column, CompoundTag columnNBT) throws Exception {
        CompoundTag root = nestColumnNBT(columnNBT);

        // Add dataVersion
        root.put("DataVersion", resolvers.dataVersion().getDataVersion());

        // Write to the MCA file
        parent.writeMCAData(dimension, column.getPosition(), root);
    }

    /**
     * Write the points of interest data for the column.
     *
     * @param chunkerColumn the column to write.
     * @throws Exception if it failed to write the POI data.
     */
    protected void writePOI(ChunkerColumn chunkerColumn) throws Exception {
        // Check if this column may contain any portals
        Optional<ChunkerLevel> optionalChunkerLevel = converter.level();
        if (optionalChunkerLevel.isEmpty()) return; // No POI if there's no level

        // Grab the portals
        List<ChunkerPortal> portals = optionalChunkerLevel.get().getPortals();
        if (portals.isEmpty()) return; // No POI if no portals

        // Create a list of portal blocks
        Map<Byte, List<BlockPosition>> portalBlocks = new Byte2ObjectOpenHashMap<>();
        for (ChunkerPortal portal : portals) {
            if (portal.getDimension() != dimension) continue;

            // Grab the horizontal blocks, we don't have vertical data, so we have to work that out
            List<BlockPosition> horizontalBlocks = portal.getHorizontalBlocks(chunkerColumn.getPosition());
            for (BlockPosition horizontalBlock : horizontalBlocks) {
                // Loop from the position and go up checking for portal blocks
                ChunkerBlockIdentifier blockIdentifier;
                int y = horizontalBlock.y();
                while (true) {
                    blockIdentifier = chunkerColumn.getBlock(horizontalBlock.x(), y, horizontalBlock.z());
                    if (blockIdentifier.getType() == ChunkerVanillaBlockType.NETHER_PORTAL) {
                        // Add the portal block to the section
                        List<BlockPosition> list = portalBlocks.computeIfAbsent((byte) (y >> 4), (ignored) -> new ArrayList<>());
                        list.add(new BlockPosition(horizontalBlock.x(), y, horizontalBlock.z()));
                    } else {
                        break;
                    }
                    y++;
                }
            }
        }

        if (portalBlocks.isEmpty()) return; // No portals

        // Write our portal records
        CompoundTag sections = new CompoundTag();
        for (Map.Entry<Byte, List<BlockPosition>> entry : portalBlocks.entrySet()) {
            CompoundTag section = new CompoundTag();
            section.put("Valid", (byte) 1);
            // Write records
            ListTag<CompoundTag, Map<String, Tag<?>>> records = new ListTag<>(TagType.COMPOUND);
            for (BlockPosition blockPosition : entry.getValue()) {
                CompoundTag record = new CompoundTag();
                record.put("type", "minecraft:nether_portal");
                record.put("pos", new int[]{blockPosition.x(), blockPosition.y(), blockPosition.z()});
                record.put("free_tickets", 0);
                records.add(record);
            }
            section.put("Records", records);

            // Add the section
            sections.put(entry.getKey().toString(), section);
        }

        CompoundTag root = new CompoundTag();
        root.put("Sections", sections);

        // Add data version
        root.put("DataVersion", resolvers.dataVersion().getDataVersion());

        // Write to the region file
        parent.writePOIData(dimension, chunkerColumn.getPosition(), root);
    }

    /**
     * Create a new chunk writer.
     *
     * @param column the column being written.
     * @return a new chunk writer instance.
     */
    public JavaChunkWriter createChunkWriter(ChunkerColumn column) {
        return new JavaChunkWriter(converter, resolvers, dimension, column);
    }
}
