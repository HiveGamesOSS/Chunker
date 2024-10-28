package com.hivemc.chunker.conversion.intermediate.world;

import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.primitive.ByteTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

/**
 * The different types of Dimensions present in Minecraft.
 */
public enum Dimension {
    OVERWORLD((byte) 0, (byte) 0, "minecraft:overworld"),
    NETHER((byte) -1, (byte) 1, "minecraft:nether"),
    THE_END((byte) 1, (byte) 2, "minecraft:the_end");

    private static final Byte2ObjectMap<Dimension> dimensionByJavaId = new Byte2ObjectOpenHashMap<>();
    private static final Byte2ObjectMap<Dimension> dimensionByBedrockId = new Byte2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<String, Dimension> dimensionByIdentifier = new Object2ObjectOpenHashMap<>();

    static {
        Dimension[] dimensions = values();
        for (Dimension dimension : dimensions) {
            dimensionByJavaId.put(dimension.getJavaID(), dimension);
            dimensionByBedrockId.put(dimension.getBedrockID(), dimension);
            dimensionByIdentifier.put(dimension.getIdentifier(), dimension);
        }
    }

    private final byte javaID;
    private final byte bedrockID;
    private final String identifier;

    /**
     * Create a new dimension.
     *
     * @param javaID     the java ID.
     * @param bedrockID  the bedrock ID.
     * @param identifier the namespaced identifier used on Java.
     */
    Dimension(byte javaID, byte bedrockID, String identifier) {
        this.javaID = javaID;
        this.bedrockID = bedrockID;
        this.identifier = identifier;
    }

    /**
     * Get the dimension based on a Java NBT tag.
     *
     * @param tag      the tag to use (string, byte or integer).
     * @param fallback the fallback dimension to use if the tag can't be parsed or the ID is invalid.
     * @return the dimension if it was parsed otherwise the fallback.
     */
    public static Dimension fromJavaNBT(@Nullable Tag<?> tag, Dimension fallback) {
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
    public static Dimension fromBedrockNBT(@Nullable Tag<?> tag, Dimension fallback) {
        // There is every chance there was no tag, so we'll handle that first
        if (tag == null) return fallback;

        // We can either parse it as an identifier or a byte
        try {
            if (tag instanceof StringTag stringTag) {
                return dimensionByIdentifier.getOrDefault(Objects.requireNonNull(stringTag.getValue()).toLowerCase(Locale.ROOT), fallback);
            } else if (tag instanceof ByteTag byteTag) {
                byte value = byteTag.getValue();
                return dimensionByBedrockId.getOrDefault(value, fallback);
            } else if (tag instanceof IntTag intTag) {
                byte value = (byte) intTag.getValue();
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
    public static Dimension fromBedrock(byte id, Dimension fallback) {
        return dimensionByBedrockId.getOrDefault(id, fallback);
    }

    /**
     * Create a dimension from a Java ID.
     *
     * @param id       the input ID.
     * @param fallback the fallback to use if the ID wasn't found.
     * @return the dimension or fallback if it wasn't found.
     */
    public static Dimension fromJava(byte id, Dimension fallback) {
        return dimensionByJavaId.getOrDefault(id, fallback);
    }

    /**
     * The Java ID for the dimension.
     *
     * @return the ID based on 0 being overworld and -1 being the nether.
     */
    public byte getJavaID() {
        return javaID;
    }

    /**
     * The Bedrock ID for the dimension.
     *
     * @return the indexed ID for the dimension.
     */
    public byte getBedrockID() {
        return bedrockID;
    }

    /**
     * Get the namespaced identifier for the dimension.
     *
     * @return the namespaced identifier.
     */
    public String getIdentifier() {
        return identifier;
    }
}
