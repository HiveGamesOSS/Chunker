package com.hivemc.chunker.conversion.encoding.bedrock.base.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockCompoundTag;
import com.hivemc.chunker.conversion.encoding.bedrock.util.PaletteUtil;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkerChunk;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.ShortBasedPalette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.WriteablePalette;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.scheduling.task.Task;
import com.hivemc.chunker.scheduling.task.TaskWeight;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * A reader for Bedrock chunks.
 */
public class BedrockChunkReader {
    protected final BedrockResolvers resolvers;
    protected final Converter converter;
    protected final Dimension dimension;
    protected final ChunkerChunk chunk;

    /**
     * Create a new bedrock chunk reader.
     *
     * @param resolvers the resolver to use.
     * @param converter the converter instance.
     * @param dimension the dimension of the chunk.
     * @param chunk     the chunk output being read into.
     */
    public BedrockChunkReader(BedrockResolvers resolvers, Converter converter, Dimension dimension, ChunkerChunk chunk) {
        this.resolvers = resolvers;
        this.converter = converter;
        this.dimension = dimension;
        this.chunk = chunk;
    }

    /**
     * Read the data of the chunk.
     *
     * @param subChunkData the palette data.
     */
    public void readChunk(byte[] subChunkData) {
        ByteBuffer buffer = ByteBuffer.wrap(subChunkData).order(ByteOrder.LITTLE_ENDIAN);
        Task.asyncConsume("Reading Palette", TaskWeight.HIGHER, this::readPalette, buffer);
    }

    /**
     * Read the palette from a byte buffer.
     *
     * @param buffer the buffer to read from.
     */
    protected void readPalette(ByteBuffer buffer) {
        // Read the version then delegate to the version method
        byte version = buffer.get();
        readPalette(version, buffer);
    }

    /**
     * Read a specific palette version.
     *
     * @param version the version of the palette.
     * @param buffer  the palette data.
     */
    protected void readPalette(byte version, ByteBuffer buffer) {
        // Only version 8 is supported by this reader
        if (version != 8) {
            converter.logNonFatalException(new Exception("Unsupported chunk version " + version));
            return;
        }

        // Grab how many layers are present
        byte layers = buffer.get();

        // Loop through each layer and read the palettes
        readLayers(buffer, layers);
    }

    /**
     * Read the layers from a block palette.
     *
     * @param buffer the palette data.
     * @param layers the number of layers in the palette data.
     */
    protected void readLayers(ByteBuffer buffer, byte layers) {
        try {
            Palette<CompoundTag> blockPalette = null;
            Palette<ChunkerBlockIdentifier> liquidPalette = null;
            for (byte layer = 0; layer < layers; layer++) {
                if (layer == 0) {
                    blockPalette = PaletteUtil.readChunkPalette(buffer, (runtimeID) -> readBlockPaletteCompound(runtimeID, buffer));
                } else {
                    liquidPalette = PaletteUtil.readChunkPalette(buffer, (runtimeID) -> readLiquidPaletteEntry(runtimeID, buffer));
                }
            }
            if (blockPalette != null) {
                // Use the main layer if there is no liquid layer (or valid liquids)
                if (liquidPalette == null || liquidPalette.isEmpty() || !liquidPalette.containsKey(a -> a.getType() == ChunkerVanillaBlockType.WATER)) {
                    chunk.setPalette(blockPalette.map(tag -> resolvers.readBlock(new BedrockBlockCompoundTag(tag, false))));
                } else {
                    WriteablePalette<BedrockBlockCompoundTag> mergedPalette = new ShortBasedPalette<>(2, 16);
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                CompoundTag entry = blockPalette.get(x, y, z);
                                ChunkerBlockIdentifier liquid = liquidPalette.get(x, y, z, ChunkerBlockIdentifier.AIR);
                                boolean waterlogged = liquid.getType() == ChunkerVanillaBlockType.WATER;
                                mergedPalette.set(x, y, z, new BedrockBlockCompoundTag(entry, waterlogged));
                            }
                        }
                    }

                    // Turn the palette into block identifiers then compact it to remove any unused entries
                    chunk.setPalette(mergedPalette.map(resolvers::readBlock).compact(ChunkerBlockIdentifier.AIR));
                }
            }
        } catch (Exception e) {
            // Skip reading as the chunk is likely corrupt / invalid
            converter.logNonFatalException(e);
        }
    }

    /**
     * Read the block palette key.
     *
     * @param runtimeID whether runtime encoding has been used.
     * @param input     the bytebuffer to read the key from.
     * @return the key.
     * @throws Exception exception if it failed to read.
     */
    protected CompoundTag readBlockPaletteCompound(boolean runtimeID, ByteBuffer input) throws Exception {
        if (runtimeID) {
            throw new Exception("RuntimeIDs are not supported in block palettes");
        }
        return Tag.readBedrockNBT(input);
    }

    /**
     * Read the liquid palette key.
     *
     * @param runtimeID whether runtime encoding has been used.
     * @param input     the bytebuffer to read the key from.
     * @return the key.
     * @throws Exception exception if it failed to read.
     */
    protected ChunkerBlockIdentifier readLiquidPaletteEntry(boolean runtimeID, ByteBuffer input) throws Exception {
        if (runtimeID) {
            throw new Exception("RuntimeIDs are not supported in block palettes");
        }
        return resolvers.readBlock(new BedrockBlockCompoundTag(Objects.requireNonNull(Tag.readBedrockNBT(input)), false));
    }
}
