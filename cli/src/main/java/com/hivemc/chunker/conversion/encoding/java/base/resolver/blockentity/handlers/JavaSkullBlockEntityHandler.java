package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.CustomItemNBTBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.SkullBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.array.IntArrayTag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Handler for Skull/Head Block Entities.
 */
public class JavaSkullBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, SkullBlockEntity> implements CustomItemNBTBlockEntityHandler<JavaResolvers, SkullBlockEntity> {
    public JavaSkullBlockEntityHandler() {
        super("minecraft:skull", SkullBlockEntity.class, SkullBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull SkullBlockEntity value) {
        // Extra type is skull name
        if (input.contains("ExtraType")) {
            value.setOwnerName(input.getString("ExtraType", null));
        }

        // Extract various skull info
        if (input.contains("profile") || input.contains("Owner") || input.contains("SkullOwner")) {
            CompoundTag profile = input.getCompound(input.contains("profile") ? "profile" : input.contains("Owner") ? "Owner" : "SkullOwner");

            // Owner Id
            Tag<?> idTag = Objects.requireNonNull(profile).get(input.contains("id") ? "id" : "Id");
            if (idTag != null) {
                if (idTag instanceof StringTag stringTag) {
                    value.setOwnerId(stringTag.getValue());
                } else if (idTag instanceof IntArrayTag intArrayTag) {
                    // Id is an int array
                    int[] ints = intArrayTag.getValue();
                    if (ints != null && ints.length == 4) {
                        value.setOwnerId(new UUID(
                                (long) ints[0] << 32 | (ints[1] & 0xFFFFFFFFL),
                                (long) ints[2] << 32 | (ints[3] & 0xFFFFFFFFL))
                                .toString());
                    }
                }
            }

            // Owner Name
            if (profile.contains("name")) {
                value.setOwnerName(profile.getString("name", null));
            }
            if (profile.contains("Name")) {
                value.setOwnerName(profile.getString("Name", null));
            }

            // Textures
            ListTag<CompoundTag, Map<String, Tag<?>>> textures = null;
            if (profile.contains("properties")) {
                textures = profile.getList("properties", CompoundTag.class, null);
            } else {
                CompoundTag properties = profile.getCompound("Properties");
                if (properties != null) {
                    textures = properties.getList("textures", CompoundTag.class, null);
                }
            }
            if (textures != null) {
                if (textures.size() > 0) {
                    CompoundTag texture = textures.get(0);

                    // Value
                    if (texture.contains("value")) {
                        value.setTexture(texture.getString("value", null));
                    } else {
                        value.setTexture(texture.getString("Value", null));
                    }

                    // Signature
                    if (texture.contains("signature")) {
                        value.setTextureSignature(texture.getString("signature", null));
                    } else {
                        value.setTextureSignature(texture.getString("Signature", null));
                    }
                }
            }
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull SkullBlockEntity value) {
        // Write the tags normally (Name tag is special though)
        if (value.getOwnerId() != null || value.getTexture() != null || value.getTextureSignature() != null || (value.getOwnerName() != null && resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 12, 0))) {
            CompoundTag owner = new CompoundTag(3);

            // Add owner tag if present
            if (value.getOwnerId() != null) {
                // If it's the 1.16 write as the integer parts
                if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 16, 0)) {
                    UUID uuid = UUID.fromString(value.getOwnerId());
                    owner.put(resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 5) ? "id" : "Id", new IntArrayTag(new int[]{
                            (int) (uuid.getMostSignificantBits() >> 32),
                            (int) uuid.getMostSignificantBits(),
                            (int) (uuid.getLeastSignificantBits() >> 32),
                            (int) uuid.getLeastSignificantBits()
                    }));
                } else {
                    owner.put("Id", value.getOwnerId());
                }
            }

            // Add name tag if present
            if (value.getOwnerName() != null) {
                owner.put(resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 5) ? "name" : "Name", value.getOwnerName());
            }

            // Add properties/textures/value tag
            if (value.getTexture() != null || value.getTextureSignature() != null) {
                CompoundTag textureEntry = new CompoundTag(2);
                if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 5)) {
                    textureEntry.put("name", "textures");
                }

                // Add texture
                if (value.getTexture() != null) {
                    textureEntry.put(resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 5) ? "value" : "Value", value.getTexture());
                }

                // Add signature
                if (value.getTextureSignature() != null) {
                    textureEntry.put(resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 5) ? "signature" : "Signature", value.getTextureSignature());
                }

                // Add to list
                ListTag<CompoundTag, Map<String, Tag<?>>> textures = new ListTag<>(TagType.COMPOUND, List.of(textureEntry));
                if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 5)) {
                    owner.put("properties", textures);
                } else {
                    owner.put("Properties", new CompoundTag(Map.of("textures", textures)));
                }
            }

            // Write as profile for 1.20.5 and above
            if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 5)) {
                output.put("profile", owner);
            } else {
                output.put(resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 16, 0) ? "SkullOwner" : "Owner", owner);
            }
        } else if (value.getOwnerName() != null) {
            // Special old writing for skulls
            output.put("ExtraType", value.getOwnerName());
        }
    }

    @Override
    public boolean generateFromItemNBT(@NotNull JavaResolvers resolvers, @NotNull ChunkerItemStack itemStack, @NotNull SkullBlockEntity output, @NotNull CompoundTag input) {
        if (resolvers.dataVersion().getVersion().isLessThan(1, 20, 5)) return false; // Components not needed

        CompoundTag components = input.getCompound("components");
        if (components == null) return false; // No components

        // Get the profile
        CompoundTag profile = components.getCompound("minecraft:profile");
        if (profile == null) return false;

        // Owner Id
        Tag<?> idTag = Objects.requireNonNull(profile).get("id");
        if (idTag instanceof IntArrayTag intArrayTag) {
            // Id is an int array
            int[] ints = intArrayTag.getValue();
            if (ints != null && ints.length == 4) {
                output.setOwnerId(new UUID(
                        (long) ints[0] << 32 | (ints[1] & 0xFFFFFFFFL),
                        (long) ints[2] << 32 | (ints[3] & 0xFFFFFFFFL))
                        .toString());
            }
        }

        // Owner Name
        if (profile.contains("name")) {
            output.setOwnerName(profile.getString("name", null));
        }

        // Textures
        ListTag<CompoundTag, Map<String, Tag<?>>> textures = null;
        if (profile.contains("properties")) {
            textures = profile.getList("properties", CompoundTag.class, null);
        }

        if (textures != null) {
            if (textures.size() > 0) {
                CompoundTag texture = textures.get(0);

                // Value
                if (texture.contains("value")) {
                    output.setTexture(texture.getString("value", null));
                }

                // Signature
                if (texture.contains("signature")) {
                    output.setTextureSignature(texture.getString("signature", null));
                }
            }
        }

        // Return true if textures / name were present
        return output.getTexture() != null || output.getOwnerId() != null || output.getOwnerName() != null;
    }

    @Override
    public boolean writeToItemNBT(@NotNull JavaResolvers resolvers, @NotNull ChunkerItemStack itemStack, @NotNull SkullBlockEntity input, @NotNull CompoundTag output) {
        if (resolvers.dataVersion().getVersion().isLessThan(1, 20, 5))
            return true; // Components not needed (write normally)

        // If present add the component
        if (input.getOwnerId() != null || input.getTexture() != null || input.getTextureSignature() != null || (input.getOwnerName() != null)) {
            CompoundTag profile = output.getOrCreateCompound("components").getOrCreateCompound("minecraft:profile");

            // Add owner tag if present
            if (input.getOwnerId() != null) {
                UUID uuid = UUID.fromString(input.getOwnerId());
                profile.put("id", new IntArrayTag(new int[]{
                        (int) (uuid.getMostSignificantBits() >> 32),
                        (int) uuid.getMostSignificantBits(),
                        (int) (uuid.getLeastSignificantBits() >> 32),
                        (int) uuid.getLeastSignificantBits()
                }));
            }

            // Add name tag if present
            if (input.getOwnerName() != null) {
                profile.put("name", input.getOwnerName());
            }

            // Add properties/textures/value tag
            if (input.getTexture() != null || input.getTextureSignature() != null) {
                CompoundTag textureEntry = new CompoundTag(2);
                textureEntry.put("name", "textures");

                // Add texture
                if (input.getTexture() != null) {
                    textureEntry.put("value", input.getTexture());
                }

                // Add signature
                if (input.getTextureSignature() != null) {
                    textureEntry.put("signature", input.getTextureSignature());
                }

                // Add to list
                ListTag<CompoundTag, Map<String, Tag<?>>> textures = new ListTag<>(TagType.COMPOUND, List.of(textureEntry));
                profile.put("properties", textures);
            }
        }

        return false; // Don't write the block entity data
    }
}
