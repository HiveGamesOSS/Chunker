package com.hivemc.chunker.conversion.integration;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.gson.*;
import com.hivemc.chunker.conversion.bedrock.resolver.BedrockBlockIdentifierValidationTests;
import com.hivemc.chunker.conversion.bedrock.resolver.MockConverter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy.BedrockLegacyBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy.JavaLegacyBlockIdentifierResolver;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.identifier.states.StateValueBoolean;
import com.hivemc.chunker.mapping.identifier.states.StateValueInt;
import com.hivemc.chunker.mapping.identifier.states.StateValueString;
import com.hivemc.chunker.resolver.Resolver;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests which use legacy Chunker data to check for regressions in block mappings.
 * This is only run with the LongRunning tag since it takes a while.
 */
@Tag("LongRunning")
public class BlockIdentifierIntegrationTests {
    // Note: The bedrock data fixes are needed because the old mappings weren't that accurate
    public static final Set<String> BEDROCK_DATA_FIXES = Set.of(
            "minecraft:powered_comparator", // Goes to the unpowered state if not lit
            "minecraft:unpowered_comparator", // Goes to powered state if lit
            "minecraft:snow_layer", // Goes to non-covered bit
            "minecraft:bone_block", // Fix deprecated bit
            "minecraft:hay_block", // Fix deprecated bit
            "minecraft:pumpkin", // No direction needed
            "minecraft:wooden_pressure_plate", // Signal isn't retained
            "minecraft:dark_oak_pressure_plate", // Signal isn't retained
            "minecraft:acacia_pressure_plate", // Signal isn't retained
            "minecraft:jungle_pressure_plate", // Signal isn't retained
            "minecraft:birch_pressure_plate", // Signal isn't retained
            "minecraft:spruce_pressure_plate", // Signal isn't retained
            "minecraft:stone_pressure_plate", // Signal isn't retained
            "minecraft:pistonarmcollision", // not in older versions
            "minecraft:bamboo_sapling", // unused sapling_type state
            "minecraft:wooden_door", // wrong bits
            "minecraft:spruce_door", // wrong bits
            "minecraft:birch_door", // wrong bits
            "minecraft:jungle_door", // wrong bits
            "minecraft:dark_oak_door", // wrong bits
            "minecraft:acacia_door", // wrong bits
            "minecraft:iron_door", // wrong bits
            "minecraft:quartz_block", // wrong bits
            "minecraft:purpur_block", // wrong bits
            "minecraft:kelp", // wrong bits
            "minecraft:bell", // wrong bits
            "minecraft:wood", // no rotation
            "minecraft:anvil", // unused state
            "minecraft:double_stone_slab", // unused state
            "minecraft:double_stone_slab2", // unused state
            "minecraft:double_stone_slab3", // unused state
            "minecraft:double_stone_slab4", // unused state
            "minecraft:double_wooden_slab", // unused state
            "minecraft:coral_fan_hang3", // unused state
            "minecraft:red_mushroom_block", // unused states
            "minecraft:brown_mushroom_block" // unused states
    );
    public static final Multimap<String, Map<String, StateValue<?>>> FIXED_MAPPINGS = ImmutableMultimap.<String, Map<String, StateValue<?>>>builder()
            .put("minecraft:sea_pickle", Map.of("dead_bit", StateValueBoolean.TRUE)) // dead_bit now depends on waterlogging
            .put("minecraft:sweet_berry_bush", Map.of()) // growth stages are more accurate
            .put("minecraft:beetroots", Map.of()) // growth stages are more accurate
            .put("minecraft:big_dripleaf", Map.of("big_dripleaf_head", StateValueBoolean.TRUE)) // fixed head stability not being preserved
            .put("minecraft:cave_vines_plant", Map.of()) // fixed the head and body being swapped
            .put("minecraft:mangrove_propagule_hanging", Map.of()) // incorrect identifier
            .put("minecraft:white_bed", Map.of()) // now uses white_bed as default
            .put("minecraft:white_banner", Map.of()) // now uses white_banner as default
            .put("minecraft:white_wall_banner", Map.of()) // now uses white_wall_banner as default
            .put("minecraft:skeleton_wall_skull", Map.of()) // now uses skeleton_wall_skull as default
            .put("minecraft:skeleton_skull", Map.of()) // now uses skeleton_skull as default
            .put("minecraft:damaged_anvil", Map.of()) // now used as a replacement for damage=broken from bedrock
            .put("minecraft:purpur_pillar", Map.of()) // fixed being incorrectly converted to purpur_block
            .put("minecraft:fern", Map.of()) // fixed being incorrectly converted to short_grass when snowy
            .put("minecraft:glow_lichen", Map.of()) // fixed states on older versions
            .put("minecraft:sculk_vein", Map.of()) // fixed states on older versions
            .put("minecraft:structure_block", Map.of("mode", new StateValueString("save"))) // Uses save as a default
            .put("minecraft:mushroom_stem", Map.of()) // Fixed a few instances of mushrooms not being mapped to the stem block
            .put("minecraft:twisting_vines", Map.of()) // Fixed no backwards mapping for producing twisting_vines_plant
            .put("minecraft:twisting_vines_block", Map.of()) // Fixed no backwards mapping for producing twisting_vines_plant
            .put("minecraft:weeping_vines", Map.of()) // Fixed no backwards mapping for producing weeping_vines_plant
            .put("minecraft:kelp_plant", Map.of()) // Fixed no backwards mapping for producing kelp
            .put("minecraft:pumpkin_stem", Map.of("growth", new StateValueInt(7))) // Fixed grown stem not being attached
            .put("minecraft:melon_stem", Map.of("growth", new StateValueInt(7))) // Fixed grown stem not being attached
            .put("minecraft:stone_slab", Map.of("stone_slab_type", new StateValueString("wood"))) // correctly stays as petrified
            .put("minecraft:double_stone_slab", Map.of("stone_slab_type", new StateValueString("wood"))) // correctly stays as petrified
            .put("minecraft:stone_block_slab", Map.of("stone_slab_type", new StateValueString("wood"))) // correctly stays as petrified
            .put("minecraft:double_stone_block_slab", Map.of("stone_slab_type", new StateValueString("wood"))) // correctly stays as petrified
            // 1.13 Java & Below fixes
            .put("minecraft:tallgrass", Map.of("data", new StateValueInt(0))) // fixed not going to dead_bush
            .put("minecraft:tallgrass", Map.of("data", new StateValueInt(1))) // fixed not going to right grass
            .put("minecraft:tallgrass", Map.of("data", new StateValueInt(2))) // fixed not going to right fern
            .put("minecraft:torch", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:chest", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:furnace", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:lit_furnace", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:ladder", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:ladder", Map.of("data", new StateValueInt(1))) // unused state
            .put("minecraft:ladder", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:wall_sign", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:unlit_redstone_torch", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:redstone_torch", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:portal", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:ender_chest", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:trapped_chest", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:wall_banner", Map.of("data", new StateValueInt(0))) // unused state
            .put("minecraft:skull", Map.of("data", new StateValueInt(0))) // unused state (1 is preferred)
            .put("minecraft:stone", Map.of("data", new StateValueInt(0))) // stone doesn't correctly map in the old mappings
            .put("minecraft:stone", Map.of("data", new StateValueInt(1))) // stone doesn't correctly map in the old mappings
            .put("minecraft:stone", Map.of("data", new StateValueInt(2))) // stone doesn't correctly map in the old mappings
            .put("minecraft:stone", Map.of("data", new StateValueInt(3))) // stone doesn't correctly map in the old mappings
            .put("minecraft:stone", Map.of("data", new StateValueInt(4))) // stone doesn't correctly map in the old mappings
            .put("minecraft:stone", Map.of("data", new StateValueInt(5))) // stone doesn't correctly map in the old mappings
            .put("minecraft:stone", Map.of("data", new StateValueInt(6))) // stone doesn't correctly map in the old mappings
            .put("minecraft:purpur_double_slab", Map.of("data", new StateValueInt(0))) // purpur slab doesn't map properly in old mappings
            .put("minecraft:bedrock", Map.of("data", new StateValueInt(0))) // infiniburn isn't present on Java
            .put("minecraft:piston_extension", Map.of("data", new StateValueInt(0))) // moving block isn't supported from bedrock <-> java
            .put("minecraft:stonebrick", Map.of("data", new StateValueInt(0))) // smooth stone brick is a duplicate
            .put("minecraft:cauldron", Map.of("data", new StateValueInt(0))) // cauldron was wrong
            .put("minecraft:cauldron", Map.of("data", new StateValueInt(1))) // cauldron level 2 goes to 1 for java to bedrock
            .put("minecraft:cauldron", Map.of("data", new StateValueInt(2))) // cauldron level 3 goes to 2 for java to bedrock
            .put("minecraft:cauldron", Map.of("data", new StateValueInt(3))) // cauldron level 5 goes to 3 for java to bedrock
            .put("minecraft:piston", Map.of("data", new StateValueInt(0))) // piston went to the wrong state
            .put("minecraft:piston", Map.of("data", new StateValueInt(1))) // piston went to the wrong state
            .put("minecraft:sticky_piston", Map.of("data", new StateValueInt(0))) // piston went to the wrong state
            .put("minecraft:sticky_piston", Map.of("data", new StateValueInt(1))) // piston went to the wrong state
            .put("minecraft:red_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:brown_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:cyan_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:silver_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:gray_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:pink_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:lime_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:yellow_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:light_blue_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:magenta_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:orange_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:white_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:blue_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:black_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:green_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:purple_glazed_terracotta", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:unlit_redstone_torch", Map.of("data", new StateValueInt(5))) // unused state
            .put("minecraft:redstone_torch", Map.of("data", new StateValueInt(5))) // unused state
            .put("minecraft:torch", Map.of("data", new StateValueInt(5))) // unused state
            .put("minecraft:wall_banner", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:wall_sign", Map.of("data", new StateValueInt(2))) // unused state
            .put("minecraft:portal", Map.of("data", new StateValueInt(1))) // portal unknown axis isn't on java
            .put("minecraft:tnt", Map.of("data", new StateValueInt(0))) // tnt underwater isn't on java
            .put("minecraft:tnt", Map.of("data", new StateValueInt(1))) // tnt underwater isn't on java
            .put("minecraft:flower_pot", Map.of("data", new StateValueInt(0))) // update_bit isn't on java
            .put("minecraft:jukebox", Map.of("data", new StateValueInt(0))) // has_record isn't on bedrock
            .put("minecraft:double_stone_slab", Map.of("data", new StateValueInt(15))) // quartz_block smooth
            .put("minecraft:purpur_slab", Map.of("data", new StateValueInt(0))) // wrong slab for purpur
            .put("minecraft:purpur_slab", Map.of("data", new StateValueInt(8))) // wrong slab for purpur
            .put("minecraft:double_stone_slab2", Map.of("data", new StateValueInt(8))) // smooth red sandstone
            .put("minecraft:double_stone_slab", Map.of("data", new StateValueInt(9))) // smooth sandstone
            .put("minecraft:structure_void", Map.of("data", new StateValueInt(0))) // use structure void void
            .put("minecraft:birch_wall_sign", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:spruce_wall_sign", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:jungle_wall_sign", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:acacia_wall_sign", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:darkoak_wall_sign", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:stonecutter_block", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:smoker", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:lit_smoker", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:blast_furnace", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:furnace", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:lit_furnace", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:lit_blast_furnace", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:chest", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:ender_chest", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:trapped_chest", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:hopper", Map.of("data", new StateValueInt(0))) // ignore unused state
            .put("minecraft:hopper", Map.of("data", new StateValueInt(8))) // ignore unused state
            .put("minecraft:hopper", Map.of("toggle_bit", StateValueBoolean.TRUE)) // fixed inverted state
            .put("minecraft:hopper", Map.of("toggle_bit", StateValueBoolean.FALSE)) // fixed inverted state
            .put("minecraft:skull", Map.of("data", new StateValueInt(2))) // ignore unused state
            .put("minecraft:skull", Map.of("data", new StateValueInt(10))) // ignore unused state
            .put("minecraft:smooth_stone", Map.of("data", new StateValueInt(0))) // goes to smooth_stone
            .put("minecraft:movingblock", Map.of("data", new StateValueInt(0))) // ignore unused state
            .put("minecraft:trapdoor", Map.of("data", new StateValueInt(3))) // incorrect states
            .put("minecraft:iron_trapdoor", Map.of("data", new StateValueInt(3))) // incorrect states
            .build();

    // Note: The java data fixes are needed because the old mappings weren't that accurate
    public static final Set<String> JAVA_DATA_FIXES = Set.of(
            "minecraft:wooden_pressure_plate", //  power is lost
            "minecraft:stone_pressure_plate", //  power is lost
            "minecraft:snow_layer", // covered_bit isn't on java
            "minecraft:acacia_door", // certain doors don't actually have as many states
            "minecraft:birch_door", // certain doors don't actually have as many states
            "minecraft:dark_oak_door",  // certain doors don't actually have as many states
            "minecraft:jungle_door", // certain doors don't actually have as many states
            "minecraft:spruce_door",  // certain doors don't actually have as many states
            "minecraft:wooden_door",  // certain doors don't actually have as many states
            "minecraft:iron_door",  // certain doors don't actually have as many states
            "minecraft:bone_block",  // deprecated isn't on java
            "minecraft:hay_block",  // deprecated isn't on java
            "minecraft:acacia_fence_gate", // doesn't match
            "minecraft:dark_oak_fence_gate", // doesn't match
            "minecraft:jungle_fence_gate", // doesn't match
            "minecraft:spruce_fence_gate", // doesn't match
            "minecraft:birch_fence_gate", // doesn't match
            "minecraft:fence_gate", // doesn't match
            "minecraft:end_rod", // doesn't match
            "minecraft:structure_block", // doesn't match
            "minecraft:bed", // data is only in one half
            "minecraft:hopper", // data is inverted
            "minecraft:quartz_block", // pillar axis isn't part of quartz_block and smooth is a slab
            "minecraft:purpur_block", // doesn't have as many types
            "minecraft:powered_comparator", // Goes to the unpowered state if not lit
            "minecraft:unpowered_comparator", // Goes to powered state if lit
            "minecraft:red_mushroom_block", // Fixed to be more accurate
            "minecraft:brown_mushroom_block", // Fixed to be more accurate
            "minecraft:double_plant", // Fixed to be more accurate
            "minecraft:stone_button", // mismatch
            "minecraft:wooden_button", // mismatch
            "minecraft:pumpkin", // not in less than 1.13
            "minecraft:double_stone_slab", // mismatch
            "minecraft:stone_slab" // mismatch
    );
    public static final Set<String> MISSING_BLOCKS = ImmutableSet.of(
            "minecraft:powder_snow_cauldron", // incorrectly is provided in below bedrock 1.16.220
            "minecraft:cauldron", // incorrectly is provided in below bedrock 1.16.220
            "minecraft:bee_nest", // incorrectly is provided in below bedrock 1.14.0
            "minecraft:beehive", // incorrectly is provided in below bedrock 1.14.0
            "minecraft:honey_block", // incorrectly is provided in below bedrock 1.14.0
            "minecraft:honeycomb_block", // incorrectly is provided in below bedrock 1.14.0
            "minecraft:cobblestone_wall", // blackstone wall is incorrectly marked as cobblestone variant
            "minecraft:double_stone_slab2", // prismarine slab not in 1.12
            "minecraft:stone_slab2", // prismarine slab not in 1.12
            "minecraft:red_flower", // not in below 1.13
            "minecraft:pumpkin", // not in below 1.13
            "minecraft:stone_slab", // not in below 1.13
            "minecraft:structure_void", // not in below 1.13
            "minecraft:stickypistonarmcollision", // not in below 1.13
            "minecraft:light_block", // not in below 1.13
            "minecraft:wither_rose", // not in below 1.13
            "minecraft:dead_tube_coral", // not in below 1.13
            "minecraft:dead_horn_coral", // not in below 1.13
            "minecraft:dead_fire_coral", // not in below 1.13
            "minecraft:dead_bubble_coral", // not in below 1.13
            "minecraft:dead_brain_coral", // not in below 1.13
            "minecraft:coral" // dead_bit not in below 1.13
    );
    private static File dataFile;

    public static Identifier fromJson(JsonObject input) {
        Map<String, StateValue<?>> states = new Object2ObjectOpenHashMap<>();
        for (Map.Entry<String, JsonElement> entry : input.getAsJsonObject("states").entrySet()) {
            JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                states.put(entry.getKey(), StateValue.fromBoxed(primitive.getAsBoolean()));
            } else if (primitive.isString()) {
                if (primitive.getAsString().equals("*")) {
                    // Use 0 for * tests (used in legacy versions)
                    states.put(entry.getKey(), StateValue.fromBoxed(0));
                } else {
                    states.put(entry.getKey(), StateValue.fromBoxed(primitive.getAsString()));
                }
            } else {
                states.put(entry.getKey(), StateValue.fromBoxed(primitive.getAsInt()));
            }

        }
        return new Identifier(input.get("identifier").getAsString(), states);
    }

    public static Map<Identifier, Identifier> loadExpected(String fileName) throws IOException {
        try (ZipFile zipFile = new ZipFile(dataFile)) {
            try (InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(fileName));
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                // Parse the JSON
                JsonObject identifiers = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
                JsonArray blockIdentifiers = identifiers.getAsJsonArray("blocks");

                // Convert to list
                return blockIdentifiers.asList().stream().map(inputOutput -> {
                    Identifier input = fromJson(inputOutput.getAsJsonObject().getAsJsonObject("input"));
                    Identifier output;
                    if (inputOutput.getAsJsonObject().has("output")) {
                        output = fromJson(inputOutput.getAsJsonObject().getAsJsonObject("output"));
                    } else {
                        output = input;
                    }
                    return Maps.immutableEntry(input, output);
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
            }
        }
    }

    public static List<String> getIntegrationFileNames() throws IOException {
        // Copy the integration data to a temp file
        dataFile = tempFile();

        // Copy
        try {
            URL resource = Resources.getResource("integration/identifiers/chunker_data.zip");
            Resources.asByteSource(resource).copyTo(Files.asByteSink(dataFile));
        } catch (Exception e) {
            throw new IllegalArgumentException("Missing chunker_data.zip, ensure you have git lfs working as it is required for this test.");
        }

        // Now open the ZIP and get a list of the files
        try (ZipFile zipFile = new ZipFile(dataFile)) {
            return zipFile.stream().map(ZipEntry::getName).filter(name -> name.endsWith(".json"))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public static Identifier fixStates(boolean bedrock, Identifier input, Version version) {
        if (bedrock) {
            // Fixed an issue where torchflower_crop didn't handle age properly
            if (version.isGreaterThanOrEqual(1, 19, 70)) {
                if (input.getIdentifier().equals("minecraft:torchflower_crop")) {
                    input.getStates().remove("growth");
                }
            }

            // Fixed an issue where books_stored isn't converted properly
            if (input.getIdentifier().equals("minecraft:chiseled_bookshelf")) {
                input.getStates().remove("books_stored");
            }

            // Fixed incorrect lodestone block/twisting vines name on 1.16
            if (version.isLessThanOrEqual(1, 16, 0)) {
                if (input.getIdentifier().equals("minecraft:lodestone_block")) {
                    input = new Identifier("minecraft:lodestone", input.getStates());
                }
                if (input.getIdentifier().equals("minecraft:twisting_vines_block")) {
                    input = new Identifier("minecraft:twisting_vines", input.getStates());
                }
            }

            // Fixed wall_block_type being present on 1.16 bedrock
            if (version.equals(new Version(1, 16, 0))) {
                if (input.getIdentifier().equals("minecraft:polished_blackstone_brick_wall")
                        || input.getIdentifier().equals("minecraft:blackstone_wall")
                        || input.getIdentifier().equals("minecraft:polished_blackstone_wall")) {
                    input.getStates().remove("wall_block_type");
                }
            }

            // Fixed kelp_age being wrong state on 1.14
            if (input.getIdentifier().equals("minecraft:kelp")) {
                input.getStates().remove("kelp_age");
                input.getStates().remove("age");
            }

            // Fixed an issue where signs had invalid rotation values
            if (input.getIdentifier().equals("minecraft:wall_sign")) {
                input.getStates().remove("facing_direction");
                input.getStates().remove("waterlogged");
            }

            // Fixed rotation for muddy_mangrove_roots
            if (input.getIdentifier().equals("minecraft:muddy_mangrove_roots")) {
                input.getStates().remove("pillar_axis");
            }

            return BedrockBlockIdentifierValidationTests.removeLossyStates(input);
        } else {
            // Fixed an issue where heads had powered states when they shouldn't
            if (input.getIdentifier().endsWith("_skull") || input.getIdentifier().endsWith("_head")) {
                input.getStates().remove("powered");
            }

            // Fixed an issue where leaves were being waterlogged when they shouldn't
            if (input.getIdentifier().endsWith("_leaves")) {
                input.getStates().remove("waterlogged");
            }

            // Fixed kelp_age being wrong state on bedrock 1.14
            if (input.getIdentifier().equals("minecraft:kelp")) {
                input.getStates().remove("kelp_age");
                input.getStates().remove("age");
            }

            // Fixed an issue where torchflower_crop didn't handle age properly
            if (version.isGreaterThanOrEqual(1, 19, 4)) {
                if (input.getIdentifier().equals("minecraft:torchflower_crop")) {
                    input.getStates().remove("age");
                }
            }

            // Fixed an issue where chiseled bookshelves weren't using the right states from older versions
            if (input.getIdentifier().equals("minecraft:chiseled_bookshelf")) {
                input.getStates().remove("slot_0_occupied");
                input.getStates().remove("slot_1_occupied");
                input.getStates().remove("slot_2_occupied");
                input.getStates().remove("slot_3_occupied");
                input.getStates().remove("slot_4_occupied");
                input.getStates().remove("slot_5_occupied");
            }

            // Fixed rotation for muddy_mangrove_roots
            if (input.getIdentifier().equals("minecraft:muddy_mangrove_roots")) {
                input.getStates().remove("axis");
            }

            // Fixed lantern being waterlogged in the wrong versions
            if (input.getIdentifier().endsWith("lantern")) {
                input.getStates().remove("waterlogged");
            }
        }
        return input;
    }

    public static File tempFile() throws IOException {
        File tempFile = File.createTempFile("resource", ".dat");
        tempFile.deleteOnExit();
        return tempFile;
    }

    @ParameterizedTest
    @MethodSource("getIntegrationFileNames")
    public void testIdentifiers(String fileName) throws IOException {
        Map<Identifier, Identifier> expected = loadExpected(fileName);

        String[] parts = fileName.replace(".json", "").split(Pattern.quote("_to_"));
        String inputName = parts[0];
        String inputType = inputName.substring(0, inputName.indexOf("_"));
        Version inputVersion = Version.fromString(inputName.substring(inputName.indexOf("_") + 1).replace("_", "."));
        Resolver<Identifier, ChunkerBlockIdentifier> inputToChunker = createResolver(true, inputType, inputVersion);

        // Ensure this format is supported
        assertNotNull(inputToChunker, "Input format/version is not currently supported!");

        String outputName = parts[1];
        String outputType = outputName.substring(0, outputName.indexOf("_"));
        Version outputVersion = Version.fromString(outputName.substring(outputName.indexOf("_") + 1).replace("_", "."));
        Resolver<Identifier, ChunkerBlockIdentifier> chunkerToOutput = createResolver(false, outputType, outputVersion);

        // Ensure this format is supported
        assertNotNull(chunkerToOutput, "Output format/version is not currently supported!");
        for (Map.Entry<Identifier, Identifier> entry : expected.entrySet()) {
            Identifier input = entry.getKey();

            // The expected output should have lossy states removed as they aren't tested
            Identifier expectedOutput = fixStates(outputType.equals("bedrock"), entry.getValue(), outputVersion);

            // Ensure the value is supported / not lossy
            if (inputType.equals("bedrock") && (BedrockBlockIdentifierValidationTests.UNSUPPORTED_BLOCKS.contains(input.getIdentifier()) ||
                    BedrockBlockIdentifierValidationTests.isLossyBlock(input))) {
                continue;
            }

            // Convert the value
            Optional<ChunkerBlockIdentifier> chunkerIdentifier = inputToChunker.to(input);
            assertTrue(chunkerIdentifier.isPresent() || MISSING_BLOCKS.contains(input.getIdentifier()), () -> "Could not map " + input + " to Chunker expected " + expectedOutput);

            if (chunkerIdentifier.isPresent()) {
                Optional<Identifier> outputOptional = chunkerToOutput.from(chunkerIdentifier.get());
                boolean present = outputOptional.isPresent() || MISSING_BLOCKS.contains(input.getIdentifier());
                assertTrue(present, () -> "Could not map " + input + " to output expected " + expectedOutput + " Chunker: " + chunkerIdentifier.get());

                if (outputOptional.isPresent()) {
                    // Attempt to fix states to ensure we've accounted for known fixes
                    final Identifier output = fixStates(outputType.equals("bedrock"), outputOptional.get(), outputVersion);

                    // Remove any virtual states (used for block connections in old mappings)
                    expectedOutput.getStates().entrySet().removeIf(a -> a.getKey().startsWith("virtual:"));

                    // We use a containsAll, as we have several states on the new mappings that fix default states being missing
                    // If a state changes, we could that as a regression
                    boolean result = equalsContainsAll(expectedOutput, output) || isFixed(inputType.equals("java"), input) || isFixed(outputType.equals("java"), output);
                    assertTrue(result, () -> "Mappings differed, for input " + input + " got: " + output + " expected: " + expectedOutput + " Chunker: " + chunkerIdentifier.get());
                }
            }
        }
    }

    private boolean equalsContainsAll(Identifier expectedOutput, Identifier output) {
        return output.getIdentifier().equals(expectedOutput.getIdentifier()) && output.getStates().entrySet().containsAll(expectedOutput.getStates().entrySet());
    }

    public Resolver<Identifier, ChunkerBlockIdentifier> createResolver(boolean reader, String type, Version version) {
        if (type.equals("java")) {
            if (version.isLessThan(1, 13, 0)) {
                return new JavaLegacyBlockIdentifierResolver(new MockConverter(null), version, reader, false);
            }
            return new JavaBlockIdentifierResolver(new MockConverter(null), version, reader, false);
        } else if (type.equals("bedrock")) {
            if (version.isLessThan(1, 13, 0)) {
                return new BedrockLegacyBlockIdentifierResolver(new MockConverter(null), version, reader, false);
            }
            return new BedrockBlockIdentifierResolver(new MockConverter(null), version, reader, false);
        } else {
            throw new IllegalArgumentException("Type not supported " + type);
        }
    }

    public boolean isFixed(boolean java, Identifier input) {
        // Fixes for java data values
        if (java && input.getStates().containsKey("data") && JAVA_DATA_FIXES.contains(input.getIdentifier())) {
            return true;
        }

        // Fixes for bedrock data values
        if (!java && input.getStates().containsKey("data") && BEDROCK_DATA_FIXES.contains(input.getIdentifier())) {
            return true;
        }

        Collection<Map<String, StateValue<?>>> list = FIXED_MAPPINGS.get(input.getIdentifier());
        if (list.isEmpty()) return false;

        // Check each entry for intersection
        for (Map<String, StateValue<?>> entry : list) {
            if (input.getStates().entrySet().containsAll(entry.entrySet())) {
                return true;
            }
        }
        return false;
    }
}
