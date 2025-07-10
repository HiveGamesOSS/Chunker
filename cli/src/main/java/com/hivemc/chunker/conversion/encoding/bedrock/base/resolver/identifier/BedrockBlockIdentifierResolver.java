package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier;

import com.google.common.collect.ImmutableMultimap;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.BlockMapping;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.ChunkerBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateMappingGroup;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.*;
import it.unimi.dsi.fastutil.Pair;

import java.util.Map;

/**
 * Resolver to convert between Bedrock block identifiers and ChunkerBlockIdentifier.
 * Note: Waterlogging is automatically handled by this resolver and states don't need to be handled.
 */
public class BedrockBlockIdentifierResolver extends ChunkerBlockIdentifierResolver {
    /**
     * The default group for waterlogged states.
     */
    public static final StateMappingGroup WATERLOGGED = new StateMappingGroup.Builder()
            .state("waterlogged", VanillaBlockStates.WATERLOGGED, BedrockStateTypes.BOOL_DEFAULT_FALSE)
            .build();

    /**
     * Create a new bedrock block identifier resolver.
     *
     * @param converter                the converter instance.
     * @param version                  the version being resolved.
     * @param reader                   whether this is used for the reader.
     * @param customIdentifiersAllowed whether unknown identifiers should be converted to custom identifiers.
     */
    public BedrockBlockIdentifierResolver(Converter converter, Version version, boolean reader, boolean customIdentifiersAllowed) {
        super(converter, version, reader, customIdentifiersAllowed);
    }

    @Override
    public void registerMappings(Version version) {
        // Waterlogging on Bedrock is allowed on every block, so we use an extra state handler to infer this
        extraStateMappingGroup(WATERLOGGED);

        // Mappings
        register(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.AIR));
        registerDuplicateInput(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.CAVE_AIR));
        registerDuplicateInput(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.VOID_AIR));

        register(BlockMapping.group("growth", 7, ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:pumpkin_stem", ChunkerVanillaBlockType.ATTACHED_PUMPKIN_STEM)
                        .put("minecraft:melon_stem", ChunkerVanillaBlockType.ATTACHED_MELON_STEM)
                        .build(),
                BedrockStateGroups.STEM_FACING));
        register(BlockMapping.of("minecraft:bamboo", ChunkerVanillaBlockType.BAMBOO, BedrockStateGroups.BAMBOO));

        // Bamboo sampling
        register(BlockMapping.of("minecraft:bamboo_sapling", ChunkerVanillaBlockType.BAMBOO_SAPLING, BedrockStateGroups.BAMBOO_SAPLING));

        register(BlockMapping.of("minecraft:barrel", ChunkerVanillaBlockType.BARREL, BedrockStateGroups.BARREL));
        register(BlockMapping.of("minecraft:barrier", ChunkerVanillaBlockType.BARRIER));
        register(BlockMapping.of("minecraft:beacon", ChunkerVanillaBlockType.BEACON));

        // Bedrock
        register(BlockMapping.of("minecraft:bedrock", ChunkerVanillaBlockType.BEDROCK, BedrockStateGroups.BEDROCK));

        register(BlockMapping.of("minecraft:bell", ChunkerVanillaBlockType.BELL, BedrockStateGroups.BELL));
        register(BlockMapping.of("minecraft:blue_ice", ChunkerVanillaBlockType.BLUE_ICE));
        register(BlockMapping.of("minecraft:bookshelf", ChunkerVanillaBlockType.BOOKSHELF));
        register(BlockMapping.of("minecraft:brewing_stand", ChunkerVanillaBlockType.BREWING_STAND, BedrockStateGroups.BREWING_STAND));
        register(BlockMapping.of("minecraft:brick_block", ChunkerVanillaBlockType.BRICKS));
        register(BlockMapping.of("minecraft:brown_mushroom", ChunkerVanillaBlockType.BROWN_MUSHROOM));
        register(BlockMapping.flatten("minecraft:leaves2", "new_leaf_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("dark_oak", ChunkerVanillaBlockType.DARK_OAK_LEAVES)
                        .put("acacia", ChunkerVanillaBlockType.ACACIA_LEAVES)
                        .build(),
                BedrockStateGroups.LEAVES));

        // Bubble columns
        register(BlockMapping.of("minecraft:bubble_column", ChunkerVanillaBlockType.BUBBLE_COLUMN, BedrockStateGroups.BUBBLE_COLUMN));

        register(BlockMapping.of("minecraft:cake", ChunkerVanillaBlockType.CAKE, BedrockStateGroups.CAKE));
        register(BlockMapping.of("minecraft:campfire", ChunkerVanillaBlockType.CAMPFIRE, BedrockStateGroups.CAMPFIRE));
        register(BlockMapping.of("minecraft:cartography_table", ChunkerVanillaBlockType.CARTOGRAPHY_TABLE));

        // Anvil
        register(BlockMapping.flatten("minecraft:anvil", "damage",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("undamaged", ChunkerVanillaBlockType.ANVIL)
                        .put("slightly_damaged", ChunkerVanillaBlockType.CHIPPED_ANVIL)
                        .put("very_damaged", ChunkerVanillaBlockType.DAMAGED_ANVIL)
                        .build(),
                BedrockStateGroups.ANVIL));
        registerDuplicateOutput(BlockMapping.of("minecraft:anvil", "damage", "broken", ChunkerVanillaBlockType.DAMAGED_ANVIL, BedrockStateGroups.ANVIL));

        register(BlockMapping.of("minecraft:chorus_flower", ChunkerVanillaBlockType.CHORUS_FLOWER, BedrockStateGroups.CHORUS_FLOWER));
        register(BlockMapping.of("minecraft:chorus_plant", ChunkerVanillaBlockType.CHORUS_PLANT, BedrockStateGroups.CHORUS_PLANT));
        register(BlockMapping.of("minecraft:clay", ChunkerVanillaBlockType.CLAY));
        register(BlockMapping.of("minecraft:coal_block", ChunkerVanillaBlockType.COAL_BLOCK));
        register(BlockMapping.of("minecraft:coal_ore", ChunkerVanillaBlockType.COAL_ORE));
        register(BlockMapping.of("minecraft:cobblestone", ChunkerVanillaBlockType.COBBLESTONE));
        register(BlockMapping.of("minecraft:web", ChunkerVanillaBlockType.COBWEB));
        register(BlockMapping.of("minecraft:cocoa", ChunkerVanillaBlockType.COCOA, BedrockStateGroups.COCOA));

        // Comparators
        register(BlockMapping.of("minecraft:powered_comparator", ChunkerVanillaBlockType.COMPARATOR, BedrockStateGroups.COMPARATOR, VanillaBlockStates.POWERED, Bool.TRUE));
        register(BlockMapping.of("minecraft:unpowered_comparator", ChunkerVanillaBlockType.COMPARATOR, BedrockStateGroups.COMPARATOR, VanillaBlockStates.POWERED, Bool.FALSE));

        register(BlockMapping.of("minecraft:composter", ChunkerVanillaBlockType.COMPOSTER, BedrockStateGroups.COMPOSTER));
        register(BlockMapping.of("minecraft:conduit", ChunkerVanillaBlockType.CONDUIT));
        register(BlockMapping.of("minecraft:crafting_table", ChunkerVanillaBlockType.CRAFTING_TABLE));
        register(BlockMapping.of("minecraft:yellow_flower", ChunkerVanillaBlockType.DANDELION));

        // Daylight detector
        register(BlockMapping.of("minecraft:daylight_detector_inverted", ChunkerVanillaBlockType.DAYLIGHT_DETECTOR, BedrockStateGroups.DAYLIGHT_DETECTOR, VanillaBlockStates.INVERTED, Bool.TRUE));
        register(BlockMapping.of("minecraft:daylight_detector", ChunkerVanillaBlockType.DAYLIGHT_DETECTOR, BedrockStateGroups.DAYLIGHT_DETECTOR, VanillaBlockStates.INVERTED, Bool.FALSE));

        register(BlockMapping.of("minecraft:deadbush", ChunkerVanillaBlockType.DEAD_BUSH));

        // Empty Cauldron
        register(BlockMapping.of("minecraft:cauldron", "fill_level", 0, ChunkerVanillaBlockType.CAULDRON));
        registerDuplicateOutput(BlockMapping.of("minecraft:cauldron", ChunkerVanillaBlockType.CAULDRON)); // Item form
        registerOverrideOutput(BlockMapping.of("minecraft:cauldron", "cauldron_liquid", "water", "fill_level", 0, ChunkerVanillaBlockType.CAULDRON));

        // Water Cauldron
        register(BlockMapping.of("minecraft:cauldron", "cauldron_liquid", "water", ChunkerVanillaBlockType.WATER_CAULDRON, BedrockStateGroups.CAULDRON));

        // Lava Cauldron
        register(BlockMapping.of("minecraft:cauldron", "cauldron_liquid", "lava", ChunkerVanillaBlockType.LAVA_CAULDRON, BedrockStateGroups.CAULDRON));

        // minecraft:lava_cauldron is removed in later game versions, so we avoid mapping to it
        registerDuplicateOutput(BlockMapping.of("minecraft:lava_cauldron", ChunkerVanillaBlockType.LAVA_CAULDRON, BedrockStateGroups.CAULDRON));
        registerDuplicateOutput(BlockMapping.of("minecraft:lava_cauldron", "fill_level", 0, ChunkerVanillaBlockType.CAULDRON));

        register(BlockMapping.of("minecraft:diamond_block", ChunkerVanillaBlockType.DIAMOND_BLOCK));
        register(BlockMapping.of("minecraft:diamond_ore", ChunkerVanillaBlockType.DIAMOND_ORE));

        // Dirt
        register(BlockMapping.flatten("minecraft:dirt", "dirt_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("coarse", ChunkerVanillaBlockType.COARSE_DIRT)
                        .put("normal", ChunkerVanillaBlockType.DIRT)
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
                BedrockStateGroups.FACING_DIRECTION_TRIGGERED));
        register(BlockMapping.of("minecraft:dried_kelp_block", ChunkerVanillaBlockType.DRIED_KELP_BLOCK));
        register(BlockMapping.of("minecraft:emerald_block", ChunkerVanillaBlockType.EMERALD_BLOCK));
        register(BlockMapping.of("minecraft:emerald_ore", ChunkerVanillaBlockType.EMERALD_ORE));
        register(BlockMapping.of("minecraft:enchanting_table", ChunkerVanillaBlockType.ENCHANTING_TABLE));
        register(BlockMapping.of("minecraft:end_gateway", ChunkerVanillaBlockType.END_GATEWAY));
        register(BlockMapping.of("minecraft:end_portal", ChunkerVanillaBlockType.END_PORTAL));
        register(BlockMapping.of("minecraft:end_portal_frame", ChunkerVanillaBlockType.END_PORTAL_FRAME, BedrockStateGroups.END_PORTAL_FRAME));
        register(BlockMapping.of("minecraft:end_stone", ChunkerVanillaBlockType.END_STONE));
        register(BlockMapping.of("minecraft:end_bricks", ChunkerVanillaBlockType.END_STONE_BRICKS));
        register(BlockMapping.of("minecraft:farmland", ChunkerVanillaBlockType.FARMLAND, BedrockStateGroups.FARMLAND));
        register(BlockMapping.of("minecraft:fire", ChunkerVanillaBlockType.FIRE, BedrockStateGroups.FIRE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:golden_rail", ChunkerVanillaBlockType.POWERED_RAIL)
                        .put("minecraft:activator_rail", ChunkerVanillaBlockType.ACTIVATOR_RAIL)
                        .put("minecraft:detector_rail", ChunkerVanillaBlockType.DETECTOR_RAIL)
                        .build(),
                BedrockStateGroups.POWERED_RAIL));
        register(BlockMapping.of("minecraft:fletching_table", ChunkerVanillaBlockType.FLETCHING_TABLE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:dark_oak_fence_gate", ChunkerVanillaBlockType.DARK_OAK_FENCE_GATE)
                        .put("minecraft:fence_gate", ChunkerVanillaBlockType.OAK_FENCE_GATE)
                        .put("minecraft:acacia_fence_gate", ChunkerVanillaBlockType.ACACIA_FENCE_GATE)
                        .put("minecraft:jungle_fence_gate", ChunkerVanillaBlockType.JUNGLE_FENCE_GATE)
                        .put("minecraft:birch_fence_gate", ChunkerVanillaBlockType.BIRCH_FENCE_GATE)
                        .put("minecraft:spruce_fence_gate", ChunkerVanillaBlockType.SPRUCE_FENCE_GATE)
                        .build(),
                BedrockStateGroups.FENCE_GATE));
        register(BlockMapping.of("minecraft:glass", ChunkerVanillaBlockType.GLASS));
        register(BlockMapping.of("minecraft:glowstone", ChunkerVanillaBlockType.GLOWSTONE));
        register(BlockMapping.of("minecraft:gold_block", ChunkerVanillaBlockType.GOLD_BLOCK));
        register(BlockMapping.of("minecraft:gold_ore", ChunkerVanillaBlockType.GOLD_ORE));

        // Doors
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:wooden_door", ChunkerVanillaBlockType.OAK_DOOR)
                        .put("minecraft:dark_oak_door", ChunkerVanillaBlockType.DARK_OAK_DOOR)
                        .put("minecraft:acacia_door", ChunkerVanillaBlockType.ACACIA_DOOR)
                        .put("minecraft:iron_door", ChunkerVanillaBlockType.IRON_DOOR)
                        .put("minecraft:jungle_door", ChunkerVanillaBlockType.JUNGLE_DOOR)
                        .put("minecraft:spruce_door", ChunkerVanillaBlockType.SPRUCE_DOOR)
                        .put("minecraft:birch_door", ChunkerVanillaBlockType.BIRCH_DOOR)
                        .build(),
                BedrockStateGroups.DOOR));

        register(BlockMapping.flatten("minecraft:sapling", "sapling_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("oak", ChunkerVanillaBlockType.OAK_SAPLING)
                        .put("spruce", ChunkerVanillaBlockType.SPRUCE_SAPLING)
                        .put("birch", ChunkerVanillaBlockType.BIRCH_SAPLING)
                        .put("dark_oak", ChunkerVanillaBlockType.DARK_OAK_SAPLING)
                        .put("acacia", ChunkerVanillaBlockType.ACACIA_SAPLING)
                        .put("jungle", ChunkerVanillaBlockType.JUNGLE_SAPLING)
                        .build(),
                BedrockStateGroups.SAPLING));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:birch_standing_sign", ChunkerVanillaBlockType.BIRCH_SIGN)
                        .put("minecraft:acacia_standing_sign", ChunkerVanillaBlockType.ACACIA_SIGN)
                        .put("minecraft:spruce_standing_sign", ChunkerVanillaBlockType.SPRUCE_SIGN)
                        .put("minecraft:jungle_standing_sign", ChunkerVanillaBlockType.JUNGLE_SIGN)
                        .put("minecraft:darkoak_standing_sign", ChunkerVanillaBlockType.DARK_OAK_SIGN)
                        .put("minecraft:standing_sign", ChunkerVanillaBlockType.OAK_SIGN)
                        .build(),
                BedrockStateGroups.SIGN));
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
                BedrockStateGroups.TRAPDOOR));

        // Command Blocks
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:repeating_command_block", ChunkerVanillaBlockType.REPEATING_COMMAND_BLOCK)
                        .put("minecraft:chain_command_block", ChunkerVanillaBlockType.CHAIN_COMMAND_BLOCK)
                        .put("minecraft:command_block", ChunkerVanillaBlockType.COMMAND_BLOCK)
                        .build(),
                BedrockStateGroups.COMMAND_BLOCK));

        // Pumpkins
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:carved_pumpkin", ChunkerVanillaBlockType.CARVED_PUMPKIN)
                        .put("minecraft:lit_pumpkin", ChunkerVanillaBlockType.JACK_O_LANTERN)
                        .build(),
                BedrockStateGroups.PUMPKIN));

        register(BlockMapping.of("minecraft:ender_chest", ChunkerVanillaBlockType.ENDER_CHEST, BedrockStateGroups.ENDER_CHEST));
        register(BlockMapping.of("minecraft:grindstone", ChunkerVanillaBlockType.GRINDSTONE, BedrockStateGroups.GRINDSTONE));
        register(BlockMapping.of("minecraft:hopper", ChunkerVanillaBlockType.HOPPER, BedrockStateGroups.HOPPER));
        register(BlockMapping.of("minecraft:stonecutter_block", ChunkerVanillaBlockType.STONECUTTER, BedrockStateGroups.STONECUTTER));


        register(BlockMapping.of("minecraft:ice", ChunkerVanillaBlockType.ICE));
        register(BlockMapping.flatten("minecraft:monster_egg", "monster_egg_stone_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("mossy_stone_brick", ChunkerVanillaBlockType.INFESTED_MOSSY_STONE_BRICKS)
                        .put("cobblestone", ChunkerVanillaBlockType.INFESTED_COBBLESTONE)
                        .put("stone", ChunkerVanillaBlockType.INFESTED_STONE)
                        .put("stone_brick", ChunkerVanillaBlockType.INFESTED_STONE_BRICKS)
                        .put("cracked_stone_brick", ChunkerVanillaBlockType.INFESTED_CRACKED_STONE_BRICKS)
                        .put("chiseled_stone_brick", ChunkerVanillaBlockType.INFESTED_CHISELED_STONE_BRICKS)
                        .build()
        ));
        register(BlockMapping.of("minecraft:iron_block", ChunkerVanillaBlockType.IRON_BLOCK));
        register(BlockMapping.of("minecraft:iron_ore", ChunkerVanillaBlockType.IRON_ORE));
        register(BlockMapping.of("minecraft:jigsaw", ChunkerVanillaBlockType.JIGSAW, BedrockStateGroups.JIGSAW));
        register(BlockMapping.of("minecraft:jukebox", ChunkerVanillaBlockType.JUKEBOX, BedrockStateGroups.JUKEBOX));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:lit_smoker", ChunkerVanillaBlockType.SMOKER)
                        .put("minecraft:lit_furnace", ChunkerVanillaBlockType.FURNACE)
                        .put("minecraft:lit_blast_furnace", ChunkerVanillaBlockType.BLAST_FURNACE)
                        .build(),
                BedrockStateGroups.CARDINAL_DIRECTION, VanillaBlockStates.LIT, Bool.TRUE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:smoker", ChunkerVanillaBlockType.SMOKER)
                        .put("minecraft:blast_furnace", ChunkerVanillaBlockType.BLAST_FURNACE)
                        .put("minecraft:furnace", ChunkerVanillaBlockType.FURNACE)
                        .build(),
                BedrockStateGroups.CARDINAL_DIRECTION, VanillaBlockStates.LIT, Bool.FALSE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:light_weighted_pressure_plate", ChunkerVanillaBlockType.LIGHT_WEIGHTED_PRESSURE_PLATE)
                        .put("minecraft:heavy_weighted_pressure_plate", ChunkerVanillaBlockType.HEAVY_WEIGHTED_PRESSURE_PLATE)
                        .build(),
                BedrockStateGroups.WEIGHTED_PRESSURE_PLATE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:wooden_button", ChunkerVanillaBlockType.OAK_BUTTON)
                        .put("minecraft:jungle_button", ChunkerVanillaBlockType.JUNGLE_BUTTON)
                        .put("minecraft:dark_oak_button", ChunkerVanillaBlockType.DARK_OAK_BUTTON)
                        .put("minecraft:acacia_button", ChunkerVanillaBlockType.ACACIA_BUTTON)
                        .put("minecraft:spruce_button", ChunkerVanillaBlockType.SPRUCE_BUTTON)
                        .put("minecraft:stone_button", ChunkerVanillaBlockType.STONE_BUTTON)
                        .put("minecraft:birch_button", ChunkerVanillaBlockType.BIRCH_BUTTON)
                        .build(),
                BedrockStateGroups.BUTTON));
        register(BlockMapping.of("minecraft:kelp", ChunkerVanillaBlockType.KELP, BedrockStateGroups.KELP));
        register(BlockMapping.of("minecraft:kelp", "age", 15, ChunkerVanillaBlockType.KELP_PLANT));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:nether_brick_fence", ChunkerVanillaBlockType.NETHER_BRICK_FENCE)
                        .put("minecraft:glass_pane", ChunkerVanillaBlockType.GLASS_PANE)
                        .put("minecraft:iron_bars", ChunkerVanillaBlockType.IRON_BARS)
                        .build(),
                BedrockStateGroups.CONNECTABLE_HORIZONTAL));
        register(BlockMapping.of("minecraft:ladder", ChunkerVanillaBlockType.LADDER, BedrockStateGroups.FACING_DIRECTION_HORIZONTAL));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:jungle_pressure_plate", ChunkerVanillaBlockType.JUNGLE_PRESSURE_PLATE)
                        .put("minecraft:acacia_pressure_plate", ChunkerVanillaBlockType.ACACIA_PRESSURE_PLATE)
                        .put("minecraft:birch_pressure_plate", ChunkerVanillaBlockType.BIRCH_PRESSURE_PLATE)
                        .put("minecraft:wooden_pressure_plate", ChunkerVanillaBlockType.OAK_PRESSURE_PLATE)
                        .put("minecraft:spruce_pressure_plate", ChunkerVanillaBlockType.SPRUCE_PRESSURE_PLATE)
                        .put("minecraft:stone_pressure_plate", ChunkerVanillaBlockType.STONE_PRESSURE_PLATE)
                        .put("minecraft:dark_oak_pressure_plate", ChunkerVanillaBlockType.DARK_OAK_PRESSURE_PLATE)
                        .build(),
                BedrockStateGroups.PRESSURE_PLATE));
        register(BlockMapping.of("minecraft:lantern", ChunkerVanillaBlockType.LANTERN, BedrockStateGroups.LANTERN));
        register(BlockMapping.of("minecraft:lapis_block", ChunkerVanillaBlockType.LAPIS_BLOCK));
        register(BlockMapping.of("minecraft:lapis_ore", ChunkerVanillaBlockType.LAPIS_ORE));
        register(BlockMapping.of("minecraft:lectern", ChunkerVanillaBlockType.LECTERN, BedrockStateGroups.LECTERN));
        register(BlockMapping.of("minecraft:lever", ChunkerVanillaBlockType.LEVER, BedrockStateGroups.LEVER));

        // Stone brick
        register(BlockMapping.flatten("minecraft:stonebrick", "stone_brick_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("default", ChunkerVanillaBlockType.STONE_BRICKS)
                        .put("mossy", ChunkerVanillaBlockType.MOSSY_STONE_BRICKS)
                        .put("cracked", ChunkerVanillaBlockType.CRACKED_STONE_BRICKS)
                        .put("chiseled", ChunkerVanillaBlockType.CHISELED_STONE_BRICKS)
                        .build()
        ));
        // Smooth is the same texture as the default
        registerDuplicateOutput(BlockMapping.of("minecraft:stonebrick", "stone_brick_type", "smooth", ChunkerVanillaBlockType.STONE_BRICKS));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:frosted_ice", ChunkerVanillaBlockType.FROSTED_ICE)
                        .put("minecraft:nether_wart", ChunkerVanillaBlockType.NETHER_WART)
                        .build(),
                BedrockStateGroups.AGE_3));
        register(BlockMapping.flatten("minecraft:red_flower", "flower_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("tulip_white", ChunkerVanillaBlockType.WHITE_TULIP)
                        .put("poppy", ChunkerVanillaBlockType.POPPY)
                        .put("oxeye", ChunkerVanillaBlockType.OXEYE_DAISY)
                        .put("cornflower", ChunkerVanillaBlockType.CORNFLOWER)
                        .put("tulip_orange", ChunkerVanillaBlockType.ORANGE_TULIP)
                        .put("lily_of_the_valley", ChunkerVanillaBlockType.LILY_OF_THE_VALLEY)
                        .put("tulip_pink", ChunkerVanillaBlockType.PINK_TULIP)
                        .put("houstonia", ChunkerVanillaBlockType.AZURE_BLUET)
                        .put("allium", ChunkerVanillaBlockType.ALLIUM)
                        .put("tulip_red", ChunkerVanillaBlockType.RED_TULIP)
                        .put("orchid", ChunkerVanillaBlockType.BLUE_ORCHID)
                        .build()
        ));
        register(BlockMapping.of("minecraft:waterlily", ChunkerVanillaBlockType.LILY_PAD));
        register(BlockMapping.flatten("minecraft:leaves", "old_leaf_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("oak", ChunkerVanillaBlockType.OAK_LEAVES)
                        .put("birch", ChunkerVanillaBlockType.BIRCH_LEAVES)
                        .put("spruce", ChunkerVanillaBlockType.SPRUCE_LEAVES)
                        .put("jungle", ChunkerVanillaBlockType.JUNGLE_LEAVES)
                        .build(),
                BedrockStateGroups.LEAVES));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:beetroot", ChunkerVanillaBlockType.BEETROOTS)
                        .put("minecraft:sweet_berry_bush", ChunkerVanillaBlockType.SWEET_BERRY_BUSH)
                        .build(),
                BedrockStateGroups.GROWTH));
        register(BlockMapping.of("minecraft:loom", ChunkerVanillaBlockType.LOOM, BedrockStateGroups.LOOM));

        // Standing banners
        register(BlockMapping.of("minecraft:standing_banner", ChunkerVanillaBlockType.WHITE_BANNER, BedrockStateGroups.BANNER));
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
                BedrockStateGroups.BANNER));

        // Wall banners
        register(BlockMapping.of("minecraft:wall_banner", ChunkerVanillaBlockType.WHITE_WALL_BANNER, BedrockStateGroups.FACING_DIRECTION_HORIZONTAL));
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
                BedrockStateGroups.FACING_DIRECTION_HORIZONTAL));

        // Beds
        register(BlockMapping.of("minecraft:bed", ChunkerVanillaBlockType.WHITE_BED, BedrockStateGroups.BED));
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
                BedrockStateGroups.BED));

        // Walls
        register(BlockMapping.flatten("minecraft:cobblestone_wall", "wall_block_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("nether_brick", ChunkerVanillaBlockType.NETHER_BRICK_WALL)
                        .put("prismarine", ChunkerVanillaBlockType.PRISMARINE_WALL)
                        .put("diorite", ChunkerVanillaBlockType.DIORITE_WALL)
                        .put("stone_brick", ChunkerVanillaBlockType.STONE_BRICK_WALL)
                        .put("andesite", ChunkerVanillaBlockType.ANDESITE_WALL)
                        .put("granite", ChunkerVanillaBlockType.GRANITE_WALL)
                        .put("brick", ChunkerVanillaBlockType.BRICK_WALL)
                        .put("end_brick", ChunkerVanillaBlockType.END_STONE_BRICK_WALL)
                        .put("red_nether_brick", ChunkerVanillaBlockType.RED_NETHER_BRICK_WALL)
                        .put("cobblestone", ChunkerVanillaBlockType.COBBLESTONE_WALL)
                        .put("red_sandstone", ChunkerVanillaBlockType.RED_SANDSTONE_WALL)
                        .put("mossy_stone_brick", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_WALL)
                        .put("mossy_cobblestone", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_WALL)
                        .put("sandstone", ChunkerVanillaBlockType.SANDSTONE_WALL)
                        .build(),
                BedrockStateGroups.WALL));

        register(BlockMapping.of("minecraft:magma", ChunkerVanillaBlockType.MAGMA_BLOCK));

        // Sandstone
        register(BlockMapping.flatten("minecraft:sandstone", "sand_stone_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("smooth", ChunkerVanillaBlockType.SMOOTH_SANDSTONE)
                        .put("cut", ChunkerVanillaBlockType.CUT_SANDSTONE)
                        .put("heiroglyphs", ChunkerVanillaBlockType.CHISELED_SANDSTONE)
                        .put("default", ChunkerVanillaBlockType.SANDSTONE)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:sandstone", ChunkerVanillaBlockType.SANDSTONE));

        // Red Sandstone
        register(BlockMapping.flatten("minecraft:red_sandstone", "sand_stone_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("smooth", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE)
                        .put("cut", ChunkerVanillaBlockType.CUT_RED_SANDSTONE)
                        .put("default", ChunkerVanillaBlockType.RED_SANDSTONE)
                        .put("heiroglyphs", ChunkerVanillaBlockType.CHISELED_RED_SANDSTONE)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:red_sandstone", ChunkerVanillaBlockType.RED_SANDSTONE));

        // Sand
        register(BlockMapping.flatten("minecraft:sand", "sand_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("normal", ChunkerVanillaBlockType.SAND)
                        .put("red", ChunkerVanillaBlockType.RED_SAND)
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
                BedrockStateGroups.FACING_DIRECTION_HORIZONTAL));

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
                BedrockStateGroups.FACING_DIRECTION_HORIZONTAL));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:grass", ChunkerVanillaBlockType.GRASS_BLOCK)
                        .put("minecraft:podzol", ChunkerVanillaBlockType.PODZOL)
                        .put("minecraft:mycelium", ChunkerVanillaBlockType.MYCELIUM)
                        .build(),
                BedrockStateGroups.SNOWY_BLOCK));
        register(BlockMapping.of("minecraft:melon_block", ChunkerVanillaBlockType.MELON));
        register(BlockMapping.of("minecraft:mossy_cobblestone", ChunkerVanillaBlockType.MOSSY_COBBLESTONE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:cactus", ChunkerVanillaBlockType.CACTUS)
                        .put("minecraft:reeds", ChunkerVanillaBlockType.SUGAR_CANE)
                        .build(),
                BedrockStateGroups.VERTICAL_GROWING));

        // Coral block
        register(BlockMapping.flatten("minecraft:coral_block", "coral_color", "dead_bit",
                ImmutableMultimap.<Pair<String, Boolean>, ChunkerVanillaBlockType>builder()
                        .put(Pair.of("pink", true), ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_BLOCK)
                        .put(Pair.of("yellow", false), ChunkerVanillaBlockType.HORN_CORAL_BLOCK)
                        .put(Pair.of("red", true), ChunkerVanillaBlockType.DEAD_FIRE_CORAL_BLOCK)
                        .put(Pair.of("purple", false), ChunkerVanillaBlockType.BUBBLE_CORAL_BLOCK)
                        .put(Pair.of("blue", false), ChunkerVanillaBlockType.TUBE_CORAL_BLOCK)
                        .put(Pair.of("blue", true), ChunkerVanillaBlockType.DEAD_TUBE_CORAL_BLOCK)
                        .put(Pair.of("red", false), ChunkerVanillaBlockType.FIRE_CORAL_BLOCK)
                        .put(Pair.of("yellow", true), ChunkerVanillaBlockType.DEAD_HORN_CORAL_BLOCK)
                        .put(Pair.of("pink", false), ChunkerVanillaBlockType.BRAIN_CORAL_BLOCK)
                        .put(Pair.of("purple", true), ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_BLOCK)
                        .build()
        ));

        // Coral
        register(BlockMapping.flatten("minecraft:coral", "coral_color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("red", ChunkerVanillaBlockType.FIRE_CORAL)
                        .put("blue", ChunkerVanillaBlockType.TUBE_CORAL)
                        .put("purple", ChunkerVanillaBlockType.BUBBLE_CORAL)
                        .put("yellow", ChunkerVanillaBlockType.HORN_CORAL)
                        .put("pink", ChunkerVanillaBlockType.BRAIN_CORAL)
                        .build()
        ));

        // Coral fan
        register(BlockMapping.flatten("minecraft:coral_fan", "coral_color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("blue", ChunkerVanillaBlockType.TUBE_CORAL_FAN)
                        .put("pink", ChunkerVanillaBlockType.BRAIN_CORAL_FAN)
                        .put("purple", ChunkerVanillaBlockType.BUBBLE_CORAL_FAN)
                        .put("red", ChunkerVanillaBlockType.FIRE_CORAL_FAN)
                        .put("yellow", ChunkerVanillaBlockType.HORN_CORAL_FAN)
                        .build(),
                BedrockStateGroups.CORAL_FAN));
        register(BlockMapping.flatten("minecraft:coral_fan_dead", "coral_color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("blue", ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN)
                        .put("pink", ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_FAN)
                        .put("yellow", ChunkerVanillaBlockType.DEAD_HORN_CORAL_FAN)
                        .put("red", ChunkerVanillaBlockType.DEAD_FIRE_CORAL_FAN)
                        .put("purple", ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_FAN)
                        .build(),
                BedrockStateGroups.CORAL_FAN));

        // Coral hanging
        register(BlockMapping.of("minecraft:coral_fan_hang", "dead_bit", false, "coral_hang_type_bit", true, ChunkerVanillaBlockType.BRAIN_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));
        register(BlockMapping.of("minecraft:coral_fan_hang", "dead_bit", false, "coral_hang_type_bit", false, ChunkerVanillaBlockType.TUBE_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));
        register(BlockMapping.of("minecraft:coral_fan_hang", "dead_bit", true, "coral_hang_type_bit", true, ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));
        register(BlockMapping.of("minecraft:coral_fan_hang", "dead_bit", true, "coral_hang_type_bit", false, ChunkerVanillaBlockType.DEAD_TUBE_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));

        register(BlockMapping.of("minecraft:coral_fan_hang2", "dead_bit", false, "coral_hang_type_bit", true, ChunkerVanillaBlockType.FIRE_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));
        register(BlockMapping.of("minecraft:coral_fan_hang2", "dead_bit", false, "coral_hang_type_bit", false, ChunkerVanillaBlockType.BUBBLE_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));
        register(BlockMapping.of("minecraft:coral_fan_hang2", "dead_bit", true, "coral_hang_type_bit", true, ChunkerVanillaBlockType.DEAD_FIRE_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));
        register(BlockMapping.of("minecraft:coral_fan_hang2", "dead_bit", true, "coral_hang_type_bit", false, ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));

        register(BlockMapping.of("minecraft:coral_fan_hang3", "dead_bit", false, "coral_hang_type_bit", false, ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));
        register(BlockMapping.of("minecraft:coral_fan_hang3", "dead_bit", true, "coral_hang_type_bit", false, ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));
        registerDuplicateOutput(BlockMapping.of("minecraft:coral_fan_hang3", "dead_bit", false, ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));
        registerDuplicateOutput(BlockMapping.of("minecraft:coral_fan_hang3", "dead_bit", true, ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN, BedrockStateGroups.CORAL_HANGING));

        // Mushroom / Mushroom Stem
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:brown_mushroom_block", ChunkerVanillaBlockType.BROWN_MUSHROOM_BLOCK)
                        .put("minecraft:red_mushroom_block", ChunkerVanillaBlockType.RED_MUSHROOM_BLOCK)
                        .build(),
                BedrockStateGroups.MUSHROOM_BLOCK));
        register(BlockMapping.of("minecraft:brown_mushroom_block", "huge_mushroom_bits", 15, ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                VanillaBlockStates.NORTH, Bool.TRUE,
                VanillaBlockStates.EAST, Bool.TRUE,
                VanillaBlockStates.SOUTH, Bool.TRUE,
                VanillaBlockStates.WEST, Bool.TRUE,
                VanillaBlockStates.UP, Bool.TRUE,
                VanillaBlockStates.DOWN, Bool.TRUE
        )));
        register(BlockMapping.of("minecraft:brown_mushroom_block", "huge_mushroom_bits", 10, ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                VanillaBlockStates.NORTH, Bool.TRUE,
                VanillaBlockStates.EAST, Bool.TRUE,
                VanillaBlockStates.SOUTH, Bool.TRUE,
                VanillaBlockStates.WEST, Bool.TRUE,
                VanillaBlockStates.UP, Bool.FALSE,
                VanillaBlockStates.DOWN, Bool.FALSE
        )));
        registerDuplicateOutput(BlockMapping.of("minecraft:red_mushroom_block", "huge_mushroom_bits", 15, ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                VanillaBlockStates.NORTH, Bool.TRUE,
                VanillaBlockStates.EAST, Bool.TRUE,
                VanillaBlockStates.SOUTH, Bool.TRUE,
                VanillaBlockStates.WEST, Bool.TRUE,
                VanillaBlockStates.UP, Bool.TRUE,
                VanillaBlockStates.DOWN, Bool.TRUE
        )));
        registerDuplicateOutput(BlockMapping.of("minecraft:red_mushroom_block", "huge_mushroom_bits", 10, ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                VanillaBlockStates.NORTH, Bool.TRUE,
                VanillaBlockStates.EAST, Bool.TRUE,
                VanillaBlockStates.SOUTH, Bool.TRUE,
                VanillaBlockStates.WEST, Bool.TRUE,
                VanillaBlockStates.UP, Bool.FALSE,
                VanillaBlockStates.DOWN, Bool.FALSE
        )));

        // Fallback to a normal mushroom block since stem doesn't exist in bedrock
        registerDuplicateInput(BlockMapping.of("minecraft:brown_mushroom_block", "huge_mushroom_bits", 15, ChunkerVanillaBlockType.MUSHROOM_STEM));

        register(BlockMapping.of("minecraft:nether_brick", ChunkerVanillaBlockType.NETHER_BRICKS));
        register(BlockMapping.of("minecraft:portal", ChunkerVanillaBlockType.NETHER_PORTAL, BedrockStateGroups.PORTAL));
        register(BlockMapping.of("minecraft:quartz_ore", ChunkerVanillaBlockType.NETHER_QUARTZ_ORE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:trapped_chest", ChunkerVanillaBlockType.TRAPPED_CHEST)
                        .put("minecraft:chest", ChunkerVanillaBlockType.CHEST)
                        .build(),
                BedrockStateGroups.CHEST));
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
                BedrockStateGroups.STAIRS));
        register(BlockMapping.of("minecraft:nether_wart_block", ChunkerVanillaBlockType.NETHER_WART_BLOCK));
        register(BlockMapping.of("minecraft:netherrack", ChunkerVanillaBlockType.NETHERRACK));
        register(BlockMapping.of("minecraft:noteblock", ChunkerVanillaBlockType.NOTE_BLOCK, BedrockStateGroups.NOTE_BLOCK));
        register(BlockMapping.of("minecraft:observer", ChunkerVanillaBlockType.OBSERVER, BedrockStateGroups.OBSERVER));
        register(BlockMapping.of("minecraft:obsidian", ChunkerVanillaBlockType.OBSIDIAN));
        register(BlockMapping.of("minecraft:packed_ice", ChunkerVanillaBlockType.PACKED_ICE));

        // Prismarine
        register(BlockMapping.flatten("minecraft:prismarine", "prismarine_block_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("dark", ChunkerVanillaBlockType.DARK_PRISMARINE)
                        .put("default", ChunkerVanillaBlockType.PRISMARINE)
                        .put("bricks", ChunkerVanillaBlockType.PRISMARINE_BRICKS)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:prismarine", ChunkerVanillaBlockType.PRISMARINE));

        // Pumpkin / Melon Stem
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:pumpkin_stem", ChunkerVanillaBlockType.PUMPKIN_STEM)
                        .put("minecraft:melon_stem", ChunkerVanillaBlockType.MELON_STEM)
                        .build(),
                BedrockStateGroups.CROP));

        // Other crops
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:potatoes", ChunkerVanillaBlockType.POTATOES)
                        .put("minecraft:carrots", ChunkerVanillaBlockType.CARROTS)
                        .put("minecraft:wheat", ChunkerVanillaBlockType.WHEAT)
                        .build(),
                BedrockStateGroups.CROP));
        register(BlockMapping.flatten("minecraft:wood", "stripped_bit", "wood_type",
                ImmutableMultimap.<Pair<Boolean, String>, ChunkerVanillaBlockType>builder()
                        .put(Pair.of(true, "oak"), ChunkerVanillaBlockType.STRIPPED_OAK_WOOD)
                        .put(Pair.of(false, "oak"), ChunkerVanillaBlockType.OAK_WOOD)
                        .put(Pair.of(false, "spruce"), ChunkerVanillaBlockType.SPRUCE_WOOD)
                        .put(Pair.of(false, "jungle"), ChunkerVanillaBlockType.JUNGLE_WOOD)
                        .put(Pair.of(true, "dark_oak"), ChunkerVanillaBlockType.STRIPPED_DARK_OAK_WOOD)
                        .put(Pair.of(false, "dark_oak"), ChunkerVanillaBlockType.DARK_OAK_WOOD)
                        .put(Pair.of(false, "birch"), ChunkerVanillaBlockType.BIRCH_WOOD)
                        .put(Pair.of(true, "acacia"), ChunkerVanillaBlockType.STRIPPED_ACACIA_WOOD)
                        .put(Pair.of(true, "spruce"), ChunkerVanillaBlockType.STRIPPED_SPRUCE_WOOD)
                        .put(Pair.of(false, "acacia"), ChunkerVanillaBlockType.ACACIA_WOOD)
                        .put(Pair.of(true, "jungle"), ChunkerVanillaBlockType.STRIPPED_JUNGLE_WOOD)
                        .put(Pair.of(true, "birch"), ChunkerVanillaBlockType.STRIPPED_BIRCH_WOOD)
                        .build(),
                BedrockStateGroups.WOOD_BLOCK));

        // Wall Skulls - Converted to block entity data
        register(BlockMapping.of("minecraft:skull", ChunkerVanillaBlockType.SKELETON_WALL_SKULL, BedrockStateGroups.WALL_SKULL));
        registerDuplicateInput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:skull", ChunkerVanillaBlockType.ZOMBIE_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.PLAYER_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.CREEPER_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.WITHER_SKELETON_WALL_SKULL)
                        .put("minecraft:skull", ChunkerVanillaBlockType.DRAGON_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.PIGLIN_WALL_HEAD)
                        .build(),
                BedrockStateGroups.WALL_SKULL));

        // Skulls - Converted to block entity data
        register(BlockMapping.of("minecraft:skull", "facing_direction", 1, ChunkerVanillaBlockType.SKELETON_SKULL, BedrockStateGroups.SKULL));
        registerDuplicateInput(BlockMapping.flatten("minecraft:skull", "facing_direction",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(1, ChunkerVanillaBlockType.PLAYER_HEAD)
                        .put(1, ChunkerVanillaBlockType.DRAGON_HEAD)
                        .put(1, ChunkerVanillaBlockType.PIGLIN_HEAD)
                        .put(1, ChunkerVanillaBlockType.WITHER_SKELETON_SKULL)
                        .put(1, ChunkerVanillaBlockType.ZOMBIE_HEAD)
                        .put(1, ChunkerVanillaBlockType.CREEPER_HEAD)
                        .build(),
                BedrockStateGroups.SKULL));

        // Flower pots
        register(BlockMapping.of("minecraft:flower_pot", ChunkerVanillaBlockType.FLOWER_POT, BedrockStateGroups.FLOWER_POT));
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
                        .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_PALE_OAK_SAPLING)
                        .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_OPEN_EYEBLOSSOM)
                        .put("minecraft:flower_pot", ChunkerVanillaBlockType.POTTED_CLOSED_EYEBLOSSOM)
                        .build(),
                BedrockStateGroups.FLOWER_POT));

        register(BlockMapping.flatten("minecraft:double_plant", "double_plant_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("fern", ChunkerVanillaBlockType.LARGE_FERN)
                        .put("sunflower", ChunkerVanillaBlockType.SUNFLOWER)
                        .put("grass", ChunkerVanillaBlockType.TALL_GRASS)
                        .put("paeonia", ChunkerVanillaBlockType.PEONY)
                        .put("rose", ChunkerVanillaBlockType.ROSE_BUSH)
                        .put("syringa", ChunkerVanillaBlockType.LILAC)
                        .build(),
                BedrockStateGroups.DOUBLE_BLOCK));

        // Tall grass (two types which look identical to other blocks)
        register(BlockMapping.of("minecraft:tallgrass", "tall_grass_type", "tall", ChunkerVanillaBlockType.SHORT_GRASS));
        registerDuplicateOutput(BlockMapping.of("minecraft:tallgrass", "tall_grass_type", "default", ChunkerVanillaBlockType.SHORT_GRASS));
        register(BlockMapping.of("minecraft:tallgrass", "tall_grass_type", "fern", ChunkerVanillaBlockType.FERN));
        registerDuplicateOutput(BlockMapping.of("minecraft:tallgrass", "tall_grass_type", "snow", ChunkerVanillaBlockType.FERN));

        // Hay and Bone have a deprecated field
        register(BlockMapping.group("deprecated", 0, ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:hay_block", ChunkerVanillaBlockType.HAY_BLOCK)
                        .put("minecraft:bone_block", ChunkerVanillaBlockType.BONE_BLOCK)
                        .build(),
                BedrockStateGroups.PILLAR_BLOCK));
        registerDuplicateOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:hay_block", ChunkerVanillaBlockType.HAY_BLOCK)
                        .put("minecraft:bone_block", ChunkerVanillaBlockType.BONE_BLOCK)
                        .build(),
                BedrockStateGroups.PILLAR_BLOCK));

        // Stripped logs
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:stripped_acacia_log", ChunkerVanillaBlockType.STRIPPED_ACACIA_LOG)
                        .put("minecraft:stripped_dark_oak_log", ChunkerVanillaBlockType.STRIPPED_DARK_OAK_LOG)
                        .put("minecraft:stripped_spruce_log", ChunkerVanillaBlockType.STRIPPED_SPRUCE_LOG)
                        .put("minecraft:stripped_oak_log", ChunkerVanillaBlockType.STRIPPED_OAK_LOG)
                        .put("minecraft:stripped_jungle_log", ChunkerVanillaBlockType.STRIPPED_JUNGLE_LOG)
                        .put("minecraft:stripped_birch_log", ChunkerVanillaBlockType.STRIPPED_BIRCH_LOG)
                        .build(),
                BedrockStateGroups.PILLAR_BLOCK));

        // Pumpkin (no direction in Chunker format)
        register(BlockMapping.of("minecraft:pumpkin", "direction", 0, ChunkerVanillaBlockType.PUMPKIN));
        registerDuplicateOutput(BlockMapping.of("minecraft:pumpkin", ChunkerVanillaBlockType.PUMPKIN));

        // Purpur Block default rotation (since there is no rotation Java)
        register(BlockMapping.of("minecraft:purpur_block", "chisel_type", "default", "direction", 0, ChunkerVanillaBlockType.PURPUR_BLOCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:purpur_block", ChunkerVanillaBlockType.PURPUR_BLOCK));

        // Quartz Blocks default rotation (since there is no rotation Java)
        register(BlockMapping.of("minecraft:quartz_block", "chisel_type", "default", "direction", 0, ChunkerVanillaBlockType.QUARTZ_BLOCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", ChunkerVanillaBlockType.QUARTZ_BLOCK));

        register(BlockMapping.of("minecraft:quartz_block", "chisel_type", "chiseled", "direction", 0, ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", "chisel_type", "chiseled", ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));

        register(BlockMapping.of("minecraft:quartz_block", "chisel_type", "smooth", "direction", 0, ChunkerVanillaBlockType.SMOOTH_QUARTZ));
        registerDuplicateOutput(BlockMapping.of("minecraft:quartz_block", "chisel_type", "smooth", ChunkerVanillaBlockType.SMOOTH_QUARTZ));

        // Purpur / Quartz Pillar
        register(BlockMapping.group("chisel_type", "lines", ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:quartz_block", ChunkerVanillaBlockType.QUARTZ_PILLAR)
                        .put("minecraft:purpur_block", ChunkerVanillaBlockType.PURPUR_PILLAR)
                        .build(),
                BedrockStateGroups.PILLAR_BLOCK));

        register(BlockMapping.of("minecraft:end_rod", ChunkerVanillaBlockType.END_ROD, BedrockStateGroups.END_ROD));
        register(BlockMapping.of("minecraft:rail", ChunkerVanillaBlockType.RAIL, BedrockStateGroups.RAIL));
        register(BlockMapping.of("minecraft:red_mushroom", ChunkerVanillaBlockType.RED_MUSHROOM));
        register(BlockMapping.of("minecraft:red_nether_brick", ChunkerVanillaBlockType.RED_NETHER_BRICKS));
        register(BlockMapping.of("minecraft:redstone_block", ChunkerVanillaBlockType.REDSTONE_BLOCK));
        register(BlockMapping.of("minecraft:slime", ChunkerVanillaBlockType.SLIME_BLOCK));
        register(BlockMapping.of("minecraft:smithing_table", ChunkerVanillaBlockType.SMITHING_TABLE));
        register(BlockMapping.of("minecraft:smooth_stone", ChunkerVanillaBlockType.SMOOTH_STONE));
        register(BlockMapping.of("minecraft:soul_sand", ChunkerVanillaBlockType.SOUL_SAND));
        register(BlockMapping.of("minecraft:mob_spawner", ChunkerVanillaBlockType.SPAWNER));
        register(BlockMapping.of("minecraft:structure_block", ChunkerVanillaBlockType.STRUCTURE_BLOCK, BedrockStateGroups.STRUCTURE_BLOCK));
        register(BlockMapping.of("minecraft:hardened_clay", ChunkerVanillaBlockType.TERRACOTTA));
        register(BlockMapping.of("minecraft:tripwire_hook", ChunkerVanillaBlockType.TRIPWIRE_HOOK, BedrockStateGroups.TRIPWIRE_HOOK));
        register(BlockMapping.of("minecraft:turtle_egg", ChunkerVanillaBlockType.TURTLE_EGG, BedrockStateGroups.TURTLE_EGG));

        // Sponge
        register(BlockMapping.flatten("minecraft:sponge", "sponge_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("wet", ChunkerVanillaBlockType.WET_SPONGE)
                        .put("dry", ChunkerVanillaBlockType.SPONGE)
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
        register(BlockMapping.of("minecraft:unlit_redstone_torch", "torch_facing_direction", "top", ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.FALSE));
        registerDuplicateOutput(BlockMapping.of("minecraft:unlit_redstone_torch", "torch_facing_direction", "unknown", ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.FALSE));
        register(BlockMapping.of("minecraft:unlit_redstone_torch", ChunkerVanillaBlockType.REDSTONE_WALL_TORCH, BedrockStateGroups.TORCH_FACING, VanillaBlockStates.LIT, Bool.FALSE));

        // Lit redstone torch
        register(BlockMapping.of("minecraft:redstone_torch", "torch_facing_direction", "top", ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.TRUE));
        registerDuplicateOutput(BlockMapping.of("minecraft:redstone_torch", "torch_facing_direction", "unknown", ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.TRUE));
        register(BlockMapping.of("minecraft:redstone_torch", ChunkerVanillaBlockType.REDSTONE_WALL_TORCH, BedrockStateGroups.TORCH_FACING, VanillaBlockStates.LIT, Bool.TRUE));

        // Torch (normal/wall)
        register(BlockMapping.of("minecraft:torch", "torch_facing_direction", "top", ChunkerVanillaBlockType.TORCH));
        registerDuplicateOutput(BlockMapping.of("minecraft:torch", "torch_facing_direction", "unknown", ChunkerVanillaBlockType.TORCH));
        register(BlockMapping.of("minecraft:torch", ChunkerVanillaBlockType.WALL_TORCH, BedrockStateGroups.TORCH_FACING));

        // Repeater
        register(BlockMapping.of("minecraft:powered_repeater", ChunkerVanillaBlockType.REPEATER, BedrockStateGroups.REPEATER, VanillaBlockStates.POWERED, Bool.TRUE));
        register(BlockMapping.of("minecraft:unpowered_repeater", ChunkerVanillaBlockType.REPEATER, BedrockStateGroups.REPEATER, VanillaBlockStates.POWERED, Bool.FALSE));

        register(BlockMapping.of("minecraft:redstone_wire", ChunkerVanillaBlockType.REDSTONE_WIRE, BedrockStateGroups.WIRE));
        register(BlockMapping.of("minecraft:scaffolding", ChunkerVanillaBlockType.SCAFFOLDING, BedrockStateGroups.SCAFFOLDING));

        // Sea Pickle
        register(BlockMapping.of("minecraft:sea_pickle", ChunkerVanillaBlockType.SEA_PICKLE, BedrockStateGroups.SEA_PICKLE));

        // Snow
        register(BlockMapping.of("minecraft:snow_layer", "covered_bit", false, ChunkerVanillaBlockType.SNOW, BedrockStateGroups.LAYER_BLOCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:snow_layer", ChunkerVanillaBlockType.SNOW, BedrockStateGroups.LAYER_BLOCK));
        register(BlockMapping.of("minecraft:snow", ChunkerVanillaBlockType.SNOW_BLOCK));

        // Sea grass
        register(BlockMapping.of("minecraft:seagrass", "sea_grass_type", "double_bot", ChunkerVanillaBlockType.TALL_SEAGRASS, VanillaBlockStates.HALF, Half.BOTTOM));
        register(BlockMapping.of("minecraft:seagrass", "sea_grass_type", "double_top", ChunkerVanillaBlockType.TALL_SEAGRASS, VanillaBlockStates.HALF, Half.TOP));
        register(BlockMapping.of("minecraft:seagrass", "sea_grass_type", "default", ChunkerVanillaBlockType.SEAGRASS));

        // Liquids
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:water", ChunkerVanillaBlockType.WATER)
                        .put("minecraft:lava", ChunkerVanillaBlockType.LAVA)
                        .build(),
                BedrockStateGroups.LIQUID, VanillaBlockStates.FLOWING, Bool.FALSE));

        // Flowing liquids should use the flowing state
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:flowing_water", ChunkerVanillaBlockType.WATER)
                        .put("minecraft:flowing_lava", ChunkerVanillaBlockType.LAVA)
                        .build(),
                BedrockStateGroups.LIQUID, VanillaBlockStates.FLOWING, Bool.TRUE));

        // TNT
        register(BlockMapping.of("minecraft:tnt", ChunkerVanillaBlockType.TNT, BedrockStateGroups.TNT));

        // Vines
        register(BlockMapping.of("minecraft:vine", ChunkerVanillaBlockType.VINE, BedrockStateGroups.VINE, VanillaBlockStates.UP, Bool.FALSE));
        registerDuplicateInput(BlockMapping.of("minecraft:vine", ChunkerVanillaBlockType.VINE, BedrockStateGroups.VINE));

        register(BlockMapping.of("minecraft:frame", ChunkerVanillaBlockType.ITEM_FRAME_BEDROCK, BedrockStateGroups.FRAME, VanillaBlockStates.LIT, Bool.FALSE)); // Needs to be turned into an entity
        registerDuplicateInput(BlockMapping.of("minecraft:frame", ChunkerVanillaBlockType.ITEM_FRAME_BEDROCK, BedrockStateGroups.FRAME));
        register(BlockMapping.flatten("minecraft:wool", "color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("purple", ChunkerVanillaBlockType.PURPLE_WOOL)
                        .put("orange", ChunkerVanillaBlockType.ORANGE_WOOL)
                        .put("white", ChunkerVanillaBlockType.WHITE_WOOL)
                        .put("lime", ChunkerVanillaBlockType.LIME_WOOL)
                        .put("pink", ChunkerVanillaBlockType.PINK_WOOL)
                        .put("green", ChunkerVanillaBlockType.GREEN_WOOL)
                        .put("yellow", ChunkerVanillaBlockType.YELLOW_WOOL)
                        .put("cyan", ChunkerVanillaBlockType.CYAN_WOOL)
                        .put("black", ChunkerVanillaBlockType.BLACK_WOOL)
                        .put("red", ChunkerVanillaBlockType.RED_WOOL)
                        .put("brown", ChunkerVanillaBlockType.BROWN_WOOL)
                        .put("magenta", ChunkerVanillaBlockType.MAGENTA_WOOL)
                        .put("silver", ChunkerVanillaBlockType.LIGHT_GRAY_WOOL)
                        .put("gray", ChunkerVanillaBlockType.GRAY_WOOL)
                        .put("light_blue", ChunkerVanillaBlockType.LIGHT_BLUE_WOOL)
                        .put("blue", ChunkerVanillaBlockType.BLUE_WOOL)
                        .build()
        ));
        register(BlockMapping.flatten("minecraft:log", "old_log_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("birch", ChunkerVanillaBlockType.BIRCH_LOG)
                        .put("spruce", ChunkerVanillaBlockType.SPRUCE_LOG)
                        .put("oak", ChunkerVanillaBlockType.OAK_LOG)
                        .put("jungle", ChunkerVanillaBlockType.JUNGLE_LOG)
                        .build(),
                BedrockStateGroups.PILLAR_BLOCK));
        register(BlockMapping.flatten("minecraft:log2", "new_log_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("acacia", ChunkerVanillaBlockType.ACACIA_LOG)
                        .put("dark_oak", ChunkerVanillaBlockType.DARK_OAK_LOG)
                        .build(),
                BedrockStateGroups.PILLAR_BLOCK));
        register(BlockMapping.flatten("minecraft:fence", "wood_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("dark_oak", ChunkerVanillaBlockType.DARK_OAK_FENCE)
                        .put("jungle", ChunkerVanillaBlockType.JUNGLE_FENCE)
                        .put("oak", ChunkerVanillaBlockType.OAK_FENCE)
                        .put("birch", ChunkerVanillaBlockType.BIRCH_FENCE)
                        .put("spruce", ChunkerVanillaBlockType.SPRUCE_FENCE)
                        .put("acacia", ChunkerVanillaBlockType.ACACIA_FENCE)
                        .build(),
                BedrockStateGroups.CONNECTABLE_HORIZONTAL));
        register(BlockMapping.flatten("minecraft:carpet", "color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("light_blue", ChunkerVanillaBlockType.LIGHT_BLUE_CARPET)
                        .put("silver", ChunkerVanillaBlockType.LIGHT_GRAY_CARPET)
                        .put("yellow", ChunkerVanillaBlockType.YELLOW_CARPET)
                        .put("lime", ChunkerVanillaBlockType.LIME_CARPET)
                        .put("brown", ChunkerVanillaBlockType.BROWN_CARPET)
                        .put("orange", ChunkerVanillaBlockType.ORANGE_CARPET)
                        .put("red", ChunkerVanillaBlockType.RED_CARPET)
                        .put("gray", ChunkerVanillaBlockType.GRAY_CARPET)
                        .put("cyan", ChunkerVanillaBlockType.CYAN_CARPET)
                        .put("pink", ChunkerVanillaBlockType.PINK_CARPET)
                        .put("green", ChunkerVanillaBlockType.GREEN_CARPET)
                        .put("white", ChunkerVanillaBlockType.WHITE_CARPET)
                        .put("blue", ChunkerVanillaBlockType.BLUE_CARPET)
                        .put("black", ChunkerVanillaBlockType.BLACK_CARPET)
                        .put("magenta", ChunkerVanillaBlockType.MAGENTA_CARPET)
                        .put("purple", ChunkerVanillaBlockType.PURPLE_CARPET)
                        .build()
        ));

        register(BlockMapping.of("minecraft:undyed_shulker_box", ChunkerVanillaBlockType.SHULKER_BOX, BedrockStateGroups.SHULKER_BOX));
        register(BlockMapping.flatten("minecraft:shulker_box", "color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("cyan", ChunkerVanillaBlockType.CYAN_SHULKER_BOX)
                        .put("green", ChunkerVanillaBlockType.GREEN_SHULKER_BOX)
                        .put("gray", ChunkerVanillaBlockType.GRAY_SHULKER_BOX)
                        .put("silver", ChunkerVanillaBlockType.LIGHT_GRAY_SHULKER_BOX)
                        .put("orange", ChunkerVanillaBlockType.ORANGE_SHULKER_BOX)
                        .put("white", ChunkerVanillaBlockType.WHITE_SHULKER_BOX)
                        .put("red", ChunkerVanillaBlockType.RED_SHULKER_BOX)
                        .put("brown", ChunkerVanillaBlockType.BROWN_SHULKER_BOX)
                        .put("light_blue", ChunkerVanillaBlockType.LIGHT_BLUE_SHULKER_BOX)
                        .put("lime", ChunkerVanillaBlockType.LIME_SHULKER_BOX)
                        .put("magenta", ChunkerVanillaBlockType.MAGENTA_SHULKER_BOX)
                        .put("black", ChunkerVanillaBlockType.BLACK_SHULKER_BOX)
                        .put("yellow", ChunkerVanillaBlockType.YELLOW_SHULKER_BOX)
                        .put("pink", ChunkerVanillaBlockType.PINK_SHULKER_BOX)
                        .put("blue", ChunkerVanillaBlockType.BLUE_SHULKER_BOX)
                        .put("purple", ChunkerVanillaBlockType.PURPLE_SHULKER_BOX)
                        .build(),
                BedrockStateGroups.SHULKER_BOX));

        register(BlockMapping.flatten("minecraft:concrete", "color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("brown", ChunkerVanillaBlockType.BROWN_CONCRETE)
                        .put("magenta", ChunkerVanillaBlockType.MAGENTA_CONCRETE)
                        .put("pink", ChunkerVanillaBlockType.PINK_CONCRETE)
                        .put("light_blue", ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE)
                        .put("lime", ChunkerVanillaBlockType.LIME_CONCRETE)
                        .put("green", ChunkerVanillaBlockType.GREEN_CONCRETE)
                        .put("purple", ChunkerVanillaBlockType.PURPLE_CONCRETE)
                        .put("cyan", ChunkerVanillaBlockType.CYAN_CONCRETE)
                        .put("yellow", ChunkerVanillaBlockType.YELLOW_CONCRETE)
                        .put("blue", ChunkerVanillaBlockType.BLUE_CONCRETE)
                        .put("white", ChunkerVanillaBlockType.WHITE_CONCRETE)
                        .put("red", ChunkerVanillaBlockType.RED_CONCRETE)
                        .put("gray", ChunkerVanillaBlockType.GRAY_CONCRETE)
                        .put("black", ChunkerVanillaBlockType.BLACK_CONCRETE)
                        .put("silver", ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE)
                        .put("orange", ChunkerVanillaBlockType.ORANGE_CONCRETE)
                        .build()
        ));

        register(BlockMapping.flatten("minecraft:stained_glass", "color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("pink", ChunkerVanillaBlockType.PINK_STAINED_GLASS)
                        .put("black", ChunkerVanillaBlockType.BLACK_STAINED_GLASS)
                        .put("white", ChunkerVanillaBlockType.WHITE_STAINED_GLASS)
                        .put("yellow", ChunkerVanillaBlockType.YELLOW_STAINED_GLASS)
                        .put("gray", ChunkerVanillaBlockType.GRAY_STAINED_GLASS)
                        .put("purple", ChunkerVanillaBlockType.PURPLE_STAINED_GLASS)
                        .put("brown", ChunkerVanillaBlockType.BROWN_STAINED_GLASS)
                        .put("cyan", ChunkerVanillaBlockType.CYAN_STAINED_GLASS)
                        .put("silver", ChunkerVanillaBlockType.LIGHT_GRAY_STAINED_GLASS)
                        .put("red", ChunkerVanillaBlockType.RED_STAINED_GLASS)
                        .put("magenta", ChunkerVanillaBlockType.MAGENTA_STAINED_GLASS)
                        .put("green", ChunkerVanillaBlockType.GREEN_STAINED_GLASS)
                        .put("orange", ChunkerVanillaBlockType.ORANGE_STAINED_GLASS)
                        .put("lime", ChunkerVanillaBlockType.LIME_STAINED_GLASS)
                        .put("blue", ChunkerVanillaBlockType.BLUE_STAINED_GLASS)
                        .put("light_blue", ChunkerVanillaBlockType.LIGHT_BLUE_STAINED_GLASS)
                        .build()
        ));

        register(BlockMapping.flatten("minecraft:stained_glass_pane", "color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("gray", ChunkerVanillaBlockType.GRAY_STAINED_GLASS_PANE)
                        .put("lime", ChunkerVanillaBlockType.LIME_STAINED_GLASS_PANE)
                        .put("purple", ChunkerVanillaBlockType.PURPLE_STAINED_GLASS_PANE)
                        .put("green", ChunkerVanillaBlockType.GREEN_STAINED_GLASS_PANE)
                        .put("white", ChunkerVanillaBlockType.WHITE_STAINED_GLASS_PANE)
                        .put("yellow", ChunkerVanillaBlockType.YELLOW_STAINED_GLASS_PANE)
                        .put("silver", ChunkerVanillaBlockType.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .put("magenta", ChunkerVanillaBlockType.MAGENTA_STAINED_GLASS_PANE)
                        .put("blue", ChunkerVanillaBlockType.BLUE_STAINED_GLASS_PANE)
                        .put("light_blue", ChunkerVanillaBlockType.LIGHT_BLUE_STAINED_GLASS_PANE)
                        .put("pink", ChunkerVanillaBlockType.PINK_STAINED_GLASS_PANE)
                        .put("orange", ChunkerVanillaBlockType.ORANGE_STAINED_GLASS_PANE)
                        .put("black", ChunkerVanillaBlockType.BLACK_STAINED_GLASS_PANE)
                        .put("cyan", ChunkerVanillaBlockType.CYAN_STAINED_GLASS_PANE)
                        .put("brown", ChunkerVanillaBlockType.BROWN_STAINED_GLASS_PANE)
                        .put("red", ChunkerVanillaBlockType.RED_STAINED_GLASS_PANE)
                        .build(),
                BedrockStateGroups.CONNECTABLE_HORIZONTAL));

        // Concrete Powder
        register(BlockMapping.flatten("minecraft:concretePowder", "color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("light_blue", ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE_POWDER)
                        .put("purple", ChunkerVanillaBlockType.PURPLE_CONCRETE_POWDER)
                        .put("gray", ChunkerVanillaBlockType.GRAY_CONCRETE_POWDER)
                        .put("green", ChunkerVanillaBlockType.GREEN_CONCRETE_POWDER)
                        .put("magenta", ChunkerVanillaBlockType.MAGENTA_CONCRETE_POWDER)
                        .put("brown", ChunkerVanillaBlockType.BROWN_CONCRETE_POWDER)
                        .put("white", ChunkerVanillaBlockType.WHITE_CONCRETE_POWDER)
                        .put("lime", ChunkerVanillaBlockType.LIME_CONCRETE_POWDER)
                        .put("silver", ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE_POWDER)
                        .put("pink", ChunkerVanillaBlockType.PINK_CONCRETE_POWDER)
                        .put("black", ChunkerVanillaBlockType.BLACK_CONCRETE_POWDER)
                        .put("orange", ChunkerVanillaBlockType.ORANGE_CONCRETE_POWDER)
                        .put("cyan", ChunkerVanillaBlockType.CYAN_CONCRETE_POWDER)
                        .put("red", ChunkerVanillaBlockType.RED_CONCRETE_POWDER)
                        .put("yellow", ChunkerVanillaBlockType.YELLOW_CONCRETE_POWDER)
                        .put("blue", ChunkerVanillaBlockType.BLUE_CONCRETE_POWDER)
                        .build()
        ));
        // Legacy alias
        registerDuplicateOutput(BlockMapping.flatten("minecraft:concretepowder", "color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("light_blue", ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE_POWDER)
                        .put("purple", ChunkerVanillaBlockType.PURPLE_CONCRETE_POWDER)
                        .put("gray", ChunkerVanillaBlockType.GRAY_CONCRETE_POWDER)
                        .put("green", ChunkerVanillaBlockType.GREEN_CONCRETE_POWDER)
                        .put("magenta", ChunkerVanillaBlockType.MAGENTA_CONCRETE_POWDER)
                        .put("brown", ChunkerVanillaBlockType.BROWN_CONCRETE_POWDER)
                        .put("white", ChunkerVanillaBlockType.WHITE_CONCRETE_POWDER)
                        .put("lime", ChunkerVanillaBlockType.LIME_CONCRETE_POWDER)
                        .put("silver", ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE_POWDER)
                        .put("pink", ChunkerVanillaBlockType.PINK_CONCRETE_POWDER)
                        .put("black", ChunkerVanillaBlockType.BLACK_CONCRETE_POWDER)
                        .put("orange", ChunkerVanillaBlockType.ORANGE_CONCRETE_POWDER)
                        .put("cyan", ChunkerVanillaBlockType.CYAN_CONCRETE_POWDER)
                        .put("red", ChunkerVanillaBlockType.RED_CONCRETE_POWDER)
                        .put("yellow", ChunkerVanillaBlockType.YELLOW_CONCRETE_POWDER)
                        .put("blue", ChunkerVanillaBlockType.BLUE_CONCRETE_POWDER)
                        .build()
        ));

        register(BlockMapping.flatten("minecraft:stained_hardened_clay", "color",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("cyan", ChunkerVanillaBlockType.CYAN_TERRACOTTA)
                        .put("lime", ChunkerVanillaBlockType.LIME_TERRACOTTA)
                        .put("gray", ChunkerVanillaBlockType.GRAY_TERRACOTTA)
                        .put("black", ChunkerVanillaBlockType.BLACK_TERRACOTTA)
                        .put("silver", ChunkerVanillaBlockType.LIGHT_GRAY_TERRACOTTA)
                        .put("orange", ChunkerVanillaBlockType.ORANGE_TERRACOTTA)
                        .put("white", ChunkerVanillaBlockType.WHITE_TERRACOTTA)
                        .put("pink", ChunkerVanillaBlockType.PINK_TERRACOTTA)
                        .put("purple", ChunkerVanillaBlockType.PURPLE_TERRACOTTA)
                        .put("yellow", ChunkerVanillaBlockType.YELLOW_TERRACOTTA)
                        .put("blue", ChunkerVanillaBlockType.BLUE_TERRACOTTA)
                        .put("light_blue", ChunkerVanillaBlockType.LIGHT_BLUE_TERRACOTTA)
                        .put("green", ChunkerVanillaBlockType.GREEN_TERRACOTTA)
                        .put("magenta", ChunkerVanillaBlockType.MAGENTA_TERRACOTTA)
                        .put("brown", ChunkerVanillaBlockType.BROWN_TERRACOTTA)
                        .put("red", ChunkerVanillaBlockType.RED_TERRACOTTA)
                        .build()
        ));

        register(BlockMapping.flatten("minecraft:stone", "stone_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("stone", ChunkerVanillaBlockType.STONE)
                        .put("diorite", ChunkerVanillaBlockType.DIORITE)
                        .put("andesite_smooth", ChunkerVanillaBlockType.POLISHED_ANDESITE)
                        .put("diorite_smooth", ChunkerVanillaBlockType.POLISHED_DIORITE)
                        .put("andesite", ChunkerVanillaBlockType.ANDESITE)
                        .put("granite", ChunkerVanillaBlockType.GRANITE)
                        .put("granite_smooth", ChunkerVanillaBlockType.POLISHED_GRANITE)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:stone", ChunkerVanillaBlockType.STONE));

        register(BlockMapping.flatten("minecraft:planks", "wood_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("jungle", ChunkerVanillaBlockType.JUNGLE_PLANKS)
                        .put("oak", ChunkerVanillaBlockType.OAK_PLANKS)
                        .put("dark_oak", ChunkerVanillaBlockType.DARK_OAK_PLANKS)
                        .put("birch", ChunkerVanillaBlockType.BIRCH_PLANKS)
                        .put("spruce", ChunkerVanillaBlockType.SPRUCE_PLANKS)
                        .put("acacia", ChunkerVanillaBlockType.ACACIA_PLANKS)
                        .build()
        ));

        // Piston / Sticky Piston
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:sticky_piston", ChunkerVanillaBlockType.STICKY_PISTON)
                        .put("minecraft:piston", ChunkerVanillaBlockType.PISTON)
                        .build(),
                BedrockStateGroups.PISTON));

        register(BlockMapping.of("minecraft:pistonArmCollision", ChunkerVanillaBlockType.PISTON_HEAD, BedrockStateGroups.PISTON_HEAD, VanillaBlockStates.PISTON_TYPE, PistonType.NORMAL));
        registerDuplicateInput(BlockMapping.of("minecraft:pistonArmCollision", ChunkerVanillaBlockType.PISTON_HEAD, BedrockStateGroups.PISTON_HEAD, VanillaBlockStates.PISTON_TYPE, PistonType.STICKY));

        // Legacy alias
        registerDuplicateOutput(BlockMapping.of("minecraft:pistonarmcollision", ChunkerVanillaBlockType.PISTON_HEAD, BedrockStateGroups.PISTON_HEAD, VanillaBlockStates.PISTON_TYPE, PistonType.NORMAL));

        // Wood slabs
        register(BlockMapping.flatten("minecraft:wooden_slab", "wood_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("jungle", ChunkerVanillaBlockType.JUNGLE_SLAB)
                        .put("oak", ChunkerVanillaBlockType.OAK_SLAB)
                        .put("acacia", ChunkerVanillaBlockType.ACACIA_SLAB)
                        .put("spruce", ChunkerVanillaBlockType.SPRUCE_SLAB)
                        .put("dark_oak", ChunkerVanillaBlockType.DARK_OAK_SLAB)
                        .put("birch", ChunkerVanillaBlockType.BIRCH_SLAB)
                        .build(),
                BedrockStateGroups.SLAB_HALF));
        registerDuplicateOutput(BlockMapping.of("minecraft:wooden_slab", ChunkerVanillaBlockType.OAK_SLAB, BedrockStateGroups.SLAB_HALF));

        register(BlockMapping.flatten("minecraft:double_wooden_slab", "wood_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("jungle", ChunkerVanillaBlockType.JUNGLE_SLAB)
                        .put("spruce", ChunkerVanillaBlockType.SPRUCE_SLAB)
                        .put("oak", ChunkerVanillaBlockType.OAK_SLAB)
                        .put("acacia", ChunkerVanillaBlockType.ACACIA_SLAB)
                        .put("dark_oak", ChunkerVanillaBlockType.DARK_OAK_SLAB)
                        .put("birch", ChunkerVanillaBlockType.BIRCH_SLAB)
                        .build(),
                BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.of("minecraft:double_wooden_slab", ChunkerVanillaBlockType.OAK_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        // Stone slabs
        register(BlockMapping.flatten("minecraft:stone_slab", "stone_slab_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("brick", ChunkerVanillaBlockType.BRICK_SLAB)
                        .put("smooth_stone", ChunkerVanillaBlockType.SMOOTH_STONE_SLAB)
                        .put("nether_brick", ChunkerVanillaBlockType.NETHER_BRICK_SLAB)
                        .put("sandstone", ChunkerVanillaBlockType.SANDSTONE_SLAB)
                        .put("stone_brick", ChunkerVanillaBlockType.STONE_BRICK_SLAB)
                        .put("cobblestone", ChunkerVanillaBlockType.COBBLESTONE_SLAB)
                        .put("quartz", ChunkerVanillaBlockType.QUARTZ_SLAB)
                        .put("wood", ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB)
                        .build(),
                BedrockStateGroups.SLAB_HALF));

        register(BlockMapping.flatten("minecraft:double_stone_slab", "stone_slab_type",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("cobblestone", ChunkerVanillaBlockType.COBBLESTONE_SLAB)
                        .put("quartz", ChunkerVanillaBlockType.QUARTZ_SLAB)
                        .put("brick", ChunkerVanillaBlockType.BRICK_SLAB)
                        .put("smooth_stone", ChunkerVanillaBlockType.SMOOTH_STONE_SLAB)
                        .put("nether_brick", ChunkerVanillaBlockType.NETHER_BRICK_SLAB)
                        .put("sandstone", ChunkerVanillaBlockType.SANDSTONE_SLAB)
                        .put("stone_brick", ChunkerVanillaBlockType.STONE_BRICK_SLAB)
                        .put("wood", ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB)
                        .build(),
                BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        register(BlockMapping.flatten("minecraft:stone_slab2", "stone_slab_type_2",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("prismarine_rough", ChunkerVanillaBlockType.PRISMARINE_SLAB)
                        .put("red_nether_brick", ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB)
                        .put("mossy_cobblestone", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB)
                        .put("red_sandstone", ChunkerVanillaBlockType.RED_SANDSTONE_SLAB)
                        .put("purpur", ChunkerVanillaBlockType.PURPUR_SLAB)
                        .put("smooth_sandstone", ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB)
                        .put("prismarine_brick", ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB)
                        .put("prismarine_dark", ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB)
                        .build(),
                BedrockStateGroups.SLAB_HALF));
        register(BlockMapping.flatten("minecraft:double_stone_slab2", "stone_slab_type_2",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("prismarine_brick", ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB)
                        .put("red_sandstone", ChunkerVanillaBlockType.RED_SANDSTONE_SLAB)
                        .put("purpur", ChunkerVanillaBlockType.PURPUR_SLAB)
                        .put("prismarine_dark", ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB)
                        .put("red_nether_brick", ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB)
                        .put("prismarine_rough", ChunkerVanillaBlockType.PRISMARINE_SLAB)
                        .put("mossy_cobblestone", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB)
                        .put("smooth_sandstone", ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB)
                        .build(),
                BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        register(BlockMapping.flatten("minecraft:stone_slab3", "stone_slab_type_3",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("granite", ChunkerVanillaBlockType.GRANITE_SLAB)
                        .put("andesite", ChunkerVanillaBlockType.ANDESITE_SLAB)
                        .put("polished_diorite", ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB)
                        .put("diorite", ChunkerVanillaBlockType.DIORITE_SLAB)
                        .put("smooth_red_sandstone", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB)
                        .put("polished_granite", ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB)
                        .put("polished_andesite", ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB)
                        .put("end_stone_brick", ChunkerVanillaBlockType.END_STONE_BRICK_SLAB)
                        .build(),
                BedrockStateGroups.SLAB_HALF));
        register(BlockMapping.flatten("minecraft:double_stone_slab3", "stone_slab_type_3",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("polished_diorite", ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB)
                        .put("end_stone_brick", ChunkerVanillaBlockType.END_STONE_BRICK_SLAB)
                        .put("granite", ChunkerVanillaBlockType.GRANITE_SLAB)
                        .put("andesite", ChunkerVanillaBlockType.ANDESITE_SLAB)
                        .put("polished_granite", ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB)
                        .put("smooth_red_sandstone", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB)
                        .put("polished_andesite", ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB)
                        .put("diorite", ChunkerVanillaBlockType.DIORITE_SLAB)
                        .build(),
                BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        register(BlockMapping.flatten("minecraft:stone_slab4", "stone_slab_type_4",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("mossy_stone_brick", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                        .put("stone", ChunkerVanillaBlockType.STONE_SLAB)
                        .put("cut_sandstone", ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB)
                        .put("cut_red_sandstone", ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB)
                        .put("smooth_quartz", ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB)
                        .build(),
                BedrockStateGroups.SLAB_HALF));
        register(BlockMapping.flatten("minecraft:double_stone_slab4", "stone_slab_type_4",
                ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("cut_red_sandstone", ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB)
                        .put("mossy_stone_brick", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                        .put("cut_sandstone", ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB)
                        .put("smooth_quartz", ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB)
                        .put("stone", ChunkerVanillaBlockType.STONE_SLAB)
                        .build(),
                BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        // Sea lantern (with legacy alias)
        register(BlockMapping.of("minecraft:seaLantern", ChunkerVanillaBlockType.SEA_LANTERN));
        registerDuplicateOutput(BlockMapping.of("minecraft:sealantern", ChunkerVanillaBlockType.SEA_LANTERN));

        // Trip wire (with legacy alias)
        register(BlockMapping.of("minecraft:tripWire", ChunkerVanillaBlockType.TRIPWIRE, BedrockStateGroups.TRIPWIRE));
        registerDuplicateOutput(BlockMapping.of("minecraft:tripwire", ChunkerVanillaBlockType.TRIPWIRE, BedrockStateGroups.TRIPWIRE));

        // Moving block is technical block which isn't equivalent to moving_piston
        registerDuplicateInput(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.MOVING_PISTON_JAVA));
        register(BlockMapping.of("minecraft:movingBlock", ChunkerVanillaBlockType.MOVING_BLOCK_BEDROCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:movingblock", ChunkerVanillaBlockType.MOVING_BLOCK_BEDROCK));

        // Invisible bedrock (with legacy alias)
        register(BlockMapping.of("minecraft:invisibleBedrock", ChunkerVanillaBlockType.INVISIBLE_BEDROCK));
        registerDuplicateOutput(BlockMapping.of("minecraft:invisiblebedrock", ChunkerVanillaBlockType.INVISIBLE_BEDROCK));

        // R13
        if (version.isGreaterThanOrEqual(1, 13, 0)) {
            // Sticky piston arm collision was added
            registerOverrideOutput(BlockMapping.of("minecraft:stickyPistonArmCollision", ChunkerVanillaBlockType.PISTON_HEAD, BedrockStateGroups.PISTON_HEAD, VanillaBlockStates.PISTON_TYPE, PistonType.STICKY));

            // Legacy aliases
            registerDuplicateOutput(BlockMapping.of("minecraft:stickypistonarmcollision", ChunkerVanillaBlockType.PISTON_HEAD, BedrockStateGroups.PISTON_HEAD, VanillaBlockStates.PISTON_TYPE, PistonType.STICKY));

            // For purpur / quartz, direction became pillar_axis
            registerOverrideOutput(BlockMapping.of("minecraft:purpur_block", "chisel_type", "default", "pillar_axis", "y", ChunkerVanillaBlockType.PURPUR_BLOCK));
            registerOverrideOutput(BlockMapping.of("minecraft:quartz_block", "chisel_type", "default", "pillar_axis", "y", ChunkerVanillaBlockType.QUARTZ_BLOCK));
            registerOverrideOutput(BlockMapping.of("minecraft:quartz_block", "chisel_type", "chiseled", "pillar_axis", "y", ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));
            registerOverrideOutput(BlockMapping.of("minecraft:quartz_block", "chisel_type", "smooth", "pillar_axis", "y", ChunkerVanillaBlockType.SMOOTH_QUARTZ));


            // Added dead_bit to coral
            registerOverrideOutput(BlockMapping.flatten("minecraft:coral", "coral_color", "dead_bit",
                    ImmutableMultimap.<Pair<String, Boolean>, ChunkerVanillaBlockType>builder()
                            .put(Pair.of("red", false), ChunkerVanillaBlockType.FIRE_CORAL)
                            .put(Pair.of("blue", false), ChunkerVanillaBlockType.TUBE_CORAL)
                            .put(Pair.of("purple", false), ChunkerVanillaBlockType.BUBBLE_CORAL)
                            .put(Pair.of("yellow", false), ChunkerVanillaBlockType.HORN_CORAL)
                            .put(Pair.of("pink", false), ChunkerVanillaBlockType.BRAIN_CORAL)
                            .build()
            ));
            register(BlockMapping.flatten("minecraft:coral", "coral_color", "dead_bit",
                    ImmutableMultimap.<Pair<String, Boolean>, ChunkerVanillaBlockType>builder()
                            .put(Pair.of("red", true), ChunkerVanillaBlockType.DEAD_FIRE_CORAL)
                            .put(Pair.of("blue", true), ChunkerVanillaBlockType.DEAD_TUBE_CORAL)
                            .put(Pair.of("purple", true), ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL)
                            .put(Pair.of("yellow", true), ChunkerVanillaBlockType.DEAD_HORN_CORAL)
                            .put(Pair.of("pink", true), ChunkerVanillaBlockType.DEAD_BRAIN_CORAL)
                            .build()
            ));

            // New blocks
            register(BlockMapping.of("minecraft:light_block", ChunkerVanillaBlockType.LIGHT, BedrockStateGroups.LIGHT_BLOCK));
            register(BlockMapping.of("minecraft:wither_rose", ChunkerVanillaBlockType.WITHER_ROSE));
            register(BlockMapping.of("minecraft:structure_void", ChunkerVanillaBlockType.STRUCTURE_VOID, BedrockStateGroups.STRUCTURE_VOID));
        }

        // R14
        if (version.isGreaterThanOrEqual(1, 14, 0)) {
            registerOverrideOutput(BlockMapping.of("minecraft:kelp", "kelp_age", 25, ChunkerVanillaBlockType.KELP_PLANT));
            register(BlockMapping.of("minecraft:honey_block", ChunkerVanillaBlockType.HONEY_BLOCK));
            register(BlockMapping.of("minecraft:honeycomb_block", ChunkerVanillaBlockType.HONEYCOMB_BLOCK));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:beehive", ChunkerVanillaBlockType.BEEHIVE)
                            .put("minecraft:bee_nest", ChunkerVanillaBlockType.BEE_NEST)
                            .build(),
                    BedrockStateGroups.BEE_NEST));
        }

        // R16
        if (version.isGreaterThanOrEqual(1, 16, 0)) {
            // Added facing_direction of 0 for pumpkin/melon
            registerOverrideOutput(BlockMapping.group("facing_direction", 0, ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:pumpkin_stem", ChunkerVanillaBlockType.PUMPKIN_STEM)
                            .put("minecraft:melon_stem", ChunkerVanillaBlockType.MELON_STEM)
                            .build(),
                    BedrockStateGroups.CROP));

            register(BlockMapping.of("minecraft:ancient_debris", ChunkerVanillaBlockType.ANCIENT_DEBRIS));
            register(BlockMapping.of("minecraft:blackstone", ChunkerVanillaBlockType.BLACKSTONE));
            register(BlockMapping.of("minecraft:blackstone_double_slab", ChunkerVanillaBlockType.BLACKSTONE_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:chain", ChunkerVanillaBlockType.CHAIN, BedrockStateGroups.CHAIN));
            register(BlockMapping.of("minecraft:chiseled_nether_bricks", ChunkerVanillaBlockType.CHISELED_NETHER_BRICKS));
            register(BlockMapping.of("minecraft:chiseled_polished_blackstone", ChunkerVanillaBlockType.CHISELED_POLISHED_BLACKSTONE));
            register(BlockMapping.of("minecraft:cracked_nether_bricks", ChunkerVanillaBlockType.CRACKED_NETHER_BRICKS));
            register(BlockMapping.of("minecraft:cracked_polished_blackstone_bricks", ChunkerVanillaBlockType.CRACKED_POLISHED_BLACKSTONE_BRICKS));
            register(BlockMapping.of("minecraft:crimson_fungus", ChunkerVanillaBlockType.CRIMSON_FUNGUS));
            register(BlockMapping.of("minecraft:crimson_nylium", ChunkerVanillaBlockType.CRIMSON_NYLIUM));
            register(BlockMapping.of("minecraft:crimson_planks", ChunkerVanillaBlockType.CRIMSON_PLANKS));
            register(BlockMapping.of("minecraft:crimson_roots", ChunkerVanillaBlockType.CRIMSON_ROOTS));
            register(BlockMapping.of("minecraft:crimson_double_slab", ChunkerVanillaBlockType.CRIMSON_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:crying_obsidian", ChunkerVanillaBlockType.CRYING_OBSIDIAN));
            register(BlockMapping.of("minecraft:gilded_blackstone", ChunkerVanillaBlockType.GILDED_BLACKSTONE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:polished_blackstone_wall", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_WALL)
                            .put("minecraft:blackstone_wall", ChunkerVanillaBlockType.BLACKSTONE_WALL)
                            .put("minecraft:polished_blackstone_brick_wall", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICK_WALL)
                            .build(),
                    BedrockStateGroups.WALL));

            // Lodestone block (on some previews it was used as _block, so upgrade those)
            register(BlockMapping.of("minecraft:lodestone", ChunkerVanillaBlockType.LODESTONE));
            registerDuplicateOutput(BlockMapping.of("minecraft:lodestone_block", ChunkerVanillaBlockType.LODESTONE));

            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:warped_slab", ChunkerVanillaBlockType.WARPED_SLAB)
                            .put("minecraft:polished_blackstone_brick_slab", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICK_SLAB)
                            .put("minecraft:polished_blackstone_slab", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_SLAB)
                            .put("minecraft:crimson_slab", ChunkerVanillaBlockType.CRIMSON_SLAB)
                            .put("minecraft:blackstone_slab", ChunkerVanillaBlockType.BLACKSTONE_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_stairs", ChunkerVanillaBlockType.CRIMSON_STAIRS)
                            .put("minecraft:blackstone_stairs", ChunkerVanillaBlockType.BLACKSTONE_STAIRS)
                            .put("minecraft:polished_blackstone_brick_stairs", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICK_STAIRS)
                            .put("minecraft:warped_stairs", ChunkerVanillaBlockType.WARPED_STAIRS)
                            .put("minecraft:polished_blackstone_stairs", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_STAIRS)
                            .build(),
                    BedrockStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:nether_gold_ore", ChunkerVanillaBlockType.NETHER_GOLD_ORE));
            register(BlockMapping.of("minecraft:nether_sprouts", ChunkerVanillaBlockType.NETHER_SPROUTS));
            register(BlockMapping.of("minecraft:netherite_block", ChunkerVanillaBlockType.NETHERITE_BLOCK));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_door", ChunkerVanillaBlockType.CRIMSON_DOOR)
                            .put("minecraft:warped_door", ChunkerVanillaBlockType.WARPED_DOOR)
                            .build(),
                    BedrockStateGroups.DOOR));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:warped_fence", ChunkerVanillaBlockType.WARPED_FENCE)
                            .put("minecraft:crimson_fence", ChunkerVanillaBlockType.CRIMSON_FENCE)
                            .build(),
                    BedrockStateGroups.CONNECTABLE_HORIZONTAL));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_fence_gate", ChunkerVanillaBlockType.CRIMSON_FENCE_GATE)
                            .put("minecraft:warped_fence_gate", ChunkerVanillaBlockType.WARPED_FENCE_GATE)
                            .build(),
                    BedrockStateGroups.FENCE_GATE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_standing_sign", ChunkerVanillaBlockType.CRIMSON_SIGN)
                            .put("minecraft:warped_standing_sign", ChunkerVanillaBlockType.WARPED_SIGN)
                            .build(),
                    BedrockStateGroups.SIGN));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_trapdoor", ChunkerVanillaBlockType.CRIMSON_TRAPDOOR)
                            .put("minecraft:warped_trapdoor", ChunkerVanillaBlockType.WARPED_TRAPDOOR)
                            .build(),
                    BedrockStateGroups.TRAPDOOR));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_wall_sign", ChunkerVanillaBlockType.CRIMSON_WALL_SIGN)
                            .put("minecraft:warped_wall_sign", ChunkerVanillaBlockType.WARPED_WALL_SIGN)
                            .build(),
                    BedrockStateGroups.FACING_DIRECTION_HORIZONTAL));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:warped_button", ChunkerVanillaBlockType.WARPED_BUTTON)
                            .put("minecraft:polished_blackstone_button", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BUTTON)
                            .put("minecraft:crimson_button", ChunkerVanillaBlockType.CRIMSON_BUTTON)
                            .build(),
                    BedrockStateGroups.BUTTON));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_pressure_plate", ChunkerVanillaBlockType.CRIMSON_PRESSURE_PLATE)
                            .put("minecraft:warped_pressure_plate", ChunkerVanillaBlockType.WARPED_PRESSURE_PLATE)
                            .put("minecraft:polished_blackstone_pressure_plate", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_PRESSURE_PLATE)
                            .build(),
                    BedrockStateGroups.PRESSURE_PLATE));
            register(BlockMapping.of("minecraft:polished_blackstone", ChunkerVanillaBlockType.POLISHED_BLACKSTONE));
            register(BlockMapping.of("minecraft:polished_blackstone_brick_double_slab", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICK_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:polished_blackstone_bricks", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICKS));
            register(BlockMapping.of("minecraft:polished_blackstone_double_slab", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:basalt", ChunkerVanillaBlockType.BASALT)
                            .put("minecraft:warped_hyphae", ChunkerVanillaBlockType.WARPED_HYPHAE)
                            .put("minecraft:warped_stem", ChunkerVanillaBlockType.WARPED_STEM)
                            .put("minecraft:polished_basalt", ChunkerVanillaBlockType.POLISHED_BASALT)
                            .put("minecraft:crimson_hyphae", ChunkerVanillaBlockType.CRIMSON_HYPHAE)
                            .put("minecraft:crimson_stem", ChunkerVanillaBlockType.CRIMSON_STEM)
                            .build(),
                    BedrockStateGroups.PILLAR_BLOCK));

            // Stripped nether wood
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:stripped_crimson_stem", ChunkerVanillaBlockType.STRIPPED_CRIMSON_STEM)
                            .put("minecraft:stripped_warped_stem", ChunkerVanillaBlockType.STRIPPED_WARPED_STEM)
                            .put("minecraft:stripped_crimson_hyphae", ChunkerVanillaBlockType.STRIPPED_CRIMSON_HYPHAE)
                            .put("minecraft:stripped_warped_hyphae", ChunkerVanillaBlockType.STRIPPED_WARPED_HYPHAE)
                            .build(),
                    BedrockStateGroups.STRIPPED_NETHER_WOOD));

            register(BlockMapping.of("minecraft:quartz_bricks", ChunkerVanillaBlockType.QUARTZ_BRICKS));
            register(BlockMapping.of("minecraft:respawn_anchor", ChunkerVanillaBlockType.RESPAWN_ANCHOR, BedrockStateGroups.RESPAWN_ANCHOR));
            register(BlockMapping.of("minecraft:shroomlight", ChunkerVanillaBlockType.SHROOMLIGHT));
            register(BlockMapping.of("minecraft:soul_campfire", ChunkerVanillaBlockType.SOUL_CAMPFIRE, BedrockStateGroups.CAMPFIRE));
            register(BlockMapping.of("minecraft:soul_fire", ChunkerVanillaBlockType.SOUL_FIRE, BedrockStateGroups.SOUL_FIRE));
            register(BlockMapping.of("minecraft:soul_lantern", ChunkerVanillaBlockType.SOUL_LANTERN, BedrockStateGroups.LANTERN));
            register(BlockMapping.of("minecraft:soul_soil", ChunkerVanillaBlockType.SOUL_SOIL));
            register(BlockMapping.of("minecraft:target", ChunkerVanillaBlockType.TARGET, VanillaBlockStates.POWER, Power._0));
            registerDuplicateInput(BlockMapping.of("minecraft:target", ChunkerVanillaBlockType.TARGET));
            register(BlockMapping.of("minecraft:warped_fungus", ChunkerVanillaBlockType.WARPED_FUNGUS));
            register(BlockMapping.of("minecraft:warped_nylium", ChunkerVanillaBlockType.WARPED_NYLIUM));
            register(BlockMapping.of("minecraft:warped_planks", ChunkerVanillaBlockType.WARPED_PLANKS));
            register(BlockMapping.of("minecraft:warped_roots", ChunkerVanillaBlockType.WARPED_ROOTS));
            register(BlockMapping.of("minecraft:warped_double_slab", ChunkerVanillaBlockType.WARPED_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:warped_wart_block", ChunkerVanillaBlockType.WARPED_WART_BLOCK));
            register(BlockMapping.of("minecraft:weeping_vines", ChunkerVanillaBlockType.WEEPING_VINES, BedrockStateGroups.WEEPING_VINES));
            register(BlockMapping.of("minecraft:weeping_vines", "weeping_vines_age", 25, ChunkerVanillaBlockType.WEEPING_VINES_PLANT));

            // Twisting vines (on some previews it had _block)
            register(BlockMapping.of("minecraft:twisting_vines", ChunkerVanillaBlockType.TWISTING_VINES, BedrockStateGroups.TWISTING_VINES));
            register(BlockMapping.of("minecraft:twisting_vines", "twisting_vines_age", 25, ChunkerVanillaBlockType.TWISTING_VINES_PLANT));
            registerDuplicateOutput(BlockMapping.of("minecraft:twisting_vines_block", ChunkerVanillaBlockType.TWISTING_VINES, BedrockStateGroups.TWISTING_VINES));
            registerDuplicateOutput(BlockMapping.of("minecraft:twisting_vines_block", "twisting_vines_age", 25, ChunkerVanillaBlockType.TWISTING_VINES_PLANT));

            // Soul Torch (normal/wall)
            register(BlockMapping.of("minecraft:soul_torch", "torch_facing_direction", "top", ChunkerVanillaBlockType.SOUL_TORCH));
            registerDuplicateOutput(BlockMapping.of("minecraft:soul_torch", "torch_facing_direction", "unknown", ChunkerVanillaBlockType.SOUL_TORCH));
            register(BlockMapping.of("minecraft:soul_torch", ChunkerVanillaBlockType.SOUL_WALL_TORCH, BedrockStateGroups.TORCH_FACING));
        }

        // R17
        if (version.isGreaterThanOrEqual(1, 17, 0)) {
            // Snow cauldron
            register(BlockMapping.of("minecraft:cauldron", "cauldron_liquid", "powder_snow", ChunkerVanillaBlockType.POWDER_SNOW_CAULDRON, BedrockStateGroups.CAULDRON));

            register(BlockMapping.of("minecraft:amethyst_block", ChunkerVanillaBlockType.AMETHYST_BLOCK));
            register(BlockMapping.of("minecraft:azalea", ChunkerVanillaBlockType.AZALEA));
            register(BlockMapping.of("minecraft:big_dripleaf", "big_dripleaf_head", true, ChunkerVanillaBlockType.BIG_DRIPLEAF, BedrockStateGroups.BIG_DRIPLEAF));
            register(BlockMapping.of("minecraft:big_dripleaf", "big_dripleaf_head", false, ChunkerVanillaBlockType.BIG_DRIPLEAF_STEM, BedrockStateGroups.BIG_DRIPLEAF_HEAD));
            register(BlockMapping.of("minecraft:budding_amethyst", ChunkerVanillaBlockType.BUDDING_AMETHYST));
            register(BlockMapping.of("minecraft:calcite", ChunkerVanillaBlockType.CALCITE));

            // Cave Vines - Heads
            register(BlockMapping.of("minecraft:cave_vines_head_with_berries", ChunkerVanillaBlockType.CAVE_VINES_HEAD, BedrockStateGroups.CAVE_VINES, VanillaBlockStates.BERRIES, Bool.TRUE));
            register(BlockMapping.of("minecraft:cave_vines", ChunkerVanillaBlockType.CAVE_VINES_HEAD, BedrockStateGroups.CAVE_VINES, VanillaBlockStates.BERRIES, Bool.FALSE));

            // Cave vines - Body
            register(BlockMapping.of("minecraft:cave_vines_body_with_berries", ChunkerVanillaBlockType.CAVE_VINES_BODY, BedrockStateGroups.CAVE_VINES, VanillaBlockStates.BERRIES, Bool.TRUE));

            // The no berries variant doesn't exist on Bedrock for the body, so we'll use the body with 25 for age
            register(BlockMapping.of("minecraft:cave_vines", "growing_plant_age", 25, ChunkerVanillaBlockType.CAVE_VINES_BODY, Map.of(VanillaBlockStates.BERRIES, Bool.FALSE, VanillaBlockStates.AGE_25, Age_25._25)));
            registerDuplicateInput(BlockMapping.of("minecraft:cave_vines", "growing_plant_age", 25, ChunkerVanillaBlockType.CAVE_VINES_BODY, VanillaBlockStates.BERRIES, Bool.FALSE));

            register(BlockMapping.of("minecraft:chiseled_deepslate", ChunkerVanillaBlockType.CHISELED_DEEPSLATE));
            register(BlockMapping.of("minecraft:cobbled_deepslate", ChunkerVanillaBlockType.COBBLED_DEEPSLATE));
            register(BlockMapping.of("minecraft:cobbled_deepslate_double_slab", ChunkerVanillaBlockType.COBBLED_DEEPSLATE_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:azalea_leaves_flowered", ChunkerVanillaBlockType.FLOWERING_AZALEA_LEAVES)
                            .put("minecraft:azalea_leaves", ChunkerVanillaBlockType.AZALEA_LEAVES)
                            .build(),
                    BedrockStateGroups.LEAVES));
            register(BlockMapping.of("minecraft:copper_block", ChunkerVanillaBlockType.COPPER_BLOCK));
            register(BlockMapping.of("minecraft:copper_ore", ChunkerVanillaBlockType.COPPER_ORE));
            register(BlockMapping.of("minecraft:cracked_deepslate_bricks", ChunkerVanillaBlockType.CRACKED_DEEPSLATE_BRICKS));
            register(BlockMapping.of("minecraft:cracked_deepslate_tiles", ChunkerVanillaBlockType.CRACKED_DEEPSLATE_TILES));
            register(BlockMapping.of("minecraft:cut_copper", ChunkerVanillaBlockType.CUT_COPPER));
            register(BlockMapping.of("minecraft:double_cut_copper_slab", ChunkerVanillaBlockType.CUT_COPPER_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:deepslate_brick_double_slab", ChunkerVanillaBlockType.DEEPSLATE_BRICK_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:deepslate_bricks", ChunkerVanillaBlockType.DEEPSLATE_BRICKS));
            register(BlockMapping.of("minecraft:deepslate_coal_ore", ChunkerVanillaBlockType.DEEPSLATE_COAL_ORE));
            register(BlockMapping.of("minecraft:deepslate_copper_ore", ChunkerVanillaBlockType.DEEPSLATE_COPPER_ORE));
            register(BlockMapping.of("minecraft:deepslate_diamond_ore", ChunkerVanillaBlockType.DEEPSLATE_DIAMOND_ORE));
            register(BlockMapping.of("minecraft:deepslate_emerald_ore", ChunkerVanillaBlockType.DEEPSLATE_EMERALD_ORE));
            register(BlockMapping.of("minecraft:deepslate_gold_ore", ChunkerVanillaBlockType.DEEPSLATE_GOLD_ORE));
            register(BlockMapping.of("minecraft:deepslate_iron_ore", ChunkerVanillaBlockType.DEEPSLATE_IRON_ORE));
            register(BlockMapping.of("minecraft:deepslate_lapis_ore", ChunkerVanillaBlockType.DEEPSLATE_LAPIS_ORE));

            // Deepslate redstone ore
            register(BlockMapping.of("minecraft:lit_deepslate_redstone_ore", ChunkerVanillaBlockType.DEEPSLATE_REDSTONE_ORE, VanillaBlockStates.LIT, Bool.TRUE));
            register(BlockMapping.of("minecraft:deepslate_redstone_ore", ChunkerVanillaBlockType.DEEPSLATE_REDSTONE_ORE, VanillaBlockStates.LIT, Bool.FALSE));

            register(BlockMapping.of("minecraft:deepslate_tile_double_slab", ChunkerVanillaBlockType.DEEPSLATE_TILE_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:deepslate_tiles", ChunkerVanillaBlockType.DEEPSLATE_TILES));
            register(BlockMapping.of("minecraft:dripstone_block", ChunkerVanillaBlockType.DRIPSTONE_BLOCK));
            register(BlockMapping.of("minecraft:exposed_copper", ChunkerVanillaBlockType.EXPOSED_COPPER));
            register(BlockMapping.of("minecraft:exposed_cut_copper", ChunkerVanillaBlockType.EXPOSED_CUT_COPPER));
            register(BlockMapping.of("minecraft:exposed_double_cut_copper_slab", ChunkerVanillaBlockType.EXPOSED_CUT_COPPER_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:infested_deepslate", ChunkerVanillaBlockType.INFESTED_DEEPSLATE)
                            .put("minecraft:deepslate", ChunkerVanillaBlockType.DEEPSLATE)
                            .build(),
                    BedrockStateGroups.PILLAR_BLOCK));
            register(BlockMapping.of("minecraft:flowering_azalea", ChunkerVanillaBlockType.FLOWERING_AZALEA));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:polished_deepslate_wall", ChunkerVanillaBlockType.POLISHED_DEEPSLATE_WALL)
                            .put("minecraft:cobbled_deepslate_wall", ChunkerVanillaBlockType.COBBLED_DEEPSLATE_WALL)
                            .put("minecraft:deepslate_brick_wall", ChunkerVanillaBlockType.DEEPSLATE_BRICK_WALL)
                            .put("minecraft:deepslate_tile_wall", ChunkerVanillaBlockType.DEEPSLATE_TILE_WALL)
                            .build(),
                    BedrockStateGroups.WALL));
            register(BlockMapping.of("minecraft:glow_lichen", ChunkerVanillaBlockType.GLOW_LICHEN, BedrockStateGroups.MULTIFACE));
            register(BlockMapping.of("minecraft:hanging_roots", ChunkerVanillaBlockType.HANGING_ROOTS));

            // Amethyst
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:large_amethyst_bud", ChunkerVanillaBlockType.LARGE_AMETHYST_BUD)
                            .put("minecraft:small_amethyst_bud", ChunkerVanillaBlockType.SMALL_AMETHYST_BUD)
                            .put("minecraft:amethyst_cluster", ChunkerVanillaBlockType.AMETHYST_CLUSTER)
                            .put("minecraft:medium_amethyst_bud", ChunkerVanillaBlockType.MEDIUM_AMETHYST_BUD)
                            .build(),
                    BedrockStateGroups.FACING_TO_BLOCK_FACE));

            register(BlockMapping.of("minecraft:lightning_rod", ChunkerVanillaBlockType.LIGHTNING_ROD, BedrockStateGroups.LIGHTNING_ROD));
            register(BlockMapping.of("minecraft:moss_block", ChunkerVanillaBlockType.MOSS_BLOCK));
            register(BlockMapping.of("minecraft:moss_carpet", ChunkerVanillaBlockType.MOSS_CARPET));
            register(BlockMapping.of("minecraft:oxidized_copper", ChunkerVanillaBlockType.OXIDIZED_COPPER));
            register(BlockMapping.of("minecraft:oxidized_cut_copper", ChunkerVanillaBlockType.OXIDIZED_CUT_COPPER));
            register(BlockMapping.of("minecraft:oxidized_double_cut_copper_slab", ChunkerVanillaBlockType.OXIDIZED_CUT_COPPER_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:pointed_dripstone", ChunkerVanillaBlockType.POINTED_DRIPSTONE, BedrockStateGroups.POINTED_DRIPSTONE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:exposed_cut_copper_slab", ChunkerVanillaBlockType.EXPOSED_CUT_COPPER_SLAB)
                            .put("minecraft:waxed_oxidized_cut_copper_slab", ChunkerVanillaBlockType.WAXED_OXIDIZED_CUT_COPPER_SLAB)
                            .put("minecraft:oxidized_cut_copper_slab", ChunkerVanillaBlockType.OXIDIZED_CUT_COPPER_SLAB)
                            .put("minecraft:waxed_exposed_cut_copper_slab", ChunkerVanillaBlockType.WAXED_EXPOSED_CUT_COPPER_SLAB)
                            .put("minecraft:waxed_weathered_cut_copper_slab", ChunkerVanillaBlockType.WAXED_WEATHERED_CUT_COPPER_SLAB)
                            .put("minecraft:weathered_cut_copper_slab", ChunkerVanillaBlockType.WEATHERED_CUT_COPPER_SLAB)
                            .put("minecraft:polished_deepslate_slab", ChunkerVanillaBlockType.POLISHED_DEEPSLATE_SLAB)
                            .put("minecraft:deepslate_brick_slab", ChunkerVanillaBlockType.DEEPSLATE_BRICK_SLAB)
                            .put("minecraft:deepslate_tile_slab", ChunkerVanillaBlockType.DEEPSLATE_TILE_SLAB)
                            .put("minecraft:waxed_cut_copper_slab", ChunkerVanillaBlockType.WAXED_CUT_COPPER_SLAB)
                            .put("minecraft:cut_copper_slab", ChunkerVanillaBlockType.CUT_COPPER_SLAB)
                            .put("minecraft:cobbled_deepslate_slab", ChunkerVanillaBlockType.COBBLED_DEEPSLATE_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:deepslate_tile_stairs", ChunkerVanillaBlockType.DEEPSLATE_TILE_STAIRS)
                            .put("minecraft:waxed_oxidized_cut_copper_stairs", ChunkerVanillaBlockType.WAXED_OXIDIZED_CUT_COPPER_STAIRS)
                            .put("minecraft:weathered_cut_copper_stairs", ChunkerVanillaBlockType.WEATHERED_CUT_COPPER_STAIRS)
                            .put("minecraft:waxed_exposed_cut_copper_stairs", ChunkerVanillaBlockType.WAXED_EXPOSED_CUT_COPPER_STAIRS)
                            .put("minecraft:polished_deepslate_stairs", ChunkerVanillaBlockType.POLISHED_DEEPSLATE_STAIRS)
                            .put("minecraft:exposed_cut_copper_stairs", ChunkerVanillaBlockType.EXPOSED_CUT_COPPER_STAIRS)
                            .put("minecraft:waxed_cut_copper_stairs", ChunkerVanillaBlockType.WAXED_CUT_COPPER_STAIRS)
                            .put("minecraft:cobbled_deepslate_stairs", ChunkerVanillaBlockType.COBBLED_DEEPSLATE_STAIRS)
                            .put("minecraft:oxidized_cut_copper_stairs", ChunkerVanillaBlockType.OXIDIZED_CUT_COPPER_STAIRS)
                            .put("minecraft:cut_copper_stairs", ChunkerVanillaBlockType.CUT_COPPER_STAIRS)
                            .put("minecraft:deepslate_brick_stairs", ChunkerVanillaBlockType.DEEPSLATE_BRICK_STAIRS)
                            .put("minecraft:waxed_weathered_cut_copper_stairs", ChunkerVanillaBlockType.WAXED_WEATHERED_CUT_COPPER_STAIRS)
                            .build(),
                    BedrockStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:polished_deepslate", ChunkerVanillaBlockType.POLISHED_DEEPSLATE));
            register(BlockMapping.of("minecraft:polished_deepslate_double_slab", ChunkerVanillaBlockType.POLISHED_DEEPSLATE_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:powder_snow", ChunkerVanillaBlockType.POWDER_SNOW));
            register(BlockMapping.of("minecraft:raw_copper_block", ChunkerVanillaBlockType.RAW_COPPER_BLOCK));
            register(BlockMapping.of("minecraft:raw_gold_block", ChunkerVanillaBlockType.RAW_GOLD_BLOCK));
            register(BlockMapping.of("minecraft:raw_iron_block", ChunkerVanillaBlockType.RAW_IRON_BLOCK));
            register(BlockMapping.of("minecraft:dirt_with_roots", ChunkerVanillaBlockType.ROOTED_DIRT));
            register(BlockMapping.of("minecraft:sculk_sensor", ChunkerVanillaBlockType.SCULK_SENSOR, BedrockStateGroups.SCULK_SENSOR));
            register(BlockMapping.of("minecraft:small_dripleaf_block", ChunkerVanillaBlockType.SMALL_DRIPLEAF, BedrockStateGroups.SMALL_DRIPLEAF));
            register(BlockMapping.of("minecraft:smooth_basalt", ChunkerVanillaBlockType.SMOOTH_BASALT));
            register(BlockMapping.of("minecraft:spore_blossom", ChunkerVanillaBlockType.SPORE_BLOSSOM));
            register(BlockMapping.of("minecraft:tinted_glass", ChunkerVanillaBlockType.TINTED_GLASS));
            register(BlockMapping.of("minecraft:tuff", ChunkerVanillaBlockType.TUFF));
            register(BlockMapping.of("minecraft:waxed_copper", ChunkerVanillaBlockType.WAXED_COPPER_BLOCK));
            register(BlockMapping.of("minecraft:waxed_cut_copper", ChunkerVanillaBlockType.WAXED_CUT_COPPER));
            register(BlockMapping.of("minecraft:waxed_double_cut_copper_slab", ChunkerVanillaBlockType.WAXED_CUT_COPPER_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:waxed_exposed_copper", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER));
            register(BlockMapping.of("minecraft:waxed_exposed_cut_copper", ChunkerVanillaBlockType.WAXED_EXPOSED_CUT_COPPER));
            register(BlockMapping.of("minecraft:waxed_exposed_double_cut_copper_slab", ChunkerVanillaBlockType.WAXED_EXPOSED_CUT_COPPER_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:waxed_oxidized_copper", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER));
            register(BlockMapping.of("minecraft:waxed_oxidized_cut_copper", ChunkerVanillaBlockType.WAXED_OXIDIZED_CUT_COPPER));
            register(BlockMapping.of("minecraft:waxed_oxidized_double_cut_copper_slab", ChunkerVanillaBlockType.WAXED_OXIDIZED_CUT_COPPER_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:waxed_weathered_copper", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER));
            register(BlockMapping.of("minecraft:waxed_weathered_cut_copper", ChunkerVanillaBlockType.WAXED_WEATHERED_CUT_COPPER));
            register(BlockMapping.of("minecraft:waxed_weathered_double_cut_copper_slab", ChunkerVanillaBlockType.WAXED_WEATHERED_CUT_COPPER_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:weathered_copper", ChunkerVanillaBlockType.WEATHERED_COPPER));
            register(BlockMapping.of("minecraft:weathered_cut_copper", ChunkerVanillaBlockType.WEATHERED_CUT_COPPER));
            register(BlockMapping.of("minecraft:weathered_double_cut_copper_slab", ChunkerVanillaBlockType.WEATHERED_CUT_COPPER_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:glow_frame", ChunkerVanillaBlockType.ITEM_FRAME_BEDROCK, BedrockStateGroups.FRAME, VanillaBlockStates.LIT, Bool.TRUE)); // Needs to be turned into an entity
        }

        // R17U1
        if (version.isGreaterThanOrEqual(1, 17, 10)) {
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:yellow_candle", ChunkerVanillaBlockType.YELLOW_CANDLE)
                            .put("minecraft:pink_candle", ChunkerVanillaBlockType.PINK_CANDLE)
                            .put("minecraft:brown_candle", ChunkerVanillaBlockType.BROWN_CANDLE)
                            .put("minecraft:gray_candle", ChunkerVanillaBlockType.GRAY_CANDLE)
                            .put("minecraft:cyan_candle", ChunkerVanillaBlockType.CYAN_CANDLE)
                            .put("minecraft:black_candle", ChunkerVanillaBlockType.BLACK_CANDLE)
                            .put("minecraft:purple_candle", ChunkerVanillaBlockType.PURPLE_CANDLE)
                            .put("minecraft:light_blue_candle", ChunkerVanillaBlockType.LIGHT_BLUE_CANDLE)
                            .put("minecraft:light_gray_candle", ChunkerVanillaBlockType.LIGHT_GRAY_CANDLE)
                            .put("minecraft:orange_candle", ChunkerVanillaBlockType.ORANGE_CANDLE)
                            .put("minecraft:candle", ChunkerVanillaBlockType.CANDLE)
                            .put("minecraft:blue_candle", ChunkerVanillaBlockType.BLUE_CANDLE)
                            .put("minecraft:green_candle", ChunkerVanillaBlockType.GREEN_CANDLE)
                            .put("minecraft:white_candle", ChunkerVanillaBlockType.WHITE_CANDLE)
                            .put("minecraft:red_candle", ChunkerVanillaBlockType.RED_CANDLE)
                            .put("minecraft:magenta_candle", ChunkerVanillaBlockType.MAGENTA_CANDLE)
                            .put("minecraft:lime_candle", ChunkerVanillaBlockType.LIME_CANDLE)
                            .build(),
                    BedrockStateGroups.CANDLE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:purple_candle_cake", ChunkerVanillaBlockType.PURPLE_CANDLE_CAKE)
                            .put("minecraft:yellow_candle_cake", ChunkerVanillaBlockType.YELLOW_CANDLE_CAKE)
                            .put("minecraft:magenta_candle_cake", ChunkerVanillaBlockType.MAGENTA_CANDLE_CAKE)
                            .put("minecraft:candle_cake", ChunkerVanillaBlockType.CANDLE_CAKE)
                            .put("minecraft:black_candle_cake", ChunkerVanillaBlockType.BLACK_CANDLE_CAKE)
                            .put("minecraft:light_blue_candle_cake", ChunkerVanillaBlockType.LIGHT_BLUE_CANDLE_CAKE)
                            .put("minecraft:white_candle_cake", ChunkerVanillaBlockType.WHITE_CANDLE_CAKE)
                            .put("minecraft:green_candle_cake", ChunkerVanillaBlockType.GREEN_CANDLE_CAKE)
                            .put("minecraft:orange_candle_cake", ChunkerVanillaBlockType.ORANGE_CANDLE_CAKE)
                            .put("minecraft:blue_candle_cake", ChunkerVanillaBlockType.BLUE_CANDLE_CAKE)
                            .put("minecraft:brown_candle_cake", ChunkerVanillaBlockType.BROWN_CANDLE_CAKE)
                            .put("minecraft:gray_candle_cake", ChunkerVanillaBlockType.GRAY_CANDLE_CAKE)
                            .put("minecraft:red_candle_cake", ChunkerVanillaBlockType.RED_CANDLE_CAKE)
                            .put("minecraft:light_gray_candle_cake", ChunkerVanillaBlockType.LIGHT_GRAY_CANDLE_CAKE)
                            .put("minecraft:pink_candle_cake", ChunkerVanillaBlockType.PINK_CANDLE_CAKE)
                            .put("minecraft:lime_candle_cake", ChunkerVanillaBlockType.LIME_CANDLE_CAKE)
                            .put("minecraft:cyan_candle_cake", ChunkerVanillaBlockType.CYAN_CANDLE_CAKE)
                            .build(),
                    BedrockStateGroups.CANDLE_CAKE));
        }

        // R17U3
        if (version.isGreaterThanOrEqual(1, 17, 30)) {
            register(BlockMapping.of("minecraft:sculk", ChunkerVanillaBlockType.SCULK));
            register(BlockMapping.of("minecraft:sculk_catalyst", ChunkerVanillaBlockType.SCULK_CATALYST, BedrockStateGroups.SCULK_CATALYST));
            register(BlockMapping.of("minecraft:sculk_shrieker", ChunkerVanillaBlockType.SCULK_SHRIEKER, BedrockStateGroups.SCULK_SHRIEKER));
            register(BlockMapping.of("minecraft:sculk_vein", ChunkerVanillaBlockType.SCULK_VEIN, BedrockStateGroups.MULTIFACE));
        }

        // R18U1
        if (version.isGreaterThanOrEqual(1, 18, 10)) {
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:pearlescent_froglight", ChunkerVanillaBlockType.PEARLESCENT_FROGLIGHT)
                            .put("minecraft:ochre_froglight", ChunkerVanillaBlockType.OCHRE_FROGLIGHT)
                            .put("minecraft:verdant_froglight", ChunkerVanillaBlockType.VERDANT_FROGLIGHT)
                            .build(),
                    BedrockStateGroups.FROGLIGHT));

            // Added frog_egg
            register(BlockMapping.of("minecraft:frog_egg", ChunkerVanillaBlockType.FROGSPAWN));
        }

        // R18U3
        if (version.isGreaterThanOrEqual(1, 18, 30)) {
            // Renamed from pistonArmCollision to piston_arm_collision
            registerOverrideOutput(BlockMapping.of("minecraft:sticky_piston_arm_collision", ChunkerVanillaBlockType.PISTON_HEAD, BedrockStateGroups.PISTON_HEAD, VanillaBlockStates.PISTON_TYPE, PistonType.STICKY));
            registerOverrideOutput(BlockMapping.of("minecraft:piston_arm_collision", ChunkerVanillaBlockType.PISTON_HEAD, BedrockStateGroups.PISTON_HEAD, VanillaBlockStates.PISTON_TYPE, PistonType.NORMAL));

            // Renamed from seaLantern to sea_lantern
            registerOverrideOutput(BlockMapping.of("minecraft:sea_lantern", ChunkerVanillaBlockType.SEA_LANTERN));

            // Renamed from tripWire to trip_wire
            registerOverrideOutput(BlockMapping.of("minecraft:trip_wire", ChunkerVanillaBlockType.TRIPWIRE, BedrockStateGroups.TRIPWIRE));

            // Renamed from concretePowder to concrete_powder
            registerOverrideOutput(BlockMapping.flatten("minecraft:concrete_powder", "color",
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("purple", ChunkerVanillaBlockType.PURPLE_CONCRETE_POWDER)
                            .put("orange", ChunkerVanillaBlockType.ORANGE_CONCRETE_POWDER)
                            .put("pink", ChunkerVanillaBlockType.PINK_CONCRETE_POWDER)
                            .put("lime", ChunkerVanillaBlockType.LIME_CONCRETE_POWDER)
                            .put("cyan", ChunkerVanillaBlockType.CYAN_CONCRETE_POWDER)
                            .put("white", ChunkerVanillaBlockType.WHITE_CONCRETE_POWDER)
                            .put("black", ChunkerVanillaBlockType.BLACK_CONCRETE_POWDER)
                            .put("light_blue", ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE_POWDER)
                            .put("blue", ChunkerVanillaBlockType.BLUE_CONCRETE_POWDER)
                            .put("gray", ChunkerVanillaBlockType.GRAY_CONCRETE_POWDER)
                            .put("red", ChunkerVanillaBlockType.RED_CONCRETE_POWDER)
                            .put("green", ChunkerVanillaBlockType.GREEN_CONCRETE_POWDER)
                            .put("brown", ChunkerVanillaBlockType.BROWN_CONCRETE_POWDER)
                            .put("silver", ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE_POWDER)
                            .put("yellow", ChunkerVanillaBlockType.YELLOW_CONCRETE_POWDER)
                            .put("magenta", ChunkerVanillaBlockType.MAGENTA_CONCRETE_POWDER)
                            .build()
            ));

            // Renamed from movingBlock -> moving_block, Chunker doesn't support this block however
            registerOverrideOutput(BlockMapping.of("minecraft:moving_block", ChunkerVanillaBlockType.MOVING_BLOCK_BEDROCK));

            // Renamed from invisibleBedrock -> invisible_bedrock
            registerOverrideOutput(BlockMapping.of("minecraft:invisible_bedrock", ChunkerVanillaBlockType.INVISIBLE_BEDROCK));

            // Frog spawn got renamed
            registerOverrideOutput(BlockMapping.of("minecraft:frog_spawn", ChunkerVanillaBlockType.FROGSPAWN));

            // New blocks
            register(BlockMapping.of("minecraft:mangrove_leaves", ChunkerVanillaBlockType.MANGROVE_LEAVES, BedrockStateGroups.LEAVES));
            register(BlockMapping.of("minecraft:mud", ChunkerVanillaBlockType.MUD));
            register(BlockMapping.of("minecraft:mud_brick_double_slab", ChunkerVanillaBlockType.MUD_BRICK_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:mud_brick_slab", ChunkerVanillaBlockType.MUD_BRICK_SLAB, BedrockStateGroups.SLAB_HALF));
            register(BlockMapping.of("minecraft:mud_brick_stairs", ChunkerVanillaBlockType.MUD_BRICK_STAIRS, BedrockStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:mud_brick_wall", ChunkerVanillaBlockType.MUD_BRICK_WALL, BedrockStateGroups.WALL));
            register(BlockMapping.of("minecraft:mud_bricks", ChunkerVanillaBlockType.MUD_BRICKS));
            register(BlockMapping.of("minecraft:packed_mud", ChunkerVanillaBlockType.PACKED_MUD));
            register(BlockMapping.of("minecraft:reinforced_deepslate", ChunkerVanillaBlockType.REINFORCED_DEEPSLATE));

            // Mangrove propagule
            register(BlockMapping.of("minecraft:mangrove_propagule", ChunkerVanillaBlockType.MANGROVE_PROPAGULE, BedrockStateGroups.MANGROVE_PROPAGULE, VanillaBlockStates.HANGING, Bool.FALSE));
            register(BlockMapping.of("minecraft:mangrove_propagule_hanging", ChunkerVanillaBlockType.MANGROVE_PROPAGULE, BedrockStateGroups.MANGROVE_PROPAGULE, VanillaBlockStates.HANGING, Bool.TRUE));
            registerDuplicateInput(BlockMapping.of("minecraft:mangrove_propagule", ChunkerVanillaBlockType.MANGROVE_PROPAGULE, BedrockStateGroups.MANGROVE_PROPAGULE));
        }

        // R19
        if (version.isGreaterThanOrEqual(1, 19, 0)) {
            // mangrove_propagule got a hanging state instead of being two blocks
            registerOverrideOutput(BlockMapping.of("minecraft:mangrove_propagule", "hanging", false, ChunkerVanillaBlockType.MANGROVE_PROPAGULE, BedrockStateGroups.MANGROVE_PROPAGULE, VanillaBlockStates.HANGING, Bool.FALSE));
            registerOverrideOutput(BlockMapping.of("minecraft:mangrove_propagule", "hanging", true, ChunkerVanillaBlockType.MANGROVE_PROPAGULE, BedrockStateGroups.MANGROVE_PROPAGULE, VanillaBlockStates.HANGING, Bool.TRUE));
            registerDuplicateOverrideOutput(BlockMapping.of("minecraft:mangrove_propagule", "hanging", false, ChunkerVanillaBlockType.MANGROVE_PROPAGULE, BedrockStateGroups.MANGROVE_PROPAGULE));

            // Stone slabs got renamed
            registerOverrideOutput(BlockMapping.flatten("minecraft:stone_block_slab", "stone_slab_type",
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("brick", ChunkerVanillaBlockType.BRICK_SLAB)
                            .put("smooth_stone", ChunkerVanillaBlockType.SMOOTH_STONE_SLAB)
                            .put("stone_brick", ChunkerVanillaBlockType.STONE_BRICK_SLAB)
                            .put("cobblestone", ChunkerVanillaBlockType.COBBLESTONE_SLAB)
                            .put("nether_brick", ChunkerVanillaBlockType.NETHER_BRICK_SLAB)
                            .put("quartz", ChunkerVanillaBlockType.QUARTZ_SLAB)
                            .put("sandstone", ChunkerVanillaBlockType.SANDSTONE_SLAB)
                            .put("wood", ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));

            registerOverrideOutput(BlockMapping.flatten("minecraft:double_stone_block_slab", "stone_slab_type",
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("stone_brick", ChunkerVanillaBlockType.STONE_BRICK_SLAB)
                            .put("cobblestone", ChunkerVanillaBlockType.COBBLESTONE_SLAB)
                            .put("nether_brick", ChunkerVanillaBlockType.NETHER_BRICK_SLAB)
                            .put("sandstone", ChunkerVanillaBlockType.SANDSTONE_SLAB)
                            .put("brick", ChunkerVanillaBlockType.BRICK_SLAB)
                            .put("quartz", ChunkerVanillaBlockType.QUARTZ_SLAB)
                            .put("smooth_stone", ChunkerVanillaBlockType.SMOOTH_STONE_SLAB)
                            .put("wood", ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

            registerOverrideOutput(BlockMapping.flatten("minecraft:stone_block_slab2", "stone_slab_type_2",
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("prismarine_rough", ChunkerVanillaBlockType.PRISMARINE_SLAB)
                            .put("purpur", ChunkerVanillaBlockType.PURPUR_SLAB)
                            .put("mossy_cobblestone", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB)
                            .put("prismarine_brick", ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB)
                            .put("red_sandstone", ChunkerVanillaBlockType.RED_SANDSTONE_SLAB)
                            .put("red_nether_brick", ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB)
                            .put("prismarine_dark", ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB)
                            .put("smooth_sandstone", ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            registerOverrideOutput(BlockMapping.flatten("minecraft:double_stone_block_slab2", "stone_slab_type_2",
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("red_nether_brick", ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB)
                            .put("prismarine_dark", ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB)
                            .put("purpur", ChunkerVanillaBlockType.PURPUR_SLAB)
                            .put("prismarine_brick", ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB)
                            .put("red_sandstone", ChunkerVanillaBlockType.RED_SANDSTONE_SLAB)
                            .put("mossy_cobblestone", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB)
                            .put("prismarine_rough", ChunkerVanillaBlockType.PRISMARINE_SLAB)
                            .put("smooth_sandstone", ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

            registerOverrideOutput(BlockMapping.flatten("minecraft:stone_block_slab3", "stone_slab_type_3",
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("polished_diorite", ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB)
                            .put("smooth_red_sandstone", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB)
                            .put("granite", ChunkerVanillaBlockType.GRANITE_SLAB)
                            .put("andesite", ChunkerVanillaBlockType.ANDESITE_SLAB)
                            .put("diorite", ChunkerVanillaBlockType.DIORITE_SLAB)
                            .put("end_stone_brick", ChunkerVanillaBlockType.END_STONE_BRICK_SLAB)
                            .put("polished_granite", ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB)
                            .put("polished_andesite", ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            registerOverrideOutput(BlockMapping.flatten("minecraft:double_stone_block_slab3", "stone_slab_type_3",
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("diorite", ChunkerVanillaBlockType.DIORITE_SLAB)
                            .put("end_stone_brick", ChunkerVanillaBlockType.END_STONE_BRICK_SLAB)
                            .put("granite", ChunkerVanillaBlockType.GRANITE_SLAB)
                            .put("polished_andesite", ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB)
                            .put("andesite", ChunkerVanillaBlockType.ANDESITE_SLAB)
                            .put("polished_granite", ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB)
                            .put("smooth_red_sandstone", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB)
                            .put("polished_diorite", ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

            registerOverrideOutput(BlockMapping.flatten("minecraft:stone_block_slab4", "stone_slab_type_4",
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("cut_sandstone", ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB)
                            .put("smooth_quartz", ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB)
                            .put("stone", ChunkerVanillaBlockType.STONE_SLAB)
                            .put("cut_red_sandstone", ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB)
                            .put("mossy_stone_brick", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            registerOverrideOutput(BlockMapping.flatten("minecraft:double_stone_block_slab4", "stone_slab_type_4",
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("cut_sandstone", ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB)
                            .put("stone", ChunkerVanillaBlockType.STONE_SLAB)
                            .put("cut_red_sandstone", ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB)
                            .put("mossy_stone_brick", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                            .put("smooth_quartz", ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

            // New mangrove blocks
            register(BlockMapping.of("minecraft:mangrove_button", ChunkerVanillaBlockType.MANGROVE_BUTTON, BedrockStateGroups.BUTTON));
            register(BlockMapping.of("minecraft:mangrove_door", ChunkerVanillaBlockType.MANGROVE_DOOR, BedrockStateGroups.DOOR));
            register(BlockMapping.of("minecraft:mangrove_fence", ChunkerVanillaBlockType.MANGROVE_FENCE, BedrockStateGroups.CONNECTABLE_HORIZONTAL));
            register(BlockMapping.of("minecraft:mangrove_fence_gate", ChunkerVanillaBlockType.MANGROVE_FENCE_GATE, BedrockStateGroups.FENCE_GATE));
            register(BlockMapping.of("minecraft:mangrove_planks", ChunkerVanillaBlockType.MANGROVE_PLANKS));
            register(BlockMapping.of("minecraft:mangrove_pressure_plate", ChunkerVanillaBlockType.MANGROVE_PRESSURE_PLATE, BedrockStateGroups.PRESSURE_PLATE));
            register(BlockMapping.of("minecraft:mangrove_roots", ChunkerVanillaBlockType.MANGROVE_ROOTS));
            register(BlockMapping.of("minecraft:mangrove_standing_sign", ChunkerVanillaBlockType.MANGROVE_SIGN, BedrockStateGroups.SIGN));
            register(BlockMapping.of("minecraft:mangrove_double_slab", ChunkerVanillaBlockType.MANGROVE_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:mangrove_slab", ChunkerVanillaBlockType.MANGROVE_SLAB, BedrockStateGroups.SLAB_HALF));
            register(BlockMapping.of("minecraft:mangrove_stairs", ChunkerVanillaBlockType.MANGROVE_STAIRS, BedrockStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:mangrove_trapdoor", ChunkerVanillaBlockType.MANGROVE_TRAPDOOR, BedrockStateGroups.TRAPDOOR));
            register(BlockMapping.of("minecraft:mangrove_wall_sign", ChunkerVanillaBlockType.MANGROVE_WALL_SIGN, BedrockStateGroups.FACING_DIRECTION_HORIZONTAL));
            register(BlockMapping.of("minecraft:muddy_mangrove_roots", ChunkerVanillaBlockType.MUDDY_MANGROVE_ROOTS, BedrockStateGroups.MUDDY_MANGROVE_ROOTS));
            register(BlockMapping.of("minecraft:mangrove_log", ChunkerVanillaBlockType.MANGROVE_LOG, BedrockStateGroups.PILLAR_BLOCK));
            register(BlockMapping.of("minecraft:stripped_mangrove_log", ChunkerVanillaBlockType.STRIPPED_MANGROVE_LOG, BedrockStateGroups.PILLAR_BLOCK));

            // Mangrove wood
            register(BlockMapping.of("minecraft:mangrove_wood", "stripped_bit", false, ChunkerVanillaBlockType.MANGROVE_WOOD, BedrockStateGroups.PILLAR_BLOCK));
            register(BlockMapping.of("minecraft:stripped_mangrove_wood", ChunkerVanillaBlockType.STRIPPED_MANGROVE_WOOD, BedrockStateGroups.PILLAR_BLOCK));

            // Fallback for mangrove wood since there is a stripped bit
            registerDuplicateOutput(BlockMapping.of("minecraft:mangrove_wood", "stripped_bit", true, ChunkerVanillaBlockType.STRIPPED_MANGROVE_WOOD, BedrockStateGroups.PILLAR_BLOCK));
        }

        // R19U5
        if (version.isGreaterThanOrEqual(1, 19, 50)) {
            register(BlockMapping.of("minecraft:bamboo_button", ChunkerVanillaBlockType.BAMBOO_BUTTON, BedrockStateGroups.BUTTON));
            register(BlockMapping.of("minecraft:bamboo_door", ChunkerVanillaBlockType.BAMBOO_DOOR, BedrockStateGroups.DOOR));
            register(BlockMapping.of("minecraft:bamboo_fence", ChunkerVanillaBlockType.BAMBOO_FENCE, BedrockStateGroups.CONNECTABLE_HORIZONTAL));
            register(BlockMapping.of("minecraft:bamboo_fence_gate", ChunkerVanillaBlockType.BAMBOO_FENCE_GATE, BedrockStateGroups.FENCE_GATE));
            register(BlockMapping.of("minecraft:bamboo_mosaic", ChunkerVanillaBlockType.BAMBOO_MOSAIC));
            register(BlockMapping.of("minecraft:bamboo_mosaic_double_slab", ChunkerVanillaBlockType.BAMBOO_MOSAIC_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:bamboo_planks", ChunkerVanillaBlockType.BAMBOO_PLANKS));
            register(BlockMapping.of("minecraft:bamboo_pressure_plate", ChunkerVanillaBlockType.BAMBOO_PRESSURE_PLATE, BedrockStateGroups.PRESSURE_PLATE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:bamboo_slab", ChunkerVanillaBlockType.BAMBOO_SLAB)
                            .put("minecraft:bamboo_mosaic_slab", ChunkerVanillaBlockType.BAMBOO_MOSAIC_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:bamboo_mosaic_stairs", ChunkerVanillaBlockType.BAMBOO_MOSAIC_STAIRS)
                            .put("minecraft:bamboo_stairs", ChunkerVanillaBlockType.BAMBOO_STAIRS)
                            .build(),
                    BedrockStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:bamboo_standing_sign", ChunkerVanillaBlockType.BAMBOO_SIGN, BedrockStateGroups.SIGN));
            register(BlockMapping.of("minecraft:bamboo_double_slab", ChunkerVanillaBlockType.BAMBOO_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:bamboo_trapdoor", ChunkerVanillaBlockType.BAMBOO_TRAPDOOR, BedrockStateGroups.TRAPDOOR));
            register(BlockMapping.of("minecraft:bamboo_wall_sign", ChunkerVanillaBlockType.BAMBOO_WALL_SIGN, BedrockStateGroups.FACING_DIRECTION_HORIZONTAL));
            register(BlockMapping.of("minecraft:chiseled_bookshelf", ChunkerVanillaBlockType.CHISELED_BOOKSHELF, BedrockStateGroups.CHISELED_BOOKSHELF));
            register(BlockMapping.group("hanging", false, ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:oak_hanging_sign", ChunkerVanillaBlockType.OAK_WALL_HANGING_SIGN)
                            .put("minecraft:crimson_hanging_sign", ChunkerVanillaBlockType.CRIMSON_WALL_HANGING_SIGN)
                            .put("minecraft:birch_hanging_sign", ChunkerVanillaBlockType.BIRCH_WALL_HANGING_SIGN)
                            .put("minecraft:dark_oak_hanging_sign", ChunkerVanillaBlockType.DARK_OAK_WALL_HANGING_SIGN)
                            .put("minecraft:acacia_hanging_sign", ChunkerVanillaBlockType.ACACIA_WALL_HANGING_SIGN)
                            .put("minecraft:spruce_hanging_sign", ChunkerVanillaBlockType.SPRUCE_WALL_HANGING_SIGN)
                            .put("minecraft:warped_hanging_sign", ChunkerVanillaBlockType.WARPED_WALL_HANGING_SIGN)
                            .put("minecraft:jungle_hanging_sign", ChunkerVanillaBlockType.JUNGLE_WALL_HANGING_SIGN)
                            .put("minecraft:mangrove_hanging_sign", ChunkerVanillaBlockType.MANGROVE_WALL_HANGING_SIGN)
                            .put("minecraft:bamboo_hanging_sign", ChunkerVanillaBlockType.BAMBOO_WALL_HANGING_SIGN)
                            .build(),
                    BedrockStateGroups.HANGING_WALL_SIGN));
            register(BlockMapping.group("hanging", true, ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:oak_hanging_sign", ChunkerVanillaBlockType.OAK_HANGING_SIGN)
                            .put("minecraft:crimson_hanging_sign", ChunkerVanillaBlockType.CRIMSON_HANGING_SIGN)
                            .put("minecraft:birch_hanging_sign", ChunkerVanillaBlockType.BIRCH_HANGING_SIGN)
                            .put("minecraft:dark_oak_hanging_sign", ChunkerVanillaBlockType.DARK_OAK_HANGING_SIGN)
                            .put("minecraft:acacia_hanging_sign", ChunkerVanillaBlockType.ACACIA_HANGING_SIGN)
                            .put("minecraft:spruce_hanging_sign", ChunkerVanillaBlockType.SPRUCE_HANGING_SIGN)
                            .put("minecraft:warped_hanging_sign", ChunkerVanillaBlockType.WARPED_HANGING_SIGN)
                            .put("minecraft:jungle_hanging_sign", ChunkerVanillaBlockType.JUNGLE_HANGING_SIGN)
                            .put("minecraft:mangrove_hanging_sign", ChunkerVanillaBlockType.MANGROVE_HANGING_SIGN)
                            .put("minecraft:bamboo_hanging_sign", ChunkerVanillaBlockType.BAMBOO_HANGING_SIGN)
                            .build(),
                    BedrockStateGroups.HANGING_SIGN));
        }

        // R19U6
        if (version.isGreaterThanOrEqual(1, 19, 60)) {
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:bamboo_block", ChunkerVanillaBlockType.BAMBOO_BLOCK)
                            .put("minecraft:stripped_bamboo_block", ChunkerVanillaBlockType.STRIPPED_BAMBOO_BLOCK)
                            .build(),
                    BedrockStateGroups.PILLAR_BLOCK));
        }

        // R19U7
        if (version.isGreaterThanOrEqual(1, 19, 70)) {
            // Wool got flattened
            registerOverrideOutput(BlockMapping.of("minecraft:black_wool", ChunkerVanillaBlockType.BLACK_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:blue_wool", ChunkerVanillaBlockType.BLUE_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:brown_wool", ChunkerVanillaBlockType.BROWN_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:cyan_wool", ChunkerVanillaBlockType.CYAN_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:gray_wool", ChunkerVanillaBlockType.GRAY_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:green_wool", ChunkerVanillaBlockType.GREEN_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:light_blue_wool", ChunkerVanillaBlockType.LIGHT_BLUE_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:light_gray_wool", ChunkerVanillaBlockType.LIGHT_GRAY_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:lime_wool", ChunkerVanillaBlockType.LIME_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:magenta_wool", ChunkerVanillaBlockType.MAGENTA_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:orange_wool", ChunkerVanillaBlockType.ORANGE_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:pink_wool", ChunkerVanillaBlockType.PINK_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:purple_wool", ChunkerVanillaBlockType.PURPLE_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:red_wool", ChunkerVanillaBlockType.RED_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:white_wool", ChunkerVanillaBlockType.WHITE_WOOL));
            registerOverrideOutput(BlockMapping.of("minecraft:yellow_wool", ChunkerVanillaBlockType.YELLOW_WOOL));

            register(BlockMapping.of("minecraft:decorated_pot", ChunkerVanillaBlockType.DECORATED_POT, BedrockStateGroups.DECORATED_POT));
            register(BlockMapping.of("minecraft:suspicious_sand", ChunkerVanillaBlockType.SUSPICIOUS_SAND, BedrockStateGroups.SUSPICIOUS_BLOCK));
            register(BlockMapping.of("minecraft:torchflower", ChunkerVanillaBlockType.TORCHFLOWER));
            register(BlockMapping.of("minecraft:torchflower_crop", ChunkerVanillaBlockType.TORCHFLOWER_CROP, BedrockStateGroups.TORCHFLOWER_CROP));

        }

        // R19U8
        if (version.isGreaterThanOrEqual(1, 19, 80)) {
            // Wood Fence got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:jungle_fence", ChunkerVanillaBlockType.JUNGLE_FENCE)
                            .put("minecraft:birch_fence", ChunkerVanillaBlockType.BIRCH_FENCE)
                            .put("minecraft:acacia_fence", ChunkerVanillaBlockType.ACACIA_FENCE)
                            .put("minecraft:spruce_fence", ChunkerVanillaBlockType.SPRUCE_FENCE)
                            .put("minecraft:dark_oak_fence", ChunkerVanillaBlockType.DARK_OAK_FENCE)
                            .put("minecraft:oak_fence", ChunkerVanillaBlockType.OAK_FENCE)
                            .build(),
                    BedrockStateGroups.CONNECTABLE_HORIZONTAL));

            // Log got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:jungle_log", ChunkerVanillaBlockType.JUNGLE_LOG)
                            .put("minecraft:oak_log", ChunkerVanillaBlockType.OAK_LOG)
                            .put("minecraft:acacia_log", ChunkerVanillaBlockType.ACACIA_LOG)
                            .put("minecraft:dark_oak_log", ChunkerVanillaBlockType.DARK_OAK_LOG)
                            .put("minecraft:birch_log", ChunkerVanillaBlockType.BIRCH_LOG)
                            .put("minecraft:spruce_log", ChunkerVanillaBlockType.SPRUCE_LOG)
                            .build(),
                    BedrockStateGroups.PILLAR_BLOCK));

            register(BlockMapping.of("minecraft:calibrated_sculk_sensor", ChunkerVanillaBlockType.CALIBRATED_SCULK_SENSOR, BedrockStateGroups.CALIBRATED_SCULK_SENSOR));
            register(BlockMapping.of("minecraft:cherry_button", ChunkerVanillaBlockType.CHERRY_BUTTON, BedrockStateGroups.BUTTON));
            register(BlockMapping.of("minecraft:cherry_door", ChunkerVanillaBlockType.CHERRY_DOOR, BedrockStateGroups.DOOR));
            register(BlockMapping.of("minecraft:cherry_fence", ChunkerVanillaBlockType.CHERRY_FENCE, BedrockStateGroups.CONNECTABLE_HORIZONTAL));
            register(BlockMapping.of("minecraft:cherry_fence_gate", ChunkerVanillaBlockType.CHERRY_FENCE_GATE, BedrockStateGroups.FENCE_GATE));
            register(BlockMapping.of("minecraft:cherry_log", ChunkerVanillaBlockType.CHERRY_LOG, BedrockStateGroups.PILLAR_BLOCK));
            register(BlockMapping.of("minecraft:stripped_cherry_log", ChunkerVanillaBlockType.STRIPPED_CHERRY_LOG, BedrockStateGroups.PILLAR_BLOCK));
            register(BlockMapping.of("minecraft:stripped_cherry_wood", ChunkerVanillaBlockType.STRIPPED_CHERRY_WOOD, BedrockStateGroups.PILLAR_BLOCK));
            register(BlockMapping.of("minecraft:cherry_leaves", ChunkerVanillaBlockType.CHERRY_LEAVES, BedrockStateGroups.LEAVES));
            register(BlockMapping.of("minecraft:cherry_planks", ChunkerVanillaBlockType.CHERRY_PLANKS));
            register(BlockMapping.of("minecraft:cherry_pressure_plate", ChunkerVanillaBlockType.CHERRY_PRESSURE_PLATE, BedrockStateGroups.PRESSURE_PLATE));
            register(BlockMapping.of("minecraft:cherry_sapling", ChunkerVanillaBlockType.CHERRY_SAPLING, BedrockStateGroups.SAPLING));
            register(BlockMapping.of("minecraft:cherry_standing_sign", ChunkerVanillaBlockType.CHERRY_SIGN, BedrockStateGroups.SIGN));
            register(BlockMapping.of("minecraft:cherry_double_slab", ChunkerVanillaBlockType.CHERRY_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:cherry_slab", ChunkerVanillaBlockType.CHERRY_SLAB, BedrockStateGroups.SLAB_HALF));
            register(BlockMapping.of("minecraft:cherry_stairs", ChunkerVanillaBlockType.CHERRY_STAIRS, BedrockStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:cherry_trapdoor", ChunkerVanillaBlockType.CHERRY_TRAPDOOR, BedrockStateGroups.TRAPDOOR));
            register(BlockMapping.of("minecraft:cherry_wall_sign", ChunkerVanillaBlockType.CHERRY_WALL_SIGN, BedrockStateGroups.FACING_DIRECTION_HORIZONTAL));
            register(BlockMapping.of("minecraft:cherry_wood", "stripped_bit", false, ChunkerVanillaBlockType.CHERRY_WOOD, BedrockStateGroups.PILLAR_BLOCK));

            // Fallback for cherry wood since there's a stripped_bit
            registerDuplicateOutput(BlockMapping.of("minecraft:cherry_wood", "stripped_bit", true, ChunkerVanillaBlockType.STRIPPED_CHERRY_WOOD, BedrockStateGroups.PILLAR_BLOCK));

            register(BlockMapping.of("minecraft:pink_petals", ChunkerVanillaBlockType.PINK_PETALS, BedrockStateGroups.PINK_PETALS));
            register(BlockMapping.of("minecraft:suspicious_gravel", ChunkerVanillaBlockType.SUSPICIOUS_GRAVEL, BedrockStateGroups.SUSPICIOUS_BLOCK));

            register(BlockMapping.of("minecraft:cherry_hanging_sign", "hanging", false, ChunkerVanillaBlockType.CHERRY_WALL_HANGING_SIGN, BedrockStateGroups.HANGING_WALL_SIGN));
            register(BlockMapping.of("minecraft:cherry_hanging_sign", "hanging", true, ChunkerVanillaBlockType.CHERRY_HANGING_SIGN, BedrockStateGroups.HANGING_SIGN));
        }

        // R20
        if (version.isGreaterThanOrEqual(1, 20, 0)) {
            // Pumpkin got migrated to minecraft:facing_direction
            registerOverrideOutput(BlockMapping.of("minecraft:pumpkin", "minecraft:cardinal_direction", "south", ChunkerVanillaBlockType.PUMPKIN));

            // Coral got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:dead_brain_coral", ChunkerVanillaBlockType.DEAD_BRAIN_CORAL)
                    .put("minecraft:bubble_coral", ChunkerVanillaBlockType.BUBBLE_CORAL)
                    .put("minecraft:dead_bubble_coral", ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL)
                    .put("minecraft:tube_coral", ChunkerVanillaBlockType.TUBE_CORAL)
                    .put("minecraft:fire_coral", ChunkerVanillaBlockType.FIRE_CORAL)
                    .put("minecraft:horn_coral", ChunkerVanillaBlockType.HORN_CORAL)
                    .put("minecraft:dead_tube_coral", ChunkerVanillaBlockType.DEAD_TUBE_CORAL)
                    .put("minecraft:brain_coral", ChunkerVanillaBlockType.BRAIN_CORAL)
                    .put("minecraft:dead_fire_coral", ChunkerVanillaBlockType.DEAD_FIRE_CORAL)
                    .put("minecraft:dead_horn_coral", ChunkerVanillaBlockType.DEAD_HORN_CORAL)
                    .build()
            ));

            // Carpet got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:black_carpet", ChunkerVanillaBlockType.BLACK_CARPET)
                    .put("minecraft:blue_carpet", ChunkerVanillaBlockType.BLUE_CARPET)
                    .put("minecraft:brown_carpet", ChunkerVanillaBlockType.BROWN_CARPET)
                    .put("minecraft:cyan_carpet", ChunkerVanillaBlockType.CYAN_CARPET)
                    .put("minecraft:gray_carpet", ChunkerVanillaBlockType.GRAY_CARPET)
                    .put("minecraft:green_carpet", ChunkerVanillaBlockType.GREEN_CARPET)
                    .put("minecraft:light_blue_carpet", ChunkerVanillaBlockType.LIGHT_BLUE_CARPET)
                    .put("minecraft:light_gray_carpet", ChunkerVanillaBlockType.LIGHT_GRAY_CARPET)
                    .put("minecraft:lime_carpet", ChunkerVanillaBlockType.LIME_CARPET)
                    .put("minecraft:magenta_carpet", ChunkerVanillaBlockType.MAGENTA_CARPET)
                    .put("minecraft:orange_carpet", ChunkerVanillaBlockType.ORANGE_CARPET)
                    .put("minecraft:pink_carpet", ChunkerVanillaBlockType.PINK_CARPET)
                    .put("minecraft:purple_carpet", ChunkerVanillaBlockType.PURPLE_CARPET)
                    .put("minecraft:red_carpet", ChunkerVanillaBlockType.RED_CARPET)
                    .put("minecraft:white_carpet", ChunkerVanillaBlockType.WHITE_CARPET)
                    .put("minecraft:yellow_carpet", ChunkerVanillaBlockType.YELLOW_CARPET)
                    .build()
            ));

            register(BlockMapping.of("minecraft:pitcher_crop", ChunkerVanillaBlockType.PITCHER_CROP, BedrockStateGroups.PITCHER_CROP));
            register(BlockMapping.of("minecraft:pitcher_plant", ChunkerVanillaBlockType.PITCHER_PLANT, BedrockStateGroups.DOUBLE_BLOCK));
            register(BlockMapping.of("minecraft:sniffer_egg", ChunkerVanillaBlockType.SNIFFER_EGG, BedrockStateGroups.SNIFFER_EGG));
        }

        // R20U1
        if (version.isGreaterThanOrEqual(1, 20, 10)) {
            // Shulker boxes got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:cyan_shulker_box", ChunkerVanillaBlockType.CYAN_SHULKER_BOX)
                            .put("minecraft:green_shulker_box", ChunkerVanillaBlockType.GREEN_SHULKER_BOX)
                            .put("minecraft:gray_shulker_box", ChunkerVanillaBlockType.GRAY_SHULKER_BOX)
                            .put("minecraft:light_gray_shulker_box", ChunkerVanillaBlockType.LIGHT_GRAY_SHULKER_BOX)
                            .put("minecraft:orange_shulker_box", ChunkerVanillaBlockType.ORANGE_SHULKER_BOX)
                            .put("minecraft:white_shulker_box", ChunkerVanillaBlockType.WHITE_SHULKER_BOX)
                            .put("minecraft:red_shulker_box", ChunkerVanillaBlockType.RED_SHULKER_BOX)
                            .put("minecraft:brown_shulker_box", ChunkerVanillaBlockType.BROWN_SHULKER_BOX)
                            .put("minecraft:light_blue_shulker_box", ChunkerVanillaBlockType.LIGHT_BLUE_SHULKER_BOX)
                            .put("minecraft:lime_shulker_box", ChunkerVanillaBlockType.LIME_SHULKER_BOX)
                            .put("minecraft:magenta_shulker_box", ChunkerVanillaBlockType.MAGENTA_SHULKER_BOX)
                            .put("minecraft:black_shulker_box", ChunkerVanillaBlockType.BLACK_SHULKER_BOX)
                            .put("minecraft:yellow_shulker_box", ChunkerVanillaBlockType.YELLOW_SHULKER_BOX)
                            .put("minecraft:pink_shulker_box", ChunkerVanillaBlockType.PINK_SHULKER_BOX)
                            .put("minecraft:blue_shulker_box", ChunkerVanillaBlockType.BLUE_SHULKER_BOX)
                            .put("minecraft:purple_shulker_box", ChunkerVanillaBlockType.PURPLE_SHULKER_BOX)
                            .build(),
                    BedrockStateGroups.SHULKER_BOX));

            // Concrete got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:black_concrete", ChunkerVanillaBlockType.BLACK_CONCRETE)
                    .put("minecraft:blue_concrete", ChunkerVanillaBlockType.BLUE_CONCRETE)
                    .put("minecraft:brown_concrete", ChunkerVanillaBlockType.BROWN_CONCRETE)
                    .put("minecraft:cyan_concrete", ChunkerVanillaBlockType.CYAN_CONCRETE)
                    .put("minecraft:gray_concrete", ChunkerVanillaBlockType.GRAY_CONCRETE)
                    .put("minecraft:green_concrete", ChunkerVanillaBlockType.GREEN_CONCRETE)
                    .put("minecraft:light_blue_concrete", ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE)
                    .put("minecraft:light_gray_concrete", ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE)
                    .put("minecraft:lime_concrete", ChunkerVanillaBlockType.LIME_CONCRETE)
                    .put("minecraft:magenta_concrete", ChunkerVanillaBlockType.MAGENTA_CONCRETE)
                    .put("minecraft:orange_concrete", ChunkerVanillaBlockType.ORANGE_CONCRETE)
                    .put("minecraft:pink_concrete", ChunkerVanillaBlockType.PINK_CONCRETE)
                    .put("minecraft:purple_concrete", ChunkerVanillaBlockType.PURPLE_CONCRETE)
                    .put("minecraft:red_concrete", ChunkerVanillaBlockType.RED_CONCRETE)
                    .put("minecraft:white_concrete", ChunkerVanillaBlockType.WHITE_CONCRETE)
                    .put("minecraft:yellow_concrete", ChunkerVanillaBlockType.YELLOW_CONCRETE)
                    .build()
            ));
        }

        // R20U3
        if (version.isGreaterThanOrEqual(1, 20, 30)) {
            // Concrete powder got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:black_concrete_powder", ChunkerVanillaBlockType.BLACK_CONCRETE_POWDER)
                    .put("minecraft:blue_concrete_powder", ChunkerVanillaBlockType.BLUE_CONCRETE_POWDER)
                    .put("minecraft:brown_concrete_powder", ChunkerVanillaBlockType.BROWN_CONCRETE_POWDER)
                    .put("minecraft:cyan_concrete_powder", ChunkerVanillaBlockType.CYAN_CONCRETE_POWDER)
                    .put("minecraft:gray_concrete_powder", ChunkerVanillaBlockType.GRAY_CONCRETE_POWDER)
                    .put("minecraft:green_concrete_powder", ChunkerVanillaBlockType.GREEN_CONCRETE_POWDER)
                    .put("minecraft:light_blue_concrete_powder", ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE_POWDER)
                    .put("minecraft:light_gray_concrete_powder", ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE_POWDER)
                    .put("minecraft:lime_concrete_powder", ChunkerVanillaBlockType.LIME_CONCRETE_POWDER)
                    .put("minecraft:magenta_concrete_powder", ChunkerVanillaBlockType.MAGENTA_CONCRETE_POWDER)
                    .put("minecraft:orange_concrete_powder", ChunkerVanillaBlockType.ORANGE_CONCRETE_POWDER)
                    .put("minecraft:pink_concrete_powder", ChunkerVanillaBlockType.PINK_CONCRETE_POWDER)
                    .put("minecraft:purple_concrete_powder", ChunkerVanillaBlockType.PURPLE_CONCRETE_POWDER)
                    .put("minecraft:red_concrete_powder", ChunkerVanillaBlockType.RED_CONCRETE_POWDER)
                    .put("minecraft:white_concrete_powder", ChunkerVanillaBlockType.WHITE_CONCRETE_POWDER)
                    .put("minecraft:yellow_concrete_powder", ChunkerVanillaBlockType.YELLOW_CONCRETE_POWDER)
                    .build()
            ));

            // Stained-glass got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:black_stained_glass", ChunkerVanillaBlockType.BLACK_STAINED_GLASS)
                    .put("minecraft:blue_stained_glass", ChunkerVanillaBlockType.BLUE_STAINED_GLASS)
                    .put("minecraft:brown_stained_glass", ChunkerVanillaBlockType.BROWN_STAINED_GLASS)
                    .put("minecraft:cyan_stained_glass", ChunkerVanillaBlockType.CYAN_STAINED_GLASS)
                    .put("minecraft:gray_stained_glass", ChunkerVanillaBlockType.GRAY_STAINED_GLASS)
                    .put("minecraft:green_stained_glass", ChunkerVanillaBlockType.GREEN_STAINED_GLASS)
                    .put("minecraft:light_blue_stained_glass", ChunkerVanillaBlockType.LIGHT_BLUE_STAINED_GLASS)
                    .put("minecraft:light_gray_stained_glass", ChunkerVanillaBlockType.LIGHT_GRAY_STAINED_GLASS)
                    .put("minecraft:lime_stained_glass", ChunkerVanillaBlockType.LIME_STAINED_GLASS)
                    .put("minecraft:magenta_stained_glass", ChunkerVanillaBlockType.MAGENTA_STAINED_GLASS)
                    .put("minecraft:orange_stained_glass", ChunkerVanillaBlockType.ORANGE_STAINED_GLASS)
                    .put("minecraft:pink_stained_glass", ChunkerVanillaBlockType.PINK_STAINED_GLASS)
                    .put("minecraft:purple_stained_glass", ChunkerVanillaBlockType.PURPLE_STAINED_GLASS)
                    .put("minecraft:red_stained_glass", ChunkerVanillaBlockType.RED_STAINED_GLASS)
                    .put("minecraft:white_stained_glass", ChunkerVanillaBlockType.WHITE_STAINED_GLASS)
                    .put("minecraft:yellow_stained_glass", ChunkerVanillaBlockType.YELLOW_STAINED_GLASS)
                    .build()
            ));

            // Stained-glass panes got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:orange_stained_glass_pane", ChunkerVanillaBlockType.ORANGE_STAINED_GLASS_PANE)
                            .put("minecraft:light_gray_stained_glass_pane", ChunkerVanillaBlockType.LIGHT_GRAY_STAINED_GLASS_PANE)
                            .put("minecraft:lime_stained_glass_pane", ChunkerVanillaBlockType.LIME_STAINED_GLASS_PANE)
                            .put("minecraft:black_stained_glass_pane", ChunkerVanillaBlockType.BLACK_STAINED_GLASS_PANE)
                            .put("minecraft:purple_stained_glass_pane", ChunkerVanillaBlockType.PURPLE_STAINED_GLASS_PANE)
                            .put("minecraft:light_blue_stained_glass_pane", ChunkerVanillaBlockType.LIGHT_BLUE_STAINED_GLASS_PANE)
                            .put("minecraft:brown_stained_glass_pane", ChunkerVanillaBlockType.BROWN_STAINED_GLASS_PANE)
                            .put("minecraft:blue_stained_glass_pane", ChunkerVanillaBlockType.BLUE_STAINED_GLASS_PANE)
                            .put("minecraft:green_stained_glass_pane", ChunkerVanillaBlockType.GREEN_STAINED_GLASS_PANE)
                            .put("minecraft:gray_stained_glass_pane", ChunkerVanillaBlockType.GRAY_STAINED_GLASS_PANE)
                            .put("minecraft:white_stained_glass_pane", ChunkerVanillaBlockType.WHITE_STAINED_GLASS_PANE)
                            .put("minecraft:pink_stained_glass_pane", ChunkerVanillaBlockType.PINK_STAINED_GLASS_PANE)
                            .put("minecraft:red_stained_glass_pane", ChunkerVanillaBlockType.RED_STAINED_GLASS_PANE)
                            .put("minecraft:cyan_stained_glass_pane", ChunkerVanillaBlockType.CYAN_STAINED_GLASS_PANE)
                            .put("minecraft:magenta_stained_glass_pane", ChunkerVanillaBlockType.MAGENTA_STAINED_GLASS_PANE)
                            .put("minecraft:yellow_stained_glass_pane", ChunkerVanillaBlockType.YELLOW_STAINED_GLASS_PANE)
                            .build(),
                    BedrockStateGroups.CONNECTABLE_HORIZONTAL));

            // Terracotta got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:black_terracotta", ChunkerVanillaBlockType.BLACK_TERRACOTTA)
                    .put("minecraft:blue_terracotta", ChunkerVanillaBlockType.BLUE_TERRACOTTA)
                    .put("minecraft:brown_terracotta", ChunkerVanillaBlockType.BROWN_TERRACOTTA)
                    .put("minecraft:cyan_terracotta", ChunkerVanillaBlockType.CYAN_TERRACOTTA)
                    .put("minecraft:gray_terracotta", ChunkerVanillaBlockType.GRAY_TERRACOTTA)
                    .put("minecraft:green_terracotta", ChunkerVanillaBlockType.GREEN_TERRACOTTA)
                    .put("minecraft:light_blue_terracotta", ChunkerVanillaBlockType.LIGHT_BLUE_TERRACOTTA)
                    .put("minecraft:light_gray_terracotta", ChunkerVanillaBlockType.LIGHT_GRAY_TERRACOTTA)
                    .put("minecraft:lime_terracotta", ChunkerVanillaBlockType.LIME_TERRACOTTA)
                    .put("minecraft:magenta_terracotta", ChunkerVanillaBlockType.MAGENTA_TERRACOTTA)
                    .put("minecraft:orange_terracotta", ChunkerVanillaBlockType.ORANGE_TERRACOTTA)
                    .put("minecraft:pink_terracotta", ChunkerVanillaBlockType.PINK_TERRACOTTA)
                    .put("minecraft:purple_terracotta", ChunkerVanillaBlockType.PURPLE_TERRACOTTA)
                    .put("minecraft:red_terracotta", ChunkerVanillaBlockType.RED_TERRACOTTA)
                    .put("minecraft:white_terracotta", ChunkerVanillaBlockType.WHITE_TERRACOTTA)
                    .put("minecraft:yellow_terracotta", ChunkerVanillaBlockType.YELLOW_TERRACOTTA)
                    .build()
            ));
        }

        // R20U5
        if (version.isGreaterThanOrEqual(1, 20, 50)) {
            // Planks got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:acacia_planks", ChunkerVanillaBlockType.ACACIA_PLANKS)
                    .put("minecraft:birch_planks", ChunkerVanillaBlockType.BIRCH_PLANKS)
                    .put("minecraft:dark_oak_planks", ChunkerVanillaBlockType.DARK_OAK_PLANKS)
                    .put("minecraft:jungle_planks", ChunkerVanillaBlockType.JUNGLE_PLANKS)
                    .put("minecraft:oak_planks", ChunkerVanillaBlockType.OAK_PLANKS)
                    .put("minecraft:spruce_planks", ChunkerVanillaBlockType.SPRUCE_PLANKS)
                    .build()
            ));

            // Stone got flattened, ensure that we don't add the stone_type state anymore
            registerOverrideInputOutput(BlockMapping.of("minecraft:stone", ChunkerVanillaBlockType.STONE));

            // Other stone types
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:andesite", ChunkerVanillaBlockType.ANDESITE)
                    .put("minecraft:diorite", ChunkerVanillaBlockType.DIORITE)
                    .put("minecraft:granite", ChunkerVanillaBlockType.GRANITE)
                    .put("minecraft:polished_andesite", ChunkerVanillaBlockType.POLISHED_ANDESITE)
                    .put("minecraft:polished_diorite", ChunkerVanillaBlockType.POLISHED_DIORITE)
                    .put("minecraft:polished_granite", ChunkerVanillaBlockType.POLISHED_GRANITE)
                    .build()
            ));

            // New blocks
            register(BlockMapping.of("minecraft:chiseled_copper", ChunkerVanillaBlockType.CHISELED_COPPER));
            register(BlockMapping.of("minecraft:chiseled_tuff", ChunkerVanillaBlockType.CHISELED_TUFF));
            register(BlockMapping.of("minecraft:chiseled_tuff_bricks", ChunkerVanillaBlockType.CHISELED_TUFF_BRICKS));
            register(BlockMapping.of("minecraft:crafter", ChunkerVanillaBlockType.CRAFTER, BedrockStateGroups.CRAFTER));
            register(BlockMapping.of("minecraft:exposed_chiseled_copper", ChunkerVanillaBlockType.EXPOSED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:oxidized_chiseled_copper", ChunkerVanillaBlockType.OXIDIZED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:polished_tuff", ChunkerVanillaBlockType.POLISHED_TUFF));
            register(BlockMapping.of("minecraft:polished_tuff_double_slab", ChunkerVanillaBlockType.POLISHED_TUFF_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:exposed_copper_bulb", ChunkerVanillaBlockType.EXPOSED_COPPER_BULB)
                            .put("minecraft:waxed_weathered_copper_bulb", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_BULB)
                            .put("minecraft:waxed_exposed_copper_bulb", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_BULB)
                            .put("minecraft:waxed_oxidized_copper_bulb", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_BULB)
                            .put("minecraft:waxed_copper_bulb", ChunkerVanillaBlockType.WAXED_COPPER_BULB)
                            .put("minecraft:oxidized_copper_bulb", ChunkerVanillaBlockType.OXIDIZED_COPPER_BULB)
                            .put("minecraft:copper_bulb", ChunkerVanillaBlockType.COPPER_BULB)
                            .put("minecraft:weathered_copper_bulb", ChunkerVanillaBlockType.WEATHERED_COPPER_BULB)
                            .build(),
                    BedrockStateGroups.BULB));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:waxed_exposed_copper_door", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_DOOR)
                            .put("minecraft:weathered_copper_door", ChunkerVanillaBlockType.WEATHERED_COPPER_DOOR)
                            .put("minecraft:copper_door", ChunkerVanillaBlockType.COPPER_DOOR)
                            .put("minecraft:waxed_copper_door", ChunkerVanillaBlockType.WAXED_COPPER_DOOR)
                            .put("minecraft:waxed_weathered_copper_door", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_DOOR)
                            .put("minecraft:waxed_oxidized_copper_door", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_DOOR)
                            .put("minecraft:oxidized_copper_door", ChunkerVanillaBlockType.OXIDIZED_COPPER_DOOR)
                            .put("minecraft:exposed_copper_door", ChunkerVanillaBlockType.EXPOSED_COPPER_DOOR)
                            .build(),
                    BedrockStateGroups.DOOR));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:waxed_oxidized_copper_grate", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_GRATE)
                    .put("minecraft:weathered_copper_grate", ChunkerVanillaBlockType.WEATHERED_COPPER_GRATE)
                    .put("minecraft:copper_grate", ChunkerVanillaBlockType.COPPER_GRATE)
                    .put("minecraft:waxed_copper_grate", ChunkerVanillaBlockType.WAXED_COPPER_GRATE)
                    .put("minecraft:oxidized_copper_grate", ChunkerVanillaBlockType.OXIDIZED_COPPER_GRATE)
                    .put("minecraft:waxed_exposed_copper_grate", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_GRATE)
                    .put("minecraft:exposed_copper_grate", ChunkerVanillaBlockType.EXPOSED_COPPER_GRATE)
                    .put("minecraft:waxed_weathered_copper_grate", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_GRATE)
                    .build()
            ));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:waxed_oxidized_copper_trapdoor", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_TRAPDOOR)
                            .put("minecraft:oxidized_copper_trapdoor", ChunkerVanillaBlockType.OXIDIZED_COPPER_TRAPDOOR)
                            .put("minecraft:exposed_copper_trapdoor", ChunkerVanillaBlockType.EXPOSED_COPPER_TRAPDOOR)
                            .put("minecraft:waxed_exposed_copper_trapdoor", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_TRAPDOOR)
                            .put("minecraft:waxed_weathered_copper_trapdoor", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_TRAPDOOR)
                            .put("minecraft:weathered_copper_trapdoor", ChunkerVanillaBlockType.WEATHERED_COPPER_TRAPDOOR)
                            .put("minecraft:copper_trapdoor", ChunkerVanillaBlockType.COPPER_TRAPDOOR)
                            .put("minecraft:waxed_copper_trapdoor", ChunkerVanillaBlockType.WAXED_COPPER_TRAPDOOR)
                            .build(),
                    BedrockStateGroups.TRAPDOOR));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:tuff_brick_slab", ChunkerVanillaBlockType.TUFF_BRICK_SLAB)
                            .put("minecraft:polished_tuff_slab", ChunkerVanillaBlockType.POLISHED_TUFF_SLAB)
                            .put("minecraft:tuff_slab", ChunkerVanillaBlockType.TUFF_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:tuff_stairs", ChunkerVanillaBlockType.TUFF_STAIRS)
                            .put("minecraft:tuff_brick_stairs", ChunkerVanillaBlockType.TUFF_BRICK_STAIRS)
                            .put("minecraft:polished_tuff_stairs", ChunkerVanillaBlockType.POLISHED_TUFF_STAIRS)
                            .build(),
                    BedrockStateGroups.STAIRS));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:polished_tuff_wall", ChunkerVanillaBlockType.POLISHED_TUFF_WALL)
                            .put("minecraft:tuff_brick_wall", ChunkerVanillaBlockType.TUFF_BRICK_WALL)
                            .put("minecraft:tuff_wall", ChunkerVanillaBlockType.TUFF_WALL)
                            .build(),
                    BedrockStateGroups.WALL));
            register(BlockMapping.of("minecraft:tuff_brick_double_slab", ChunkerVanillaBlockType.TUFF_BRICK_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:tuff_bricks", ChunkerVanillaBlockType.TUFF_BRICKS));
            register(BlockMapping.of("minecraft:tuff_double_slab", ChunkerVanillaBlockType.TUFF_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:waxed_chiseled_copper", ChunkerVanillaBlockType.WAXED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:waxed_exposed_chiseled_copper", ChunkerVanillaBlockType.WAXED_EXPOSED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:waxed_oxidized_chiseled_copper", ChunkerVanillaBlockType.WAXED_OXIDIZED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:waxed_weathered_chiseled_copper", ChunkerVanillaBlockType.WAXED_WEATHERED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:weathered_chiseled_copper", ChunkerVanillaBlockType.WEATHERED_CHISELED_COPPER));
        }

        // R20U6
        if (version.isGreaterThanOrEqual(1, 20, 60)) {
            // New blocks
            register(BlockMapping.of("minecraft:trial_spawner", ChunkerVanillaBlockType.TRIAL_SPAWNER, BedrockStateGroups.TRIAL_SPAWNER));
        }

        // R20U7
        if (version.isGreaterThanOrEqual(1, 20, 70)) {
            // Grass got renamed to grass_block
            registerOverrideOutput(BlockMapping.of("minecraft:grass_block", ChunkerVanillaBlockType.GRASS_BLOCK, BedrockStateGroups.SNOWY_BLOCK));

            // Wood Slabs got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:jungle_slab", ChunkerVanillaBlockType.JUNGLE_SLAB)
                            .put("minecraft:oak_slab", ChunkerVanillaBlockType.OAK_SLAB)
                            .put("minecraft:acacia_slab", ChunkerVanillaBlockType.ACACIA_SLAB)
                            .put("minecraft:spruce_slab", ChunkerVanillaBlockType.SPRUCE_SLAB)
                            .put("minecraft:dark_oak_slab", ChunkerVanillaBlockType.DARK_OAK_SLAB)
                            .put("minecraft:birch_slab", ChunkerVanillaBlockType.BIRCH_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:jungle_double_slab", ChunkerVanillaBlockType.JUNGLE_SLAB)
                            .put("minecraft:oak_double_slab", ChunkerVanillaBlockType.OAK_SLAB)
                            .put("minecraft:acacia_double_slab", ChunkerVanillaBlockType.ACACIA_SLAB)
                            .put("minecraft:spruce_double_slab", ChunkerVanillaBlockType.SPRUCE_SLAB)
                            .put("minecraft:dark_oak_double_slab", ChunkerVanillaBlockType.DARK_OAK_SLAB)
                            .put("minecraft:birch_double_slab", ChunkerVanillaBlockType.BIRCH_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

            // Legacy mapping for petrified_oak_slab
            registerDuplicateOverrideOutput(BlockMapping.of("minecraft:oak_slab", ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB, BedrockStateGroups.SLAB_HALF));
            registerDuplicateOverrideOutput(BlockMapping.of("minecraft:oak_double_slab", ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

            // Wood got flattened
            registerOverrideOutput(BlockMapping.group(
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:oak_wood", ChunkerVanillaBlockType.OAK_WOOD)
                            .put("minecraft:spruce_wood", ChunkerVanillaBlockType.SPRUCE_WOOD)
                            .put("minecraft:jungle_wood", ChunkerVanillaBlockType.JUNGLE_WOOD)
                            .put("minecraft:dark_oak_wood", ChunkerVanillaBlockType.DARK_OAK_WOOD)
                            .put("minecraft:acacia_wood", ChunkerVanillaBlockType.ACACIA_WOOD)
                            .put("minecraft:birch_wood", ChunkerVanillaBlockType.BIRCH_WOOD)
                            .build(),
                    BedrockStateGroups.PILLAR_BLOCK));
            registerOverrideOutput(BlockMapping.group(
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:stripped_oak_wood", ChunkerVanillaBlockType.STRIPPED_OAK_WOOD)
                            .put("minecraft:stripped_spruce_wood", ChunkerVanillaBlockType.STRIPPED_SPRUCE_WOOD)
                            .put("minecraft:stripped_jungle_wood", ChunkerVanillaBlockType.STRIPPED_JUNGLE_WOOD)
                            .put("minecraft:stripped_dark_oak_wood", ChunkerVanillaBlockType.STRIPPED_DARK_OAK_WOOD)
                            .put("minecraft:stripped_acacia_wood", ChunkerVanillaBlockType.STRIPPED_ACACIA_WOOD)
                            .put("minecraft:stripped_birch_wood", ChunkerVanillaBlockType.STRIPPED_BIRCH_WOOD)
                            .build(),
                    BedrockStateGroups.PILLAR_BLOCK));

            // Leaves got flattened
            registerOverrideOutput(BlockMapping.group(
                    ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:oak_leaves", ChunkerVanillaBlockType.OAK_LEAVES)
                            .put("minecraft:birch_leaves", ChunkerVanillaBlockType.BIRCH_LEAVES)
                            .put("minecraft:spruce_leaves", ChunkerVanillaBlockType.SPRUCE_LEAVES)
                            .put("minecraft:jungle_leaves", ChunkerVanillaBlockType.JUNGLE_LEAVES)
                            .put("minecraft:acacia_leaves", ChunkerVanillaBlockType.ACACIA_LEAVES)
                            .put("minecraft:dark_oak_leaves", ChunkerVanillaBlockType.DARK_OAK_LEAVES)
                            .build(),
                    BedrockStateGroups.LEAVES));

            // New blocks
            register(BlockMapping.of("minecraft:vault", ChunkerVanillaBlockType.VAULT, BedrockStateGroups.VAULT));
        }

        // R20U8
        if (version.isGreaterThanOrEqual(1, 20, 80)) {
            // Saplings got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:oak_sapling", ChunkerVanillaBlockType.OAK_SAPLING)
                            .put("minecraft:spruce_sapling", ChunkerVanillaBlockType.SPRUCE_SAPLING)
                            .put("minecraft:birch_sapling", ChunkerVanillaBlockType.BIRCH_SAPLING)
                            .put("minecraft:dark_oak_sapling", ChunkerVanillaBlockType.DARK_OAK_SAPLING)
                            .put("minecraft:acacia_sapling", ChunkerVanillaBlockType.ACACIA_SAPLING)
                            .put("minecraft:jungle_sapling", ChunkerVanillaBlockType.JUNGLE_SAPLING)
                            .build(),
                    BedrockStateGroups.SAPLING));

            // Flowers got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:white_tulip", ChunkerVanillaBlockType.WHITE_TULIP)
                    .put("minecraft:poppy", ChunkerVanillaBlockType.POPPY)
                    .put("minecraft:oxeye_daisy", ChunkerVanillaBlockType.OXEYE_DAISY)
                    .put("minecraft:cornflower", ChunkerVanillaBlockType.CORNFLOWER)
                    .put("minecraft:orange_tulip", ChunkerVanillaBlockType.ORANGE_TULIP)
                    .put("minecraft:lily_of_the_valley", ChunkerVanillaBlockType.LILY_OF_THE_VALLEY)
                    .put("minecraft:pink_tulip", ChunkerVanillaBlockType.PINK_TULIP)
                    .put("minecraft:azure_bluet", ChunkerVanillaBlockType.AZURE_BLUET)
                    .put("minecraft:allium", ChunkerVanillaBlockType.ALLIUM)
                    .put("minecraft:red_tulip", ChunkerVanillaBlockType.RED_TULIP)
                    .put("minecraft:blue_orchid", ChunkerVanillaBlockType.BLUE_ORCHID)
                    .build()
            ));

            // Coral fans got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:tube_coral_fan", ChunkerVanillaBlockType.TUBE_CORAL_FAN)
                            .put("minecraft:brain_coral_fan", ChunkerVanillaBlockType.BRAIN_CORAL_FAN)
                            .put("minecraft:bubble_coral_fan", ChunkerVanillaBlockType.BUBBLE_CORAL_FAN)
                            .put("minecraft:fire_coral_fan", ChunkerVanillaBlockType.FIRE_CORAL_FAN)
                            .put("minecraft:horn_coral_fan", ChunkerVanillaBlockType.HORN_CORAL_FAN)
                            .build(),
                    BedrockStateGroups.CORAL_FAN));
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:dead_tube_coral_fan", ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN)
                            .put("minecraft:dead_brain_coral_fan", ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_FAN)
                            .put("minecraft:dead_horn_coral_fan", ChunkerVanillaBlockType.DEAD_HORN_CORAL_FAN)
                            .put("minecraft:dead_fire_coral_fan", ChunkerVanillaBlockType.DEAD_FIRE_CORAL_FAN)
                            .put("minecraft:dead_bubble_coral_fan", ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_FAN)
                            .build(),
                    BedrockStateGroups.CORAL_FAN));

            // New blocks
            register(BlockMapping.of("minecraft:heavy_core", ChunkerVanillaBlockType.HEAVY_CORE));
        }

        // R21
        if (version.isGreaterThanOrEqual(1, 21, 0)) {
            // Coral block got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:dead_brain_coral_block", ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_BLOCK)
                    .put("minecraft:horn_coral_block", ChunkerVanillaBlockType.HORN_CORAL_BLOCK)
                    .put("minecraft:dead_fire_coral_block", ChunkerVanillaBlockType.DEAD_FIRE_CORAL_BLOCK)
                    .put("minecraft:bubble_coral_block", ChunkerVanillaBlockType.BUBBLE_CORAL_BLOCK)
                    .put("minecraft:tube_coral_block", ChunkerVanillaBlockType.TUBE_CORAL_BLOCK)
                    .put("minecraft:dead_tube_coral_block", ChunkerVanillaBlockType.DEAD_TUBE_CORAL_BLOCK)
                    .put("minecraft:fire_coral_block", ChunkerVanillaBlockType.FIRE_CORAL_BLOCK)
                    .put("minecraft:dead_horn_coral_block", ChunkerVanillaBlockType.DEAD_HORN_CORAL_BLOCK)
                    .put("minecraft:brain_coral_block", ChunkerVanillaBlockType.BRAIN_CORAL_BLOCK)
                    .put("minecraft:dead_bubble_coral_block", ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_BLOCK)
                    .build()
            ));

            // Tall grass got flattened
            registerOverrideOutput(BlockMapping.of("minecraft:short_grass", ChunkerVanillaBlockType.SHORT_GRASS));
            registerOverrideOutput(BlockMapping.of("minecraft:fern", ChunkerVanillaBlockType.FERN));

            // Several flowers got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:large_fern", ChunkerVanillaBlockType.LARGE_FERN)
                            .put("minecraft:lilac", ChunkerVanillaBlockType.LILAC)
                            .put("minecraft:peony", ChunkerVanillaBlockType.PEONY)
                            .put("minecraft:rose_bush", ChunkerVanillaBlockType.ROSE_BUSH)
                            .put("minecraft:sunflower", ChunkerVanillaBlockType.SUNFLOWER)
                            .put("minecraft:tall_grass", ChunkerVanillaBlockType.TALL_GRASS)
                            .build(),
                    BedrockStateGroups.DOUBLE_BLOCK));

            // Stone slabs got flattened (only the half)
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:brick_slab", ChunkerVanillaBlockType.BRICK_SLAB)
                            .put("minecraft:smooth_stone_slab", ChunkerVanillaBlockType.SMOOTH_STONE_SLAB)
                            .put("minecraft:nether_brick_slab", ChunkerVanillaBlockType.NETHER_BRICK_SLAB)
                            .put("minecraft:sandstone_slab", ChunkerVanillaBlockType.SANDSTONE_SLAB)
                            .put("minecraft:stone_brick_slab", ChunkerVanillaBlockType.STONE_BRICK_SLAB)
                            .put("minecraft:cobblestone_slab", ChunkerVanillaBlockType.COBBLESTONE_SLAB)
                            .put("minecraft:quartz_slab", ChunkerVanillaBlockType.QUARTZ_SLAB)
                            .put("minecraft:petrified_oak_slab", ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
        }


        // R21U1
        if (version.isGreaterThanOrEqual(1, 21, 10)) {
            // Prismarine blocks got flattened
            registerOverrideInputOutput(BlockMapping.of("minecraft:prismarine", ChunkerVanillaBlockType.PRISMARINE));
            registerOverrideOutput(BlockMapping.of("minecraft:dark_prismarine", ChunkerVanillaBlockType.DARK_PRISMARINE));
            registerOverrideOutput(BlockMapping.of("minecraft:prismarine_bricks", ChunkerVanillaBlockType.PRISMARINE_BRICKS));

            // Stone slabs got flattened (only the double)
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:brick_double_slab", ChunkerVanillaBlockType.BRICK_SLAB)
                            .put("minecraft:smooth_stone_double_slab", ChunkerVanillaBlockType.SMOOTH_STONE_SLAB)
                            .put("minecraft:nether_brick_double_slab", ChunkerVanillaBlockType.NETHER_BRICK_SLAB)
                            .put("minecraft:sandstone_double_slab", ChunkerVanillaBlockType.SANDSTONE_SLAB)
                            .put("minecraft:stone_brick_double_slab", ChunkerVanillaBlockType.STONE_BRICK_SLAB)
                            .put("minecraft:cobblestone_double_slab", ChunkerVanillaBlockType.COBBLESTONE_SLAB)
                            .put("minecraft:quartz_double_slab", ChunkerVanillaBlockType.QUARTZ_SLAB)
                            .put("minecraft:petrified_oak_double_slab", ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

            // Stone slabs 2 got flattened (half / double)
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:red_sandstone_slab", ChunkerVanillaBlockType.RED_SANDSTONE_SLAB)
                            .put("minecraft:purpur_slab", ChunkerVanillaBlockType.PURPUR_SLAB)
                            .put("minecraft:prismarine_slab", ChunkerVanillaBlockType.PRISMARINE_SLAB)
                            .put("minecraft:dark_prismarine_slab", ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB)
                            .put("minecraft:prismarine_brick_slab", ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB)
                            .put("minecraft:mossy_cobblestone_slab", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB)
                            .put("minecraft:smooth_sandstone_slab", ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB)
                            .put("minecraft:red_nether_brick_slab", ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:red_sandstone_double_slab", ChunkerVanillaBlockType.RED_SANDSTONE_SLAB)
                            .put("minecraft:purpur_double_slab", ChunkerVanillaBlockType.PURPUR_SLAB)
                            .put("minecraft:prismarine_double_slab", ChunkerVanillaBlockType.PRISMARINE_SLAB)
                            .put("minecraft:dark_prismarine_double_slab", ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB)
                            .put("minecraft:prismarine_brick_double_slab", ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB)
                            .put("minecraft:mossy_cobblestone_double_slab", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB)
                            .put("minecraft:smooth_sandstone_double_slab", ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB)
                            .put("minecraft:red_nether_brick_double_slab", ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

            // Stone slabs 3 got flattened (half / double)
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:end_stone_brick_slab", ChunkerVanillaBlockType.END_STONE_BRICK_SLAB)
                            .put("minecraft:smooth_red_sandstone_slab", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB)
                            .put("minecraft:polished_andesite_slab", ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB)
                            .put("minecraft:andesite_slab", ChunkerVanillaBlockType.ANDESITE_SLAB)
                            .put("minecraft:diorite_slab", ChunkerVanillaBlockType.DIORITE_SLAB)
                            .put("minecraft:polished_diorite_slab", ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB)
                            .put("minecraft:granite_slab", ChunkerVanillaBlockType.GRANITE_SLAB)
                            .put("minecraft:polished_granite_slab", ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:end_stone_brick_double_slab", ChunkerVanillaBlockType.END_STONE_BRICK_SLAB)
                            .put("minecraft:smooth_red_sandstone_double_slab", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB)
                            .put("minecraft:polished_andesite_double_slab", ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB)
                            .put("minecraft:andesite_double_slab", ChunkerVanillaBlockType.ANDESITE_SLAB)
                            .put("minecraft:diorite_double_slab", ChunkerVanillaBlockType.DIORITE_SLAB)
                            .put("minecraft:polished_diorite_double_slab", ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB)
                            .put("minecraft:granite_double_slab", ChunkerVanillaBlockType.GRANITE_SLAB)
                            .put("minecraft:polished_granite_double_slab", ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

            // Stone slabs 4 got flattened (half / double)
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:mossy_stone_brick_slab", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                            .put("minecraft:smooth_quartz_slab", ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB)
                            .put("minecraft:normal_stone_slab", ChunkerVanillaBlockType.STONE_SLAB)
                            .put("minecraft:cut_sandstone_slab", ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB)
                            .put("minecraft:cut_red_sandstone_slab", ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_HALF));
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:mossy_stone_brick_double_slab", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                            .put("minecraft:smooth_quartz_double_slab", ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB)
                            .put("minecraft:normal_stone_double_slab", ChunkerVanillaBlockType.STONE_SLAB)
                            .put("minecraft:cut_sandstone_double_slab", ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB)
                            .put("minecraft:cut_red_sandstone_double_slab", ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB)
                            .build(),
                    BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

            // Coral Wall Fans got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:dead_brain_coral_wall_fan", ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_WALL_FAN)
                            .put("minecraft:dead_bubble_coral_wall_fan", ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_WALL_FAN)
                            .put("minecraft:dead_fire_coral_wall_fan", ChunkerVanillaBlockType.DEAD_FIRE_CORAL_WALL_FAN)
                            .put("minecraft:dead_horn_coral_wall_fan", ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN)
                            .put("minecraft:dead_tube_coral_wall_fan", ChunkerVanillaBlockType.DEAD_TUBE_CORAL_WALL_FAN)
                            .put("minecraft:brain_coral_wall_fan", ChunkerVanillaBlockType.BRAIN_CORAL_WALL_FAN)
                            .put("minecraft:bubble_coral_wall_fan", ChunkerVanillaBlockType.BUBBLE_CORAL_WALL_FAN)
                            .put("minecraft:fire_coral_wall_fan", ChunkerVanillaBlockType.FIRE_CORAL_WALL_FAN)
                            .put("minecraft:horn_coral_wall_fan", ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN)
                            .put("minecraft:tube_coral_wall_fan", ChunkerVanillaBlockType.TUBE_CORAL_WALL_FAN)
                            .build(),
                    BedrockStateGroups.CORAL_HANGING
            ));

            // Stone bricks got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:stone_bricks", ChunkerVanillaBlockType.STONE_BRICKS)
                    .put("minecraft:mossy_stone_bricks", ChunkerVanillaBlockType.MOSSY_STONE_BRICKS)
                    .put("minecraft:cracked_stone_bricks", ChunkerVanillaBlockType.CRACKED_STONE_BRICKS)
                    .put("minecraft:chiseled_stone_bricks", ChunkerVanillaBlockType.CHISELED_STONE_BRICKS)
                    .build()
            ));

            // Infested stone got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:infested_cobblestone", ChunkerVanillaBlockType.INFESTED_COBBLESTONE)
                    .put("minecraft:infested_stone", ChunkerVanillaBlockType.INFESTED_STONE)
                    .put("minecraft:infested_stone_bricks", ChunkerVanillaBlockType.INFESTED_STONE_BRICKS)
                    .put("minecraft:infested_mossy_stone_bricks", ChunkerVanillaBlockType.INFESTED_MOSSY_STONE_BRICKS)
                    .put("minecraft:infested_cracked_stone_bricks", ChunkerVanillaBlockType.INFESTED_CRACKED_STONE_BRICKS)
                    .put("minecraft:infested_chiseled_stone_bricks", ChunkerVanillaBlockType.INFESTED_CHISELED_STONE_BRICKS)
                    .build()
            ));
        }

        // R21U2
        if (version.isGreaterThanOrEqual(1, 21, 20)) {
            // Dandelion got renamed
            registerOverrideOutput(BlockMapping.of("minecraft:dandelion", ChunkerVanillaBlockType.DANDELION));

            // Light Blocks got flattened
            register(BlockMapping.merge(ChunkerVanillaBlockType.LIGHT, VanillaBlockStates.LIGHT_LEVEL, ImmutableMultimap.<String, LightLevel>builder()
                    .put("minecraft:light_block_0", LightLevel._0)
                    .put("minecraft:light_block_1", LightLevel._1)
                    .put("minecraft:light_block_2", LightLevel._2)
                    .put("minecraft:light_block_3", LightLevel._3)
                    .put("minecraft:light_block_4", LightLevel._4)
                    .put("minecraft:light_block_5", LightLevel._5)
                    .put("minecraft:light_block_6", LightLevel._6)
                    .put("minecraft:light_block_7", LightLevel._7)
                    .put("minecraft:light_block_8", LightLevel._8)
                    .put("minecraft:light_block_9", LightLevel._9)
                    .put("minecraft:light_block_10", LightLevel._10)
                    .put("minecraft:light_block_11", LightLevel._11)
                    .put("minecraft:light_block_12", LightLevel._12)
                    .put("minecraft:light_block_13", LightLevel._13)
                    .put("minecraft:light_block_14", LightLevel._14)
                    .put("minecraft:light_block_15", LightLevel._15)
                    .build()
            ));
            registerDuplicateOverrideOutput(BlockMapping.of("minecraft:light_block_0", ChunkerVanillaBlockType.LIGHT));

            // Sandstone got flattened
            registerOverrideInputOutput(BlockMapping.of("minecraft:sandstone", ChunkerVanillaBlockType.SANDSTONE));
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:smooth_sandstone", ChunkerVanillaBlockType.SMOOTH_SANDSTONE)
                    .put("minecraft:cut_sandstone", ChunkerVanillaBlockType.CUT_SANDSTONE)
                    .put("minecraft:chiseled_sandstone", ChunkerVanillaBlockType.CHISELED_SANDSTONE)
                    .build()
            ));

            // Red Sandstone got flattened
            registerOverrideInputOutput(BlockMapping.of("minecraft:red_sandstone", ChunkerVanillaBlockType.RED_SANDSTONE));
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:smooth_red_sandstone", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE)
                    .put("minecraft:cut_red_sandstone", ChunkerVanillaBlockType.CUT_RED_SANDSTONE)
                    .put("minecraft:chiseled_red_sandstone", ChunkerVanillaBlockType.CHISELED_RED_SANDSTONE)
                    .build()
            ));

            // Anvil got flattened
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:anvil", ChunkerVanillaBlockType.ANVIL)
                            .put("minecraft:chipped_anvil", ChunkerVanillaBlockType.CHIPPED_ANVIL)
                            .put("minecraft:damaged_anvil", ChunkerVanillaBlockType.DAMAGED_ANVIL)
                            .build(),
                    BedrockStateGroups.ANVIL
            ));
            // Deprecated anvil is visually the same
            registerDuplicateOutput(BlockMapping.of("minecraft:deprecated_anvil", ChunkerVanillaBlockType.DAMAGED_ANVIL, BedrockStateGroups.ANVIL));

            // Quartz got flattened
            registerOverrideOutput(BlockMapping.of("minecraft:quartz_block", "pillar_axis", "y", ChunkerVanillaBlockType.QUARTZ_BLOCK));
            registerDuplicateOverrideInput(BlockMapping.of("minecraft:quartz_block", ChunkerVanillaBlockType.QUARTZ_BLOCK));
            registerOverrideOutput(BlockMapping.of("minecraft:chiseled_quartz_block", "pillar_axis", "y", ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));
            registerDuplicateOutput(BlockMapping.of("minecraft:chiseled_quartz_block", ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));
            registerOverrideOutput(BlockMapping.of("minecraft:smooth_quartz", "pillar_axis", "y", ChunkerVanillaBlockType.SMOOTH_QUARTZ));
            registerDuplicateOutput(BlockMapping.of("minecraft:smooth_quartz", ChunkerVanillaBlockType.SMOOTH_QUARTZ));

            registerOverrideOutput(BlockMapping.of("minecraft:quartz_pillar", ChunkerVanillaBlockType.QUARTZ_PILLAR, BedrockStateGroups.PILLAR_BLOCK));

            // Sand got flattened
            registerOverrideInputOutput(BlockMapping.of("minecraft:sand", ChunkerVanillaBlockType.SAND));
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:red_sand", ChunkerVanillaBlockType.RED_SAND)
                    .build()
            ));

            // Dirt got flattened
            registerOverrideInputOutput(BlockMapping.of("minecraft:dirt", ChunkerVanillaBlockType.DIRT));
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                    .put("minecraft:coarse_dirt", ChunkerVanillaBlockType.COARSE_DIRT)
                    .build()
            ));
        }

        // R21U3
        if (version.isGreaterThanOrEqual(1, 21, 30)) {
            // Walls got flattened
            registerOverrideOutput(BlockMapping.of("minecraft:cobblestone_wall", ChunkerVanillaBlockType.COBBLESTONE_WALL, BedrockStateGroups.WALL));
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:andesite_wall", ChunkerVanillaBlockType.ANDESITE_WALL)
                            .put("minecraft:brick_wall", ChunkerVanillaBlockType.BRICK_WALL)
                            .put("minecraft:diorite_wall", ChunkerVanillaBlockType.DIORITE_WALL)
                            .put("minecraft:end_stone_brick_wall", ChunkerVanillaBlockType.END_STONE_BRICK_WALL)
                            .put("minecraft:granite_wall", ChunkerVanillaBlockType.GRANITE_WALL)
                            .put("minecraft:mossy_cobblestone_wall", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_WALL)
                            .put("minecraft:mossy_stone_brick_wall", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_WALL)
                            .put("minecraft:nether_brick_wall", ChunkerVanillaBlockType.NETHER_BRICK_WALL)
                            .put("minecraft:prismarine_wall", ChunkerVanillaBlockType.PRISMARINE_WALL)
                            .put("minecraft:red_nether_brick_wall", ChunkerVanillaBlockType.RED_NETHER_BRICK_WALL)
                            .put("minecraft:red_sandstone_wall", ChunkerVanillaBlockType.RED_SANDSTONE_WALL)
                            .put("minecraft:sandstone_wall", ChunkerVanillaBlockType.SANDSTONE_WALL)
                            .put("minecraft:stone_brick_wall", ChunkerVanillaBlockType.STONE_BRICK_WALL)
                            .build(),
                    BedrockStateGroups.WALL));

            // Sponge got flattened
            registerOverrideInputOutput(BlockMapping.of("minecraft:sponge", ChunkerVanillaBlockType.SPONGE));
            registerOverrideOutput(BlockMapping.of("minecraft:wet_sponge", ChunkerVanillaBlockType.WET_SPONGE));

            // TNT got flattened
            register(BlockMapping.of("minecraft:underwater_tnt", ChunkerVanillaBlockType.TNT, BedrockStateGroups.TNT, VanillaBlockStates.UNDERWATER, Bool.TRUE));

            // Purpur got flattened
            registerOverrideOutput(BlockMapping.of("minecraft:purpur_block", "pillar_axis", "y", ChunkerVanillaBlockType.PURPUR_BLOCK));
            registerDuplicateOverrideInput(BlockMapping.of("minecraft:purpur_block", ChunkerVanillaBlockType.PURPUR_BLOCK));
            registerOverrideOutput(BlockMapping.of("minecraft:purpur_pillar", ChunkerVanillaBlockType.PURPUR_PILLAR, BedrockStateGroups.PILLAR_BLOCK));

            // Deprecated purpur variants
            registerDuplicateOutput(BlockMapping.of("minecraft:deprecated_purpur_block_1", ChunkerVanillaBlockType.PURPUR_BLOCK));
            registerDuplicateOutput(BlockMapping.of("minecraft:deprecated_purpur_block_2", ChunkerVanillaBlockType.PURPUR_BLOCK));
        }

        // R21U4
        if (version.isGreaterThanOrEqual(1, 21, 40)) {
            // Fixed the stripped_bit being present on cherry/mangrove wood
            registerOverrideOutput(BlockMapping.of("minecraft:cherry_wood", ChunkerVanillaBlockType.CHERRY_WOOD, BedrockStateGroups.PILLAR_BLOCK));
            registerOverrideOutput(BlockMapping.of("minecraft:mangrove_wood", ChunkerVanillaBlockType.MANGROVE_WOOD, BedrockStateGroups.PILLAR_BLOCK));

            // Flattened - Wall Skulls
            registerOverrideOutput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:skeleton_skull", ChunkerVanillaBlockType.SKELETON_WALL_SKULL)
                            .put("minecraft:zombie_head", ChunkerVanillaBlockType.ZOMBIE_WALL_HEAD)
                            .put("minecraft:player_head", ChunkerVanillaBlockType.PLAYER_WALL_HEAD)
                            .put("minecraft:creeper_head", ChunkerVanillaBlockType.CREEPER_WALL_HEAD)
                            .put("minecraft:wither_skeleton_skull", ChunkerVanillaBlockType.WITHER_SKELETON_WALL_SKULL)
                            .put("minecraft:dragon_head", ChunkerVanillaBlockType.DRAGON_WALL_HEAD)
                            .put("minecraft:piglin_head", ChunkerVanillaBlockType.PIGLIN_WALL_HEAD)
                            .build(),
                    BedrockStateGroups.WALL_SKULL));

            // Flattened - Skulls
            registerOverrideOutput(BlockMapping.group("facing_direction", 1, ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:skeleton_skull", ChunkerVanillaBlockType.SKELETON_SKULL)
                            .put("minecraft:zombie_head", ChunkerVanillaBlockType.ZOMBIE_HEAD)
                            .put("minecraft:player_head", ChunkerVanillaBlockType.PLAYER_HEAD)
                            .put("minecraft:creeper_head", ChunkerVanillaBlockType.CREEPER_HEAD)
                            .put("minecraft:wither_skeleton_skull", ChunkerVanillaBlockType.WITHER_SKELETON_SKULL)
                            .put("minecraft:dragon_head", ChunkerVanillaBlockType.DRAGON_HEAD)
                            .put("minecraft:piglin_head", ChunkerVanillaBlockType.PIGLIN_HEAD)
                            .build(),
                    BedrockStateGroups.SKULL));

            // Flattened mushroom stem block (only uses huge_mushroom_bits 10 and 15)
            registerOverrideOutput(BlockMapping.of("minecraft:mushroom_stem", "huge_mushroom_bits", 15, ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                    VanillaBlockStates.NORTH, Bool.TRUE,
                    VanillaBlockStates.EAST, Bool.TRUE,
                    VanillaBlockStates.SOUTH, Bool.TRUE,
                    VanillaBlockStates.WEST, Bool.TRUE,
                    VanillaBlockStates.UP, Bool.TRUE,
                    VanillaBlockStates.DOWN, Bool.TRUE
            )));
            registerOverrideOutput(BlockMapping.of("minecraft:mushroom_stem", "huge_mushroom_bits", 10, ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                    VanillaBlockStates.NORTH, Bool.TRUE,
                    VanillaBlockStates.EAST, Bool.TRUE,
                    VanillaBlockStates.SOUTH, Bool.TRUE,
                    VanillaBlockStates.WEST, Bool.TRUE,
                    VanillaBlockStates.UP, Bool.FALSE,
                    VanillaBlockStates.DOWN, Bool.FALSE
            )));

            // Defaults for all the other chunker states
            registerDuplicateOverrideOutput(BlockMapping.of("minecraft:mushroom_stem", "huge_mushroom_bits", 15, ChunkerVanillaBlockType.MUSHROOM_STEM));

            // Defaults for all the other huge_mushroom_bits states
            registerDuplicateOutput(BlockMapping.of("minecraft:mushroom_stem", ChunkerVanillaBlockType.MUSHROOM_STEM, Map.of(
                    VanillaBlockStates.NORTH, Bool.TRUE,
                    VanillaBlockStates.EAST, Bool.TRUE,
                    VanillaBlockStates.SOUTH, Bool.TRUE,
                    VanillaBlockStates.WEST, Bool.TRUE,
                    VanillaBlockStates.UP, Bool.TRUE,
                    VanillaBlockStates.DOWN, Bool.TRUE
            )));
        }

        // R21U5
        if (version.isGreaterThanOrEqual(1, 21, 50)) {
            register(BlockMapping.of("minecraft:creaking_heart", ChunkerVanillaBlockType.CREAKING_HEART, BedrockStateGroups.CREAKING_HEART));
            register(BlockMapping.of("minecraft:pale_hanging_moss", ChunkerVanillaBlockType.PALE_HANGING_MOSS, BedrockStateGroups.PALE_HANGING_MOSS));
            register(BlockMapping.of("minecraft:pale_moss_block", ChunkerVanillaBlockType.PALE_MOSS_BLOCK));
            register(BlockMapping.of("minecraft:pale_moss_carpet", ChunkerVanillaBlockType.PALE_MOSS_CARPET, BedrockStateGroups.PALE_MOSS_CARPET));

            // New pale oak blocks
            register(BlockMapping.of("minecraft:pale_oak_sapling", ChunkerVanillaBlockType.PALE_OAK_SAPLING, BedrockStateGroups.SAPLING));
            register(BlockMapping.of("minecraft:pale_oak_leaves", ChunkerVanillaBlockType.PALE_OAK_LEAVES, BedrockStateGroups.LEAVES));
            register(BlockMapping.of("minecraft:pale_oak_log", ChunkerVanillaBlockType.PALE_OAK_LOG, BedrockStateGroups.PILLAR_BLOCK));
            register(BlockMapping.of("minecraft:pale_oak_wood", ChunkerVanillaBlockType.PALE_OAK_WOOD, BedrockStateGroups.PILLAR_BLOCK));
            register(BlockMapping.of("minecraft:stripped_pale_oak_log", ChunkerVanillaBlockType.STRIPPED_PALE_OAK_LOG, BedrockStateGroups.PILLAR_BLOCK));
            register(BlockMapping.of("minecraft:stripped_pale_oak_wood", ChunkerVanillaBlockType.STRIPPED_PALE_OAK_WOOD, BedrockStateGroups.PILLAR_BLOCK));

            register(BlockMapping.of("minecraft:pale_oak_button", ChunkerVanillaBlockType.PALE_OAK_BUTTON, BedrockStateGroups.BUTTON));
            register(BlockMapping.of("minecraft:pale_oak_door", ChunkerVanillaBlockType.PALE_OAK_DOOR, BedrockStateGroups.DOOR));
            register(BlockMapping.of("minecraft:pale_oak_fence", ChunkerVanillaBlockType.PALE_OAK_FENCE, BedrockStateGroups.CONNECTABLE_HORIZONTAL));
            register(BlockMapping.of("minecraft:pale_oak_fence_gate", ChunkerVanillaBlockType.PALE_OAK_FENCE_GATE, BedrockStateGroups.FENCE_GATE));
            register(BlockMapping.of("minecraft:pale_oak_planks", ChunkerVanillaBlockType.PALE_OAK_PLANKS));
            register(BlockMapping.of("minecraft:pale_oak_pressure_plate", ChunkerVanillaBlockType.PALE_OAK_PRESSURE_PLATE, BedrockStateGroups.PRESSURE_PLATE));
            register(BlockMapping.of("minecraft:pale_oak_slab", ChunkerVanillaBlockType.PALE_OAK_SLAB, BedrockStateGroups.SLAB_HALF));
            register(BlockMapping.of("minecraft:pale_oak_double_slab", ChunkerVanillaBlockType.PALE_OAK_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:pale_oak_stairs", ChunkerVanillaBlockType.PALE_OAK_STAIRS, BedrockStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:pale_oak_trapdoor", ChunkerVanillaBlockType.PALE_OAK_TRAPDOOR, BedrockStateGroups.TRAPDOOR));

            // New flowers
            register(BlockMapping.of("minecraft:closed_eyeblossom", ChunkerVanillaBlockType.CLOSED_EYEBLOSSOM));
            register(BlockMapping.of("minecraft:open_eyeblossom", ChunkerVanillaBlockType.OPEN_EYEBLOSSOM));

            // New signs
            register(BlockMapping.of("minecraft:pale_oak_hanging_sign", "hanging", false, ChunkerVanillaBlockType.PALE_OAK_WALL_HANGING_SIGN, BedrockStateGroups.HANGING_WALL_SIGN));
            register(BlockMapping.of("minecraft:pale_oak_hanging_sign", "hanging", true, ChunkerVanillaBlockType.PALE_OAK_HANGING_SIGN, BedrockStateGroups.HANGING_SIGN));
            register(BlockMapping.of("minecraft:pale_oak_standing_sign", ChunkerVanillaBlockType.PALE_OAK_SIGN, BedrockStateGroups.SIGN));
            register(BlockMapping.of("minecraft:pale_oak_wall_sign", ChunkerVanillaBlockType.PALE_OAK_WALL_SIGN, BedrockStateGroups.FACING_DIRECTION_HORIZONTAL));

            // New resin blocks
            register(BlockMapping.of("minecraft:resin_block", ChunkerVanillaBlockType.RESIN_BLOCK));
            register(BlockMapping.of("minecraft:resin_bricks", ChunkerVanillaBlockType.RESIN_BRICKS));
            register(BlockMapping.of("minecraft:chiseled_resin_bricks", ChunkerVanillaBlockType.CHISELED_RESIN_BRICKS));
            register(BlockMapping.of("minecraft:resin_brick_slab", ChunkerVanillaBlockType.RESIN_BRICK_SLAB, BedrockStateGroups.SLAB_HALF));
            register(BlockMapping.of("minecraft:resin_brick_double_slab", ChunkerVanillaBlockType.RESIN_BRICK_SLAB, BedrockStateGroups.SLAB_DOUBLE, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:resin_brick_stairs", ChunkerVanillaBlockType.RESIN_BRICK_STAIRS, BedrockStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:resin_brick_wall", ChunkerVanillaBlockType.RESIN_BRICK_WALL, BedrockStateGroups.WALL));
            register(BlockMapping.of("minecraft:resin_clump", ChunkerVanillaBlockType.RESIN_CLUMP, BedrockStateGroups.MULTIFACE));
        }

        // R21U7
        if (version.isGreaterThanOrEqual(1, 21, 70)) {
            register(BlockMapping.of("minecraft:bush", ChunkerVanillaBlockType.BUSH));
            register(BlockMapping.of("minecraft:cactus_flower", ChunkerVanillaBlockType.CACTUS_FLOWER));
            register(BlockMapping.of("minecraft:firefly_bush", ChunkerVanillaBlockType.FIREFLY_BUSH));
            register(BlockMapping.of("minecraft:leaf_litter", ChunkerVanillaBlockType.LEAF_LITTER, BedrockStateGroups.LEAF_LITTER));
            register(BlockMapping.of("minecraft:short_dry_grass", ChunkerVanillaBlockType.SHORT_DRY_GRASS));
            register(BlockMapping.of("minecraft:tall_dry_grass", ChunkerVanillaBlockType.TALL_DRY_GRASS));
            register(BlockMapping.of("minecraft:wildflowers", ChunkerVanillaBlockType.WILDFLOWERS, BedrockStateGroups.WILDFLOWERS));
        }

        // R21U8
        if (version.isGreaterThanOrEqual(1, 21, 80)) {
            register(BlockMapping.of("minecraft:dried_ghast", ChunkerVanillaBlockType.DRIED_GHAST, BedrockStateGroups.DRIED_GHAST));
        }

        // R21U10
        if (version.isGreaterThanOrEqual(1, 21, 100)) {
            register(BlockMapping.of("minecraft:copper_chest", ChunkerVanillaBlockType.COPPER_CHEST, BedrockStateGroups.CHEST));
            register(BlockMapping.of("minecraft:exposed_copper_chest", ChunkerVanillaBlockType.EXPOSED_COPPER_CHEST, BedrockStateGroups.CHEST));
            register(BlockMapping.of("minecraft:oxidized_copper_chest", ChunkerVanillaBlockType.OXIDIZED_COPPER_CHEST, BedrockStateGroups.CHEST));
            register(BlockMapping.of("minecraft:waxed_copper_chest", ChunkerVanillaBlockType.WAXED_COPPER_CHEST, BedrockStateGroups.CHEST));
            register(BlockMapping.of("minecraft:waxed_exposed_copper_chest", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_CHEST, BedrockStateGroups.CHEST));
            register(BlockMapping.of("minecraft:waxed_oxidized_copper_chest", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_CHEST, BedrockStateGroups.CHEST));
            register(BlockMapping.of("minecraft:waxed_weathered_copper_chest", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_CHEST, BedrockStateGroups.CHEST));
            register(BlockMapping.of("minecraft:weathered_copper_chest", ChunkerVanillaBlockType.WEATHERED_COPPER_CHEST, BedrockStateGroups.CHEST));
        }
    }
}
