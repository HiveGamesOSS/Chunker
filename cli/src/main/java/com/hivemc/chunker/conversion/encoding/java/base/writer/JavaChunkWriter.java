package com.hivemc.chunker.conversion.encoding.java.base.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.TagWithName;
import com.hivemc.chunker.nbt.tags.array.ByteArrayTag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;
import com.hivemc.chunker.util.ByteUtil;
import com.hivemc.chunker.util.LegacyIdentifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A writer for Java chunks
 */
public class JavaChunkWriter {
    /**
     * Empty light array used for when light isn't present
     */
    public static final byte[] EMPTY_LIGHT_ARRAY = new byte[2048];

    protected final JavaResolvers resolvers;
    protected final Converter converter;
    protected final Dimension dimension;
    protected final ChunkerColumn chunkerColumn;

    /**
     * Create a new java chunk writer.
     *
     * @param converter     the converter instance.
     * @param resolvers     the resolvers to use.
     * @param dimension     the dimension the chunk is inside.
     * @param chunkerColumn the column the chunk belongs to.
     */
    public JavaChunkWriter(Converter converter, JavaResolvers resolvers, Dimension dimension, ChunkerColumn chunkerColumn) {
        this.resolvers = resolvers;
        this.converter = converter;
        this.dimension = dimension;
        this.chunkerColumn = chunkerColumn;
    }

    /**
     * Write a chunk to NBT.
     *
     * @param chunkerChunk the input chunk to write (can be null).
     * @return the NBT for the chunk or null if it shouldn't be written.
     */
    @Nullable
    public CompoundTag writeChunk(@Nullable ChunkerChunk chunkerChunk) {
        // Don't write if it's empty and discard is enabled
        if (chunkerChunk == null || converter.shouldDiscardEmptyChunks() && chunkerChunk.isEmpty()) return null;
        if (!isChunkHeightSupported(chunkerChunk.getY())) return null;

        // Create the base tag with the Y set
        CompoundTag output = new CompoundTag();
        output.put("Y", chunkerChunk.getY());

        // Create an output array to handle asynchronous results
        List<TagWithName<?>> outputs = Collections.synchronizedList(new ArrayList<>());

        // Create the tasks
        List<Task<Void>> tasks = createChunkTasks(chunkerChunk, outputs);

        // Combine all the data into the compound tag
        Task.join(tasks).then("Combining Chunk NBT", TaskWeight.LOW, () -> {
            for (TagWithName<?> tag : outputs) {
                if (tag == null) continue;
                output.put(tag.name(), tag.tag());
            }
        });

        // Return the tag (filled async)
        return output;
    }

    /**
     * Create the tasks for writing the chunk.
     *
     * @param chunkerChunk the chunk being written.
     * @param outputs      the output list for NBT which is combined.
     * @return a list of tasks which have been created.
     */
    protected List<Task<Void>> createChunkTasks(ChunkerChunk chunkerChunk, List<TagWithName<?>> outputs) {
        List<Task<Void>> tasks = new ArrayList<>();
        tasks.add(Task.async("Writing Block Palette", TaskWeight.HIGH, () -> writeBlockPalette(chunkerChunk, outputs)));
        tasks.add(Task.async("Writing Light Data", TaskWeight.MEDIUM, () -> writeLightData(chunkerChunk, outputs)));
        return tasks;
    }

    /**
     * Check whether a chunk height is supported.
     *
     * @param chunkY the index Y of the chunk.
     * @return true if the chunk height is supported.
     */
    protected boolean isChunkHeightSupported(byte chunkY) {
        return chunkY >= 0 && chunkY < 16; // 0-15 is supported for every version (higher needs a newer version)
    }

    /**
     * Write the block data for the chunk.
     *
     * @param chunk  the input chunk to write the light for.
     * @param output the output list for writing NBT tags.
     */
    protected void writeBlockPalette(ChunkerChunk chunk, List<TagWithName<?>> output) {
        // Blocks
        byte[] blockArray = new byte[4096];
        byte[] addArray = new byte[2048];
        byte[] dataArray = new byte[2048];

        if (!chunk.isEmpty()) {
            for (int i = 0; i < 4096; i++) {
                int x = i & 0xF;
                int z = (i >> 4) & 0xF;
                int y = (i >> 8) & 0xF;
                int nibbleIndex = i >> 1;
                boolean lowestBits = (i & 1) == 0;

                // Get key + value
                ChunkerBlockIdentifier identifier = chunk.getPalette().get(x, y, z, ChunkerBlockIdentifier.AIR);
                LegacyIdentifier legacyIdentifier = resolvers.writeLegacyBlockIdentifier(identifier);

                // Set block id
                byte blockValue = (byte) (legacyIdentifier.id() & 0xFF);
                byte addValue = (byte) ((legacyIdentifier.id() >> 8) & 0xFF);
                blockArray[i] = blockValue;

                // Set add value (extra block ids)
                addArray[nibbleIndex] = ByteUtil.updateNibble(addArray[nibbleIndex], addValue, lowestBits);

                // Set block data
                dataArray[nibbleIndex] = ByteUtil.updateNibble(dataArray[nibbleIndex], legacyIdentifier.data(), lowestBits);
            }
        }

        // Add tags
        output.add(new TagWithName<>("Blocks", new ByteArrayTag(blockArray)));
        output.add(new TagWithName<>("Add", new ByteArrayTag(addArray)));
        output.add(new TagWithName<>("Data", new ByteArrayTag(dataArray)));
    }

    /**
     * Write the light data for the chunk.
     *
     * @param chunk  the input chunk to write the light for.
     * @param output the output list for writing NBT tags.
     */
    protected void writeLightData(ChunkerChunk chunk, List<TagWithName<?>> output) {
        if (chunk.getBlockLight() != null) {
            byte[] blockLightArray = ByteUtil.convertToNibbleArray(chunk.getBlockLight());
            output.add(new TagWithName<>("BlockLight", new ByteArrayTag(blockLightArray)));
        } else {
            output.add(new TagWithName<>("BlockLight", new ByteArrayTag(EMPTY_LIGHT_ARRAY)));
        }

        // SkyLight is not in the nether
        if (dimension != Dimension.NETHER) {
            if (chunk.getSkyLight() != null) {
                byte[] skyLightArray = ByteUtil.convertToNibbleArray(chunk.getSkyLight());
                output.add(new TagWithName<>("SkyLight", new ByteArrayTag(skyLightArray)));
            } else {
                output.add(new TagWithName<>("SkyLight", new ByteArrayTag(EMPTY_LIGHT_ARRAY)));
            }
        }
    }
}
