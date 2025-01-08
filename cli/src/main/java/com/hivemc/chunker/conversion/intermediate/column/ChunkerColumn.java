package com.hivemc.chunker.conversion.intermediate.column;

import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.intermediate.column.biome.layout.ChunkerBiomes;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.WriteablePalette;
import com.hivemc.chunker.conversion.intermediate.column.entity.Entity;
import com.hivemc.chunker.conversion.intermediate.column.heightmap.HeightMap;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectSortedMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A Minecraft 16x16 columns containing chunks.
 */
public class ChunkerColumn {
    private final ChunkCoordPair position;
    private final List<Entity> entities = new ArrayList<>(0);
    private final List<BlockEntity> blockEntities = new ArrayList<>(0);
    private final Byte2ObjectSortedMap<ChunkerChunk> chunks = new Byte2ObjectAVLTreeMap<>();
    private final Set<Edge> requiredPreTransformEdges = EnumSet.noneOf(Edge.class);
    @Nullable
    private ChunkerBiomes biomes;
    @Nullable
    private HeightMap heightMap;
    private boolean lightPopulated;
    private Consumer<Map<Edge, ChunkerColumn>> preTransformHandler;

    /**
     * Create a new column.
     *
     * @param position the position of the column in the world.
     */
    public ChunkerColumn(ChunkCoordPair position) {
        this.position = position;
    }

    /**
     * Get the position of this column in the world.
     *
     * @return the co-ordinate pair for this column.
     */
    public ChunkCoordPair getPosition() {
        return position;
    }

    /**
     * Get the biomes for this column.
     *
     * @return the biomes for this column.
     */
    @Nullable
    public ChunkerBiomes getBiomes() {
        return biomes;
    }

    /**
     * Set the biomes for this column.
     *
     * @param biomes the new biomes to use for the column.
     */
    public void setBiomes(@Nullable ChunkerBiomes biomes) {
        this.biomes = biomes;
    }

    /**
     * Get the height map used for this column.
     *
     * @return null if the height map should be regenerated (if it is required).
     */
    @Nullable
    public HeightMap getHeightMap() {
        return heightMap;
    }

    /**
     * Set the height map for the column.
     *
     * @param heightMap null if none present, and it should be regenerated if needed.
     */
    public void setHeightMap(@Nullable HeightMap heightMap) {
        this.heightMap = heightMap;
    }

    /**
     * Get the entities which are inside this column.
     *
     * @return a list of the entities.
     */
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * Get the block entities which are inside this column.
     *
     * @return a list of the block-entities.
     */
    public List<BlockEntity> getBlockEntities() {
        return blockEntities;
    }

    @NotNull
    public Byte2ObjectSortedMap<ChunkerChunk> getChunks() {
        return chunks;
    }

    /**
     * Whether the chunk is marked as light populated.
     *
     * @return true if light is marked as populated.
     */
    public boolean isLightPopulated() {
        return lightPopulated;
    }

    /**
     * Set whether chunk light is populated (Java).
     *
     * @param lightPopulated true if light is populated.
     */
    public void setLightPopulated(boolean lightPopulated) {
        this.lightPopulated = lightPopulated;
    }

    /**
     * Get the edges of this column which are required for the pre-transform stage of conversion.
     *
     * @return the edges required for the pre-transform stage.
     */
    public Set<Edge> getRequiredPreTransformEdges() {
        return requiredPreTransformEdges;
    }

    /**
     * Add a handler which should be called during the pre-transform stage, this is after the world is read and before
     * the world is written. It allows a column to query neighbours to calculate any missing information.
     *
     * @param edges               the edges which are required for this handler.
     * @param preTransformHandler the handler to be called when the edges have been fulfilled.
     */
    public void addPreTransformHandler(Set<Edge> edges, Consumer<Map<Edge, ChunkerColumn>> preTransformHandler) {
        // Append the handler if it exists
        if (this.preTransformHandler != null) {
            this.preTransformHandler = this.preTransformHandler.andThen(preTransformHandler);
        } else {
            this.preTransformHandler = preTransformHandler;
        }

        // Update the edges
        requiredPreTransformEdges.addAll(edges);
    }

    /**
     * Call the preTransform handlers for this column and then clear them.
     *
     * @param resolvedColumns the resolved columns based on {@link #getRequiredPreTransformEdges()}.
     */
    public void preTransform(Map<Edge, ChunkerColumn> resolvedColumns) {
        if (preTransformHandler == null) return; // If there's no handler, don't run

        // Call the preTransform handler
        preTransformHandler.accept(resolvedColumns);

        // Clear the handler
        preTransformHandler = null;
    }

    /**
     * Get the highest block and height at an X / Z position in the column.
     *
     * @param x         the x co-ordinate (can be a local or global position as a bitmask is applied).
     * @param z         the z co-ordinate (can be a local or global position as a bitmask is applied).
     * @param predicate the predicate to check each block type against, if it returns true it will be marked as the
     *                  highest.
     * @return a pair with the block Y and the identifier at the position otherwise null if the chunk is empty or there
     * isn't a match.
     */
    @Nullable
    public Pair<Integer, ChunkerBlockIdentifier> getHighestBlock(int x, int z, Predicate<ChunkerBlockIdentifier> predicate) {
        if (chunks.isEmpty()) return null; // Can't find the highest block if there's no chunks

        // Use local co-ordinates
        x = x & 15;
        z = z & 15;

        // Start from the top
        ObjectSortedSet<Byte2ObjectMap.Entry<ChunkerChunk>> chunkSet = chunks.byte2ObjectEntrySet();
        ObjectBidirectionalIterator<Byte2ObjectMap.Entry<ChunkerChunk>> iterator = chunkSet.iterator(chunkSet.last());
        while (iterator.hasPrevious()) {
            ChunkerChunk chunk = iterator.previous().getValue();
            Palette<ChunkerBlockIdentifier> palette = chunk.getPalette();

            // If the palette contains a block matching the predicate
            if (palette != null && palette.containsKey(predicate)) {
                for (int y = 15; y >= 0; y--) {
                    ChunkerBlockIdentifier identifier = palette.get(x, y, z, ChunkerBlockIdentifier.AIR);
                    if (predicate.test(identifier)) {
                        // Return this height
                        return Pair.of((chunk.getY() << 4) | y, identifier);
                    }
                }
            }
        }

        return null; // Empty chunk
    }

    /**
     * Get a block at a location, if it is in a sub-chunk that doesn't exist one will be created.
     *
     * @param x the x co-ordinate (can be a local or global position as a bitmask is applied).
     * @param y the y co-ordinate of the block.
     * @param z the z co-ordinate (can be a local or global position as a bitmask is applied).
     * @return the block or an air identifier if the chunk is not in the column.
     */
    public ChunkerBlockIdentifier getBlock(int x, int y, int z) {
        if (chunks.isEmpty()) return ChunkerBlockIdentifier.AIR;
        byte chunkY = (byte) (y >> 4);

        // Find the chunk
        ChunkerChunk chunk = chunks.get(chunkY);
        if (chunk == null) return ChunkerBlockIdentifier.AIR;

        // Find the block
        return chunk.getPalette().get(x & 15, y & 15, z & 15, ChunkerBlockIdentifier.AIR);
    }

    /**
     * Set a block at a location, if it is in a sub-chunk that doesn't exist one will be created.
     *
     * @param x          the x co-ordinate (can be a local or global position as a bitmask is applied).
     * @param y          the y co-ordinate of the block.
     * @param z          the z co-ordinate (can be a local or global position as a bitmask is applied).
     * @param identifier the identifier to set for the block.
     */
    public void setBlock(int x, int y, int z, ChunkerBlockIdentifier identifier) {
        byte chunkY = (byte) (y >> 4);

        // Find the chunk
        ChunkerChunk chunk = chunks.get(chunkY);
        if (chunk == null) {
            // Create a new chunk for this block
            chunk = new ChunkerChunk(chunkY);
            chunk.setPalette(ChunkerBlockIdentifier.AIR.asFilledChunkPalette());
            chunks.put(chunkY, chunk);
        }

        // Ensure the palette is writeable then add the identifier
        WriteablePalette<ChunkerBlockIdentifier> newPalette = chunk.getPalette().asWriteable();
        newPalette.set(x & 15, y & 15, z & 15, identifier);
        chunk.setPalette(newPalette);
    }

    /**
     * Get a block entity at a location.
     *
     * @param x the x co-ordinate (can be a local or global position as a bitmask is applied).
     * @param y the y co-ordinate of the block.
     * @param z the z co-ordinate (can be a local or global position as a bitmask is applied).
     * @return the block entity if it was found otherwise null.
     */
    @Nullable
    public BlockEntity getBlockEntity(int x, int y, int z) {
        // Use local co-ordinates
        x = x & 15;
        z = z & 15;

        // Search block entities
        for (BlockEntity blockEntity : blockEntities) {
            if ((blockEntity.getX() & 15) == x && blockEntity.getY() == y && (blockEntity.getZ() & 15) == z) {
                return blockEntity;
            }
        }
        return null;
    }
}
