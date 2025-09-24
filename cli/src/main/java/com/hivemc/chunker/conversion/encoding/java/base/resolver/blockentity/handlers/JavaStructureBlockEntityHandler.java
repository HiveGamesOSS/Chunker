package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.StructureBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.StructureBlockMode;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.InvertibleMap;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Structure Block Entities.
 */
public class JavaStructureBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, StructureBlockEntity> {
    /**
     * Mapping of rotations to java strings.
     */
    public static final InvertibleMap<StructureBlockMode, String> MODE_TO_STRING = InvertibleMap.enumKeys(StructureBlockMode.class);

    /**
     * Mapping of rotations to java strings.
     */
    public static final InvertibleMap<StructureBlockEntity.Rotation, String> ROTATION_TO_STRING = InvertibleMap.enumKeys(StructureBlockEntity.Rotation.class);

    /**
     * Mapping of mirror to java strings.
     */
    public static final InvertibleMap<StructureBlockEntity.Mirror, String> MIRROR_TO_STRING = InvertibleMap.enumKeys(StructureBlockEntity.Mirror.class);

    static {
        // Type -> String mappings

        // Not supported on Java
        MODE_TO_STRING.put(StructureBlockMode.EXPORT, "SAVE");
        MODE_TO_STRING.put(StructureBlockMode.INVALID, "SAVE");

        // Supported values
        MODE_TO_STRING.put(StructureBlockMode.SAVE, "SAVE");
        MODE_TO_STRING.put(StructureBlockMode.LOAD, "LOAD");
        MODE_TO_STRING.put(StructureBlockMode.CORNER, "CORNER");
        MODE_TO_STRING.put(StructureBlockMode.DATA, "DATA");

        // Rotation -> String mappings
        ROTATION_TO_STRING.put(StructureBlockEntity.Rotation.NONE, "NONE");
        ROTATION_TO_STRING.put(StructureBlockEntity.Rotation.CLOCKWISE_90, "CLOCKWISE_90");
        ROTATION_TO_STRING.put(StructureBlockEntity.Rotation.CLOCKWISE_180, "CLOCKWISE_180");
        ROTATION_TO_STRING.put(StructureBlockEntity.Rotation.COUNTER_CLOCKWISE_90, "COUNTERCLOCKWISE_90");

        // Mirror -> String mappings
        MIRROR_TO_STRING.put(StructureBlockEntity.Mirror.NONE, "NONE");
        MIRROR_TO_STRING.put(StructureBlockEntity.Mirror.LEFT_RIGHT, "LEFT_RIGHT");
        MIRROR_TO_STRING.put(StructureBlockEntity.Mirror.FRONT_BACK, "FRONT_BACK");
    }

    public JavaStructureBlockEntityHandler() {
        super("minecraft:structure_block", StructureBlockEntity.class, StructureBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull StructureBlockEntity value) {
        value.setName(input.getString("name", ""));
        value.setAuthor(input.getString("author", ""));
        value.setMetadata(input.getString("metadata", ""));
        value.setPosX(input.getInt("posX", 0));
        value.setPosY(input.getInt("posY", 0));
        value.setPosZ(input.getInt("posZ", 0));
        value.setSizeX(input.getInt("sizeX", 0));
        value.setSizeY(input.getInt("sizeY", 0));
        value.setSizeZ(input.getInt("sizeZ", 0));
        value.setRotation(ROTATION_TO_STRING.inverse().getOrDefault(input.getString("rotation", "none"), StructureBlockEntity.Rotation.NONE));
        value.setMirror(MIRROR_TO_STRING.inverse().getOrDefault(input.getString("mirror", "none"), StructureBlockEntity.Mirror.NONE));
        value.setMode(MODE_TO_STRING.inverse().getOrDefault(input.getString("mode", "data"), StructureBlockMode.DATA));
        value.setIgnoreEntities(input.getByte("ignoreEntities", (byte) 1) == (byte) 1);

        // New strict field in 1.21.5
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 21, 5)) {
            value.setStrict(input.getByte("strict", (byte) 0) == (byte) 1);
        }

        // New fields added in 1.10
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 10, 0)) {
            value.setPowered(input.getByte("powered", (byte) 0) == (byte) 1);
            value.setShowAir(input.getByte("showair", (byte) 0) == (byte) 1);
            value.setShowBoundingBox(input.getByte("showboundingbox", (byte) 1) == (byte) 1);
            value.setIntegrity(input.getFloat("integrity", 1));
            value.setSeed(input.getLong("seed", 0));
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull StructureBlockEntity value) {
        output.put("name", value.getName());
        output.put("author", value.getAuthor());
        output.put("metadata", value.getMetadata());
        output.put("posX", value.getPosX());
        output.put("posY", value.getPosY());
        output.put("posZ", value.getPosZ());
        output.put("sizeX", value.getSizeX());
        output.put("sizeY", value.getSizeY());
        output.put("sizeZ", value.getSizeZ());
        output.put("rotation", ROTATION_TO_STRING.forward().getOrDefault(value.getRotation(), "none"));
        output.put("mirror", MIRROR_TO_STRING.forward().getOrDefault(value.getMirror(), "none"));
        output.put("mode", MODE_TO_STRING.forward().getOrDefault(value.getMode(), "data"));
        output.put("ignoreEntities", value.isIgnoreEntities() ? (byte) 1 : (byte) 0);

        // New strict field in 1.21.5
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 21, 5)) {
            output.put("strict", value.isStrict() ? (byte) 1 : (byte) 0);
        }

        // New fields added in 1.10
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 10, 0)) {
            output.put("powered", value.isPowered() ? (byte) 1 : (byte) 0);
            output.put("showair", value.isShowAir() ? (byte) 1 : (byte) 0);
            output.put("showboundingbox", value.isShowBoundingBox() ? (byte) 1 : (byte) 0);
            output.put("integrity", value.getIntegrity());
            output.put("seed", value.getSeed());
        }
    }
}
