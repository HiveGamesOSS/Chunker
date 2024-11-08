package com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy;

import com.google.common.collect.ImmutableMultimap;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.BlockMapping;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.ChunkerBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateMappingGroup;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.*;
import it.unimi.dsi.fastutil.Pair;

import java.util.List;
import java.util.Map;

/**
 * Resolver to convert between Java legacy block identifiers and ChunkerBlockIdentifier.
 */
public class JavaLegacyBlockIdentifierResolver extends ChunkerBlockIdentifierResolver {
    /**
     * Default states which use 0 for data and false for waterlogged.
     */
    public static final StateMappingGroup DEFAULT_DATA_WATERLOGGED_FALSE = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.FALSE)
            .defaultInput("data", 0)
            .build();

    /**
     * Create a new legacy java block identifier resolver.
     *
     * @param converter                the converter instance.
     * @param version                  the version being resolved.
     * @param reader                   whether this is used for the reader.
     * @param customIdentifiersAllowed whether unknown identifiers should be converted to custom identifiers.
     */
    public JavaLegacyBlockIdentifierResolver(Converter converter, Version version, boolean reader, boolean customIdentifiersAllowed) {
        super(converter, version, reader, customIdentifiersAllowed);
    }

    @Override
    public void registerMappings(Version version) {
        extraStateMappingGroup(DEFAULT_DATA_WATERLOGGED_FALSE);

        // Air
        register(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.AIR));
        registerDuplicateInput(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.VOID_AIR));
        registerDuplicateInput(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.CAVE_AIR));

        // Blocks
        register(BlockMapping.of("minecraft:beacon", ChunkerVanillaBlockType.BEACON));
        register(BlockMapping.of("minecraft:bedrock", ChunkerVanillaBlockType.BEDROCK, JavaLegacyStateGroups.BEDROCK));
        register(BlockMapping.of("minecraft:bookshelf", ChunkerVanillaBlockType.BOOKSHELF));
        register(BlockMapping.of("minecraft:brewing_stand", ChunkerVanillaBlockType.BREWING_STAND, JavaLegacyStateGroups.BREWING_STAND));
        register(BlockMapping.of("minecraft:brick_block", ChunkerVanillaBlockType.BRICKS));
        register(BlockMapping.of("minecraft:brown_mushroom", ChunkerVanillaBlockType.BROWN_MUSHROOM));
        register(BlockMapping.of("minecraft:cake", ChunkerVanillaBlockType.CAKE, JavaLegacyStateGroups.CAKE));
        register(BlockMapping.of("minecraft:clay", ChunkerVanillaBlockType.CLAY));
        register(BlockMapping.of("minecraft:coal_block", ChunkerVanillaBlockType.COAL_BLOCK));
        register(BlockMapping.of("minecraft:coal_ore", ChunkerVanillaBlockType.COAL_ORE));
        register(BlockMapping.of("minecraft:cobblestone", ChunkerVanillaBlockType.COBBLESTONE));
        register(BlockMapping.of("minecraft:web", ChunkerVanillaBlockType.COBWEB));
        register(BlockMapping.of("minecraft:cocoa", ChunkerVanillaBlockType.COCOA, JavaLegacyStateGroups.COCOA));
        register(BlockMapping.of("minecraft:crafting_table", ChunkerVanillaBlockType.CRAFTING_TABLE));
        register(BlockMapping.of("minecraft:yellow_flower", ChunkerVanillaBlockType.DANDELION));
        register(BlockMapping.of("minecraft:deadbush", ChunkerVanillaBlockType.DEAD_BUSH));
        register(BlockMapping.of("minecraft:diamond_block", ChunkerVanillaBlockType.DIAMOND_BLOCK));
        register(BlockMapping.of("minecraft:diamond_ore", ChunkerVanillaBlockType.DIAMOND_ORE));
        register(BlockMapping.of("minecraft:dragon_egg", ChunkerVanillaBlockType.DRAGON_EGG));
        register(BlockMapping.of("minecraft:emerald_block", ChunkerVanillaBlockType.EMERALD_BLOCK));
        register(BlockMapping.of("minecraft:emerald_ore", ChunkerVanillaBlockType.EMERALD_ORE));
        register(BlockMapping.of("minecraft:enchanting_table", ChunkerVanillaBlockType.ENCHANTING_TABLE));
        register(BlockMapping.of("minecraft:end_portal", ChunkerVanillaBlockType.END_PORTAL));
        register(BlockMapping.of("minecraft:end_portal_frame", ChunkerVanillaBlockType.END_PORTAL_FRAME, JavaLegacyStateGroups.END_PORTAL_FRAME));
        register(BlockMapping.of("minecraft:end_stone", ChunkerVanillaBlockType.END_STONE));
        register(BlockMapping.of("minecraft:farmland", ChunkerVanillaBlockType.FARMLAND, JavaLegacyStateGroups.FARMLAND));
        register(BlockMapping.of("minecraft:fire", ChunkerVanillaBlockType.FIRE, JavaLegacyStateGroups.FIRE));
        register(BlockMapping.of("minecraft:glass", ChunkerVanillaBlockType.GLASS));
        register(BlockMapping.of("minecraft:glowstone", ChunkerVanillaBlockType.GLOWSTONE));
        register(BlockMapping.of("minecraft:gold_block", ChunkerVanillaBlockType.GOLD_BLOCK));
        register(BlockMapping.of("minecraft:gold_ore", ChunkerVanillaBlockType.GOLD_ORE));
        register(BlockMapping.of("minecraft:gravel", ChunkerVanillaBlockType.GRAVEL));
        register(BlockMapping.of("minecraft:hopper", ChunkerVanillaBlockType.HOPPER, JavaLegacyStateGroups.HOPPER));
        register(BlockMapping.of("minecraft:ice", ChunkerVanillaBlockType.ICE));
        register(BlockMapping.of("minecraft:iron_block", ChunkerVanillaBlockType.IRON_BLOCK));
        register(BlockMapping.of("minecraft:iron_ore", ChunkerVanillaBlockType.IRON_ORE));
        register(BlockMapping.of("minecraft:jukebox", ChunkerVanillaBlockType.JUKEBOX, JavaLegacyStateGroups.JUKEBOX));
        register(BlockMapping.of("minecraft:lapis_block", ChunkerVanillaBlockType.LAPIS_BLOCK));
        register(BlockMapping.of("minecraft:lapis_ore", ChunkerVanillaBlockType.LAPIS_ORE));
        register(BlockMapping.of("minecraft:waterlily", ChunkerVanillaBlockType.LILY_PAD));
        register(BlockMapping.of("minecraft:melon_block", ChunkerVanillaBlockType.MELON));
        register(BlockMapping.of("minecraft:mossy_cobblestone", ChunkerVanillaBlockType.MOSSY_COBBLESTONE));
        register(BlockMapping.of("minecraft:nether_brick", ChunkerVanillaBlockType.NETHER_BRICKS));
        register(BlockMapping.of("minecraft:portal", ChunkerVanillaBlockType.NETHER_PORTAL, JavaLegacyStateGroups.NETHER_PORTAL));
        register(BlockMapping.of("minecraft:quartz_ore", ChunkerVanillaBlockType.NETHER_QUARTZ_ORE));
        register(BlockMapping.of("minecraft:netherrack", ChunkerVanillaBlockType.NETHERRACK));
        register(BlockMapping.of("minecraft:noteblock", ChunkerVanillaBlockType.NOTE_BLOCK, JavaLegacyStateGroups.NOTE_BLOCK));
        register(BlockMapping.of("minecraft:obsidian", ChunkerVanillaBlockType.OBSIDIAN));
        register(BlockMapping.of("minecraft:packed_ice", ChunkerVanillaBlockType.PACKED_ICE));
        register(BlockMapping.of("minecraft:piston_head", ChunkerVanillaBlockType.PISTON_HEAD, JavaLegacyStateGroups.PISTON_HEAD));
        register(BlockMapping.of("minecraft:pumpkin", ChunkerVanillaBlockType.CARVED_PUMPKIN, JavaLegacyStateGroups.FACING_HORIZONTAL_SWNE));
        register(BlockMapping.of("minecraft:lit_pumpkin", ChunkerVanillaBlockType.JACK_O_LANTERN, JavaLegacyStateGroups.FACING_HORIZONTAL_SWNE));
        register(BlockMapping.of("minecraft:rail", ChunkerVanillaBlockType.RAIL, JavaLegacyStateGroups.RAIL));
        register(BlockMapping.of("minecraft:red_mushroom", ChunkerVanillaBlockType.RED_MUSHROOM));
        register(BlockMapping.of("minecraft:redstone_block", ChunkerVanillaBlockType.REDSTONE_BLOCK));
        register(BlockMapping.of("minecraft:redstone_wire", ChunkerVanillaBlockType.REDSTONE_WIRE, JavaLegacyStateGroups.REDSTONE_WIRE));
        register(BlockMapping.of("minecraft:sea_lantern", ChunkerVanillaBlockType.SEA_LANTERN));
        register(BlockMapping.of("minecraft:slime", ChunkerVanillaBlockType.SLIME_BLOCK));
        register(BlockMapping.of("minecraft:snow_layer", ChunkerVanillaBlockType.SNOW, JavaLegacyStateGroups.SNOW));
        register(BlockMapping.of("minecraft:snow", ChunkerVanillaBlockType.SNOW_BLOCK));
        register(BlockMapping.of("minecraft:soul_sand", ChunkerVanillaBlockType.SOUL_SAND));
        register(BlockMapping.of("minecraft:mob_spawner", ChunkerVanillaBlockType.SPAWNER));
        register(BlockMapping.of("minecraft:hardened_clay", ChunkerVanillaBlockType.TERRACOTTA));
        register(BlockMapping.of("minecraft:tnt", ChunkerVanillaBlockType.TNT, JavaLegacyStateGroups.TNT));
        register(BlockMapping.of("minecraft:tripwire", ChunkerVanillaBlockType.TRIPWIRE, JavaLegacyStateGroups.TRIPWIRE));
        register(BlockMapping.of("minecraft:tripwire_hook", ChunkerVanillaBlockType.TRIPWIRE_HOOK, JavaLegacyStateGroups.TRIPWIRE_HOOK));
        register(BlockMapping.of("minecraft:vine", ChunkerVanillaBlockType.VINE, JavaLegacyStateGroups.VINE));

        // Sponge
        register(BlockMapping.flatten("minecraft:sponge", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.SPONGE)
                        .put(1, ChunkerVanillaBlockType.WET_SPONGE)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:sponge", ChunkerVanillaBlockType.SPONGE));

        // Prismarine
        register(BlockMapping.of("minecraft:prismarine", ChunkerVanillaBlockType.PRISMARINE));
        register(BlockMapping.of("minecraft:prismarine", "data", 1, ChunkerVanillaBlockType.PRISMARINE_BRICKS));
        register(BlockMapping.of("minecraft:prismarine", "data", 2, ChunkerVanillaBlockType.DARK_PRISMARINE));

        // Moving Block (not equivalent to moving piston)
        registerDuplicateInput(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.MOVING_BLOCK_BEDROCK));
        register(BlockMapping.of("minecraft:piston_extension", ChunkerVanillaBlockType.MOVING_PISTON_JAVA, JavaLegacyStateGroups.PISTON_EXTENSION));

        // Barrier (also use barrier for invisible_bedrock)
        register(BlockMapping.of("minecraft:barrier", ChunkerVanillaBlockType.BARRIER));
        registerDuplicateInput(BlockMapping.of("minecraft:barrier", ChunkerVanillaBlockType.INVISIBLE_BEDROCK));

        // Signs
        register(BlockMapping.of("minecraft:standing_sign", ChunkerVanillaBlockType.OAK_SIGN, JavaLegacyStateGroups.ROTATION));
        register(BlockMapping.of("minecraft:wall_sign", ChunkerVanillaBlockType.OAK_WALL_SIGN, JavaLegacyStateGroups.FACING_HORIZONTAL_UNUSUAL));

        // Cauldron
        register(BlockMapping.of("minecraft:cauldron", "data", 0, ChunkerVanillaBlockType.CAULDRON));

        // Cauldron levels should be mapped to water cauldron since in newer versions the block is split
        register(BlockMapping.of("minecraft:cauldron", ChunkerVanillaBlockType.WATER_CAULDRON, JavaLegacyStateGroups.CAULDRON));

        // Pistons
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:piston", ChunkerVanillaBlockType.PISTON)
                        .put("minecraft:sticky_piston", ChunkerVanillaBlockType.STICKY_PISTON)
                        .build(),
                JavaLegacyStateGroups.PISTON));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:dispenser", ChunkerVanillaBlockType.DISPENSER)
                        .put("minecraft:dropper", ChunkerVanillaBlockType.DROPPER)
                        .build(),
                JavaLegacyStateGroups.FACING_TRIGGERED));

        register(BlockMapping.of("minecraft:lever", ChunkerVanillaBlockType.LEVER, JavaLegacyStateGroups.LEVER));

        // Buttons
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:wooden_button", ChunkerVanillaBlockType.OAK_BUTTON)
                        .put("minecraft:stone_button", ChunkerVanillaBlockType.STONE_BUTTON)
                        .build(),
                JavaLegacyStateGroups.BUTTON));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:heavy_weighted_pressure_plate", ChunkerVanillaBlockType.HEAVY_WEIGHTED_PRESSURE_PLATE)
                        .put("minecraft:light_weighted_pressure_plate", ChunkerVanillaBlockType.LIGHT_WEIGHTED_PRESSURE_PLATE)
                        .build(),
                JavaLegacyStateGroups.POWER));

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

        // Trapdoors
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:iron_trapdoor", ChunkerVanillaBlockType.IRON_TRAPDOOR)
                        .put("minecraft:trapdoor", ChunkerVanillaBlockType.OAK_TRAPDOOR)
                        .build(),
                JavaLegacyStateGroups.TRAPDOOR));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:cactus", ChunkerVanillaBlockType.CACTUS)
                        .put("minecraft:reeds", ChunkerVanillaBlockType.SUGAR_CANE)
                        .build(),
                JavaLegacyStateGroups.AGE_15));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:ender_chest", ChunkerVanillaBlockType.ENDER_CHEST)
                        .put("minecraft:ladder", ChunkerVanillaBlockType.LADDER)
                        .build(),
                JavaLegacyStateGroups.FACING_HORIZONTAL_UNUSUAL));

        // Pressure plates
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:wooden_pressure_plate", ChunkerVanillaBlockType.OAK_PRESSURE_PLATE)
                        .put("minecraft:stone_pressure_plate", ChunkerVanillaBlockType.STONE_PRESSURE_PLATE)
                        .build(),
                JavaLegacyStateGroups.POWERED));
        register(BlockMapping.of("minecraft:nether_wart", ChunkerVanillaBlockType.NETHER_WART, JavaLegacyStateGroups.AGE_3));

        // Fence gates
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_fence_gate", ChunkerVanillaBlockType.ACACIA_FENCE_GATE)
                        .put("minecraft:birch_fence_gate", ChunkerVanillaBlockType.BIRCH_FENCE_GATE)
                        .put("minecraft:dark_oak_fence_gate", ChunkerVanillaBlockType.DARK_OAK_FENCE_GATE)
                        .put("minecraft:jungle_fence_gate", ChunkerVanillaBlockType.JUNGLE_FENCE_GATE)
                        .put("minecraft:fence_gate", ChunkerVanillaBlockType.OAK_FENCE_GATE)
                        .put("minecraft:spruce_fence_gate", ChunkerVanillaBlockType.SPRUCE_FENCE_GATE)
                        .build(),
                JavaLegacyStateGroups.FENCE_GATE));

        // Chests
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:chest", ChunkerVanillaBlockType.CHEST)
                        .put("minecraft:trapped_chest", ChunkerVanillaBlockType.TRAPPED_CHEST)
                        .build(),
                JavaLegacyStateGroups.CHEST));

        // Crops
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:carrots", ChunkerVanillaBlockType.CARROTS)
                        .put("minecraft:melon_stem", ChunkerVanillaBlockType.MELON_STEM)
                        .put("minecraft:potatoes", ChunkerVanillaBlockType.POTATOES)
                        .put("minecraft:pumpkin_stem", ChunkerVanillaBlockType.PUMPKIN_STEM)
                        .put("minecraft:wheat", ChunkerVanillaBlockType.WHEAT)
                        .build(),
                JavaLegacyStateGroups.AGE_7));

        // Stems
        register(BlockMapping.group("growth", 7, ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:pumpkin_stem", ChunkerVanillaBlockType.ATTACHED_PUMPKIN_STEM)
                        .put("minecraft:melon_stem", ChunkerVanillaBlockType.ATTACHED_MELON_STEM)
                        .build(),
                JavaLegacyStateGroups.STEM_FACING));

        // Redstone based rails
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:activator_rail", ChunkerVanillaBlockType.ACTIVATOR_RAIL)
                        .put("minecraft:detector_rail", ChunkerVanillaBlockType.DETECTOR_RAIL)
                        .put("minecraft:golden_rail", ChunkerVanillaBlockType.POWERED_RAIL)
                        .build(),
                JavaLegacyStateGroups.POWERED_RAIL));
        register(BlockMapping.of("minecraft:hay_block", ChunkerVanillaBlockType.HAY_BLOCK, JavaLegacyStateGroups.AXIS));

        // Command blocks
        register(BlockMapping.of("minecraft:command_block", ChunkerVanillaBlockType.COMMAND_BLOCK, JavaLegacyStateGroups.COMMAND_BLOCK));

        // Grass variants
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:grass", ChunkerVanillaBlockType.GRASS_BLOCK)
                        .put("minecraft:mycelium", ChunkerVanillaBlockType.MYCELIUM)
                        .build(),
                JavaLegacyStateGroups.SNOWY));

        // Stairs
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_stairs", ChunkerVanillaBlockType.ACACIA_STAIRS)
                        .put("minecraft:birch_stairs", ChunkerVanillaBlockType.BIRCH_STAIRS)
                        .put("minecraft:brick_stairs", ChunkerVanillaBlockType.BRICK_STAIRS)
                        .put("minecraft:dark_oak_stairs", ChunkerVanillaBlockType.DARK_OAK_STAIRS)
                        .put("minecraft:jungle_stairs", ChunkerVanillaBlockType.JUNGLE_STAIRS)
                        .put("minecraft:nether_brick_stairs", ChunkerVanillaBlockType.NETHER_BRICK_STAIRS)
                        .put("minecraft:oak_stairs", ChunkerVanillaBlockType.OAK_STAIRS)
                        .put("minecraft:quartz_stairs", ChunkerVanillaBlockType.QUARTZ_STAIRS)
                        .put("minecraft:red_sandstone_stairs", ChunkerVanillaBlockType.RED_SANDSTONE_STAIRS)
                        .put("minecraft:sandstone_stairs", ChunkerVanillaBlockType.SANDSTONE_STAIRS)
                        .put("minecraft:spruce_stairs", ChunkerVanillaBlockType.SPRUCE_STAIRS)
                        .put("minecraft:stone_brick_stairs", ChunkerVanillaBlockType.STONE_BRICK_STAIRS)
                        .put("minecraft:stone_stairs", ChunkerVanillaBlockType.COBBLESTONE_STAIRS)
                        .build(),
                JavaLegacyStateGroups.STAIRS));

        // Doors
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:iron_door", ChunkerVanillaBlockType.IRON_DOOR)
                        .put("minecraft:wooden_door", ChunkerVanillaBlockType.OAK_DOOR)
                        .build(),
                JavaLegacyStateGroups.DOOR));

        // New doors, don't have as many valid states
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_door", ChunkerVanillaBlockType.ACACIA_DOOR)
                        .put("minecraft:birch_door", ChunkerVanillaBlockType.BIRCH_DOOR)
                        .put("minecraft:dark_oak_door", ChunkerVanillaBlockType.DARK_OAK_DOOR)
                        .put("minecraft:jungle_door", ChunkerVanillaBlockType.JUNGLE_DOOR)
                        .put("minecraft:spruce_door", ChunkerVanillaBlockType.SPRUCE_DOOR)
                        .build(),
                JavaLegacyStateGroups.DOOR_2));

        // Beds
        register(BlockMapping.of("minecraft:bed", ChunkerVanillaBlockType.RED_BED, JavaLegacyStateGroups.BED));
        registerDuplicateInput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:bed", ChunkerVanillaBlockType.BROWN_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.BLACK_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.CYAN_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.LIGHT_BLUE_BED)
                        .put("minecraft:bed", ChunkerVanillaBlockType.WHITE_BED)
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
                JavaLegacyStateGroups.BED));

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
        register(BlockMapping.flatten("minecraft:log", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(12, ChunkerVanillaBlockType.OAK_WOOD)
                        .put(13, ChunkerVanillaBlockType.SPRUCE_WOOD)
                        .put(14, ChunkerVanillaBlockType.BIRCH_WOOD)
                        .put(15, ChunkerVanillaBlockType.JUNGLE_WOOD)
                        .build(), JavaLegacyStateGroups.WOOD
        ));

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
        register(BlockMapping.flatten("minecraft:log2", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(12, ChunkerVanillaBlockType.ACACIA_WOOD)
                        .put(13, ChunkerVanillaBlockType.DARK_OAK_WOOD)
                        .build(), JavaLegacyStateGroups.WOOD
        ));

        // Panes / Fences
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_fence", ChunkerVanillaBlockType.ACACIA_FENCE)
                        .put("minecraft:birch_fence", ChunkerVanillaBlockType.BIRCH_FENCE)
                        .put("minecraft:dark_oak_fence", ChunkerVanillaBlockType.DARK_OAK_FENCE)
                        .put("minecraft:glass_pane", ChunkerVanillaBlockType.GLASS_PANE)
                        .put("minecraft:iron_bars", ChunkerVanillaBlockType.IRON_BARS)
                        .put("minecraft:jungle_fence", ChunkerVanillaBlockType.JUNGLE_FENCE)
                        .put("minecraft:nether_brick_fence", ChunkerVanillaBlockType.NETHER_BRICK_FENCE)
                        .put("minecraft:fence", ChunkerVanillaBlockType.OAK_FENCE)
                        .put("minecraft:spruce_fence", ChunkerVanillaBlockType.SPRUCE_FENCE)
                        .build(),
                JavaLegacyStateGroups.CONNECTABLE_HORIZONTAL));
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
                JavaLegacyStateGroups.CONNECTABLE_HORIZONTAL));

        // Standing banners
        register(BlockMapping.of("minecraft:standing_banner", ChunkerVanillaBlockType.WHITE_BANNER, JavaLegacyStateGroups.ROTATION));
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
                JavaLegacyStateGroups.ROTATION));

        // Wall banners
        register(BlockMapping.of("minecraft:wall_banner", ChunkerVanillaBlockType.WHITE_WALL_BANNER, JavaLegacyStateGroups.FACING_HORIZONTAL_UNUSUAL));
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
                JavaLegacyStateGroups.FACING_HORIZONTAL_UNUSUAL));


        // Wall Skulls - Converted to block entity data
        register(BlockMapping.of("minecraft:skull", ChunkerVanillaBlockType.SKELETON_WALL_SKULL, JavaLegacyStateGroups.WALL_SKULL));
        registerDuplicateInput(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:skull", ChunkerVanillaBlockType.ZOMBIE_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.PLAYER_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.CREEPER_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.WITHER_SKELETON_WALL_SKULL)
                        .put("minecraft:skull", ChunkerVanillaBlockType.DRAGON_WALL_HEAD)
                        .put("minecraft:skull", ChunkerVanillaBlockType.PIGLIN_WALL_HEAD)
                        .build(),
                JavaLegacyStateGroups.WALL_SKULL));

        // Skulls - Converted to block entity data
        register(BlockMapping.of("minecraft:skull", "data", 1, ChunkerVanillaBlockType.SKELETON_SKULL, JavaLegacyStateGroups.SKULL, VanillaBlockStates.NO_DROP, Bool.FALSE));
        registerDuplicateOutput(BlockMapping.of("minecraft:skull", "data", 0, ChunkerVanillaBlockType.SKELETON_SKULL, JavaLegacyStateGroups.SKULL, VanillaBlockStates.NO_DROP, Bool.FALSE));
        register(BlockMapping.of("minecraft:skull", "data", 9, ChunkerVanillaBlockType.SKELETON_SKULL, JavaLegacyStateGroups.SKULL, VanillaBlockStates.NO_DROP, Bool.TRUE));
        registerDuplicateOutput(BlockMapping.of("minecraft:skull", "data", 8, ChunkerVanillaBlockType.SKELETON_SKULL, JavaLegacyStateGroups.SKULL, VanillaBlockStates.NO_DROP, Bool.TRUE));
        registerDuplicateInput(BlockMapping.flatten("minecraft:skull", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(1, ChunkerVanillaBlockType.PLAYER_HEAD)
                        .put(1, ChunkerVanillaBlockType.DRAGON_HEAD)
                        .put(1, ChunkerVanillaBlockType.PIGLIN_HEAD)
                        .put(1, ChunkerVanillaBlockType.WITHER_SKELETON_SKULL)
                        .put(1, ChunkerVanillaBlockType.ZOMBIE_HEAD)
                        .put(1, ChunkerVanillaBlockType.CREEPER_HEAD)
                        .build(),
                JavaLegacyStateGroups.SKULL, VanillaBlockStates.NO_DROP, Bool.FALSE));
        registerDuplicateInput(BlockMapping.flatten("minecraft:skull", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(9, ChunkerVanillaBlockType.PLAYER_HEAD)
                        .put(9, ChunkerVanillaBlockType.DRAGON_HEAD)
                        .put(9, ChunkerVanillaBlockType.PIGLIN_HEAD)
                        .put(9, ChunkerVanillaBlockType.WITHER_SKELETON_SKULL)
                        .put(9, ChunkerVanillaBlockType.ZOMBIE_HEAD)
                        .put(9, ChunkerVanillaBlockType.CREEPER_HEAD)
                        .build(),
                JavaLegacyStateGroups.SKULL, VanillaBlockStates.NO_DROP, Bool.TRUE));

        // Stone brick
        register(BlockMapping.flatten("minecraft:stonebrick", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.STONE_BRICKS)
                        .put(1, ChunkerVanillaBlockType.MOSSY_STONE_BRICKS)
                        .put(2, ChunkerVanillaBlockType.CRACKED_STONE_BRICKS)
                        .put(3, ChunkerVanillaBlockType.CHISELED_STONE_BRICKS)
                        .build()
        ));
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
                        .build()
        ));

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
                        .put(8, Pair.of(Half.TOP, ChunkerVanillaBlockType.PEONY))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.flatten("minecraft:double_plant", "data", VanillaBlockStates.HALF,
                ImmutableMultimap.<Integer, Pair<Half, ? extends ChunkerBlockType>>builder()
                        .put(9, Pair.of(Half.TOP, ChunkerVanillaBlockType.PEONY))
                        .put(10, Pair.of(Half.TOP, ChunkerVanillaBlockType.PEONY))
                        .put(11, Pair.of(Half.TOP, ChunkerVanillaBlockType.PEONY))
                        .build()
        ));

        // These need to use the same top half
        registerDuplicateInput(BlockMapping.of("minecraft:double_plant", "data", 8, ChunkerVanillaBlockType.SUNFLOWER, VanillaBlockStates.HALF, Half.TOP));
        registerDuplicateInput(BlockMapping.of("minecraft:double_plant", "data", 8, ChunkerVanillaBlockType.TALL_GRASS, VanillaBlockStates.HALF, Half.TOP));
        registerDuplicateInput(BlockMapping.of("minecraft:double_plant", "data", 8, ChunkerVanillaBlockType.LARGE_FERN, VanillaBlockStates.HALF, Half.TOP));
        registerDuplicateInput(BlockMapping.of("minecraft:double_plant", "data", 8, ChunkerVanillaBlockType.ROSE_BUSH, VanillaBlockStates.HALF, Half.TOP));
        registerDuplicateInput(BlockMapping.of("minecraft:double_plant", "data", 8, ChunkerVanillaBlockType.LILAC, VanillaBlockStates.HALF, Half.TOP));

        // Leaves
        register(BlockMapping.flatten("minecraft:leaves", "data", List.of(VanillaBlockStates.UPDATE, VanillaBlockStates.PERSISTENT),
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
                JavaLegacyStateGroups.LEAVES
        ));
        register(BlockMapping.flatten("minecraft:leaves2", "data", List.of(VanillaBlockStates.UPDATE, VanillaBlockStates.PERSISTENT),
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
                JavaLegacyStateGroups.LEAVES
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
        registerDuplicateOutput(BlockMapping.of("minecraft:wooden_slab", ChunkerVanillaBlockType.OAK_SLAB, VanillaBlockStates.HALF, Half.BOTTOM));

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
        registerDuplicateOutput(BlockMapping.of("minecraft:double_wooden_slab", ChunkerVanillaBlockType.OAK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        // Stone slabs
        register(BlockMapping.flatten("minecraft:stone_slab", "data", VanillaBlockStates.SLAB_TYPE,
                ImmutableMultimap.<Integer, Pair<SlabType, ? extends ChunkerBlockType>>builder()
                        .put(0, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.SMOOTH_STONE_SLAB))
                        .put(1, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.SANDSTONE_SLAB))
                        .put(3, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.COBBLESTONE_SLAB))
                        .put(4, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.BRICK_SLAB))
                        .put(5, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.STONE_BRICK_SLAB))
                        .put(6, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.NETHER_BRICK_SLAB))
                        .put(7, Pair.of(SlabType.BOTTOM, ChunkerVanillaBlockType.QUARTZ_SLAB))
                        .put(8, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.SMOOTH_STONE_SLAB))
                        .put(9, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.SANDSTONE_SLAB))
                        .put(11, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.COBBLESTONE_SLAB))
                        .put(12, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.BRICK_SLAB))
                        .put(13, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.STONE_BRICK_SLAB))
                        .put(14, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.NETHER_BRICK_SLAB))
                        .put(15, Pair.of(SlabType.TOP, ChunkerVanillaBlockType.QUARTZ_SLAB))
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:stone_slab", ChunkerVanillaBlockType.SMOOTH_STONE_SLAB, VanillaBlockStates.HALF, Half.BOTTOM));

        register(BlockMapping.flatten("minecraft:double_stone_slab", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.SMOOTH_STONE_SLAB)
                        .put(1, ChunkerVanillaBlockType.SANDSTONE_SLAB)
                        .put(3, ChunkerVanillaBlockType.COBBLESTONE_SLAB)
                        .put(4, ChunkerVanillaBlockType.BRICK_SLAB)
                        .put(5, ChunkerVanillaBlockType.STONE_BRICK_SLAB)
                        .put(6, ChunkerVanillaBlockType.NETHER_BRICK_SLAB)
                        .put(7, ChunkerVanillaBlockType.QUARTZ_SLAB)
                        .build(),
                VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.of("minecraft:double_stone_slab", ChunkerVanillaBlockType.SMOOTH_STONE_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        // Mapping for petrified_oak_slab
        register(BlockMapping.of("minecraft:stone_slab", "data", 2, ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.BOTTOM));
        register(BlockMapping.of("minecraft:stone_slab", "data", 10, ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.TOP));
        register(BlockMapping.of("minecraft:double_stone_slab", "data", 2, ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        // Duplicate double slabs
        registerDuplicateOutput(BlockMapping.of("minecraft:double_stone_slab", "data", 10, ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.of("minecraft:double_stone_slab", "data", 11, ChunkerVanillaBlockType.COBBLESTONE_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.of("minecraft:double_stone_slab", "data", 12, ChunkerVanillaBlockType.BRICK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.of("minecraft:double_stone_slab", "data", 13, ChunkerVanillaBlockType.STONE_BRICK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        registerDuplicateOutput(BlockMapping.of("minecraft:double_stone_slab", "data", 14, ChunkerVanillaBlockType.NETHER_BRICK_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        // Extra double slab types
        register(BlockMapping.of("minecraft:double_stone_slab", "data", 9, ChunkerVanillaBlockType.SMOOTH_SANDSTONE));
        register(BlockMapping.flatten("minecraft:double_stone_slab", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(8, ChunkerVanillaBlockType.SMOOTH_STONE)
                        .put(15, ChunkerVanillaBlockType.SMOOTH_QUARTZ)
                        .build()
        ));

        register(BlockMapping.of("minecraft:stone_slab2", ChunkerVanillaBlockType.RED_SANDSTONE_SLAB, JavaLegacyStateGroups.SLAB_HALF));
        register(BlockMapping.of("minecraft:double_stone_slab2", "data", 0, ChunkerVanillaBlockType.RED_SANDSTONE_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
        register(BlockMapping.of("minecraft:double_stone_slab2", "data", 8, ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE));
        registerDuplicateInput(BlockMapping.of("minecraft:double_stone_slab2", "data", 8, ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));

        // Stone
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

        // Redstone lamp
        register(BlockMapping.of("minecraft:lit_redstone_lamp", ChunkerVanillaBlockType.REDSTONE_LAMP, VanillaBlockStates.LIT, Bool.TRUE));
        register(BlockMapping.of("minecraft:redstone_lamp", ChunkerVanillaBlockType.REDSTONE_LAMP, VanillaBlockStates.LIT, Bool.FALSE));

        // Redstone ore
        register(BlockMapping.of("minecraft:lit_redstone_ore", ChunkerVanillaBlockType.REDSTONE_ORE, VanillaBlockStates.LIT, Bool.TRUE));
        register(BlockMapping.of("minecraft:redstone_ore", ChunkerVanillaBlockType.REDSTONE_ORE, VanillaBlockStates.LIT, Bool.FALSE));

        // Redstone torch (normal/wall)
        register(BlockMapping.of("minecraft:unlit_redstone_torch", "data", 5, ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.FALSE));
        register(BlockMapping.of("minecraft:unlit_redstone_torch", ChunkerVanillaBlockType.REDSTONE_WALL_TORCH, JavaLegacyStateGroups.TORCH_DIRECTION, VanillaBlockStates.LIT, Bool.FALSE));

        // Lit redstone torch
        register(BlockMapping.of("minecraft:redstone_torch", "data", 5, ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.TRUE));
        register(BlockMapping.of("minecraft:redstone_torch", ChunkerVanillaBlockType.REDSTONE_WALL_TORCH, JavaLegacyStateGroups.TORCH_DIRECTION, VanillaBlockStates.LIT, Bool.TRUE));

        register(BlockMapping.of("minecraft:lit_furnace", ChunkerVanillaBlockType.FURNACE, JavaLegacyStateGroups.FACING_HORIZONTAL_UNUSUAL, VanillaBlockStates.LIT, Bool.TRUE));
        register(BlockMapping.of("minecraft:furnace", ChunkerVanillaBlockType.FURNACE, JavaLegacyStateGroups.FACING_HORIZONTAL_UNUSUAL, VanillaBlockStates.LIT, Bool.FALSE));

        // Comparators
        register(BlockMapping.of("minecraft:unpowered_comparator", ChunkerVanillaBlockType.COMPARATOR, JavaLegacyStateGroups.UNPOWERED_COMPARATOR));
        registerDuplicateOutput(BlockMapping.of("minecraft:powered_comparator", ChunkerVanillaBlockType.COMPARATOR, JavaLegacyStateGroups.POWERED_COMPARATOR));

        // Repeater
        register(BlockMapping.of("minecraft:powered_repeater", ChunkerVanillaBlockType.REPEATER, JavaLegacyStateGroups.REPEATER, VanillaBlockStates.POWERED, Bool.TRUE));
        register(BlockMapping.of("minecraft:unpowered_repeater", ChunkerVanillaBlockType.REPEATER, JavaLegacyStateGroups.REPEATER, VanillaBlockStates.POWERED, Bool.FALSE));

        // Daylight detector
        register(BlockMapping.of("minecraft:daylight_detector_inverted", ChunkerVanillaBlockType.DAYLIGHT_DETECTOR, JavaLegacyStateGroups.POWER, VanillaBlockStates.INVERTED, Bool.TRUE));
        register(BlockMapping.of("minecraft:daylight_detector", ChunkerVanillaBlockType.DAYLIGHT_DETECTOR, JavaLegacyStateGroups.POWER, VanillaBlockStates.INVERTED, Bool.FALSE));

        // Liquids
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:water", ChunkerVanillaBlockType.WATER)
                        .put("minecraft:lava", ChunkerVanillaBlockType.LAVA)
                        .build(),
                JavaLegacyStateGroups.LIQUID, VanillaBlockStates.FLOWING, Bool.FALSE));

        // Flowing liquids should use the flowing state
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:flowing_water", ChunkerVanillaBlockType.WATER)
                        .put("minecraft:flowing_lava", ChunkerVanillaBlockType.LAVA)
                        .build(),
                JavaLegacyStateGroups.LIQUID, VanillaBlockStates.FLOWING, Bool.TRUE));

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
        // Sandstone
        register(BlockMapping.flatten("minecraft:sandstone", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.SANDSTONE)
                        .put(1, ChunkerVanillaBlockType.CHISELED_SANDSTONE)
                        .put(2, ChunkerVanillaBlockType.CUT_SANDSTONE)
                        .build()
        ));
        registerDuplicateOutput(BlockMapping.of("minecraft:sandstone", ChunkerVanillaBlockType.SANDSTONE));

        // Red Sandstone
        register(BlockMapping.flatten("minecraft:red_sandstone", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.RED_SANDSTONE)
                        .put(1, ChunkerVanillaBlockType.CHISELED_RED_SANDSTONE)
                        .put(2, ChunkerVanillaBlockType.CUT_RED_SANDSTONE)
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

        // Quartz Blocks
        register(BlockMapping.of("minecraft:quartz_block", "data", 1, ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));
        register(BlockMapping.of("minecraft:quartz_block", "data", 2, ChunkerVanillaBlockType.QUARTZ_PILLAR, VanillaBlockStates.AXIS, Axis.Y));
        register(BlockMapping.of("minecraft:quartz_block", "data", 3, ChunkerVanillaBlockType.QUARTZ_PILLAR, VanillaBlockStates.AXIS, Axis.X));
        register(BlockMapping.of("minecraft:quartz_block", "data", 4, ChunkerVanillaBlockType.QUARTZ_PILLAR, VanillaBlockStates.AXIS, Axis.Z));
        register(BlockMapping.of("minecraft:quartz_block", ChunkerVanillaBlockType.QUARTZ_BLOCK));

        // Tall grass (two types which look identical to other blocks)
        registerDuplicateOutput(BlockMapping.of("minecraft:tallgrass", "data", 0, ChunkerVanillaBlockType.DEAD_BUSH));
        register(BlockMapping.of("minecraft:tallgrass", "data", 1, ChunkerVanillaBlockType.SHORT_GRASS));
        register(BlockMapping.of("minecraft:tallgrass", "data", 2, ChunkerVanillaBlockType.FERN));

        // Dirt
        register(BlockMapping.flatten("minecraft:dirt", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.DIRT)
                        .put(1, ChunkerVanillaBlockType.COARSE_DIRT)
                        .build()
        ));
        register(BlockMapping.of("minecraft:dirt", "data", 2, ChunkerVanillaBlockType.PODZOL, JavaLegacyStateGroups.SNOWY));

        // Torch (normal/wall)
        register(BlockMapping.of("minecraft:torch", "data", 5, ChunkerVanillaBlockType.TORCH));
        register(BlockMapping.of("minecraft:torch", ChunkerVanillaBlockType.WALL_TORCH, JavaLegacyStateGroups.TORCH_DIRECTION));

        // Walls
        register(BlockMapping.flatten("minecraft:cobblestone_wall", "data",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(0, ChunkerVanillaBlockType.COBBLESTONE_WALL)
                        .put(1, ChunkerVanillaBlockType.MOSSY_COBBLESTONE_WALL)
                        .build(),
                JavaLegacyStateGroups.WALL));

        // Mushroom / Mushroom Stem
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:brown_mushroom_block", ChunkerVanillaBlockType.BROWN_MUSHROOM_BLOCK)
                        .put("minecraft:red_mushroom_block", ChunkerVanillaBlockType.RED_MUSHROOM_BLOCK)
                        .build(),
                JavaLegacyStateGroups.MUSHROOM_BLOCK));
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

        // Fallback to a normal mushroom block since stem doesn't exist in this version
        registerDuplicateInput(BlockMapping.of("minecraft:brown_mushroom_block", "data", 15, ChunkerVanillaBlockType.MUSHROOM_STEM));

        // 1.9
        if (version.isGreaterThanOrEqual(1, 9, 0)) {
            register(BlockMapping.of("minecraft:chorus_flower", ChunkerVanillaBlockType.CHORUS_FLOWER, JavaLegacyStateGroups.AGE_5));
            register(BlockMapping.of("minecraft:chorus_plant", ChunkerVanillaBlockType.CHORUS_PLANT, JavaLegacyStateGroups.CONNECTABLE));
            register(BlockMapping.of("minecraft:beetroots", ChunkerVanillaBlockType.BEETROOTS, JavaLegacyStateGroups.AGE_3_TO_7));
            register(BlockMapping.of("minecraft:end_gateway", ChunkerVanillaBlockType.END_GATEWAY));
            register(BlockMapping.of("minecraft:end_bricks", ChunkerVanillaBlockType.END_STONE_BRICKS));
            register(BlockMapping.of("minecraft:end_rod", ChunkerVanillaBlockType.END_ROD, JavaLegacyStateGroups.FACING_ALL));
            register(BlockMapping.of("minecraft:frosted_ice", ChunkerVanillaBlockType.FROSTED_ICE, JavaLegacyStateGroups.AGE_3));
            register(BlockMapping.of("minecraft:grass_path", ChunkerVanillaBlockType.DIRT_PATH));
            register(BlockMapping.of("minecraft:structure_block", ChunkerVanillaBlockType.STRUCTURE_BLOCK, JavaLegacyStateGroups.STRUCTURE_BLOCK));

            register(BlockMapping.of("minecraft:purpur_pillar", ChunkerVanillaBlockType.PURPUR_PILLAR, JavaLegacyStateGroups.AXIS));
            register(BlockMapping.of("minecraft:purpur_stairs", ChunkerVanillaBlockType.PURPUR_STAIRS, JavaLegacyStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:purpur_block", ChunkerVanillaBlockType.PURPUR_BLOCK));
            register(BlockMapping.of("minecraft:purpur_slab", ChunkerVanillaBlockType.PURPUR_SLAB, JavaLegacyStateGroups.SLAB_HALF));
            register(BlockMapping.of("minecraft:purpur_double_slab", ChunkerVanillaBlockType.PURPUR_SLAB, VanillaBlockStates.SLAB_TYPE, SlabType.DOUBLE));
            register(BlockMapping.of("minecraft:chain_command_block", ChunkerVanillaBlockType.CHAIN_COMMAND_BLOCK, JavaLegacyStateGroups.COMMAND_BLOCK));
            register(BlockMapping.of("minecraft:repeating_command_block", ChunkerVanillaBlockType.REPEATING_COMMAND_BLOCK, JavaLegacyStateGroups.COMMAND_BLOCK));
        }

        // 1.10
        if (version.isGreaterThanOrEqual(1, 10, 0)) {
            register(BlockMapping.of("minecraft:structure_void", ChunkerVanillaBlockType.STRUCTURE_VOID, JavaLegacyStateGroups.STRUCTURE_VOID));
            register(BlockMapping.of("minecraft:magma", ChunkerVanillaBlockType.MAGMA_BLOCK));
            register(BlockMapping.of("minecraft:nether_wart_block", ChunkerVanillaBlockType.NETHER_WART_BLOCK));
            register(BlockMapping.of("minecraft:red_nether_brick", ChunkerVanillaBlockType.RED_NETHER_BRICKS));
            register(BlockMapping.of("minecraft:bone_block", ChunkerVanillaBlockType.BONE_BLOCK, JavaLegacyStateGroups.AXIS));
        }

        // 1.11
        if (version.isGreaterThanOrEqual(1, 11, 0)) {
            register(BlockMapping.of("minecraft:observer", ChunkerVanillaBlockType.OBSERVER, JavaLegacyStateGroups.FACING_POWERED));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:black_shulker_box", ChunkerVanillaBlockType.BLACK_SHULKER_BOX)
                            .put("minecraft:blue_shulker_box", ChunkerVanillaBlockType.BLUE_SHULKER_BOX)
                            .put("minecraft:brown_shulker_box", ChunkerVanillaBlockType.BROWN_SHULKER_BOX)
                            .put("minecraft:cyan_shulker_box", ChunkerVanillaBlockType.CYAN_SHULKER_BOX)
                            .put("minecraft:gray_shulker_box", ChunkerVanillaBlockType.GRAY_SHULKER_BOX)
                            .put("minecraft:green_shulker_box", ChunkerVanillaBlockType.GREEN_SHULKER_BOX)
                            .put("minecraft:light_blue_shulker_box", ChunkerVanillaBlockType.LIGHT_BLUE_SHULKER_BOX)
                            .put("minecraft:silver_shulker_box", ChunkerVanillaBlockType.LIGHT_GRAY_SHULKER_BOX)
                            .put("minecraft:lime_shulker_box", ChunkerVanillaBlockType.LIME_SHULKER_BOX)
                            .put("minecraft:magenta_shulker_box", ChunkerVanillaBlockType.MAGENTA_SHULKER_BOX)
                            .put("minecraft:orange_shulker_box", ChunkerVanillaBlockType.ORANGE_SHULKER_BOX)
                            .put("minecraft:pink_shulker_box", ChunkerVanillaBlockType.PINK_SHULKER_BOX)
                            .put("minecraft:purple_shulker_box", ChunkerVanillaBlockType.PURPLE_SHULKER_BOX)
                            .put("minecraft:red_shulker_box", ChunkerVanillaBlockType.RED_SHULKER_BOX)
                            .put("minecraft:white_shulker_box", ChunkerVanillaBlockType.WHITE_SHULKER_BOX)
                            .put("minecraft:yellow_shulker_box", ChunkerVanillaBlockType.YELLOW_SHULKER_BOX)
                            .build(),
                    JavaLegacyStateGroups.FACING_ALL));
        }

        // 1.12
        if (version.isGreaterThanOrEqual(1, 12, 0)) {
            // Concrete Powder
            register(BlockMapping.flatten("minecraft:concrete_powder", "data",
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

            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:black_glazed_terracotta", ChunkerVanillaBlockType.BLACK_GLAZED_TERRACOTTA)
                            .put("minecraft:blue_glazed_terracotta", ChunkerVanillaBlockType.BLUE_GLAZED_TERRACOTTA)
                            .put("minecraft:brown_glazed_terracotta", ChunkerVanillaBlockType.BROWN_GLAZED_TERRACOTTA)
                            .put("minecraft:cyan_glazed_terracotta", ChunkerVanillaBlockType.CYAN_GLAZED_TERRACOTTA)
                            .put("minecraft:gray_glazed_terracotta", ChunkerVanillaBlockType.GRAY_GLAZED_TERRACOTTA)
                            .put("minecraft:green_glazed_terracotta", ChunkerVanillaBlockType.GREEN_GLAZED_TERRACOTTA)
                            .put("minecraft:light_blue_glazed_terracotta", ChunkerVanillaBlockType.LIGHT_BLUE_GLAZED_TERRACOTTA)
                            .put("minecraft:silver_glazed_terracotta", ChunkerVanillaBlockType.LIGHT_GRAY_GLAZED_TERRACOTTA)
                            .put("minecraft:lime_glazed_terracotta", ChunkerVanillaBlockType.LIME_GLAZED_TERRACOTTA)
                            .put("minecraft:magenta_glazed_terracotta", ChunkerVanillaBlockType.MAGENTA_GLAZED_TERRACOTTA)
                            .put("minecraft:orange_glazed_terracotta", ChunkerVanillaBlockType.ORANGE_GLAZED_TERRACOTTA)
                            .put("minecraft:pink_glazed_terracotta", ChunkerVanillaBlockType.PINK_GLAZED_TERRACOTTA)
                            .put("minecraft:purple_glazed_terracotta", ChunkerVanillaBlockType.PURPLE_GLAZED_TERRACOTTA)
                            .put("minecraft:red_glazed_terracotta", ChunkerVanillaBlockType.RED_GLAZED_TERRACOTTA)
                            .put("minecraft:white_glazed_terracotta", ChunkerVanillaBlockType.WHITE_GLAZED_TERRACOTTA)
                            .put("minecraft:yellow_glazed_terracotta", ChunkerVanillaBlockType.YELLOW_GLAZED_TERRACOTTA)
                            .build(),
                    JavaLegacyStateGroups.FACING_HORIZONTAL_SWNE));
        }
    }
}
