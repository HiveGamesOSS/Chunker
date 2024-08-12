package com.hivemc.chunker.conversion.encoding.bedrock.util;

/**
 * Various types which can be used for chunk entries.
 */
public enum LevelDBChunkType {
    DATA_3D((byte) 43), // HeightMap / 3D Biomes
    VERSION((byte) 44), // Single byte version
    DATA_2D((byte) 45), // HeightMap / 2D Biomes
    DATA_2D_LEGACY((byte) 46), // Old biome data
    SUB_CHUNK_PREFIX((byte) 47), // Terrain data
    LEGACY_TERRAIN((byte) 48), // Legacy terrain data
    BLOCK_ENTITY((byte) 49), // Block Entity data
    ENTITY((byte) 50), // Entity data
    PENDING_TICKS((byte) 51),
    LEGACY_BLOCK_EXTRA_DATA((byte) 52),
    BIOME_STATE((byte) 53),
    FINALIZED_STATE((byte) 54),
    CONVERSION_DATA((byte) 55),
    BORDER_BLOCKS((byte) 56),
    HARDCODED_SPAWNERS((byte) 57),
    RANDOM_TICKS((byte) 58),
    CHECKSUMS((byte) 59),
    GENERATION_SEED((byte) 60),
    GENERATED_PRE_CAVES_AND_CLIFFS_BLENDING((byte) 61),
    BLENDING_BIOME_HEIGHT((byte) 62),
    METADATA_HASH((byte) 63),
    BLENDING_DATA((byte) 64),
    ACTOR_DIGEST_VERSION((byte) 65),
    LEGACY_VERSION((byte) 118); // Legacy version byte (later moved to 44)

    private final byte id;

    /**
     * Create a new type ID.
     *
     * @param id the ID of the entry.
     */
    LevelDBChunkType(byte id) {
        this.id = id;
    }

    /**
     * Get the ID used for this type.
     *
     * @return the byte ID.
     */
    public byte getId() {
        return id;
    }
}
