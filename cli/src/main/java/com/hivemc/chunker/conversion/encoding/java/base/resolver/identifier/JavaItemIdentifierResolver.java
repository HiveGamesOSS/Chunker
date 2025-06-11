package com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier;

import com.google.common.collect.ImmutableMultimap;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.ChunkerItemIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.base.resolver.identifier.ItemMapping;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerVanillaItemType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemProperty;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;

/**
 * Resolver to convert between Java item identifiers and ChunkerItemStackIdentifier.
 */
public class JavaItemIdentifierResolver extends ChunkerItemIdentifierResolver {
    /**
     * Create a new Java item identifier resolver.
     *
     * @param converter the converter instance.
     * @param version   the version being resolved.
     * @param reader    whether this is used for the reader.
     */
    public JavaItemIdentifierResolver(Converter converter, Version version, boolean reader) {
        super(converter, version, reader, false);
    }

    @Override
    public void registerMappings(Version version) {
        register(ItemMapping.of("minecraft:acacia_boat", ChunkerVanillaItemType.ACACIA_BOAT));
        register(ItemMapping.of("minecraft:apple", ChunkerVanillaItemType.APPLE));
        register(ItemMapping.of("minecraft:armor_stand", ChunkerVanillaItemType.ARMOR_STAND));
        register(ItemMapping.of("minecraft:arrow", ChunkerVanillaItemType.ARROW));
        register(ItemMapping.of("minecraft:baked_potato", ChunkerVanillaItemType.BAKED_POTATO));
        register(ItemMapping.of("minecraft:bat_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.BAT));
        register(ItemMapping.of("minecraft:beef", ChunkerVanillaItemType.BEEF));
        register(ItemMapping.of("minecraft:beetroot", ChunkerVanillaItemType.BEETROOT));
        register(ItemMapping.of("minecraft:beetroot_seeds", ChunkerVanillaItemType.BEETROOT_SEEDS));
        register(ItemMapping.of("minecraft:beetroot_soup", ChunkerVanillaItemType.BEETROOT_SOUP));
        register(ItemMapping.of("minecraft:birch_boat", ChunkerVanillaItemType.BIRCH_BOAT));
        register(ItemMapping.of("minecraft:blaze_powder", ChunkerVanillaItemType.BLAZE_POWDER));
        register(ItemMapping.of("minecraft:blaze_rod", ChunkerVanillaItemType.BLAZE_ROD));
        register(ItemMapping.of("minecraft:blaze_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.BLAZE));
        register(ItemMapping.of("minecraft:bone", ChunkerVanillaItemType.BONE));
        register(ItemMapping.of("minecraft:bone_meal", ChunkerVanillaItemType.BONE_MEAL));
        register(ItemMapping.of("minecraft:book", ChunkerVanillaItemType.BOOK));
        register(ItemMapping.of("minecraft:bow", ChunkerVanillaItemType.BOW));
        register(ItemMapping.of("minecraft:bowl", ChunkerVanillaItemType.BOWL));
        register(ItemMapping.of("minecraft:bread", ChunkerVanillaItemType.BREAD));
        register(ItemMapping.of("minecraft:brick", ChunkerVanillaItemType.BRICK));
        register(ItemMapping.of("minecraft:bucket", ChunkerVanillaItemType.BUCKET));
        register(ItemMapping.of("minecraft:cactus_green", ChunkerVanillaItemType.GREEN_DYE));
        register(ItemMapping.of("minecraft:carrot", ChunkerVanillaItemType.CARROT));
        register(ItemMapping.of("minecraft:carrot_on_a_stick", ChunkerVanillaItemType.CARROT_ON_A_STICK));
        register(ItemMapping.of("minecraft:cave_spider_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.CAVE_SPIDER));
        register(ItemMapping.of("minecraft:chainmail_boots", ChunkerVanillaItemType.CHAINMAIL_BOOTS));
        register(ItemMapping.of("minecraft:chainmail_chestplate", ChunkerVanillaItemType.CHAINMAIL_CHESTPLATE));
        register(ItemMapping.of("minecraft:chainmail_helmet", ChunkerVanillaItemType.CHAINMAIL_HELMET));
        register(ItemMapping.of("minecraft:chainmail_leggings", ChunkerVanillaItemType.CHAINMAIL_LEGGINGS));
        register(ItemMapping.of("minecraft:charcoal", ChunkerVanillaItemType.CHARCOAL));
        register(ItemMapping.of("minecraft:chest_minecart", ChunkerVanillaItemType.CHEST_MINECART));
        register(ItemMapping.of("minecraft:chicken", ChunkerVanillaItemType.CHICKEN));
        register(ItemMapping.of("minecraft:chicken_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.CHICKEN));
        register(ItemMapping.of("minecraft:chorus_fruit", ChunkerVanillaItemType.CHORUS_FRUIT));
        register(ItemMapping.of("minecraft:clay_ball", ChunkerVanillaItemType.CLAY_BALL));
        register(ItemMapping.of("minecraft:clock", ChunkerVanillaItemType.CLOCK));
        register(ItemMapping.of("minecraft:coal", ChunkerVanillaItemType.COAL));
        register(ItemMapping.of("minecraft:cocoa_beans", ChunkerVanillaItemType.COCOA_BEANS));
        register(ItemMapping.of("minecraft:cod", ChunkerVanillaItemType.COD));
        register(ItemMapping.of("minecraft:cod_bucket", ChunkerVanillaItemType.COD_BUCKET));
        register(ItemMapping.of("minecraft:cod_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.COD));
        register(ItemMapping.of("minecraft:command_block_minecart", ChunkerVanillaItemType.COMMAND_BLOCK_MINECART));
        register(ItemMapping.of("minecraft:compass", ChunkerVanillaItemType.COMPASS));
        registerDuplicateInput(ItemMapping.of("minecraft:compass", ChunkerVanillaItemType.LODESTONE_COMPASS)); // Just a compass in Java
        register(ItemMapping.of("minecraft:cooked_beef", ChunkerVanillaItemType.COOKED_BEEF));
        register(ItemMapping.of("minecraft:cooked_chicken", ChunkerVanillaItemType.COOKED_CHICKEN));
        register(ItemMapping.of("minecraft:cooked_cod", ChunkerVanillaItemType.COOKED_COD));
        register(ItemMapping.of("minecraft:cooked_mutton", ChunkerVanillaItemType.COOKED_MUTTON));
        register(ItemMapping.of("minecraft:cooked_porkchop", ChunkerVanillaItemType.COOKED_PORKCHOP));
        register(ItemMapping.of("minecraft:cooked_rabbit", ChunkerVanillaItemType.COOKED_RABBIT));
        register(ItemMapping.of("minecraft:cooked_salmon", ChunkerVanillaItemType.COOKED_SALMON));
        register(ItemMapping.of("minecraft:cookie", ChunkerVanillaItemType.COOKIE));
        register(ItemMapping.of("minecraft:cow_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.COW));
        register(ItemMapping.of("minecraft:creeper_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.CREEPER));
        register(ItemMapping.of("minecraft:cyan_dye", ChunkerVanillaItemType.CYAN_DYE));
        register(ItemMapping.of("minecraft:dandelion_yellow", ChunkerVanillaItemType.YELLOW_DYE));
        register(ItemMapping.of("minecraft:dark_oak_boat", ChunkerVanillaItemType.DARK_OAK_BOAT));
        register(ItemMapping.of("minecraft:debug_stick", ChunkerVanillaItemType.DEBUG_STICK));
        register(ItemMapping.of("minecraft:diamond", ChunkerVanillaItemType.DIAMOND));
        register(ItemMapping.of("minecraft:diamond_axe", ChunkerVanillaItemType.DIAMOND_AXE));
        register(ItemMapping.of("minecraft:diamond_boots", ChunkerVanillaItemType.DIAMOND_BOOTS));
        register(ItemMapping.of("minecraft:diamond_chestplate", ChunkerVanillaItemType.DIAMOND_CHESTPLATE));
        register(ItemMapping.of("minecraft:diamond_helmet", ChunkerVanillaItemType.DIAMOND_HELMET));
        register(ItemMapping.of("minecraft:diamond_hoe", ChunkerVanillaItemType.DIAMOND_HOE));
        register(ItemMapping.of("minecraft:diamond_horse_armor", ChunkerVanillaItemType.DIAMOND_HORSE_ARMOR));
        register(ItemMapping.of("minecraft:diamond_leggings", ChunkerVanillaItemType.DIAMOND_LEGGINGS));
        register(ItemMapping.of("minecraft:diamond_pickaxe", ChunkerVanillaItemType.DIAMOND_PICKAXE));
        register(ItemMapping.of("minecraft:diamond_shovel", ChunkerVanillaItemType.DIAMOND_SHOVEL));
        register(ItemMapping.of("minecraft:diamond_sword", ChunkerVanillaItemType.DIAMOND_SWORD));
        register(ItemMapping.of("minecraft:dolphin_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.DOLPHIN));
        register(ItemMapping.of("minecraft:donkey_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.DONKEY));
        register(ItemMapping.of("minecraft:dragon_breath", ChunkerVanillaItemType.DRAGON_BREATH));
        register(ItemMapping.of("minecraft:dried_kelp", ChunkerVanillaItemType.DRIED_KELP));
        register(ItemMapping.of("minecraft:drowned_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.DROWNED));
        register(ItemMapping.of("minecraft:egg", ChunkerVanillaItemType.EGG));
        register(ItemMapping.of("minecraft:elder_guardian_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ELDER_GUARDIAN));
        register(ItemMapping.of("minecraft:elytra", ChunkerVanillaItemType.ELYTRA));
        register(ItemMapping.of("minecraft:emerald", ChunkerVanillaItemType.EMERALD));
        register(ItemMapping.of("minecraft:enchanted_book", ChunkerVanillaItemType.ENCHANTED_BOOK));
        register(ItemMapping.of("minecraft:enchanted_golden_apple", ChunkerVanillaItemType.ENCHANTED_GOLDEN_APPLE));
        register(ItemMapping.of("minecraft:end_crystal", ChunkerVanillaItemType.END_CRYSTAL));
        register(ItemMapping.of("minecraft:ender_eye", ChunkerVanillaItemType.ENDER_EYE));
        register(ItemMapping.of("minecraft:ender_pearl", ChunkerVanillaItemType.ENDER_PEARL));
        register(ItemMapping.of("minecraft:enderman_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ENDERMAN));
        register(ItemMapping.of("minecraft:endermite_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ENDERMITE));
        register(ItemMapping.of("minecraft:evoker_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.EVOKER));
        register(ItemMapping.of("minecraft:experience_bottle", ChunkerVanillaItemType.EXPERIENCE_BOTTLE));
        register(ItemMapping.of("minecraft:feather", ChunkerVanillaItemType.FEATHER));
        register(ItemMapping.of("minecraft:fermented_spider_eye", ChunkerVanillaItemType.FERMENTED_SPIDER_EYE));
        register(ItemMapping.of("minecraft:filled_map", ChunkerVanillaItemType.FILLED_MAP));
        register(ItemMapping.of("minecraft:fire_charge", ChunkerVanillaItemType.FIRE_CHARGE));
        register(ItemMapping.of("minecraft:firework_rocket", ChunkerVanillaItemType.FIREWORK_ROCKET));
        register(ItemMapping.of("minecraft:firework_star", ChunkerVanillaItemType.FIREWORK_STAR));
        register(ItemMapping.of("minecraft:fishing_rod", ChunkerVanillaItemType.FISHING_ROD));
        register(ItemMapping.of("minecraft:flint", ChunkerVanillaItemType.FLINT));
        register(ItemMapping.of("minecraft:flint_and_steel", ChunkerVanillaItemType.FLINT_AND_STEEL));
        register(ItemMapping.of("minecraft:furnace_minecart", ChunkerVanillaItemType.FURNACE_MINECART));
        register(ItemMapping.of("minecraft:ghast_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.GHAST));
        register(ItemMapping.of("minecraft:ghast_tear", ChunkerVanillaItemType.GHAST_TEAR));
        register(ItemMapping.of("minecraft:glass_bottle", ChunkerVanillaItemType.GLASS_BOTTLE));
        register(ItemMapping.of("minecraft:glistering_melon_slice", ChunkerVanillaItemType.GLISTERING_MELON_SLICE));
        register(ItemMapping.of("minecraft:glowstone_dust", ChunkerVanillaItemType.GLOWSTONE_DUST));
        register(ItemMapping.of("minecraft:gold_ingot", ChunkerVanillaItemType.GOLD_INGOT));
        register(ItemMapping.of("minecraft:gold_nugget", ChunkerVanillaItemType.GOLD_NUGGET));
        register(ItemMapping.of("minecraft:golden_apple", ChunkerVanillaItemType.GOLDEN_APPLE));
        register(ItemMapping.of("minecraft:golden_axe", ChunkerVanillaItemType.GOLDEN_AXE));
        register(ItemMapping.of("minecraft:golden_boots", ChunkerVanillaItemType.GOLDEN_BOOTS));
        register(ItemMapping.of("minecraft:golden_carrot", ChunkerVanillaItemType.GOLDEN_CARROT));
        register(ItemMapping.of("minecraft:golden_chestplate", ChunkerVanillaItemType.GOLDEN_CHESTPLATE));
        register(ItemMapping.of("minecraft:golden_helmet", ChunkerVanillaItemType.GOLDEN_HELMET));
        register(ItemMapping.of("minecraft:golden_hoe", ChunkerVanillaItemType.GOLDEN_HOE));
        register(ItemMapping.of("minecraft:golden_horse_armor", ChunkerVanillaItemType.GOLDEN_HORSE_ARMOR));
        register(ItemMapping.of("minecraft:golden_leggings", ChunkerVanillaItemType.GOLDEN_LEGGINGS));
        register(ItemMapping.of("minecraft:golden_pickaxe", ChunkerVanillaItemType.GOLDEN_PICKAXE));
        register(ItemMapping.of("minecraft:golden_shovel", ChunkerVanillaItemType.GOLDEN_SHOVEL));
        register(ItemMapping.of("minecraft:golden_sword", ChunkerVanillaItemType.GOLDEN_SWORD));
        register(ItemMapping.of("minecraft:gray_dye", ChunkerVanillaItemType.GRAY_DYE));
        register(ItemMapping.of("minecraft:guardian_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.GUARDIAN));
        register(ItemMapping.of("minecraft:gunpowder", ChunkerVanillaItemType.GUNPOWDER));
        register(ItemMapping.of("minecraft:heart_of_the_sea", ChunkerVanillaItemType.HEART_OF_THE_SEA));
        register(ItemMapping.of("minecraft:hopper_minecart", ChunkerVanillaItemType.HOPPER_MINECART));
        register(ItemMapping.of("minecraft:horse_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.HORSE));
        register(ItemMapping.of("minecraft:husk_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.HUSK));
        register(ItemMapping.of("minecraft:ink_sac", ChunkerVanillaItemType.INK_SAC));
        register(ItemMapping.of("minecraft:iron_axe", ChunkerVanillaItemType.IRON_AXE));
        register(ItemMapping.of("minecraft:iron_boots", ChunkerVanillaItemType.IRON_BOOTS));
        register(ItemMapping.of("minecraft:iron_chestplate", ChunkerVanillaItemType.IRON_CHESTPLATE));
        register(ItemMapping.of("minecraft:iron_helmet", ChunkerVanillaItemType.IRON_HELMET));
        register(ItemMapping.of("minecraft:iron_hoe", ChunkerVanillaItemType.IRON_HOE));
        register(ItemMapping.of("minecraft:iron_horse_armor", ChunkerVanillaItemType.IRON_HORSE_ARMOR));
        register(ItemMapping.of("minecraft:iron_ingot", ChunkerVanillaItemType.IRON_INGOT));
        register(ItemMapping.of("minecraft:iron_leggings", ChunkerVanillaItemType.IRON_LEGGINGS));
        register(ItemMapping.of("minecraft:iron_nugget", ChunkerVanillaItemType.IRON_NUGGET));
        register(ItemMapping.of("minecraft:iron_pickaxe", ChunkerVanillaItemType.IRON_PICKAXE));
        register(ItemMapping.of("minecraft:iron_shovel", ChunkerVanillaItemType.IRON_SHOVEL));
        register(ItemMapping.of("minecraft:iron_sword", ChunkerVanillaItemType.IRON_SWORD));
        register(ItemMapping.of("minecraft:item_frame", ChunkerVanillaItemType.ITEM_FRAME));
        register(ItemMapping.of("minecraft:jungle_boat", ChunkerVanillaItemType.JUNGLE_BOAT));
        register(ItemMapping.of("minecraft:knowledge_book", ChunkerVanillaItemType.KNOWLEDGE_BOOK));
        register(ItemMapping.of("minecraft:lapis_lazuli", ChunkerVanillaItemType.LAPIS_LAZULI));
        register(ItemMapping.of("minecraft:lava_bucket", ChunkerVanillaItemType.LAVA_BUCKET));
        register(ItemMapping.of("minecraft:lead", ChunkerVanillaItemType.LEAD));
        register(ItemMapping.of("minecraft:leather", ChunkerVanillaItemType.LEATHER));
        register(ItemMapping.of("minecraft:leather_boots", ChunkerVanillaItemType.LEATHER_BOOTS));
        register(ItemMapping.of("minecraft:leather_chestplate", ChunkerVanillaItemType.LEATHER_CHESTPLATE));
        register(ItemMapping.of("minecraft:leather_helmet", ChunkerVanillaItemType.LEATHER_HELMET));
        register(ItemMapping.of("minecraft:leather_leggings", ChunkerVanillaItemType.LEATHER_LEGGINGS));
        register(ItemMapping.of("minecraft:light_blue_dye", ChunkerVanillaItemType.LIGHT_BLUE_DYE));
        register(ItemMapping.of("minecraft:light_gray_dye", ChunkerVanillaItemType.LIGHT_GRAY_DYE));
        register(ItemMapping.of("minecraft:lime_dye", ChunkerVanillaItemType.LIME_DYE));
        register(ItemMapping.of("minecraft:lingering_potion", ChunkerVanillaItemType.LINGERING_POTION));
        register(ItemMapping.of("minecraft:llama_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.LLAMA));
        register(ItemMapping.of("minecraft:magenta_dye", ChunkerVanillaItemType.MAGENTA_DYE));
        register(ItemMapping.of("minecraft:magma_cream", ChunkerVanillaItemType.MAGMA_CREAM));
        register(ItemMapping.of("minecraft:magma_cube_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.MAGMA_CUBE));
        register(ItemMapping.of("minecraft:map", ChunkerVanillaItemType.MAP));
        register(ItemMapping.of("minecraft:melon_seeds", ChunkerVanillaItemType.MELON_SEEDS));
        register(ItemMapping.of("minecraft:melon_slice", ChunkerVanillaItemType.MELON_SLICE));
        register(ItemMapping.of("minecraft:milk_bucket", ChunkerVanillaItemType.MILK_BUCKET));
        register(ItemMapping.of("minecraft:minecart", ChunkerVanillaItemType.MINECART));
        register(ItemMapping.of("minecraft:mooshroom_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.MOOSHROOM));
        register(ItemMapping.of("minecraft:mule_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.MULE));
        register(ItemMapping.of("minecraft:mushroom_stew", ChunkerVanillaItemType.MUSHROOM_STEW));
        register(ItemMapping.of("minecraft:music_disc_11", ChunkerVanillaItemType.MUSIC_DISC_11));
        register(ItemMapping.of("minecraft:music_disc_13", ChunkerVanillaItemType.MUSIC_DISC_13));
        register(ItemMapping.of("minecraft:music_disc_blocks", ChunkerVanillaItemType.MUSIC_DISC_BLOCKS));
        register(ItemMapping.of("minecraft:music_disc_cat", ChunkerVanillaItemType.MUSIC_DISC_CAT));
        register(ItemMapping.of("minecraft:music_disc_chirp", ChunkerVanillaItemType.MUSIC_DISC_CHIRP));
        register(ItemMapping.of("minecraft:music_disc_far", ChunkerVanillaItemType.MUSIC_DISC_FAR));
        register(ItemMapping.of("minecraft:music_disc_mall", ChunkerVanillaItemType.MUSIC_DISC_MALL));
        register(ItemMapping.of("minecraft:music_disc_mellohi", ChunkerVanillaItemType.MUSIC_DISC_MELLOHI));
        register(ItemMapping.of("minecraft:music_disc_stal", ChunkerVanillaItemType.MUSIC_DISC_STAL));
        register(ItemMapping.of("minecraft:music_disc_strad", ChunkerVanillaItemType.MUSIC_DISC_STRAD));
        register(ItemMapping.of("minecraft:music_disc_wait", ChunkerVanillaItemType.MUSIC_DISC_WAIT));
        register(ItemMapping.of("minecraft:music_disc_ward", ChunkerVanillaItemType.MUSIC_DISC_WARD));
        register(ItemMapping.of("minecraft:mutton", ChunkerVanillaItemType.MUTTON));
        register(ItemMapping.of("minecraft:name_tag", ChunkerVanillaItemType.NAME_TAG));
        register(ItemMapping.of("minecraft:nautilus_shell", ChunkerVanillaItemType.NAUTILUS_SHELL));
        register(ItemMapping.of("minecraft:nether_brick", ChunkerVanillaItemType.NETHER_BRICK));
        register(ItemMapping.of("minecraft:nether_star", ChunkerVanillaItemType.NETHER_STAR));
        register(ItemMapping.of("minecraft:nether_wart", ChunkerVanillaItemType.NETHER_WART));
        register(ItemMapping.of("minecraft:oak_boat", ChunkerVanillaItemType.OAK_BOAT));
        register(ItemMapping.of("minecraft:ocelot_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.OCELOT));
        register(ItemMapping.of("minecraft:orange_dye", ChunkerVanillaItemType.ORANGE_DYE));
        register(ItemMapping.of("minecraft:painting", ChunkerVanillaItemType.PAINTING));
        register(ItemMapping.of("minecraft:paper", ChunkerVanillaItemType.PAPER));
        register(ItemMapping.of("minecraft:parrot_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.PARROT));
        register(ItemMapping.of("minecraft:phantom_membrane", ChunkerVanillaItemType.PHANTOM_MEMBRANE));
        register(ItemMapping.of("minecraft:phantom_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.PHANTOM));
        register(ItemMapping.of("minecraft:pig_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.PIG));
        register(ItemMapping.of("minecraft:pink_dye", ChunkerVanillaItemType.PINK_DYE));
        register(ItemMapping.of("minecraft:poisonous_potato", ChunkerVanillaItemType.POISONOUS_POTATO));
        register(ItemMapping.of("minecraft:polar_bear_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.POLAR_BEAR));
        register(ItemMapping.of("minecraft:popped_chorus_fruit", ChunkerVanillaItemType.POPPED_CHORUS_FRUIT));
        register(ItemMapping.of("minecraft:porkchop", ChunkerVanillaItemType.PORKCHOP));
        register(ItemMapping.of("minecraft:potato", ChunkerVanillaItemType.POTATO));
        register(ItemMapping.of("minecraft:potion", ChunkerVanillaItemType.POTION));
        register(ItemMapping.of("minecraft:prismarine_crystals", ChunkerVanillaItemType.PRISMARINE_CRYSTALS));
        register(ItemMapping.of("minecraft:prismarine_shard", ChunkerVanillaItemType.PRISMARINE_SHARD));
        register(ItemMapping.of("minecraft:pufferfish", ChunkerVanillaItemType.PUFFERFISH));
        register(ItemMapping.of("minecraft:pufferfish_bucket", ChunkerVanillaItemType.PUFFERFISH_BUCKET));
        register(ItemMapping.of("minecraft:pufferfish_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.PUFFERFISH));
        register(ItemMapping.of("minecraft:pumpkin_pie", ChunkerVanillaItemType.PUMPKIN_PIE));
        register(ItemMapping.of("minecraft:pumpkin_seeds", ChunkerVanillaItemType.PUMPKIN_SEEDS));
        register(ItemMapping.of("minecraft:purple_dye", ChunkerVanillaItemType.PURPLE_DYE));
        register(ItemMapping.of("minecraft:quartz", ChunkerVanillaItemType.QUARTZ));
        register(ItemMapping.of("minecraft:rabbit", ChunkerVanillaItemType.RABBIT));
        register(ItemMapping.of("minecraft:rabbit_foot", ChunkerVanillaItemType.RABBIT_FOOT));
        register(ItemMapping.of("minecraft:rabbit_hide", ChunkerVanillaItemType.RABBIT_HIDE));
        register(ItemMapping.of("minecraft:rabbit_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.RABBIT));
        register(ItemMapping.of("minecraft:rabbit_stew", ChunkerVanillaItemType.RABBIT_STEW));
        register(ItemMapping.of("minecraft:redstone", ChunkerVanillaItemType.REDSTONE));
        register(ItemMapping.of("minecraft:rose_red", ChunkerVanillaItemType.RED_DYE));
        register(ItemMapping.of("minecraft:rotten_flesh", ChunkerVanillaItemType.ROTTEN_FLESH));
        register(ItemMapping.of("minecraft:saddle", ChunkerVanillaItemType.SADDLE));
        register(ItemMapping.of("minecraft:salmon", ChunkerVanillaItemType.SALMON));
        register(ItemMapping.of("minecraft:salmon_bucket", ChunkerVanillaItemType.SALMON_BUCKET));
        register(ItemMapping.of("minecraft:salmon_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SALMON));
        register(ItemMapping.of("minecraft:scute", ChunkerVanillaItemType.TURTLE_SCUTE));
        register(ItemMapping.of("minecraft:shears", ChunkerVanillaItemType.SHEARS));
        register(ItemMapping.of("minecraft:sheep_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SHEEP));
        register(ItemMapping.of("minecraft:shield", ChunkerVanillaItemType.SHIELD));
        register(ItemMapping.of("minecraft:shulker_shell", ChunkerVanillaItemType.SHULKER_SHELL));
        register(ItemMapping.of("minecraft:shulker_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SHULKER));
        register(ItemMapping.of("minecraft:silverfish_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SILVERFISH));
        register(ItemMapping.of("minecraft:skeleton_horse_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SKELETON_HORSE));
        register(ItemMapping.of("minecraft:skeleton_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SKELETON));
        register(ItemMapping.of("minecraft:slime_ball", ChunkerVanillaItemType.SLIME_BALL));
        register(ItemMapping.of("minecraft:slime_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SLIME));
        register(ItemMapping.of("minecraft:snowball", ChunkerVanillaItemType.SNOWBALL));
        register(ItemMapping.of("minecraft:spectral_arrow", ChunkerVanillaItemType.SPECTRAL_ARROW));
        register(ItemMapping.of("minecraft:spider_eye", ChunkerVanillaItemType.SPIDER_EYE));
        register(ItemMapping.of("minecraft:spider_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SPIDER));
        register(ItemMapping.of("minecraft:splash_potion", ChunkerVanillaItemType.SPLASH_POTION));
        register(ItemMapping.of("minecraft:spruce_boat", ChunkerVanillaItemType.SPRUCE_BOAT));
        register(ItemMapping.of("minecraft:squid_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SQUID));
        register(ItemMapping.of("minecraft:stick", ChunkerVanillaItemType.STICK));
        register(ItemMapping.of("minecraft:stone_axe", ChunkerVanillaItemType.STONE_AXE));
        register(ItemMapping.of("minecraft:stone_hoe", ChunkerVanillaItemType.STONE_HOE));
        register(ItemMapping.of("minecraft:stone_pickaxe", ChunkerVanillaItemType.STONE_PICKAXE));
        register(ItemMapping.of("minecraft:stone_shovel", ChunkerVanillaItemType.STONE_SHOVEL));
        register(ItemMapping.of("minecraft:stone_sword", ChunkerVanillaItemType.STONE_SWORD));
        register(ItemMapping.of("minecraft:stray_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.STRAY));
        register(ItemMapping.of("minecraft:string", ChunkerVanillaItemType.STRING));
        register(ItemMapping.of("minecraft:sugar", ChunkerVanillaItemType.SUGAR));
        register(ItemMapping.of("minecraft:tipped_arrow", ChunkerVanillaItemType.TIPPED_ARROW));
        register(ItemMapping.of("minecraft:tnt_minecart", ChunkerVanillaItemType.TNT_MINECART));
        register(ItemMapping.of("minecraft:totem_of_undying", ChunkerVanillaItemType.TOTEM_OF_UNDYING));
        register(ItemMapping.of("minecraft:trident", ChunkerVanillaItemType.TRIDENT));
        register(ItemMapping.of("minecraft:tropical_fish", ChunkerVanillaItemType.TROPICAL_FISH));
        register(ItemMapping.of("minecraft:tropical_fish_bucket", ChunkerVanillaItemType.TROPICAL_FISH_BUCKET));
        register(ItemMapping.of("minecraft:tropical_fish_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.TROPICAL_FISH));
        register(ItemMapping.of("minecraft:turtle_helmet", ChunkerVanillaItemType.TURTLE_HELMET));
        register(ItemMapping.of("minecraft:turtle_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.TURTLE));
        register(ItemMapping.of("minecraft:vex_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.VEX));
        register(ItemMapping.of("minecraft:villager_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.VILLAGER));
        register(ItemMapping.of("minecraft:vindicator_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.VINDICATOR));
        register(ItemMapping.of("minecraft:water_bucket", ChunkerVanillaItemType.WATER_BUCKET));
        register(ItemMapping.of("minecraft:wheat", ChunkerVanillaItemType.WHEAT));
        register(ItemMapping.of("minecraft:wheat_seeds", ChunkerVanillaItemType.WHEAT_SEEDS));
        register(ItemMapping.of("minecraft:witch_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.WITCH));
        register(ItemMapping.of("minecraft:wither_skeleton_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.WITHER_SKELETON));
        register(ItemMapping.of("minecraft:wolf_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.WOLF));
        register(ItemMapping.of("minecraft:wooden_axe", ChunkerVanillaItemType.WOODEN_AXE));
        register(ItemMapping.of("minecraft:wooden_hoe", ChunkerVanillaItemType.WOODEN_HOE));
        register(ItemMapping.of("minecraft:wooden_pickaxe", ChunkerVanillaItemType.WOODEN_PICKAXE));
        register(ItemMapping.of("minecraft:wooden_shovel", ChunkerVanillaItemType.WOODEN_SHOVEL));
        register(ItemMapping.of("minecraft:wooden_sword", ChunkerVanillaItemType.WOODEN_SWORD));
        register(ItemMapping.of("minecraft:writable_book", ChunkerVanillaItemType.WRITABLE_BOOK));
        register(ItemMapping.of("minecraft:written_book", ChunkerVanillaItemType.WRITTEN_BOOK));
        register(ItemMapping.of("minecraft:zombie_horse_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ZOMBIE_HORSE));
        register(ItemMapping.of("minecraft:zombie_pigman_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ZOMBIFIED_PIGLIN));
        register(ItemMapping.of("minecraft:zombie_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ZOMBIE));
        register(ItemMapping.of("minecraft:zombie_villager_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ZOMBIE_VILLAGER));

        // Default spawn egg
        registerDuplicateInput(ItemMapping.of("minecraft:pig_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG));

        // Banners
        register(ItemMapping.group(ImmutableMultimap.<String, ChunkerVanillaItemType>builder()
                .put("minecraft:black_banner", ChunkerVanillaItemType.BLACK_BANNER)
                .put("minecraft:blue_banner", ChunkerVanillaItemType.BLUE_BANNER)
                .put("minecraft:brown_banner", ChunkerVanillaItemType.BROWN_BANNER)
                .put("minecraft:cyan_banner", ChunkerVanillaItemType.CYAN_BANNER)
                .put("minecraft:gray_banner", ChunkerVanillaItemType.GRAY_BANNER)
                .put("minecraft:green_banner", ChunkerVanillaItemType.GREEN_BANNER)
                .put("minecraft:light_blue_banner", ChunkerVanillaItemType.LIGHT_BLUE_BANNER)
                .put("minecraft:light_gray_banner", ChunkerVanillaItemType.LIGHT_GRAY_BANNER)
                .put("minecraft:lime_banner", ChunkerVanillaItemType.LIME_BANNER)
                .put("minecraft:magenta_banner", ChunkerVanillaItemType.MAGENTA_BANNER)
                .put("minecraft:orange_banner", ChunkerVanillaItemType.ORANGE_BANNER)
                .put("minecraft:pink_banner", ChunkerVanillaItemType.PINK_BANNER)
                .put("minecraft:purple_banner", ChunkerVanillaItemType.PURPLE_BANNER)
                .put("minecraft:red_banner", ChunkerVanillaItemType.RED_BANNER)
                .put("minecraft:white_banner", ChunkerVanillaItemType.WHITE_BANNER)
                .put("minecraft:yellow_banner", ChunkerVanillaItemType.YELLOW_BANNER)
                .build()
        ));

        // Oak Sign
        register(ItemMapping.of("minecraft:sign", ChunkerVanillaItemType.OAK_SIGN));

        // 1.14
        if (version.isGreaterThanOrEqual(1, 14, 0)) {
            // New signs (the old sign became oak)
            registerOverrideOutput(ItemMapping.of("minecraft:oak_sign", ChunkerVanillaItemType.OAK_SIGN));
            register(ItemMapping.group(ImmutableMultimap.<String, ChunkerVanillaItemType>builder()
                    .put("minecraft:acacia_sign", ChunkerVanillaItemType.ACACIA_SIGN)
                    .put("minecraft:birch_sign", ChunkerVanillaItemType.BIRCH_SIGN)
                    .put("minecraft:dark_oak_sign", ChunkerVanillaItemType.DARK_OAK_SIGN)
                    .put("minecraft:jungle_sign", ChunkerVanillaItemType.JUNGLE_SIGN)
                    .put("minecraft:spruce_sign", ChunkerVanillaItemType.SPRUCE_SIGN)
                    .build()
            ));

            // Renamed dyes
            registerOverrideOutput(ItemMapping.of("minecraft:red_dye", ChunkerVanillaItemType.RED_DYE));
            registerOverrideOutput(ItemMapping.of("minecraft:green_dye", ChunkerVanillaItemType.GREEN_DYE));
            registerOverrideOutput(ItemMapping.of("minecraft:yellow_dye", ChunkerVanillaItemType.YELLOW_DYE));

            // New items
            register(ItemMapping.of("minecraft:black_dye", ChunkerVanillaItemType.BLACK_DYE));
            register(ItemMapping.of("minecraft:blue_dye", ChunkerVanillaItemType.BLUE_DYE));
            register(ItemMapping.of("minecraft:brown_dye", ChunkerVanillaItemType.BROWN_DYE));
            register(ItemMapping.of("minecraft:cat_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.CAT));
            register(ItemMapping.of("minecraft:creeper_banner_pattern", ChunkerVanillaItemType.CREEPER_BANNER_PATTERN));
            register(ItemMapping.of("minecraft:crossbow", ChunkerVanillaItemType.CROSSBOW));
            register(ItemMapping.of("minecraft:flower_banner_pattern", ChunkerVanillaItemType.FLOWER_BANNER_PATTERN));
            register(ItemMapping.of("minecraft:fox_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.FOX));
            register(ItemMapping.of("minecraft:globe_banner_pattern", ChunkerVanillaItemType.GLOBE_BANNER_PATTERN));
            register(ItemMapping.of("minecraft:leather_horse_armor", ChunkerVanillaItemType.LEATHER_HORSE_ARMOR));
            register(ItemMapping.of("minecraft:mojang_banner_pattern", ChunkerVanillaItemType.MOJANG_BANNER_PATTERN));
            register(ItemMapping.of("minecraft:panda_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.PANDA));
            register(ItemMapping.of("minecraft:pillager_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.PILLAGER));
            register(ItemMapping.of("minecraft:ravager_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.RAVAGER));
            register(ItemMapping.of("minecraft:skull_banner_pattern", ChunkerVanillaItemType.SKULL_BANNER_PATTERN));
            register(ItemMapping.of("minecraft:suspicious_stew", ChunkerVanillaItemType.SUSPICIOUS_STEW));
            register(ItemMapping.of("minecraft:sweet_berries", ChunkerVanillaItemType.SWEET_BERRIES));
            register(ItemMapping.of("minecraft:trader_llama_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.TRADER_LLAMA));
            register(ItemMapping.of("minecraft:wandering_trader_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.WANDERING_TRADER));
            register(ItemMapping.of("minecraft:white_dye", ChunkerVanillaItemType.WHITE_DYE));
        }

        // 1.15
        if (version.isGreaterThanOrEqual(1, 15, 0)) {
            register(ItemMapping.of("minecraft:bee_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.BEE));
            register(ItemMapping.of("minecraft:honey_bottle", ChunkerVanillaItemType.HONEY_BOTTLE));
            register(ItemMapping.of("minecraft:honeycomb", ChunkerVanillaItemType.HONEYCOMB));
        }

        // 1.16
        if (version.isGreaterThanOrEqual(1, 16, 0)) {
            // Renamed zombie pigman spawn egg
            registerOverrideOutput(ItemMapping.of("minecraft:zombified_piglin_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ZOMBIFIED_PIGLIN));

            // New items
            register(ItemMapping.of("minecraft:hoglin_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.HOGLIN));
            register(ItemMapping.of("minecraft:music_disc_pigstep", ChunkerVanillaItemType.MUSIC_DISC_PIGSTEP));
            register(ItemMapping.of("minecraft:netherite_axe", ChunkerVanillaItemType.NETHERITE_AXE));
            register(ItemMapping.of("minecraft:netherite_boots", ChunkerVanillaItemType.NETHERITE_BOOTS));
            register(ItemMapping.of("minecraft:netherite_chestplate", ChunkerVanillaItemType.NETHERITE_CHESTPLATE));
            register(ItemMapping.of("minecraft:netherite_helmet", ChunkerVanillaItemType.NETHERITE_HELMET));
            register(ItemMapping.of("minecraft:netherite_hoe", ChunkerVanillaItemType.NETHERITE_HOE));
            register(ItemMapping.of("minecraft:netherite_ingot", ChunkerVanillaItemType.NETHERITE_INGOT));
            register(ItemMapping.of("minecraft:netherite_leggings", ChunkerVanillaItemType.NETHERITE_LEGGINGS));
            register(ItemMapping.of("minecraft:netherite_pickaxe", ChunkerVanillaItemType.NETHERITE_PICKAXE));
            register(ItemMapping.of("minecraft:netherite_scrap", ChunkerVanillaItemType.NETHERITE_SCRAP));
            register(ItemMapping.of("minecraft:netherite_shovel", ChunkerVanillaItemType.NETHERITE_SHOVEL));
            register(ItemMapping.of("minecraft:netherite_sword", ChunkerVanillaItemType.NETHERITE_SWORD));
            register(ItemMapping.of("minecraft:piglin_banner_pattern", ChunkerVanillaItemType.PIGLIN_BANNER_PATTERN));
            register(ItemMapping.of("minecraft:piglin_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.PIGLIN));
            register(ItemMapping.of("minecraft:strider_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.STRIDER));
            register(ItemMapping.of("minecraft:warped_fungus_on_a_stick", ChunkerVanillaItemType.WARPED_FUNGUS_ON_A_STICK));
            register(ItemMapping.of("minecraft:zoglin_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ZOGLIN));

            // Signs
            register(ItemMapping.group(ImmutableMultimap.<String, ChunkerVanillaItemType>builder()
                    .put("minecraft:crimson_sign", ChunkerVanillaItemType.CRIMSON_SIGN)
                    .put("minecraft:warped_sign", ChunkerVanillaItemType.WARPED_SIGN)
                    .build()
            ));
        }

        // 1.16.2
        if (version.isGreaterThanOrEqual(1, 16, 2)) {
            register(ItemMapping.of("minecraft:piglin_brute_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.PIGLIN_BRUTE));
        }

        // 1.17
        if (version.isGreaterThanOrEqual(1, 17, 0)) {
            register(ItemMapping.of("minecraft:amethyst_shard", ChunkerVanillaItemType.AMETHYST_SHARD));
            register(ItemMapping.of("minecraft:axolotl_bucket", ChunkerVanillaItemType.AXOLOTL_BUCKET));
            register(ItemMapping.of("minecraft:axolotl_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.AXOLOTL));
            register(ItemMapping.of("minecraft:bundle", ChunkerVanillaItemType.BUNDLE));
            register(ItemMapping.of("minecraft:copper_ingot", ChunkerVanillaItemType.COPPER_INGOT));
            register(ItemMapping.of("minecraft:glow_berries", ChunkerVanillaItemType.GLOW_BERRIES));
            register(ItemMapping.of("minecraft:glow_ink_sac", ChunkerVanillaItemType.GLOW_INK_SAC));
            register(ItemMapping.of("minecraft:glow_item_frame", ChunkerVanillaItemType.GLOW_ITEM_FRAME));
            register(ItemMapping.of("minecraft:glow_squid_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.GLOW_SQUID));
            register(ItemMapping.of("minecraft:goat_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.GOAT));
            register(ItemMapping.of("minecraft:powder_snow_bucket", ChunkerVanillaItemType.POWDER_SNOW_BUCKET));
            register(ItemMapping.of("minecraft:raw_copper", ChunkerVanillaItemType.RAW_COPPER));
            register(ItemMapping.of("minecraft:raw_gold", ChunkerVanillaItemType.RAW_GOLD));
            register(ItemMapping.of("minecraft:raw_iron", ChunkerVanillaItemType.RAW_IRON));
            register(ItemMapping.of("minecraft:spyglass", ChunkerVanillaItemType.SPYGLASS));
        }

        // 1.18
        if (version.isGreaterThanOrEqual(1, 18, 0)) {
            register(ItemMapping.of("minecraft:music_disc_otherside", ChunkerVanillaItemType.MUSIC_DISC_OTHERSIDE));
        }

        // 1.19
        if (version.isGreaterThanOrEqual(1, 19, 0)) {
            register(ItemMapping.of("minecraft:acacia_chest_boat", ChunkerVanillaItemType.ACACIA_CHEST_BOAT));
            register(ItemMapping.of("minecraft:allay_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ALLAY));
            register(ItemMapping.of("minecraft:birch_chest_boat", ChunkerVanillaItemType.BIRCH_CHEST_BOAT));
            register(ItemMapping.of("minecraft:dark_oak_chest_boat", ChunkerVanillaItemType.DARK_OAK_CHEST_BOAT));
            register(ItemMapping.of("minecraft:disc_fragment_5", ChunkerVanillaItemType.DISC_FRAGMENT_5));
            register(ItemMapping.of("minecraft:echo_shard", ChunkerVanillaItemType.ECHO_SHARD));
            register(ItemMapping.of("minecraft:frog_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.FROG));
            register(ItemMapping.of("minecraft:goat_horn", ChunkerVanillaItemType.GOAT_HORN));
            register(ItemMapping.of("minecraft:jungle_chest_boat", ChunkerVanillaItemType.JUNGLE_CHEST_BOAT));
            register(ItemMapping.of("minecraft:mangrove_boat", ChunkerVanillaItemType.MANGROVE_BOAT));
            register(ItemMapping.of("minecraft:mangrove_chest_boat", ChunkerVanillaItemType.MANGROVE_CHEST_BOAT));
            register(ItemMapping.of("minecraft:music_disc_5", ChunkerVanillaItemType.MUSIC_DISC_5));
            register(ItemMapping.of("minecraft:oak_chest_boat", ChunkerVanillaItemType.OAK_CHEST_BOAT));
            register(ItemMapping.of("minecraft:recovery_compass", ChunkerVanillaItemType.RECOVERY_COMPASS));
            register(ItemMapping.of("minecraft:spruce_chest_boat", ChunkerVanillaItemType.SPRUCE_CHEST_BOAT));
            register(ItemMapping.of("minecraft:tadpole_bucket", ChunkerVanillaItemType.TADPOLE_BUCKET));
            register(ItemMapping.of("minecraft:tadpole_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.TADPOLE));
            register(ItemMapping.of("minecraft:warden_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.WARDEN));
            register(ItemMapping.of("minecraft:mangrove_sign", ChunkerVanillaItemType.MANGROVE_SIGN));
        }

        // 1.19.3
        if (version.isGreaterThanOrEqual(1, 19, 3)) {
            register(ItemMapping.of("minecraft:bamboo_chest_raft", ChunkerVanillaItemType.BAMBOO_CHEST_RAFT));
            register(ItemMapping.of("minecraft:bamboo_raft", ChunkerVanillaItemType.BAMBOO_RAFT));
            register(ItemMapping.of("minecraft:bamboo_sign", ChunkerVanillaItemType.BAMBOO_SIGN));

            // Hanging Signs
            register(ItemMapping.group(ImmutableMultimap.<String, ChunkerVanillaItemType>builder()
                    .put("minecraft:acacia_hanging_sign", ChunkerVanillaItemType.ACACIA_HANGING_SIGN)
                    .put("minecraft:bamboo_hanging_sign", ChunkerVanillaItemType.BAMBOO_HANGING_SIGN)
                    .put("minecraft:birch_hanging_sign", ChunkerVanillaItemType.BIRCH_HANGING_SIGN)
                    .put("minecraft:crimson_hanging_sign", ChunkerVanillaItemType.CRIMSON_HANGING_SIGN)
                    .put("minecraft:dark_oak_hanging_sign", ChunkerVanillaItemType.DARK_OAK_HANGING_SIGN)
                    .put("minecraft:jungle_hanging_sign", ChunkerVanillaItemType.JUNGLE_HANGING_SIGN)
                    .put("minecraft:mangrove_hanging_sign", ChunkerVanillaItemType.MANGROVE_HANGING_SIGN)
                    .put("minecraft:oak_hanging_sign", ChunkerVanillaItemType.OAK_HANGING_SIGN)
                    .put("minecraft:spruce_hanging_sign", ChunkerVanillaItemType.SPRUCE_HANGING_SIGN)
                    .put("minecraft:warped_hanging_sign", ChunkerVanillaItemType.WARPED_HANGING_SIGN)
                    .build()
            ));

            // New spawn eggs
            register(ItemMapping.of("minecraft:camel_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.CAMEL));
            register(ItemMapping.of("minecraft:ender_dragon_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ENDER_DRAGON));
            register(ItemMapping.of("minecraft:iron_golem_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.IRON_GOLEM));
            register(ItemMapping.of("minecraft:snow_golem_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SNOW_GOLEM));
            register(ItemMapping.of("minecraft:wither_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.WITHER));
        }

        // 1.19.4
        if (version.isGreaterThanOrEqual(1, 19, 4)) {
            register(ItemMapping.of("minecraft:brush", ChunkerVanillaItemType.BRUSH));
            register(ItemMapping.of("minecraft:cherry_boat", ChunkerVanillaItemType.CHERRY_BOAT));
            register(ItemMapping.of("minecraft:cherry_chest_boat", ChunkerVanillaItemType.CHERRY_CHEST_BOAT));
            register(ItemMapping.of("minecraft:cherry_sign", ChunkerVanillaItemType.CHERRY_SIGN));
            register(ItemMapping.of("minecraft:cherry_hanging_sign", ChunkerVanillaItemType.CHERRY_HANGING_SIGN));


            // Smithing templates
            register(ItemMapping.of("minecraft:coast_armor_trim_smithing_template", ChunkerVanillaItemType.COAST_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:dune_armor_trim_smithing_template", ChunkerVanillaItemType.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:eye_armor_trim_smithing_template", ChunkerVanillaItemType.EYE_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:netherite_upgrade_smithing_template", ChunkerVanillaItemType.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:rib_armor_trim_smithing_template", ChunkerVanillaItemType.RIB_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:sentry_armor_trim_smithing_template", ChunkerVanillaItemType.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:snout_armor_trim_smithing_template", ChunkerVanillaItemType.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:spire_armor_trim_smithing_template", ChunkerVanillaItemType.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:tide_armor_trim_smithing_template", ChunkerVanillaItemType.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:vex_armor_trim_smithing_template", ChunkerVanillaItemType.VEX_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:ward_armor_trim_smithing_template", ChunkerVanillaItemType.WARD_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:wild_armor_trim_smithing_template", ChunkerVanillaItemType.WILD_ARMOR_TRIM_SMITHING_TEMPLATE));

            // Sherds
            register(ItemMapping.of("minecraft:pottery_shard_skull", ChunkerVanillaItemType.SKULL_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:pottery_shard_archer", ChunkerVanillaItemType.ARCHER_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:pottery_shard_arms_up", ChunkerVanillaItemType.ARMS_UP_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:pottery_shard_prize", ChunkerVanillaItemType.PRIZE_POTTERY_SHERD));

            register(ItemMapping.of("minecraft:sniffer_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.SNIFFER));
            register(ItemMapping.of("minecraft:torchflower_seeds", ChunkerVanillaItemType.TORCHFLOWER_SEEDS));
        }

        // 1.20
        if (version.isGreaterThanOrEqual(1, 20, 0)) {
            // Renamed to sherds
            registerOverrideOutput(ItemMapping.of("minecraft:skull_pottery_sherd", ChunkerVanillaItemType.SKULL_POTTERY_SHERD));
            registerOverrideOutput(ItemMapping.of("minecraft:archer_pottery_sherd", ChunkerVanillaItemType.ARCHER_POTTERY_SHERD));
            registerOverrideOutput(ItemMapping.of("minecraft:arms_up_pottery_sherd", ChunkerVanillaItemType.ARMS_UP_POTTERY_SHERD));
            registerOverrideOutput(ItemMapping.of("minecraft:prize_pottery_sherd", ChunkerVanillaItemType.PRIZE_POTTERY_SHERD));

            // New sherds
            register(ItemMapping.of("minecraft:angler_pottery_sherd", ChunkerVanillaItemType.ANGLER_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:blade_pottery_sherd", ChunkerVanillaItemType.BLADE_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:brewer_pottery_sherd", ChunkerVanillaItemType.BREWER_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:burn_pottery_sherd", ChunkerVanillaItemType.BURN_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:danger_pottery_sherd", ChunkerVanillaItemType.DANGER_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:explorer_pottery_sherd", ChunkerVanillaItemType.EXPLORER_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:friend_pottery_sherd", ChunkerVanillaItemType.FRIEND_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:heart_pottery_sherd", ChunkerVanillaItemType.HEART_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:heartbreak_pottery_sherd", ChunkerVanillaItemType.HEARTBREAK_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:howl_pottery_sherd", ChunkerVanillaItemType.HOWL_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:miner_pottery_sherd", ChunkerVanillaItemType.MINER_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:mourner_pottery_sherd", ChunkerVanillaItemType.MOURNER_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:plenty_pottery_sherd", ChunkerVanillaItemType.PLENTY_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:sheaf_pottery_sherd", ChunkerVanillaItemType.SHEAF_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:shelter_pottery_sherd", ChunkerVanillaItemType.SHELTER_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:snort_pottery_sherd", ChunkerVanillaItemType.SNORT_POTTERY_SHERD));

            // New smithing templates
            register(ItemMapping.of("minecraft:host_armor_trim_smithing_template", ChunkerVanillaItemType.HOST_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:raiser_armor_trim_smithing_template", ChunkerVanillaItemType.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:shaper_armor_trim_smithing_template", ChunkerVanillaItemType.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:silence_armor_trim_smithing_template", ChunkerVanillaItemType.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:wayfinder_armor_trim_smithing_template", ChunkerVanillaItemType.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE));

            register(ItemMapping.of("minecraft:pitcher_pod", ChunkerVanillaItemType.PITCHER_POD));

            // New music disc
            register(ItemMapping.of("minecraft:music_disc_relic", ChunkerVanillaItemType.MUSIC_DISC_RELIC));
        }

        // 1.20.3
        if (version.isGreaterThanOrEqual(1, 20, 3)) {
            register(ItemMapping.of("minecraft:breeze_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.BREEZE));
            register(ItemMapping.of("minecraft:trial_key", ChunkerVanillaItemType.TRIAL_KEY));
        }

        // 1.20.5
        if (version.isGreaterThanOrEqual(1, 20, 5)) {
            // Renamed turtle scute
            registerOverrideOutput(ItemMapping.of("minecraft:turtle_scute", ChunkerVanillaItemType.TURTLE_SCUTE));

            // New items
            register(ItemMapping.of("minecraft:wind_charge", ChunkerVanillaItemType.WIND_CHARGE));
            register(ItemMapping.of("minecraft:breeze_rod", ChunkerVanillaItemType.BREEZE_ROD));
            register(ItemMapping.of("minecraft:mace", ChunkerVanillaItemType.MACE));
            register(ItemMapping.of("minecraft:wolf_armor", ChunkerVanillaItemType.WOLF_ARMOR));
            register(ItemMapping.of("minecraft:armadillo_scute", ChunkerVanillaItemType.ARMADILLO_SCUTE));
            register(ItemMapping.of("minecraft:ominous_bottle", ChunkerVanillaItemType.OMINOUS_BOTTLE));
            register(ItemMapping.of("minecraft:ominous_trial_key", ChunkerVanillaItemType.OMINOUS_TRIAL_KEY));

            // New eggs
            register(ItemMapping.of("minecraft:armadillo_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.ARMADILLO));
            register(ItemMapping.of("minecraft:bogged_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.BOGGED));

            // New sherds
            register(ItemMapping.of("minecraft:flow_pottery_sherd", ChunkerVanillaItemType.FLOW_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:guster_pottery_sherd", ChunkerVanillaItemType.GUSTER_POTTERY_SHERD));
            register(ItemMapping.of("minecraft:scrape_pottery_sherd", ChunkerVanillaItemType.SCRAPE_POTTERY_SHERD));

            // New banner patterns
            register(ItemMapping.of("minecraft:flow_banner_pattern", ChunkerVanillaItemType.FLOW_BANNER_PATTERN));
            register(ItemMapping.of("minecraft:guster_banner_pattern", ChunkerVanillaItemType.GUSTER_BANNER_PATTERN));

            // New smithing templates
            register(ItemMapping.of("minecraft:bolt_armor_trim_smithing_template", ChunkerVanillaItemType.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE));
            register(ItemMapping.of("minecraft:flow_armor_trim_smithing_template", ChunkerVanillaItemType.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE));
        }

        // 1.21
        if (version.isGreaterThanOrEqual(1, 21, 0)) {
            register(ItemMapping.of("minecraft:music_disc_creator", ChunkerVanillaItemType.MUSIC_DISC_CREATOR));
            register(ItemMapping.of("minecraft:music_disc_creator_music_box", ChunkerVanillaItemType.MUSIC_DISC_CREATOR_MUSIC_BOX));
            register(ItemMapping.of("minecraft:music_disc_precipice", ChunkerVanillaItemType.MUSIC_DISC_PRECIPICE));
        }

        // 1.21.2
        if (version.isGreaterThanOrEqual(1, 21, 2)) {
            // New eggs
            register(ItemMapping.of("minecraft:creaking_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.CREAKING));

            // Banner pattern parity with Bedrock
            register(ItemMapping.of("minecraft:bordure_indented_banner_pattern", ChunkerVanillaItemType.BORDURE_INDENTED_BANNER_PATTERN));
            register(ItemMapping.of("minecraft:field_masoned_banner_pattern", ChunkerVanillaItemType.FIELD_MASONED_BANNER_PATTERN));

            // Colored bundles
            register(ItemMapping.of("minecraft:black_bundle", ChunkerVanillaItemType.BLACK_BUNDLE));
            register(ItemMapping.of("minecraft:blue_bundle", ChunkerVanillaItemType.BLUE_BUNDLE));
            register(ItemMapping.of("minecraft:brown_bundle", ChunkerVanillaItemType.BROWN_BUNDLE));
            register(ItemMapping.of("minecraft:cyan_bundle", ChunkerVanillaItemType.CYAN_BUNDLE));
            register(ItemMapping.of("minecraft:gray_bundle", ChunkerVanillaItemType.GRAY_BUNDLE));
            register(ItemMapping.of("minecraft:green_bundle", ChunkerVanillaItemType.GREEN_BUNDLE));
            register(ItemMapping.of("minecraft:light_blue_bundle", ChunkerVanillaItemType.LIGHT_BLUE_BUNDLE));
            register(ItemMapping.of("minecraft:light_gray_bundle", ChunkerVanillaItemType.LIGHT_GRAY_BUNDLE));
            register(ItemMapping.of("minecraft:lime_bundle", ChunkerVanillaItemType.LIME_BUNDLE));
            register(ItemMapping.of("minecraft:magenta_bundle", ChunkerVanillaItemType.MAGENTA_BUNDLE));
            register(ItemMapping.of("minecraft:orange_bundle", ChunkerVanillaItemType.ORANGE_BUNDLE));
            register(ItemMapping.of("minecraft:pink_bundle", ChunkerVanillaItemType.PINK_BUNDLE));
            register(ItemMapping.of("minecraft:purple_bundle", ChunkerVanillaItemType.PURPLE_BUNDLE));
            register(ItemMapping.of("minecraft:red_bundle", ChunkerVanillaItemType.RED_BUNDLE));
            register(ItemMapping.of("minecraft:white_bundle", ChunkerVanillaItemType.WHITE_BUNDLE));
            register(ItemMapping.of("minecraft:yellow_bundle", ChunkerVanillaItemType.YELLOW_BUNDLE));

            // New pale oak boat
            register(ItemMapping.of("minecraft:pale_oak_boat", ChunkerVanillaItemType.PALE_OAK_BOAT));
            register(ItemMapping.of("minecraft:pale_oak_chest_boat", ChunkerVanillaItemType.PALE_OAK_CHEST_BOAT));

            // New Pale Oak signs
            register(ItemMapping.of("minecraft:pale_oak_sign", ChunkerVanillaItemType.PALE_OAK_SIGN));
            register(ItemMapping.of("minecraft:pale_oak_hanging_sign", ChunkerVanillaItemType.PALE_OAK_HANGING_SIGN));
        }

        // 1.21.4
        if (version.isGreaterThanOrEqual(1, 21, 4)) {
            register(ItemMapping.of("minecraft:resin_brick", ChunkerVanillaItemType.RESIN_BRICK));
        }

        // 1.21.5
        if (version.isGreaterThanOrEqual(1, 21, 5)) {
            register(ItemMapping.of("minecraft:blue_egg", ChunkerVanillaItemType.BLUE_EGG));
            register(ItemMapping.of("minecraft:brown_egg", ChunkerVanillaItemType.BROWN_EGG));
        }

        // 1.21.6
        if (version.isGreaterThanOrEqual(1, 21, 6)) {
            // Game Drop 2 Experiments
            // New egg
            register(ItemMapping.of("minecraft:happy_ghast_spawn_egg", ChunkerVanillaItemType.SPAWN_EGG, ChunkerItemProperty.SPAWN_EGG_MOB, ChunkerVanillaEntityType.HAPPY_GHAST));

            // Harness
            register(ItemMapping.of("minecraft:black_harness", ChunkerVanillaItemType.BLACK_HARNESS));
            register(ItemMapping.of("minecraft:blue_harness", ChunkerVanillaItemType.BLUE_HARNESS));
            register(ItemMapping.of("minecraft:brown_harness", ChunkerVanillaItemType.BROWN_HARNESS));
            register(ItemMapping.of("minecraft:cyan_harness", ChunkerVanillaItemType.CYAN_HARNESS));
            register(ItemMapping.of("minecraft:gray_harness", ChunkerVanillaItemType.GRAY_HARNESS));
            register(ItemMapping.of("minecraft:green_harness", ChunkerVanillaItemType.GREEN_HARNESS));
            register(ItemMapping.of("minecraft:light_blue_harness", ChunkerVanillaItemType.LIGHT_BLUE_HARNESS));
            register(ItemMapping.of("minecraft:light_gray_harness", ChunkerVanillaItemType.LIGHT_GRAY_HARNESS));
            register(ItemMapping.of("minecraft:lime_harness", ChunkerVanillaItemType.LIME_HARNESS));
            register(ItemMapping.of("minecraft:magenta_harness", ChunkerVanillaItemType.MAGENTA_HARNESS));
            register(ItemMapping.of("minecraft:orange_harness", ChunkerVanillaItemType.ORANGE_HARNESS));
            register(ItemMapping.of("minecraft:pink_harness", ChunkerVanillaItemType.PINK_HARNESS));
            register(ItemMapping.of("minecraft:purple_harness", ChunkerVanillaItemType.PURPLE_HARNESS));
            register(ItemMapping.of("minecraft:red_harness", ChunkerVanillaItemType.RED_HARNESS));
            register(ItemMapping.of("minecraft:white_harness", ChunkerVanillaItemType.WHITE_HARNESS));
            register(ItemMapping.of("minecraft:yellow_harness", ChunkerVanillaItemType.YELLOW_HARNESS));

            // Music disc
            register(ItemMapping.of("minecraft:music_disc_tears", ChunkerVanillaItemType.MUSIC_DISC_TEARS));

        }
    }
}
