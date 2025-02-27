package com.hivemc.chunker.conversion.bedrock.resolver;

import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockIdentifierResolver;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.identifier.states.StateValueBoolean;
import com.hivemc.chunker.mapping.identifier.states.StateValueInt;
import com.hivemc.chunker.mapping.identifier.states.StateValueString;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests to ensure that all Bedrock block identifiers are mapped to valid Chunker / Minecraft values.
 */
public class BedrockBlockIdentifierValidationTests {
    public static final Multimap<String, Map<String, StateValue<?>>> LOSSY_BLOCKS = ImmutableMultimap.<String, Map<String, StateValue<?>>>builder()
            .put("minecraft:cherry_wood", Map.of("stripped_bit", StateValueBoolean.TRUE)) // Remapped to stripped_cherry_wood
            .put("minecraft:mangrove_wood", Map.of("stripped_bit", StateValueBoolean.TRUE)) // Remapped to stripped_mangrove_wood
            .put("minecraft:double_stone_block_slab", Map.of("stone_slab_type", new StateValueString("wood"))) // Remapped to oak_slab on some versions
            .put("minecraft:stone_block_slab", Map.of("stone_slab_type", new StateValueString("wood"))) // Remapped to double_oak_slab on some versions
            .put("minecraft:red_mushroom_block", Map.of("huge_mushroom_bits", new StateValueInt(10))) // Remapped to mushroom_stem (stem)
            .put("minecraft:red_mushroom_block", Map.of("huge_mushroom_bits", new StateValueInt(15))) // Remapped to mushroom_stem (stem all faces)
            .put("minecraft:brown_mushroom_block", Map.of("huge_mushroom_bits", new StateValueInt(10))) // Remapped to mushroom_stem (stem)
            .put("minecraft:brown_mushroom_block", Map.of("huge_mushroom_bits", new StateValueInt(15))) // Remapped to mushroom_stem (stem all faces)
            .put("minecraft:lava_cauldron", Map.of()) // Migrated to :cauldron[cauldron_liquid=lava]
            .put("minecraft:glow_frame", Map.of()) // Becomes a block entity
            .put("minecraft:frame", Map.of()) // Becomes a block entity
            .put("minecraft:jigsaw", Map.of()) // Not all rotations are converted
            .build();
    public static final Set<String> UNSUPPORTED_BLOCKS = ImmutableSet.of(
            // Debug/Technical blocks
            "minecraft:unknown",
            "minecraft:info_update",
            "minecraft:info_update2",
            "minecraft:reserved6",
            "minecraft:client_request_placeholder_block",

            // Old unused blocks
            "minecraft:glowingobsidian",
            "minecraft:netherreactor",
            "minecraft:stonecutter",
            "minecraft:mysterious_frame",
            "minecraft:mysterious_frame_slot",

            // Education Blocks
            "minecraft:chalkboard",
            "minecraft:camera",
            "minecraft:allow",
            "minecraft:deny",
            "minecraft:border_block",
            "minecraft:chemical_heat",
            "minecraft:chemistry_table",
            "minecraft:lab_table",
            "minecraft:material_reducer",
            "minecraft:element_constructor",
            "minecraft:compound_creator",
            "minecraft:colored_torch_bp",
            "minecraft:colored_torch_rg",
            "minecraft:colored_torch_blue",
            "minecraft:colored_torch_green",
            "minecraft:colored_torch_purple",
            "minecraft:colored_torch_red",
            "minecraft:hard_glass",
            "minecraft:hard_glass_pane",
            "minecraft:hard_stained_glass",
            "minecraft:hard_black_stained_glass",
            "minecraft:hard_blue_stained_glass",
            "minecraft:hard_brown_stained_glass",
            "minecraft:hard_cyan_stained_glass",
            "minecraft:hard_gray_stained_glass",
            "minecraft:hard_green_stained_glass",
            "minecraft:hard_light_blue_stained_glass",
            "minecraft:hard_light_gray_stained_glass",
            "minecraft:hard_lime_stained_glass",
            "minecraft:hard_magenta_stained_glass",
            "minecraft:hard_orange_stained_glass",
            "minecraft:hard_pink_stained_glass",
            "minecraft:hard_purple_stained_glass",
            "minecraft:hard_red_stained_glass",
            "minecraft:hard_white_stained_glass",
            "minecraft:hard_yellow_stained_glass",
            "minecraft:hard_stained_glass_pane",
            "minecraft:hard_black_stained_glass_pane",
            "minecraft:hard_blue_stained_glass_pane",
            "minecraft:hard_brown_stained_glass_pane",
            "minecraft:hard_cyan_stained_glass_pane",
            "minecraft:hard_gray_stained_glass_pane",
            "minecraft:hard_green_stained_glass_pane",
            "minecraft:hard_light_blue_stained_glass_pane",
            "minecraft:hard_light_gray_stained_glass_pane",
            "minecraft:hard_lime_stained_glass_pane",
            "minecraft:hard_magenta_stained_glass_pane",
            "minecraft:hard_orange_stained_glass_pane",
            "minecraft:hard_pink_stained_glass_pane",
            "minecraft:hard_purple_stained_glass_pane",
            "minecraft:hard_red_stained_glass_pane",
            "minecraft:hard_white_stained_glass_pane",
            "minecraft:hard_yellow_stained_glass_pane",
            "minecraft:underwater_torch",

            // Elements
            "minecraft:element_0",
            "minecraft:element_1",
            "minecraft:element_2",
            "minecraft:element_3",
            "minecraft:element_4",
            "minecraft:element_5",
            "minecraft:element_6",
            "minecraft:element_7",
            "minecraft:element_8",
            "minecraft:element_9",
            "minecraft:element_10",
            "minecraft:element_11",
            "minecraft:element_12",
            "minecraft:element_13",
            "minecraft:element_14",
            "minecraft:element_15",
            "minecraft:element_16",
            "minecraft:element_17",
            "minecraft:element_18",
            "minecraft:element_19",
            "minecraft:element_20",
            "minecraft:element_21",
            "minecraft:element_22",
            "minecraft:element_23",
            "minecraft:element_24",
            "minecraft:element_25",
            "minecraft:element_26",
            "minecraft:element_27",
            "minecraft:element_28",
            "minecraft:element_29",
            "minecraft:element_30",
            "minecraft:element_31",
            "minecraft:element_32",
            "minecraft:element_33",
            "minecraft:element_34",
            "minecraft:element_35",
            "minecraft:element_36",
            "minecraft:element_37",
            "minecraft:element_38",
            "minecraft:element_39",
            "minecraft:element_40",
            "minecraft:element_41",
            "minecraft:element_42",
            "minecraft:element_43",
            "minecraft:element_44",
            "minecraft:element_45",
            "minecraft:element_46",
            "minecraft:element_47",
            "minecraft:element_48",
            "minecraft:element_49",
            "minecraft:element_50",
            "minecraft:element_51",
            "minecraft:element_52",
            "minecraft:element_53",
            "minecraft:element_54",
            "minecraft:element_55",
            "minecraft:element_56",
            "minecraft:element_57",
            "minecraft:element_58",
            "minecraft:element_59",
            "minecraft:element_60",
            "minecraft:element_61",
            "minecraft:element_62",
            "minecraft:element_63",
            "minecraft:element_64",
            "minecraft:element_65",
            "minecraft:element_66",
            "minecraft:element_67",
            "minecraft:element_68",
            "minecraft:element_69",
            "minecraft:element_70",
            "minecraft:element_71",
            "minecraft:element_72",
            "minecraft:element_73",
            "minecraft:element_74",
            "minecraft:element_75",
            "minecraft:element_76",
            "minecraft:element_77",
            "minecraft:element_78",
            "minecraft:element_79",
            "minecraft:element_80",
            "minecraft:element_81",
            "minecraft:element_82",
            "minecraft:element_83",
            "minecraft:element_84",
            "minecraft:element_85",
            "minecraft:element_86",
            "minecraft:element_87",
            "minecraft:element_88",
            "minecraft:element_89",
            "minecraft:element_90",
            "minecraft:element_91",
            "minecraft:element_92",
            "minecraft:element_93",
            "minecraft:element_94",
            "minecraft:element_95",
            "minecraft:element_96",
            "minecraft:element_97",
            "minecraft:element_98",
            "minecraft:element_99",
            "minecraft:element_100",
            "minecraft:element_101",
            "minecraft:element_102",
            "minecraft:element_103",
            "minecraft:element_104",
            "minecraft:element_105",
            "minecraft:element_106",
            "minecraft:element_107",
            "minecraft:element_108",
            "minecraft:element_109",
            "minecraft:element_110",
            "minecraft:element_111",
            "minecraft:element_112",
            "minecraft:element_113",
            "minecraft:element_114",
            "minecraft:element_115",
            "minecraft:element_116",
            "minecraft:element_117",
            "minecraft:element_118"
    );
    private static final Gson gson = new Gson();

    public static boolean isLossyBlock(Identifier input) {
        Collection<Map<String, StateValue<?>>> list = LOSSY_BLOCKS.get(input.getIdentifier());
        if (list.isEmpty()) return false;

        // Check each entry for intersection
        for (Map<String, StateValue<?>> entry : list) {
            if (input.getStates().entrySet().containsAll(entry.entrySet())) {
                return true;
            }
        }
        return false;
    }

    public static Identifier removeLossyStates(Identifier input) {
        Map<String, StateValue<?>> clonedStates = new Object2ObjectOpenHashMap<>(input.getStates());

        // Deprecated states
        if (input.getIdentifier().equals("minecraft:bone_block") ||
                input.getIdentifier().equals("minecraft:hay_block") ||
                input.getIdentifier().equals("minecraft:stripped_crimson_hyphae") ||
                input.getIdentifier().equals("minecraft:stripped_crimson_stem") ||
                input.getIdentifier().equals("minecraft:stripped_warped_hyphae") ||
                input.getIdentifier().equals("minecraft:stripped_warped_stem")) {
            clonedStates.remove("deprecated");
        }

        // Redstone signal isn't currently converted for pressure plates
        if (input.getIdentifier().contains("_pressure_plate")) {
            clonedStates.remove("redstone_signal");
        }

        // Natural bit isn't stored
        if (input.getIdentifier().equals("minecraft:creaking_heart")) {
            clonedStates.remove("natural");
        }

        // Hanging bit isn't stored
        if (input.getIdentifier().startsWith("minecraft:suspicious_")) {
            clonedStates.remove("hanging");
        }

        // Technical states (states used for game logic that aren't required)
        if (input.getIdentifier().equals("minecraft:flower_pot")) {
            clonedStates.remove("update_bit");
        }
        if (input.getIdentifier().equals("minecraft:skull")) {
            // Skull no drop bit (removed)
            clonedStates.remove("no_drop_bit");
        }
        if (input.getIdentifier().equals("minecraft:bell")) {
            // Bell toggle bit
            clonedStates.remove("toggle_bit");
        }
        if (input.getIdentifier().equals("minecraft:snow_layer")) {
            // Snow covered bit
            clonedStates.remove("covered_bit");
        }
        if (input.getIdentifier().equals("minecraft:chiseled_bookshelf")) {
            // Last interacted book (removed in 1.19.60)
            clonedStates.remove("last_interacted_slot");
        }
        if (input.getIdentifier().endsWith("torch")) {
            // Unknown torch direction
            clonedStates.replace("torch_facing_direction", new StateValueString("unknown"), new StateValueString("top"));
        }

        // Unused states
        if (input.getIdentifier().equals("minecraft:pumpkin")) {
            // Pumpkin doesn't have direction
            clonedStates.remove("minecraft:cardinal_direction");
            clonedStates.remove("direction");
        }
        if (input.getIdentifier().equals("minecraft:portal")) {
            // Doesn't support unknown portal axis
            clonedStates.replace("portal_axis", new StateValueString("unknown"), new StateValueString("x"));
        }
        if (input.getIdentifier().contains("double") && input.getIdentifier().contains("slab")) {
            // Double slabs can have the half, which needs removing
            clonedStates.remove("minecraft:vertical_half");
            clonedStates.remove("top_slot_bit");
        }
        if (input.getIdentifier().equals("minecraft:bamboo_sapling")) {
            // Bamboo sapling sapling_type
            clonedStates.remove("sapling_type");
        }
        if (input.getIdentifier().equals("minecraft:hopper")) {
            // Hopper (unused facing_direction bits)
            clonedStates.replace("facing_direction", new StateValueInt(1), new StateValueInt(0));
            clonedStates.replace("facing_direction", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("facing_direction", new StateValueInt(7), new StateValueInt(0));
        }
        // Horizontal rotatable blocks (which have unused facing_direction bits)
        if (input.getIdentifier().contains("_glazed_terracotta") ||
                input.getIdentifier().contains("ladder") ||
                input.getIdentifier().contains("wall_") ||
                input.getIdentifier().equals("minecraft:skull") ||
                input.getIdentifier().endsWith("_skull") ||
                input.getIdentifier().endsWith("_head") ||
                input.getIdentifier().endsWith("_stem") ||
                input.getIdentifier().endsWith("chest") ||
                input.getIdentifier().endsWith("furnace") ||
                input.getIdentifier().endsWith("smoker") ||
                input.getIdentifier().endsWith("stonecutter_block") ||
                input.getIdentifier().endsWith("_hanging_sign") ||
                input.getIdentifier().equals("minecraft:bee_nest") ||
                input.getIdentifier().equals("minecraft:beehive")) {
            clonedStates.replace("facing_direction", new StateValueInt(0), new StateValueInt(2));
            clonedStates.replace("facing_direction", new StateValueInt(1), new StateValueInt(2));
            clonedStates.replace("facing_direction", new StateValueInt(6), new StateValueInt(2));
            clonedStates.replace("facing_direction", new StateValueInt(7), new StateValueInt(2));
        }
        if (input.getIdentifier().endsWith("_rod")
                || input.getIdentifier().endsWith("piston")
                || input.getIdentifier().endsWith("pistonArmCollision")
        ) {
            // Unused facing states
            clonedStates.replace("facing_direction", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("facing_direction", new StateValueInt(7), new StateValueInt(0));
        }
        if (input.getIdentifier().equals("minecraft:dispenser")
                || input.getIdentifier().equals("minecraft:barrel")
                || input.getIdentifier().endsWith("command_block")
                || input.getIdentifier().equals("minecraft:dropper")
                || input.getIdentifier().equals("minecraft:observer")
        ) {
            // Every direction with bool (unused states)
            clonedStates.replace("facing_direction", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("facing_direction", new StateValueInt(7), new StateValueInt(0));
        }
        if (input.getIdentifier().contains("_hanging_sign")) {
            // hanging_signs either use ground_sign_direction or facing_direction depending on hanging
            if (input.getStates().get("hanging").getBoxed().equals(false)) {
                clonedStates.remove("ground_sign_direction");
            } else {
                clonedStates.remove("facing_direction");
            }
        }
        if (input.getIdentifier().endsWith("_button")) {
            // Unused facing states
            clonedStates.replace("facing_direction", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("facing_direction", new StateValueInt(7), new StateValueInt(0));
        }
        if (input.getIdentifier().endsWith("_log")
                || input.getIdentifier().equals("minecraft:log")
                || input.getIdentifier().equals("minecraft:log2")
                || input.getIdentifier().equals("minecraft:purpur_block")
                || input.getIdentifier().equals("minecraft:quartz_block")
                || input.getIdentifier().equals("minecraft:hay_block")
                || input.getIdentifier().equals("minecraft:bone_block")
        ) {
            // Unused facing states (axis)
            clonedStates.replace("direction", new StateValueInt(3), new StateValueInt(0));
        }
        if (input.getIdentifier().equals("minecraft:detector_rail") || input.getIdentifier().equals("minecraft:activator_rail") || input.getIdentifier().equals("minecraft:golden_rail")) {
            // Powered Rails (unknown state)
            clonedStates.replace("rail_direction", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("rail_direction", new StateValueInt(7), new StateValueInt(0));
        }
        if (input.getIdentifier().equals("minecraft:rail")) {
            // Rail (unknown states)
            clonedStates.replace("rail_direction", new StateValueInt(10), new StateValueInt(0));
            clonedStates.replace("rail_direction", new StateValueInt(11), new StateValueInt(0));
            clonedStates.replace("rail_direction", new StateValueInt(12), new StateValueInt(0));
            clonedStates.replace("rail_direction", new StateValueInt(13), new StateValueInt(0));
            clonedStates.replace("rail_direction", new StateValueInt(14), new StateValueInt(0));
            clonedStates.replace("rail_direction", new StateValueInt(15), new StateValueInt(0));
        }
        if (input.getIdentifier().equals("minecraft:chorus_flower")) {
            // Extra states
            clonedStates.replace("age", new StateValueInt(6), new StateValueInt(5));
            clonedStates.replace("age", new StateValueInt(7), new StateValueInt(5));
        }
        if (input.getIdentifier().equals("minecraft:cauldron")) {
            // Extra level state
            clonedStates.replace("fill_level", new StateValueInt(7), new StateValueInt(6));
        }
        if (input.getIdentifier().equals("minecraft:composter")) {
            // Extra fill states
            clonedStates.replace("composter_fill_level", new StateValueInt(9), new StateValueInt(8));
            clonedStates.replace("composter_fill_level", new StateValueInt(10), new StateValueInt(8));
            clonedStates.replace("composter_fill_level", new StateValueInt(11), new StateValueInt(8));
            clonedStates.replace("composter_fill_level", new StateValueInt(12), new StateValueInt(8));
            clonedStates.replace("composter_fill_level", new StateValueInt(13), new StateValueInt(8));
            clonedStates.replace("composter_fill_level", new StateValueInt(14), new StateValueInt(8));
            clonedStates.replace("composter_fill_level", new StateValueInt(15), new StateValueInt(8));
        }
        if (input.getIdentifier().equals("minecraft:cake")) {
            clonedStates.replace("bite_counter", new StateValueInt(7), new StateValueInt(6));
        }
        if (input.getIdentifier().equals("minecraft:cocoa")) {
            // Cocoa block (unused states)
            clonedStates.replace("age", new StateValueInt(3), new StateValueInt(2));
        }

        // Same texture replacements
        if (input.getIdentifier().contains("minecraft:stonebrick")) {
            clonedStates.replace("stone_brick_type", new StateValueString("smooth"), new StateValueString("default"));
            if (clonedStates.containsValue(new StateValueString("default"))) {
                clonedStates.remove("minecraft:pillar_axis");
            }
        }
        if (input.getIdentifier().equals("minecraft:purpur_block")) {
            if (clonedStates.containsKey("chisel_type")) {
                // Both these states just use the normal block
                clonedStates.replace("chisel_type", new StateValueString("smooth"), new StateValueString("default"));
                clonedStates.replace("chisel_type", new StateValueString("chiseled"), new StateValueString("default"));
            }

            // Unless the purpur_block is lines then the direction isn't visible
            if (!clonedStates.containsKey("chisel_type") || !clonedStates.get("chisel_type").equals(new StateValueString("lines"))) {
                clonedStates.remove("pillar_axis");
                clonedStates.remove("direction");
            }
        }
        if (input.getIdentifier().equals("minecraft:quartz_block")
                || input.getIdentifier().equals("minecraft:smooth_quartz")
                || input.getIdentifier().equals("minecraft:chiseled_quartz_block")) {
            // Unless the quartz_block is lines then the direction isn't visible
            if (!clonedStates.containsKey("chisel_type") || !clonedStates.get("chisel_type").equals(new StateValueString("lines"))) {
                clonedStates.remove("pillar_axis");
                clonedStates.remove("direction");
            }
        }
        if (input.getIdentifier().contains("minecraft:deprecated_purpur_block_")) {
            input = new Identifier("minecraft:purpur_block", input.getStates());

            // Remove rotation
            clonedStates.remove("pillar_axis");
            clonedStates.remove("direction");
        }
        if (input.getIdentifier().contains("minecraft:deprecated_anvil")) {
            input = new Identifier("minecraft:damaged_anvil", input.getStates());
        }
        if (input.getIdentifier().contains("minecraft:anvil")) {
            // Inaccessible state with same texture as base
            clonedStates.replace("damage", new StateValueString("broken"), new StateValueString("very_damaged"));
        }
        if (input.getIdentifier().contains("minecraft:cauldron")) {
            if (input.getStates().containsKey("fill_level") && input.getStates().get("fill_level").getBoxed().equals(0)) {
                // All empty cauldrons are equal
                clonedStates.replace("cauldron_liquid", new StateValueString("water"));
            }
        }
        if (input.getIdentifier().contains("minecraft:mangrove_propagule")) {
            // This was removed in later versions
            clonedStates.remove("facing_direction");
            if (input.getStates().containsKey("growth") && ((StateValueInt) input.getStates().get("growth")).getValue() >= 5) {
                // Looks the same after state 4
                clonedStates.replace("growth", new StateValueInt(4));
            }
        }
        if (input.getIdentifier().contains("minecraft:coral_fan_hang3")) {
            // There is only one type of coral on fan_hang3
            clonedStates.replace("coral_hang_type_bit", StateValueBoolean.FALSE);
        }
        if ((input.getIdentifier().equals("minecraft:melon_stem") || input.getIdentifier().equals("minecraft:pumpkin_stem")) && input.getStates().containsKey("growth") && !input.getStates().get("growth").getBoxed().equals(7)) {
            // Stem direction only matters at stage 7
            clonedStates.replace("facing_direction", new StateValueInt(2));
        }
        if ((input.getIdentifier().equals("minecraft:big_dripleaf")) && input.getStates().containsKey("big_dripleaf_head") && input.getStates().get("big_dripleaf_head").getBoxed().equals(false)) {
            // Tilt is not visible on non-head parts
            clonedStates.replace("big_dripleaf_tilt", new StateValueString("none"));
        }
        if (input.getIdentifier().endsWith("_comparator")) {
            // output_lit_bit is just if it's powered
            clonedStates.remove("output_lit_bit");
        }
        if (input.getIdentifier().equals("minecraft:torchflower_crop") && input.getStates().containsKey("growth") && ((StateValueInt) input.getStates().get("growth")).getValue() >= 1) {
            // Looks the same after state 1
            clonedStates.replace("growth", new StateValueInt(7));
        }
        if ((input.getIdentifier().equals("minecraft:pink_petals") || input.getIdentifier().equals("minecraft:wildflowers") || input.getIdentifier().equals("minecraft:leaf_litter")) && input.getStates().containsKey("growth") && ((StateValueInt) input.getStates().get("growth")).getValue() >= 4) {
            // Looks the same after state 4
            clonedStates.replace("growth", new StateValueInt(3));
        }
        if (input.getIdentifier().equals("minecraft:pitcher_crop") && input.getStates().containsKey("growth") && ((StateValueInt) input.getStates().get("growth")).getValue() >= 4) {
            // Looks the same after state 4
            clonedStates.replace("growth", new StateValueInt(3));
        }
        if (input.getIdentifier().endsWith("_mushroom_block")) {
            // There are a few values for pores on all faces
            clonedStates.replace("huge_mushroom_bits", new StateValueInt(11), new StateValueInt(0));
            clonedStates.replace("huge_mushroom_bits", new StateValueInt(12), new StateValueInt(0));
            clonedStates.replace("huge_mushroom_bits", new StateValueInt(13), new StateValueInt(0));
        }
        if (input.getIdentifier().equals("minecraft:mushroom_stem")) {
            int bits = ((StateValueInt) input.getStates().get("huge_mushroom_bits")).getValue();
            if (bits != 10 && bits != 15) {
                // All stems are used for the other faces
                clonedStates.put("huge_mushroom_bits", new StateValueInt(15));
            }
        }
        if (input.getIdentifier().equals("minecraft:tallgrass")) {
            // Duplicate textures (which don't seem to be used)
            clonedStates.replace("tall_grass_type", new StateValueString("default"), new StateValueString("tall"));
            clonedStates.replace("tall_grass_type", new StateValueString("snow"), new StateValueString("fern"));
        }

        return new Identifier(input.getIdentifier().toLowerCase(), clonedStates); // Make identifiers lower case to avoid case issues
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @TestFactory
    Stream<DynamicNode> perVersionTests() {
        List<File> directories = new ArrayList<>(List.of(Objects.requireNonNull(Path.of("data", "bedrock").toFile().listFiles(File::isDirectory))));
        directories.sort(Comparator.comparing((File a) -> Version.fromString(a.getName())));

        return directories.stream().<DynamicNode>map(dataDirectory -> {
            Version version = Version.fromString(dataDirectory.getName());
            File blockStates = new File(dataDirectory, "block_states.json");
            // Create the test class
            VersionTest versionTest = new VersionTest(version, blockStates);
            Map<String, Set<Map<String, StateValue<?>>>> blocks = versionTest.blocks();
            return dynamicContainer("Bedrock " + dataDirectory.getName(), Stream.of(
                    dynamicContainer("Bedrock identifiers map to a Chunker output", blocks
                            .entrySet()
                            .stream()
                            .map(input -> dynamicTest(input.getKey(),
                                    () -> assertAll(input.getKey(), input.getValue().stream().map((values) -> {
                                        Identifier identifier = new Identifier(input.getKey(), values);
                                        return () -> versionTest.checkInputIdentifierMapped(identifier);
                                    }))
                            ))
                    ),
                    dynamicContainer("Bedrock to Chunker produces valid output (all states present)", blocks
                            .entrySet()
                            .stream()
                            .map(input -> dynamicTest(input.getKey(),
                                    () -> assertAll(input.getKey(), input.getValue().stream().map((values) -> {
                                        Identifier identifier = new Identifier(input.getKey(), values);
                                        return () -> versionTest.checkIdentifierOutputStates(identifier);
                                    }))
                            ))
                    ),
                    dynamicContainer("Bedrock identifiers are lossless (Bedrock -> Chunker -> Bedrock)", blocks
                            .entrySet()
                            .stream()
                            .map(input -> dynamicTest(input.getKey(),
                                    () -> assertAll(input.getKey(), input.getValue().stream().map((values) -> {
                                        Identifier identifier = new Identifier(input.getKey(), values);
                                        return () -> versionTest.checkInputIdentifierSymmetry(identifier);
                                    }))
                            ))
                    ),
                    dynamicContainer("Chunker identifiers maps to a Bedrock input", Stream.of(ChunkerVanillaBlockType.values())
                            .filter(versionTest::isSupported)
                            .map(input -> dynamicTest(input.name(),
                                    () -> assertAll(input.name(),
                                            Lists.cartesianProduct(input.getStates().stream()
                                                            .map(a -> List.of(a.getValues())).collect(Collectors.toList()))
                                                    .stream()
                                                    .map(a -> Streams.zip(input.getStates().stream(), a.stream(), Maps::immutableEntry).collect(Collectors.toMap(
                                                            Map.Entry::getKey, Map.Entry::getValue
                                                    )))
                                                    .map((states) -> {
                                                        ChunkerBlockIdentifier identifier = new ChunkerBlockIdentifier(input, (Map<BlockState<?>, BlockStateValue>) (Map) states);
                                                        return () -> versionTest.checkOutputIdentifierMapped(identifier);
                                                    }))
                            ))
                    ),
                    dynamicContainer("Chunker to Bedrock produces valid output (all states present)", Stream.of(ChunkerVanillaBlockType.values())
                            .map(input -> dynamicTest(input.name(),
                                    () -> assertAll(input.name(),
                                            Lists.cartesianProduct(input.getStates().stream()
                                                            .map(a -> List.of(a.getValues())).collect(Collectors.toList()))
                                                    .stream()
                                                    .map(a -> Streams.zip(input.getStates().stream(), a.stream(), Maps::immutableEntry).collect(Collectors.toMap(
                                                            Map.Entry::getKey, Map.Entry::getValue
                                                    )))
                                                    .map((states) -> {
                                                        ChunkerBlockIdentifier identifier = new ChunkerBlockIdentifier(input, (Map<BlockState<?>, BlockStateValue>) (Map) states);
                                                        return () -> versionTest.checkIdentifierInputStates(blocks, identifier);
                                                    }))
                            ))
                    )
            ));
        }).filter(Objects::nonNull);
    }

    public static class VersionTest {
        private final JsonArray blockStates;
        private final BedrockBlockIdentifierResolver resolver;

        public VersionTest(Version version, File blockStates) {
            resolver = new BedrockBlockIdentifierResolver(new MockConverter(null), version, true, false);
            try (FileInputStream fis = new FileInputStream(blockStates)) {
                JsonObject root = gson.fromJson(new InputStreamReader(fis), JsonObject.class);
                this.blockStates = root.getAsJsonArray("blocks");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Map<String, Set<Map<String, StateValue<?>>>> blocks() {
            Map<String, Set<Map<String, StateValue<?>>>> blocks = new TreeMap<>();
            for (JsonElement element : blockStates) {
                JsonObject blockState = element.getAsJsonObject();

                // Create a map of the states
                Map<String, StateValue<?>> stateMap = new Object2ObjectOpenHashMap<>();
                if (blockState.has("states") && blockState.get("states").isJsonArray()) {
                    for (JsonElement stateElement : blockState.get("states").getAsJsonArray()) {
                        JsonObject state = stateElement.getAsJsonObject();
                        String name = state.get("name").getAsString();
                        StateValue<?> value = switch (state.get("type").getAsString()) {
                            case "int" -> new StateValueInt(state.get("value").getAsInt());
                            case "byte" -> {
                                if (state.get("value").getAsByte() == (byte) 1) {
                                    yield StateValueBoolean.TRUE;
                                } else {
                                    yield StateValueBoolean.FALSE;
                                }
                            }
                            case "string" -> new StateValueString(state.get("value").getAsString());
                            default ->
                                    throw new IllegalArgumentException("Unknown state type " + state.get("type").getAsString());
                        };

                        stateMap.put(name, value);
                    }
                }

                // Get the list and add the entry
                Set<Map<String, StateValue<?>>> stateMaps = blocks.computeIfAbsent(blockState.get("name").getAsString(), (ignored) -> new LinkedHashSet<>());

                // Add to the list of state maps with waterlogged not set
                Map<String, StateValue<?>> stateMapClone = new Object2ObjectOpenHashMap<>(stateMap);
                stateMap.put("waterlogged", StateValueBoolean.FALSE);
                stateMaps.add(stateMap);

                // Add to the list of state maps with waterlogged not set
                stateMapClone.put("waterlogged", StateValueBoolean.TRUE);
                stateMaps.add(stateMapClone);
            }
            return blocks;
        }

        public void checkInputIdentifierMapped(Identifier input) {
            // If the block is present it shouldn't be an unsupported block
            assertEquals(UNSUPPORTED_BLOCKS.contains(input.getIdentifier()), resolver.to(input).isEmpty(), () -> input + " is not mapped.");
        }

        public void checkInputIdentifierSymmetry(Identifier input) {
            if (!UNSUPPORTED_BLOCKS.contains(input.getIdentifier())) {
                // Ensure block isn't known to be lossy
                if (isLossyBlock(input)) {
                    return;
                }

                Optional<ChunkerBlockIdentifier> chunker = resolver.to(input);
                if (chunker.isPresent()) {
                    Optional<Identifier> converted = resolver.from(chunker.get());
                    assertTrue(converted.isPresent(), () -> "Lossy conversion for input " + input + ", could not map back using " + chunker.get());
                    if (converted.isPresent()) {
                        assertEquals(removeLossyStates(input), removeLossyStates(converted.get()), () -> "Lossy conversion for input " + input + ", got " + converted.get() + " Chunker: " + chunker.get());
                    }
                }
            }
        }

        public void checkOutputIdentifierMapped(ChunkerBlockIdentifier input) {
            // If the block is present it shouldn't be an unsupported block
            assertTrue(resolver.from(input).isPresent(), () -> input + " is not mapped.");
        }

        public void checkIdentifierInputStates(Map<String, Set<Map<String, StateValue<?>>>> blocks, ChunkerBlockIdentifier chunkerBlockIdentifier) {
            // If the block is present it shouldn't be an unsupported block
            Optional<Identifier> output = resolver.from(chunkerBlockIdentifier);
            if (output.isPresent()) {
                Identifier outputIdentifier = output.get();

                // Special case: Since bedrock has waterlogging we should add that, it's used as a virtual state to select the liquid layer
                outputIdentifier.getStates().putIfAbsent("waterlogged", StateValueBoolean.FALSE);

                Set<Map<String, StateValue<?>>> allowedOutput = blocks.get(outputIdentifier.getIdentifier());
                assertNotNull(allowedOutput, () -> "Invalid identifier " + outputIdentifier.getIdentifier() + " for input " + chunkerBlockIdentifier);
                assertTrue(allowedOutput.contains(outputIdentifier.getStates()), () -> "Invalid states " + outputIdentifier + " for input " + chunkerBlockIdentifier + ", valid states: " + allowedOutput);
            }
        }

        public void checkIdentifierOutputStates(Identifier input) {
            if (UNSUPPORTED_BLOCKS.contains(input.getIdentifier())) return;

            // If the block is present it shouldn't be an unsupported block
            Optional<ChunkerBlockIdentifier> output = resolver.to(input);
            if (output.isPresent()) {
                ChunkerBlockIdentifier outputIdentifier = output.get();

                // Ensure it's a vanilla block
                assertInstanceOf(ChunkerVanillaBlockType.class, outputIdentifier.getType());

                // Ensure all states are present
                ChunkerVanillaBlockType vanillaBlockType = (ChunkerVanillaBlockType) outputIdentifier.getType();
                for (BlockState<?> state : vanillaBlockType.getStates()) {
                    assertTrue(outputIdentifier.containsState(state), () -> "Missing output state " + state + " for input " + input);
                }
                for (BlockState<?> state : outputIdentifier.getPresentStates().keySet()) {
                    assertTrue(vanillaBlockType.getStates().contains(state), () -> "Invalid output state " + state + " for input " + input);
                }
            }
        }

        public boolean isSupported(ChunkerVanillaBlockType input) {
            // A ChunkerVanillaBlockType is marked as supported if it's possible to produce it from a mapping
            // If a block is in the original format and isn't in chunker then it will flag the checkInputIdentifierMapped unit test
            return resolver.isSupported(input);
        }
    }
}
