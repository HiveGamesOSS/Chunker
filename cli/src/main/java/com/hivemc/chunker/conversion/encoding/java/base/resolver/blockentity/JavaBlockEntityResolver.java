package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityResolver;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.EmptyBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers.*;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.*;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.BlastFurnaceBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.EnderChestBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.HopperBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.SmokerBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.*;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.end.EndPortalBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.sculksensor.CalibratedSculkSensorBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.sign.HangingSignBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.util.Optional;

/**
 * Resolver for converting Java block entities between Chunker and NBT.
 */
public class JavaBlockEntityResolver extends BlockEntityResolver<JavaResolvers, CompoundTag> {
    /**
     * Create a new java block entity resolver.
     *
     * @param version   the java version.
     * @param resolvers the resolvers to use.
     */
    public JavaBlockEntityResolver(Version version, JavaResolvers resolvers) {
        super(version, resolvers, resolvers.converter().shouldAllowNBTCopying());
    }

    @Override
    protected void registerTypeHandlers(Version version) {
        // Handlers which write/read abstract types which others use
        register(new JavaBlockEntityHandler());
        register(new JavaContainerBlockEntityHandler());
        register(new JavaRandomizableContainerBlockEntityHandler(resolvers.converter().shouldProcessLootTables()));

        // Cauldron isn't a block entity in Java
        register(new JavaCauldronBlockEntityHandler());

        // Block entities
        register(new JavaFurnaceBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("minecraft:chest", ChestBlockEntity.class, ChestBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("minecraft:trapped_chest", TrappedChestBlockEntity.class, TrappedChestBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("minecraft:ender_chest", EnderChestBlockEntity.class, EnderChestBlockEntity::new));
        register(new JavaJukeboxBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("minecraft:dispenser", DispenserBlockEntity.class, DispenserBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("minecraft:dropper", DropperBlockEntity.class, DropperBlockEntity::new));
        register(new JavaSignBlockEntityHandler());
        register(new JavaSpawnerBlockEntityHandler());
        register(new JavaPistonArmBlockEntityHandler());
        register(new JavaBrewingStandBlockEntityHandler());
        register(new JavaEnchantmentBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("minecraft:end_portal", EndPortalBlockEntity.class, EndPortalBlockEntity::new));
        register(new JavaBeaconBlockEntityHandler());
        register(new JavaSkullBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("minecraft:daylight_detector", DaylightDetectorBlockEntity.class, DaylightDetectorBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("minecraft:hopper", HopperBlockEntity.class, HopperBlockEntity::new));
        register(new JavaComparatorBlockEntityHandler());
        register(new JavaBannerBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("minecraft:structure_block", StructureBlockEntity.class, StructureBlockEntity::new));
        register(new JavaEndGatewayBlockEntityHandler());
        register(new JavaCommandBlockBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("minecraft:shulker_box", ShulkerBoxBlockEntity.class, ShulkerBoxBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("minecraft:bed", BedBlockEntity.class, BedBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("minecraft:conduit", ConduitBlockEntity.class, ConduitBlockEntity::new));
        if (version.isGreaterThanOrEqual(1, 14, 0)) {
            register(new JavaCampfireBlockEntityHandler());
            register(new EmptyBlockEntityHandler<>("minecraft:jigsaw", JigsawBlockEntity.class, JigsawBlockEntity::new));
            register(new EmptyBlockEntityHandler<>("minecraft:bell", BellBlockEntity.class, BellBlockEntity::new));
            register(new JavaLecternBlockEntityHandler());
            register(new EmptyBlockEntityHandler<>("minecraft:blast_furnace", BlastFurnaceBlockEntity.class, BlastFurnaceBlockEntity::new));
            register(new EmptyBlockEntityHandler<>("minecraft:smoker", SmokerBlockEntity.class, SmokerBlockEntity::new));
            register(new EmptyBlockEntityHandler<>("minecraft:barrel", BarrelBlockEntity.class, BarrelBlockEntity::new));
        }
        if (version.isGreaterThanOrEqual(1, 15, 0)) {
            register(new EmptyBlockEntityHandler<>("minecraft:beehive", BeehiveBlockEntity.class, BeehiveBlockEntity::new));
        }
        if (version.isGreaterThanOrEqual(1, 17, 0)) {
            register(new JavaSculkSensorBlockEntityHandler());
        }
        if (version.isGreaterThanOrEqual(1, 19, 0)) {
            register(new EmptyBlockEntityHandler<>("minecraft:sculk_catalyst", SculkCatalystBlockEntity.class, SculkCatalystBlockEntity::new));
            register(new EmptyBlockEntityHandler<>("minecraft:sculk_shrieker", SculkShriekerBlockEntity.class, SculkShriekerBlockEntity::new));
        }
        if (version.isGreaterThanOrEqual(1, 19, 3)) {
            register(new JavaChiseledBookshelfBlockEntityHandler());
        }
        if (version.isGreaterThanOrEqual(1, 19, 4)) {
            register(new JavaBrushableBlockEntityHandler());
            register(new JavaDecoratedPotBlockEntityHandler());
        }
        if (version.isGreaterThanOrEqual(1, 20, 0)) {
            register(new EmptyBlockEntityHandler<>("minecraft:hanging_sign", HangingSignBlockEntity.class, HangingSignBlockEntity::new));
            register(new EmptyBlockEntityHandler<>("minecraft:calibrated_sculk_sensor", CalibratedSculkSensorBlockEntity.class, CalibratedSculkSensorBlockEntity::new));
        }
        if (version.isGreaterThanOrEqual(1, 20, 3)) {
            register(new JavaCrafterBlockEntityHandler());
            register(new EmptyBlockEntityHandler<>("minecraft:trial_spawner", TrialSpawnerBlockEntity.class, TrialSpawnerBlockEntity::new));
        }
        if (version.isGreaterThanOrEqual(1, 20, 5)) {
            register(new EmptyBlockEntityHandler<>("minecraft:vault", VaultBlockEntity.class, VaultBlockEntity::new));
        }
        if (version.isGreaterThanOrEqual(1, 21, 2)) {
            register(new EmptyBlockEntityHandler<>("minecraft:creaking_heart", CreakingHeartBlockEntity.class, CreakingHeartBlockEntity::new));
        }
        if (version.isGreaterThanOrEqual(1, 21, 9)) {
            register(new JavaShelfBlockEntityHandler());
        }
    }

    @Override
    protected CompoundTag constructDataType(String key) {
        // Create a new compoundTag with the ID
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("id", key);
        return compoundTag;
    }

    @Override
    public Optional<String> getKey(CompoundTag input) {
        // Use id but ensure there is a minecraft: prefix
        return input.getOptionalValue("id", String.class).map(a -> !a.contains(":") ? "minecraft:" + a : a);
    }
}
