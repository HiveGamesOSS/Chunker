package com.hivemc.chunker.conversion.intermediate.world;

import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.primitive.ByteTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Registry of dimensions that are registered to this world or the output world
 */
public class DimensionRegistry {
    /**
     * The first valid ID for custom dimensions on Bedrock.
     */
    public static final int BEDROCK_CUSTOM_DIMENSION_ID_START = 1000;

    private final HashMap<String, Dimension> dimensionByIdentifier = new HashMap<>();
    private final Int2ObjectMap<Dimension> dimensionByJavaId = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Dimension> dimensionByBedrockId = new Int2ObjectOpenHashMap<>();

    /**
     * Creates a new DimensionRegistry with vanilla dimensions by default
     */
    public DimensionRegistry() {
        register(Dimension.OVERWORLD.getIdentifier(), Dimension.OVERWORLD);
        register(Dimension.NETHER.getIdentifier(), Dimension.NETHER);
        register(Dimension.THE_END.getIdentifier(), Dimension.THE_END);
    }

    /**
     * Get the dimensions registered
     *
     * @return Collection of Dimensions registered
     */
    public Collection<Dimension> getDimensions() {
        return dimensionByIdentifier.values();
    }

    /**
     * Registers a dimension
     *
     * @param identifier the identifier of the Dimension
     * @param dimension  the dimension to be added
     */
    public void register(String identifier, Dimension dimension) {
        // If this a replacement, we need to remove old BedrockIDs / JavaIDs
        Dimension replaced = this.dimensionByIdentifier.put(identifier, dimension);
        if (replaced != null) {
            this.dimensionByBedrockId.remove(replaced.getBedrockID(), replaced);
            replaced.getJavaID().ifPresent(javaID -> this.dimensionByJavaId.remove(javaID, replaced));
        }

        // Register the Dimension
        this.dimensionByBedrockId.put(dimension.getBedrockID(), dimension);
        dimension.getJavaID().ifPresent(javaID -> this.dimensionByJavaId.put(javaID, dimension));
    }

    /**
     * Check whether a dimension is a vanilla built-in dimension.
     *
     * @param dimension the input dimension.
     * @return true if it's one of the built-in dimensions (compares against the constants in Dimension).
     */
    public static boolean isVanilla(@Nullable Dimension dimension) {
        return dimension == Dimension.OVERWORLD || dimension == Dimension.NETHER || dimension == Dimension.THE_END;
    }

    /**
     * Util method to copy a dimension with a different bedrock ID.
     *
     * @param dimension the dimension
     * @param bedrockID the bedrock ID.
     * @return a new dimension with the bedrock ID.
     */
    protected static Dimension cloneWithBedrockID(Dimension dimension, int bedrockID) {
        return new Dimension(null, bedrockID, dimension.getIdentifier(), dimension.getFallbackBiome(), dimension.getBiomeHeight());
    }

    /**
     * Create a custom dimension.
     *
     * @param identifier the namespaced identifier of the dimension.
     * @param bedrockID  the Bedrock ID to use.
     * @return the custom dimension.
     */
    public Dimension createCustom(String identifier, OptionalInt bedrockID) {
        int resolvedBedrockID = bedrockID.orElseGet(this::getNextCustomBedrockID);
        return new Dimension(null, resolvedBedrockID, identifier, ChunkerBiome.ChunkerVanillaBiome.PLAINS, 24);
    }

    /**
     * Generate the next dimension ID to use a custom bedrock dimension.
     *
     * @return an unused ID at or above {@link #BEDROCK_CUSTOM_DIMENSION_ID_START}.
     */
    public int getNextCustomBedrockID() {
        int bedrockID = BEDROCK_CUSTOM_DIMENSION_ID_START;
        while (dimensionByBedrockId.containsKey(bedrockID)) bedrockID++;
        return bedrockID;
    }

    /**
     * Finds a dimension by it's registered identifier
     *
     * @param identifier the identifier of the Dimension to be found
     * @return The dimension if found, null otherwise
     */
    public @Nullable Dimension getByIdentifier(String identifier) {
        return dimensionByIdentifier.get(identifier);
    }

    /**
     * Get the dimension based on a Java NBT tag.
     *
     * @param tag      the tag to use (string, byte or integer).
     * @param fallback the fallback dimension to use if the tag can't be parsed or the ID is invalid.
     * @return the dimension if it was parsed otherwise the fallback.
     */
    public Dimension fromJavaNBT(@Nullable Tag<?> tag, Dimension fallback) {
        // There is every chance there was no tag, so we'll handle that first
        if (tag == null) return fallback;

        // We can either parse it as an identifier or a byte
        try {
            if (tag instanceof StringTag stringTag) {
                return dimensionByIdentifier.getOrDefault(Objects.requireNonNull(stringTag.getValue()).toLowerCase(Locale.ROOT), fallback);
            } else if (tag instanceof ByteTag byteTag) {
                byte value = byteTag.getValue();
                return fromJava(value, fallback);
            } else if (tag instanceof IntTag intTag) {
                byte value = (byte) intTag.getValue();
                return fromJava(value, fallback);
            } else {
                return fallback; // Can't be parsed
            }
        } catch (Exception exception) {
            return fallback;
        }
    }

    /**
     * Get the dimension based on a Bedrock NBT tag.
     *
     * @param tag      the tag to use (string, byte or integer).
     * @param fallback the fallback dimension to use if the tag can't be parsed or the ID is invalid.
     * @return the dimension if it was parsed otherwise the fallback.
     */
    public Dimension fromBedrockNBT(@Nullable Tag<?> tag, Dimension fallback) {
        // There is every chance there was no tag, so we'll handle that first
        if (tag == null) return fallback;

        // We can either parse it as an identifier or a byte
        try {
            if (tag instanceof StringTag stringTag) {
                return dimensionByIdentifier.getOrDefault(Objects.requireNonNull(stringTag.getValue()).toLowerCase(Locale.ROOT), fallback);
            } else if (tag instanceof ByteTag byteTag) {
                int value = byteTag.getValue();
                return dimensionByBedrockId.getOrDefault(value, fallback);
            } else if (tag instanceof IntTag intTag) {
                int value = intTag.getValue();
                return dimensionByBedrockId.getOrDefault(value, fallback);
            } else {
                return fallback; // Can't be parsed
            }
        } catch (Exception exception) {
            return fallback;
        }
    }

    /**
     * Create a dimension from a Bedrock ID.
     *
     * @param id       the input ID.
     * @param fallback the fallback to use if the ID wasn't found.
     * @return the dimension or fallback if it wasn't found.
     */
    public Dimension fromBedrock(int id, Dimension fallback) {
        return dimensionByBedrockId.getOrDefault(id, fallback);
    }

    /**
     * Register a custom dimension from world data, if the dimension already exists it is returned.
     *
     * @param identifier the identifier to use for the world.
     * @param bedrockID  the Bedrock dimension ID used.
     * @return the registered dimension.
     */
    public Dimension registerFromWorld(String identifier, OptionalInt bedrockID) {
        // Check if the identifier has already been used
        Dimension declared = dimensionByIdentifier.get(identifier);
        if (declared != null) {
            if (isVanilla(declared)) return declared; // Don't reregister vanilla dimensions

            // Already declared with the ID the world uses or the world didn't provide one
            if (bedrockID.isEmpty() || declared.getBedrockID() == bedrockID.getAsInt()) return declared;

            // The ID differs, so we need to check if there is a dimension that already uses this ID
            Dimension declaredByID = dimensionByBedrockId.get(bedrockID.getAsInt());
            if (isVanilla(declaredByID)) return declaredByID; // Don't rebind a vanilla ID

            // Another dimension was given this ID before the world was read, so give it a new ID instead
            if (declaredByID != null) {
                register(declaredByID.getIdentifier(), cloneWithBedrockID(declaredByID, getNextCustomBedrockID()));
            }

            // Keep the settings it was declared with but update it to use the ID from the world
            Dimension dimension = cloneWithBedrockID(declared, bedrockID.getAsInt());
            register(identifier, dimension);
            return dimension;
        }

        // The identifier is new, so we need to check if there is a dimension that already uses this ID
        if (bedrockID.isPresent()) {
            Dimension declaredByID = dimensionByBedrockId.get(bedrockID.getAsInt());
            if (isVanilla(declaredByID)) return declaredByID; // Don't rebind a vanilla ID

            // Another dimension was given this ID before the world was read so update it to a new one
            if (declaredByID != null) {
                register(declaredByID.getIdentifier(), cloneWithBedrockID(declaredByID, getNextCustomBedrockID()));
            }
        }

        // Register it as a new custom dimension, allocating an ID if the world didn't provide one
        Dimension dimension = createCustom(identifier, bedrockID);
        register(identifier, dimension);
        return dimension;
    }

    /**
     * Create a dimension from a Java ID.
     *
     * @param id       the input ID.
     * @param fallback the fallback to use if the ID wasn't found.
     * @return the dimension or fallback if it wasn't found.
     */
    public Dimension fromJava(int id, Dimension fallback) {
        return dimensionByJavaId.getOrDefault(id, fallback);
    }
}
