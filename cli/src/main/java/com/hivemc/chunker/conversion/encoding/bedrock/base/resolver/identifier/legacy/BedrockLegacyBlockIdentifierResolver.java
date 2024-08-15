package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy;

import com.google.common.collect.ImmutableMultimap;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.BlockMapping;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.ChunkerBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateMappingGroup;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockStateTypes;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.*;
import it.unimi.dsi.fastutil.Pair;

import java.util.List;
import java.util.Map;

/**
 * Resolver to convert between legacy Bedrock block identifiers and ChunkerBlockIdentifier.
 * Note: Waterlogging is automatically handled by this resolver and states don't need to be handled.
 */
public class BedrockLegacyBlockIdentifierResolver extends ChunkerBlockIdentifierResolver {
    /**
     * Default states which use 0 for data and false for waterlogged.
     */
    public static final StateMappingGroup DEFAULT_DATA_WATERLOGGED_FALSE = new StateMappingGroup.Builder()
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, BedrockStateTypes.BOOL_DEFAULT_FALSE)
            .defaultInput("data", 0)
            .build();

    /**
     * Create a new legacy bedrock block identifier resolver.
     *
     * @param converter                the converter instance.
     * @param version                  the version being resolved.
     * @param reader                   whether this is used for the reader.
     * @param customIdentifiersAllowed whether unknown identifiers should be converted to custom identifiers.
     */
    public BedrockLegacyBlockIdentifierResolver(Converter converter, Version version, boolean reader, boolean customIdentifiersAllowed) {
        super(converter, version, reader, customIdentifiersAllowed);
    }

    @Override
    public void registerMappings(Version version) {
        // Waterlogging on Bedrock is allowed on every block, so we use an extra state handler
        // This also sets data to 0 if it's not defined
        extraStateMappingGroup(DEFAULT_DATA_WATERLOGGED_FALSE);

        // Mappings
        register(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.AIR));
        registerDuplicateInput(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.CAVE_AIR));
        registerDuplicateInput(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.VOID_AIR));

        register(BlockMapping.group("growth", 7, ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:pumpkin_stem", ChunkerVanillaBlockType.ATTACHED_PUMPKIN_STEM)
                        .put("minecraft:melon_stem", ChunkerVanillaBlockType.ATTACHED_MELON_STEM)
                        .build(),
                BedrockLegacyStateGroups.STEM_FACING));
        register(BlockMapping.of("minecraft:bamboo", ChunkerVanillaBlockType.BAMBOO, BedrockLegacyStateGroups.BAMBOO));

        // Bamboo sampling
        register(BlockMapping.of("minecraft:bamboo_sapling", ChunkerVanillaBlockType.BAMBOO_SAPLING, BedrockLegacyStateGroups.BAMBOO_SAPLING));

        register(BlockMapping.of("minecraft:barrel", ChunkerVanillaBlockType.BARREL, BedrockLegacyStateGroups.BARREL));
        register(BlockMapping.of("minecraft:barrier", ChunkerVanillaBlockType.BARRIER));
        register(BlockMapping.of("minecraft:beacon", ChunkerVanillaBlockType.BEACON));

        // Bedrock
        register(BlockMapping.of("minecraft:bedrock", ChunkerVanillaBlockType.BEDROCK, BedrockLegacyStateGroups.BEDROCK));

        register(BlockMapping.of("minecraft:bell", ChunkerVanillaBlockType.BELL, BedrockLegacyStateGroups.BELL));
        register(BlockMapping.of("minecraft:blue_ice", ChunkerVanillaBlockType.BLUE_ICE));
        register(BlockMapping.of("minecraft:bookshelf", ChunkerVanillaBlockType.BOOKSHELF));
        register(BlockMapping.of("minecraft:brewing_stand", ChunkerVanillaBlockType.BREWING_STAND, BedrockLegacyStateGroups.BREWING_STAND));
        register(BlockMapping.of("minecraft:brick_block", ChunkerVanillaBlockType.BRICKS));
        register(BlockMapping.of("minecraft:brown_mushroom", ChunkerVanillaBlockType.BROWN_MUSHROOM));

        // Bubble columns
        register(BlockMapping.of("minecraft:bubble_column", ChunkerVanillaBlockType.BUBBLE_COLUMN, BedrockLegacyStateGroups.BUBBLE_COLUMN));

        register(BlockMapping.of("minecraft:cake", ChunkerVanillaBlockType.CAKE, BedrockLegacyStateGroups.CAKE));
        register(BlockMapping.of("minecraft:campfire", ChunkerVanillaBlockType.CAMPFIRE, BedrockLegacyStateGroups.CAMPFIRE));
        register(BlockMapping.of("minecraft:cartography_table", ChunkerVanillaBlockType.CARTOGRAPHY_TABLE));

        // Anvil
        register(BlockMapping.flatten("minecraft:anvil", "data", VanillaBlockStates.FACING_HORIZONTAL,
                ImmutableMultimap.<Integer, Pair<FacingDirectionHorizontal, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.ANVIL))
                        .put(1, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.ANVIL))
                        .put(2, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.ANVIL))
                        .put(3, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.ANVIL))
                        .put(4, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.CHIPPED_ANVIL))
                        .put(5, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.CHIPPED_ANVIL))
                        .put(6, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.CHIPPED_ANVIL))
                        .put(7, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.CHIPPED_ANVIL))
                        .put(8, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.DAMAGED_ANVIL))
                        .put(9, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.DAMAGED_ANVIL))
                        .put(10, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.DAMAGED_ANVIL))
                        .put(11, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.DAMAGED_ANVIL))
                        .build()
        ));

        // Broken looks like damaged
        registerDuplicateOutput(BlockMapping.flatten("minecraft:anvil", "data", VanillaBlockStates.FACING_HORIZONTAL,
                ImmutableMultimap.<Integer, Pair<FacingDirectionHorizontal, ? extends ChunkerBlockType>>builder()
                        .put(12, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.DAMAGED_ANVIL))
                        .put(13, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.DAMAGED_ANVIL))
                        .put(14, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.DAMAGED_ANVIL))
                        .put(15, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.DAMAGED_ANVIL))
                        .build()
        ));

        register(BlockMapping.of("minecraft:chorus_flower", ChunkerVanillaBlockType.CHORUS_FLOWER, BedrockLegacyStateGroups.CHORUS_FLOWER));
        register(BlockMapping.of("minecraft:chorus_plant", ChunkerVanillaBlockType.CHORUS_PLANT, BedrockLegacyStateGroups.CHORUS_PLANT));
        register(BlockMapping.of("minecraft:clay", ChunkerVanillaBlockType.CLAY));
        register(BlockMapping.of("minecraft:coal_block", ChunkerVanillaBlockType.COAL_BLOCK));
        register(BlockMapping.of("minecraft:coal_ore", ChunkerVanillaBlockType.COAL_ORE));
        register(BlockMapping.of("minecraft:cobblestone", ChunkerVanillaBlockType.COBBLESTONE));
        register(BlockMapping.of("minecraft:web", ChunkerVanillaBlockType.COBWEB));
        register(BlockMapping.of("minecraft:cocoa", ChunkerVanillaBlockType.COCOA, BedrockLegacyStateGroups.COCOA));

        // Comparators
        register(BlockMapping.of("minecraft:powered_comparator", ChunkerVanillaBlockType.COMPARATOR, BedrockLegacyStateGroups.COMPARATOR, VanillaBlockStates.POWERED, Bool.TRUE));
        register(BlockMapping.of("minecraft:unpowered_comparator", ChunkerVanillaBlockType.COMPARATOR, BedrockLegacyStateGroups.COMPARATOR, VanillaBlockStates.POWERED, Bool.FALSE));

        register(BlockMapping.of("minecraft:composter", ChunkerVanillaBlockType.COMPOSTER, BedrockLegacyStateGroups.COMPOSTER));
        register(BlockMapping.of("minecraft:conduit", ChunkerVanillaBlockType.CONDUIT));
        register(BlockMapping.of("minecraft:crafting_table", ChunkerVanillaBlockType.CRAFTING_TABLE));
        register(BlockMapping.of("minecraft:yellow_flower", ChunkerVanillaBlockType.DANDELION));

        // Daylight detector
        register(BlockMapping.of("minecraft:daylight_detector_inverted", ChunkerVanillaBlockType.DAYLIGHT_DETECTOR, BedrockLegacyStateGroups.DAYLIGHT_DETECTOR, VanillaBlockStates.INVERTED, Bool.TRUE));
        register(BlockMapping.of("minecraft:daylight_detector", ChunkerVanillaBlockType.DAYLIGHT_DETECTOR, BedrockLegacyStateGroups.DAYLIGHT_DETECTOR, VanillaBlockStates.INVERTED, Bool.FALSE));

        register(BlockMapping.of("minecraft:deadbush", ChunkerVanillaBlockType.DEAD_BUSH));

        // Empty Cauldron
        register(BlockMapping.of("minecraft:cauldron", "data", 0, ChunkerVanillaBlockType.CAULDRON));
        registerDuplicateOutput(BlockMapping.of("minecraft:cauldron", "data", 8, ChunkerVanillaBlockType.CAULDRON));

        // Cauldron
        register(BlockMapping.flatten("minecraft:cauldron", "data", VanillaBlockStates.CAULDRON_LEVEL,
                ImmutableMultimap.<Integer, Pair<CauldronLevel, ? extends ChunkerBlockType>>builder()
                        .put(1, Pair.of(CauldronLevel._1, ChunkerVanillaBlockType.WATER_CAULDRON))
                        .put(2, Pair.of(CauldronLevel._2, ChunkerVanillaBlockType.WATER_CAULDRON))
                        .put(3, Pair.of(CauldronLevel._3, ChunkerVanillaBlockType.WATER_CAULDRON))
                        .put(4, Pair.of(CauldronLevel._4, ChunkerVanillaBlockType.WATER_CAULDRON))
                        .put(5, Pair.of(CauldronLevel._5, ChunkerVanillaBlockType.WATER_CAULDRON))
                        .put(6, Pair.of(CauldronLevel._6, ChunkerVanillaBlockType.WATER_CAULDRON))

                        .put(9, Pair.of(CauldronLevel._1, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(10, Pair.of(CauldronLevel._2, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(11, Pair.of(CauldronLevel._3, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(12, Pair.of(CauldronLevel._4, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(13, Pair.of(CauldronLevel._5, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(14, Pair.of(CauldronLevel._6, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:cauldron", "data", 7, ChunkerVanillaBlockType.WATER_CAULDRON, VanillaBlockStates.CAULDRON_LEVEL, CauldronLevel._6));
        registerDuplicateOutput(BlockMapping.of("minecraft:cauldron", "data", 15, ChunkerVanillaBlockType.LAVA_CAULDRON, VanillaBlockStates.CAULDRON_LEVEL, CauldronLevel._6));

        // minecraft:lava_cauldron is removed in later game versions, so we avoid mapping to it
        registerDuplicateOutput(BlockMapping.flatten("minecraft:lava_cauldron", "data", VanillaBlockStates.CAULDRON_LEVEL,
                ImmutableMultimap.<Integer, Pair<CauldronLevel, ? extends ChunkerBlockType>>builder()
                        .put(1, Pair.of(CauldronLevel._1, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(2, Pair.of(CauldronLevel._2, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(3, Pair.of(CauldronLevel._3, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(4, Pair.of(CauldronLevel._4, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(5, Pair.of(CauldronLevel._5, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(6, Pair.of(CauldronLevel._6, ChunkerVanillaBlockType.LAVA_CAULDRON))

                        .put(9, Pair.of(CauldronLevel._1, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(10, Pair.of(CauldronLevel._2, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(11, Pair.of(CauldronLevel._3, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(12, Pair.of(CauldronLevel._4, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(13, Pair.of(CauldronLevel._5, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .put(14, Pair.of(CauldronLevel._6, ChunkerVanillaBlockType.LAVA_CAULDRON))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:lava_cauldron", "data", 0, ChunkerVanillaBlockType.CAULDRON));
        registerDuplicateOutput(BlockMapping.of("minecraft:lava_cauldron", "data", 8, ChunkerVanillaBlockType.CAULDRON));
        registerDuplicateOutput(BlockMapping.of("minecraft:lava_cauldron", "data", 7, ChunkerVanillaBlockType.LAVA_CAULDRON, VanillaBlockStates.CAULDRON_LEVEL, CauldronLevel._6));
        registerDuplicateOutput(BlockMapping.of("minecraft:lava_cauldron", "data", 15, ChunkerVanillaBlockType.LAVA_CAULDRON, VanillaBlockStates.CAULDRON_LEVEL, CauldronLevel._6));

        register(BlockMapping.of("minecraft:diamond_block", ChunkerVanillaBlockType.DIAMOND_BLOCK));
        register(BlockMapping.of("minecraft:diamond_ore", ChunkerVanillaBlockType.DIAMOND_ORE));

        // Dirt
        register(BlockMapping.flatten("minecraft:dirt", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.DIRT)
                        .put(1, ChunkerVanillaBlockType.COARSE_DIRT)
                        .build()
        ));

        // Fallback for dirt with no type
        registerDuplicateOutput(BlockMapping.of("minecraft:dirt", ChunkerVanillaBlockType.DIRT));

        register(BlockMapping.of("minecraft:grass_path", ChunkerVanillaBlockType.DIRT_PATH));
        register(BlockMapping.of("minecraft:dragon_egg", ChunkerVanillaBlockType.DRAGON_EGG));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:dropper", ChunkerVanillaBlockType.DROPPER)
                        .put("minecraft:dispenser", ChunkerVanillaBlockType.DISPENSER)
                        .build(),
                BedrockLegacyStateGroups.FACING_DIRECTION_TRIGGERED));
        register(BlockMapping.of("minecraft:dried_kelp_block", ChunkerVanillaBlockType.DRIED_KELP_BLOCK));
        register(BlockMapping.of("minecraft:emerald_block", ChunkerVanillaBlockType.EMERALD_BLOCK));
        register(BlockMapping.of("minecraft:emerald_ore", ChunkerVanillaBlockType.EMERALD_ORE));
        register(BlockMapping.of("minecraft:enchanting_table", ChunkerVanillaBlockType.ENCHANTING_TABLE));
        register(BlockMapping.of("minecraft:end_gateway", ChunkerVanillaBlockType.END_GATEWAY));
        register(BlockMapping.of("minecraft:end_portal", ChunkerVanillaBlockType.END_PORTAL));
        register(BlockMapping.of("minecraft:end_portal_frame", ChunkerVanillaBlockType.END_PORTAL_FRAME, BedrockLegacyStateGroups.END_PORTAL_FRAME));
        register(BlockMapping.of("minecraft:end_stone", ChunkerVanillaBlockType.END_STONE));
        register(BlockMapping.of("minecraft:end_bricks", ChunkerVanillaBlockType.END_STONE_BRICKS));
        register(BlockMapping.of("minecraft:farmland", ChunkerVanillaBlockType.FARMLAND, BedrockLegacyStateGroups.FARMLAND));
        register(BlockMapping.of("minecraft:fire", ChunkerVanillaBlockType.FIRE, BedrockLegacyStateGroups.FIRE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:golden_rail", ChunkerVanillaBlockType.POWERED_RAIL)
                        .put("minecraft:activator_rail", ChunkerVanillaBlockType.ACTIVATOR_RAIL)
                        .put("minecraft:detector_rail", ChunkerVanillaBlockType.DETECTOR_RAIL)
                        .build(),
                BedrockLegacyStateGroups.POWERED_RAIL));
        register(BlockMapping.of("minecraft:fletching_table", ChunkerVanillaBlockType.FLETCHING_TABLE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:dark_oak_fence_gate", ChunkerVanillaBlockType.DARK_OAK_FENCE_GATE)
                        .put("minecraft:fence_gate", ChunkerVanillaBlockType.OAK_FENCE_GATE)
                        .put("minecraft:acacia_fence_gate", ChunkerVanillaBlockType.ACACIA_FENCE_GATE)
                        .put("minecraft:jungle_fence_gate", ChunkerVanillaBlockType.JUNGLE_FENCE_GATE)
                        .put("minecraft:birch_fence_gate", ChunkerVanillaBlockType.BIRCH_FENCE_GATE)
                        .put("minecraft:spruce_fence_gate", ChunkerVanillaBlockType.SPRUCE_FENCE_GATE)
                        .build(),
                BedrockLegacyStateGroups.FENCE_GATE));
        register(BlockMapping.of("minecraft:glass", ChunkerVanillaBlockType.GLASS));
        register(BlockMapping.of("minecraft:glowstone", ChunkerVanillaBlockType.GLOWSTONE));
        register(BlockMapping.of("minecraft:gold_block", ChunkerVanillaBlockType.GOLD_BLOCK));
        register(BlockMapping.of("minecraft:gold_ore", ChunkerVanillaBlockType.GOLD_ORE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:wooden_door", ChunkerVanillaBlockType.OAK_DOOR)
                        .put("minecraft:dark_oak_door", ChunkerVanillaBlockType.DARK_OAK_DOOR)
                        .put("minecraft:acacia_door", ChunkerVanillaBlockType.ACACIA_DOOR)
                        .put("minecraft:iron_door", ChunkerVanillaBlockType.IRON_DOOR)
                        .put("minecraft:jungle_door", ChunkerVanillaBlockType.JUNGLE_DOOR)
                        .put("minecraft:spruce_door", ChunkerVanillaBlockType.SPRUCE_DOOR)
                        .put("minecraft:birch_door", ChunkerVanillaBlockType.BIRCH_DOOR)
                        .build(),
                BedrockLegacyStateGroups.DOOR));

        // Sapling
        register(BlockMapping.flatten("minecraft:sapling", "data", VanillaBlockStates.STAGE,
                ImmutableMultimap.<Integer, Pair<Stage, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(Stage._0, ChunkerVanillaBlockType.OAK_SAPLING))
                        .put(1, Pair.of(Stage._0, ChunkerVanillaBlockType.SPRUCE_SAPLING))
                        .put(2, Pair.of(Stage._0, ChunkerVanillaBlockType.BIRCH_SAPLING))
                        .put(3, Pair.of(Stage._0, ChunkerVanillaBlockType.JUNGLE_SAPLING))
                        .put(4, Pair.of(Stage._0, ChunkerVanillaBlockType.ACACIA_SAPLING))
                        .put(5, Pair.of(Stage._0, ChunkerVanillaBlockType.DARK_OAK_SAPLING))
                        .put(8, Pair.of(Stage._1, ChunkerVanillaBlockType.OAK_SAPLING))
                        .put(9, Pair.of(Stage._1, ChunkerVanillaBlockType.SPRUCE_SAPLING))
                        .put(10, Pair.of(Stage._1, ChunkerVanillaBlockType.BIRCH_SAPLING))
                        .put(11, Pair.of(Stage._1, ChunkerVanillaBlockType.JUNGLE_SAPLING))
                        .put(12, Pair.of(Stage._1, ChunkerVanillaBlockType.ACACIA_SAPLING))
                        .put(13, Pair.of(Stage._1, ChunkerVanillaBlockType.DARK_OAK_SAPLING))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:sapling", "data", VanillaBlockStates.STAGE,
                ImmutableMultimap.<Integer, Pair<Stage, ? extends ChunkerBlockType>>builder()
                        .put(6, Pair.of(Stage._0, ChunkerVanillaBlockType.OAK_SAPLING))
                        .put(7, Pair.of(Stage._0, ChunkerVanillaBlockType.OAK_SAPLING))
                        .put(14, Pair.of(Stage._1, ChunkerVanillaBlockType.OAK_SAPLING))
                        .put(15, Pair.of(Stage._1, ChunkerVanillaBlockType.OAK_SAPLING))
                        .build()
        ));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:birch_standing_sign", ChunkerVanillaBlockType.BIRCH_SIGN)
                        .put("minecraft:acacia_standing_sign", ChunkerVanillaBlockType.ACACIA_SIGN)
                        .put("minecraft:spruce_standing_sign", ChunkerVanillaBlockType.SPRUCE_SIGN)
                        .put("minecraft:jungle_standing_sign", ChunkerVanillaBlockType.JUNGLE_SIGN)
                        .put("minecraft:darkoak_standing_sign", ChunkerVanillaBlockType.DARK_OAK_SIGN)
                        .put("minecraft:standing_sign", ChunkerVanillaBlockType.OAK_SIGN)
                        .build(),
                BedrockLegacyStateGroups.SIGN));
        register(BlockMapping.of("minecraft:gravel", ChunkerVanillaBlockType.GRAVEL));

        // Trapdoors
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:birch_trapdoor", ChunkerVanillaBlockType.BIRCH_TRAPDOOR)
                        .put("minecraft:dark_oak_trapdoor", ChunkerVanillaBlockType.DARK_OAK_TRAPDOOR)
                        .put("minecraft:jungle_trapdoor", ChunkerVanillaBlockType.JUNGLE_TRAPDOOR)
                        .put("minecraft:spruce_trapdoor", ChunkerVanillaBlockType.SPRUCE_TRAPDOOR)
                        .put("minecraft:iron_trapdoor", ChunkerVanillaBlockType.IRON_TRAPDOOR)
                        .put("minecraft:trapdoor", ChunkerVanillaBlockType.OAK_TRAPDOOR)
                        .put("minecraft:acacia_trapdoor", ChunkerVanillaBlockType.ACACIA_TRAPDOOR)
                        .build(),
                BedrockLegacyStateGroups.TRAPDOOR));

        // Command Blocks
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:repeating_command_block", ChunkerVanillaBlockType.REPEATING_COMMAND_BLOCK)
                        .put("minecraft:chain_command_block", ChunkerVanillaBlockType.CHAIN_COMMAND_BLOCK)
                        .put("minecraft:command_block", ChunkerVanillaBlockType.COMMAND_BLOCK)
                        .build(),
                BedrockLegacyStateGroups.COMMAND_BLOCK));

        // Pumpkins
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:carved_pumpkin", ChunkerVanillaBlockType.CARVED_PUMPKIN)
                        .put("minecraft:lit_pumpkin", ChunkerVanillaBlockType.JACK_O_LANTERN)
                        .build(),
                BedrockLegacyStateGroups.PUMPKIN));

        register(BlockMapping.of("minecraft:ender_chest", ChunkerVanillaBlockType.ENDER_CHEST, BedrockLegacyStateGroups.ENDER_CHEST));
        register(BlockMapping.of("minecraft:grindstone", ChunkerVanillaBlockType.GRINDSTONE, BedrockLegacyStateGroups.GRINDSTONE));
        register(BlockMapping.of("minecraft:hopper", ChunkerVanillaBlockType.HOPPER, BedrockLegacyStateGroups.HOPPER));
        register(BlockMapping.of("minecraft:stonecutter_block", ChunkerVanillaBlockType.STONECUTTER, BedrockLegacyStateGroups.STONECUTTER));


        register(BlockMapping.of("minecraft:ice", ChunkerVanillaBlockType.ICE));

        // Monster egg
        register(BlockMapping.flatten("minecraft:monster_egg", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.INFESTED_STONE)
                        .put(2, ChunkerVanillaBlockType.INFESTED_STONE_BRICKS)
                        .put(1, ChunkerVanillaBlockType.INFESTED_COBBLESTONE)
                        .put(3, ChunkerVanillaBlockType.INFESTED_MOSSY_STONE_BRICKS)
                        .put(4, ChunkerVanillaBlockType.INFESTED_CRACKED_STONE_BRICKS)
                        .put(5, ChunkerVanillaBlockType.INFESTED_CHISELED_STONE_BRICKS)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:monster_egg", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(6, ChunkerVanillaBlockType.INFESTED_STONE)
                        .put(7, ChunkerVanillaBlockType.INFESTED_STONE)
                        .build()
        ));

        register(BlockMapping.of("minecraft:iron_block", ChunkerVanillaBlockType.IRON_BLOCK));
        register(BlockMapping.of("minecraft:iron_ore", ChunkerVanillaBlockType.IRON_ORE));
        register(BlockMapping.of("minecraft:jigsaw", ChunkerVanillaBlockType.JIGSAW, BedrockLegacyStateGroups.JIGSAW));
        register(BlockMapping.of("minecraft:jukebox", ChunkerVanillaBlockType.JUKEBOX, BedrockLegacyStateGroups.JUKEBOX));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:lit_smoker", ChunkerVanillaBlockType.SMOKER)
                        .put("minecraft:lit_furnace", ChunkerVanillaBlockType.FURNACE)
                        .put("minecraft:lit_blast_furnace", ChunkerVanillaBlockType.BLAST_FURNACE)
                        .build(),
                BedrockLegacyStateGroups.CARDINAL_DIRECTION, VanillaBlockStates.LIT, Bool.TRUE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:smoker", ChunkerVanillaBlockType.SMOKER)
                        .put("minecraft:blast_furnace", ChunkerVanillaBlockType.BLAST_FURNACE)
                        .put("minecraft:furnace", ChunkerVanillaBlockType.FURNACE)
                        .build(),
                BedrockLegacyStateGroups.CARDINAL_DIRECTION, VanillaBlockStates.LIT, Bool.FALSE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:light_weighted_pressure_plate", ChunkerVanillaBlockType.LIGHT_WEIGHTED_PRESSURE_PLATE)
                        .put("minecraft:heavy_weighted_pressure_plate", ChunkerVanillaBlockType.HEAVY_WEIGHTED_PRESSURE_PLATE)
                        .build(),
                BedrockLegacyStateGroups.WEIGHTED_PRESSURE_PLATE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:wooden_button", ChunkerVanillaBlockType.OAK_BUTTON)
                        .put("minecraft:jungle_button", ChunkerVanillaBlockType.JUNGLE_BUTTON)
                        .put("minecraft:dark_oak_button", ChunkerVanillaBlockType.DARK_OAK_BUTTON)
                        .put("minecraft:acacia_button", ChunkerVanillaBlockType.ACACIA_BUTTON)
                        .put("minecraft:spruce_button", ChunkerVanillaBlockType.SPRUCE_BUTTON)
                        .put("minecraft:stone_button", ChunkerVanillaBlockType.STONE_BUTTON)
                        .put("minecraft:birch_button", ChunkerVanillaBlockType.BIRCH_BUTTON)
                        .build(),
                BedrockLegacyStateGroups.BUTTON));
        register(BlockMapping.of("minecraft:kelp", ChunkerVanillaBlockType.KELP, BedrockLegacyStateGroups.KELP));
        register(BlockMapping.of("minecraft:kelp", "age", 15, ChunkerVanillaBlockType.KELP_PLANT));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:nether_brick_fence", ChunkerVanillaBlockType.NETHER_BRICK_FENCE)
                        .put("minecraft:glass_pane", ChunkerVanillaBlockType.GLASS_PANE)
                        .put("minecraft:iron_bars", ChunkerVanillaBlockType.IRON_BARS)
                        .build(),
                BedrockLegacyStateGroups.CONNECTABLE_HORIZONTAL));
        register(BlockMapping.of("minecraft:ladder", ChunkerVanillaBlockType.LADDER, BedrockLegacyStateGroups.FACING_DIRECTION_HORIZONTAL));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:jungle_pressure_plate", ChunkerVanillaBlockType.JUNGLE_PRESSURE_PLATE)
                        .put("minecraft:acacia_pressure_plate", ChunkerVanillaBlockType.ACACIA_PRESSURE_PLATE)
                        .put("minecraft:birch_pressure_plate", ChunkerVanillaBlockType.BIRCH_PRESSURE_PLATE)
                        .put("minecraft:wooden_pressure_plate", ChunkerVanillaBlockType.OAK_PRESSURE_PLATE)
                        .put("minecraft:spruce_pressure_plate", ChunkerVanillaBlockType.SPRUCE_PRESSURE_PLATE)
                        .put("minecraft:stone_pressure_plate", ChunkerVanillaBlockType.STONE_PRESSURE_PLATE)
                        .put("minecraft:dark_oak_pressure_plate", ChunkerVanillaBlockType.DARK_OAK_PRESSURE_PLATE)
                        .build(),
                BedrockLegacyStateGroups.PRESSURE_PLATE));
        register(BlockMapping.of("minecraft:lantern", ChunkerVanillaBlockType.LANTERN, BedrockLegacyStateGroups.LANTERN));
        register(BlockMapping.of("minecraft:lapis_block", ChunkerVanillaBlockType.LAPIS_BLOCK));
        register(BlockMapping.of("minecraft:lapis_ore", ChunkerVanillaBlockType.LAPIS_ORE));
        register(BlockMapping.of("minecraft:lectern", ChunkerVanillaBlockType.LECTERN, BedrockLegacyStateGroups.LECTERN));
        register(BlockMapping.of("minecraft:lever", ChunkerVanillaBlockType.LEVER, BedrockLegacyStateGroups.LEVER));

        // Stone brick
        register(BlockMapping.flatten("minecraft:stonebrick", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.STONE_BRICKS)
                        .put(1, ChunkerVanillaBlockType.MOSSY_STONE_BRICKS)
                        .put(2, ChunkerVanillaBlockType.CRACKED_STONE_BRICKS)
                        .put(3, ChunkerVanillaBlockType.CHISELED_STONE_BRICKS)
                        .build()
        ));
        // Smooth is the same texture as the default
        // Also there are empty states from 5-7 so map them all to stonebrick
        registerDuplicateOutput(BlockMapping.of("minecraft:stonebrick", ChunkerVanillaBlockType.STONE_BRICKS));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:frosted_ice", ChunkerVanillaBlockType.FROSTED_ICE)
                        .put("minecraft:nether_wart", ChunkerVanillaBlockType.NETHER_WART)
                        .build(),
                BedrockLegacyStateGroups.AGE_3));

        // Flowers
        register(BlockMapping.flatten("minecraft:red_flower", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.POPPY)
                        .put(1, ChunkerVanillaBlockType.BLUE_ORCHID)
                        .put(2, ChunkerVanillaBlockType.ALLIUM)
                        .put(3, ChunkerVanillaBlockType.AZURE_BLUET)
                        .put(4, ChunkerVanillaBlockType.RED_TULIP)
                        .put(5, ChunkerVanillaBlockType.ORANGE_TULIP)
                        .put(6, ChunkerVanillaBlockType.WHITE_TULIP)
                        .put(7, ChunkerVanillaBlockType.PINK_TULIP)
                        .put(8, ChunkerVanillaBlockType.OXEYE_DAISY)
                        .put(9, ChunkerVanillaBlockType.CORNFLOWER)
                        .put(10, ChunkerVanillaBlockType.LILY_OF_THE_VALLEY)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:red_flower", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(11, ChunkerVanillaBlockType.POPPY)
                        .put(12, ChunkerVanillaBlockType.POPPY)
                        .put(13, ChunkerVanillaBlockType.POPPY)
                        .put(14, ChunkerVanillaBlockType.POPPY)
                        .put(15, ChunkerVanillaBlockType.POPPY)
                        .build()
        ));

        register(BlockMapping.of("minecraft:waterlily", ChunkerVanillaBlockType.LILY_PAD));

        // Leaves
        register(BlockMapping.flatten("minecraft:leaves", "data", List.of(VanillaBlockStates.PERSISTENT, VanillaBlockStates.UPDATE),
                ImmutableMultimap.<Integer, Pair<List<BlockStateValue>, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(List.of(Bool.FALSE, Bool.FALSE), ChunkerVanillaBlockType.OAK_LEAVES))
                        .put(1, Pair.of(List.of(Bool.FALSE, Bool.FALSE), ChunkerVanillaBlockType.SPRUCE_LEAVES))
                        .put(2, Pair.of(List.of(Bool.FALSE, Bool.FALSE), ChunkerVanillaBlockType.BIRCH_LEAVES))
                        .put(3, Pair.of(List.of(Bool.FALSE, Bool.FALSE), ChunkerVanillaBlockType.JUNGLE_LEAVES))
                        .put(4, Pair.of(List.of(Bool.FALSE, Bool.TRUE), ChunkerVanillaBlockType.OAK_LEAVES))
                        .put(5, Pair.of(List.of(Bool.FALSE, Bool.TRUE), ChunkerVanillaBlockType.SPRUCE_LEAVES))
                        .put(6, Pair.of(List.of(Bool.FALSE, Bool.TRUE), ChunkerVanillaBlockType.BIRCH_LEAVES))
                        .put(7, Pair.of(List.of(Bool.FALSE, Bool.TRUE), ChunkerVanillaBlockType.JUNGLE_LEAVES))
                        .put(8, Pair.of(List.of(Bool.TRUE, Bool.FALSE), ChunkerVanillaBlockType.OAK_LEAVES))
                        .put(9, Pair.of(List.of(Bool.TRUE, Bool.FALSE), ChunkerVanillaBlockType.SPRUCE_LEAVES))
                        .put(10, Pair.of(List.of(Bool.TRUE, Bool.FALSE), ChunkerVanillaBlockType.BIRCH_LEAVES))
                        .put(11, Pair.of(List.of(Bool.TRUE, Bool.FALSE), ChunkerVanillaBlockType.JUNGLE_LEAVES))
                        .put(12, Pair.of(List.of(Bool.TRUE, Bool.TRUE), ChunkerVanillaBlockType.OAK_LEAVES))
                        .put(13, Pair.of(List.of(Bool.TRUE, Bool.TRUE), ChunkerVanillaBlockType.SPRUCE_LEAVES))
                        .put(14, Pair.of(List.of(Bool.TRUE, Bool.TRUE), ChunkerVanillaBlockType.BIRCH_LEAVES))
                        .put(15, Pair.of(List.of(Bool.TRUE, Bool.TRUE), ChunkerVanillaBlockType.JUNGLE_LEAVES))
                        .build(),
                BedrockLegacyStateGroups.LEAVES
        ));

        // Leaves 2
        register(BlockMapping.flatten("minecraft:leaves2", "data", List.of(VanillaBlockStates.PERSISTENT, VanillaBlockStates.UPDATE),
                ImmutableMultimap.<Integer, Pair<List<BlockStateValue>, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(List.of(Bool.FALSE, Bool.FALSE), ChunkerVanillaBlockType.ACACIA_LEAVES))
                        .put(1, Pair.of(List.of(Bool.FALSE, Bool.FALSE), ChunkerVanillaBlockType.DARK_OAK_LEAVES))
                        .put(4, Pair.of(List.of(Bool.FALSE, Bool.TRUE), ChunkerVanillaBlockType.ACACIA_LEAVES))
                        .put(5, Pair.of(List.of(Bool.FALSE, Bool.TRUE), ChunkerVanillaBlockType.DARK_OAK_LEAVES))
                        .put(8, Pair.of(List.of(Bool.TRUE, Bool.FALSE), ChunkerVanillaBlockType.ACACIA_LEAVES))
                        .put(9, Pair.of(List.of(Bool.TRUE, Bool.FALSE), ChunkerVanillaBlockType.DARK_OAK_LEAVES))
                        .put(12, Pair.of(List.of(Bool.TRUE, Bool.TRUE), ChunkerVanillaBlockType.ACACIA_LEAVES))
                        .put(13, Pair.of(List.of(Bool.TRUE, Bool.TRUE), ChunkerVanillaBlockType.DARK_OAK_LEAVES))
                        .build(),
                BedrockLegacyStateGroups.LEAVES
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:leaves2", "data", List.of(VanillaBlockStates.PERSISTENT, VanillaBlockStates.UPDATE),
                ImmutableMultimap.<Integer, Pair<List<BlockStateValue>, ? extends ChunkerBlockType>>builder()
                        .put(2, Pair.of(List.of(Bool.FALSE, Bool.FALSE), ChunkerVanillaBlockType.ACACIA_LEAVES))
                        .put(3, Pair.of(List.of(Bool.FALSE, Bool.FALSE), ChunkerVanillaBlockType.DARK_OAK_LEAVES))
                        .put(6, Pair.of(List.of(Bool.FALSE, Bool.TRUE), ChunkerVanillaBlockType.ACACIA_LEAVES))
                        .put(7, Pair.of(List.of(Bool.FALSE, Bool.TRUE), ChunkerVanillaBlockType.DARK_OAK_LEAVES))
                        .put(10, Pair.of(List.of(Bool.TRUE, Bool.FALSE), ChunkerVanillaBlockType.ACACIA_LEAVES))
                        .put(11, Pair.of(List.of(Bool.TRUE, Bool.FALSE), ChunkerVanillaBlockType.DARK_OAK_LEAVES))
                        .put(14, Pair.of(List.of(Bool.TRUE, Bool.TRUE), ChunkerVanillaBlockType.ACACIA_LEAVES))
                        .put(15, Pair.of(List.of(Bool.TRUE, Bool.TRUE), ChunkerVanillaBlockType.DARK_OAK_LEAVES))
                        .build(),
                BedrockLegacyStateGroups.LEAVES
        ));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:beetroot", ChunkerVanillaBlockType.BEETROOTS)
                        .put("minecraft:sweet_berry_bush", ChunkerVanillaBlockType.SWEET_BERRY_BUSH)
                        .build(),
                BedrockLegacyStateGroups.GROWTH));
        register(BlockMapping.of("minecraft:loom", ChunkerVanillaBlockType.LOOM, BedrockLegacyStateGroups.LOOM));

        // Standing banners
        register(BlockMapping.of("minecraft:standing_banner", ChunkerVanillaBlockType.WHITE_BANNER, BedrockLegacyStateGroups.BANNER));
        registerDuplicateInput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.ORANGE_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.BROWN_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.BLUE_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.CYAN_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.GREEN_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.RED_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.YELLOW_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.MAGENTA_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.GRAY_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.PINK_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.BLACK_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.LIGHT_BLUE_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.LIGHT_GRAY_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.LIME_BANNER)
                        .put("minecraft:standing_banner", ChunkerVanillaBlockType.PURPLE_BANNER)
                        .build(),
                BedrockLegacyStateGroups.BANNER));

        // Wall banners
        register(BlockMapping.of("minecraft:wall_banner", ChunkerVanillaBlockType.WHITE_WALL_BANNER, BedrockLegacyStateGroups.FACING_DIRECTION_HORIZONTAL));
        registerDuplicateInput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.MAGENTA_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.BROWN_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.BLUE_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.GRAY_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.PINK_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.ORANGE_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.LIGHT_GRAY_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.YELLOW_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.CYAN_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.LIGHT_BLUE_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.RED_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.PURPLE_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.BLACK_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.GREEN_WALL_BANNER)
                        .put("minecraft:wall_banner", ChunkerVanillaBlockType.LIME_WALL_BANNER)
                        .build(),
                BedrockLegacyStateGroups.FACING_DIRECTION_HORIZONTAL));

        // Beds
        register(BlockMapping.of("minecraft:bed", ChunkerVanillaBlockType.WHITE_BED, BedrockLegacyStateGroups.BED));
        registerDuplicateInput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:bed", ChunkerVanillaBlockType.BROWN_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.BLACK_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.CYAN_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.LIGHT_BLUE_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.RED_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.ORANGE_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.MAGENTA_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.BLUE_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.GREEN_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.GRAY_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.PINK_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.YELLOW_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.LIGHT_GRAY_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.PURPLE_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.LIME_BED)
                        .build(),
                BedrockLegacyStateGroups.BED));

        // Walls
        register(BlockMapping.flatten("minecraft:cobblestone_wall", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.COBBLESTONE_WALL)
                        .put(1, ChunkerVanillaBlockType.MOSSY_COBBLESTONE_WALL)
                        .put(2, ChunkerVanillaBlockType.GRANITE_WALL)
                        .put(3, ChunkerVanillaBlockType.DIORITE_WALL)
                        .put(4, ChunkerVanillaBlockType.ANDESITE_WALL)
                        .put(5, ChunkerVanillaBlockType.SANDSTONE_WALL)
                        .put(6, ChunkerVanillaBlockType.BRICK_WALL)
                        .put(7, ChunkerVanillaBlockType.STONE_BRICK_WALL)
                        .put(8, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_WALL)
                        .put(9, ChunkerVanillaBlockType.NETHER_BRICK_WALL)
                        .put(10, ChunkerVanillaBlockType.END_STONE_BRICK_WALL)
                        .put(11, ChunkerVanillaBlockType.PRISMARINE_WALL)
                        .put(12, ChunkerVanillaBlockType.RED_SANDSTONE_WALL)
                        .put(13, ChunkerVanillaBlockType.RED_NETHER_BRICK_WALL)
                        .build(),
                BedrockLegacyStateGroups.WALL));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:cobblestone_wall", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(14, ChunkerVanillaBlockType.COBBLESTONE_WALL)
                        .put(15, ChunkerVanillaBlockType.COBBLESTONE_WALL)
                        .build(),
                BedrockLegacyStateGroups.WALL));

        register(BlockMapping.of("minecraft:magma", ChunkerVanillaBlockType.MAGMA_BLOCK));

        // Sandstone
        register(BlockMapping.flatten("minecraft:sandstone", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.SANDSTONE)
                        .put(1, ChunkerVanillaBlockType.CHISELED_SANDSTONE)
                        .put(2, ChunkerVanillaBlockType.CUT_SANDSTONE)
                        .put(3, ChunkerVanillaBlockType.SMOOTH_SANDSTONE)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:sandstone", ChunkerVanillaBlockType.SANDSTONE));

        // Red Sandstone
        register(BlockMapping.flatten("minecraft:red_sandstone", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.RED_SANDSTONE)
                        .put(1, ChunkerVanillaBlockType.CHISELED_RED_SANDSTONE)
                        .put(2, ChunkerVanillaBlockType.CUT_RED_SANDSTONE)
                        .put(3, ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:red_sandstone", ChunkerVanillaBlockType.RED_SANDSTONE));

        // Sand
        register(BlockMapping.flatten("minecraft:sand", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.SAND)
                        .put(1, ChunkerVanillaBlockType.RED_SAND)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:sand", ChunkerVanillaBlockType.SAND));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:wall_sign", ChunkerVanillaBlockType.OAK_WALL_SIGN)
                        .put("minecraft:darkoak_wall_sign", ChunkerVanillaBlockType.DARK_OAK_WALL_SIGN)
                        .put("minecraft:acacia_wall_sign", ChunkerVanillaBlockType.ACACIA_WALL_SIGN)
                        .put("minecraft:spruce_wall_sign", ChunkerVanillaBlockType.SPRUCE_WALL_SIGN)
                        .put("minecraft:birch_wall_sign", ChunkerVanillaBlockType.BIRCH_WALL_SIGN)
                        .put("minecraft:jungle_wall_sign", ChunkerVanillaBlockType.JUNGLE_WALL_SIGN)
                        .build(),
                BedrockLegacyStateGroups.FACING_DIRECTION_HORIZONTAL));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:silver_glazed_terracotta", ChunkerVanillaBlockType.LIGHT_GRAY_GLAZED_TERRACOTTA)
                        .put("minecraft:orange_glazed_terracotta", ChunkerVanillaBlockType.ORANGE_GLAZED_TERRACOTTA)
                        .put("minecraft:cyan_glazed_terracotta", ChunkerVanillaBlockType.CYAN_GLAZED_TERRACOTTA)
                        .put("minecraft:purple_glazed_terracotta", ChunkerVanillaBlockType.PURPLE_GLAZED_TERRACOTTA)
                        .put("minecraft:light_blue_glazed_terracotta", ChunkerVanillaBlockType.LIGHT_BLUE_GLAZED_TERRACOTTA)
                        .put("minecraft:black_glazed_terracotta", ChunkerVanillaBlockType.BLACK_GLAZED_TERRACOTTA)
                        .put("minecraft:blue_glazed_terracotta", ChunkerVanillaBlockType.BLUE_GLAZED_TERRACOTTA)
                        .put("minecraft:white_glazed_terracotta", ChunkerVanillaBlockType.WHITE_GLAZED_TERRACOTTA)
                        .put("minecraft:yellow_glazed_terracotta", ChunkerVanillaBlockType.YELLOW_GLAZED_TERRACOTTA)
                        .put("minecraft:gray_glazed_terracotta", ChunkerVanillaBlockType.GRAY_GLAZED_TERRACOTTA)
                        .put("minecraft:brown_glazed_terracotta", ChunkerVanillaBlockType.BROWN_GLAZED_TERRACOTTA)
                        .put("minecraft:magenta_glazed_terracotta", ChunkerVanillaBlockType.MAGENTA_GLAZED_TERRACOTTA)
                        .put("minecraft:pink_glazed_terracotta", ChunkerVanillaBlockType.PINK_GLAZED_TERRACOTTA)
                        .put("minecraft:red_glazed_terracotta", ChunkerVanillaBlockType.RED_GLAZED_TERRACOTTA)
                        .put("minecraft:green_glazed_terracotta", ChunkerVanillaBlockType.GREEN_GLAZED_TERRACOTTA)
                        .put("minecraft:lime_glazed_terracotta", ChunkerVanillaBlockType.LIME_GLAZED_TERRACOTTA)
                        .build(),
                BedrockLegacyStateGroups.FACING_DIRECTION_HORIZONTAL));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:grass", ChunkerVanillaBlockType.GRASS_BLOCK)
                        .put("minecraft:podzol", ChunkerVanillaBlockType.PODZOL)
                        .put("minecraft:mycelium", ChunkerVanillaBlockType.MYCELIUM)
                        .build(),
                BedrockLegacyStateGroups.SNOWY_BLOCK));
        register(BlockMapping.of("minecraft:melon_block", ChunkerVanillaBlockType.MELON));
        register(BlockMapping.of("minecraft:mossy_cobblestone", ChunkerVanillaBlockType.MOSSY_COBBLESTONE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:cactus", ChunkerVanillaBlockType.CACTUS)
                        .put("minecraft:reeds", ChunkerVanillaBlockType.SUGAR_CANE)
                        .build(),
                BedrockLegacyStateGroups.VERTICAL_GROWING));

        // Coral block
        register(BlockMapping.flatten("minecraft:coral_block", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.TUBE_CORAL_BLOCK)
                        .put(1, ChunkerVanillaBlockType.BRAIN_CORAL_BLOCK)
                        .put(2, ChunkerVanillaBlockType.BUBBLE_CORAL_BLOCK)
                        .put(3, ChunkerVanillaBlockType.FIRE_CORAL_BLOCK)
                        .put(4, ChunkerVanillaBlockType.HORN_CORAL_BLOCK)
                        .put(8, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_BLOCK)
                        .put(9, ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_BLOCK)
                        .put(10, ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_BLOCK)
                        .put(11, ChunkerVanillaBlockType.DEAD_FIRE_CORAL_BLOCK)
                        .put(12, ChunkerVanillaBlockType.DEAD_HORN_CORAL_BLOCK)
                        .build()
        ));

        // Fallback for other states
        registerDuplicateOutput(BlockMapping.flatten("minecraft:coral_block", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(5, ChunkerVanillaBlockType.TUBE_CORAL_BLOCK)
                        .put(6, ChunkerVanillaBlockType.TUBE_CORAL_BLOCK)
                        .put(7, ChunkerVanillaBlockType.TUBE_CORAL_BLOCK)
                        .put(13, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_BLOCK)
                        .put(14, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_BLOCK)
                        .put(15, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_BLOCK)
                        .build()
        ));

        // Coral
        register(BlockMapping.flatten("minecraft:coral", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.TUBE_CORAL)
                        .put(1, ChunkerVanillaBlockType.BRAIN_CORAL)
                        .put(2, ChunkerVanillaBlockType.BUBBLE_CORAL)
                        .put(3, ChunkerVanillaBlockType.FIRE_CORAL)
                        .put(4, ChunkerVanillaBlockType.HORN_CORAL)
                        .build()
        ));

        // Fallback for other states
        registerDuplicateOutput(BlockMapping.of("minecraft:coral", ChunkerVanillaBlockType.TUBE_CORAL));

        // Coral fan
        register(BlockMapping.flatten("minecraft:coral_fan", "data", VanillaBlockStates.CORAL_FAN_DIRECTION,
                ImmutableMultimap.<Integer, Pair<CoralFanDirection, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.TUBE_CORAL_FAN))
                        .put(1, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.BRAIN_CORAL_FAN))
                        .put(2, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.BUBBLE_CORAL_FAN))
                        .put(3, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.FIRE_CORAL_FAN))
                        .put(4, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.HORN_CORAL_FAN))
                        .put(8, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.TUBE_CORAL_FAN))
                        .put(9, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.BRAIN_CORAL_FAN))
                        .put(10, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.BUBBLE_CORAL_FAN))
                        .put(11, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.FIRE_CORAL_FAN))
                        .put(12, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.HORN_CORAL_FAN))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:coral_fan", "data", VanillaBlockStates.CORAL_FAN_DIRECTION,
                ImmutableMultimap.<Integer, Pair<CoralFanDirection, ? extends ChunkerBlockType>>builder()
                        .put(5, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.TUBE_CORAL_FAN))
                        .put(6, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.TUBE_CORAL_FAN))
                        .put(7, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.TUBE_CORAL_FAN))
                        .put(13, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.TUBE_CORAL_FAN))
                        .put(14, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.TUBE_CORAL_FAN))
                        .put(15, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.TUBE_CORAL_FAN))
                        .build()
        ));

        // Dead coral fan
        register(BlockMapping.flatten("minecraft:coral_fan_dead", "data", VanillaBlockStates.CORAL_FAN_DIRECTION,
                ImmutableMultimap.<Integer, Pair<CoralFanDirection, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN))
                        .put(1, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_FAN))
                        .put(2, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_FAN))
                        .put(3, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.DEAD_FIRE_CORAL_FAN))
                        .put(4, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.DEAD_HORN_CORAL_FAN))
                        .put(8, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN))
                        .put(9, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_FAN))
                        .put(10, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_FAN))
                        .put(11, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.DEAD_FIRE_CORAL_FAN))
                        .put(12, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.DEAD_HORN_CORAL_FAN))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:coral_fan_dead", "data", VanillaBlockStates.CORAL_FAN_DIRECTION,
                ImmutableMultimap.<Integer, Pair<CoralFanDirection, ? extends ChunkerBlockType>>builder()
                        .put(5, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN))
                        .put(6, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN))
                        .put(7, Pair.of(CoralFanDirection.EAST_WEST, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN))
                        .put(13, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN))
                        .put(14, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN))
                        .put(15, Pair.of(CoralFanDirection.NORTH_SOUTH, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN))
                        .build()
        ));

        // Coral hanging
        register(BlockMapping.flatten("minecraft:coral_fan_hang", "data", VanillaBlockStates.FACING_HORIZONTAL,
                ImmutableMultimap.<Integer, Pair<FacingDirectionHorizontal, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.TUBE_CORAL_WALL_FAN))
                        .put(1, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.BRAIN_CORAL_WALL_FAN))
                        .put(2, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_WALL_FAN))
                        .put(3, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_WALL_FAN))
                        .put(4, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.TUBE_CORAL_WALL_FAN))
                        .put(5, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.BRAIN_CORAL_WALL_FAN))
                        .put(6, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_WALL_FAN))
                        .put(7, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_WALL_FAN))
                        .put(8, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.TUBE_CORAL_WALL_FAN))
                        .put(9, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.BRAIN_CORAL_WALL_FAN))
                        .put(10, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_WALL_FAN))
                        .put(11, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_WALL_FAN))
                        .put(12, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.TUBE_CORAL_WALL_FAN))
                        .put(13, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.BRAIN_CORAL_WALL_FAN))
                        .put(14, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_WALL_FAN))
                        .put(15, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_WALL_FAN))
                        .build()
        ));
        register(BlockMapping.flatten("minecraft:coral_fan_hang2", "data", VanillaBlockStates.FACING_HORIZONTAL,
                ImmutableMultimap.<Integer, Pair<FacingDirectionHorizontal, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.BUBBLE_CORAL_WALL_FAN))
                        .put(1, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.FIRE_CORAL_WALL_FAN))
                        .put(2, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_WALL_FAN))
                        .put(3, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.DEAD_FIRE_CORAL_WALL_FAN))
                        .put(4, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.BUBBLE_CORAL_WALL_FAN))
                        .put(5, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.FIRE_CORAL_WALL_FAN))
                        .put(6, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_WALL_FAN))
                        .put(7, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.DEAD_FIRE_CORAL_WALL_FAN))
                        .put(8, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.BUBBLE_CORAL_WALL_FAN))
                        .put(9, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.FIRE_CORAL_WALL_FAN))
                        .put(10, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_WALL_FAN))
                        .put(11, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.DEAD_FIRE_CORAL_WALL_FAN))
                        .put(12, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.BUBBLE_CORAL_WALL_FAN))
                        .put(13, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.FIRE_CORAL_WALL_FAN))
                        .put(14, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_WALL_FAN))
                        .put(15, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.DEAD_FIRE_CORAL_WALL_FAN))
                        .build()
        ));
        register(BlockMapping.flatten("minecraft:coral_fan_hang3", "data", VanillaBlockStates.FACING_HORIZONTAL,
                ImmutableMultimap.<Integer, Pair<FacingDirectionHorizontal, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN))
                        .put(2, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN))
                        .put(4, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN))
                        .put(6, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN))
                        .put(8, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN))
                        .put(10, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN))
                        .put(12, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN))
                        .put(14, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:coral_fan_hang3", "data", VanillaBlockStates.FACING_HORIZONTAL,
                ImmutableMultimap.<Integer, Pair<FacingDirectionHorizontal, ? extends ChunkerBlockType>>builder()
                        .put(1, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN))
                        .put(3, Pair.of(FacingDirectionHorizontal.WEST, ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN))
                        .put(5, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN))
                        .put(7, Pair.of(FacingDirectionHorizontal.EAST, ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN))
                        .put(9, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN))
                        .put(11, Pair.of(FacingDirectionHorizontal.NORTH, ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN))
                        .put(13, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN))
                        .put(15, Pair.of(FacingDirectionHorizontal.SOUTH, ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN))
                        .build()
        ));

        // Mushroom / Mushroom Stem
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:brown_mushroom_block", ChunkerVanillaBlockType.BROWN_MUSHROOM_BLOCK)
                        .put("minecraft:red_mushroom_block", ChunkerVanillaBlockType.RED_MUSHROOM_BLOCK)
                        .build(),
                BedrockLegacyStateGroups.MUSHROOM_BLOCK));
        register(BlockMapping.of("minecraft:brown_mushroom_block", "data", 15, ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                VanillaBlockStates.NORTH, Bool.TRUE,
                VanillaBlockStates.EAST, Bool.TRUE,
                VanillaBlockStates.SOUTH, Bool.TRUE,
                VanillaBlockStates.WEST, Bool.TRUE,
                VanillaBlockStates.UP, Bool.TRUE,
                VanillaBlockStates.DOWN, Bool.TRUE
        )));
        register(BlockMapping.of("minecraft:brown_mushroom_block", "data", 10, ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                VanillaBlockStates.NORTH, Bool.TRUE,
                VanillaBlockStates.EAST, Bool.TRUE,
                VanillaBlockStates.SOUTH, Bool.TRUE,
                VanillaBlockStates.WEST, Bool.TRUE,
                VanillaBlockStates.UP, Bool.FALSE,
                VanillaBlockStates.DOWN, Bool.FALSE
        )));
        registerDuplicateOutput(BlockMapping.of("minecraft:red_mushroom_block", "data", 15, ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                VanillaBlockStates.NORTH, Bool.TRUE,
                VanillaBlockStates.EAST, Bool.TRUE,
                VanillaBlockStates.SOUTH, Bool.TRUE,
                VanillaBlockStates.WEST, Bool.TRUE,
                VanillaBlockStates.UP, Bool.TRUE,
                VanillaBlockStates.DOWN, Bool.TRUE
        )));
        registerDuplicateOutput(BlockMapping.of("minecraft:red_mushroom_block", "data", 10, ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                VanillaBlockStates.NORTH, Bool.TRUE,
                VanillaBlockStates.EAST, Bool.TRUE,
                VanillaBlockStates.SOUTH, Bool.TRUE,
                VanillaBlockStates.WEST, Bool.TRUE,
                VanillaBlockStates.UP, Bool.FALSE,
                VanillaBlockStates.DOWN, Bool.FALSE
        )));

        // Fallback to a normal mushroom block since stem doesn't exist in bedrock
        registerDuplicateInput(BlockMapping.of("minecraft:brown_mushroom_block", "data", 15, ChunkerVanillaBlockType.MUSHROOM_STEM));

        register(BlockMapping.of("minecraft:nether_brick", ChunkerVanillaBlockType.NETHER_BRICKS));
        register(BlockMapping.of("minecraft:portal", ChunkerVanillaBlockType.NETHER_PORTAL, BedrockLegacyStateGroups.PORTAL));
        register(BlockMapping.of("minecraft:quartz_ore", ChunkerVanillaBlockType.NETHER_QUARTZ_ORE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:trapped_chest", ChunkerVanillaBlockType.TRAPPED_CHEST)
                        .put("minecraft:chest", ChunkerVanillaBlockType.CHEST)
                        .build(),
                BedrockLegacyStateGroups.CHEST));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:granite_stairs", ChunkerVanillaBlockType.GRANITE_STAIRS)
                        .put("minecraft:diorite_stairs", ChunkerVanillaBlockType.DIORITE_STAIRS)
                        .put("minecraft:purpur_stairs", ChunkerVanillaBlockType.PURPUR_STAIRS)
                        .put("minecraft:red_sandstone_stairs", ChunkerVanillaBlockType.RED_SANDSTONE_STAIRS)
                        .put("minecraft:dark_prismarine_stairs", ChunkerVanillaBlockType.DARK_PRISMARINE_STAIRS)
                        .put("minecraft:dark_oak_stairs", ChunkerVanillaBlockType.DARK_OAK_STAIRS)
                        .put("minecraft:smooth_red_sandstone_stairs", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_STAIRS)
                        .put("minecraft:smooth_quartz_stairs", ChunkerVanillaBlockType.SMOOTH_QUARTZ_STAIRS)
                        .put("minecraft:polished_diorite_stairs", ChunkerVanillaBlockType.POLISHED_DIORITE_STAIRS)
                        .put("minecraft:oak_stairs", ChunkerVanillaBlockType.OAK_STAIRS)
                        .put("minecraft:polished_granite_stairs", ChunkerVanillaBlockType.POLISHED_GRANITE_STAIRS)
                        .put("minecraft:red_nether_brick_stairs", ChunkerVanillaBlockType.RED_NETHER_BRICK_STAIRS)
                        .put("minecraft:mossy_stone_brick_stairs", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_STAIRS)
                        .put("minecraft:polished_andesite_stairs", ChunkerVanillaBlockType.POLISHED_ANDESITE_STAIRS)
                        .put("minecraft:prismarine_bricks_stairs", ChunkerVanillaBlockType.PRISMARINE_BRICK_STAIRS)
                        .put("minecraft:sandstone_stairs", ChunkerVanillaBlockType.SANDSTONE_STAIRS)
                        .put("minecraft:stone_stairs", ChunkerVanillaBlockType.COBBLESTONE_STAIRS)
                        .put("minecraft:normal_stone_stairs", ChunkerVanillaBlockType.STONE_STAIRS)
                        .put("minecraft:stone_brick_stairs", ChunkerVanillaBlockType.STONE_BRICK_STAIRS)
                        .put("minecraft:birch_stairs", ChunkerVanillaBlockType.BIRCH_STAIRS)
                        .put("minecraft:smooth_sandstone_stairs", ChunkerVanillaBlockType.SMOOTH_SANDSTONE_STAIRS)
                        .put("minecraft:jungle_stairs", ChunkerVanillaBlockType.JUNGLE_STAIRS)
                        .put("minecraft:prismarine_stairs", ChunkerVanillaBlockType.PRISMARINE_STAIRS)
                        .put("minecraft:brick_stairs", ChunkerVanillaBlockType.BRICK_STAIRS)
                        .put("minecraft:nether_brick_stairs", ChunkerVanillaBlockType.NETHER_BRICK_STAIRS)
                        .put("minecraft:mossy_cobblestone_stairs", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_STAIRS)
                        .put("minecraft:acacia_stairs", ChunkerVanillaBlockType.ACACIA_STAIRS)
                        .put("minecraft:end_brick_stairs", ChunkerVanillaBlockType.END_STONE_BRICK_STAIRS)
                        .put("minecraft:quartz_stairs", ChunkerVanillaBlockType.QUARTZ_STAIRS)
                        .put("minecraft:spruce_stairs", ChunkerVanillaBlockType.SPRUCE_STAIRS)
                        .put("minecraft:andesite_stairs", ChunkerVanillaBlockType.ANDESITE_STAIRS)
                        .build(),
                BedrockLegacyStateGroups.STAIRS));
        register(BlockMapping.of("minecraft:nether_wart_block", ChunkerVanillaBlockType.NETHER_WART_BLOCK));
        register(BlockMapping.of("minecraft:netherrack", ChunkerVanillaBlockType.NETHERRACK));
        register(BlockMapping.of("minecraft:noteblock", ChunkerVanillaBlockType.NOTE_BLOCK, BedrockLegacyStateGroups.NOTE_BLOCK));
        register(BlockMapping.of("minecraft:observer", ChunkerVanillaBlockType.OBSERVER, BedrockLegacyStateGroups.OBSERVER));
        register(BlockMapping.of("minecraft:obsidian", ChunkerVanillaBlockType.OBSIDIAN));
        register(BlockMapping.of("minecraft:packed_ice", ChunkerVanillaBlockType.PACKED_ICE));

        // Prismarine
        register(BlockMapping.flatten("minecraft:prismarine", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.PRISMARINE)
                        .put(1, ChunkerVanillaBlockType.DARK_PRISMARINE)
                        .put(2, ChunkerVanillaBlockType.PRISMARINE_BRICKS)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:prismarine", ChunkerVanillaBlockType.PRISMARINE));

        // Pumpkin / Melon Stem
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:pumpkin_stem", ChunkerVanillaBlockType.PUMPKIN_STEM)
                        .put("minecraft:melon_stem", ChunkerVanillaBlockType.MELON_STEM)
                        .build(),
                BedrockLegacyStateGroups.CROP));

        // Other crops
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:potatoes", ChunkerVanillaBlockType.POTATOES)
                        .put("minecraft:carrots", ChunkerVanillaBlockType.CARROTS)
                        .put("minecraft:wheat", ChunkerVanillaBlockType.WHEAT)
                        .build(),
                BedrockLegacyStateGroups.CROP));

        // Wood
        register(BlockMapping.flatten("minecraft:wood", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.OAK_WOOD)
                        .put(1, ChunkerVanillaBlockType.SPRUCE_WOOD)
                        .put(2, ChunkerVanillaBlockType.BIRCH_WOOD)
                        .put(3, ChunkerVanillaBlockType.JUNGLE_WOOD)
                        .put(4, ChunkerVanillaBlockType.ACACIA_WOOD)
                        .put(5, ChunkerVanillaBlockType.DARK_OAK_WOOD)
                        .put(8, ChunkerVanillaBlockType.STRIPPED_OAK_WOOD)
                        .put(9, ChunkerVanillaBlockType.STRIPPED_SPRUCE_WOOD)
                        .put(10, ChunkerVanillaBlockType.STRIPPED_BIRCH_WOOD)
                        .put(11, ChunkerVanillaBlockType.STRIPPED_JUNGLE_WOOD)
                        .put(12, ChunkerVanillaBlockType.STRIPPED_ACACIA_WOOD)
                        .put(13, ChunkerVanillaBlockType.STRIPPED_DARK_OAK_WOOD)
                        .build(),
                BedrockLegacyStateGroups.DEFAULT_AXIS
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:wood", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(6, ChunkerVanillaBlockType.OAK_WOOD)
                        .put(7, ChunkerVanillaBlockType.OAK_WOOD)
                        .put(14, ChunkerVanillaBlockType.STRIPPED_OAK_WOOD)
                        .put(15, ChunkerVanillaBlockType.STRIPPED_OAK_WOOD)
                        .build(),
                BedrockLegacyStateGroups.DEFAULT_AXIS
        ));

        // Wall Skulls - Converted to block entity data
        register(BlockMapping.of("minecraft:skull", ChunkerVanillaBlockType.SKELETON_WALL_SKULL, BedrockLegacyStateGroups.WALL_SKULL));
        registerDuplicateInput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:skull", ChunkerVanillaBlockType.ZOMBIE_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.PLAYER_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.CREEPER_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.WITHER_SKELETON_WALL_SKULL)
                        .put("minecraft:skull", ChunkerVanillaBlockType.DRAGON_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.PIGLIN_WALL_HEAD)
                        .build(),
                BedrockLegacyStateGroups.WALL_SKULL));

        // Skulls - Converted to block entity data
        register(BlockMapping.of("minecraft:skull", "data", 1, ChunkerVanillaBlockType.SKELETON_SKULL, BedrockLegacyStateGroups.SKULL, VanillaBlockStates.NO_DROP, Bool.FALSE));
        register(BlockMapping.of("minecraft:skull", "data", 9, ChunkerVanillaBlockType.SKELETON_SKULL, BedrockLegacyStateGroups.SKULL, VanillaBlockStates.NO_DROP, Bool.TRUE));
        registerDuplicateInput(BlockMapping.flatten("minecraft:skull", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(1, ChunkerVanillaBlockType.PLAYER_HEAD)
                        .put(1, ChunkerVanillaBlockType.DRAGON_HEAD)
                        .put(1, ChunkerVanillaBlockType.PIGLIN_HEAD)
                        .put(1, ChunkerVanillaBlockType.WITHER_SKELETON_SKULL)
                        .put(1, ChunkerVanillaBlockType.ZOMBIE_HEAD)
                        .put(1, ChunkerVanillaBlockType.CREEPER_HEAD)
                        .build(),
                BedrockLegacyStateGroups.SKULL, VanillaBlockStates.NO_DROP, Bool.FALSE));
        registerDuplicateInput(BlockMapping.flatten("minecraft:skull", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(9, ChunkerVanillaBlockType.PLAYER_HEAD)
                        .put(9, ChunkerVanillaBlockType.DRAGON_HEAD)
                        .put(9, ChunkerVanillaBlockType.PIGLIN_HEAD)
                        .put(9, ChunkerVanillaBlockType.WITHER_SKELETON_SKULL)
                        .put(9, ChunkerVanillaBlockType.ZOMBIE_HEAD)
                        .put(9, ChunkerVanillaBlockType.CREEPER_HEAD)
                        .build(),
                BedrockLegacyStateGroups.SKULL, VanillaBlockStates.NO_DROP, Bool.TRUE));

        // Flower pots
        register(BlockMapping.of("minecraft:flower_pot", ChunkerVanillaBlockType.FLOWER_POT));
        registerDuplicateInput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_RED_TULIP)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_AZURE_BLUET)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_WHITE_TULIP)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_BLUE_ORCHID)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_RED_MUSHROOM)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_SPRUCE_SAPLING)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_AZALEA_BUSH)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_WITHER_ROSE)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_TORCHFLOWER)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_ORANGE_TULIP)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_CHERRY_SAPLING)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_JUNGLE_SAPLING)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_BROWN_MUSHROOM)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_CORNFLOWER)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_FLOWERING_AZALEA_BUSH)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_ALLIUM)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_OAK_SAPLING)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_WARPED_FUNGUS)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_DEAD_BUSH)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_CRIMSON_ROOTS)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_CRIMSON_FUNGUS)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_POPPY)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_MANGROVE_PROPAGULE)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_FERN)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_CACTUS)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_BIRCH_SAPLING)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_OXEYE_DAISY)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_ACACIA_SAPLING)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_LILY_OF_THE_VALLEY)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_DARK_OAK_SAPLING)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_DANDELION)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_PINK_TULIP)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_WARPED_ROOTS)
                .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_BAMBOO)
                .build()
        ));

        // Double Plant
        register(BlockMapping.flatten("minecraft:double_plant", "data", VanillaBlockStates.HALF,
                ImmutableMultimap.<Integer, Pair<Half, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(Half.BOTTOM, ChunkerVanillaBlockType.SUNFLOWER))
                        .put(1, Pair.of(Half.BOTTOM, ChunkerVanillaBlockType.LILAC))
                        .put(2, Pair.of(Half.BOTTOM, ChunkerVanillaBlockType.TALL_GRASS))
                        .put(3, Pair.of(Half.BOTTOM, ChunkerVanillaBlockType.LARGE_FERN))
                        .put(4, Pair.of(Half.BOTTOM, ChunkerVanillaBlockType.ROSE_BUSH))
                        .put(5, Pair.of(Half.BOTTOM, ChunkerVanillaBlockType.PEONY))
                        .put(8, Pair.of(Half.TOP, ChunkerVanillaBlockType.SUNFLOWER))
                        .put(9, Pair.of(Half.TOP, ChunkerVanillaBlockType.LILAC))
                        .put(10, Pair.of(Half.TOP, ChunkerVanillaBlockType.TALL_GRASS))
                        .put(11, Pair.of(Half.TOP, ChunkerVanillaBlockType.LARGE_FERN))
                        .put(12, Pair.of(Half.TOP, ChunkerVanillaBlockType.ROSE_BUSH))
                        .put(13, Pair.of(Half.TOP, ChunkerVanillaBlockType.PEONY))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:double_plant", "data", VanillaBlockStates.HALF,
                ImmutableMultimap.<Integer, Pair<Half, ? extends ChunkerBlockType>>builder()
                        .put(6, Pair.of(Half.BOTTOM, ChunkerVanillaBlockType.SUNFLOWER))
                        .put(7, Pair.of(Half.BOTTOM, ChunkerVanillaBlockType.SUNFLOWER))
                        .put(14, Pair.of(Half.TOP, ChunkerVanillaBlockType.SUNFLOWER))
                        .put(15, Pair.of(Half.TOP, ChunkerVanillaBlockType.SUNFLOWER))
                        .build()
        ));

        // Tall grass (two types which look identical to other blocks)
        register(BlockMapping.of("minecraft:tallgrass", "data", 1, ChunkerVanillaBlockType.SHORT_GRASS));
        registerDuplicateOutput(BlockMapping.of("minecraft:tallgrass", "data", 0, ChunkerVanillaBlockType.SHORT_GRASS));
        register(BlockMapping.of("minecraft:tallgrass", "data", 2, ChunkerVanillaBlockType.FERN));
        registerDuplicateOutput(BlockMapping.of("minecraft:tallgrass", "data", 3, ChunkerVanillaBlockType.FERN));

        // Hay and Bone have a deprecated field
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:hay_block", ChunkerVanillaBlockType.HAY_BLOCK)
                        .put("minecraft:bone_block", ChunkerVanillaBlockType.BONE_BLOCK)
                        .build(),
                BedrockLegacyStateGroups.PILLAR_BLOCK_DEPRECATED));

        // Stripped logs
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:stripped_acacia_log", ChunkerVanillaBlockType.STRIPPED_ACACIA_LOG)
                        .put("minecraft:stripped_dark_oak_log", ChunkerVanillaBlockType.STRIPPED_DARK_OAK_LOG)
                        .put("minecraft:stripped_spruce_log", ChunkerVanillaBlockType.STRIPPED_SPRUCE_LOG)
                        .put("minecraft:stripped_oak_log", ChunkerVanillaBlockType.STRIPPED_OAK_LOG)
                        .put("minecraft:stripped_jungle_log", ChunkerVanillaBlockType.STRIPPED_JUNGLE_LOG)
                        .put("minecraft:stripped_birch_log", ChunkerVanillaBlockType.STRIPPED_BIRCH_LOG)
                        .build(),
                BedrockLegacyStateGroups.PILLAR_BLOCK));

        // Pumpkin (no direction in Chunker format)
        register(BlockMapping.of("minecraft:pumpkin", "data", 0, ChunkerVanillaBlockType.PUMPKIN));
        registerDuplicateOutput(BlockMapping.of("minecraft:pumpkin", ChunkerVanillaBlockType.PUMPKIN));

        // Purpur Block default rotation (since there is no rotation Java)
        register(BlockMapping.of("minecraft:purpur_block", "data", 0, ChunkerVanillaBlockType.PURPUR_BLOCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:purpur_block", ChunkerVanillaBlockType.PURPUR_BLOCK));

        // Quartz Blocks default rotation (since there is no rotation Java)
        register(BlockMapping.of("minecraft:quartz_block", "data", 0, ChunkerVanillaBlockType.QUARTZ_BLOCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", ChunkerVanillaBlockType.QUARTZ_BLOCK));

        register(BlockMapping.of("minecraft:quartz_block", "data", 1, ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", "data", 5, ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", "data", 9, ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", "data", 13, ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));

        register(BlockMapping.of("minecraft:quartz_block", "data", 3, ChunkerVanillaBlockType.SMOOTH_QUARTZ));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", "data", 7, ChunkerVanillaBlockType.SMOOTH_QUARTZ));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", "data", 11, ChunkerVanillaBlockType.SMOOTH_QUARTZ));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", "data", 15, ChunkerVanillaBlockType.SMOOTH_QUARTZ));

        // Purpur / Quartz Pillar
        register(BlockMapping.of("minecraft:quartz_block", "data", 2, ChunkerVanillaBlockType.QUARTZ_PILLAR, VanillaBlockStates.AXIS, Axis.Y));
        register(BlockMapping.of("minecraft:quartz_block", "data", 6, ChunkerVanillaBlockType.QUARTZ_PILLAR, VanillaBlockStates.AXIS, Axis.X));
        register(BlockMapping.of("minecraft:quartz_block", "data", 10, ChunkerVanillaBlockType.QUARTZ_PILLAR, VanillaBlockStates.AXIS, Axis.Z));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", "data", 14, ChunkerVanillaBlockType.QUARTZ_PILLAR, VanillaBlockStates.AXIS, Axis.Y));

        register(BlockMapping.of("minecraft:purpur_block", "data", 2, ChunkerVanillaBlockType.PURPUR_PILLAR, VanillaBlockStates.AXIS, Axis.Y));
        register(BlockMapping.of("minecraft:purpur_block", "data", 6, ChunkerVanillaBlockType.PURPUR_PILLAR, VanillaBlockStates.AXIS, Axis.X));
        register(BlockMapping.of("minecraft:purpur_block", "data", 10, ChunkerVanillaBlockType.PURPUR_PILLAR, VanillaBlockStates.AXIS, Axis.Z));
        registerDuplicateOutput(BlockMapping.of("minecraft:purpur_block", "data", 14, ChunkerVanillaBlockType.PURPUR_PILLAR, VanillaBlockStates.AXIS, Axis.Y));

        register(BlockMapping.of("minecraft:end_rod", ChunkerVanillaBlockType.END_ROD, BedrockLegacyStateGroups.END_ROD));
        register(BlockMapping.of("minecraft:rail", ChunkerVanillaBlockType.RAIL, BedrockLegacyStateGroups.RAIL));
        register(BlockMapping.of("minecraft:red_mushroom", ChunkerVanillaBlockType.RED_MUSHROOM));
        register(BlockMapping.of("minecraft:red_nether_brick", ChunkerVanillaBlockType.RED_NETHER_BRICKS));
        register(BlockMapping.of("minecraft:redstone_block", ChunkerVanillaBlockType.REDSTONE_BLOCK));
        register(BlockMapping.of("minecraft:slime", ChunkerVanillaBlockType.SLIME_BLOCK));
        register(BlockMapping.of("minecraft:smithing_table", ChunkerVanillaBlockType.SMITHING_TABLE));
        register(BlockMapping.of("minecraft:smooth_stone", ChunkerVanillaBlockType.SMOOTH_STONE));
        register(BlockMapping.of("minecraft:soul_sand", ChunkerVanillaBlockType.SOUL_SAND));
        register(BlockMapping.of("minecraft:mob_spawner", ChunkerVanillaBlockType.SPAWNER));
        register(BlockMapping.of("minecraft:structure_block", ChunkerVanillaBlockType.STRUCTURE_BLOCK, BedrockLegacyStateGroups.STRUCTURE_BLOCK));
        register(BlockMapping.of("minecraft:hardened_clay", ChunkerVanillaBlockType.TERRACOTTA));
        register(BlockMapping.of("minecraft:tripwire_hook", ChunkerVanillaBlockType.TRIPWIRE_HOOK, BedrockLegacyStateGroups.TRIPWIRE_HOOK));
        register(BlockMapping.of("minecraft:turtle_egg", ChunkerVanillaBlockType.TURTLE_EGG, BedrockLegacyStateGroups.TURTLE_EGG));

        // Sponge
        register(BlockMapping.flatten("minecraft:sponge", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(1, ChunkerVanillaBlockType.WET_SPONGE)
                        .put(0, ChunkerVanillaBlockType.SPONGE)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:sponge", ChunkerVanillaBlockType.SPONGE));

        // Redstone lamp
        register(BlockMapping.of("minecraft:lit_redstone_lamp", ChunkerVanillaBlockType.REDSTONE_LAMP, VanillaBlockStates.LIT, Bool.TRUE));
        register(BlockMapping.of("minecraft:redstone_lamp", ChunkerVanillaBlockType.REDSTONE_LAMP, VanillaBlockStates.LIT, Bool.FALSE));

        // Redstone ore
        register(BlockMapping.of("minecraft:lit_redstone_ore", ChunkerVanillaBlockType.REDSTONE_ORE, VanillaBlockStates.LIT, Bool.TRUE));
        register(BlockMapping.of("minecraft:redstone_ore", ChunkerVanillaBlockType.REDSTONE_ORE, VanillaBlockStates.LIT, Bool.FALSE));

        // Redstone torch (normal/wall)
        register(BlockMapping.of("minecraft:unlit_redstone_torch", "data", 5, ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.FALSE));
        registerDuplicateOutput(BlockMapping.of("minecraft:unlit_redstone_torch", "data", 0, ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.FALSE));
        registerDuplicateOutput(BlockMapping.of("minecraft:unlit_redstone_torch", "data", 6, ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.FALSE));
        registerDuplicateOutput(BlockMapping.of("minecraft:unlit_redstone_torch", "data", 7, ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.FALSE));
        register(BlockMapping.of("minecraft:unlit_redstone_torch", ChunkerVanillaBlockType.REDSTONE_WALL_TORCH, BedrockLegacyStateGroups.TORCH_FACING, VanillaBlockStates.LIT, Bool.FALSE));

        // Lit redstone torch
        register(BlockMapping.of("minecraft:redstone_torch", "data", 5, ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.TRUE));
        registerDuplicateOutput(BlockMapping.of("minecraft:redstone_torch", "data", 0, ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.TRUE));
        registerDuplicateOutput(BlockMapping.of("minecraft:redstone_torch", "data", 6, ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.TRUE));
        registerDuplicateOutput(BlockMapping.of("minecraft:redstone_torch", "data", 7, ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.TRUE));
        register(BlockMapping.of("minecraft:redstone_torch", ChunkerVanillaBlockType.REDSTONE_WALL_TORCH, BedrockLegacyStateGroups.TORCH_FACING, VanillaBlockStates.LIT, Bool.TRUE));

        // Torch (normal/wall)
        register(BlockMapping.of("minecraft:torch", "data", 5, ChunkerVanillaBlockType.TORCH));
        registerDuplicateOutput(BlockMapping.of("minecraft:torch", "data", 0, ChunkerVanillaBlockType.TORCH));
        registerDuplicateOutput(BlockMapping.of("minecraft:torch", "data", 6, ChunkerVanillaBlockType.TORCH));
        registerDuplicateOutput(BlockMapping.of("minecraft:torch", "data", 7, ChunkerVanillaBlockType.TORCH));
        register(BlockMapping.of("minecraft:torch", ChunkerVanillaBlockType.WALL_TORCH, BedrockLegacyStateGroups.TORCH_FACING));

        // Repeater
        register(BlockMapping.of("minecraft:powered_repeater", ChunkerVanillaBlockType.REPEATER, BedrockLegacyStateGroups.REPEATER, VanillaBlockStates.POWERED, Bool.TRUE));
        register(BlockMapping.of("minecraft:unpowered_repeater", ChunkerVanillaBlockType.REPEATER, BedrockLegacyStateGroups.REPEATER, VanillaBlockStates.POWERED, Bool.FALSE));

        register(BlockMapping.of("minecraft:redstone_wire", ChunkerVanillaBlockType.REDSTONE_WIRE, BedrockLegacyStateGroups.WIRE));
        register(BlockMapping.of("minecraft:scaffolding", ChunkerVanillaBlockType.SCAFFOLDING, BedrockLegacyStateGroups.SCAFFOLDING));

        // Sea Pickle
        register(BlockMapping.of("minecraft:sea_pickle", ChunkerVanillaBlockType.SEA_PICKLE, BedrockLegacyStateGroups.SEA_PICKLE));

        // Snow
        register(BlockMapping.of("minecraft:snow_layer", ChunkerVanillaBlockType.SNOW, BedrockLegacyStateGroups.LAYER_BLOCK));
        register(BlockMapping.of("minecraft:snow", ChunkerVanillaBlockType.SNOW_BLOCK));

        // Sea grass
        register(BlockMapping.of("minecraft:seagrass", "data", 2, ChunkerVanillaBlockType.TALL_SEAGRASS, VanillaBlockStates.HALF, Half.BOTTOM));
        register(BlockMapping.of("minecraft:seagrass", "data", 1, ChunkerVanillaBlockType.TALL_SEAGRASS, VanillaBlockStates.HALF, Half.TOP));
        register(BlockMapping.of("minecraft:seagrass", "data", 0, ChunkerVanillaBlockType.SEAGRASS));
        registerDuplicateOutput(BlockMapping.of("minecraft:seagrass", "data", 3, ChunkerVanillaBlockType.SEAGRASS));

        // Liquids
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:water", ChunkerVanillaBlockType.WATER)
                        .put("minecraft:lava", ChunkerVanillaBlockType.LAVA)
                        .build(),
                BedrockLegacyStateGroups.LIQUID, VanillaBlockStates.FLOWING, Bool.FALSE));

        // Flowing liquids should use the flowing state
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:flowing_water", ChunkerVanillaBlockType.WATER)
                        .put("minecraft:flowing_lava", ChunkerVanillaBlockType.LAVA)
                        .build(),
                BedrockLegacyStateGroups.LIQUID, VanillaBlockStates.FLOWING, Bool.TRUE));

        // TNT
        register(BlockMapping.of("minecraft:tnt", ChunkerVanillaBlockType.TNT, BedrockLegacyStateGroups.TNT));

        // Vines
        register(BlockMapping.of("minecraft:vine", ChunkerVanillaBlockType.VINE, BedrockLegacyStateGroups.VINE, VanillaBlockStates.UP, Bool.FALSE));
        registerDuplicateInput(BlockMapping.of("minecraft:vine", ChunkerVanillaBlockType.VINE, BedrockLegacyStateGroups.VINE));

        register(BlockMapping.of("minecraft:frame", ChunkerVanillaBlockType.ITEM_FRAME_BEDROCK, BedrockLegacyStateGroups.FRAME, VanillaBlockStates.LIT, Bool.FALSE)); // Needs to be turned into an entity
        registerDuplicateInput(BlockMapping.of("minecraft:frame", ChunkerVanillaBlockType.ITEM_FRAME_BEDROCK, BedrockLegacyStateGroups.FRAME));
        register(BlockMapping.flatten("minecraft:wool", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.WHITE_WOOL)
                        .put(1, ChunkerVanillaBlockType.ORANGE_WOOL)
                        .put(2, ChunkerVanillaBlockType.MAGENTA_WOOL)
                        .put(3, ChunkerVanillaBlockType.LIGHT_BLUE_WOOL)
                        .put(4, ChunkerVanillaBlockType.YELLOW_WOOL)
                        .put(5, ChunkerVanillaBlockType.LIME_WOOL)
                        .put(6, ChunkerVanillaBlockType.PINK_WOOL)
                        .put(7, ChunkerVanillaBlockType.GRAY_WOOL)
                        .put(8, ChunkerVanillaBlockType.LIGHT_GRAY_WOOL)
                        .put(9, ChunkerVanillaBlockType.CYAN_WOOL)
                        .put(10, ChunkerVanillaBlockType.PURPLE_WOOL)
                        .put(11, ChunkerVanillaBlockType.BLUE_WOOL)
                        .put(12, ChunkerVanillaBlockType.BROWN_WOOL)
                        .put(13, ChunkerVanillaBlockType.GREEN_WOOL)
                        .put(14, ChunkerVanillaBlockType.RED_WOOL)
                        .put(15, ChunkerVanillaBlockType.BLACK_WOOL)
                        .build()
        ));

        // Log
        register(BlockMapping.flatten("minecraft:log", "data", VanillaBlockStates.AXIS,
                ImmutableMultimap.<Integer, Pair<Axis, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(Axis.Y, ChunkerVanillaBlockType.OAK_LOG))
                        .put(1, Pair.of(Axis.Y, ChunkerVanillaBlockType.SPRUCE_LOG))
                        .put(2, Pair.of(Axis.Y, ChunkerVanillaBlockType.BIRCH_LOG))
                        .put(3, Pair.of(Axis.Y, ChunkerVanillaBlockType.JUNGLE_LOG))
                        .put(4, Pair.of(Axis.X, ChunkerVanillaBlockType.OAK_LOG))
                        .put(5, Pair.of(Axis.X, ChunkerVanillaBlockType.SPRUCE_LOG))
                        .put(6, Pair.of(Axis.X, ChunkerVanillaBlockType.BIRCH_LOG))
                        .put(7, Pair.of(Axis.X, ChunkerVanillaBlockType.JUNGLE_LOG))
                        .put(8, Pair.of(Axis.Z, ChunkerVanillaBlockType.OAK_LOG))
                        .put(9, Pair.of(Axis.Z, ChunkerVanillaBlockType.SPRUCE_LOG))
                        .put(10, Pair.of(Axis.Z, ChunkerVanillaBlockType.BIRCH_LOG))
                        .put(11, Pair.of(Axis.Z, ChunkerVanillaBlockType.JUNGLE_LOG))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:log", "data", VanillaBlockStates.AXIS,
                ImmutableMultimap.<Integer, Pair<Axis, ? extends ChunkerBlockType>>builder()
                        .put(12, Pair.of(Axis.Y, ChunkerVanillaBlockType.OAK_LOG))
                        .put(13, Pair.of(Axis.Y, ChunkerVanillaBlockType.SPRUCE_LOG))
                        .put(14, Pair.of(Axis.Y, ChunkerVanillaBlockType.BIRCH_LOG))
                        .put(15, Pair.of(Axis.Y, ChunkerVanillaBlockType.JUNGLE_LOG))
                        .build()
        ));
        // Log 2
        register(BlockMapping.flatten("minecraft:log2", "data", VanillaBlockStates.AXIS,
                ImmutableMultimap.<Integer, Pair<Axis, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(Axis.Y, ChunkerVanillaBlockType.ACACIA_LOG))
                        .put(1, Pair.of(Axis.Y, ChunkerVanillaBlockType.DARK_OAK_LOG))
                        .put(4, Pair.of(Axis.X, ChunkerVanillaBlockType.ACACIA_LOG))
                        .put(5, Pair.of(Axis.X, ChunkerVanillaBlockType.DARK_OAK_LOG))
                        .put(8, Pair.of(Axis.Z, ChunkerVanillaBlockType.ACACIA_LOG))
                        .put(9, Pair.of(Axis.Z, ChunkerVanillaBlockType.DARK_OAK_LOG))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:log2", "data", VanillaBlockStates.AXIS,
                ImmutableMultimap.<Integer, Pair<Axis, ? extends ChunkerBlockType>>builder()
                        .put(2, Pair.of(Axis.Y, ChunkerVanillaBlockType.ACACIA_LOG))
                        .put(3, Pair.of(Axis.Y, ChunkerVanillaBlockType.DARK_OAK_LOG))
                        .put(6, Pair.of(Axis.X, ChunkerVanillaBlockType.ACACIA_LOG))
                        .put(7, Pair.of(Axis.X, ChunkerVanillaBlockType.DARK_OAK_LOG))
                        .put(10, Pair.of(Axis.Z, ChunkerVanillaBlockType.ACACIA_LOG))
                        .put(11, Pair.of(Axis.Z, ChunkerVanillaBlockType.ACACIA_LOG))
                        .put(12, Pair.of(Axis.Y, ChunkerVanillaBlockType.ACACIA_LOG))
                        .put(13, Pair.of(Axis.Y, ChunkerVanillaBlockType.ACACIA_LOG))
                        .put(14, Pair.of(Axis.Y, ChunkerVanillaBlockType.ACACIA_LOG))
                        .put(15, Pair.of(Axis.Y, ChunkerVanillaBlockType.ACACIA_LOG))
                        .build()
        ));

        // Fence
        register(BlockMapping.flatten("minecraft:fence", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.OAK_FENCE)
                        .put(1, ChunkerVanillaBlockType.SPRUCE_FENCE)
                        .put(2, ChunkerVanillaBlockType.BIRCH_FENCE)
                        .put(3, ChunkerVanillaBlockType.JUNGLE_FENCE)
                        .put(4, ChunkerVanillaBlockType.ACACIA_FENCE)
                        .put(5, ChunkerVanillaBlockType.DARK_OAK_FENCE)
                        .build(),
                BedrockLegacyStateGroups.CONNECTABLE_HORIZONTAL));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:fence", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(6, ChunkerVanillaBlockType.OAK_FENCE)
                        .put(7, ChunkerVanillaBlockType.OAK_FENCE)
                        .build(),
                BedrockLegacyStateGroups.CONNECTABLE_HORIZONTAL));

        register(BlockMapping.flatten("minecraft:carpet", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.WHITE_CARPET)
                        .put(1, ChunkerVanillaBlockType.ORANGE_CARPET)
                        .put(2, ChunkerVanillaBlockType.MAGENTA_CARPET)
                        .put(3, ChunkerVanillaBlockType.LIGHT_BLUE_CARPET)
                        .put(4, ChunkerVanillaBlockType.YELLOW_CARPET)
                        .put(5, ChunkerVanillaBlockType.LIME_CARPET)
                        .put(6, ChunkerVanillaBlockType.PINK_CARPET)
                        .put(7, ChunkerVanillaBlockType.GRAY_CARPET)
                        .put(8, ChunkerVanillaBlockType.LIGHT_GRAY_CARPET)
                        .put(9, ChunkerVanillaBlockType.CYAN_CARPET)
                        .put(10, ChunkerVanillaBlockType.PURPLE_CARPET)
                        .put(11, ChunkerVanillaBlockType.BLUE_CARPET)
                        .put(12, ChunkerVanillaBlockType.BROWN_CARPET)
                        .put(13, ChunkerVanillaBlockType.GREEN_CARPET)
                        .put(14, ChunkerVanillaBlockType.RED_CARPET)
                        .put(15, ChunkerVanillaBlockType.BLACK_CARPET)
                        .build()
        ));

        register(BlockMapping.of("minecraft:undyed_shulker_box", ChunkerVanillaBlockType.SHULKER_BOX, BedrockLegacyStateGroups.SHULKER_BOX));
        register(BlockMapping.flatten("minecraft:shulker_box", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.WHITE_SHULKER_BOX)
                        .put(1, ChunkerVanillaBlockType.ORANGE_SHULKER_BOX)
                        .put(2, ChunkerVanillaBlockType.MAGENTA_SHULKER_BOX)
                        .put(3, ChunkerVanillaBlockType.LIGHT_BLUE_SHULKER_BOX)
                        .put(4, ChunkerVanillaBlockType.YELLOW_SHULKER_BOX)
                        .put(5, ChunkerVanillaBlockType.LIME_SHULKER_BOX)
                        .put(6, ChunkerVanillaBlockType.PINK_SHULKER_BOX)
                        .put(7, ChunkerVanillaBlockType.GRAY_SHULKER_BOX)
                        .put(8, ChunkerVanillaBlockType.LIGHT_GRAY_SHULKER_BOX)
                        .put(9, ChunkerVanillaBlockType.CYAN_SHULKER_BOX)
                        .put(10, ChunkerVanillaBlockType.PURPLE_SHULKER_BOX)
                        .put(11, ChunkerVanillaBlockType.BLUE_SHULKER_BOX)
                        .put(12, ChunkerVanillaBlockType.BROWN_SHULKER_BOX)
                        .put(13, ChunkerVanillaBlockType.GREEN_SHULKER_BOX)
                        .put(14, ChunkerVanillaBlockType.RED_SHULKER_BOX)
                        .put(15, ChunkerVanillaBlockType.BLACK_SHULKER_BOX)
                        .build(),
                BedrockLegacyStateGroups.SHULKER_BOX
        ));

        // Concrete
        register(BlockMapping.flatten("minecraft:concrete", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.WHITE_CONCRETE)
                        .put(1, ChunkerVanillaBlockType.ORANGE_CONCRETE)
                        .put(2, ChunkerVanillaBlockType.MAGENTA_CONCRETE)
                        .put(3, ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE)
                        .put(4, ChunkerVanillaBlockType.YELLOW_CONCRETE)
                        .put(5, ChunkerVanillaBlockType.LIME_CONCRETE)
                        .put(6, ChunkerVanillaBlockType.PINK_CONCRETE)
                        .put(7, ChunkerVanillaBlockType.GRAY_CONCRETE)
                        .put(8, ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE)
                        .put(9, ChunkerVanillaBlockType.CYAN_CONCRETE)
                        .put(10, ChunkerVanillaBlockType.PURPLE_CONCRETE)
                        .put(11, ChunkerVanillaBlockType.BLUE_CONCRETE)
                        .put(12, ChunkerVanillaBlockType.BROWN_CONCRETE)
                        .put(13, ChunkerVanillaBlockType.GREEN_CONCRETE)
                        .put(14, ChunkerVanillaBlockType.RED_CONCRETE)
                        .put(15, ChunkerVanillaBlockType.BLACK_CONCRETE)
                        .build()
        ));

        register(BlockMapping.flatten("minecraft:stained_glass", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.WHITE_STAINED_GLASS)
                        .put(1, ChunkerVanillaBlockType.ORANGE_STAINED_GLASS)
                        .put(2, ChunkerVanillaBlockType.MAGENTA_STAINED_GLASS)
                        .put(3, ChunkerVanillaBlockType.LIGHT_BLUE_STAINED_GLASS)
                        .put(4, ChunkerVanillaBlockType.YELLOW_STAINED_GLASS)
                        .put(5, ChunkerVanillaBlockType.LIME_STAINED_GLASS)
                        .put(6, ChunkerVanillaBlockType.PINK_STAINED_GLASS)
                        .put(7, ChunkerVanillaBlockType.GRAY_STAINED_GLASS)
                        .put(8, ChunkerVanillaBlockType.LIGHT_GRAY_STAINED_GLASS)
                        .put(9, ChunkerVanillaBlockType.CYAN_STAINED_GLASS)
                        .put(10, ChunkerVanillaBlockType.PURPLE_STAINED_GLASS)
                        .put(11, ChunkerVanillaBlockType.BLUE_STAINED_GLASS)
                        .put(12, ChunkerVanillaBlockType.BROWN_STAINED_GLASS)
                        .put(13, ChunkerVanillaBlockType.GREEN_STAINED_GLASS)
                        .put(14, ChunkerVanillaBlockType.RED_STAINED_GLASS)
                        .put(15, ChunkerVanillaBlockType.BLACK_STAINED_GLASS)
                        .build()
        ));

        register(BlockMapping.flatten("minecraft:stained_glass_pane", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.WHITE_STAINED_GLASS_PANE)
                        .put(1, ChunkerVanillaBlockType.ORANGE_STAINED_GLASS_PANE)
                        .put(2, ChunkerVanillaBlockType.MAGENTA_STAINED_GLASS_PANE)
                        .put(3, ChunkerVanillaBlockType.LIGHT_BLUE_STAINED_GLASS_PANE)
                        .put(4, ChunkerVanillaBlockType.YELLOW_STAINED_GLASS_PANE)
                        .put(5, ChunkerVanillaBlockType.LIME_STAINED_GLASS_PANE)
                        .put(6, ChunkerVanillaBlockType.PINK_STAINED_GLASS_PANE)
                        .put(7, ChunkerVanillaBlockType.GRAY_STAINED_GLASS_PANE)
                        .put(8, ChunkerVanillaBlockType.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .put(9, ChunkerVanillaBlockType.CYAN_STAINED_GLASS_PANE)
                        .put(10, ChunkerVanillaBlockType.PURPLE_STAINED_GLASS_PANE)
                        .put(11, ChunkerVanillaBlockType.BLUE_STAINED_GLASS_PANE)
                        .put(12, ChunkerVanillaBlockType.BROWN_STAINED_GLASS_PANE)
                        .put(13, ChunkerVanillaBlockType.GREEN_STAINED_GLASS_PANE)
                        .put(14, ChunkerVanillaBlockType.RED_STAINED_GLASS_PANE)
                        .put(15, ChunkerVanillaBlockType.BLACK_STAINED_GLASS_PANE)
                        .build(),
                BedrockLegacyStateGroups.CONNECTABLE_HORIZONTAL));

        // Concrete Powder
        register(BlockMapping.flatten("minecraft:concretePowder", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.WHITE_CONCRETE_POWDER)
                        .put(1, ChunkerVanillaBlockType.ORANGE_CONCRETE_POWDER)
                        .put(2, ChunkerVanillaBlockType.MAGENTA_CONCRETE_POWDER)
                        .put(3, ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE_POWDER)
                        .put(4, ChunkerVanillaBlockType.YELLOW_CONCRETE_POWDER)
                        .put(5, ChunkerVanillaBlockType.LIME_CONCRETE_POWDER)
                        .put(6, ChunkerVanillaBlockType.PINK_CONCRETE_POWDER)
                        .put(7, ChunkerVanillaBlockType.GRAY_CONCRETE_POWDER)
                        .put(8, ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE_POWDER)
                        .put(9, ChunkerVanillaBlockType.CYAN_CONCRETE_POWDER)
                        .put(10, ChunkerVanillaBlockType.PURPLE_CONCRETE_POWDER)
                        .put(11, ChunkerVanillaBlockType.BLUE_CONCRETE_POWDER)
                        .put(12, ChunkerVanillaBlockType.BROWN_CONCRETE_POWDER)
                        .put(13, ChunkerVanillaBlockType.GREEN_CONCRETE_POWDER)
                        .put(14, ChunkerVanillaBlockType.RED_CONCRETE_POWDER)
                        .put(15, ChunkerVanillaBlockType.BLACK_CONCRETE_POWDER)
                        .build()
        ));
        // Legacy alias
        registerDuplicateOutput(BlockMapping.flatten("minecraft:concretepowder", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.WHITE_CONCRETE_POWDER)
                        .put(1, ChunkerVanillaBlockType.ORANGE_CONCRETE_POWDER)
                        .put(2, ChunkerVanillaBlockType.MAGENTA_CONCRETE_POWDER)
                        .put(3, ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE_POWDER)
                        .put(4, ChunkerVanillaBlockType.YELLOW_CONCRETE_POWDER)
                        .put(5, ChunkerVanillaBlockType.LIME_CONCRETE_POWDER)
                        .put(6, ChunkerVanillaBlockType.PINK_CONCRETE_POWDER)
                        .put(7, ChunkerVanillaBlockType.GRAY_CONCRETE_POWDER)
                        .put(8, ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE_POWDER)
                        .put(9, ChunkerVanillaBlockType.CYAN_CONCRETE_POWDER)
                        .put(10, ChunkerVanillaBlockType.PURPLE_CONCRETE_POWDER)
                        .put(11, ChunkerVanillaBlockType.BLUE_CONCRETE_POWDER)
                        .put(12, ChunkerVanillaBlockType.BROWN_CONCRETE_POWDER)
                        .put(13, ChunkerVanillaBlockType.GREEN_CONCRETE_POWDER)
                        .put(14, ChunkerVanillaBlockType.RED_CONCRETE_POWDER)
                        .put(15, ChunkerVanillaBlockType.BLACK_CONCRETE_POWDER)
                        .build()
        ));

        register(BlockMapping.flatten("minecraft:stained_hardened_clay", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.WHITE_TERRACOTTA)
                        .put(1, ChunkerVanillaBlockType.ORANGE_TERRACOTTA)
                        .put(2, ChunkerVanillaBlockType.MAGENTA_TERRACOTTA)
                        .put(3, ChunkerVanillaBlockType.LIGHT_BLUE_TERRACOTTA)
                        .put(4, ChunkerVanillaBlockType.YELLOW_TERRACOTTA)
                        .put(5, ChunkerVanillaBlockType.LIME_TERRACOTTA)
                        .put(6, ChunkerVanillaBlockType.PINK_TERRACOTTA)
                        .put(7, ChunkerVanillaBlockType.GRAY_TERRACOTTA)
                        .put(8, ChunkerVanillaBlockType.LIGHT_GRAY_TERRACOTTA)
                        .put(9, ChunkerVanillaBlockType.CYAN_TERRACOTTA)
                        .put(10, ChunkerVanillaBlockType.PURPLE_TERRACOTTA)
                        .put(11, ChunkerVanillaBlockType.BLUE_TERRACOTTA)
                        .put(12, ChunkerVanillaBlockType.BROWN_TERRACOTTA)
                        .put(13, ChunkerVanillaBlockType.GREEN_TERRACOTTA)
                        .put(14, ChunkerVanillaBlockType.RED_TERRACOTTA)
                        .put(15, ChunkerVanillaBlockType.BLACK_TERRACOTTA)
                        .build()
        ));

        register(BlockMapping.flatten("minecraft:stone", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.STONE)
                        .put(1, ChunkerVanillaBlockType.GRANITE)
                        .put(2, ChunkerVanillaBlockType.POLISHED_GRANITE)
                        .put(3, ChunkerVanillaBlockType.DIORITE)
                        .put(4, ChunkerVanillaBlockType.POLISHED_DIORITE)
                        .put(5, ChunkerVanillaBlockType.ANDESITE)
                        .put(6, ChunkerVanillaBlockType.POLISHED_ANDESITE)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:stone", ChunkerVanillaBlockType.STONE));

        register(BlockMapping.flatten("minecraft:planks", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.OAK_PLANKS)
                        .put(1, ChunkerVanillaBlockType.SPRUCE_PLANKS)
                        .put(2, ChunkerVanillaBlockType.BIRCH_PLANKS)
                        .put(3, ChunkerVanillaBlockType.JUNGLE_PLANKS)
                        .put(4, ChunkerVanillaBlockType.ACACIA_PLANKS)
                        .put(5, ChunkerVanillaBlockType.DARK_OAK_PLANKS)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:planks", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(6, ChunkerVanillaBlockType.OAK_PLANKS)
                        .put(7, ChunkerVanillaBlockType.OAK_PLANKS)
                        .build()
        ));

        // Piston / Sticky Piston
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:sticky_piston", ChunkerVanillaBlockType.STICKY_PISTON)
                        .put("minecraft:piston", ChunkerVanillaBlockType.PISTON)
                        .build(),
                BedrockLegacyStateGroups.PISTON));

        register(BlockMapping.of("minecraft:pistonArmCollision", ChunkerVanillaBlockType.PISTON_HEAD, BedrockLegacyStateGroups.PISTON_HEAD, VanillaBlockStates.PISTON_TYPE, PistonType.NORMAL));
        registerDuplicateInput(BlockMapping.of("minecraft:pistonArmCollision", ChunkerVanillaBlockType.PISTON_HEAD, BedrockLegacyStateGroups.PISTON_HEAD, VanillaBlockStates.PISTON_TYPE, PistonType.STICKY));

        // Legacy aliases
        registerDuplicateOutput(BlockMapping.of("minecraft:pistonarmcollision", ChunkerVanillaBlockType.PISTON_HEAD, BedrockLegacyStateGroups.PISTON_HEAD, VanillaBlockStates.PISTON_TYPE, PistonType.NORMAL));

        // Wood slabs
        register(BlockMapping.flatten("minecraft:wooden_slab", "data", VanillaBlockStates.SLAB_TYPE,
                ImmutableMultimap.<Integer, Pair<SlabType, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.OAK_SLAB))
                        .put(1, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.SPRUCE_SLAB))
                        .put(2, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.BIRCH_SLAB))
                        .put(3, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.JUNGLE_SLAB))
                        .put(4, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.ACACIA_SLAB))
                        .put(5, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.DARK_OAK_SLAB))
                        .put(8, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.OAK_SLAB))
                        .put(9, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.SPRUCE_SLAB))
                        .put(10, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.BIRCH_SLAB))
                        .put(11, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.JUNGLE_SLAB))
                        .put(12, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.ACACIA_SLAB))
                        .put(13, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.DARK_OAK_SLAB))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:wooden_slab", "data", VanillaBlockStates.SLAB_TYPE,
                ImmutableMultimap.<Integer, Pair<SlabType, ? extends ChunkerBlockType>>builder()
                        .put(6, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.OAK_SLAB))
                        .put(7, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.OAK_SLAB))
                        .put(14, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.OAK_SLAB))
                        .put(15, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.OAK_SLAB))
                        .build()
        ));

        register(BlockMapping.flatten("minecraft:double_wooden_slab", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.OAK_SLAB)
                        .put(1, ChunkerVanillaBlockType.SPRUCE_SLAB)
                        .put(2, ChunkerVanillaBlockType.BIRCH_SLAB)
                        .put(3, ChunkerVanillaBlockType.JUNGLE_SLAB)
                        .put(4, ChunkerVanillaBlockType.ACACIA_SLAB)
                        .put(5, ChunkerVanillaBlockType.DARK_OAK_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:double_wooden_slab", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(6, ChunkerVanillaBlockType.OAK_SLAB)
                        .put(7, ChunkerVanillaBlockType.OAK_SLAB)
                        .put(8, ChunkerVanillaBlockType.OAK_SLAB)
                        .put(9, ChunkerVanillaBlockType.SPRUCE_SLAB)
                        .put(10, ChunkerVanillaBlockType.BIRCH_SLAB)
                        .put(11, ChunkerVanillaBlockType.JUNGLE_SLAB)
                        .put(12, ChunkerVanillaBlockType.ACACIA_SLAB)
                        .put(13, ChunkerVanillaBlockType.DARK_OAK_SLAB)
                        .put(14, ChunkerVanillaBlockType.OAK_SLAB)
                        .put(15, ChunkerVanillaBlockType.OAK_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        // Legacy mapping for petrified_oak_slab
        registerDuplicateInput(BlockMapping.of("minecraft:wooden_slab", "data", 0, ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.BOTTOM));
        registerDuplicateInput(BlockMapping.of("minecraft:wooden_slab", "data", 8, ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.TOP));
        registerDuplicateInput(BlockMapping.of("minecraft:double_wooden_slab", "data", 0, ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        // Stone slabs
        register(BlockMapping.flatten("minecraft:stone_slab", "data", VanillaBlockStates.SLAB_TYPE,
                ImmutableMultimap.<Integer, Pair<SlabType, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.SMOOTH_STONE_SLAB))
                        .put(1, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.SANDSTONE_SLAB))
                        .put(3, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.COBBLESTONE_SLAB))
                        .put(4, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.BRICK_SLAB))
                        .put(5, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.STONE_BRICK_SLAB))
                        .put(6, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.QUARTZ_SLAB))
                        .put(7, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.NETHER_BRICK_SLAB))
                        .put(8, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.SMOOTH_STONE_SLAB))
                        .put(9, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.SANDSTONE_SLAB))
                        .put(11, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.COBBLESTONE_SLAB))
                        .put(12, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.BRICK_SLAB))
                        .put(13, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.STONE_BRICK_SLAB))
                        .put(14, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.QUARTZ_SLAB))
                        .put(15, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.NETHER_BRICK_SLAB))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:stone_slab", "data", 2, ChunkerVanillaBlockType.OAK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.BOTTOM));
        registerDuplicateOutput(BlockMapping.of("minecraft:stone_slab", "data", 10, ChunkerVanillaBlockType.OAK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.TOP));

        register(BlockMapping.flatten("minecraft:double_stone_slab", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.SMOOTH_STONE_SLAB)
                        .put(1, ChunkerVanillaBlockType.SANDSTONE_SLAB)
                        .put(3, ChunkerVanillaBlockType.COBBLESTONE_SLAB)
                        .put(4, ChunkerVanillaBlockType.BRICK_SLAB)
                        .put(5, ChunkerVanillaBlockType.STONE_BRICK_SLAB)
                        .put(6, ChunkerVanillaBlockType.QUARTZ_SLAB)
                        .put(7, ChunkerVanillaBlockType.NETHER_BRICK_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:double_stone_slab", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(2, ChunkerVanillaBlockType.OAK_SLAB) // Duplicate
                        .put(8, ChunkerVanillaBlockType.SMOOTH_STONE_SLAB)
                        .put(9, ChunkerVanillaBlockType.SANDSTONE_SLAB)
                        .put(10, ChunkerVanillaBlockType.OAK_SLAB)
                        .put(11, ChunkerVanillaBlockType.COBBLESTONE_SLAB)
                        .put(12, ChunkerVanillaBlockType.BRICK_SLAB)
                        .put(13, ChunkerVanillaBlockType.STONE_BRICK_SLAB)
                        .put(14, ChunkerVanillaBlockType.QUARTZ_SLAB)
                        .put(15, ChunkerVanillaBlockType.NETHER_BRICK_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        register(BlockMapping.flatten("minecraft:stone_slab2", "data", VanillaBlockStates.SLAB_TYPE,
                ImmutableMultimap.<Integer, Pair<SlabType, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.RED_SANDSTONE_SLAB))
                        .put(1, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.PURPUR_SLAB))
                        .put(2, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.PRISMARINE_SLAB))
                        .put(3, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB))
                        .put(4, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB))
                        .put(5, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB))
                        .put(6, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB))
                        .put(7, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB))
                        .put(8, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.RED_SANDSTONE_SLAB))
                        .put(9, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.PURPUR_SLAB))
                        .put(10, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.PRISMARINE_SLAB))
                        .put(11, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB))
                        .put(12, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB))
                        .put(13, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB))
                        .put(14, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB))
                        .put(15, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB))
                        .build()
        ));

        register(BlockMapping.flatten("minecraft:double_stone_slab2", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.RED_SANDSTONE_SLAB)
                        .put(1, ChunkerVanillaBlockType.PURPUR_SLAB)
                        .put(2, ChunkerVanillaBlockType.PRISMARINE_SLAB)
                        .put(3, ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB)
                        .put(4, ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB)
                        .put(5, ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB)
                        .put(6, ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB)
                        .put(7, ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:double_stone_slab2", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(8, ChunkerVanillaBlockType.RED_SANDSTONE_SLAB)
                        .put(9, ChunkerVanillaBlockType.PURPUR_SLAB)
                        .put(10, ChunkerVanillaBlockType.PRISMARINE_SLAB)
                        .put(11, ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB)
                        .put(12, ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB)
                        .put(13, ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB)
                        .put(14, ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB)
                        .put(15, ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        register(BlockMapping.flatten("minecraft:stone_slab3", "data", VanillaBlockStates.SLAB_TYPE,
                ImmutableMultimap.<Integer, Pair<SlabType, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.END_STONE_BRICK_SLAB))
                        .put(1, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB))
                        .put(2, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB))
                        .put(3, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.ANDESITE_SLAB))
                        .put(4, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.DIORITE_SLAB))
                        .put(5, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB))
                        .put(6, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.GRANITE_SLAB))
                        .put(7, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB))
                        .put(8, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.END_STONE_BRICK_SLAB))
                        .put(9, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB))
                        .put(10, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB))
                        .put(11, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.ANDESITE_SLAB))
                        .put(12, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.DIORITE_SLAB))
                        .put(13, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB))
                        .put(14, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.GRANITE_SLAB))
                        .put(15, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB))
                        .build()
        ));
        register(BlockMapping.flatten("minecraft:double_stone_slab3", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.END_STONE_BRICK_SLAB)
                        .put(1, ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB)
                        .put(2, ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB)
                        .put(3, ChunkerVanillaBlockType.ANDESITE_SLAB)
                        .put(4, ChunkerVanillaBlockType.DIORITE_SLAB)
                        .put(5, ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB)
                        .put(6, ChunkerVanillaBlockType.GRANITE_SLAB)
                        .put(7, ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:double_stone_slab3", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(8, ChunkerVanillaBlockType.END_STONE_BRICK_SLAB)
                        .put(9, ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB)
                        .put(10, ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB)
                        .put(11, ChunkerVanillaBlockType.ANDESITE_SLAB)
                        .put(12, ChunkerVanillaBlockType.DIORITE_SLAB)
                        .put(13, ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB)
                        .put(14, ChunkerVanillaBlockType.GRANITE_SLAB)
                        .put(15, ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        register(BlockMapping.flatten("minecraft:stone_slab4", "data", VanillaBlockStates.SLAB_TYPE,
                ImmutableMultimap.<Integer, Pair<SlabType, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB))
                        .put(1, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB))
                        .put(2, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.STONE_SLAB))
                        .put(3, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB))
                        .put(4, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB))
                        .put(8, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB))
                        .put(9, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB))
                        .put(10, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.STONE_SLAB))
                        .put(11, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB))
                        .put(12, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:stone_slab4", "data", VanillaBlockStates.SLAB_TYPE,
                ImmutableMultimap.<Integer, Pair<SlabType, ? extends ChunkerBlockType>>builder()
                        .put(5, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB))
                        .put(6, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB))
                        .put(7, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB))
                        .put(13, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB))
                        .put(14, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB))
                        .put(15, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB))
                        .build()
        ));
        register(BlockMapping.flatten("minecraft:double_stone_slab4", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                        .put(1, ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB)
                        .put(2, ChunkerVanillaBlockType.STONE_SLAB)
                        .put(3, ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB)
                        .put(4, ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:double_stone_slab4", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(5, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                        .put(6, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                        .put(7, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                        .put(8, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                        .put(9, ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB)
                        .put(10, ChunkerVanillaBlockType.STONE_SLAB)
                        .put(11, ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB)
                        .put(12, ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB)
                        .put(13, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                        .put(14, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                        .put(15, ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        // Sea lantern (with legacy alias)
        register(BlockMapping.of("minecraft:seaLantern", ChunkerVanillaBlockType.SEA_LANTERN));
        registerDuplicateOutput(BlockMapping.of("minecraft:sealantern", ChunkerVanillaBlockType.SEA_LANTERN));

        // Trip wire (with legacy alias)
        register(BlockMapping.of("minecraft:tripWire", ChunkerVanillaBlockType.TRIPWIRE, BedrockLegacyStateGroups.TRIPWIRE));
        registerDuplicateOutput(BlockMapping.of("minecraft:tripwire", ChunkerVanillaBlockType.TRIPWIRE, BedrockLegacyStateGroups.TRIPWIRE));

        // Moving block is technical block which isn't equivalent to moving_piston
        registerDuplicateInput(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.MOVING_PISTON_JAVA));
        register(BlockMapping.of("minecraft:movingBlock", ChunkerVanillaBlockType.MOVING_BLOCK_BEDROCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:movingblock", ChunkerVanillaBlockType.MOVING_BLOCK_BEDROCK));

        // Invisible bedrock (with legacy alias)
        register(BlockMapping.of("minecraft:invisibleBedrock", ChunkerVanillaBlockType.INVISIBLE_BEDROCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:invisiblebedrock", ChunkerVanillaBlockType.INVISIBLE_BEDROCK));
    }
}
