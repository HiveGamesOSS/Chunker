package com.hivemc.chunker.conversion.encoding.preview;

import com.google.common.collect.Sets;
import com.hivemc.chunker.conversion.encoding.base.writer.ColumnWriter;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import it.unimi.dsi.fastutil.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Write all the regions as images based on the block colors.
 */
public class PreviewColumnWriter implements ColumnWriter {
    private final File outputFolder;
    private final ConcurrentMap<RegionCoordPair, ConcurrentMap<ChunkCoordPair, int[]>> chunkRGBA = new ConcurrentHashMap<>();
    private final PreviewWorldWriter.WorldData worldData;

    /**
     * Create a new preview column writer.
     *
     * @param outputFolder the folder where the images should be written.
     * @param worldData    the world data to add present chunks to.
     */
    public PreviewColumnWriter(File outputFolder, PreviewWorldWriter.WorldData worldData) {
        this.outputFolder = outputFolder;
        this.worldData = worldData;
    }

    @Override
    public void writeColumn(ChunkerColumn chunkerColumn) {
        int[] argb = new int[256];
        boolean present = false;

        // Loop through each column to calculate color
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Find the highest block that has RGB color
                Pair<Integer, ChunkerBlockIdentifier> block = chunkerColumn.getHighestBlock(x, z, ChunkerBlockIdentifier::hasRGBColor);
                if (block != null) {
                    // Mark the chunk as present
                    present = true;

                    // Grab the color
                    int rgb = block.value().getRGBColor();

                    // Convert to ARGB
                    argb[(z << 4) | x] = rgb == 0 ? 0 : 0xFF000000 | rgb;
                }
            }
        }

        RegionCoordPair regionCoordPair = chunkerColumn.getPosition().getRegion();
        ConcurrentMap<ChunkCoordPair, int[]> regionRGBA = chunkRGBA.computeIfAbsent(regionCoordPair, (ignored) -> new ConcurrentHashMap<>());
        if (present) {
            // Add the RGB
            regionRGBA.put(chunkerColumn.getPosition(), argb);

            // Record the chunk being present in this region
            Set<ChunkCoordPair> chunks = worldData.regionToPresentChunks.computeIfAbsent(regionCoordPair, (ignored) -> Sets.newConcurrentHashSet());
            chunks.add(chunkerColumn.getPosition());
        }
    }

    @Override
    public void flushColumns() {
        // Calculate min & max for the world
        for (Set<ChunkCoordPair> regionChunks : worldData.regionToPresentChunks.values()) {
            for (ChunkCoordPair chunk : regionChunks) {
                if (chunk.chunkX() < worldData.minX) {
                    worldData.minX = chunk.chunkX();
                }

                if (chunk.chunkX() > worldData.maxX) {
                    worldData.maxX = chunk.chunkX();
                }

                if (chunk.chunkZ() < worldData.minZ) {
                    worldData.minZ = chunk.chunkZ();
                }

                if (chunk.chunkZ() > worldData.maxZ) {
                    worldData.maxZ = chunk.chunkZ();
                }
            }
        }

        // Ensure output is a directory
        outputFolder.mkdirs();

        // Create the images for each region and write them
        for (Map.Entry<RegionCoordPair, ConcurrentMap<ChunkCoordPair, int[]>> entry : chunkRGBA.entrySet()) {
            RegionCoordPair region = entry.getKey();

            BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
            for (Map.Entry<ChunkCoordPair, int[]> chunk : entry.getValue().entrySet()) {
                // If the chunk isn't present, we don't need to write any data (as it should be transparent)
                if (chunk.getValue().length == 0) continue;

                // Copy pixels
                image.setRGB(
                        ((chunk.getKey().chunkX() & 31) << 4), // Place our chunk inside the region (512x512)
                        ((chunk.getKey().chunkZ() & 31) << 4),
                        16, // Each chunk is 16x16
                        16,
                        chunk.getValue(), // ARGB array
                        0, // Starts from the initial value
                        16 // Size of each Y
                );
            }

            // Write the region PNG
            File outputFile = new File(outputFolder, worldData.dimension.name() + "." + region.regionX() + "." + region.regionZ() + ".png");
            try {
                ImageIO.write(image, "png", outputFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
