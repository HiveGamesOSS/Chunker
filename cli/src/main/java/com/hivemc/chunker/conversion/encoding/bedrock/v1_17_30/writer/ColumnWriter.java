package com.hivemc.chunker.conversion.encoding.bedrock.v1_17_30.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockChunkWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockWorldWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBChunkType;
import com.hivemc.chunker.conversion.encoding.bedrock.util.LevelDBKey;
import com.hivemc.chunker.conversion.encoding.bedrock.util.PaletteUtil;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.EmptyPalette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.Palette;
import com.hivemc.chunker.conversion.intermediate.column.chunk.palette.SingleValuePalette;
import com.hivemc.chunker.conversion.intermediate.column.heightmap.BedrockHeightMap;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.nbt.io.Writer;
import org.iq80.leveldb.DB;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Collections;
import java.util.List;

public class ColumnWriter extends com.hivemc.chunker.conversion.encoding.bedrock.v1_17.writer.ColumnWriter {
    public ColumnWriter(BedrockWorldWriter parent, Converter converter, BedrockResolvers resolvers, DB database, Dimension dimension) {
        super(parent, converter, resolvers, database, dimension);
    }

    @Override
    protected void writeHeightMapBiomes(ChunkerColumn column) throws Exception {
        // If the heightmap isn't bedrock specific, we need to regenerate it
        BedrockHeightMap heightMap;
        if (!(column.getHeightMap() instanceof BedrockHeightMap bedrockHeightMap)) {
            heightMap = generateHeightMap(column);
        } else {
            heightMap = bedrockHeightMap;
        }

        // Write Data3D (3d biomes)
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

            List<Palette<ChunkerBiome>> biomesChunkList = column.getBiomes() == null ? Collections.emptyList() : column.getBiomes().asPalette();

            // If the input world wasn't caves and cliffs, we need to pad the chunks at the bottom
            // If the chunks are empty, it doesn't need padding
            if (!biomesChunkList.isEmpty() && !converter.level().map(level -> level.getSettings().CavesAndCliffs).orElse(true)) {
                Palette<ChunkerBiome> palette = biomesChunkList.get(0);
                if (palette.isEmpty()) {
                    // Empty palettes are only allowed in Bedrock at the end of a column
                    palette = new SingleValuePalette<>(16, resolvers.getFallbackBiome(dimension));
                }

                // Pad the negative chunks
                for (int i = 0; i < 4; i++) {
                    biomesChunkList.add(0, palette);
                }
            }

            // Write 3d biomes
            for (int i = 0; i < 64; i++) {
                Palette<ChunkerBiome> palette;

                // If the index is outside the data we have, we should repeat the last chunk
                if (i >= biomesChunkList.size()) {
                    // Repeat the last chunk or an empty biome chunk
                    if (!column.getChunks().isEmpty() && !biomesChunkList.isEmpty()) {
                        palette = biomesChunkList.get(biomesChunkList.size() - 1);
                    } else {
                        // Use the empty chunk as we don't have this data
                        palette = EmptyPalette.chunk();
                    }
                } else {
                    palette = biomesChunkList.get(i);
                    if (palette.isEmpty()) {
                        // Empty palettes are only allowed in Bedrock at the end of a column
                        palette = new SingleValuePalette<>(16, resolvers.getFallbackBiome(dimension));
                    }
                }

                // If we have extra biome data write it but also ensure we write 24 chunks
                if (i < biomesChunkList.size() || i < getDimensionBiomeHeight()) {
                    // Write the palette
                    PaletteUtil.writeChunkPalette(
                            writer,
                            palette,
                            true,
                            true,
                            (biome) -> writer.writeInt(resolvers.writeBiomeID(biome, dimension))
                    );
                }
            }

            bytes = byteArrayOutputStream.toByteArray();
        }

        // Save Data3D (0x2B) - Heightmap / Biome
        database.put(LevelDBKey.key(dimension, column.getPosition(), LevelDBChunkType.DATA_3D), bytes);
    }

    @Override
    public BedrockChunkWriter createChunkWriter(ChunkerColumn column) {
        return new ChunkWriter(converter, resolvers, database, dimension, column);
    }

    /**
     * Get the height that biomes is required to be for the current dimension.
     *
     * @return the height in chunks of how many biome palettes should be written.
     */
    public int getDimensionBiomeHeight() {
        return switch (dimension) {
            case OVERWORLD -> 24;
            case NETHER -> 8;
            case THE_END -> 16;
            default -> 24;
        };
    }
}
