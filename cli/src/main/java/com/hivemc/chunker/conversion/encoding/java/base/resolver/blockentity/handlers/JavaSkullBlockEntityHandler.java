package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.SkullBlockEntity;
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
public class JavaSkullBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, SkullBlockEntity> {
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
        if (input.contains("Owner") || input.contains("SkullOwner")) {
            CompoundTag owner = input.getCompound(input.contains("Owner") ? "Owner" : "SkullOwner");

            // Owner Id
            Tag<?> idTag = Objects.requireNonNull(owner).get("Id");
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
            if (owner.contains("Name")) {
                value.setOwnerName(owner.getString("Name", null));
            }

            // Textures
            CompoundTag properties = owner.getCompound("Properties");
            if (properties != null) {
                ListTag<CompoundTag, Map<String, Tag<?>>> textures = properties.getList("textures", CompoundTag.class, null);
                if (textures != null) {
                    if (textures.size() > 0) {
                        CompoundTag texture = textures.get(0);
                        value.setTexture(texture.getString("Value", null));
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
                    owner.put("Id", new IntArrayTag(new int[]{
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
            if (value.getOwnerName() != null && resolvers.dataVersion().getVersion().isGreaterThan(1, 12, 0)) {
                owner.put("Name", value.getOwnerName());
            }

            // Add properties/textures/value tag
            if (value.getTexture() != null || value.getTextureSignature() != null) {
                CompoundTag textureEntry = new CompoundTag(2);

                // Add texture
                if (value.getTexture() != null) {
                    textureEntry.put("Value", value.getTexture());
                }

                // Add signature
                if (value.getTextureSignature() != null) {
                    textureEntry.put("Signature", value.getTextureSignature());
                }

                // Add to list
                ListTag<CompoundTag, Map<String, Tag<?>>> textures = new ListTag<>(TagType.COMPOUND, List.of(textureEntry));
                owner.put("Properties", new CompoundTag(Map.of("textures", textures)));
            }

            output.put(resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 16, 0) ? "SkullOwner" : "Owner", owner);
        } else if (value.getOwnerName() != null) {
            // Special old writing for skulls
            output.put("ExtraType", value.getOwnerName());
        }
    }
}
