package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.StructureBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.StructureBlockMode;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.InvertibleMap;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Structure Blocks.
 */
public class BedrockStructureBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, StructureBlockEntity> {
    /**
     * Mapping of rotations to bedrock bytes.
     */
    public static final InvertibleMap<StructureBlockMode, Integer> MODE_TO_INT = InvertibleMap.enumKeys(StructureBlockMode.class);

    /**
     * Mapping of rotations to bedrock bytes.
     */
    public static final InvertibleMap<StructureBlockEntity.Rotation, Byte> ROTATION_TO_BYTE = InvertibleMap.enumKeys(StructureBlockEntity.Rotation.class);

    /**
     * Mapping of mirror to bedrock bytes.
     */
    public static final InvertibleMap<StructureBlockEntity.Mirror, Byte> MIRROR_TO_BYTE = InvertibleMap.enumKeys(StructureBlockEntity.Mirror.class);

    static {
        // Type -> Integer mappings
        MODE_TO_INT.put(StructureBlockMode.DATA, 0);
        MODE_TO_INT.put(StructureBlockMode.SAVE, 1);
        MODE_TO_INT.put(StructureBlockMode.LOAD, 2);
        MODE_TO_INT.put(StructureBlockMode.CORNER, 3);
        MODE_TO_INT.put(StructureBlockMode.INVALID, 4);
        MODE_TO_INT.put(StructureBlockMode.EXPORT, 5);

        // Rotation -> Byte mappings
        ROTATION_TO_BYTE.put(StructureBlockEntity.Rotation.NONE, (byte) 0);
        ROTATION_TO_BYTE.put(StructureBlockEntity.Rotation.CLOCKWISE_90, (byte) 1);
        ROTATION_TO_BYTE.put(StructureBlockEntity.Rotation.CLOCKWISE_180, (byte) 2);
        ROTATION_TO_BYTE.put(StructureBlockEntity.Rotation.COUNTER_CLOCKWISE_90, (byte) 3);

        // Mirror -> Byte mappings
        MIRROR_TO_BYTE.put(StructureBlockEntity.Mirror.NONE, (byte) 0);
        MIRROR_TO_BYTE.put(StructureBlockEntity.Mirror.LEFT_RIGHT, (byte) 1);
        MIRROR_TO_BYTE.put(StructureBlockEntity.Mirror.FRONT_BACK, (byte) 2);
        MIRROR_TO_BYTE.put(StructureBlockEntity.Mirror.BOTH, (byte) 3);
    }

    public BedrockStructureBlockEntityHandler() {
        super("StructureBlock", StructureBlockEntity.class, StructureBlockEntity::new);
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull StructureBlockEntity value) {
        value.setName(input.getString("structureName", ""));
        value.setMetadata(input.getString("dataField", ""));
        value.setPosX(input.getInt("xStructureOffset", 0));
        value.setPosY(input.getInt("yStructureOffset", 0));
        value.setPosZ(input.getInt("zStructureOffset", 0));
        value.setSizeX(input.getInt("xStructureSize", 0));
        value.setSizeY(input.getInt("yStructureSize", 0));
        value.setSizeZ(input.getInt("zStructureSize", 0));
        value.setMode(MODE_TO_INT.inverse().getOrDefault(input.getInt("data", 0), StructureBlockMode.DATA));
        value.setRotation(ROTATION_TO_BYTE.inverse().getOrDefault(input.getByte("rotation", (byte) 0), StructureBlockEntity.Rotation.NONE));
        value.setMirror(MIRROR_TO_BYTE.inverse().getOrDefault(input.getByte("mirror", (byte) 0), StructureBlockEntity.Mirror.NONE));
        value.setIgnoreEntities(input.getByte("ignoreEntities", (byte) 0) == (byte) 1);
        value.setIncludePlayers(input.getByte("includePlayers", (byte) 0) == (byte) 1);
        value.setRemoveBlocks(input.getByte("removeBlocks", (byte) 0) == (byte) 1);
        value.setPowered(input.getByte("isPowered", (byte) 0) == (byte) 1);
        value.setShowBoundingBox(input.getByte("showBoundingBox", (byte) 1) == (byte) 1);
        value.setIntegrity(input.getFloat("integrity", 100) / 100F);
        value.setSeed(input.getLong("seed", 0));

        // Added in ~1.13
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 13, 0)) {
            value.setRedstoneSaveMode(input.getInt("redstoneSaveMode", 0));
        }

        // Added in 1.21.20
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 21, 20)) {
            value.setLastTouchedPlayerId(input.getLong("lastTouchedPlayerId", 0));
        }

        // Added in 1.16.230 / 1.17
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 16, 230)) {
            value.setAnimationMode(input.getByte("animationMode", (byte) 0));
            value.setAnimationSeconds(input.getFloat("animationSeconds", 0f));
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull StructureBlockEntity value) {
        output.put("structureName", value.getName());
        output.put("dataField", value.getMetadata());
        output.put("xStructureOffset", value.getPosX());
        output.put("yStructureOffset", value.getPosY());
        output.put("zStructureOffset", value.getPosZ());
        output.put("xStructureSize", value.getSizeX());
        output.put("yStructureSize", value.getSizeY());
        output.put("zStructureSize", value.getSizeZ());
        output.put("data", MODE_TO_INT.forward().getOrDefault(value.getMode(), 0));
        output.put("rotation", ROTATION_TO_BYTE.forward().getOrDefault(value.getRotation(), (byte) 0));
        output.put("mirror", MIRROR_TO_BYTE.forward().getOrDefault(value.getMirror(), (byte) 0));
        output.put("ignoreEntities", value.isIgnoreEntities() ? (byte) 1 : (byte) 0);
        output.put("includePlayers", value.isIncludePlayers() ? (byte) 1 : (byte) 0);
        output.put("removeBlocks", value.isRemoveBlocks() ? (byte) 1 : (byte) 0);
        output.put("isPowered", value.isPowered() ? (byte) 1 : (byte) 0);
        output.put("showBoundingBox", value.isShowBoundingBox() ? (byte) 1 : (byte) 0);
        output.put("integrity", value.getIntegrity() * 100F);
        output.put("seed", value.getSeed());

        // Added in ~1.13
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 13, 0)) {
            output.put("redstoneSaveMode", value.getRedstoneSaveMode());
        }

        // Added in 1.21.20
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 21, 20)) {
            output.put("lastTouchedPlayerId", value.getLastTouchedPlayerId());
        }

        // Added in 1.16.230 / 1.17
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 16, 230)) {
            output.put("animationMode", value.getAnimationMode());
            output.put("animationSeconds", value.getAnimationSeconds());
        }
    }
}
