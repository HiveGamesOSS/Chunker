package com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block;

import java.util.Set;

/**
 * Common block groups which should be kept up to date. These are useful for running pre-transforming / targeting a
 * specific block type.
 * Tests should be kept up to date for these in com.hivemc.chunker.util.ChunkerVanillaBlockGroupsTests
 */
public class ChunkerVanillaBlockGroups {
    /**
     * Group of all the door blocks.
     */
    public static final Set<ChunkerBlockType> DOORS = Set.of(
            ChunkerVanillaBlockType.IRON_DOOR,
            ChunkerVanillaBlockType.OAK_DOOR,
            ChunkerVanillaBlockType.SPRUCE_DOOR,
            ChunkerVanillaBlockType.BIRCH_DOOR,
            ChunkerVanillaBlockType.JUNGLE_DOOR,
            ChunkerVanillaBlockType.ACACIA_DOOR,
            ChunkerVanillaBlockType.CHERRY_DOOR,
            ChunkerVanillaBlockType.DARK_OAK_DOOR,
            ChunkerVanillaBlockType.PALE_OAK_DOOR,
            ChunkerVanillaBlockType.MANGROVE_DOOR,
            ChunkerVanillaBlockType.BAMBOO_DOOR,
            ChunkerVanillaBlockType.CRIMSON_DOOR,
            ChunkerVanillaBlockType.WARPED_DOOR,
            ChunkerVanillaBlockType.COPPER_DOOR,
            ChunkerVanillaBlockType.WAXED_COPPER_DOOR,
            ChunkerVanillaBlockType.EXPOSED_COPPER_DOOR,
            ChunkerVanillaBlockType.WAXED_WEATHERED_COPPER_DOOR,
            ChunkerVanillaBlockType.WAXED_EXPOSED_COPPER_DOOR,
            ChunkerVanillaBlockType.WAXED_OXIDIZED_COPPER_DOOR,
            ChunkerVanillaBlockType.OXIDIZED_COPPER_DOOR,
            ChunkerVanillaBlockType.WEATHERED_COPPER_DOOR
    );
    /**
     * Group of all the fence gates.
     */
    public static final Set<ChunkerBlockType> FENCE_GATES = Set.of(
            ChunkerVanillaBlockType.OAK_FENCE_GATE,
            ChunkerVanillaBlockType.SPRUCE_FENCE_GATE,
            ChunkerVanillaBlockType.BIRCH_FENCE_GATE,
            ChunkerVanillaBlockType.JUNGLE_FENCE_GATE,
            ChunkerVanillaBlockType.ACACIA_FENCE_GATE,
            ChunkerVanillaBlockType.CHERRY_FENCE_GATE,
            ChunkerVanillaBlockType.DARK_OAK_FENCE_GATE,
            ChunkerVanillaBlockType.PALE_OAK_FENCE_GATE,
            ChunkerVanillaBlockType.WARPED_FENCE_GATE,
            ChunkerVanillaBlockType.CRIMSON_FENCE_GATE,
            ChunkerVanillaBlockType.MANGROVE_FENCE_GATE,
            ChunkerVanillaBlockType.BAMBOO_FENCE_GATE
    );
    /**
     * Group of all the iron bars and glass panes.
     */
    public static final Set<ChunkerBlockType> IRON_BARS_AND_GLASS_PANES = Set.of(
            ChunkerVanillaBlockType.IRON_BARS,
            ChunkerVanillaBlockType.GLASS_PANE,
            ChunkerVanillaBlockType.WHITE_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.ORANGE_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.MAGENTA_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.LIGHT_BLUE_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.YELLOW_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.LIME_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.PINK_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.GRAY_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.LIGHT_GRAY_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.CYAN_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.PURPLE_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.BLUE_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.BROWN_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.GREEN_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.RED_STAINED_GLASS_PANE,
            ChunkerVanillaBlockType.BLACK_STAINED_GLASS_PANE
    );
    /**
     * Group of all blocks which redstone wire connects towards.
     */
    public static final Set<ChunkerBlockType> REDSTONE_CONNECTABLE = Set.of(
            ChunkerVanillaBlockType.DETECTOR_RAIL,
            ChunkerVanillaBlockType.REDSTONE_WIRE,
            ChunkerVanillaBlockType.LEVER,
            ChunkerVanillaBlockType.STONE_PRESSURE_PLATE,
            ChunkerVanillaBlockType.OAK_PRESSURE_PLATE,
            ChunkerVanillaBlockType.SPRUCE_PRESSURE_PLATE,
            ChunkerVanillaBlockType.BIRCH_PRESSURE_PLATE,
            ChunkerVanillaBlockType.JUNGLE_PRESSURE_PLATE,
            ChunkerVanillaBlockType.ACACIA_PRESSURE_PLATE,
            ChunkerVanillaBlockType.CHERRY_PRESSURE_PLATE,
            ChunkerVanillaBlockType.DARK_OAK_PRESSURE_PLATE,
            ChunkerVanillaBlockType.PALE_OAK_PRESSURE_PLATE,
            ChunkerVanillaBlockType.MANGROVE_PRESSURE_PLATE,
            ChunkerVanillaBlockType.BAMBOO_PRESSURE_PLATE,
            ChunkerVanillaBlockType.REDSTONE_TORCH,
            ChunkerVanillaBlockType.REDSTONE_WALL_TORCH,
            ChunkerVanillaBlockType.STONE_BUTTON,
            ChunkerVanillaBlockType.JUKEBOX,
            ChunkerVanillaBlockType.REPEATER,
            ChunkerVanillaBlockType.TRIPWIRE_HOOK,
            ChunkerVanillaBlockType.OAK_BUTTON,
            ChunkerVanillaBlockType.SPRUCE_BUTTON,
            ChunkerVanillaBlockType.BIRCH_BUTTON,
            ChunkerVanillaBlockType.JUNGLE_BUTTON,
            ChunkerVanillaBlockType.ACACIA_BUTTON,
            ChunkerVanillaBlockType.CHERRY_BUTTON,
            ChunkerVanillaBlockType.DARK_OAK_BUTTON,
            ChunkerVanillaBlockType.PALE_OAK_BUTTON,
            ChunkerVanillaBlockType.MANGROVE_BUTTON,
            ChunkerVanillaBlockType.BAMBOO_BUTTON,
            ChunkerVanillaBlockType.TRAPPED_CHEST,
            ChunkerVanillaBlockType.LIGHT_WEIGHTED_PRESSURE_PLATE,
            ChunkerVanillaBlockType.HEAVY_WEIGHTED_PRESSURE_PLATE,
            ChunkerVanillaBlockType.COMPARATOR,
            ChunkerVanillaBlockType.DAYLIGHT_DETECTOR,
            ChunkerVanillaBlockType.REDSTONE_BLOCK,
            ChunkerVanillaBlockType.OBSERVER,
            ChunkerVanillaBlockType.LECTERN,
            ChunkerVanillaBlockType.CRIMSON_PRESSURE_PLATE,
            ChunkerVanillaBlockType.WARPED_PRESSURE_PLATE,
            ChunkerVanillaBlockType.CRIMSON_BUTTON,
            ChunkerVanillaBlockType.WARPED_BUTTON,
            ChunkerVanillaBlockType.TARGET,
            ChunkerVanillaBlockType.POLISHED_BLACKSTONE_PRESSURE_PLATE,
            ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BUTTON,
            ChunkerVanillaBlockType.SCULK_SENSOR,
            ChunkerVanillaBlockType.CALIBRATED_SCULK_SENSOR,
            ChunkerVanillaBlockType.LIGHTNING_ROD
    );
    /**
     * Group of blocks which cause grass below to turn to a snowy variant.
     */
    public static final Set<ChunkerBlockType> SNOWY_BLOCKS = Set.of(
            ChunkerVanillaBlockType.SNOW,
            ChunkerVanillaBlockType.SNOW_BLOCK,
            ChunkerVanillaBlockType.POWDER_SNOW
    );
    /**
     * Grass like blocks which have a snowy state.
     */
    public static final Set<ChunkerBlockType> SNOWY_GRASS_BLOCKS = Set.of(
            ChunkerVanillaBlockType.GRASS_BLOCK,
            ChunkerVanillaBlockType.PODZOL,
            ChunkerVanillaBlockType.MYCELIUM
    );
    /**
     * Group of all the stair blocks.
     */
    public static final Set<ChunkerBlockType> STAIRS = Set.of(
            ChunkerVanillaBlockType.OAK_STAIRS,
            ChunkerVanillaBlockType.COBBLESTONE_STAIRS,
            ChunkerVanillaBlockType.BRICK_STAIRS,
            ChunkerVanillaBlockType.STONE_BRICK_STAIRS,
            ChunkerVanillaBlockType.MUD_BRICK_STAIRS,
            ChunkerVanillaBlockType.NETHER_BRICK_STAIRS,
            ChunkerVanillaBlockType.SANDSTONE_STAIRS,
            ChunkerVanillaBlockType.SPRUCE_STAIRS,
            ChunkerVanillaBlockType.BIRCH_STAIRS,
            ChunkerVanillaBlockType.JUNGLE_STAIRS,
            ChunkerVanillaBlockType.QUARTZ_STAIRS,
            ChunkerVanillaBlockType.ACACIA_STAIRS,
            ChunkerVanillaBlockType.CHERRY_STAIRS,
            ChunkerVanillaBlockType.DARK_OAK_STAIRS,
            ChunkerVanillaBlockType.PALE_OAK_STAIRS,
            ChunkerVanillaBlockType.MANGROVE_STAIRS,
            ChunkerVanillaBlockType.BAMBOO_STAIRS,
            ChunkerVanillaBlockType.BAMBOO_MOSAIC_STAIRS,
            ChunkerVanillaBlockType.PRISMARINE_STAIRS,
            ChunkerVanillaBlockType.PRISMARINE_BRICK_STAIRS,
            ChunkerVanillaBlockType.DARK_PRISMARINE_STAIRS,
            ChunkerVanillaBlockType.RED_SANDSTONE_STAIRS,
            ChunkerVanillaBlockType.PURPUR_STAIRS,
            ChunkerVanillaBlockType.POLISHED_GRANITE_STAIRS,
            ChunkerVanillaBlockType.SMOOTH_RED_SANDSTONE_STAIRS,
            ChunkerVanillaBlockType.MOSSY_STONE_BRICK_STAIRS,
            ChunkerVanillaBlockType.POLISHED_DIORITE_STAIRS,
            ChunkerVanillaBlockType.MOSSY_COBBLESTONE_STAIRS,
            ChunkerVanillaBlockType.END_STONE_BRICK_STAIRS,
            ChunkerVanillaBlockType.STONE_STAIRS,
            ChunkerVanillaBlockType.SMOOTH_SANDSTONE_STAIRS,
            ChunkerVanillaBlockType.SMOOTH_QUARTZ_STAIRS,
            ChunkerVanillaBlockType.GRANITE_STAIRS,
            ChunkerVanillaBlockType.ANDESITE_STAIRS,
            ChunkerVanillaBlockType.RED_NETHER_BRICK_STAIRS,
            ChunkerVanillaBlockType.POLISHED_ANDESITE_STAIRS,
            ChunkerVanillaBlockType.DIORITE_STAIRS,
            ChunkerVanillaBlockType.CRIMSON_STAIRS,
            ChunkerVanillaBlockType.WARPED_STAIRS,
            ChunkerVanillaBlockType.BLACKSTONE_STAIRS,
            ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICK_STAIRS,
            ChunkerVanillaBlockType.POLISHED_BLACKSTONE_STAIRS,
            ChunkerVanillaBlockType.TUFF_STAIRS,
            ChunkerVanillaBlockType.POLISHED_TUFF_STAIRS,
            ChunkerVanillaBlockType.TUFF_BRICK_STAIRS,
            ChunkerVanillaBlockType.OXIDIZED_CUT_COPPER_STAIRS,
            ChunkerVanillaBlockType.WEATHERED_CUT_COPPER_STAIRS,
            ChunkerVanillaBlockType.EXPOSED_CUT_COPPER_STAIRS,
            ChunkerVanillaBlockType.CUT_COPPER_STAIRS,
            ChunkerVanillaBlockType.WAXED_OXIDIZED_CUT_COPPER_STAIRS,
            ChunkerVanillaBlockType.WAXED_WEATHERED_CUT_COPPER_STAIRS,
            ChunkerVanillaBlockType.WAXED_EXPOSED_CUT_COPPER_STAIRS,
            ChunkerVanillaBlockType.WAXED_CUT_COPPER_STAIRS,
            ChunkerVanillaBlockType.COBBLED_DEEPSLATE_STAIRS,
            ChunkerVanillaBlockType.POLISHED_DEEPSLATE_STAIRS,
            ChunkerVanillaBlockType.DEEPSLATE_TILE_STAIRS,
            ChunkerVanillaBlockType.DEEPSLATE_BRICK_STAIRS,
            ChunkerVanillaBlockType.RESIN_BRICK_STAIRS
    );
    /**
     * Group of all the wall blocks.
     */
    public static final Set<ChunkerBlockType> WALLS = Set.of(
            ChunkerVanillaBlockType.COBBLESTONE_WALL,
            ChunkerVanillaBlockType.MOSSY_COBBLESTONE_WALL,
            ChunkerVanillaBlockType.BRICK_WALL,
            ChunkerVanillaBlockType.PRISMARINE_WALL,
            ChunkerVanillaBlockType.RED_SANDSTONE_WALL,
            ChunkerVanillaBlockType.MOSSY_STONE_BRICK_WALL,
            ChunkerVanillaBlockType.GRANITE_WALL,
            ChunkerVanillaBlockType.STONE_BRICK_WALL,
            ChunkerVanillaBlockType.MUD_BRICK_WALL,
            ChunkerVanillaBlockType.NETHER_BRICK_WALL,
            ChunkerVanillaBlockType.ANDESITE_WALL,
            ChunkerVanillaBlockType.RED_NETHER_BRICK_WALL,
            ChunkerVanillaBlockType.SANDSTONE_WALL,
            ChunkerVanillaBlockType.END_STONE_BRICK_WALL,
            ChunkerVanillaBlockType.DIORITE_WALL,
            ChunkerVanillaBlockType.BLACKSTONE_WALL,
            ChunkerVanillaBlockType.POLISHED_BLACKSTONE_BRICK_WALL,
            ChunkerVanillaBlockType.POLISHED_BLACKSTONE_WALL,
            ChunkerVanillaBlockType.TUFF_WALL,
            ChunkerVanillaBlockType.POLISHED_TUFF_WALL,
            ChunkerVanillaBlockType.TUFF_BRICK_WALL,
            ChunkerVanillaBlockType.COBBLED_DEEPSLATE_WALL,
            ChunkerVanillaBlockType.POLISHED_DEEPSLATE_WALL,
            ChunkerVanillaBlockType.DEEPSLATE_TILE_WALL,
            ChunkerVanillaBlockType.DEEPSLATE_BRICK_WALL,
            ChunkerVanillaBlockType.RESIN_BRICK_WALL
    );
    /**
     * Group of all the wooden fences (currently just excludes the nether brick fence).
     */
    public static final Set<ChunkerBlockType> WOODEN_FENCES = Set.of(
            ChunkerVanillaBlockType.OAK_FENCE,
            ChunkerVanillaBlockType.SPRUCE_FENCE,
            ChunkerVanillaBlockType.BIRCH_FENCE,
            ChunkerVanillaBlockType.JUNGLE_FENCE,
            ChunkerVanillaBlockType.ACACIA_FENCE,
            ChunkerVanillaBlockType.CHERRY_FENCE,
            ChunkerVanillaBlockType.DARK_OAK_FENCE,
            ChunkerVanillaBlockType.PALE_OAK_FENCE,
            ChunkerVanillaBlockType.WARPED_FENCE,
            ChunkerVanillaBlockType.CRIMSON_FENCE,
            ChunkerVanillaBlockType.MANGROVE_FENCE,
            ChunkerVanillaBlockType.BAMBOO_FENCE
    );
}
