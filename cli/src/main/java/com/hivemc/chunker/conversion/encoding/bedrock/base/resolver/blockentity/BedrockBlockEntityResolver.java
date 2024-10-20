package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityResolver;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.EmptyBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers.*;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockGlowItemFrameBlockEntity;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type.BedrockMovingBlockBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.*;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.BlastFurnaceBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.EnderChestBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.HopperBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.SmokerBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.*;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.end.EndPortalBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.sculksensor.CalibratedSculkSensorBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.sign.HangingSignBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifierType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.util.Optional;

/**
 * Resolver for converting Bedrock block entities between Chunker and NBT.
 */
public class BedrockBlockEntityResolver extends BlockEntityResolver<BedrockResolvers, CompoundTag> {
    /**
     * Create a new bedrock block entity resolver.
     *
     * @param version   the bedrock version.
     * @param resolvers the resolvers to use.
     */
    public BedrockBlockEntityResolver(Version version, BedrockResolvers resolvers) {
        super(version, resolvers, resolvers.converter().shouldAllowNBTCopying());
    }

    @Override
    protected void registerTypeHandlers(Version version) {
        // Handlers which write/read abstract types which others use
        register(new BedrockBlockEntityHandler());
        register(new BedrockContainerBlockEntityHandler());
        register(new BedrockRandomizableContainerBlockEntityHandler(resolvers.converter().shouldProcessLootTables()));

        // Chest handler
        register(new BedrockChestBlockEntityHandler()); // Also splits into TrappedChest (which has an empty handler below)
        register(new EmptyBlockEntityHandler<>("TrappedChest", TrappedChestBlockEntity.class, () -> {
            throw new IllegalArgumentException("Cannot make TrappedChest");
        }));

        register(new BedrockFurnaceBlockEntityHandler());
        register(new BedrockSignBlockEntityHandler());
        register(new BedrockSpawnerBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("EnchantTable", EnchantmentTableBlockEntity.class, EnchantmentTableBlockEntity::new));
        register(new BedrockSkullBlockEntityHandler());
        register(new BedrockFlowerPotBlockEntityHandler());
        register(new BedrockBrewingStandBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("DaylightDetector", DaylightDetectorBlockEntity.class, DaylightDetectorBlockEntity::new));
        register(new BedrockNoteBlockBlockEntityHandler());
        register(new BedrockJukeboxBlockEntityHandler());
        register(new BedrockComparatorBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("Dispenser", DispenserBlockEntity.class, DispenserBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("Dropper", DropperBlockEntity.class, DropperBlockEntity::new));
        register(new BedrockCrafterBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("Hopper", HopperBlockEntity.class, HopperBlockEntity::new));
        register(new BedrockCauldronBlockEntityHandler());
        register(new BedrockItemFrameBlockEntityHandler()); // Bedrock only
        register(new BedrockPistonArmBlockEntityHandler());
        register(new BedrockBeaconBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("MovingBlock", BedrockMovingBlockBlockEntity.class, BedrockMovingBlockBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("EndPortal", EndPortalBlockEntity.class, EndPortalBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("EnderChest", EnderChestBlockEntity.class, EnderChestBlockEntity::new));
        register(new BedrockEndGatewayBlockEntityHandler());
        register(new BedrockBannerBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("StructureBlock", StructureBlockEntity.class, StructureBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("JigsawBlock", JigsawBlockEntity.class, JigsawBlockEntity::new));
        register(new BedrockShulkerBoxBlockEntityHandler());
        register(new BedrockCommandBlockBlockEntityHandler());
        register(new BedrockBedBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("Conduit", ConduitBlockEntity.class, ConduitBlockEntity::new));
        register(new BedrockLecternBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("BlastFurnace", BlastFurnaceBlockEntity.class, BlastFurnaceBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("Bell", BellBlockEntity.class, BellBlockEntity::new));
        register(new EmptyBlockEntityHandler<>("Smoker", SmokerBlockEntity.class, SmokerBlockEntity::new));
        register(new BedrockCampfireBlockEntityHandler());
        register(new EmptyBlockEntityHandler<>("Barrel", BarrelBlockEntity.class, BarrelBlockEntity::new));

        // R16
        if (version.isGreaterThanOrEqual(1, 16, 0)) {
            register(new BedrockLodestoneBlockEntityHandler());
            register(new EmptyBlockEntityHandler<>("Beehive", BeehiveBlockEntity.class, BeehiveBlockEntity::new));
        }

        // R17
        if (version.isGreaterThanOrEqual(1, 17, 0)) {
            register(new EmptyBlockEntityHandler<>("GlowItemFrame", BedrockGlowItemFrameBlockEntity.class, BedrockGlowItemFrameBlockEntity::new));
            register(new BedrockSculkSensorBlockEntityHandler());
            register(new BedrockSporeBlossomBlockEntityHandler());
        }

        // R17U3
        if (version.isGreaterThanOrEqual(1, 17, 30)) {
            register(new EmptyBlockEntityHandler<>("SculkCatalyst", SculkCatalystBlockEntity.class, SculkCatalystBlockEntity::new));
            register(new EmptyBlockEntityHandler<>("SculkShrieker", SculkShriekerBlockEntity.class, SculkShriekerBlockEntity::new));
        }

        // R19U5
        if (version.isGreaterThanOrEqual(1, 19, 50)) {
            register(new BedrockChiseledBookshelfBlockEntityHandler());
            register(new EmptyBlockEntityHandler<>("HangingSign", HangingSignBlockEntity.class, HangingSignBlockEntity::new));
        }

        // R19U7
        if (version.isGreaterThanOrEqual(1, 19, 70)) {
            register(new BedrockDecoratedPotBlockEntityHandler());
            register(new BedrockBrushableBlockEntityHandler());
        }

        // R19U8
        if (version.isGreaterThanOrEqual(1, 19, 80)) {
            register(new EmptyBlockEntityHandler<>("CalibratedSculkSensor", CalibratedSculkSensorBlockEntity.class, CalibratedSculkSensorBlockEntity::new));
        }

        // R20U6
        if (version.isGreaterThanOrEqual(1, 20, 60)) {
            register(new EmptyBlockEntityHandler<>("TrialSpawner", TrialSpawnerBlockEntity.class, TrialSpawnerBlockEntity::new));
        }

        // R20U7
        if (version.isGreaterThanOrEqual(1, 20, 70)) {
            register(new EmptyBlockEntityHandler<>("Vault", VaultBlockEntity.class, VaultBlockEntity::new));
        }

        // R21U5
        if (version.isGreaterThanOrEqual(1, 21, 50)) {
            register(new EmptyBlockEntityHandler<>("CreakingHeart", CreakingHeartBlockEntity.class, CreakingHeartBlockEntity::new));
        }

        // Not used: register(new EmptyBlockEntityHandler<>("NetherReactor", NetherReactorBlockEntity.class, NetherReactorBlockEntity::new));
        // EDU: register(new EmptyBlockEntityHandler<>("ChalkboardBlock", ChalkboardBlockEntity.class, ChalkboardBlockEntity::new));
        // EDU: register(new EmptyBlockEntityHandler<>("ChemistryTable", ChemistryTableBlockEntity.class, ChemistryTableBlockEntity::new));
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
        return input.getOptionalValue("id", String.class);
    }

    @Override
    public Optional<Class<? extends BlockEntity>> getBlockEntityClass(ChunkerItemStackIdentifierType itemStackType) {
        // Bedrock doesn't have trapped chest
        if (itemStackType == ChunkerVanillaBlockType.TRAPPED_CHEST) {
            return Optional.of(ChestBlockEntity.class);
        }
        return super.getBlockEntityClass(itemStackType);
    }
}
