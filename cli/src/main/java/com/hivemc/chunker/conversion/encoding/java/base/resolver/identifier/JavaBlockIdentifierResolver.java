package com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier;

import com.google.common.collect.ImmutableMultimap;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.BlockMapping;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.ChunkerBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.state.StateMappingGroup;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;

/**
 * Resolver to convert between Java block identifiers and ChunkerBlockIdentifier.
 */
public class JavaBlockIdentifierResolver extends ChunkerBlockIdentifierResolver {
    /**
     * The default group which assumes waterlogging is false.
     */
    public static final StateMappingGroup DEFAULT_WATERLOGGED_FALSE = new StateMappingGroup.Builder()
            .defaultOutput(VanillaBlockStates.WATERLOGGED, Bool.FALSE)
            .build();

    /**
     * Create a new java block identifier resolver.
     *
     * @param converter                the converter instance.
     * @param version                  the version being resolved.
     * @param reader                   whether this is used for the reader.
     * @param customIdentifiersAllowed whether unknown identifiers should be converted to custom identifiers.
     */
    public JavaBlockIdentifierResolver(Converter converter, Version version, boolean reader, boolean customIdentifiersAllowed) {
        super(converter, version, reader, customIdentifiersAllowed);
    }

    @Override
    public void registerMappings(Version version) {
        // Waterlogging on Java is block specific, so we assume waterlogging isn't present when the state isn't present
        extraStateMappingGroup(DEFAULT_WATERLOGGED_FALSE);

        // Mappings
        register(BlockMapping.of("minecraft:acacia_planks", ChunkerVanillaBlockType.ACACIA_PLANKS));
        register(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.AIR));
        register(BlockMapping.of("minecraft:allium", ChunkerVanillaBlockType.ALLIUM));
        register(BlockMapping.of("minecraft:andesite", ChunkerVanillaBlockType.ANDESITE));
        register(BlockMapping.of("minecraft:azure_bluet", ChunkerVanillaBlockType.AZURE_BLUET));
        register(BlockMapping.of("minecraft:beacon", ChunkerVanillaBlockType.BEACON));
        register(BlockMapping.of("minecraft:bedrock", ChunkerVanillaBlockType.BEDROCK, JavaStateGroups.BEDROCK));
        register(BlockMapping.of("minecraft:birch_planks", ChunkerVanillaBlockType.BIRCH_PLANKS));
        register(BlockMapping.of("minecraft:black_carpet", ChunkerVanillaBlockType.BLACK_CARPET));
        register(BlockMapping.of("minecraft:black_concrete", ChunkerVanillaBlockType.BLACK_CONCRETE));
        register(BlockMapping.of("minecraft:black_concrete_powder", ChunkerVanillaBlockType.BLACK_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:black_stained_glass", ChunkerVanillaBlockType.BLACK_STAINED_GLASS));
        register(BlockMapping.of("minecraft:black_terracotta", ChunkerVanillaBlockType.BLACK_TERRACOTTA));
        register(BlockMapping.of("minecraft:black_wool", ChunkerVanillaBlockType.BLACK_WOOL));
        register(BlockMapping.of("minecraft:blue_carpet", ChunkerVanillaBlockType.BLUE_CARPET));
        register(BlockMapping.of("minecraft:blue_concrete", ChunkerVanillaBlockType.BLUE_CONCRETE));
        register(BlockMapping.of("minecraft:blue_concrete_powder", ChunkerVanillaBlockType.BLUE_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:blue_ice", ChunkerVanillaBlockType.BLUE_ICE));
        register(BlockMapping.of("minecraft:blue_orchid", ChunkerVanillaBlockType.BLUE_ORCHID));
        register(BlockMapping.of("minecraft:blue_stained_glass", ChunkerVanillaBlockType.BLUE_STAINED_GLASS));
        register(BlockMapping.of("minecraft:blue_terracotta", ChunkerVanillaBlockType.BLUE_TERRACOTTA));
        register(BlockMapping.of("minecraft:blue_wool", ChunkerVanillaBlockType.BLUE_WOOL));
        register(BlockMapping.of("minecraft:bookshelf", ChunkerVanillaBlockType.BOOKSHELF));
        register(BlockMapping.of("minecraft:brain_coral_block", ChunkerVanillaBlockType.BRAIN_CORAL_BLOCK));
        register(BlockMapping.of("minecraft:brewing_stand", ChunkerVanillaBlockType.BREWING_STAND, JavaStateGroups.BREWING_STAND));
        register(BlockMapping.of("minecraft:bricks", ChunkerVanillaBlockType.BRICKS));
        register(BlockMapping.of("minecraft:brown_carpet", ChunkerVanillaBlockType.BROWN_CARPET));
        register(BlockMapping.of("minecraft:brown_concrete", ChunkerVanillaBlockType.BROWN_CONCRETE));
        register(BlockMapping.of("minecraft:brown_concrete_powder", ChunkerVanillaBlockType.BROWN_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:brown_mushroom", ChunkerVanillaBlockType.BROWN_MUSHROOM));
        register(BlockMapping.of("minecraft:brown_stained_glass", ChunkerVanillaBlockType.BROWN_STAINED_GLASS));
        register(BlockMapping.of("minecraft:brown_terracotta", ChunkerVanillaBlockType.BROWN_TERRACOTTA));
        register(BlockMapping.of("minecraft:brown_wool", ChunkerVanillaBlockType.BROWN_WOOL));
        register(BlockMapping.of("minecraft:bubble_column", ChunkerVanillaBlockType.BUBBLE_COLUMN, JavaStateGroups.BUBBLE_COLUMN));
        register(BlockMapping.of("minecraft:bubble_coral_block", ChunkerVanillaBlockType.BUBBLE_CORAL_BLOCK));
        register(BlockMapping.of("minecraft:cake", ChunkerVanillaBlockType.CAKE, JavaStateGroups.CAKE));
        register(BlockMapping.of("minecraft:cave_air", ChunkerVanillaBlockType.CAVE_AIR));
        register(BlockMapping.of("minecraft:chiseled_quartz_block", ChunkerVanillaBlockType.CHISELED_QUARTZ_BLOCK));
        register(BlockMapping.of("minecraft:chiseled_red_sandstone", ChunkerVanillaBlockType.CHISELED_RED_SANDSTONE));
        register(BlockMapping.of("minecraft:chiseled_sandstone", ChunkerVanillaBlockType.CHISELED_SANDSTONE));
        register(BlockMapping.of("minecraft:chiseled_stone_bricks", ChunkerVanillaBlockType.CHISELED_STONE_BRICKS));
        register(BlockMapping.of("minecraft:chorus_flower", ChunkerVanillaBlockType.CHORUS_FLOWER, JavaStateGroups.CHORUS_FLOWER));
        register(BlockMapping.of("minecraft:clay", ChunkerVanillaBlockType.CLAY));
        register(BlockMapping.of("minecraft:coal_block", ChunkerVanillaBlockType.COAL_BLOCK));
        register(BlockMapping.of("minecraft:coal_ore", ChunkerVanillaBlockType.COAL_ORE));
        register(BlockMapping.of("minecraft:coarse_dirt", ChunkerVanillaBlockType.COARSE_DIRT));
        register(BlockMapping.of("minecraft:cobblestone", ChunkerVanillaBlockType.COBBLESTONE));
        register(BlockMapping.of("minecraft:cobweb", ChunkerVanillaBlockType.COBWEB));
        register(BlockMapping.of("minecraft:cocoa", ChunkerVanillaBlockType.COCOA, JavaStateGroups.COCOA));
        register(BlockMapping.of("minecraft:comparator", ChunkerVanillaBlockType.COMPARATOR, JavaStateGroups.COMPARATOR));
        register(BlockMapping.of("minecraft:cracked_stone_bricks", ChunkerVanillaBlockType.CRACKED_STONE_BRICKS));
        register(BlockMapping.of("minecraft:crafting_table", ChunkerVanillaBlockType.CRAFTING_TABLE));
        register(BlockMapping.of("minecraft:cut_red_sandstone", ChunkerVanillaBlockType.CUT_RED_SANDSTONE));
        register(BlockMapping.of("minecraft:cut_sandstone", ChunkerVanillaBlockType.CUT_SANDSTONE));
        register(BlockMapping.of("minecraft:cyan_carpet", ChunkerVanillaBlockType.CYAN_CARPET));
        register(BlockMapping.of("minecraft:cyan_concrete", ChunkerVanillaBlockType.CYAN_CONCRETE));
        register(BlockMapping.of("minecraft:cyan_concrete_powder", ChunkerVanillaBlockType.CYAN_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:cyan_stained_glass", ChunkerVanillaBlockType.CYAN_STAINED_GLASS));
        register(BlockMapping.of("minecraft:cyan_terracotta", ChunkerVanillaBlockType.CYAN_TERRACOTTA));
        register(BlockMapping.of("minecraft:cyan_wool", ChunkerVanillaBlockType.CYAN_WOOL));
        register(BlockMapping.of("minecraft:dandelion", ChunkerVanillaBlockType.DANDELION));
        register(BlockMapping.of("minecraft:dark_oak_planks", ChunkerVanillaBlockType.DARK_OAK_PLANKS));
        register(BlockMapping.of("minecraft:dark_prismarine", ChunkerVanillaBlockType.DARK_PRISMARINE));
        register(BlockMapping.of("minecraft:daylight_detector", ChunkerVanillaBlockType.DAYLIGHT_DETECTOR, JavaStateGroups.DAYLIGHT_DETECTOR));
        register(BlockMapping.of("minecraft:dead_brain_coral_block", ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_BLOCK));
        register(BlockMapping.of("minecraft:dead_bubble_coral_block", ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_BLOCK));
        register(BlockMapping.of("minecraft:dead_bush", ChunkerVanillaBlockType.DEAD_BUSH));
        register(BlockMapping.of("minecraft:dead_fire_coral_block", ChunkerVanillaBlockType.DEAD_FIRE_CORAL_BLOCK));
        register(BlockMapping.of("minecraft:dead_horn_coral_block", ChunkerVanillaBlockType.DEAD_HORN_CORAL_BLOCK));
        register(BlockMapping.of("minecraft:dead_tube_coral_block", ChunkerVanillaBlockType.DEAD_TUBE_CORAL_BLOCK));
        register(BlockMapping.of("minecraft:diamond_block", ChunkerVanillaBlockType.DIAMOND_BLOCK));
        register(BlockMapping.of("minecraft:diamond_ore", ChunkerVanillaBlockType.DIAMOND_ORE));
        register(BlockMapping.of("minecraft:diorite", ChunkerVanillaBlockType.DIORITE));
        register(BlockMapping.of("minecraft:dirt", ChunkerVanillaBlockType.DIRT));
        register(BlockMapping.of("minecraft:dragon_egg", ChunkerVanillaBlockType.DRAGON_EGG));
        register(BlockMapping.of("minecraft:dried_kelp_block", ChunkerVanillaBlockType.DRIED_KELP_BLOCK));
        register(BlockMapping.of("minecraft:emerald_block", ChunkerVanillaBlockType.EMERALD_BLOCK));
        register(BlockMapping.of("minecraft:emerald_ore", ChunkerVanillaBlockType.EMERALD_ORE));
        register(BlockMapping.of("minecraft:enchanting_table", ChunkerVanillaBlockType.ENCHANTING_TABLE));
        register(BlockMapping.of("minecraft:end_gateway", ChunkerVanillaBlockType.END_GATEWAY));
        register(BlockMapping.of("minecraft:end_portal", ChunkerVanillaBlockType.END_PORTAL));
        register(BlockMapping.of("minecraft:end_portal_frame", ChunkerVanillaBlockType.END_PORTAL_FRAME, JavaStateGroups.END_PORTAL_FRAME));
        register(BlockMapping.of("minecraft:end_stone", ChunkerVanillaBlockType.END_STONE));
        register(BlockMapping.of("minecraft:end_stone_bricks", ChunkerVanillaBlockType.END_STONE_BRICKS));
        register(BlockMapping.of("minecraft:farmland", ChunkerVanillaBlockType.FARMLAND, JavaStateGroups.FARMLAND));
        register(BlockMapping.of("minecraft:fern", ChunkerVanillaBlockType.FERN));
        register(BlockMapping.of("minecraft:fire", ChunkerVanillaBlockType.FIRE, JavaStateGroups.FIRE));
        register(BlockMapping.of("minecraft:fire_coral_block", ChunkerVanillaBlockType.FIRE_CORAL_BLOCK));
        register(BlockMapping.of("minecraft:flower_pot", ChunkerVanillaBlockType.FLOWER_POT));
        register(BlockMapping.of("minecraft:glass", ChunkerVanillaBlockType.GLASS));
        register(BlockMapping.of("minecraft:glowstone", ChunkerVanillaBlockType.GLOWSTONE));
        register(BlockMapping.of("minecraft:gold_block", ChunkerVanillaBlockType.GOLD_BLOCK));
        register(BlockMapping.of("minecraft:gold_ore", ChunkerVanillaBlockType.GOLD_ORE));
        register(BlockMapping.of("minecraft:granite", ChunkerVanillaBlockType.GRANITE));
        register(BlockMapping.of("minecraft:grass", ChunkerVanillaBlockType.SHORT_GRASS));
        register(BlockMapping.of("minecraft:grass_path", ChunkerVanillaBlockType.DIRT_PATH));
        register(BlockMapping.of("minecraft:gravel", ChunkerVanillaBlockType.GRAVEL));
        register(BlockMapping.of("minecraft:gray_carpet", ChunkerVanillaBlockType.GRAY_CARPET));
        register(BlockMapping.of("minecraft:gray_concrete", ChunkerVanillaBlockType.GRAY_CONCRETE));
        register(BlockMapping.of("minecraft:gray_concrete_powder", ChunkerVanillaBlockType.GRAY_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:gray_stained_glass", ChunkerVanillaBlockType.GRAY_STAINED_GLASS));
        register(BlockMapping.of("minecraft:gray_terracotta", ChunkerVanillaBlockType.GRAY_TERRACOTTA));
        register(BlockMapping.of("minecraft:gray_wool", ChunkerVanillaBlockType.GRAY_WOOL));
        register(BlockMapping.of("minecraft:green_carpet", ChunkerVanillaBlockType.GREEN_CARPET));
        register(BlockMapping.of("minecraft:green_concrete", ChunkerVanillaBlockType.GREEN_CONCRETE));
        register(BlockMapping.of("minecraft:green_concrete_powder", ChunkerVanillaBlockType.GREEN_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:green_stained_glass", ChunkerVanillaBlockType.GREEN_STAINED_GLASS));
        register(BlockMapping.of("minecraft:green_terracotta", ChunkerVanillaBlockType.GREEN_TERRACOTTA));
        register(BlockMapping.of("minecraft:green_wool", ChunkerVanillaBlockType.GREEN_WOOL));
        register(BlockMapping.of("minecraft:hopper", ChunkerVanillaBlockType.HOPPER, JavaStateGroups.HOPPER));
        register(BlockMapping.of("minecraft:horn_coral_block", ChunkerVanillaBlockType.HORN_CORAL_BLOCK));
        register(BlockMapping.of("minecraft:ice", ChunkerVanillaBlockType.ICE));
        register(BlockMapping.of("minecraft:infested_chiseled_stone_bricks", ChunkerVanillaBlockType.INFESTED_CHISELED_STONE_BRICKS));
        register(BlockMapping.of("minecraft:infested_cobblestone", ChunkerVanillaBlockType.INFESTED_COBBLESTONE));
        register(BlockMapping.of("minecraft:infested_cracked_stone_bricks", ChunkerVanillaBlockType.INFESTED_CRACKED_STONE_BRICKS));
        register(BlockMapping.of("minecraft:infested_mossy_stone_bricks", ChunkerVanillaBlockType.INFESTED_MOSSY_STONE_BRICKS));
        register(BlockMapping.of("minecraft:infested_stone", ChunkerVanillaBlockType.INFESTED_STONE));
        register(BlockMapping.of("minecraft:infested_stone_bricks", ChunkerVanillaBlockType.INFESTED_STONE_BRICKS));
        register(BlockMapping.of("minecraft:iron_block", ChunkerVanillaBlockType.IRON_BLOCK));
        register(BlockMapping.of("minecraft:iron_ore", ChunkerVanillaBlockType.IRON_ORE));
        register(BlockMapping.of("minecraft:jukebox", ChunkerVanillaBlockType.JUKEBOX, JavaStateGroups.JUKEBOX));
        register(BlockMapping.of("minecraft:jungle_planks", ChunkerVanillaBlockType.JUNGLE_PLANKS));
        register(BlockMapping.of("minecraft:kelp", ChunkerVanillaBlockType.KELP, JavaStateGroups.KELP));
        register(BlockMapping.of("minecraft:kelp_plant", ChunkerVanillaBlockType.KELP_PLANT, JavaStateGroups.WATERLOGGED_DEFAULT_TRUE));
        register(BlockMapping.of("minecraft:lapis_block", ChunkerVanillaBlockType.LAPIS_BLOCK));
        register(BlockMapping.of("minecraft:lapis_ore", ChunkerVanillaBlockType.LAPIS_ORE));
        register(BlockMapping.of("minecraft:light_blue_carpet", ChunkerVanillaBlockType.LIGHT_BLUE_CARPET));
        register(BlockMapping.of("minecraft:light_blue_concrete", ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE));
        register(BlockMapping.of("minecraft:light_blue_concrete_powder", ChunkerVanillaBlockType.LIGHT_BLUE_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:light_blue_stained_glass", ChunkerVanillaBlockType.LIGHT_BLUE_STAINED_GLASS));
        register(BlockMapping.of("minecraft:light_blue_terracotta", ChunkerVanillaBlockType.LIGHT_BLUE_TERRACOTTA));
        register(BlockMapping.of("minecraft:light_blue_wool", ChunkerVanillaBlockType.LIGHT_BLUE_WOOL));
        register(BlockMapping.of("minecraft:light_gray_carpet", ChunkerVanillaBlockType.LIGHT_GRAY_CARPET));
        register(BlockMapping.of("minecraft:light_gray_concrete", ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE));
        register(BlockMapping.of("minecraft:light_gray_concrete_powder", ChunkerVanillaBlockType.LIGHT_GRAY_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:light_gray_stained_glass", ChunkerVanillaBlockType.LIGHT_GRAY_STAINED_GLASS));
        register(BlockMapping.of("minecraft:light_gray_terracotta", ChunkerVanillaBlockType.LIGHT_GRAY_TERRACOTTA));
        register(BlockMapping.of("minecraft:light_gray_wool", ChunkerVanillaBlockType.LIGHT_GRAY_WOOL));
        register(BlockMapping.of("minecraft:lily_pad", ChunkerVanillaBlockType.LILY_PAD));
        register(BlockMapping.of("minecraft:lime_carpet", ChunkerVanillaBlockType.LIME_CARPET));
        register(BlockMapping.of("minecraft:lime_concrete", ChunkerVanillaBlockType.LIME_CONCRETE));
        register(BlockMapping.of("minecraft:lime_concrete_powder", ChunkerVanillaBlockType.LIME_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:lime_stained_glass", ChunkerVanillaBlockType.LIME_STAINED_GLASS));
        register(BlockMapping.of("minecraft:lime_terracotta", ChunkerVanillaBlockType.LIME_TERRACOTTA));
        register(BlockMapping.of("minecraft:lime_wool", ChunkerVanillaBlockType.LIME_WOOL));
        register(BlockMapping.of("minecraft:magenta_carpet", ChunkerVanillaBlockType.MAGENTA_CARPET));
        register(BlockMapping.of("minecraft:magenta_concrete", ChunkerVanillaBlockType.MAGENTA_CONCRETE));
        register(BlockMapping.of("minecraft:magenta_concrete_powder", ChunkerVanillaBlockType.MAGENTA_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:magenta_stained_glass", ChunkerVanillaBlockType.MAGENTA_STAINED_GLASS));
        register(BlockMapping.of("minecraft:magenta_terracotta", ChunkerVanillaBlockType.MAGENTA_TERRACOTTA));
        register(BlockMapping.of("minecraft:magenta_wool", ChunkerVanillaBlockType.MAGENTA_WOOL));
        register(BlockMapping.of("minecraft:magma_block", ChunkerVanillaBlockType.MAGMA_BLOCK));
        register(BlockMapping.of("minecraft:melon", ChunkerVanillaBlockType.MELON));
        register(BlockMapping.of("minecraft:mossy_cobblestone", ChunkerVanillaBlockType.MOSSY_COBBLESTONE));
        register(BlockMapping.of("minecraft:mossy_stone_bricks", ChunkerVanillaBlockType.MOSSY_STONE_BRICKS));
        register(BlockMapping.of("minecraft:nether_bricks", ChunkerVanillaBlockType.NETHER_BRICKS));
        register(BlockMapping.of("minecraft:nether_portal", ChunkerVanillaBlockType.NETHER_PORTAL, JavaStateGroups.NETHER_PORTAL));
        register(BlockMapping.of("minecraft:nether_quartz_ore", ChunkerVanillaBlockType.NETHER_QUARTZ_ORE));
        register(BlockMapping.of("minecraft:nether_wart_block", ChunkerVanillaBlockType.NETHER_WART_BLOCK));
        register(BlockMapping.of("minecraft:netherrack", ChunkerVanillaBlockType.NETHERRACK));
        register(BlockMapping.of("minecraft:note_block", ChunkerVanillaBlockType.NOTE_BLOCK, JavaStateGroups.NOTE_BLOCK));
        register(BlockMapping.of("minecraft:oak_planks", ChunkerVanillaBlockType.OAK_PLANKS));
        register(BlockMapping.of("minecraft:observer", ChunkerVanillaBlockType.OBSERVER, JavaStateGroups.OBSERVER));
        register(BlockMapping.of("minecraft:obsidian", ChunkerVanillaBlockType.OBSIDIAN));
        register(BlockMapping.of("minecraft:orange_carpet", ChunkerVanillaBlockType.ORANGE_CARPET));
        register(BlockMapping.of("minecraft:orange_concrete", ChunkerVanillaBlockType.ORANGE_CONCRETE));
        register(BlockMapping.of("minecraft:orange_concrete_powder", ChunkerVanillaBlockType.ORANGE_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:orange_stained_glass", ChunkerVanillaBlockType.ORANGE_STAINED_GLASS));
        register(BlockMapping.of("minecraft:orange_terracotta", ChunkerVanillaBlockType.ORANGE_TERRACOTTA));
        register(BlockMapping.of("minecraft:orange_tulip", ChunkerVanillaBlockType.ORANGE_TULIP));
        register(BlockMapping.of("minecraft:orange_wool", ChunkerVanillaBlockType.ORANGE_WOOL));
        register(BlockMapping.of("minecraft:oxeye_daisy", ChunkerVanillaBlockType.OXEYE_DAISY));
        register(BlockMapping.of("minecraft:packed_ice", ChunkerVanillaBlockType.PACKED_ICE));
        register(BlockMapping.of("minecraft:pink_carpet", ChunkerVanillaBlockType.PINK_CARPET));
        register(BlockMapping.of("minecraft:pink_concrete", ChunkerVanillaBlockType.PINK_CONCRETE));
        register(BlockMapping.of("minecraft:pink_concrete_powder", ChunkerVanillaBlockType.PINK_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:pink_stained_glass", ChunkerVanillaBlockType.PINK_STAINED_GLASS));
        register(BlockMapping.of("minecraft:pink_terracotta", ChunkerVanillaBlockType.PINK_TERRACOTTA));
        register(BlockMapping.of("minecraft:pink_tulip", ChunkerVanillaBlockType.PINK_TULIP));
        register(BlockMapping.of("minecraft:pink_wool", ChunkerVanillaBlockType.PINK_WOOL));
        register(BlockMapping.of("minecraft:piston_head", ChunkerVanillaBlockType.PISTON_HEAD, JavaStateGroups.PISTON_HEAD));
        register(BlockMapping.of("minecraft:polished_andesite", ChunkerVanillaBlockType.POLISHED_ANDESITE));
        register(BlockMapping.of("minecraft:polished_diorite", ChunkerVanillaBlockType.POLISHED_DIORITE));
        register(BlockMapping.of("minecraft:polished_granite", ChunkerVanillaBlockType.POLISHED_GRANITE));
        register(BlockMapping.of("minecraft:poppy", ChunkerVanillaBlockType.POPPY));
        register(BlockMapping.of("minecraft:potted_acacia_sapling", ChunkerVanillaBlockType.POTTED_ACACIA_SAPLING));
        register(BlockMapping.of("minecraft:potted_allium", ChunkerVanillaBlockType.POTTED_ALLIUM));
        register(BlockMapping.of("minecraft:potted_azure_bluet", ChunkerVanillaBlockType.POTTED_AZURE_BLUET));
        register(BlockMapping.of("minecraft:potted_birch_sapling", ChunkerVanillaBlockType.POTTED_BIRCH_SAPLING));
        register(BlockMapping.of("minecraft:potted_blue_orchid", ChunkerVanillaBlockType.POTTED_BLUE_ORCHID));
        register(BlockMapping.of("minecraft:potted_brown_mushroom", ChunkerVanillaBlockType.POTTED_BROWN_MUSHROOM));
        register(BlockMapping.of("minecraft:potted_cactus", ChunkerVanillaBlockType.POTTED_CACTUS));
        register(BlockMapping.of("minecraft:potted_dandelion", ChunkerVanillaBlockType.POTTED_DANDELION));
        register(BlockMapping.of("minecraft:potted_dark_oak_sapling", ChunkerVanillaBlockType.POTTED_DARK_OAK_SAPLING));
        register(BlockMapping.of("minecraft:potted_dead_bush", ChunkerVanillaBlockType.POTTED_DEAD_BUSH));
        register(BlockMapping.of("minecraft:potted_fern", ChunkerVanillaBlockType.POTTED_FERN));
        register(BlockMapping.of("minecraft:potted_jungle_sapling", ChunkerVanillaBlockType.POTTED_JUNGLE_SAPLING));
        register(BlockMapping.of("minecraft:potted_oak_sapling", ChunkerVanillaBlockType.POTTED_OAK_SAPLING));
        register(BlockMapping.of("minecraft:potted_orange_tulip", ChunkerVanillaBlockType.POTTED_ORANGE_TULIP));
        register(BlockMapping.of("minecraft:potted_oxeye_daisy", ChunkerVanillaBlockType.POTTED_OXEYE_DAISY));
        register(BlockMapping.of("minecraft:potted_pink_tulip", ChunkerVanillaBlockType.POTTED_PINK_TULIP));
        register(BlockMapping.of("minecraft:potted_poppy", ChunkerVanillaBlockType.POTTED_POPPY));
        register(BlockMapping.of("minecraft:potted_red_mushroom", ChunkerVanillaBlockType.POTTED_RED_MUSHROOM));
        register(BlockMapping.of("minecraft:potted_red_tulip", ChunkerVanillaBlockType.POTTED_RED_TULIP));
        register(BlockMapping.of("minecraft:potted_spruce_sapling", ChunkerVanillaBlockType.POTTED_SPRUCE_SAPLING));
        register(BlockMapping.of("minecraft:potted_white_tulip", ChunkerVanillaBlockType.POTTED_WHITE_TULIP));
        register(BlockMapping.of("minecraft:prismarine", ChunkerVanillaBlockType.PRISMARINE));
        register(BlockMapping.of("minecraft:prismarine_bricks", ChunkerVanillaBlockType.PRISMARINE_BRICKS));
        register(BlockMapping.of("minecraft:pumpkin", ChunkerVanillaBlockType.PUMPKIN));
        register(BlockMapping.of("minecraft:purple_carpet", ChunkerVanillaBlockType.PURPLE_CARPET));
        register(BlockMapping.of("minecraft:purple_concrete", ChunkerVanillaBlockType.PURPLE_CONCRETE));
        register(BlockMapping.of("minecraft:purple_concrete_powder", ChunkerVanillaBlockType.PURPLE_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:purple_stained_glass", ChunkerVanillaBlockType.PURPLE_STAINED_GLASS));
        register(BlockMapping.of("minecraft:purple_terracotta", ChunkerVanillaBlockType.PURPLE_TERRACOTTA));
        register(BlockMapping.of("minecraft:purple_wool", ChunkerVanillaBlockType.PURPLE_WOOL));
        register(BlockMapping.of("minecraft:purpur_block", ChunkerVanillaBlockType.PURPUR_BLOCK));
        register(BlockMapping.of("minecraft:quartz_block", ChunkerVanillaBlockType.QUARTZ_BLOCK));
        register(BlockMapping.of("minecraft:rail", ChunkerVanillaBlockType.RAIL, JavaStateGroups.RAIL));
        register(BlockMapping.of("minecraft:red_carpet", ChunkerVanillaBlockType.RED_CARPET));
        register(BlockMapping.of("minecraft:red_concrete", ChunkerVanillaBlockType.RED_CONCRETE));
        register(BlockMapping.of("minecraft:red_concrete_powder", ChunkerVanillaBlockType.RED_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:red_mushroom", ChunkerVanillaBlockType.RED_MUSHROOM));
        register(BlockMapping.of("minecraft:red_nether_bricks", ChunkerVanillaBlockType.RED_NETHER_BRICKS));
        register(BlockMapping.of("minecraft:red_sand", ChunkerVanillaBlockType.RED_SAND));
        register(BlockMapping.of("minecraft:red_sandstone", ChunkerVanillaBlockType.RED_SANDSTONE));
        register(BlockMapping.of("minecraft:red_stained_glass", ChunkerVanillaBlockType.RED_STAINED_GLASS));
        register(BlockMapping.of("minecraft:red_terracotta", ChunkerVanillaBlockType.RED_TERRACOTTA));
        register(BlockMapping.of("minecraft:red_tulip", ChunkerVanillaBlockType.RED_TULIP));
        register(BlockMapping.of("minecraft:red_wool", ChunkerVanillaBlockType.RED_WOOL));
        register(BlockMapping.of("minecraft:redstone_block", ChunkerVanillaBlockType.REDSTONE_BLOCK));
        register(BlockMapping.of("minecraft:redstone_wire", ChunkerVanillaBlockType.REDSTONE_WIRE, JavaStateGroups.REDSTONE_WIRE));
        register(BlockMapping.of("minecraft:repeater", ChunkerVanillaBlockType.REPEATER, JavaStateGroups.REPEATER));
        register(BlockMapping.of("minecraft:sand", ChunkerVanillaBlockType.SAND));
        register(BlockMapping.of("minecraft:sandstone", ChunkerVanillaBlockType.SANDSTONE));
        register(BlockMapping.of("minecraft:sea_lantern", ChunkerVanillaBlockType.SEA_LANTERN));
        register(BlockMapping.of("minecraft:sea_pickle", ChunkerVanillaBlockType.SEA_PICKLE, JavaStateGroups.SEA_PICKLE));
        register(BlockMapping.of("minecraft:seagrass", ChunkerVanillaBlockType.SEAGRASS, JavaStateGroups.WATERLOGGED_DEFAULT_TRUE));
        register(BlockMapping.of("minecraft:slime_block", ChunkerVanillaBlockType.SLIME_BLOCK));
        register(BlockMapping.of("minecraft:smooth_quartz", ChunkerVanillaBlockType.SMOOTH_QUARTZ));
        register(BlockMapping.of("minecraft:smooth_red_sandstone", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE));
        register(BlockMapping.of("minecraft:smooth_sandstone", ChunkerVanillaBlockType.SMOOTH_SANDSTONE));
        register(BlockMapping.of("minecraft:smooth_stone", ChunkerVanillaBlockType.SMOOTH_STONE));
        register(BlockMapping.of("minecraft:snow", ChunkerVanillaBlockType.SNOW, JavaStateGroups.SNOW));
        register(BlockMapping.of("minecraft:snow_block", ChunkerVanillaBlockType.SNOW_BLOCK));
        register(BlockMapping.of("minecraft:soul_sand", ChunkerVanillaBlockType.SOUL_SAND));
        register(BlockMapping.of("minecraft:spawner", ChunkerVanillaBlockType.SPAWNER));
        register(BlockMapping.of("minecraft:sponge", ChunkerVanillaBlockType.SPONGE));
        register(BlockMapping.of("minecraft:spruce_planks", ChunkerVanillaBlockType.SPRUCE_PLANKS));
        register(BlockMapping.of("minecraft:stone", ChunkerVanillaBlockType.STONE));
        register(BlockMapping.of("minecraft:stone_bricks", ChunkerVanillaBlockType.STONE_BRICKS));
        register(BlockMapping.of("minecraft:structure_block", ChunkerVanillaBlockType.STRUCTURE_BLOCK, JavaStateGroups.STRUCTURE_BLOCK));
        register(BlockMapping.of("minecraft:structure_void", ChunkerVanillaBlockType.STRUCTURE_VOID, JavaStateGroups.STRUCTURE_VOID));
        register(BlockMapping.of("minecraft:terracotta", ChunkerVanillaBlockType.TERRACOTTA));
        register(BlockMapping.of("minecraft:tnt", ChunkerVanillaBlockType.TNT, JavaStateGroups.TNT));
        register(BlockMapping.of("minecraft:torch", ChunkerVanillaBlockType.TORCH));
        register(BlockMapping.of("minecraft:tripwire", ChunkerVanillaBlockType.TRIPWIRE, JavaStateGroups.TRIPWIRE));
        register(BlockMapping.of("minecraft:tripwire_hook", ChunkerVanillaBlockType.TRIPWIRE_HOOK, JavaStateGroups.TRIPWIRE_HOOK));
        register(BlockMapping.of("minecraft:tube_coral_block", ChunkerVanillaBlockType.TUBE_CORAL_BLOCK));
        register(BlockMapping.of("minecraft:turtle_egg", ChunkerVanillaBlockType.TURTLE_EGG, JavaStateGroups.TURTLE_EGG));
        register(BlockMapping.of("minecraft:vine", ChunkerVanillaBlockType.VINE, JavaStateGroups.VINE));
        register(BlockMapping.of("minecraft:void_air", ChunkerVanillaBlockType.VOID_AIR));
        register(BlockMapping.of("minecraft:wet_sponge", ChunkerVanillaBlockType.WET_SPONGE));
        register(BlockMapping.of("minecraft:white_carpet", ChunkerVanillaBlockType.WHITE_CARPET));
        register(BlockMapping.of("minecraft:white_concrete", ChunkerVanillaBlockType.WHITE_CONCRETE));
        register(BlockMapping.of("minecraft:white_concrete_powder", ChunkerVanillaBlockType.WHITE_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:white_stained_glass", ChunkerVanillaBlockType.WHITE_STAINED_GLASS));
        register(BlockMapping.of("minecraft:white_terracotta", ChunkerVanillaBlockType.WHITE_TERRACOTTA));
        register(BlockMapping.of("minecraft:white_tulip", ChunkerVanillaBlockType.WHITE_TULIP));
        register(BlockMapping.of("minecraft:white_wool", ChunkerVanillaBlockType.WHITE_WOOL));
        register(BlockMapping.of("minecraft:yellow_carpet", ChunkerVanillaBlockType.YELLOW_CARPET));
        register(BlockMapping.of("minecraft:yellow_concrete", ChunkerVanillaBlockType.YELLOW_CONCRETE));
        register(BlockMapping.of("minecraft:yellow_concrete_powder", ChunkerVanillaBlockType.YELLOW_CONCRETE_POWDER));
        register(BlockMapping.of("minecraft:yellow_stained_glass", ChunkerVanillaBlockType.YELLOW_STAINED_GLASS));
        register(BlockMapping.of("minecraft:yellow_terracotta", ChunkerVanillaBlockType.YELLOW_TERRACOTTA));
        register(BlockMapping.of("minecraft:yellow_wool", ChunkerVanillaBlockType.YELLOW_WOOL));

        // Moving Block (not equivalent to moving piston)
        registerDuplicateInput(BlockMapping.of("minecraft:air", ChunkerVanillaBlockType.MOVING_BLOCK_BEDROCK));
        register(BlockMapping.of("minecraft:moving_piston", ChunkerVanillaBlockType.MOVING_PISTON_JAVA, JavaStateGroups.MOVING_PISTON));

        // Barrier (also use barrier for invisible_bedrock)
        register(BlockMapping.of("minecraft:barrier", ChunkerVanillaBlockType.BARRIER, JavaStateGroups.BARRIER));
        registerDuplicateInput(BlockMapping.of("minecraft:barrier", ChunkerVanillaBlockType.INVISIBLE_BEDROCK, JavaStateGroups.BARRIER));

        // Signs
        register(BlockMapping.of("minecraft:sign", ChunkerVanillaBlockType.OAK_SIGN, JavaStateGroups.SIGN));
        register(BlockMapping.of("minecraft:wall_sign", ChunkerVanillaBlockType.OAK_WALL_SIGN, JavaStateGroups.FACING_HORIZONTAL_WATERLOGGED));

        // Cauldron
        register(BlockMapping.of("minecraft:cauldron", "level", "0", ChunkerVanillaBlockType.CAULDRON));

        // Cauldron (Item form)
        registerDuplicateOutput(BlockMapping.of("minecraft:cauldron", "data", 0, ChunkerVanillaBlockType.CAULDRON));

        // Cauldron levels should be mapped to water cauldron since in newer versions the block is split
        register(BlockMapping.of("minecraft:cauldron", ChunkerVanillaBlockType.WATER_CAULDRON, JavaStateGroups.CAULDRON));

        // Pistons
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:piston", ChunkerVanillaBlockType.PISTON)
                        .put("minecraft:sticky_piston", ChunkerVanillaBlockType.STICKY_PISTON)
                        .build(),
                JavaStateGroups.PISTON));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:dispenser", ChunkerVanillaBlockType.DISPENSER)
                        .put("minecraft:dropper", ChunkerVanillaBlockType.DROPPER)
                        .build(),
                JavaStateGroups.FACING_TRIGGERED));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:anvil", ChunkerVanillaBlockType.ANVIL)
                        .put("minecraft:attached_melon_stem", ChunkerVanillaBlockType.ATTACHED_MELON_STEM)
                        .put("minecraft:attached_pumpkin_stem", ChunkerVanillaBlockType.ATTACHED_PUMPKIN_STEM)
                        .put("minecraft:black_glazed_terracotta", ChunkerVanillaBlockType.BLACK_GLAZED_TERRACOTTA)
                        .put("minecraft:black_wall_banner", ChunkerVanillaBlockType.BLACK_WALL_BANNER)
                        .put("minecraft:blue_glazed_terracotta", ChunkerVanillaBlockType.BLUE_GLAZED_TERRACOTTA)
                        .put("minecraft:blue_wall_banner", ChunkerVanillaBlockType.BLUE_WALL_BANNER)
                        .put("minecraft:brown_glazed_terracotta", ChunkerVanillaBlockType.BROWN_GLAZED_TERRACOTTA)
                        .put("minecraft:brown_wall_banner", ChunkerVanillaBlockType.BROWN_WALL_BANNER)
                        .put("minecraft:carved_pumpkin", ChunkerVanillaBlockType.CARVED_PUMPKIN)
                        .put("minecraft:chipped_anvil", ChunkerVanillaBlockType.CHIPPED_ANVIL)
                        .put("minecraft:cyan_glazed_terracotta", ChunkerVanillaBlockType.CYAN_GLAZED_TERRACOTTA)
                        .put("minecraft:cyan_wall_banner", ChunkerVanillaBlockType.CYAN_WALL_BANNER)
                        .put("minecraft:damaged_anvil", ChunkerVanillaBlockType.DAMAGED_ANVIL)
                        .put("minecraft:gray_glazed_terracotta", ChunkerVanillaBlockType.GRAY_GLAZED_TERRACOTTA)
                        .put("minecraft:gray_wall_banner", ChunkerVanillaBlockType.GRAY_WALL_BANNER)
                        .put("minecraft:green_glazed_terracotta", ChunkerVanillaBlockType.GREEN_GLAZED_TERRACOTTA)
                        .put("minecraft:green_wall_banner", ChunkerVanillaBlockType.GREEN_WALL_BANNER)
                        .put("minecraft:jack_o_lantern", ChunkerVanillaBlockType.JACK_O_LANTERN)
                        .put("minecraft:light_blue_glazed_terracotta", ChunkerVanillaBlockType.LIGHT_BLUE_GLAZED_TERRACOTTA)
                        .put("minecraft:light_blue_wall_banner", ChunkerVanillaBlockType.LIGHT_BLUE_WALL_BANNER)
                        .put("minecraft:light_gray_glazed_terracotta", ChunkerVanillaBlockType.LIGHT_GRAY_GLAZED_TERRACOTTA)
                        .put("minecraft:light_gray_wall_banner", ChunkerVanillaBlockType.LIGHT_GRAY_WALL_BANNER)
                        .put("minecraft:lime_glazed_terracotta", ChunkerVanillaBlockType.LIME_GLAZED_TERRACOTTA)
                        .put("minecraft:lime_wall_banner", ChunkerVanillaBlockType.LIME_WALL_BANNER)
                        .put("minecraft:magenta_glazed_terracotta", ChunkerVanillaBlockType.MAGENTA_GLAZED_TERRACOTTA)
                        .put("minecraft:magenta_wall_banner", ChunkerVanillaBlockType.MAGENTA_WALL_BANNER)
                        .put("minecraft:orange_glazed_terracotta", ChunkerVanillaBlockType.ORANGE_GLAZED_TERRACOTTA)
                        .put("minecraft:orange_wall_banner", ChunkerVanillaBlockType.ORANGE_WALL_BANNER)
                        .put("minecraft:pink_glazed_terracotta", ChunkerVanillaBlockType.PINK_GLAZED_TERRACOTTA)
                        .put("minecraft:pink_wall_banner", ChunkerVanillaBlockType.PINK_WALL_BANNER)
                        .put("minecraft:purple_glazed_terracotta", ChunkerVanillaBlockType.PURPLE_GLAZED_TERRACOTTA)
                        .put("minecraft:purple_wall_banner", ChunkerVanillaBlockType.PURPLE_WALL_BANNER)
                        .put("minecraft:red_glazed_terracotta", ChunkerVanillaBlockType.RED_GLAZED_TERRACOTTA)
                        .put("minecraft:red_wall_banner", ChunkerVanillaBlockType.RED_WALL_BANNER)
                        .put("minecraft:wall_torch", ChunkerVanillaBlockType.WALL_TORCH)
                        .put("minecraft:white_glazed_terracotta", ChunkerVanillaBlockType.WHITE_GLAZED_TERRACOTTA)
                        .put("minecraft:white_wall_banner", ChunkerVanillaBlockType.WHITE_WALL_BANNER)
                        .put("minecraft:yellow_glazed_terracotta", ChunkerVanillaBlockType.YELLOW_GLAZED_TERRACOTTA)
                        .put("minecraft:yellow_wall_banner", ChunkerVanillaBlockType.YELLOW_WALL_BANNER)
                        .build(),
                JavaStateGroups.FACING_HORIZONTAL));

        // Skulls
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:creeper_head", ChunkerVanillaBlockType.CREEPER_HEAD)
                        .put("minecraft:dragon_head", ChunkerVanillaBlockType.DRAGON_HEAD)
                        .put("minecraft:player_head", ChunkerVanillaBlockType.PLAYER_HEAD)
                        .put("minecraft:skeleton_skull", ChunkerVanillaBlockType.SKELETON_SKULL)
                        .put("minecraft:wither_skeleton_skull", ChunkerVanillaBlockType.WITHER_SKELETON_SKULL)
                        .put("minecraft:zombie_head", ChunkerVanillaBlockType.ZOMBIE_HEAD)
                        .build(),
                JavaStateGroups.SKULL));


        // Wall skulls
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:creeper_wall_head", ChunkerVanillaBlockType.CREEPER_WALL_HEAD)
                        .put("minecraft:dragon_wall_head", ChunkerVanillaBlockType.DRAGON_WALL_HEAD)
                        .put("minecraft:player_wall_head", ChunkerVanillaBlockType.PLAYER_WALL_HEAD)
                        .put("minecraft:skeleton_wall_skull", ChunkerVanillaBlockType.SKELETON_WALL_SKULL)
                        .put("minecraft:wither_skeleton_wall_skull", ChunkerVanillaBlockType.WITHER_SKELETON_WALL_SKULL)
                        .put("minecraft:zombie_wall_head", ChunkerVanillaBlockType.ZOMBIE_WALL_HEAD)
                        .build(),
                JavaStateGroups.WALL_SKULL));

        // Buttons
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_button", ChunkerVanillaBlockType.ACACIA_BUTTON)
                        .put("minecraft:birch_button", ChunkerVanillaBlockType.BIRCH_BUTTON)
                        .put("minecraft:dark_oak_button", ChunkerVanillaBlockType.DARK_OAK_BUTTON)
                        .put("minecraft:jungle_button", ChunkerVanillaBlockType.JUNGLE_BUTTON)
                        .put("minecraft:lever", ChunkerVanillaBlockType.LEVER)
                        .put("minecraft:oak_button", ChunkerVanillaBlockType.OAK_BUTTON)
                        .put("minecraft:spruce_button", ChunkerVanillaBlockType.SPRUCE_BUTTON)
                        .put("minecraft:stone_button", ChunkerVanillaBlockType.STONE_BUTTON)
                        .build(),
                JavaStateGroups.BUTTON));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:heavy_weighted_pressure_plate", ChunkerVanillaBlockType.HEAVY_WEIGHTED_PRESSURE_PLATE)
                        .put("minecraft:light_weighted_pressure_plate", ChunkerVanillaBlockType.LIGHT_WEIGHTED_PRESSURE_PLATE)
                        .build(),
                JavaStateGroups.POWER));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:black_shulker_box", ChunkerVanillaBlockType.BLACK_SHULKER_BOX)
                        .put("minecraft:blue_shulker_box", ChunkerVanillaBlockType.BLUE_SHULKER_BOX)
                        .put("minecraft:brown_shulker_box", ChunkerVanillaBlockType.BROWN_SHULKER_BOX)
                        .put("minecraft:cyan_shulker_box", ChunkerVanillaBlockType.CYAN_SHULKER_BOX)
                        .put("minecraft:end_rod", ChunkerVanillaBlockType.END_ROD)
                        .put("minecraft:gray_shulker_box", ChunkerVanillaBlockType.GRAY_SHULKER_BOX)
                        .put("minecraft:green_shulker_box", ChunkerVanillaBlockType.GREEN_SHULKER_BOX)
                        .put("minecraft:light_blue_shulker_box", ChunkerVanillaBlockType.LIGHT_BLUE_SHULKER_BOX)
                        .put("minecraft:light_gray_shulker_box", ChunkerVanillaBlockType.LIGHT_GRAY_SHULKER_BOX)
                        .put("minecraft:lime_shulker_box", ChunkerVanillaBlockType.LIME_SHULKER_BOX)
                        .put("minecraft:magenta_shulker_box", ChunkerVanillaBlockType.MAGENTA_SHULKER_BOX)
                        .put("minecraft:orange_shulker_box", ChunkerVanillaBlockType.ORANGE_SHULKER_BOX)
                        .put("minecraft:pink_shulker_box", ChunkerVanillaBlockType.PINK_SHULKER_BOX)
                        .put("minecraft:purple_shulker_box", ChunkerVanillaBlockType.PURPLE_SHULKER_BOX)
                        .put("minecraft:red_shulker_box", ChunkerVanillaBlockType.RED_SHULKER_BOX)
                        .put("minecraft:shulker_box", ChunkerVanillaBlockType.SHULKER_BOX)
                        .put("minecraft:white_shulker_box", ChunkerVanillaBlockType.WHITE_SHULKER_BOX)
                        .put("minecraft:yellow_shulker_box", ChunkerVanillaBlockType.YELLOW_SHULKER_BOX)
                        .build(),
                JavaStateGroups.FACING_ALL));

        // Saplings
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_sapling", ChunkerVanillaBlockType.ACACIA_SAPLING)
                        .put("minecraft:birch_sapling", ChunkerVanillaBlockType.BIRCH_SAPLING)
                        .put("minecraft:dark_oak_sapling", ChunkerVanillaBlockType.DARK_OAK_SAPLING)
                        .put("minecraft:jungle_sapling", ChunkerVanillaBlockType.JUNGLE_SAPLING)
                        .put("minecraft:oak_sapling", ChunkerVanillaBlockType.OAK_SAPLING)
                        .put("minecraft:spruce_sapling", ChunkerVanillaBlockType.SPRUCE_SAPLING)
                        .build(),
                JavaStateGroups.SAPLING));

        // Trapdoors
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_trapdoor", ChunkerVanillaBlockType.ACACIA_TRAPDOOR)
                        .put("minecraft:birch_trapdoor", ChunkerVanillaBlockType.BIRCH_TRAPDOOR)
                        .put("minecraft:dark_oak_trapdoor", ChunkerVanillaBlockType.DARK_OAK_TRAPDOOR)
                        .put("minecraft:iron_trapdoor", ChunkerVanillaBlockType.IRON_TRAPDOOR)
                        .put("minecraft:jungle_trapdoor", ChunkerVanillaBlockType.JUNGLE_TRAPDOOR)
                        .put("minecraft:oak_trapdoor", ChunkerVanillaBlockType.OAK_TRAPDOOR)
                        .put("minecraft:spruce_trapdoor", ChunkerVanillaBlockType.SPRUCE_TRAPDOOR)
                        .build(),
                JavaStateGroups.TRAPDOOR));

        // Slabs
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_slab", ChunkerVanillaBlockType.ACACIA_SLAB)
                        .put("minecraft:birch_slab", ChunkerVanillaBlockType.BIRCH_SLAB)
                        .put("minecraft:brick_slab", ChunkerVanillaBlockType.BRICK_SLAB)
                        .put("minecraft:cobblestone_slab", ChunkerVanillaBlockType.COBBLESTONE_SLAB)
                        .put("minecraft:dark_oak_slab", ChunkerVanillaBlockType.DARK_OAK_SLAB)
                        .put("minecraft:dark_prismarine_slab", ChunkerVanillaBlockType.DARK_PRISMARINE_SLAB)
                        .put("minecraft:jungle_slab", ChunkerVanillaBlockType.JUNGLE_SLAB)
                        .put("minecraft:nether_brick_slab", ChunkerVanillaBlockType.NETHER_BRICK_SLAB)
                        .put("minecraft:oak_slab", ChunkerVanillaBlockType.OAK_SLAB)
                        .put("minecraft:petrified_oak_slab", ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB)
                        .put("minecraft:prismarine_brick_slab", ChunkerVanillaBlockType.PRISMARINE_BRICK_SLAB)
                        .put("minecraft:prismarine_slab", ChunkerVanillaBlockType.PRISMARINE_SLAB)
                        .put("minecraft:purpur_slab", ChunkerVanillaBlockType.PURPUR_SLAB)
                        .put("minecraft:quartz_slab", ChunkerVanillaBlockType.QUARTZ_SLAB)
                        .put("minecraft:red_sandstone_slab", ChunkerVanillaBlockType.RED_SANDSTONE_SLAB)
                        .put("minecraft:sandstone_slab", ChunkerVanillaBlockType.SANDSTONE_SLAB)
                        .put("minecraft:spruce_slab", ChunkerVanillaBlockType.SPRUCE_SLAB)
                        .put("minecraft:stone_brick_slab", ChunkerVanillaBlockType.STONE_BRICK_SLAB)
                        .put("minecraft:stone_slab", ChunkerVanillaBlockType.SMOOTH_STONE_SLAB)
                        .build(),
                JavaStateGroups.SLAB));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:redstone_lamp", ChunkerVanillaBlockType.REDSTONE_LAMP)
                        .put("minecraft:redstone_ore", ChunkerVanillaBlockType.REDSTONE_ORE)
                        .build(),
                JavaStateGroups.LIT_DEFAULT_FALSE));
        register(BlockMapping.of("minecraft:redstone_torch", ChunkerVanillaBlockType.REDSTONE_TORCH, JavaStateGroups.LIT_DEFAULT_TRUE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:cactus", ChunkerVanillaBlockType.CACTUS)
                        .put("minecraft:sugar_cane", ChunkerVanillaBlockType.SUGAR_CANE)
                        .build(),
                JavaStateGroups.AGE_15));

        // Beds
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:black_bed", ChunkerVanillaBlockType.BLACK_BED)
                        .put("minecraft:blue_bed", ChunkerVanillaBlockType.BLUE_BED)
                        .put("minecraft:brown_bed", ChunkerVanillaBlockType.BROWN_BED)
                        .put("minecraft:cyan_bed", ChunkerVanillaBlockType.CYAN_BED)
                        .put("minecraft:gray_bed", ChunkerVanillaBlockType.GRAY_BED)
                        .put("minecraft:green_bed", ChunkerVanillaBlockType.GREEN_BED)
                        .put("minecraft:light_blue_bed", ChunkerVanillaBlockType.LIGHT_BLUE_BED)
                        .put("minecraft:light_gray_bed", ChunkerVanillaBlockType.LIGHT_GRAY_BED)
                        .put("minecraft:lime_bed", ChunkerVanillaBlockType.LIME_BED)
                        .put("minecraft:magenta_bed", ChunkerVanillaBlockType.MAGENTA_BED)
                        .put("minecraft:orange_bed", ChunkerVanillaBlockType.ORANGE_BED)
                        .put("minecraft:pink_bed", ChunkerVanillaBlockType.PINK_BED)
                        .put("minecraft:purple_bed", ChunkerVanillaBlockType.PURPLE_BED)
                        .put("minecraft:red_bed", ChunkerVanillaBlockType.RED_BED)
                        .put("minecraft:white_bed", ChunkerVanillaBlockType.WHITE_BED)
                        .put("minecraft:yellow_bed", ChunkerVanillaBlockType.YELLOW_BED)
                        .build(),
                JavaStateGroups.BED));

        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:brain_coral_wall_fan", ChunkerVanillaBlockType.BRAIN_CORAL_WALL_FAN)
                        .put("minecraft:bubble_coral_wall_fan", ChunkerVanillaBlockType.BUBBLE_CORAL_WALL_FAN)
                        .put("minecraft:dead_brain_coral_wall_fan", ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_WALL_FAN)
                        .put("minecraft:dead_bubble_coral_wall_fan", ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_WALL_FAN)
                        .put("minecraft:dead_fire_coral_wall_fan", ChunkerVanillaBlockType.DEAD_FIRE_CORAL_WALL_FAN)
                        .put("minecraft:dead_horn_coral_wall_fan", ChunkerVanillaBlockType.DEAD_HORN_CORAL_WALL_FAN)
                        .put("minecraft:dead_tube_coral_wall_fan", ChunkerVanillaBlockType.DEAD_TUBE_CORAL_WALL_FAN)
                        .put("minecraft:fire_coral_wall_fan", ChunkerVanillaBlockType.FIRE_CORAL_WALL_FAN)
                        .put("minecraft:horn_coral_wall_fan", ChunkerVanillaBlockType.HORN_CORAL_WALL_FAN)
                        .put("minecraft:tube_coral_wall_fan", ChunkerVanillaBlockType.TUBE_CORAL_WALL_FAN)
                        .put("minecraft:ladder", ChunkerVanillaBlockType.LADDER)
                        .put("minecraft:ender_chest", ChunkerVanillaBlockType.ENDER_CHEST)
                        .build(),
                JavaStateGroups.FACING_HORIZONTAL_WATERLOGGED));

        // Banners
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:black_banner", ChunkerVanillaBlockType.BLACK_BANNER)
                        .put("minecraft:blue_banner", ChunkerVanillaBlockType.BLUE_BANNER)
                        .put("minecraft:brown_banner", ChunkerVanillaBlockType.BROWN_BANNER)
                        .put("minecraft:cyan_banner", ChunkerVanillaBlockType.CYAN_BANNER)
                        .put("minecraft:gray_banner", ChunkerVanillaBlockType.GRAY_BANNER)
                        .put("minecraft:green_banner", ChunkerVanillaBlockType.GREEN_BANNER)
                        .put("minecraft:light_blue_banner", ChunkerVanillaBlockType.LIGHT_BLUE_BANNER)
                        .put("minecraft:light_gray_banner", ChunkerVanillaBlockType.LIGHT_GRAY_BANNER)
                        .put("minecraft:lime_banner", ChunkerVanillaBlockType.LIME_BANNER)
                        .put("minecraft:magenta_banner", ChunkerVanillaBlockType.MAGENTA_BANNER)
                        .put("minecraft:orange_banner", ChunkerVanillaBlockType.ORANGE_BANNER)
                        .put("minecraft:pink_banner", ChunkerVanillaBlockType.PINK_BANNER)
                        .put("minecraft:purple_banner", ChunkerVanillaBlockType.PURPLE_BANNER)
                        .put("minecraft:red_banner", ChunkerVanillaBlockType.RED_BANNER)
                        .put("minecraft:white_banner", ChunkerVanillaBlockType.WHITE_BANNER)
                        .put("minecraft:yellow_banner", ChunkerVanillaBlockType.YELLOW_BANNER)
                        .build(),
                JavaStateGroups.ROTATION));

        // Pressure plates
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_pressure_plate", ChunkerVanillaBlockType.ACACIA_PRESSURE_PLATE)
                        .put("minecraft:birch_pressure_plate", ChunkerVanillaBlockType.BIRCH_PRESSURE_PLATE)
                        .put("minecraft:dark_oak_pressure_plate", ChunkerVanillaBlockType.DARK_OAK_PRESSURE_PLATE)
                        .put("minecraft:jungle_pressure_plate", ChunkerVanillaBlockType.JUNGLE_PRESSURE_PLATE)
                        .put("minecraft:oak_pressure_plate", ChunkerVanillaBlockType.OAK_PRESSURE_PLATE)
                        .put("minecraft:spruce_pressure_plate", ChunkerVanillaBlockType.SPRUCE_PRESSURE_PLATE)
                        .put("minecraft:stone_pressure_plate", ChunkerVanillaBlockType.STONE_PRESSURE_PLATE)
                        .build(),
                JavaStateGroups.POWERED));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:brown_mushroom_block", ChunkerVanillaBlockType.BROWN_MUSHROOM_BLOCK)
                        .put("minecraft:chorus_plant", ChunkerVanillaBlockType.CHORUS_PLANT)
                        .put("minecraft:mushroom_stem", ChunkerVanillaBlockType.MUSHROOM_STEM)
                        .put("minecraft:red_mushroom_block", ChunkerVanillaBlockType.RED_MUSHROOM_BLOCK)
                        .build(),
                JavaStateGroups.CONNECTABLE));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:frosted_ice", ChunkerVanillaBlockType.FROSTED_ICE)
                        .put("minecraft:nether_wart", ChunkerVanillaBlockType.NETHER_WART)
                        .build(),
                JavaStateGroups.AGE_3));
        register(BlockMapping.of("minecraft:beetroots", ChunkerVanillaBlockType.BEETROOTS, JavaStateGroups.AGE_3_TO_7));

        // Fence gates
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_fence_gate", ChunkerVanillaBlockType.ACACIA_FENCE_GATE)
                        .put("minecraft:birch_fence_gate", ChunkerVanillaBlockType.BIRCH_FENCE_GATE)
                        .put("minecraft:dark_oak_fence_gate", ChunkerVanillaBlockType.DARK_OAK_FENCE_GATE)
                        .put("minecraft:jungle_fence_gate", ChunkerVanillaBlockType.JUNGLE_FENCE_GATE)
                        .put("minecraft:oak_fence_gate", ChunkerVanillaBlockType.OAK_FENCE_GATE)
                        .put("minecraft:spruce_fence_gate", ChunkerVanillaBlockType.SPRUCE_FENCE_GATE)
                        .build(),
                JavaStateGroups.FENCE_GATE));

        // Chests
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:chest", ChunkerVanillaBlockType.CHEST)
                        .put("minecraft:trapped_chest", ChunkerVanillaBlockType.TRAPPED_CHEST)
                        .build(),
                JavaStateGroups.CHEST));

        // Panes / Fences
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_fence", ChunkerVanillaBlockType.ACACIA_FENCE)
                        .put("minecraft:birch_fence", ChunkerVanillaBlockType.BIRCH_FENCE)
                        .put("minecraft:black_stained_glass_pane", ChunkerVanillaBlockType.BLACK_STAINED_GLASS_PANE)
                        .put("minecraft:blue_stained_glass_pane", ChunkerVanillaBlockType.BLUE_STAINED_GLASS_PANE)
                        .put("minecraft:brown_stained_glass_pane", ChunkerVanillaBlockType.BROWN_STAINED_GLASS_PANE)
                        .put("minecraft:cyan_stained_glass_pane", ChunkerVanillaBlockType.CYAN_STAINED_GLASS_PANE)
                        .put("minecraft:dark_oak_fence", ChunkerVanillaBlockType.DARK_OAK_FENCE)
                        .put("minecraft:glass_pane", ChunkerVanillaBlockType.GLASS_PANE)
                        .put("minecraft:gray_stained_glass_pane", ChunkerVanillaBlockType.GRAY_STAINED_GLASS_PANE)
                        .put("minecraft:green_stained_glass_pane", ChunkerVanillaBlockType.GREEN_STAINED_GLASS_PANE)
                        .put("minecraft:iron_bars", ChunkerVanillaBlockType.IRON_BARS)
                        .put("minecraft:jungle_fence", ChunkerVanillaBlockType.JUNGLE_FENCE)
                        .put("minecraft:light_blue_stained_glass_pane", ChunkerVanillaBlockType.LIGHT_BLUE_STAINED_GLASS_PANE)
                        .put("minecraft:light_gray_stained_glass_pane", ChunkerVanillaBlockType.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .put("minecraft:lime_stained_glass_pane", ChunkerVanillaBlockType.LIME_STAINED_GLASS_PANE)
                        .put("minecraft:magenta_stained_glass_pane", ChunkerVanillaBlockType.MAGENTA_STAINED_GLASS_PANE)
                        .put("minecraft:nether_brick_fence", ChunkerVanillaBlockType.NETHER_BRICK_FENCE)
                        .put("minecraft:oak_fence", ChunkerVanillaBlockType.OAK_FENCE)
                        .put("minecraft:orange_stained_glass_pane", ChunkerVanillaBlockType.ORANGE_STAINED_GLASS_PANE)
                        .put("minecraft:pink_stained_glass_pane", ChunkerVanillaBlockType.PINK_STAINED_GLASS_PANE)
                        .put("minecraft:purple_stained_glass_pane", ChunkerVanillaBlockType.PURPLE_STAINED_GLASS_PANE)
                        .put("minecraft:red_stained_glass_pane", ChunkerVanillaBlockType.RED_STAINED_GLASS_PANE)
                        .put("minecraft:spruce_fence", ChunkerVanillaBlockType.SPRUCE_FENCE)
                        .put("minecraft:white_stained_glass_pane", ChunkerVanillaBlockType.WHITE_STAINED_GLASS_PANE)
                        .put("minecraft:yellow_stained_glass_pane", ChunkerVanillaBlockType.YELLOW_STAINED_GLASS_PANE)
                        .build(),
                JavaStateGroups.CONNECTABLE_HORIZONTAL));

        // Walls
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:cobblestone_wall", ChunkerVanillaBlockType.COBBLESTONE_WALL)
                        .put("minecraft:mossy_cobblestone_wall", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_WALL)
                        .build(),
                JavaStateGroups.WALL));

        // Crops
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:carrots", ChunkerVanillaBlockType.CARROTS)
                        .put("minecraft:melon_stem", ChunkerVanillaBlockType.MELON_STEM)
                        .put("minecraft:potatoes", ChunkerVanillaBlockType.POTATOES)
                        .put("minecraft:pumpkin_stem", ChunkerVanillaBlockType.PUMPKIN_STEM)
                        .put("minecraft:wheat", ChunkerVanillaBlockType.WHEAT)
                        .build(),
                JavaStateGroups.AGE_7));

        // Redstone based rails
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:activator_rail", ChunkerVanillaBlockType.ACTIVATOR_RAIL)
                        .put("minecraft:detector_rail", ChunkerVanillaBlockType.DETECTOR_RAIL)
                        .put("minecraft:powered_rail", ChunkerVanillaBlockType.POWERED_RAIL)
                        .build(),
                JavaStateGroups.POWERED_RAIL));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_log", ChunkerVanillaBlockType.ACACIA_LOG)
                        .put("minecraft:acacia_wood", ChunkerVanillaBlockType.ACACIA_WOOD)
                        .put("minecraft:birch_log", ChunkerVanillaBlockType.BIRCH_LOG)
                        .put("minecraft:birch_wood", ChunkerVanillaBlockType.BIRCH_WOOD)
                        .put("minecraft:bone_block", ChunkerVanillaBlockType.BONE_BLOCK)
                        .put("minecraft:dark_oak_log", ChunkerVanillaBlockType.DARK_OAK_LOG)
                        .put("minecraft:dark_oak_wood", ChunkerVanillaBlockType.DARK_OAK_WOOD)
                        .put("minecraft:hay_block", ChunkerVanillaBlockType.HAY_BLOCK)
                        .put("minecraft:jungle_log", ChunkerVanillaBlockType.JUNGLE_LOG)
                        .put("minecraft:jungle_wood", ChunkerVanillaBlockType.JUNGLE_WOOD)
                        .put("minecraft:oak_log", ChunkerVanillaBlockType.OAK_LOG)
                        .put("minecraft:oak_wood", ChunkerVanillaBlockType.OAK_WOOD)
                        .put("minecraft:purpur_pillar", ChunkerVanillaBlockType.PURPUR_PILLAR)
                        .put("minecraft:quartz_pillar", ChunkerVanillaBlockType.QUARTZ_PILLAR)
                        .put("minecraft:spruce_log", ChunkerVanillaBlockType.SPRUCE_LOG)
                        .put("minecraft:spruce_wood", ChunkerVanillaBlockType.SPRUCE_WOOD)
                        .put("minecraft:stripped_acacia_log", ChunkerVanillaBlockType.STRIPPED_ACACIA_LOG)
                        .put("minecraft:stripped_acacia_wood", ChunkerVanillaBlockType.STRIPPED_ACACIA_WOOD)
                        .put("minecraft:stripped_birch_log", ChunkerVanillaBlockType.STRIPPED_BIRCH_LOG)
                        .put("minecraft:stripped_birch_wood", ChunkerVanillaBlockType.STRIPPED_BIRCH_WOOD)
                        .put("minecraft:stripped_dark_oak_log", ChunkerVanillaBlockType.STRIPPED_DARK_OAK_LOG)
                        .put("minecraft:stripped_dark_oak_wood", ChunkerVanillaBlockType.STRIPPED_DARK_OAK_WOOD)
                        .put("minecraft:stripped_jungle_log", ChunkerVanillaBlockType.STRIPPED_JUNGLE_LOG)
                        .put("minecraft:stripped_jungle_wood", ChunkerVanillaBlockType.STRIPPED_JUNGLE_WOOD)
                        .put("minecraft:stripped_oak_log", ChunkerVanillaBlockType.STRIPPED_OAK_LOG)
                        .put("minecraft:stripped_oak_wood", ChunkerVanillaBlockType.STRIPPED_OAK_WOOD)
                        .put("minecraft:stripped_spruce_log", ChunkerVanillaBlockType.STRIPPED_SPRUCE_LOG)
                        .put("minecraft:stripped_spruce_wood", ChunkerVanillaBlockType.STRIPPED_SPRUCE_WOOD)
                        .build(),
                JavaStateGroups.AXIS));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:furnace", ChunkerVanillaBlockType.FURNACE)
                        .put("minecraft:redstone_wall_torch", ChunkerVanillaBlockType.REDSTONE_WALL_TORCH)
                        .build(),
                JavaStateGroups.FACING_HORIZONTAL_LIT));
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:large_fern", ChunkerVanillaBlockType.LARGE_FERN)
                        .put("minecraft:lilac", ChunkerVanillaBlockType.LILAC)
                        .put("minecraft:peony", ChunkerVanillaBlockType.PEONY)
                        .put("minecraft:rose_bush", ChunkerVanillaBlockType.ROSE_BUSH)
                        .put("minecraft:sunflower", ChunkerVanillaBlockType.SUNFLOWER)
                        .put("minecraft:tall_grass", ChunkerVanillaBlockType.TALL_GRASS)
                        .build(),
                JavaStateGroups.HALF));
        register(BlockMapping.of("minecraft:tall_seagrass", ChunkerVanillaBlockType.TALL_SEAGRASS, JavaStateGroups.TALL_SEAGRASS));

        // Command blocks
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:chain_command_block", ChunkerVanillaBlockType.CHAIN_COMMAND_BLOCK)
                        .put("minecraft:command_block", ChunkerVanillaBlockType.COMMAND_BLOCK)
                        .put("minecraft:repeating_command_block", ChunkerVanillaBlockType.REPEATING_COMMAND_BLOCK)
                        .build(),
                JavaStateGroups.COMMAND_BLOCK));

        // Grass variants
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:grass_block", ChunkerVanillaBlockType.GRASS_BLOCK)
                        .put("minecraft:mycelium", ChunkerVanillaBlockType.MYCELIUM)
                        .put("minecraft:podzol", ChunkerVanillaBlockType.PODZOL)
                        .build(),
                JavaStateGroups.SNOWY));

        // Leaves
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_leaves", ChunkerVanillaBlockType.ACACIA_LEAVES)
                        .put("minecraft:birch_leaves", ChunkerVanillaBlockType.BIRCH_LEAVES)
                        .put("minecraft:dark_oak_leaves", ChunkerVanillaBlockType.DARK_OAK_LEAVES)
                        .put("minecraft:jungle_leaves", ChunkerVanillaBlockType.JUNGLE_LEAVES)
                        .put("minecraft:oak_leaves", ChunkerVanillaBlockType.OAK_LEAVES)
                        .put("minecraft:spruce_leaves", ChunkerVanillaBlockType.SPRUCE_LEAVES)
                        .build(),
                JavaStateGroups.LEAVES));

        // Liquids
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:lava", ChunkerVanillaBlockType.LAVA)
                        .put("minecraft:water", ChunkerVanillaBlockType.WATER)
                        .build(),
                JavaStateGroups.LIQUID));

        // Conduit
        register(BlockMapping.of("minecraft:conduit", ChunkerVanillaBlockType.CONDUIT, JavaStateGroups.CONDUIT));

        // Coral
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:horn_coral", ChunkerVanillaBlockType.HORN_CORAL)
                        .put("minecraft:tube_coral", ChunkerVanillaBlockType.TUBE_CORAL)
                        .put("minecraft:fire_coral", ChunkerVanillaBlockType.FIRE_CORAL)
                        .put("minecraft:bubble_coral", ChunkerVanillaBlockType.BUBBLE_CORAL)
                        .put("minecraft:brain_coral", ChunkerVanillaBlockType.BRAIN_CORAL)
                        .build(),
                JavaStateGroups.CORAL));

        // Coral Fans
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:brain_coral_fan", ChunkerVanillaBlockType.BRAIN_CORAL_FAN)
                        .put("minecraft:bubble_coral_fan", ChunkerVanillaBlockType.BUBBLE_CORAL_FAN)
                        .put("minecraft:dead_brain_coral_fan", ChunkerVanillaBlockType.DEAD_BRAIN_CORAL_FAN)
                        .put("minecraft:dead_bubble_coral_fan", ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL_FAN)
                        .put("minecraft:dead_fire_coral_fan", ChunkerVanillaBlockType.DEAD_FIRE_CORAL_FAN)
                        .put("minecraft:dead_horn_coral_fan", ChunkerVanillaBlockType.DEAD_HORN_CORAL_FAN)
                        .put("minecraft:dead_tube_coral_fan", ChunkerVanillaBlockType.DEAD_TUBE_CORAL_FAN)
                        .put("minecraft:fire_coral_fan", ChunkerVanillaBlockType.FIRE_CORAL_FAN)
                        .put("minecraft:horn_coral_fan", ChunkerVanillaBlockType.HORN_CORAL_FAN)
                        .put("minecraft:tube_coral_fan", ChunkerVanillaBlockType.TUBE_CORAL_FAN)
                        .build(),
                JavaStateGroups.CORAL_FAN));

        // Stairs
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_stairs", ChunkerVanillaBlockType.ACACIA_STAIRS)
                        .put("minecraft:birch_stairs", ChunkerVanillaBlockType.BIRCH_STAIRS)
                        .put("minecraft:brick_stairs", ChunkerVanillaBlockType.BRICK_STAIRS)
                        .put("minecraft:cobblestone_stairs", ChunkerVanillaBlockType.COBBLESTONE_STAIRS)
                        .put("minecraft:dark_oak_stairs", ChunkerVanillaBlockType.DARK_OAK_STAIRS)
                        .put("minecraft:dark_prismarine_stairs", ChunkerVanillaBlockType.DARK_PRISMARINE_STAIRS)
                        .put("minecraft:jungle_stairs", ChunkerVanillaBlockType.JUNGLE_STAIRS)
                        .put("minecraft:nether_brick_stairs", ChunkerVanillaBlockType.NETHER_BRICK_STAIRS)
                        .put("minecraft:oak_stairs", ChunkerVanillaBlockType.OAK_STAIRS)
                        .put("minecraft:prismarine_brick_stairs", ChunkerVanillaBlockType.PRISMARINE_BRICK_STAIRS)
                        .put("minecraft:prismarine_stairs", ChunkerVanillaBlockType.PRISMARINE_STAIRS)
                        .put("minecraft:purpur_stairs", ChunkerVanillaBlockType.PURPUR_STAIRS)
                        .put("minecraft:quartz_stairs", ChunkerVanillaBlockType.QUARTZ_STAIRS)
                        .put("minecraft:red_sandstone_stairs", ChunkerVanillaBlockType.RED_SANDSTONE_STAIRS)
                        .put("minecraft:sandstone_stairs", ChunkerVanillaBlockType.SANDSTONE_STAIRS)
                        .put("minecraft:spruce_stairs", ChunkerVanillaBlockType.SPRUCE_STAIRS)
                        .put("minecraft:stone_brick_stairs", ChunkerVanillaBlockType.STONE_BRICK_STAIRS)
                        .build(),
                JavaStateGroups.STAIRS));

        // Doors
        register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                        .put("minecraft:acacia_door", ChunkerVanillaBlockType.ACACIA_DOOR)
                        .put("minecraft:birch_door", ChunkerVanillaBlockType.BIRCH_DOOR)
                        .put("minecraft:dark_oak_door", ChunkerVanillaBlockType.DARK_OAK_DOOR)
                        .put("minecraft:iron_door", ChunkerVanillaBlockType.IRON_DOOR)
                        .put("minecraft:jungle_door", ChunkerVanillaBlockType.JUNGLE_DOOR)
                        .put("minecraft:oak_door", ChunkerVanillaBlockType.OAK_DOOR)
                        .put("minecraft:spruce_door", ChunkerVanillaBlockType.SPRUCE_DOOR)
                        .build(),
                JavaStateGroups.DOOR));

        // 1.13.1
        if (version.isGreaterThanOrEqual(1, 13, 1)) {
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:dead_brain_coral", ChunkerVanillaBlockType.DEAD_BRAIN_CORAL)
                            .put("minecraft:dead_bubble_coral", ChunkerVanillaBlockType.DEAD_BUBBLE_CORAL)
                            .put("minecraft:dead_fire_coral", ChunkerVanillaBlockType.DEAD_FIRE_CORAL)
                            .put("minecraft:dead_horn_coral", ChunkerVanillaBlockType.DEAD_HORN_CORAL)
                            .put("minecraft:dead_tube_coral", ChunkerVanillaBlockType.DEAD_TUBE_CORAL)
                            .build(),
                    JavaStateGroups.WATERLOGGED));
        }

        // 1.14
        if (version.isGreaterThanOrEqual(1, 14, 0)) {
            // New signs (the old sign became oak)
            registerOverrideOutput(BlockMapping.of("minecraft:oak_sign", ChunkerVanillaBlockType.OAK_SIGN, JavaStateGroups.SIGN));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:acacia_sign", ChunkerVanillaBlockType.ACACIA_SIGN)
                            .put("minecraft:birch_sign", ChunkerVanillaBlockType.BIRCH_SIGN)
                            .put("minecraft:dark_oak_sign", ChunkerVanillaBlockType.DARK_OAK_SIGN)
                            .put("minecraft:jungle_sign", ChunkerVanillaBlockType.JUNGLE_SIGN)
                            .put("minecraft:spruce_sign", ChunkerVanillaBlockType.SPRUCE_SIGN)
                            .build(),
                    JavaStateGroups.SIGN));

            // New wall signs (the old sign became oak)
            registerOverrideOutput(BlockMapping.of("minecraft:oak_wall_sign", ChunkerVanillaBlockType.OAK_WALL_SIGN, JavaStateGroups.FACING_HORIZONTAL_WATERLOGGED));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:acacia_wall_sign", ChunkerVanillaBlockType.ACACIA_WALL_SIGN)
                            .put("minecraft:birch_wall_sign", ChunkerVanillaBlockType.BIRCH_WALL_SIGN)
                            .put("minecraft:dark_oak_wall_sign", ChunkerVanillaBlockType.DARK_OAK_WALL_SIGN)
                            .put("minecraft:jungle_wall_sign", ChunkerVanillaBlockType.JUNGLE_WALL_SIGN)
                            .put("minecraft:spruce_wall_sign", ChunkerVanillaBlockType.SPRUCE_WALL_SIGN)
                            .build(),
                    JavaStateGroups.FACING_HORIZONTAL_WATERLOGGED));

            register(BlockMapping.of("minecraft:bamboo", ChunkerVanillaBlockType.BAMBOO, JavaStateGroups.BAMBOO));
            register(BlockMapping.of("minecraft:bamboo_sapling", ChunkerVanillaBlockType.BAMBOO_SAPLING, JavaStateGroups.BAMBOO_SAPLING));
            register(BlockMapping.of("minecraft:barrel", ChunkerVanillaBlockType.BARREL, JavaStateGroups.BARREL));
            register(BlockMapping.of("minecraft:bell", ChunkerVanillaBlockType.BELL, JavaStateGroups.BELL));
            register(BlockMapping.of("minecraft:campfire", ChunkerVanillaBlockType.CAMPFIRE, JavaStateGroups.CAMPFIRE));
            register(BlockMapping.of("minecraft:cartography_table", ChunkerVanillaBlockType.CARTOGRAPHY_TABLE));
            register(BlockMapping.of("minecraft:composter", ChunkerVanillaBlockType.COMPOSTER, JavaStateGroups.COMPOSTER));
            register(BlockMapping.of("minecraft:cornflower", ChunkerVanillaBlockType.CORNFLOWER));
            register(BlockMapping.of("minecraft:fletching_table", ChunkerVanillaBlockType.FLETCHING_TABLE));
            register(BlockMapping.of("minecraft:grindstone", ChunkerVanillaBlockType.GRINDSTONE, JavaStateGroups.GRINDSTONE));
            register(BlockMapping.of("minecraft:jigsaw", ChunkerVanillaBlockType.JIGSAW, JavaStateGroups.JIGSAW));
            register(BlockMapping.of("minecraft:lantern", ChunkerVanillaBlockType.LANTERN, JavaStateGroups.LANTERN));
            register(BlockMapping.of("minecraft:lectern", ChunkerVanillaBlockType.LECTERN, JavaStateGroups.LECTERN));
            register(BlockMapping.of("minecraft:lily_of_the_valley", ChunkerVanillaBlockType.LILY_OF_THE_VALLEY));
            register(BlockMapping.of("minecraft:potted_bamboo", ChunkerVanillaBlockType.POTTED_BAMBOO));
            register(BlockMapping.of("minecraft:potted_cornflower", ChunkerVanillaBlockType.POTTED_CORNFLOWER));
            register(BlockMapping.of("minecraft:potted_lily_of_the_valley", ChunkerVanillaBlockType.POTTED_LILY_OF_THE_VALLEY));
            register(BlockMapping.of("minecraft:potted_wither_rose", ChunkerVanillaBlockType.POTTED_WITHER_ROSE));
            register(BlockMapping.of("minecraft:scaffolding", ChunkerVanillaBlockType.SCAFFOLDING, JavaStateGroups.SCAFFOLDING));
            register(BlockMapping.of("minecraft:smithing_table", ChunkerVanillaBlockType.SMITHING_TABLE));
            register(BlockMapping.of("minecraft:sweet_berry_bush", ChunkerVanillaBlockType.SWEET_BERRY_BUSH, JavaStateGroups.AGE_3_TO_7));
            register(BlockMapping.of("minecraft:wither_rose", ChunkerVanillaBlockType.WITHER_ROSE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:loom", ChunkerVanillaBlockType.LOOM)
                            .put("minecraft:stonecutter", ChunkerVanillaBlockType.STONECUTTER)
                            .build(),
                    JavaStateGroups.FACING_HORIZONTAL));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:blast_furnace", ChunkerVanillaBlockType.BLAST_FURNACE)
                            .put("minecraft:smoker", ChunkerVanillaBlockType.SMOKER)
                            .build(),
                    JavaStateGroups.FACING_HORIZONTAL_LIT));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:andesite_stairs", ChunkerVanillaBlockType.ANDESITE_STAIRS)
                            .put("minecraft:diorite_stairs", ChunkerVanillaBlockType.DIORITE_STAIRS)
                            .put("minecraft:end_stone_brick_stairs", ChunkerVanillaBlockType.END_STONE_BRICK_STAIRS)
                            .put("minecraft:granite_stairs", ChunkerVanillaBlockType.GRANITE_STAIRS)
                            .put("minecraft:mossy_cobblestone_stairs", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_STAIRS)
                            .put("minecraft:mossy_stone_brick_stairs", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_STAIRS)
                            .put("minecraft:polished_andesite_stairs", ChunkerVanillaBlockType.POLISHED_ANDESITE_STAIRS)
                            .put("minecraft:polished_diorite_stairs", ChunkerVanillaBlockType.POLISHED_DIORITE_STAIRS)
                            .put("minecraft:polished_granite_stairs", ChunkerVanillaBlockType.POLISHED_GRANITE_STAIRS)
                            .put("minecraft:red_nether_brick_stairs", ChunkerVanillaBlockType.RED_NETHER_BRICK_STAIRS)
                            .put("minecraft:smooth_quartz_stairs", ChunkerVanillaBlockType.SMOOTH_QUARTZ_STAIRS)
                            .put("minecraft:smooth_red_sandstone_stairs", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_STAIRS)
                            .put("minecraft:smooth_sandstone_stairs", ChunkerVanillaBlockType.SMOOTH_SANDSTONE_STAIRS)
                            .put("minecraft:stone_stairs", ChunkerVanillaBlockType.STONE_STAIRS)
                            .build(),
                    JavaStateGroups.STAIRS));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:andesite_wall", ChunkerVanillaBlockType.ANDESITE_WALL)
                            .put("minecraft:brick_wall", ChunkerVanillaBlockType.BRICK_WALL)
                            .put("minecraft:diorite_wall", ChunkerVanillaBlockType.DIORITE_WALL)
                            .put("minecraft:end_stone_brick_wall", ChunkerVanillaBlockType.END_STONE_BRICK_WALL)
                            .put("minecraft:granite_wall", ChunkerVanillaBlockType.GRANITE_WALL)
                            .put("minecraft:mossy_stone_brick_wall", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_WALL)
                            .put("minecraft:nether_brick_wall", ChunkerVanillaBlockType.NETHER_BRICK_WALL)
                            .put("minecraft:prismarine_wall", ChunkerVanillaBlockType.PRISMARINE_WALL)
                            .put("minecraft:red_nether_brick_wall", ChunkerVanillaBlockType.RED_NETHER_BRICK_WALL)
                            .put("minecraft:red_sandstone_wall", ChunkerVanillaBlockType.RED_SANDSTONE_WALL)
                            .put("minecraft:sandstone_wall", ChunkerVanillaBlockType.SANDSTONE_WALL)
                            .put("minecraft:stone_brick_wall", ChunkerVanillaBlockType.STONE_BRICK_WALL)
                            .build(),
                    JavaStateGroups.WALL));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:andesite_slab", ChunkerVanillaBlockType.ANDESITE_SLAB)
                            .put("minecraft:cut_red_sandstone_slab", ChunkerVanillaBlockType.CUT_RED_SANDSTONE_SLAB)
                            .put("minecraft:cut_sandstone_slab", ChunkerVanillaBlockType.CUT_SANDSTONE_SLAB)
                            .put("minecraft:diorite_slab", ChunkerVanillaBlockType.DIORITE_SLAB)
                            .put("minecraft:end_stone_brick_slab", ChunkerVanillaBlockType.END_STONE_BRICK_SLAB)
                            .put("minecraft:granite_slab", ChunkerVanillaBlockType.GRANITE_SLAB)
                            .put("minecraft:mossy_cobblestone_slab", ChunkerVanillaBlockType.MOSSY_COBBLESTONE_SLAB)
                            .put("minecraft:mossy_stone_brick_slab", ChunkerVanillaBlockType.MOSSY_STONE_BRICK_SLAB)
                            .put("minecraft:polished_andesite_slab", ChunkerVanillaBlockType.POLISHED_ANDESITE_SLAB)
                            .put("minecraft:polished_diorite_slab", ChunkerVanillaBlockType.POLISHED_DIORITE_SLAB)
                            .put("minecraft:polished_granite_slab", ChunkerVanillaBlockType.POLISHED_GRANITE_SLAB)
                            .put("minecraft:red_nether_brick_slab", ChunkerVanillaBlockType.RED_NETHER_BRICK_SLAB)
                            .put("minecraft:smooth_quartz_slab", ChunkerVanillaBlockType.SMOOTH_QUARTZ_SLAB)
                            .put("minecraft:smooth_red_sandstone_slab", ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_SLAB)
                            .put("minecraft:smooth_sandstone_slab", ChunkerVanillaBlockType.SMOOTH_SANDSTONE_SLAB)
                            .build(),
                    JavaStateGroups.SLAB));

            // Stone slab is now just stone (not smooth) with smooth_slab being a new identifier
            registerOverrideOutput(BlockMapping.of("minecraft:smooth_stone_slab", ChunkerVanillaBlockType.SMOOTH_STONE_SLAB, JavaStateGroups.SLAB));
            registerOverrideInput(BlockMapping.of("minecraft:stone_slab", ChunkerVanillaBlockType.STONE_SLAB, JavaStateGroups.SLAB));
        }

        // 1.15
        if (version.isGreaterThanOrEqual(1, 15, 0)) {
            register(BlockMapping.of("minecraft:honey_block", ChunkerVanillaBlockType.HONEY_BLOCK));
            register(BlockMapping.of("minecraft:honeycomb_block", ChunkerVanillaBlockType.HONEYCOMB_BLOCK));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:bee_nest", ChunkerVanillaBlockType.BEE_NEST)
                            .put("minecraft:beehive", ChunkerVanillaBlockType.BEEHIVE)
                            .build(),
                    JavaStateGroups.BEE_NEST));
        }

        // 1.16
        if (version.isGreaterThanOrEqual(1, 16, 0)) {
            register(BlockMapping.of("minecraft:ancient_debris", ChunkerVanillaBlockType.ANCIENT_DEBRIS));
            register(BlockMapping.of("minecraft:blackstone", ChunkerVanillaBlockType.BLACKSTONE));
            register(BlockMapping.of("minecraft:chain", ChunkerVanillaBlockType.CHAIN, JavaStateGroups.CHAIN));
            register(BlockMapping.of("minecraft:chiseled_nether_bricks", ChunkerVanillaBlockType.CHISELED_NETHER_BRICKS));
            register(BlockMapping.of("minecraft:chiseled_polished_blackstone", ChunkerVanillaBlockType.CHISELED_POLISHED_BLACKSTONE));
            register(BlockMapping.of("minecraft:cracked_nether_bricks", ChunkerVanillaBlockType.CRACKED_NETHER_BRICKS));
            register(BlockMapping.of("minecraft:cracked_polished_blackstone_bricks", ChunkerVanillaBlockType.CRACKED_POLISHED_BLACKSTONE_BRICKS));
            register(BlockMapping.of("minecraft:crimson_fungus", ChunkerVanillaBlockType.CRIMSON_FUNGUS));
            register(BlockMapping.of("minecraft:crimson_nylium", ChunkerVanillaBlockType.CRIMSON_NYLIUM));
            register(BlockMapping.of("minecraft:crimson_planks", ChunkerVanillaBlockType.CRIMSON_PLANKS));
            register(BlockMapping.of("minecraft:crimson_roots", ChunkerVanillaBlockType.CRIMSON_ROOTS));
            register(BlockMapping.of("minecraft:crying_obsidian", ChunkerVanillaBlockType.CRYING_OBSIDIAN));
            register(BlockMapping.of("minecraft:gilded_blackstone", ChunkerVanillaBlockType.GILDED_BLACKSTONE));
            register(BlockMapping.of("minecraft:lodestone", ChunkerVanillaBlockType.LODESTONE));
            register(BlockMapping.of("minecraft:nether_gold_ore", ChunkerVanillaBlockType.NETHER_GOLD_ORE));
            register(BlockMapping.of("minecraft:nether_sprouts", ChunkerVanillaBlockType.NETHER_SPROUTS));
            register(BlockMapping.of("minecraft:netherite_block", ChunkerVanillaBlockType.NETHERITE_BLOCK));
            register(BlockMapping.of("minecraft:polished_blackstone", ChunkerVanillaBlockType.POLISHED_BLACKSTONE));
            register(BlockMapping.of("minecraft:polished_blackstone_bricks", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICKS));
            register(BlockMapping.of("minecraft:potted_crimson_fungus", ChunkerVanillaBlockType.POTTED_CRIMSON_FUNGUS));
            register(BlockMapping.of("minecraft:potted_crimson_roots", ChunkerVanillaBlockType.POTTED_CRIMSON_ROOTS));
            register(BlockMapping.of("minecraft:potted_warped_fungus", ChunkerVanillaBlockType.POTTED_WARPED_FUNGUS));
            register(BlockMapping.of("minecraft:potted_warped_roots", ChunkerVanillaBlockType.POTTED_WARPED_ROOTS));
            register(BlockMapping.of("minecraft:quartz_bricks", ChunkerVanillaBlockType.QUARTZ_BRICKS));
            register(BlockMapping.of("minecraft:respawn_anchor", ChunkerVanillaBlockType.RESPAWN_ANCHOR, JavaStateGroups.RESPAWN_ANCHOR));
            register(BlockMapping.of("minecraft:shroomlight", ChunkerVanillaBlockType.SHROOMLIGHT));
            register(BlockMapping.of("minecraft:soul_campfire", ChunkerVanillaBlockType.SOUL_CAMPFIRE, JavaStateGroups.CAMPFIRE));
            register(BlockMapping.of("minecraft:soul_fire", ChunkerVanillaBlockType.SOUL_FIRE, JavaStateGroups.SOUL_FIRE));
            register(BlockMapping.of("minecraft:soul_lantern", ChunkerVanillaBlockType.SOUL_LANTERN, JavaStateGroups.LANTERN));
            register(BlockMapping.of("minecraft:soul_soil", ChunkerVanillaBlockType.SOUL_SOIL));
            register(BlockMapping.of("minecraft:soul_torch", ChunkerVanillaBlockType.SOUL_TORCH));
            register(BlockMapping.of("minecraft:soul_wall_torch", ChunkerVanillaBlockType.SOUL_WALL_TORCH, JavaStateGroups.FACING_HORIZONTAL));
            register(BlockMapping.of("minecraft:target", ChunkerVanillaBlockType.TARGET, JavaStateGroups.POWER));
            register(BlockMapping.of("minecraft:twisting_vines_plant", ChunkerVanillaBlockType.TWISTING_VINES_PLANT));
            register(BlockMapping.of("minecraft:warped_fungus", ChunkerVanillaBlockType.WARPED_FUNGUS));
            register(BlockMapping.of("minecraft:warped_nylium", ChunkerVanillaBlockType.WARPED_NYLIUM));
            register(BlockMapping.of("minecraft:warped_planks", ChunkerVanillaBlockType.WARPED_PLANKS));
            register(BlockMapping.of("minecraft:warped_roots", ChunkerVanillaBlockType.WARPED_ROOTS));
            register(BlockMapping.of("minecraft:warped_wart_block", ChunkerVanillaBlockType.WARPED_WART_BLOCK));
            register(BlockMapping.of("minecraft:weeping_vines_plant", ChunkerVanillaBlockType.WEEPING_VINES_PLANT));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_sign", ChunkerVanillaBlockType.CRIMSON_SIGN)
                            .put("minecraft:warped_sign", ChunkerVanillaBlockType.WARPED_SIGN)
                            .build(),
                    JavaStateGroups.SIGN));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_fence_gate", ChunkerVanillaBlockType.CRIMSON_FENCE_GATE)
                            .put("minecraft:warped_fence_gate", ChunkerVanillaBlockType.WARPED_FENCE_GATE)
                            .build(),
                    JavaStateGroups.FENCE_GATE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:basalt", ChunkerVanillaBlockType.BASALT)
                            .put("minecraft:crimson_hyphae", ChunkerVanillaBlockType.CRIMSON_HYPHAE)
                            .put("minecraft:crimson_stem", ChunkerVanillaBlockType.CRIMSON_STEM)
                            .put("minecraft:polished_basalt", ChunkerVanillaBlockType.POLISHED_BASALT)
                            .put("minecraft:stripped_crimson_hyphae", ChunkerVanillaBlockType.STRIPPED_CRIMSON_HYPHAE)
                            .put("minecraft:stripped_crimson_stem", ChunkerVanillaBlockType.STRIPPED_CRIMSON_STEM)
                            .put("minecraft:stripped_warped_hyphae", ChunkerVanillaBlockType.STRIPPED_WARPED_HYPHAE)
                            .put("minecraft:stripped_warped_stem", ChunkerVanillaBlockType.STRIPPED_WARPED_STEM)
                            .put("minecraft:warped_hyphae", ChunkerVanillaBlockType.WARPED_HYPHAE)
                            .put("minecraft:warped_stem", ChunkerVanillaBlockType.WARPED_STEM)
                            .build(),
                    JavaStateGroups.AXIS));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_button", ChunkerVanillaBlockType.CRIMSON_BUTTON)
                            .put("minecraft:polished_blackstone_button", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BUTTON)
                            .put("minecraft:warped_button", ChunkerVanillaBlockType.WARPED_BUTTON)
                            .build(),
                    JavaStateGroups.BUTTON));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:twisting_vines", ChunkerVanillaBlockType.TWISTING_VINES)
                            .put("minecraft:weeping_vines", ChunkerVanillaBlockType.WEEPING_VINES)
                            .build(),
                    JavaStateGroups.AGE_25));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_fence", ChunkerVanillaBlockType.CRIMSON_FENCE)
                            .put("minecraft:warped_fence", ChunkerVanillaBlockType.WARPED_FENCE)
                            .build(),
                    JavaStateGroups.CONNECTABLE_HORIZONTAL));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_wall_sign", ChunkerVanillaBlockType.CRIMSON_WALL_SIGN)
                            .put("minecraft:warped_wall_sign", ChunkerVanillaBlockType.WARPED_WALL_SIGN)
                            .build(),
                    JavaStateGroups.FACING_HORIZONTAL_WATERLOGGED));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:blackstone_stairs", ChunkerVanillaBlockType.BLACKSTONE_STAIRS)
                            .put("minecraft:crimson_stairs", ChunkerVanillaBlockType.CRIMSON_STAIRS)
                            .put("minecraft:polished_blackstone_brick_stairs", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICK_STAIRS)
                            .put("minecraft:polished_blackstone_stairs", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_STAIRS)
                            .put("minecraft:warped_stairs", ChunkerVanillaBlockType.WARPED_STAIRS)
                            .build(),
                    JavaStateGroups.STAIRS));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_pressure_plate", ChunkerVanillaBlockType.CRIMSON_PRESSURE_PLATE)
                            .put("minecraft:polished_blackstone_pressure_plate", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_PRESSURE_PLATE)
                            .put("minecraft:warped_pressure_plate", ChunkerVanillaBlockType.WARPED_PRESSURE_PLATE)
                            .build(),
                    JavaStateGroups.POWERED));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:blackstone_wall", ChunkerVanillaBlockType.BLACKSTONE_WALL)
                            .put("minecraft:polished_blackstone_brick_wall", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICK_WALL)
                            .put("minecraft:polished_blackstone_wall", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_WALL)
                            .build(),
                    JavaStateGroups.WALL));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_door", ChunkerVanillaBlockType.CRIMSON_DOOR)
                            .put("minecraft:warped_door", ChunkerVanillaBlockType.WARPED_DOOR)
                            .build(),
                    JavaStateGroups.DOOR));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:crimson_trapdoor", ChunkerVanillaBlockType.CRIMSON_TRAPDOOR)
                            .put("minecraft:warped_trapdoor", ChunkerVanillaBlockType.WARPED_TRAPDOOR)
                            .build(),
                    JavaStateGroups.TRAPDOOR));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:blackstone_slab", ChunkerVanillaBlockType.BLACKSTONE_SLAB)
                            .put("minecraft:crimson_slab", ChunkerVanillaBlockType.CRIMSON_SLAB)
                            .put("minecraft:polished_blackstone_brick_slab", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICK_SLAB)
                            .put("minecraft:polished_blackstone_slab", ChunkerVanillaBlockType.POLISHED_BLACKSTONE_SLAB)
                            .put("minecraft:warped_slab", ChunkerVanillaBlockType.WARPED_SLAB)
                            .build(),
                    JavaStateGroups.SLAB));
        }

        // 1.17
        if (version.isGreaterThanOrEqual(1, 17, 0)) {
            // Water cauldron got introduced instead of cauldron
            registerOverrideInputOutput(BlockMapping.of("minecraft:cauldron", ChunkerVanillaBlockType.CAULDRON));
            registerOverrideOutput(BlockMapping.of("minecraft:water_cauldron", ChunkerVanillaBlockType.WATER_CAULDRON, JavaStateGroups.CAULDRON));

            // New blocks
            register(BlockMapping.of("minecraft:amethyst_block", ChunkerVanillaBlockType.AMETHYST_BLOCK));
            register(BlockMapping.of("minecraft:azalea", ChunkerVanillaBlockType.AZALEA));
            register(BlockMapping.of("minecraft:big_dripleaf", ChunkerVanillaBlockType.BIG_DRIPLEAF, JavaStateGroups.BIG_DRIPLEAF));
            register(BlockMapping.of("minecraft:big_dripleaf_stem", ChunkerVanillaBlockType.BIG_DRIPLEAF_STEM, JavaStateGroups.FACING_HORIZONTAL_WATERLOGGED));
            register(BlockMapping.of("minecraft:budding_amethyst", ChunkerVanillaBlockType.BUDDING_AMETHYST));
            register(BlockMapping.of("minecraft:calcite", ChunkerVanillaBlockType.CALCITE));
            register(BlockMapping.of("minecraft:cave_vines", ChunkerVanillaBlockType.CAVE_VINES_HEAD, JavaStateGroups.CAVE_VINES_HEAD));
            register(BlockMapping.of("minecraft:cave_vines_plant", ChunkerVanillaBlockType.CAVE_VINES_BODY, JavaStateGroups.CAVE_VINES_BODY));
            register(BlockMapping.of("minecraft:chiseled_deepslate", ChunkerVanillaBlockType.CHISELED_DEEPSLATE));
            register(BlockMapping.of("minecraft:cobbled_deepslate", ChunkerVanillaBlockType.COBBLED_DEEPSLATE));
            register(BlockMapping.of("minecraft:copper_block", ChunkerVanillaBlockType.COPPER_BLOCK));
            register(BlockMapping.of("minecraft:copper_ore", ChunkerVanillaBlockType.COPPER_ORE));
            register(BlockMapping.of("minecraft:cracked_deepslate_bricks", ChunkerVanillaBlockType.CRACKED_DEEPSLATE_BRICKS));
            register(BlockMapping.of("minecraft:cracked_deepslate_tiles", ChunkerVanillaBlockType.CRACKED_DEEPSLATE_TILES));
            register(BlockMapping.of("minecraft:cut_copper", ChunkerVanillaBlockType.CUT_COPPER));
            register(BlockMapping.of("minecraft:deepslate_bricks", ChunkerVanillaBlockType.DEEPSLATE_BRICKS));
            register(BlockMapping.of("minecraft:deepslate_coal_ore", ChunkerVanillaBlockType.DEEPSLATE_COAL_ORE));
            register(BlockMapping.of("minecraft:deepslate_copper_ore", ChunkerVanillaBlockType.DEEPSLATE_COPPER_ORE));
            register(BlockMapping.of("minecraft:deepslate_diamond_ore", ChunkerVanillaBlockType.DEEPSLATE_DIAMOND_ORE));
            register(BlockMapping.of("minecraft:deepslate_emerald_ore", ChunkerVanillaBlockType.DEEPSLATE_EMERALD_ORE));
            register(BlockMapping.of("minecraft:deepslate_gold_ore", ChunkerVanillaBlockType.DEEPSLATE_GOLD_ORE));
            register(BlockMapping.of("minecraft:deepslate_iron_ore", ChunkerVanillaBlockType.DEEPSLATE_IRON_ORE));
            register(BlockMapping.of("minecraft:deepslate_lapis_ore", ChunkerVanillaBlockType.DEEPSLATE_LAPIS_ORE));
            register(BlockMapping.of("minecraft:deepslate_tiles", ChunkerVanillaBlockType.DEEPSLATE_TILES));
            registerOverrideOutput(BlockMapping.of("minecraft:dirt_path", ChunkerVanillaBlockType.DIRT_PATH));
            register(BlockMapping.of("minecraft:dripstone_block", ChunkerVanillaBlockType.DRIPSTONE_BLOCK));
            register(BlockMapping.of("minecraft:exposed_copper", ChunkerVanillaBlockType.EXPOSED_COPPER));
            register(BlockMapping.of("minecraft:exposed_cut_copper", ChunkerVanillaBlockType.EXPOSED_CUT_COPPER));
            register(BlockMapping.of("minecraft:flowering_azalea", ChunkerVanillaBlockType.FLOWERING_AZALEA));
            register(BlockMapping.of("minecraft:glow_lichen", ChunkerVanillaBlockType.GLOW_LICHEN, JavaStateGroups.CONNECTABLE_WATERLOGGED));
            register(BlockMapping.of("minecraft:hanging_roots", ChunkerVanillaBlockType.HANGING_ROOTS, JavaStateGroups.WATERLOGGED));
            register(BlockMapping.of("minecraft:lava_cauldron", ChunkerVanillaBlockType.LAVA_CAULDRON, JavaStateGroups.LAVA_CAULDRON));
            register(BlockMapping.of("minecraft:light", ChunkerVanillaBlockType.LIGHT, JavaStateGroups.LIGHT_BLOCK));
            register(BlockMapping.of("minecraft:lightning_rod", ChunkerVanillaBlockType.LIGHTNING_ROD, JavaStateGroups.LIGHTNING_ROD));
            register(BlockMapping.of("minecraft:moss_block", ChunkerVanillaBlockType.MOSS_BLOCK));
            register(BlockMapping.of("minecraft:moss_carpet", ChunkerVanillaBlockType.MOSS_CARPET));
            register(BlockMapping.of("minecraft:oxidized_copper", ChunkerVanillaBlockType.OXIDIZED_COPPER));
            register(BlockMapping.of("minecraft:oxidized_cut_copper", ChunkerVanillaBlockType.OXIDIZED_CUT_COPPER));
            register(BlockMapping.of("minecraft:pointed_dripstone", ChunkerVanillaBlockType.POINTED_DRIPSTONE, JavaStateGroups.POINTED_DRIPSTONE));
            register(BlockMapping.of("minecraft:polished_deepslate", ChunkerVanillaBlockType.POLISHED_DEEPSLATE));
            register(BlockMapping.of("minecraft:potted_azalea_bush", ChunkerVanillaBlockType.POTTED_AZALEA_BUSH));
            register(BlockMapping.of("minecraft:potted_flowering_azalea_bush", ChunkerVanillaBlockType.POTTED_FLOWERING_AZALEA_BUSH));
            register(BlockMapping.of("minecraft:powder_snow", ChunkerVanillaBlockType.POWDER_SNOW));
            register(BlockMapping.of("minecraft:powder_snow_cauldron", ChunkerVanillaBlockType.POWDER_SNOW_CAULDRON, JavaStateGroups.CAULDRON));
            register(BlockMapping.of("minecraft:raw_copper_block", ChunkerVanillaBlockType.RAW_COPPER_BLOCK));
            register(BlockMapping.of("minecraft:raw_gold_block", ChunkerVanillaBlockType.RAW_GOLD_BLOCK));
            register(BlockMapping.of("minecraft:raw_iron_block", ChunkerVanillaBlockType.RAW_IRON_BLOCK));
            register(BlockMapping.of("minecraft:rooted_dirt", ChunkerVanillaBlockType.ROOTED_DIRT));
            register(BlockMapping.of("minecraft:sculk_sensor", ChunkerVanillaBlockType.SCULK_SENSOR, JavaStateGroups.SCULK_SENSOR));
            register(BlockMapping.of("minecraft:small_dripleaf", ChunkerVanillaBlockType.SMALL_DRIPLEAF, JavaStateGroups.SMALL_DRIPLEAF));
            register(BlockMapping.of("minecraft:smooth_basalt", ChunkerVanillaBlockType.SMOOTH_BASALT));
            register(BlockMapping.of("minecraft:spore_blossom", ChunkerVanillaBlockType.SPORE_BLOSSOM));
            register(BlockMapping.of("minecraft:tinted_glass", ChunkerVanillaBlockType.TINTED_GLASS));
            register(BlockMapping.of("minecraft:tuff", ChunkerVanillaBlockType.TUFF));
            register(BlockMapping.of("minecraft:waxed_copper_block", ChunkerVanillaBlockType.WAXED_COPPER_BLOCK));
            register(BlockMapping.of("minecraft:waxed_cut_copper", ChunkerVanillaBlockType.WAXED_CUT_COPPER));
            register(BlockMapping.of("minecraft:waxed_exposed_copper", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER));
            register(BlockMapping.of("minecraft:waxed_exposed_cut_copper", ChunkerVanillaBlockType.WAXED_EXPOSED_CUT_COPPER));
            register(BlockMapping.of("minecraft:waxed_oxidized_copper", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER));
            register(BlockMapping.of("minecraft:waxed_oxidized_cut_copper", ChunkerVanillaBlockType.WAXED_OXIDIZED_CUT_COPPER));
            register(BlockMapping.of("minecraft:waxed_weathered_copper", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER));
            register(BlockMapping.of("minecraft:waxed_weathered_cut_copper", ChunkerVanillaBlockType.WAXED_WEATHERED_CUT_COPPER));
            register(BlockMapping.of("minecraft:weathered_copper", ChunkerVanillaBlockType.WEATHERED_COPPER));
            register(BlockMapping.of("minecraft:weathered_cut_copper", ChunkerVanillaBlockType.WEATHERED_CUT_COPPER));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:black_candle_cake", ChunkerVanillaBlockType.BLACK_CANDLE_CAKE)
                            .put("minecraft:blue_candle_cake", ChunkerVanillaBlockType.BLUE_CANDLE_CAKE)
                            .put("minecraft:brown_candle_cake", ChunkerVanillaBlockType.BROWN_CANDLE_CAKE)
                            .put("minecraft:candle_cake", ChunkerVanillaBlockType.CANDLE_CAKE)
                            .put("minecraft:cyan_candle_cake", ChunkerVanillaBlockType.CYAN_CANDLE_CAKE)
                            .put("minecraft:deepslate_redstone_ore", ChunkerVanillaBlockType.DEEPSLATE_REDSTONE_ORE)
                            .put("minecraft:gray_candle_cake", ChunkerVanillaBlockType.GRAY_CANDLE_CAKE)
                            .put("minecraft:green_candle_cake", ChunkerVanillaBlockType.GREEN_CANDLE_CAKE)
                            .put("minecraft:light_blue_candle_cake", ChunkerVanillaBlockType.LIGHT_BLUE_CANDLE_CAKE)
                            .put("minecraft:light_gray_candle_cake", ChunkerVanillaBlockType.LIGHT_GRAY_CANDLE_CAKE)
                            .put("minecraft:lime_candle_cake", ChunkerVanillaBlockType.LIME_CANDLE_CAKE)
                            .put("minecraft:magenta_candle_cake", ChunkerVanillaBlockType.MAGENTA_CANDLE_CAKE)
                            .put("minecraft:orange_candle_cake", ChunkerVanillaBlockType.ORANGE_CANDLE_CAKE)
                            .put("minecraft:pink_candle_cake", ChunkerVanillaBlockType.PINK_CANDLE_CAKE)
                            .put("minecraft:purple_candle_cake", ChunkerVanillaBlockType.PURPLE_CANDLE_CAKE)
                            .put("minecraft:red_candle_cake", ChunkerVanillaBlockType.RED_CANDLE_CAKE)
                            .put("minecraft:white_candle_cake", ChunkerVanillaBlockType.WHITE_CANDLE_CAKE)
                            .put("minecraft:yellow_candle_cake", ChunkerVanillaBlockType.YELLOW_CANDLE_CAKE)
                            .build(),
                    JavaStateGroups.LIT_DEFAULT_FALSE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:deepslate", ChunkerVanillaBlockType.DEEPSLATE)
                            .put("minecraft:infested_deepslate", ChunkerVanillaBlockType.INFESTED_DEEPSLATE)
                            .build(),
                    JavaStateGroups.AXIS));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:amethyst_cluster", ChunkerVanillaBlockType.AMETHYST_CLUSTER)
                            .put("minecraft:large_amethyst_bud", ChunkerVanillaBlockType.LARGE_AMETHYST_BUD)
                            .put("minecraft:medium_amethyst_bud", ChunkerVanillaBlockType.MEDIUM_AMETHYST_BUD)
                            .put("minecraft:small_amethyst_bud", ChunkerVanillaBlockType.SMALL_AMETHYST_BUD)
                            .build(),
                    JavaStateGroups.FACING_ALL_WATERLOGGED));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:azalea_leaves", ChunkerVanillaBlockType.AZALEA_LEAVES)
                            .put("minecraft:flowering_azalea_leaves", ChunkerVanillaBlockType.FLOWERING_AZALEA_LEAVES)
                            .build(),
                    JavaStateGroups.LEAVES));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:black_candle", ChunkerVanillaBlockType.BLACK_CANDLE)
                            .put("minecraft:blue_candle", ChunkerVanillaBlockType.BLUE_CANDLE)
                            .put("minecraft:brown_candle", ChunkerVanillaBlockType.BROWN_CANDLE)
                            .put("minecraft:candle", ChunkerVanillaBlockType.CANDLE)
                            .put("minecraft:cyan_candle", ChunkerVanillaBlockType.CYAN_CANDLE)
                            .put("minecraft:gray_candle", ChunkerVanillaBlockType.GRAY_CANDLE)
                            .put("minecraft:green_candle", ChunkerVanillaBlockType.GREEN_CANDLE)
                            .put("minecraft:light_blue_candle", ChunkerVanillaBlockType.LIGHT_BLUE_CANDLE)
                            .put("minecraft:light_gray_candle", ChunkerVanillaBlockType.LIGHT_GRAY_CANDLE)
                            .put("minecraft:lime_candle", ChunkerVanillaBlockType.LIME_CANDLE)
                            .put("minecraft:magenta_candle", ChunkerVanillaBlockType.MAGENTA_CANDLE)
                            .put("minecraft:orange_candle", ChunkerVanillaBlockType.ORANGE_CANDLE)
                            .put("minecraft:pink_candle", ChunkerVanillaBlockType.PINK_CANDLE)
                            .put("minecraft:purple_candle", ChunkerVanillaBlockType.PURPLE_CANDLE)
                            .put("minecraft:red_candle", ChunkerVanillaBlockType.RED_CANDLE)
                            .put("minecraft:white_candle", ChunkerVanillaBlockType.WHITE_CANDLE)
                            .put("minecraft:yellow_candle", ChunkerVanillaBlockType.YELLOW_CANDLE)
                            .build(),
                    JavaStateGroups.CANDLE));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:cobbled_deepslate_stairs", ChunkerVanillaBlockType.COBBLED_DEEPSLATE_STAIRS)
                            .put("minecraft:cut_copper_stairs", ChunkerVanillaBlockType.CUT_COPPER_STAIRS)
                            .put("minecraft:deepslate_brick_stairs", ChunkerVanillaBlockType.DEEPSLATE_BRICK_STAIRS)
                            .put("minecraft:deepslate_tile_stairs", ChunkerVanillaBlockType.DEEPSLATE_TILE_STAIRS)
                            .put("minecraft:exposed_cut_copper_stairs", ChunkerVanillaBlockType.EXPOSED_CUT_COPPER_STAIRS)
                            .put("minecraft:oxidized_cut_copper_stairs", ChunkerVanillaBlockType.OXIDIZED_CUT_COPPER_STAIRS)
                            .put("minecraft:polished_deepslate_stairs", ChunkerVanillaBlockType.POLISHED_DEEPSLATE_STAIRS)
                            .put("minecraft:waxed_cut_copper_stairs", ChunkerVanillaBlockType.WAXED_CUT_COPPER_STAIRS)
                            .put("minecraft:waxed_exposed_cut_copper_stairs", ChunkerVanillaBlockType.WAXED_EXPOSED_CUT_COPPER_STAIRS)
                            .put("minecraft:waxed_oxidized_cut_copper_stairs", ChunkerVanillaBlockType.WAXED_OXIDIZED_CUT_COPPER_STAIRS)
                            .put("minecraft:waxed_weathered_cut_copper_stairs", ChunkerVanillaBlockType.WAXED_WEATHERED_CUT_COPPER_STAIRS)
                            .put("minecraft:weathered_cut_copper_stairs", ChunkerVanillaBlockType.WEATHERED_CUT_COPPER_STAIRS)
                            .build(),
                    JavaStateGroups.STAIRS));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:cobbled_deepslate_wall", ChunkerVanillaBlockType.COBBLED_DEEPSLATE_WALL)
                            .put("minecraft:deepslate_brick_wall", ChunkerVanillaBlockType.DEEPSLATE_BRICK_WALL)
                            .put("minecraft:deepslate_tile_wall", ChunkerVanillaBlockType.DEEPSLATE_TILE_WALL)
                            .put("minecraft:polished_deepslate_wall", ChunkerVanillaBlockType.POLISHED_DEEPSLATE_WALL)
                            .build(),
                    JavaStateGroups.WALL));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:cobbled_deepslate_slab", ChunkerVanillaBlockType.COBBLED_DEEPSLATE_SLAB)
                            .put("minecraft:cut_copper_slab", ChunkerVanillaBlockType.CUT_COPPER_SLAB)
                            .put("minecraft:deepslate_brick_slab", ChunkerVanillaBlockType.DEEPSLATE_BRICK_SLAB)
                            .put("minecraft:deepslate_tile_slab", ChunkerVanillaBlockType.DEEPSLATE_TILE_SLAB)
                            .put("minecraft:exposed_cut_copper_slab", ChunkerVanillaBlockType.EXPOSED_CUT_COPPER_SLAB)
                            .put("minecraft:oxidized_cut_copper_slab", ChunkerVanillaBlockType.OXIDIZED_CUT_COPPER_SLAB)
                            .put("minecraft:polished_deepslate_slab", ChunkerVanillaBlockType.POLISHED_DEEPSLATE_SLAB)
                            .put("minecraft:waxed_cut_copper_slab", ChunkerVanillaBlockType.WAXED_CUT_COPPER_SLAB)
                            .put("minecraft:waxed_exposed_cut_copper_slab", ChunkerVanillaBlockType.WAXED_EXPOSED_CUT_COPPER_SLAB)
                            .put("minecraft:waxed_oxidized_cut_copper_slab", ChunkerVanillaBlockType.WAXED_OXIDIZED_CUT_COPPER_SLAB)
                            .put("minecraft:waxed_weathered_cut_copper_slab", ChunkerVanillaBlockType.WAXED_WEATHERED_CUT_COPPER_SLAB)
                            .put("minecraft:weathered_cut_copper_slab", ChunkerVanillaBlockType.WEATHERED_CUT_COPPER_SLAB)
                            .build(),
                    JavaStateGroups.SLAB));
        }

        // 1.19
        if (version.isGreaterThanOrEqual(1, 19, 0)) {
            register(BlockMapping.of("minecraft:frogspawn", ChunkerVanillaBlockType.FROGSPAWN));
            register(BlockMapping.of("minecraft:mangrove_button", ChunkerVanillaBlockType.MANGROVE_BUTTON, JavaStateGroups.BUTTON));
            register(BlockMapping.of("minecraft:mangrove_door", ChunkerVanillaBlockType.MANGROVE_DOOR, JavaStateGroups.DOOR));
            register(BlockMapping.of("minecraft:mangrove_fence", ChunkerVanillaBlockType.MANGROVE_FENCE, JavaStateGroups.CONNECTABLE_HORIZONTAL));
            register(BlockMapping.of("minecraft:mangrove_fence_gate", ChunkerVanillaBlockType.MANGROVE_FENCE_GATE, JavaStateGroups.FENCE_GATE));
            register(BlockMapping.of("minecraft:mangrove_leaves", ChunkerVanillaBlockType.MANGROVE_LEAVES, JavaStateGroups.LEAVES));
            register(BlockMapping.of("minecraft:mangrove_planks", ChunkerVanillaBlockType.MANGROVE_PLANKS));
            register(BlockMapping.of("minecraft:mangrove_pressure_plate", ChunkerVanillaBlockType.MANGROVE_PRESSURE_PLATE, JavaStateGroups.POWERED));
            register(BlockMapping.of("minecraft:mangrove_propagule", ChunkerVanillaBlockType.MANGROVE_PROPAGULE, JavaStateGroups.MANGROVE_PROPAGULE));
            register(BlockMapping.of("minecraft:mangrove_roots", ChunkerVanillaBlockType.MANGROVE_ROOTS, JavaStateGroups.WATERLOGGED));
            register(BlockMapping.of("minecraft:mangrove_sign", ChunkerVanillaBlockType.MANGROVE_SIGN, JavaStateGroups.SIGN));
            register(BlockMapping.of("minecraft:mangrove_trapdoor", ChunkerVanillaBlockType.MANGROVE_TRAPDOOR, JavaStateGroups.TRAPDOOR));
            register(BlockMapping.of("minecraft:mangrove_wall_sign", ChunkerVanillaBlockType.MANGROVE_WALL_SIGN, JavaStateGroups.FACING_HORIZONTAL_WATERLOGGED));
            register(BlockMapping.of("minecraft:mud", ChunkerVanillaBlockType.MUD));
            register(BlockMapping.of("minecraft:mud_brick_wall", ChunkerVanillaBlockType.MUD_BRICK_WALL, JavaStateGroups.WALL));
            register(BlockMapping.of("minecraft:mud_bricks", ChunkerVanillaBlockType.MUD_BRICKS));
            register(BlockMapping.of("minecraft:packed_mud", ChunkerVanillaBlockType.PACKED_MUD));
            register(BlockMapping.of("minecraft:potted_mangrove_propagule", ChunkerVanillaBlockType.POTTED_MANGROVE_PROPAGULE));
            register(BlockMapping.of("minecraft:reinforced_deepslate", ChunkerVanillaBlockType.REINFORCED_DEEPSLATE));
            register(BlockMapping.of("minecraft:sculk", ChunkerVanillaBlockType.SCULK));
            register(BlockMapping.of("minecraft:sculk_catalyst", ChunkerVanillaBlockType.SCULK_CATALYST, JavaStateGroups.SCULK_CATALYST));
            register(BlockMapping.of("minecraft:sculk_shrieker", ChunkerVanillaBlockType.SCULK_SHRIEKER, JavaStateGroups.SCULK_SHRIEKER));
            register(BlockMapping.of("minecraft:sculk_vein", ChunkerVanillaBlockType.SCULK_VEIN, JavaStateGroups.CONNECTABLE_WATERLOGGED));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:mangrove_log", ChunkerVanillaBlockType.MANGROVE_LOG)
                            .put("minecraft:mangrove_wood", ChunkerVanillaBlockType.MANGROVE_WOOD)
                            .put("minecraft:muddy_mangrove_roots", ChunkerVanillaBlockType.MUDDY_MANGROVE_ROOTS)
                            .put("minecraft:ochre_froglight", ChunkerVanillaBlockType.OCHRE_FROGLIGHT)
                            .put("minecraft:pearlescent_froglight", ChunkerVanillaBlockType.PEARLESCENT_FROGLIGHT)
                            .put("minecraft:stripped_mangrove_log", ChunkerVanillaBlockType.STRIPPED_MANGROVE_LOG)
                            .put("minecraft:stripped_mangrove_wood", ChunkerVanillaBlockType.STRIPPED_MANGROVE_WOOD)
                            .put("minecraft:verdant_froglight", ChunkerVanillaBlockType.VERDANT_FROGLIGHT)
                            .build(),
                    JavaStateGroups.AXIS));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:mangrove_stairs", ChunkerVanillaBlockType.MANGROVE_STAIRS)
                            .put("minecraft:mud_brick_stairs", ChunkerVanillaBlockType.MUD_BRICK_STAIRS)
                            .build(),
                    JavaStateGroups.STAIRS));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:mangrove_slab", ChunkerVanillaBlockType.MANGROVE_SLAB)
                            .put("minecraft:mud_brick_slab", ChunkerVanillaBlockType.MUD_BRICK_SLAB)
                            .build(),
                    JavaStateGroups.SLAB));
        }

        // 1.19.3
        if (version.isGreaterThanOrEqual(1, 19, 3)) {
            register(BlockMapping.of("minecraft:bamboo_button", ChunkerVanillaBlockType.BAMBOO_BUTTON, JavaStateGroups.BUTTON));
            register(BlockMapping.of("minecraft:bamboo_door", ChunkerVanillaBlockType.BAMBOO_DOOR, JavaStateGroups.DOOR));
            register(BlockMapping.of("minecraft:bamboo_fence", ChunkerVanillaBlockType.BAMBOO_FENCE, JavaStateGroups.CONNECTABLE_HORIZONTAL));
            register(BlockMapping.of("minecraft:bamboo_fence_gate", ChunkerVanillaBlockType.BAMBOO_FENCE_GATE, JavaStateGroups.FENCE_GATE));
            register(BlockMapping.of("minecraft:bamboo_mosaic", ChunkerVanillaBlockType.BAMBOO_MOSAIC));
            register(BlockMapping.of("minecraft:bamboo_planks", ChunkerVanillaBlockType.BAMBOO_PLANKS));
            register(BlockMapping.of("minecraft:bamboo_pressure_plate", ChunkerVanillaBlockType.BAMBOO_PRESSURE_PLATE, JavaStateGroups.POWERED));
            register(BlockMapping.of("minecraft:bamboo_sign", ChunkerVanillaBlockType.BAMBOO_SIGN, JavaStateGroups.SIGN));
            register(BlockMapping.of("minecraft:bamboo_trapdoor", ChunkerVanillaBlockType.BAMBOO_TRAPDOOR, JavaStateGroups.TRAPDOOR));
            register(BlockMapping.of("minecraft:chiseled_bookshelf", ChunkerVanillaBlockType.CHISELED_BOOKSHELF, JavaStateGroups.CHISELED_BOOKSHELF));
            register(BlockMapping.of("minecraft:piglin_head", ChunkerVanillaBlockType.PIGLIN_HEAD, JavaStateGroups.SKULL));
            register(BlockMapping.of("minecraft:piglin_wall_head", ChunkerVanillaBlockType.PIGLIN_WALL_HEAD, JavaStateGroups.WALL_SKULL));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:bamboo_block", ChunkerVanillaBlockType.BAMBOO_BLOCK)
                            .put("minecraft:stripped_bamboo_block", ChunkerVanillaBlockType.STRIPPED_BAMBOO_BLOCK)
                            .build(),
                    JavaStateGroups.AXIS));
            register(BlockMapping.of("minecraft:bamboo_wall_sign", ChunkerVanillaBlockType.BAMBOO_WALL_SIGN, JavaStateGroups.FACING_HORIZONTAL_WATERLOGGED));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:acacia_wall_hanging_sign", ChunkerVanillaBlockType.ACACIA_WALL_HANGING_SIGN)
                            .put("minecraft:bamboo_wall_hanging_sign", ChunkerVanillaBlockType.BAMBOO_WALL_HANGING_SIGN)
                            .put("minecraft:birch_wall_hanging_sign", ChunkerVanillaBlockType.BIRCH_WALL_HANGING_SIGN)
                            .put("minecraft:crimson_wall_hanging_sign", ChunkerVanillaBlockType.CRIMSON_WALL_HANGING_SIGN)
                            .put("minecraft:dark_oak_wall_hanging_sign", ChunkerVanillaBlockType.DARK_OAK_WALL_HANGING_SIGN)
                            .put("minecraft:jungle_wall_hanging_sign", ChunkerVanillaBlockType.JUNGLE_WALL_HANGING_SIGN)
                            .put("minecraft:mangrove_wall_hanging_sign", ChunkerVanillaBlockType.MANGROVE_WALL_HANGING_SIGN)
                            .put("minecraft:oak_wall_hanging_sign", ChunkerVanillaBlockType.OAK_WALL_HANGING_SIGN)
                            .put("minecraft:spruce_wall_hanging_sign", ChunkerVanillaBlockType.SPRUCE_WALL_HANGING_SIGN)
                            .put("minecraft:warped_wall_hanging_sign", ChunkerVanillaBlockType.WARPED_WALL_HANGING_SIGN)
                            .build(),
                    JavaStateGroups.WALL_HANGING_SIGN));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:bamboo_mosaic_stairs", ChunkerVanillaBlockType.BAMBOO_MOSAIC_STAIRS)
                            .put("minecraft:bamboo_stairs", ChunkerVanillaBlockType.BAMBOO_STAIRS)
                            .build(),
                    JavaStateGroups.STAIRS));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:acacia_hanging_sign", ChunkerVanillaBlockType.ACACIA_HANGING_SIGN)
                            .put("minecraft:bamboo_hanging_sign", ChunkerVanillaBlockType.BAMBOO_HANGING_SIGN)
                            .put("minecraft:birch_hanging_sign", ChunkerVanillaBlockType.BIRCH_HANGING_SIGN)
                            .put("minecraft:crimson_hanging_sign", ChunkerVanillaBlockType.CRIMSON_HANGING_SIGN)
                            .put("minecraft:dark_oak_hanging_sign", ChunkerVanillaBlockType.DARK_OAK_HANGING_SIGN)
                            .put("minecraft:jungle_hanging_sign", ChunkerVanillaBlockType.JUNGLE_HANGING_SIGN)
                            .put("minecraft:mangrove_hanging_sign", ChunkerVanillaBlockType.MANGROVE_HANGING_SIGN)
                            .put("minecraft:oak_hanging_sign", ChunkerVanillaBlockType.OAK_HANGING_SIGN)
                            .put("minecraft:spruce_hanging_sign", ChunkerVanillaBlockType.SPRUCE_HANGING_SIGN)
                            .put("minecraft:warped_hanging_sign", ChunkerVanillaBlockType.WARPED_HANGING_SIGN)
                            .build(),
                    JavaStateGroups.HANGING_SIGN));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:bamboo_mosaic_slab", ChunkerVanillaBlockType.BAMBOO_MOSAIC_SLAB)
                            .put("minecraft:bamboo_slab", ChunkerVanillaBlockType.BAMBOO_SLAB)
                            .build(),
                    JavaStateGroups.SLAB));
        }

        // 1.19.4
        if (version.isGreaterThanOrEqual(1, 19, 4)) {
            register(BlockMapping.of("minecraft:cherry_button", ChunkerVanillaBlockType.CHERRY_BUTTON, JavaStateGroups.BUTTON));
            register(BlockMapping.of("minecraft:cherry_door", ChunkerVanillaBlockType.CHERRY_DOOR, JavaStateGroups.DOOR));
            register(BlockMapping.of("minecraft:cherry_fence", ChunkerVanillaBlockType.CHERRY_FENCE, JavaStateGroups.CONNECTABLE_HORIZONTAL));
            register(BlockMapping.of("minecraft:cherry_fence_gate", ChunkerVanillaBlockType.CHERRY_FENCE_GATE, JavaStateGroups.FENCE_GATE));
            register(BlockMapping.of("minecraft:cherry_hanging_sign", ChunkerVanillaBlockType.CHERRY_HANGING_SIGN, JavaStateGroups.HANGING_SIGN));
            register(BlockMapping.of("minecraft:cherry_leaves", ChunkerVanillaBlockType.CHERRY_LEAVES, JavaStateGroups.LEAVES));
            register(BlockMapping.of("minecraft:cherry_planks", ChunkerVanillaBlockType.CHERRY_PLANKS));
            register(BlockMapping.of("minecraft:cherry_pressure_plate", ChunkerVanillaBlockType.CHERRY_PRESSURE_PLATE, JavaStateGroups.POWERED));
            register(BlockMapping.of("minecraft:cherry_sapling", ChunkerVanillaBlockType.CHERRY_SAPLING, JavaStateGroups.SAPLING));
            register(BlockMapping.of("minecraft:cherry_sign", ChunkerVanillaBlockType.CHERRY_SIGN, JavaStateGroups.SIGN));
            register(BlockMapping.of("minecraft:cherry_slab", ChunkerVanillaBlockType.CHERRY_SLAB, JavaStateGroups.SLAB));
            register(BlockMapping.of("minecraft:cherry_stairs", ChunkerVanillaBlockType.CHERRY_STAIRS, JavaStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:cherry_trapdoor", ChunkerVanillaBlockType.CHERRY_TRAPDOOR, JavaStateGroups.TRAPDOOR));
            register(BlockMapping.of("minecraft:decorated_pot", ChunkerVanillaBlockType.DECORATED_POT, JavaStateGroups.DECORATED_POT));
            register(BlockMapping.of("minecraft:pink_petals", ChunkerVanillaBlockType.PINK_PETALS, JavaStateGroups.PINK_PETALS));
            register(BlockMapping.of("minecraft:potted_cherry_sapling", ChunkerVanillaBlockType.POTTED_CHERRY_SAPLING));
            register(BlockMapping.of("minecraft:potted_torchflower", ChunkerVanillaBlockType.POTTED_TORCHFLOWER));
            register(BlockMapping.of("minecraft:suspicious_sand", ChunkerVanillaBlockType.SUSPICIOUS_SAND, JavaStateGroups.SUSPICIOUS_BLOCK));
            register(BlockMapping.of("minecraft:torchflower", ChunkerVanillaBlockType.TORCHFLOWER));
            register(BlockMapping.of("minecraft:torchflower_crop", ChunkerVanillaBlockType.TORCHFLOWER_CROP, JavaStateGroups.TORCHFLOWER_CROP));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:cherry_log", ChunkerVanillaBlockType.CHERRY_LOG)
                            .put("minecraft:cherry_wood", ChunkerVanillaBlockType.CHERRY_WOOD)
                            .put("minecraft:stripped_cherry_log", ChunkerVanillaBlockType.STRIPPED_CHERRY_LOG)
                            .put("minecraft:stripped_cherry_wood", ChunkerVanillaBlockType.STRIPPED_CHERRY_WOOD)
                            .build(),
                    JavaStateGroups.AXIS));
            register(BlockMapping.of("minecraft:cherry_wall_hanging_sign", ChunkerVanillaBlockType.CHERRY_WALL_HANGING_SIGN, JavaStateGroups.WALL_HANGING_SIGN));
            register(BlockMapping.of("minecraft:cherry_wall_sign", ChunkerVanillaBlockType.CHERRY_WALL_SIGN, JavaStateGroups.FACING_HORIZONTAL_WATERLOGGED));
        }

        // 1.20
        if (version.isGreaterThanOrEqual(1, 20, 0)) {
            register(BlockMapping.of("minecraft:calibrated_sculk_sensor", ChunkerVanillaBlockType.CALIBRATED_SCULK_SENSOR, JavaStateGroups.CALIBRATED_SCULK_SENSOR));
            register(BlockMapping.of("minecraft:pitcher_crop", ChunkerVanillaBlockType.PITCHER_CROP, JavaStateGroups.PITCHER_CROP));
            register(BlockMapping.of("minecraft:pitcher_plant", ChunkerVanillaBlockType.PITCHER_PLANT, JavaStateGroups.HALF));
            register(BlockMapping.of("minecraft:sniffer_egg", ChunkerVanillaBlockType.SNIFFER_EGG, JavaStateGroups.SNIFFER_EGG));
            register(BlockMapping.of("minecraft:suspicious_gravel", ChunkerVanillaBlockType.SUSPICIOUS_GRAVEL, JavaStateGroups.SUSPICIOUS_BLOCK));
        }

        // 1.20.3
        if (version.isGreaterThanOrEqual(1, 20, 3)) {
            // Grass got renamed (plant)
            registerOverrideOutput(BlockMapping.of("minecraft:short_grass", ChunkerVanillaBlockType.SHORT_GRASS));

            // New blocks
            register(BlockMapping.of("minecraft:chiseled_copper", ChunkerVanillaBlockType.CHISELED_COPPER));
            register(BlockMapping.of("minecraft:chiseled_tuff", ChunkerVanillaBlockType.CHISELED_TUFF));
            register(BlockMapping.of("minecraft:chiseled_tuff_bricks", ChunkerVanillaBlockType.CHISELED_TUFF_BRICKS));
            register(BlockMapping.of("minecraft:crafter", ChunkerVanillaBlockType.CRAFTER, JavaStateGroups.CRAFTER));
            register(BlockMapping.of("minecraft:exposed_chiseled_copper", ChunkerVanillaBlockType.EXPOSED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:oxidized_chiseled_copper", ChunkerVanillaBlockType.OXIDIZED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:polished_tuff", ChunkerVanillaBlockType.POLISHED_TUFF));
            register(BlockMapping.of("minecraft:trial_spawner", ChunkerVanillaBlockType.TRIAL_SPAWNER, JavaStateGroups.TRIAL_SPAWNER));
            register(BlockMapping.of("minecraft:tuff_bricks", ChunkerVanillaBlockType.TUFF_BRICKS));
            register(BlockMapping.of("minecraft:waxed_chiseled_copper", ChunkerVanillaBlockType.WAXED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:waxed_exposed_chiseled_copper", ChunkerVanillaBlockType.WAXED_EXPOSED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:waxed_oxidized_chiseled_copper", ChunkerVanillaBlockType.WAXED_OXIDIZED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:waxed_weathered_chiseled_copper", ChunkerVanillaBlockType.WAXED_WEATHERED_CHISELED_COPPER));
            register(BlockMapping.of("minecraft:weathered_chiseled_copper", ChunkerVanillaBlockType.WEATHERED_CHISELED_COPPER));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:polished_tuff_stairs", ChunkerVanillaBlockType.POLISHED_TUFF_STAIRS)
                            .put("minecraft:tuff_brick_stairs", ChunkerVanillaBlockType.TUFF_BRICK_STAIRS)
                            .put("minecraft:tuff_stairs", ChunkerVanillaBlockType.TUFF_STAIRS)
                            .build(),
                    JavaStateGroups.STAIRS));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:copper_grate", ChunkerVanillaBlockType.COPPER_GRATE)
                            .put("minecraft:exposed_copper_grate", ChunkerVanillaBlockType.EXPOSED_COPPER_GRATE)
                            .put("minecraft:oxidized_copper_grate", ChunkerVanillaBlockType.OXIDIZED_COPPER_GRATE)
                            .put("minecraft:waxed_copper_grate", ChunkerVanillaBlockType.WAXED_COPPER_GRATE)
                            .put("minecraft:waxed_exposed_copper_grate", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_GRATE)
                            .put("minecraft:waxed_oxidized_copper_grate", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_GRATE)
                            .put("minecraft:waxed_weathered_copper_grate", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_GRATE)
                            .put("minecraft:weathered_copper_grate", ChunkerVanillaBlockType.WEATHERED_COPPER_GRATE)
                            .build(),
                    JavaStateGroups.WATERLOGGED));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:polished_tuff_wall", ChunkerVanillaBlockType.POLISHED_TUFF_WALL)
                            .put("minecraft:tuff_brick_wall", ChunkerVanillaBlockType.TUFF_BRICK_WALL)
                            .put("minecraft:tuff_wall", ChunkerVanillaBlockType.TUFF_WALL)
                            .build(),
                    JavaStateGroups.WALL));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:copper_door", ChunkerVanillaBlockType.COPPER_DOOR)
                            .put("minecraft:exposed_copper_door", ChunkerVanillaBlockType.EXPOSED_COPPER_DOOR)
                            .put("minecraft:oxidized_copper_door", ChunkerVanillaBlockType.OXIDIZED_COPPER_DOOR)
                            .put("minecraft:waxed_copper_door", ChunkerVanillaBlockType.WAXED_COPPER_DOOR)
                            .put("minecraft:waxed_exposed_copper_door", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_DOOR)
                            .put("minecraft:waxed_oxidized_copper_door", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_DOOR)
                            .put("minecraft:waxed_weathered_copper_door", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_DOOR)
                            .put("minecraft:weathered_copper_door", ChunkerVanillaBlockType.WEATHERED_COPPER_DOOR)
                            .build(),
                    JavaStateGroups.DOOR));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:copper_bulb", ChunkerVanillaBlockType.COPPER_BULB)
                            .put("minecraft:exposed_copper_bulb", ChunkerVanillaBlockType.EXPOSED_COPPER_BULB)
                            .put("minecraft:oxidized_copper_bulb", ChunkerVanillaBlockType.OXIDIZED_COPPER_BULB)
                            .put("minecraft:waxed_copper_bulb", ChunkerVanillaBlockType.WAXED_COPPER_BULB)
                            .put("minecraft:waxed_exposed_copper_bulb", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_BULB)
                            .put("minecraft:waxed_oxidized_copper_bulb", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_BULB)
                            .put("minecraft:waxed_weathered_copper_bulb", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_BULB)
                            .put("minecraft:weathered_copper_bulb", ChunkerVanillaBlockType.WEATHERED_COPPER_BULB)
                            .build(),
                    JavaStateGroups.BULB));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:copper_trapdoor", ChunkerVanillaBlockType.COPPER_TRAPDOOR)
                            .put("minecraft:exposed_copper_trapdoor", ChunkerVanillaBlockType.EXPOSED_COPPER_TRAPDOOR)
                            .put("minecraft:oxidized_copper_trapdoor", ChunkerVanillaBlockType.OXIDIZED_COPPER_TRAPDOOR)
                            .put("minecraft:waxed_copper_trapdoor", ChunkerVanillaBlockType.WAXED_COPPER_TRAPDOOR)
                            .put("minecraft:waxed_exposed_copper_trapdoor", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_TRAPDOOR)
                            .put("minecraft:waxed_oxidized_copper_trapdoor", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_TRAPDOOR)
                            .put("minecraft:waxed_weathered_copper_trapdoor", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_TRAPDOOR)
                            .put("minecraft:weathered_copper_trapdoor", ChunkerVanillaBlockType.WEATHERED_COPPER_TRAPDOOR)
                            .build(),
                    JavaStateGroups.TRAPDOOR));
            register(BlockMapping.group(ImmutableMultimap.<String, ChunkerVanillaBlockType>builder()
                            .put("minecraft:polished_tuff_slab", ChunkerVanillaBlockType.POLISHED_TUFF_SLAB)
                            .put("minecraft:tuff_brick_slab", ChunkerVanillaBlockType.TUFF_BRICK_SLAB)
                            .put("minecraft:tuff_slab", ChunkerVanillaBlockType.TUFF_SLAB)
                            .build(),
                    JavaStateGroups.SLAB));
        }

        // 1.20.5
        if (version.isGreaterThanOrEqual(1, 20, 5)) {
            register(BlockMapping.of("minecraft:vault", ChunkerVanillaBlockType.VAULT, JavaStateGroups.VAULT));
            register(BlockMapping.of("minecraft:heavy_core", ChunkerVanillaBlockType.HEAVY_CORE, JavaStateGroups.WATERLOGGED));
        }

        // 1.21.2
        if (version.isGreaterThanOrEqual(1, 21, 2)) {
            register(BlockMapping.of("minecraft:creaking_heart", ChunkerVanillaBlockType.CREAKING_HEART, JavaStateGroups.CREAKING_HEART));
            register(BlockMapping.of("minecraft:pale_hanging_moss", ChunkerVanillaBlockType.PALE_HANGING_MOSS, JavaStateGroups.PALE_HANGING_MOSS));
            register(BlockMapping.of("minecraft:pale_moss_block", ChunkerVanillaBlockType.PALE_MOSS_BLOCK));
            register(BlockMapping.of("minecraft:pale_moss_carpet", ChunkerVanillaBlockType.PALE_MOSS_CARPET, JavaStateGroups.PALE_MOSS_CARPET));

            // New pale oak blocks
            register(BlockMapping.of("minecraft:potted_pale_oak_sapling", ChunkerVanillaBlockType.POTTED_PALE_OAK_SAPLING));
            register(BlockMapping.of("minecraft:pale_oak_sapling", ChunkerVanillaBlockType.PALE_OAK_SAPLING, JavaStateGroups.SAPLING));
            register(BlockMapping.of("minecraft:pale_oak_leaves", ChunkerVanillaBlockType.PALE_OAK_LEAVES, JavaStateGroups.LEAVES));
            register(BlockMapping.of("minecraft:pale_oak_log", ChunkerVanillaBlockType.PALE_OAK_LOG, JavaStateGroups.AXIS));
            register(BlockMapping.of("minecraft:pale_oak_wood", ChunkerVanillaBlockType.PALE_OAK_WOOD, JavaStateGroups.AXIS));
            register(BlockMapping.of("minecraft:stripped_pale_oak_log", ChunkerVanillaBlockType.STRIPPED_PALE_OAK_LOG, JavaStateGroups.AXIS));
            register(BlockMapping.of("minecraft:stripped_pale_oak_wood", ChunkerVanillaBlockType.STRIPPED_PALE_OAK_WOOD, JavaStateGroups.AXIS));

            register(BlockMapping.of("minecraft:pale_oak_button", ChunkerVanillaBlockType.PALE_OAK_BUTTON, JavaStateGroups.BUTTON));
            register(BlockMapping.of("minecraft:pale_oak_door", ChunkerVanillaBlockType.PALE_OAK_DOOR, JavaStateGroups.DOOR));
            register(BlockMapping.of("minecraft:pale_oak_fence", ChunkerVanillaBlockType.PALE_OAK_FENCE, JavaStateGroups.CONNECTABLE_HORIZONTAL));
            register(BlockMapping.of("minecraft:pale_oak_fence_gate", ChunkerVanillaBlockType.PALE_OAK_FENCE_GATE, JavaStateGroups.FENCE_GATE));
            register(BlockMapping.of("minecraft:pale_oak_planks", ChunkerVanillaBlockType.PALE_OAK_PLANKS));
            register(BlockMapping.of("minecraft:pale_oak_pressure_plate", ChunkerVanillaBlockType.PALE_OAK_PRESSURE_PLATE, JavaStateGroups.POWERED));
            register(BlockMapping.of("minecraft:pale_oak_slab", ChunkerVanillaBlockType.PALE_OAK_SLAB, JavaStateGroups.SLAB));
            register(BlockMapping.of("minecraft:pale_oak_stairs", ChunkerVanillaBlockType.PALE_OAK_STAIRS, JavaStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:pale_oak_trapdoor", ChunkerVanillaBlockType.PALE_OAK_TRAPDOOR, JavaStateGroups.TRAPDOOR));

            // New signs
            register(BlockMapping.of("minecraft:pale_oak_hanging_sign", ChunkerVanillaBlockType.PALE_OAK_HANGING_SIGN, JavaStateGroups.HANGING_SIGN));
            register(BlockMapping.of("minecraft:pale_oak_wall_hanging_sign", ChunkerVanillaBlockType.PALE_OAK_WALL_HANGING_SIGN, JavaStateGroups.WALL_HANGING_SIGN));
            register(BlockMapping.of("minecraft:pale_oak_sign", ChunkerVanillaBlockType.PALE_OAK_SIGN, JavaStateGroups.SIGN));
            register(BlockMapping.of("minecraft:pale_oak_wall_sign", ChunkerVanillaBlockType.PALE_OAK_WALL_SIGN, JavaStateGroups.FACING_HORIZONTAL_WATERLOGGED));
        }

        // 1.21.4
        if (version.isGreaterThanOrEqual(1, 21, 4)) {
            // New flowers
            register(BlockMapping.of("minecraft:closed_eyeblossom", ChunkerVanillaBlockType.CLOSED_EYEBLOSSOM));
            register(BlockMapping.of("minecraft:open_eyeblossom", ChunkerVanillaBlockType.OPEN_EYEBLOSSOM));
            register(BlockMapping.of("minecraft:potted_closed_eyeblossom", ChunkerVanillaBlockType.POTTED_CLOSED_EYEBLOSSOM));
            register(BlockMapping.of("minecraft:potted_open_eyeblossom", ChunkerVanillaBlockType.POTTED_OPEN_EYEBLOSSOM));

            // New resin blocks
            register(BlockMapping.of("minecraft:resin_block", ChunkerVanillaBlockType.RESIN_BLOCK));
            register(BlockMapping.of("minecraft:resin_bricks", ChunkerVanillaBlockType.RESIN_BRICKS));
            register(BlockMapping.of("minecraft:chiseled_resin_bricks", ChunkerVanillaBlockType.CHISELED_RESIN_BRICKS));
            register(BlockMapping.of("minecraft:resin_brick_slab", ChunkerVanillaBlockType.RESIN_BRICK_SLAB, JavaStateGroups.SLAB));
            register(BlockMapping.of("minecraft:resin_brick_stairs", ChunkerVanillaBlockType.RESIN_BRICK_STAIRS, JavaStateGroups.STAIRS));
            register(BlockMapping.of("minecraft:resin_brick_wall", ChunkerVanillaBlockType.RESIN_BRICK_WALL, JavaStateGroups.WALL));
            register(BlockMapping.of("minecraft:resin_clump", ChunkerVanillaBlockType.RESIN_CLUMP, JavaStateGroups.CONNECTABLE_WATERLOGGED));
        }

        // 1.21.5
        if (version.isGreaterThanOrEqual(1, 21, 5)) {
            // New testing blocks
            register(BlockMapping.of("minecraft:test_block", ChunkerVanillaBlockType.TEST_BLOCK, JavaStateGroups.TEST_BLOCK));
            register(BlockMapping.of("minecraft:test_instance_block", ChunkerVanillaBlockType.TEST_INSTANCE_BLOCK));

            // Spring drop
            register(BlockMapping.of("minecraft:bush", ChunkerVanillaBlockType.BUSH));
            register(BlockMapping.of("minecraft:cactus_flower", ChunkerVanillaBlockType.CACTUS_FLOWER));
            register(BlockMapping.of("minecraft:firefly_bush", ChunkerVanillaBlockType.FIREFLY_BUSH));
            register(BlockMapping.of("minecraft:leaf_litter", ChunkerVanillaBlockType.LEAF_LITTER, JavaStateGroups.LEAF_LITTER));
            register(BlockMapping.of("minecraft:short_dry_grass", ChunkerVanillaBlockType.SHORT_DRY_GRASS));
            register(BlockMapping.of("minecraft:tall_dry_grass", ChunkerVanillaBlockType.TALL_DRY_GRASS));
            register(BlockMapping.of("minecraft:wildflowers", ChunkerVanillaBlockType.WILDFLOWERS, JavaStateGroups.WILDFLOWERS));
        }

        // 1.21.6
        if (version.isGreaterThanOrEqual(1, 21, 6)) {
            register(BlockMapping.of("minecraft:dried_ghast", ChunkerVanillaBlockType.DRIED_GHAST, JavaStateGroups.DRIED_GHAST));
        }

        // 1.21.9
        if (version.isGreaterThanOrEqual(1, 21, 9)) {
            // New copper chests
            register(BlockMapping.of("minecraft:copper_chest", ChunkerVanillaBlockType.COPPER_CHEST, JavaStateGroups.CHEST));
            register(BlockMapping.of("minecraft:exposed_copper_chest", ChunkerVanillaBlockType.EXPOSED_COPPER_CHEST, JavaStateGroups.CHEST));
            register(BlockMapping.of("minecraft:oxidized_copper_chest", ChunkerVanillaBlockType.OXIDIZED_COPPER_CHEST, JavaStateGroups.CHEST));
            register(BlockMapping.of("minecraft:waxed_copper_chest", ChunkerVanillaBlockType.WAXED_COPPER_CHEST, JavaStateGroups.CHEST));
            register(BlockMapping.of("minecraft:waxed_exposed_copper_chest", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_CHEST, JavaStateGroups.CHEST));
            register(BlockMapping.of("minecraft:waxed_oxidized_copper_chest", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_CHEST, JavaStateGroups.CHEST));
            register(BlockMapping.of("minecraft:waxed_weathered_copper_chest", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_CHEST, JavaStateGroups.CHEST));
            register(BlockMapping.of("minecraft:weathered_copper_chest", ChunkerVanillaBlockType.WEATHERED_COPPER_CHEST, JavaStateGroups.CHEST));

            // New lightning rods
            register(BlockMapping.of("minecraft:exposed_lightning_rod", ChunkerVanillaBlockType.EXPOSED_LIGHTNING_ROD, JavaStateGroups.LIGHTNING_ROD));
            register(BlockMapping.of("minecraft:oxidized_lightning_rod", ChunkerVanillaBlockType.OXIDIZED_LIGHTNING_ROD, JavaStateGroups.LIGHTNING_ROD));
            register(BlockMapping.of("minecraft:waxed_lightning_rod", ChunkerVanillaBlockType.WAXED_LIGHTNING_ROD, JavaStateGroups.LIGHTNING_ROD));
            register(BlockMapping.of("minecraft:waxed_exposed_lightning_rod", ChunkerVanillaBlockType.WAXED_EXPOSED_LIGHTNING_ROD, JavaStateGroups.LIGHTNING_ROD));
            register(BlockMapping.of("minecraft:waxed_oxidized_lightning_rod", ChunkerVanillaBlockType.WAXED_OXIDIZED_LIGHTNING_ROD, JavaStateGroups.LIGHTNING_ROD));
            register(BlockMapping.of("minecraft:waxed_weathered_lightning_rod", ChunkerVanillaBlockType.WAXED_WEATHERED_LIGHTNING_ROD, JavaStateGroups.LIGHTNING_ROD));
            register(BlockMapping.of("minecraft:weathered_lightning_rod", ChunkerVanillaBlockType.WEATHERED_LIGHTNING_ROD, JavaStateGroups.LIGHTNING_ROD));

            // New copper golem
            register(BlockMapping.of("minecraft:copper_golem_statue", ChunkerVanillaBlockType.COPPER_GOLEM, JavaStateGroups.COPPER_GOLEM));
            register(BlockMapping.of("minecraft:exposed_copper_golem_statue", ChunkerVanillaBlockType.EXPOSED_COPPER_GOLEM_STATUE, JavaStateGroups.COPPER_GOLEM));
            register(BlockMapping.of("minecraft:oxidized_copper_golem_statue", ChunkerVanillaBlockType.OXIDIZED_COPPER_GOLEM_STATUE, JavaStateGroups.COPPER_GOLEM));
            register(BlockMapping.of("minecraft:waxed_copper_golem_statue", ChunkerVanillaBlockType.WAXED_COPPER_GOLEM_STATUE, JavaStateGroups.COPPER_GOLEM));
            register(BlockMapping.of("minecraft:waxed_exposed_copper_golem_statue", ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_GOLEM_STATUE, JavaStateGroups.COPPER_GOLEM));
            register(BlockMapping.of("minecraft:waxed_oxidized_copper_golem_statue", ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_GOLEM_STATUE, JavaStateGroups.COPPER_GOLEM));
            register(BlockMapping.of("minecraft:waxed_weathered_copper_golem_statue", ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_GOLEM_STATUE, JavaStateGroups.COPPER_GOLEM));
            register(BlockMapping.of("minecraft:weathered_copper_golem_statue", ChunkerVanillaBlockType.WEATHERED_COPPER_GOLEM_STATUE, JavaStateGroups.COPPER_GOLEM));

            // New shelves
            register(BlockMapping.of("minecraft:acacia_shelf", ChunkerVanillaBlockType.ACACIA_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:bamboo_shelf", ChunkerVanillaBlockType.BAMBOO_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:birch_shelf", ChunkerVanillaBlockType.BIRCH_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:cherry_shelf", ChunkerVanillaBlockType.CHERRY_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:crimson_shelf", ChunkerVanillaBlockType.CRIMSON_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:dark_oak_shelf", ChunkerVanillaBlockType.DARK_OAK_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:jungle_shelf", ChunkerVanillaBlockType.JUNGLE_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:mangrove_shelf", ChunkerVanillaBlockType.MANGROVE_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:oak_shelf", ChunkerVanillaBlockType.OAK_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:pale_oak_shelf", ChunkerVanillaBlockType.PALE_OAK_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:spruce_shelf", ChunkerVanillaBlockType.SPRUCE_SHELF, JavaStateGroups.SHELF));
            register(BlockMapping.of("minecraft:warped_shelf", ChunkerVanillaBlockType.WARPED_SHELF, JavaStateGroups.SHELF));
        }
    }
}
