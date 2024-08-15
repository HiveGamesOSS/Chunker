package com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy;

import com.google.common.collect.ImmutableMultimap;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.ChunkerItemIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.ItemMapping;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.VanillaBlockStates;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.Bool;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerVanillaItemType;

/**
 * Resolver to convert between Java legacy item identifiers and ChunkerItemStackIdentifier.
 */
public class JavaLegacyItemIdentifierResolver extends ChunkerItemIdentifierResolver {
    /**
     * Create a new java legacy item identifier resolver.
     *
     * @param converter the converter instance.
     * @param version   the version being resolved.
     * @param reader    whether this is used for the reader.
     */
    public JavaLegacyItemIdentifierResolver(Converter converter, Version version, boolean reader) {
        super(converter, version, reader, true);
    }

    @Override
    public void registerMappings(Version version) {
        register(ItemMapping.of("minecraft:bucket", ChunkerVanillaItemType.BUCKET));
        register(ItemMapping.of("minecraft:water_bucket", ChunkerVanillaItemType.WATER_BUCKET));
        register(ItemMapping.of("minecraft:lava_bucket", ChunkerVanillaItemType.LAVA_BUCKET));
        register(ItemMapping.of("minecraft:milk_bucket", ChunkerVanillaItemType.MILK_BUCKET));
        register(ItemMapping.of("minecraft:redstone", ChunkerVanillaItemType.REDSTONE));
        register(ItemMapping.of("minecraft:minecart", ChunkerVanillaItemType.MINECART));
        register(ItemMapping.of("minecraft:furnace_minecart", ChunkerVanillaItemType.FURNACE_MINECART));
        register(ItemMapping.of("minecraft:fire_charge", ChunkerVanillaItemType.FIRE_CHARGE));
        register(ItemMapping.of("minecraft:item_frame", ChunkerVanillaItemType.ITEM_FRAME));

        // Arrows
        register(ItemMapping.of("minecraft:arrow", ChunkerVanillaItemType.ARROW));

        // Potions
        register(ItemMapping.of("minecraft:potion", ChunkerVanillaItemType.POTION));
        registerDuplicateInput(ItemMapping.of("minecraft:potion", ChunkerVanillaItemType.SPLASH_POTION));

        register(ItemMapping.of("minecraft:netherbrick", ChunkerVanillaItemType.NETHER_BRICK));
        register(ItemMapping.of("minecraft:nether_star", ChunkerVanillaItemType.NETHER_STAR));
        register(ItemMapping.of("minecraft:iron_horse_armor", ChunkerVanillaItemType.IRON_HORSE_ARMOR));
        register(ItemMapping.of("minecraft:golden_horse_armor", ChunkerVanillaItemType.GOLDEN_HORSE_ARMOR));
        register(ItemMapping.of("minecraft:diamond_horse_armor", ChunkerVanillaItemType.DIAMOND_HORSE_ARMOR));
        register(ItemMapping.of("minecraft:carrot_on_a_stick", ChunkerVanillaItemType.CARROT_ON_A_STICK));

        // Banners
        register(ItemMapping.flatten("minecraft:banner",
                ImmutableMultimap.<Integer, ChunkerVanillaItemType>builder()
                        .put(15, ChunkerVanillaItemType.WHITE_BANNER)
                        .put(12, ChunkerVanillaItemType.LIGHT_BLUE_BANNER)
                        .put(8, ChunkerVanillaItemType.GRAY_BANNER)
                        .put(6, ChunkerVanillaItemType.CYAN_BANNER)
                        .put(7, ChunkerVanillaItemType.LIGHT_GRAY_BANNER)
                        .put(2, ChunkerVanillaItemType.GREEN_BANNER)
                        .put(14, ChunkerVanillaItemType.ORANGE_BANNER)
                        .put(11, ChunkerVanillaItemType.YELLOW_BANNER)
                        .put(1, ChunkerVanillaItemType.RED_BANNER)
                        .put(9, ChunkerVanillaItemType.PINK_BANNER)
                        .put(3, ChunkerVanillaItemType.BROWN_BANNER)
                        .put(5, ChunkerVanillaItemType.PURPLE_BANNER)
                        .put(13, ChunkerVanillaItemType.MAGENTA_BANNER)
                        .put(10, ChunkerVanillaItemType.LIME_BANNER)
                        .put(4, ChunkerVanillaItemType.BLUE_BANNER)
                        .build()
        ));
        register(ItemMapping.of("minecraft:banner", ChunkerVanillaItemType.BLACK_BANNER));

        register(ItemMapping.of("minecraft:bed", ChunkerVanillaBlockType.RED_BED));
        register(ItemMapping.of("minecraft:sign", ChunkerVanillaItemType.OAK_SIGN));
        register(ItemMapping.of("minecraft:boat", ChunkerVanillaItemType.OAK_BOAT));
        register(ItemMapping.of("minecraft:speckled_melon", ChunkerVanillaItemType.GLISTERING_MELON_SLICE));
        register(ItemMapping.of("minecraft:melon", ChunkerVanillaItemType.MELON_SLICE));
        register(ItemMapping.flatten("minecraft:dye",
                ImmutableMultimap.<Integer, ChunkerVanillaItemType>builder()
                        .put(1, ChunkerVanillaItemType.RED_DYE)
                        .put(2, ChunkerVanillaItemType.GREEN_DYE)
                        .put(3, ChunkerVanillaItemType.COCOA_BEANS)
                        .put(4, ChunkerVanillaItemType.LAPIS_LAZULI)
                        .put(5, ChunkerVanillaItemType.PURPLE_DYE)
                        .put(6, ChunkerVanillaItemType.CYAN_DYE)
                        .put(7, ChunkerVanillaItemType.LIGHT_GRAY_DYE)
                        .put(8, ChunkerVanillaItemType.GRAY_DYE)
                        .put(9, ChunkerVanillaItemType.PINK_DYE)
                        .put(10, ChunkerVanillaItemType.LIME_DYE)
                        .put(11, ChunkerVanillaItemType.YELLOW_DYE)
                        .put(12, ChunkerVanillaItemType.LIGHT_BLUE_DYE)
                        .put(13, ChunkerVanillaItemType.MAGENTA_DYE)
                        .put(14, ChunkerVanillaItemType.ORANGE_DYE)
                        .put(15, ChunkerVanillaItemType.BONE_MEAL)
                        .build()
        ));
        register(ItemMapping.of("minecraft:dye", ChunkerVanillaItemType.INK_SAC));
        register(ItemMapping.of("minecraft:spawn_egg", ChunkerVanillaItemType.SPAWN_EGG));
        register(ItemMapping.of("minecraft:record_13", ChunkerVanillaItemType.MUSIC_DISC_13));
        register(ItemMapping.of("minecraft:record_cat", ChunkerVanillaItemType.MUSIC_DISC_CAT));
        register(ItemMapping.of("minecraft:record_blocks", ChunkerVanillaItemType.MUSIC_DISC_BLOCKS));
        register(ItemMapping.of("minecraft:record_chirp", ChunkerVanillaItemType.MUSIC_DISC_CHIRP));
        register(ItemMapping.of("minecraft:record_far", ChunkerVanillaItemType.MUSIC_DISC_FAR));
        register(ItemMapping.of("minecraft:record_mall", ChunkerVanillaItemType.MUSIC_DISC_MALL));
        register(ItemMapping.of("minecraft:record_mellohi", ChunkerVanillaItemType.MUSIC_DISC_MELLOHI));
        register(ItemMapping.of("minecraft:record_stal", ChunkerVanillaItemType.MUSIC_DISC_STAL));
        register(ItemMapping.of("minecraft:record_strad", ChunkerVanillaItemType.MUSIC_DISC_STRAD));
        register(ItemMapping.of("minecraft:record_ward", ChunkerVanillaItemType.MUSIC_DISC_WARD));
        register(ItemMapping.of("minecraft:record_11", ChunkerVanillaItemType.MUSIC_DISC_11));
        register(ItemMapping.of("minecraft:record_wait", ChunkerVanillaItemType.MUSIC_DISC_WAIT));
        register(ItemMapping.of("minecraft:map", ChunkerVanillaItemType.MAP));
        register(ItemMapping.of("minecraft:filled_map", ChunkerVanillaItemType.FILLED_MAP));

        register(ItemMapping.of("minecraft:coal", ChunkerVanillaItemType.COAL));
        register(ItemMapping.of("minecraft:coal", 1, ChunkerVanillaItemType.CHARCOAL));
        register(ItemMapping.of("minecraft:fish", ChunkerVanillaItemType.COD));
        register(ItemMapping.of("minecraft:fish", 1, ChunkerVanillaItemType.SALMON));
        register(ItemMapping.of("minecraft:fish", 2, ChunkerVanillaItemType.TROPICAL_FISH));
        register(ItemMapping.of("minecraft:fish", 3, ChunkerVanillaItemType.PUFFERFISH));
        register(ItemMapping.of("minecraft:cooked_fish", ChunkerVanillaItemType.COOKED_COD));
        register(ItemMapping.of("minecraft:cooked_fish", 1, ChunkerVanillaItemType.COOKED_SALMON));
        register(ItemMapping.of("minecraft:fireworks", ChunkerVanillaItemType.FIREWORK_ROCKET));
        register(ItemMapping.of("minecraft:firework_charge", ChunkerVanillaItemType.FIREWORK_STAR));
        register(ItemMapping.of("minecraft:book", ChunkerVanillaItemType.BOOK));
        register(ItemMapping.of("minecraft:stick", ChunkerVanillaItemType.STICK));

        // Skulls
        register(ItemMapping.flatten("minecraft:skull",
                ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                        .put(1, ChunkerVanillaBlockType.WITHER_SKELETON_SKULL)
                        .put(2, ChunkerVanillaBlockType.ZOMBIE_HEAD)
                        .put(3, ChunkerVanillaBlockType.PLAYER_HEAD)
                        .put(4, ChunkerVanillaBlockType.CREEPER_HEAD)
                        .put(5, ChunkerVanillaBlockType.DRAGON_HEAD)
                        .put(6, ChunkerVanillaBlockType.PIGLIN_HEAD)
                        .build()
        ));
        register(ItemMapping.of("minecraft:skull", ChunkerVanillaBlockType.SKELETON_SKULL));
        register(ItemMapping.of("minecraft:apple", ChunkerVanillaItemType.APPLE));
        register(ItemMapping.of("minecraft:armor_stand", ChunkerVanillaItemType.ARMOR_STAND));
        register(ItemMapping.of("minecraft:baked_potato", ChunkerVanillaItemType.BAKED_POTATO));
        register(ItemMapping.of("minecraft:beef", ChunkerVanillaItemType.BEEF));
        register(ItemMapping.of("minecraft:blaze_powder", ChunkerVanillaItemType.BLAZE_POWDER));
        register(ItemMapping.of("minecraft:blaze_rod", ChunkerVanillaItemType.BLAZE_ROD));
        register(ItemMapping.of("minecraft:bone", ChunkerVanillaItemType.BONE));
        register(ItemMapping.of("minecraft:bow", ChunkerVanillaItemType.BOW));
        register(ItemMapping.of("minecraft:bowl", ChunkerVanillaItemType.BOWL));
        register(ItemMapping.of("minecraft:bread", ChunkerVanillaItemType.BREAD));
        register(ItemMapping.of("minecraft:brick", ChunkerVanillaItemType.BRICK));
        register(ItemMapping.of("minecraft:carrot", ChunkerVanillaItemType.CARROT));
        register(ItemMapping.of("minecraft:chainmail_boots", ChunkerVanillaItemType.CHAINMAIL_BOOTS));
        register(ItemMapping.of("minecraft:chainmail_chestplate", ChunkerVanillaItemType.CHAINMAIL_CHESTPLATE));
        register(ItemMapping.of("minecraft:chainmail_helmet", ChunkerVanillaItemType.CHAINMAIL_HELMET));
        register(ItemMapping.of("minecraft:chainmail_leggings", ChunkerVanillaItemType.CHAINMAIL_LEGGINGS));
        register(ItemMapping.of("minecraft:chest_minecart", ChunkerVanillaItemType.CHEST_MINECART));
        register(ItemMapping.of("minecraft:chicken", ChunkerVanillaItemType.CHICKEN));
        register(ItemMapping.of("minecraft:clay_ball", ChunkerVanillaItemType.CLAY_BALL));
        register(ItemMapping.of("minecraft:clock", ChunkerVanillaItemType.CLOCK));
        register(ItemMapping.of("minecraft:command_block_minecart", ChunkerVanillaItemType.COMMAND_BLOCK_MINECART));
        register(ItemMapping.of("minecraft:compass", ChunkerVanillaItemType.COMPASS));
        register(ItemMapping.of("minecraft:cooked_beef", ChunkerVanillaItemType.COOKED_BEEF));
        register(ItemMapping.of("minecraft:cooked_chicken", ChunkerVanillaItemType.COOKED_CHICKEN));
        register(ItemMapping.of("minecraft:cooked_mutton", ChunkerVanillaItemType.COOKED_MUTTON));
        register(ItemMapping.of("minecraft:cooked_porkchop", ChunkerVanillaItemType.COOKED_PORKCHOP));
        register(ItemMapping.of("minecraft:cooked_rabbit", ChunkerVanillaItemType.COOKED_RABBIT));
        register(ItemMapping.of("minecraft:cookie", ChunkerVanillaItemType.COOKIE));
        register(ItemMapping.of("minecraft:diamond", ChunkerVanillaItemType.DIAMOND));
        register(ItemMapping.of("minecraft:diamond_axe", ChunkerVanillaItemType.DIAMOND_AXE));
        register(ItemMapping.of("minecraft:diamond_boots", ChunkerVanillaItemType.DIAMOND_BOOTS));
        register(ItemMapping.of("minecraft:diamond_chestplate", ChunkerVanillaItemType.DIAMOND_CHESTPLATE));
        register(ItemMapping.of("minecraft:diamond_helmet", ChunkerVanillaItemType.DIAMOND_HELMET));
        register(ItemMapping.of("minecraft:diamond_hoe", ChunkerVanillaItemType.DIAMOND_HOE));
        register(ItemMapping.of("minecraft:diamond_leggings", ChunkerVanillaItemType.DIAMOND_LEGGINGS));
        register(ItemMapping.of("minecraft:diamond_pickaxe", ChunkerVanillaItemType.DIAMOND_PICKAXE));
        register(ItemMapping.of("minecraft:diamond_shovel", ChunkerVanillaItemType.DIAMOND_SHOVEL));
        register(ItemMapping.of("minecraft:diamond_sword", ChunkerVanillaItemType.DIAMOND_SWORD));
        register(ItemMapping.of("minecraft:egg", ChunkerVanillaItemType.EGG));
        register(ItemMapping.of("minecraft:emerald", ChunkerVanillaItemType.EMERALD));
        register(ItemMapping.of("minecraft:enchanted_book", ChunkerVanillaItemType.ENCHANTED_BOOK));
        register(ItemMapping.of("minecraft:ender_eye", ChunkerVanillaItemType.ENDER_EYE));
        register(ItemMapping.of("minecraft:ender_pearl", ChunkerVanillaItemType.ENDER_PEARL));
        register(ItemMapping.of("minecraft:experience_bottle", ChunkerVanillaItemType.EXPERIENCE_BOTTLE));
        register(ItemMapping.of("minecraft:feather", ChunkerVanillaItemType.FEATHER));
        register(ItemMapping.of("minecraft:fermented_spider_eye", ChunkerVanillaItemType.FERMENTED_SPIDER_EYE));
        register(ItemMapping.of("minecraft:fishing_rod", ChunkerVanillaItemType.FISHING_ROD));
        register(ItemMapping.of("minecraft:flint", ChunkerVanillaItemType.FLINT));
        register(ItemMapping.of("minecraft:flint_and_steel", ChunkerVanillaItemType.FLINT_AND_STEEL));
        register(ItemMapping.of("minecraft:ghast_tear", ChunkerVanillaItemType.GHAST_TEAR));
        register(ItemMapping.of("minecraft:glass_bottle", ChunkerVanillaItemType.GLASS_BOTTLE));
        register(ItemMapping.of("minecraft:glowstone_dust", ChunkerVanillaItemType.GLOWSTONE_DUST));
        register(ItemMapping.of("minecraft:gold_ingot", ChunkerVanillaItemType.GOLD_INGOT));
        register(ItemMapping.of("minecraft:gold_nugget", ChunkerVanillaItemType.GOLD_NUGGET));
        register(ItemMapping.of("minecraft:golden_apple", ChunkerVanillaItemType.GOLDEN_APPLE));
        register(ItemMapping.of("minecraft:golden_apple", 1, ChunkerVanillaItemType.ENCHANTED_GOLDEN_APPLE));
        register(ItemMapping.of("minecraft:golden_axe", ChunkerVanillaItemType.GOLDEN_AXE));
        register(ItemMapping.of("minecraft:golden_boots", ChunkerVanillaItemType.GOLDEN_BOOTS));
        register(ItemMapping.of("minecraft:golden_carrot", ChunkerVanillaItemType.GOLDEN_CARROT));
        register(ItemMapping.of("minecraft:golden_chestplate", ChunkerVanillaItemType.GOLDEN_CHESTPLATE));
        register(ItemMapping.of("minecraft:golden_helmet", ChunkerVanillaItemType.GOLDEN_HELMET));
        register(ItemMapping.of("minecraft:golden_hoe", ChunkerVanillaItemType.GOLDEN_HOE));
        register(ItemMapping.of("minecraft:golden_leggings", ChunkerVanillaItemType.GOLDEN_LEGGINGS));
        register(ItemMapping.of("minecraft:golden_pickaxe", ChunkerVanillaItemType.GOLDEN_PICKAXE));
        register(ItemMapping.of("minecraft:golden_shovel", ChunkerVanillaItemType.GOLDEN_SHOVEL));
        register(ItemMapping.of("minecraft:golden_sword", ChunkerVanillaItemType.GOLDEN_SWORD));
        register(ItemMapping.of("minecraft:gunpowder", ChunkerVanillaItemType.GUNPOWDER));
        register(ItemMapping.of("minecraft:hopper_minecart", ChunkerVanillaItemType.HOPPER_MINECART));
        register(ItemMapping.of("minecraft:iron_axe", ChunkerVanillaItemType.IRON_AXE));
        register(ItemMapping.of("minecraft:iron_boots", ChunkerVanillaItemType.IRON_BOOTS));
        register(ItemMapping.of("minecraft:iron_chestplate", ChunkerVanillaItemType.IRON_CHESTPLATE));
        register(ItemMapping.of("minecraft:iron_helmet", ChunkerVanillaItemType.IRON_HELMET));
        register(ItemMapping.of("minecraft:iron_hoe", ChunkerVanillaItemType.IRON_HOE));
        register(ItemMapping.of("minecraft:iron_ingot", ChunkerVanillaItemType.IRON_INGOT));
        register(ItemMapping.of("minecraft:iron_leggings", ChunkerVanillaItemType.IRON_LEGGINGS));
        register(ItemMapping.of("minecraft:iron_pickaxe", ChunkerVanillaItemType.IRON_PICKAXE));
        register(ItemMapping.of("minecraft:iron_shovel", ChunkerVanillaItemType.IRON_SHOVEL));
        register(ItemMapping.of("minecraft:iron_sword", ChunkerVanillaItemType.IRON_SWORD));
        register(ItemMapping.of("minecraft:lead", ChunkerVanillaItemType.LEAD));
        register(ItemMapping.of("minecraft:leather", ChunkerVanillaItemType.LEATHER));
        register(ItemMapping.of("minecraft:leather_boots", ChunkerVanillaItemType.LEATHER_BOOTS));
        register(ItemMapping.of("minecraft:leather_chestplate", ChunkerVanillaItemType.LEATHER_CHESTPLATE));
        register(ItemMapping.of("minecraft:leather_helmet", ChunkerVanillaItemType.LEATHER_HELMET));
        register(ItemMapping.of("minecraft:leather_leggings", ChunkerVanillaItemType.LEATHER_LEGGINGS));
        register(ItemMapping.of("minecraft:magma_cream", ChunkerVanillaItemType.MAGMA_CREAM));
        register(ItemMapping.of("minecraft:melon_seeds", ChunkerVanillaItemType.MELON_SEEDS));
        register(ItemMapping.of("minecraft:mushroom_stew", ChunkerVanillaItemType.MUSHROOM_STEW));
        register(ItemMapping.of("minecraft:mutton", ChunkerVanillaItemType.MUTTON));
        register(ItemMapping.of("minecraft:name_tag", ChunkerVanillaItemType.NAME_TAG));
        register(ItemMapping.of("minecraft:nether_wart", ChunkerVanillaItemType.NETHER_WART));
        register(ItemMapping.of("minecraft:painting", ChunkerVanillaItemType.PAINTING));
        register(ItemMapping.of("minecraft:paper", ChunkerVanillaItemType.PAPER));
        register(ItemMapping.of("minecraft:poisonous_potato", ChunkerVanillaItemType.POISONOUS_POTATO));
        register(ItemMapping.of("minecraft:porkchop", ChunkerVanillaItemType.PORKCHOP));
        register(ItemMapping.of("minecraft:potato", ChunkerVanillaItemType.POTATO));
        register(ItemMapping.of("minecraft:prismarine_shard", ChunkerVanillaItemType.PRISMARINE_SHARD));
        register(ItemMapping.of("minecraft:prismarine_crystals", ChunkerVanillaItemType.PRISMARINE_CRYSTALS));
        register(ItemMapping.of("minecraft:pumpkin_pie", ChunkerVanillaItemType.PUMPKIN_PIE));
        register(ItemMapping.of("minecraft:pumpkin_seeds", ChunkerVanillaItemType.PUMPKIN_SEEDS));
        register(ItemMapping.of("minecraft:quartz", ChunkerVanillaItemType.QUARTZ));
        register(ItemMapping.of("minecraft:rabbit", ChunkerVanillaItemType.RABBIT));
        register(ItemMapping.of("minecraft:rabbit_foot", ChunkerVanillaItemType.RABBIT_FOOT));
        register(ItemMapping.of("minecraft:rabbit_hide", ChunkerVanillaItemType.RABBIT_HIDE));
        register(ItemMapping.of("minecraft:rabbit_stew", ChunkerVanillaItemType.RABBIT_STEW));
        register(ItemMapping.of("minecraft:rotten_flesh", ChunkerVanillaItemType.ROTTEN_FLESH));
        register(ItemMapping.of("minecraft:saddle", ChunkerVanillaItemType.SADDLE));
        register(ItemMapping.of("minecraft:shears", ChunkerVanillaItemType.SHEARS));
        register(ItemMapping.of("minecraft:slime_ball", ChunkerVanillaItemType.SLIME_BALL));
        register(ItemMapping.of("minecraft:snowball", ChunkerVanillaItemType.SNOWBALL));
        register(ItemMapping.of("minecraft:spider_eye", ChunkerVanillaItemType.SPIDER_EYE));
        register(ItemMapping.of("minecraft:stone_axe", ChunkerVanillaItemType.STONE_AXE));
        register(ItemMapping.of("minecraft:stone_hoe", ChunkerVanillaItemType.STONE_HOE));
        register(ItemMapping.of("minecraft:stone_pickaxe", ChunkerVanillaItemType.STONE_PICKAXE));
        register(ItemMapping.of("minecraft:stone_shovel", ChunkerVanillaItemType.STONE_SHOVEL));
        register(ItemMapping.of("minecraft:stone_sword", ChunkerVanillaItemType.STONE_SWORD));
        register(ItemMapping.of("minecraft:string", ChunkerVanillaItemType.STRING));
        register(ItemMapping.of("minecraft:sugar", ChunkerVanillaItemType.SUGAR));
        register(ItemMapping.of("minecraft:tnt_minecart", ChunkerVanillaItemType.TNT_MINECART));
        register(ItemMapping.of("minecraft:wheat", ChunkerVanillaItemType.WHEAT));
        register(ItemMapping.of("minecraft:wheat_seeds", ChunkerVanillaItemType.WHEAT_SEEDS));
        register(ItemMapping.of("minecraft:wooden_axe", ChunkerVanillaItemType.WOODEN_AXE));
        register(ItemMapping.of("minecraft:wooden_hoe", ChunkerVanillaItemType.WOODEN_HOE));
        register(ItemMapping.of("minecraft:wooden_pickaxe", ChunkerVanillaItemType.WOODEN_PICKAXE));
        register(ItemMapping.of("minecraft:wooden_shovel", ChunkerVanillaItemType.WOODEN_SHOVEL));
        register(ItemMapping.of("minecraft:wooden_sword", ChunkerVanillaItemType.WOODEN_SWORD));
        register(ItemMapping.of("minecraft:writable_book", ChunkerVanillaItemType.WRITABLE_BOOK));
        register(ItemMapping.of("minecraft:written_book", ChunkerVanillaItemType.WRITTEN_BOOK));

        // Anvils
        register(ItemMapping.of("minecraft:anvil", 1, ChunkerVanillaBlockType.CHIPPED_ANVIL));
        register(ItemMapping.of("minecraft:anvil", 2, ChunkerVanillaBlockType.DAMAGED_ANVIL));

        // Torch
        register(ItemMapping.of("minecraft:torch", ChunkerVanillaBlockType.TORCH));

        // Use lit redstone torch by default
        register(ItemMapping.of("minecraft:redstone_torch", ChunkerVanillaBlockType.REDSTONE_TORCH, VanillaBlockStates.LIT, Bool.TRUE));
        registerDuplicateInput(ItemMapping.of("minecraft:redstone_torch", ChunkerVanillaBlockType.REDSTONE_TORCH));

        register(ItemMapping.of("minecraft:comparator", ChunkerVanillaBlockType.COMPARATOR));
        register(ItemMapping.of("minecraft:repeater", ChunkerVanillaBlockType.REPEATER));

        // Wood doesn't exist, we have to explicitly map it as it does exist as a block
        registerDuplicateInput(ItemMapping.of("minecraft:air", ChunkerVanillaBlockType.OAK_WOOD));
        registerDuplicateInput(ItemMapping.of("minecraft:air", ChunkerVanillaBlockType.SPRUCE_WOOD));
        registerDuplicateInput(ItemMapping.of("minecraft:air", ChunkerVanillaBlockType.BIRCH_WOOD));
        registerDuplicateInput(ItemMapping.of("minecraft:air", ChunkerVanillaBlockType.JUNGLE_WOOD));
        registerDuplicateInput(ItemMapping.of("minecraft:air", ChunkerVanillaBlockType.ACACIA_WOOD));
        registerDuplicateInput(ItemMapping.of("minecraft:air", ChunkerVanillaBlockType.DARK_OAK_WOOD));

        // Mushroom blocks don't exist as items
        registerDuplicateInput(ItemMapping.of("minecraft:air", ChunkerVanillaBlockType.MUSHROOM_STEM));
        registerDuplicateInput(ItemMapping.of("minecraft:air", ChunkerVanillaBlockType.RED_MUSHROOM_BLOCK));
        registerDuplicateInput(ItemMapping.of("minecraft:air", ChunkerVanillaBlockType.BROWN_MUSHROOM_BLOCK));

        // Petrified oak slab doesn't exist either
        registerDuplicateInput(ItemMapping.of("minecraft:air", ChunkerVanillaBlockType.PETRIFIED_OAK_SLAB));

        // 1.9
        if (version.isGreaterThanOrEqual(1, 9, 0)) {
            register(ItemMapping.of("minecraft:beetroot", ChunkerVanillaItemType.BEETROOT));
            register(ItemMapping.of("minecraft:beetroot_soup", ChunkerVanillaItemType.BEETROOT_SOUP));
            register(ItemMapping.of("minecraft:beetroot_seeds", ChunkerVanillaItemType.BEETROOT_SEEDS));
            register(ItemMapping.of("minecraft:chorus_fruit", ChunkerVanillaItemType.CHORUS_FRUIT));
            register(ItemMapping.of("minecraft:chorus_fruit_popped", ChunkerVanillaItemType.POPPED_CHORUS_FRUIT));
            register(ItemMapping.of("minecraft:dragon_breath", ChunkerVanillaItemType.DRAGON_BREATH));
            register(ItemMapping.of("minecraft:end_crystal", ChunkerVanillaItemType.END_CRYSTAL));
            register(ItemMapping.of("minecraft:elytra", ChunkerVanillaItemType.ELYTRA));
            register(ItemMapping.of("minecraft:shield", ChunkerVanillaItemType.SHIELD));

            // Potions were changed
            registerOverrideOutput(ItemMapping.of("minecraft:splash_potion", ChunkerVanillaItemType.SPLASH_POTION));
            register(ItemMapping.of("minecraft:lingering_potion", ChunkerVanillaItemType.LINGERING_POTION));

            // Arrows
            register(ItemMapping.of("minecraft:tipped_arrow", ChunkerVanillaItemType.TIPPED_ARROW));
            register(ItemMapping.of("minecraft:spectral_arrow", ChunkerVanillaItemType.SPECTRAL_ARROW));

            // Boats
            register(ItemMapping.of("minecraft:spruce_boat", ChunkerVanillaItemType.SPRUCE_BOAT));
            register(ItemMapping.of("minecraft:birch_boat", ChunkerVanillaItemType.BIRCH_BOAT));
            register(ItemMapping.of("minecraft:jungle_boat", ChunkerVanillaItemType.JUNGLE_BOAT));
            register(ItemMapping.of("minecraft:acacia_boat", ChunkerVanillaItemType.ACACIA_BOAT));
            register(ItemMapping.of("minecraft:dark_oak_boat", ChunkerVanillaItemType.DARK_OAK_BOAT));
        }

        // 1.11
        if (version.isGreaterThanOrEqual(1, 11, 0)) {
            register(ItemMapping.of("minecraft:shulker_shell", ChunkerVanillaItemType.SHULKER_SHELL));
            register(ItemMapping.of("minecraft:totem_of_undying", ChunkerVanillaItemType.TOTEM_OF_UNDYING));
            register(ItemMapping.of("minecraft:iron_nugget", ChunkerVanillaItemType.IRON_NUGGET));
        }

        // 1.12
        if (version.isGreaterThanOrEqual(1, 12, 0)) {
            // Beds
            removeOutputMapping(ChunkerVanillaBlockType.RED_BED);
            registerOverrideInput(ItemMapping.of("minecraft:bed", ChunkerVanillaBlockType.WHITE_BED));
            register(ItemMapping.flatten("minecraft:bed",
                    ImmutableMultimap.<Integer, ChunkerVanillaBlockType>builder()
                            .put(1, ChunkerVanillaBlockType.ORANGE_BED)
                            .put(2, ChunkerVanillaBlockType.MAGENTA_BED)
                            .put(3, ChunkerVanillaBlockType.LIGHT_BLUE_BED)
                            .put(4, ChunkerVanillaBlockType.YELLOW_BED)
                            .put(5, ChunkerVanillaBlockType.LIME_BED)
                            .put(6, ChunkerVanillaBlockType.PINK_BED)
                            .put(7, ChunkerVanillaBlockType.GRAY_BED)
                            .put(8, ChunkerVanillaBlockType.LIGHT_GRAY_BED)
                            .put(9, ChunkerVanillaBlockType.CYAN_BED)
                            .put(10, ChunkerVanillaBlockType.PURPLE_BED)
                            .put(11, ChunkerVanillaBlockType.BLUE_BED)
                            .put(12, ChunkerVanillaBlockType.BROWN_BED)
                            .put(13, ChunkerVanillaBlockType.GREEN_BED)
                            .put(14, ChunkerVanillaBlockType.RED_BED)
                            .put(15, ChunkerVanillaBlockType.BLACK_BED)
                            .build()
            ));

            register(ItemMapping.of("minecraft:knowledge_book", ChunkerVanillaItemType.KNOWLEDGE_BOOK));
        }
    }
}
