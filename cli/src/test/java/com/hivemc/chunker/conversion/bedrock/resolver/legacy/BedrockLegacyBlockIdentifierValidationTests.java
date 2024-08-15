package com.hivemc.chunker.conversion.bedrock.resolver.legacy;

import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hivemc.chunker.conversion.bedrock.resolver.MockConverter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy.BedrockLegacyBlockIdentifierResolver;
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
 * Tests to ensure that all Bedrock Legacy block identifiers are mapped to valid Chunker / Minecraft values.
 */
public class BedrockLegacyBlockIdentifierValidationTests {
    public static final Multimap<String, Map<String, StateValue<?>>> LOSSY_BLOCKS = ImmutableMultimap.<String, Map<String, StateValue<?>>>builder()
            .put("minecraft:stone_slab", Map.of("data", new StateValueInt(2))) // Remapped to double_oak_slab
            .put("minecraft:stone_slab", Map.of("data", new StateValueInt(10))) // Remapped to double_oak_slab
            .put("minecraft:double_stone_slab", Map.of("data", new StateValueInt(2))) // Remapped to double_oak_slab
            .put("minecraft:double_stone_slab", Map.of("data", new StateValueInt(10))) // Remapped to double_oak_slab
            .put("minecraft:red_mushroom_block", Map.of("data", new StateValueInt(10))) // Remapped to brown_mushroom_block (stem)
            .put("minecraft:red_mushroom_block", Map.of("data", new StateValueInt(15))) // Remapped to brown_mushroom_block (stem all faces)
            .put("minecraft:lava_cauldron", Map.of()) // Migrated to :cauldron[cauldron_liquid=lava]
            .put("minecraft:powered_comparator", Map.of()) // Weird power states
            .put("minecraft:unpowered_comparator", Map.of()) // Weird power states
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
            "minecraft:camera",
            "minecraft:allow",
            "minecraft:deny",
            "minecraft:border_block",
            "minecraft:chemical_heat",
            "minecraft:chemistry_table",
            "minecraft:colored_torch_bp",
            "minecraft:colored_torch_rg",
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

        // Horizontal only blocks
        if (input.getIdentifier().endsWith("_terracotta")
                || input.getIdentifier().endsWith("wall_sign")
                || input.getIdentifier().endsWith("furnace")
                || input.getIdentifier().endsWith("smoker")
                || input.getIdentifier().equals("minecraft:ladder")
                || input.getIdentifier().endsWith("chest")
                || input.getIdentifier().equals("minecraft:skull")
                || input.getIdentifier().equals("minecraft:stonecutter_block")
                || input.getIdentifier().endsWith("wall_banner")
        ) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(0), new StateValueInt(2));
            clonedStates.replace("data", new StateValueInt(1), new StateValueInt(2));
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(2));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(2));
        }

        // Pressure plates don't record power
        if (input.getIdentifier().endsWith("pressure_plate")) {
            if (input.getDataValue().orElse(0) > 1) {
                clonedStates.put("data", new StateValueInt(1));
            }
        }

        // Axis direction (unused states)
        if (input.getIdentifier().endsWith("_log")) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(3), new StateValueInt(0));
        }

        // Hopper direction (unused states)
        if (input.getIdentifier().equals("minecraft:hopper")) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(1), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(9), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(8));
        }

        // Axis direction (unused states / deprecated)
        if (input.getIdentifier().equals("minecraft:hay_block") || input.getIdentifier().equals("minecraft:bone_block")) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(1), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(2), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(3), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(5), new StateValueInt(4));
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(4));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(4));
            clonedStates.replace("data", new StateValueInt(9), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(10), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(11), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(0));
        }

        // Log direction (unused states)
        if (input.getIdentifier().equals("minecraft:log")) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(2));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(3));
        }

        // Jigsaw direction (unused states)
        if (input.getIdentifier().equals("minecraft:jigsaw")) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(0));
        }

        // Log2 direction (unused states)
        if (input.getIdentifier().equals("minecraft:log2")) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(2), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(3), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(4));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(5));
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(0));
        }

        // Bamboo Sapling (unused states)
        if (input.getIdentifier().equals("minecraft:bamboo_sapling")) {
            clonedStates.replace("data", new StateValueInt(2), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(3), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(4), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(5), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(8), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(9), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(10), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(11), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(1));
        }

        // Skull (unused states)
        if (input.getIdentifier().equals("minecraft:skull")) {
            clonedStates.replace("data", new StateValueInt(8), new StateValueInt(10));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(10));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(10));
        }

        // Torch direction (unused states)
        if (input.getIdentifier().endsWith("torch")) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(0), new StateValueInt(5));
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(5));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(5));
        }

        // Stone brick (unused states)
        if (input.getIdentifier().equals("minecraft:stonebrick")) {
            clonedStates.replace("data", new StateValueInt(4), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(5), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(0));
        }

        // Double plant (unused states)
        if (input.getIdentifier().equals("minecraft:double_plant")) {
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(9));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(9));
        }

        // Coral Block (unused states)
        if (input.getIdentifier().equals("minecraft:coral_block")) {
            clonedStates.replace("data", new StateValueInt(5), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(8));
        }

        // Every direction (unused states)
        if (input.getIdentifier().endsWith("_rod")
                || input.getIdentifier().endsWith("piston")
                || input.getIdentifier().endsWith("pistonArmCollision")
                || input.getIdentifier().equals("minecraft:barrel")
        ) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(1));
        }

        // Every direction with bool (unused states)
        if (input.getIdentifier().equals("minecraft:dispenser")
                || input.getIdentifier().equals("minecraft:barrel")
                || input.getIdentifier().endsWith("command_block")
                || input.getIdentifier().equals("minecraft:dropper")
                || input.getIdentifier().equals("minecraft:observer")
        ) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(9));
        }

        // Buttons (unused states)
        if (input.getIdentifier().endsWith("_button")) {
            // Unused facing states
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(8));
        }

        // Double slabs have an unused bit
        if (input.getIdentifier().startsWith("minecraft:double_") && input.getIdentifier().contains("slab")) {
            clonedStates.put("data", new StateValueInt(input.getDataValue().orElse(0) % 8));
        }

        // Bell has an unused bit
        if (input.getIdentifier().equals("minecraft:bell")) {
            clonedStates.put("data", new StateValueInt(input.getDataValue().orElse(0) % 16));
        }

        // Wooden slabs have empty states
        if (input.getIdentifier().startsWith("minecraft:double_wooden_slab")) {
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(0));
        }

        // Stone slab 4 has empty states
        if (input.getIdentifier().startsWith("minecraft:double_stone_slab4")) {
            clonedStates.replace("data", new StateValueInt(5), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(0));
        }

        // Snow layer have an unused bit
        if (input.getIdentifier().equals("minecraft:snow_layer")) {
            clonedStates.put("data", new StateValueInt(input.getDataValue().orElse(0) % 8));
        }

        // Coral Hang 3 has unused states
        if (input.getIdentifier().equals("minecraft:coral_fan_hang3")) {
            clonedStates.replace("data", new StateValueInt(1), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(3), new StateValueInt(2));
            clonedStates.replace("data", new StateValueInt(5), new StateValueInt(4));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(6));
            clonedStates.replace("data", new StateValueInt(9), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(11), new StateValueInt(10));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(12));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(14));
        }

        // Leaves 2 has unused states
        if (input.getIdentifier().equals("minecraft:leaves2")) {
            clonedStates.replace("data", new StateValueInt(2), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(3), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(4));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(5));
            clonedStates.replace("data", new StateValueInt(10), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(11), new StateValueInt(9));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(12));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(13));
        }

        // Flower pot (ignored bit)
        if (input.getIdentifier().equals("minecraft:flower_pot")) {
            clonedStates.replace("data", new StateValueInt(1), new StateValueInt(0));
        }

        // Cake (extra state)
        if (input.getIdentifier().equals("minecraft:cake")) {
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(6));
        }

        // Tall grass (identical states)
        if (input.getIdentifier().equals("minecraft:tallgrass")) {
            clonedStates.replace("data", new StateValueInt(0), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(3), new StateValueInt(2));
        }

        // Mushroom block (duplicate pore states)
        if (input.getIdentifier().equals("minecraft:brown_mushroom_block") || input.getIdentifier().equals("minecraft:red_mushroom_block")) {
            clonedStates.replace("data", new StateValueInt(11), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(0));
        }

        // No pillar rotations for specific purpur data values
        if (input.getIdentifier().equals("minecraft:purpur_block")) {
            clonedStates.replace("data", new StateValueInt(1), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(3), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(4), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(5), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(8), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(9), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(11), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(2));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(0));
        }

        // No pillar rotations for specific quartz data values
        if (input.getIdentifier().equals("minecraft:quartz_block")) {
            clonedStates.replace("data", new StateValueInt(4), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(5), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(3));
            clonedStates.replace("data", new StateValueInt(8), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(9), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(11), new StateValueInt(3));
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(2));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(3));
        }

        // Pumpkin (no rotation in Chunker)
        if (input.getIdentifier().equals("minecraft:pumpkin")) {
            clonedStates.replace("data", new StateValueInt(1), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(2), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(3), new StateValueInt(0));
        }

        // Composter (extra states)
        if (input.getIdentifier().equals("minecraft:composter")) {
            clonedStates.replace("data", new StateValueInt(9), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(10), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(11), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(8));
        }

        // Portal block (unknown state)
        if (input.getIdentifier().equals("minecraft:portal")) {
            clonedStates.replace("data", new StateValueInt(0), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(3), new StateValueInt(1));
        }

        // Cauldron block (extra state)
        if (input.getIdentifier().equals("minecraft:cauldron")) {
            // Extra level state
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(6));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(14));

            // Goes to empty cauldron
            clonedStates.replace("data", new StateValueInt(8), new StateValueInt(0));
        }

        // Chorus flower block (extra states)
        if (input.getIdentifier().equals("minecraft:chorus_flower")) {
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(5));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(5));
        }

        // Anvil block (identical states)
        if (input.getIdentifier().equals("minecraft:anvil")) {
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(9));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(10));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(11));
        }

        // Cocoa block (unused states)
        if (input.getIdentifier().equals("minecraft:cocoa")) {
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(2));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(3));
        }

        // Powered Rails (unknown state)
        if (input.getIdentifier().equals("minecraft:detector_rail") || input.getIdentifier().equals("minecraft:activator_rail") || input.getIdentifier().equals("minecraft:golden_rail")) {
            clonedStates.replace("data", new StateValueInt(6), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(7), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(8));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(8));
        }

        // Rail (unknown states)
        if (input.getIdentifier().equals("minecraft:rail")) {
            clonedStates.replace("data", new StateValueInt(10), new StateValueInt(0));
            clonedStates.replace("data", new StateValueInt(11), new StateValueInt(1));
            clonedStates.replace("data", new StateValueInt(12), new StateValueInt(2));
            clonedStates.replace("data", new StateValueInt(13), new StateValueInt(3));
            clonedStates.replace("data", new StateValueInt(14), new StateValueInt(4));
            clonedStates.replace("data", new StateValueInt(15), new StateValueInt(5));
        }

        return new Identifier(input.getIdentifier(), clonedStates);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @TestFactory
    Stream<DynamicNode> perVersionTests() {
        List<File> directories = new ArrayList<>(List.of(Objects.requireNonNull(Path.of("data", "bedrock").toFile().listFiles())));
        directories.sort(Comparator.comparing((File a) -> Version.fromString(a.getName())));

        return directories.stream().<DynamicNode>map(dataDirectory -> {
            Version version = Version.fromString(dataDirectory.getName());
            if (version.isGreaterThanOrEqual(1, 13, 0)) return null; // Handled by the non-legacy resolver

            File blockStates = new File(dataDirectory, "block_states.json");
            // Create the test class
            VersionTest versionTest = new VersionTest(version, blockStates);
            Map<String, Map<Integer, Map<String, StateValue<?>>>> blocks = versionTest.blocksData();
            return dynamicContainer("Bedrock " + dataDirectory.getName(), Stream.of(
                    dynamicContainer("Bedrock identifiers map to a Chunker output", blocks
                            .entrySet()
                            .stream()
                            .map(input -> dynamicTest(input.getKey(),
                                    () -> assertAll(input.getKey(), input.getValue().keySet().stream().map((values) -> {
                                        Identifier identifier = Identifier.fromData(input.getKey(), OptionalInt.of(values));
                                        return () -> versionTest.checkInputIdentifierMapped(identifier);
                                    }))
                            ))
                    ),
                    dynamicContainer("Bedrock to Chunker produces valid output (all states present)", blocks
                            .entrySet()
                            .stream()
                            .map(input -> dynamicTest(input.getKey(),
                                    () -> assertAll(input.getKey(), input.getValue().keySet().stream().map((values) -> {
                                        Identifier identifier = Identifier.fromData(input.getKey(), OptionalInt.of(values));
                                        return () -> versionTest.checkIdentifierOutputStates(identifier);
                                    }))
                            ))
                    ),
                    dynamicContainer("Bedrock identifiers are lossless (Bedrock -> Chunker -> Bedrock)", blocks
                            .entrySet()
                            .stream()
                            .map(input -> dynamicTest(input.getKey(),
                                    () -> assertAll(input.getKey(), input.getValue().keySet().stream().map((values) -> {
                                        Identifier identifier = Identifier.fromData(input.getKey(), OptionalInt.of(values));
                                        return () -> versionTest.checkInputIdentifierSymmetry(blocks, identifier);
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
                    dynamicContainer("Upgraded Bedrock identifiers produces valid output (upgraded states match)", blocks
                            .entrySet()
                            .stream()
                            .map(input -> dynamicTest(input.getKey(),
                                    () -> assertAll(input.getKey(), input.getValue().keySet().stream().map((values) -> {
                                        Identifier identifier = Identifier.fromData(input.getKey(), OptionalInt.of(values));
                                        return () -> versionTest.checkIdentifierUpgradedInputStates(blocks, identifier);
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
                                                        return () -> versionTest.checkIdentifierInputData(blocks, identifier);
                                                    }))
                            ))
                    )
            ));
        }).filter(Objects::nonNull);
    }

    public static class VersionTest {
        private final JsonArray blockStates;
        private final BedrockLegacyBlockIdentifierResolver resolver;
        private final BedrockBlockIdentifierResolver stateResolver;

        public VersionTest(Version version, File blockStates) {
            resolver = new BedrockLegacyBlockIdentifierResolver(new MockConverter(null), version, true, false);
            stateResolver = new BedrockBlockIdentifierResolver(new MockConverter(null), version, true, false);
            try (FileInputStream fis = new FileInputStream(blockStates)) {
                JsonObject root = gson.fromJson(new InputStreamReader(fis), JsonObject.class);
                this.blockStates = root.getAsJsonArray("blocks");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Map<String, Map<Integer, Map<String, StateValue<?>>>> blocksData() {
            Map<String, Map<Integer, Map<String, StateValue<?>>>> blocks = new TreeMap<>();
            for (JsonElement element : blockStates) {
                JsonObject blockState = element.getAsJsonObject();

                // Create a map of the states
                Map<String, StateValue<?>> stateMap = new Object2ObjectOpenHashMap<>();
                int data = blockState.has("val") ? blockState.get("val").getAsInt() : 0;

                // Add states (used for later validation)
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
                Map<Integer, Map<String, StateValue<?>>> stateMaps = blocks.computeIfAbsent(blockState.get("name").getAsString(), (ignored) -> new HashMap<>());
                if (stateMaps.put(data, stateMap) != null) {
                    throw new IllegalArgumentException("Duplicate data " + data + " for " + blockState);
                }
            }
            return blocks;
        }

        public void checkInputIdentifierMapped(Identifier input) {
            // If the block is present it shouldn't be an unsupported block
            assertEquals(UNSUPPORTED_BLOCKS.contains(input.getIdentifier()), resolver.to(input).isEmpty(), () -> input + " is not mapped.");
        }

        public void checkInputIdentifierSymmetry(Map<String, Map<Integer, Map<String, StateValue<?>>>> blocks, Identifier input) {
            if (!UNSUPPORTED_BLOCKS.contains(input.getIdentifier())) {
                // Ensure block isn't known to be lossy
                if (isLossyBlock(input)) {
                    return;
                }

                Optional<ChunkerBlockIdentifier> chunker = resolver.to(input);
                if (chunker.isPresent()) {
                    Optional<Identifier> converted = resolver.from(chunker.get());
                    assertTrue(converted.isPresent(), () -> "Lossy conversion for input " + input + ", could not map back using " + chunker.get());

                    // If it's converted
                    if (converted.isPresent()) {
                        // If the data value differs, we check if this could be a state alias (states which are identical)
                        if (converted.get().getIdentifier().equals(input.getIdentifier()) && converted.get().getDataValue().orElse(0) != input.getDataValue().orElse(0)) {
                            Map<String, StateValue<?>> convertedStates = blocks.get(converted.get().getIdentifier()).get(converted.get().getDataValue().orElse(0));
                            Map<String, StateValue<?>> inputStates = blocks.get(converted.get().getIdentifier()).get(input.getDataValue().orElse(0));
                            if (convertedStates.equals(inputStates)) {
                                // This is a duplicate, and we're returning an equivalent, it's fine to skip the check
                                return;
                            }
                        }

                        converted.get().getStates().remove("waterlogged"); // Don't compare waterlogging
                        assertEquals(removeLossyStates(input), removeLossyStates(converted.get()), () -> "Lossy conversion for input " + input + ", got " + converted.get() + " Chunker: " + chunker.get());
                    }
                }
            }
        }

        public void checkOutputIdentifierMapped(ChunkerBlockIdentifier input) {
            // If the block is present it shouldn't be an unsupported block
            assertTrue(resolver.from(input).isPresent(), () -> input + " is not mapped.");
        }

        public void checkIdentifierInputData(Map<String, Map<Integer, Map<String, StateValue<?>>>> blocks, ChunkerBlockIdentifier chunkerBlockIdentifier) {
            // If the block is present it shouldn't be an unsupported block
            Optional<Identifier> output = resolver.from(chunkerBlockIdentifier);
            if (output.isPresent()) {
                Identifier outputIdentifier = output.get();

                Map<Integer, Map<String, StateValue<?>>> allowedOutput = blocks.get(outputIdentifier.getIdentifier());
                assertNotNull(allowedOutput, () -> "Invalid identifier " + outputIdentifier.getIdentifier() + " for input " + chunkerBlockIdentifier);
                assertTrue(allowedOutput.containsKey(outputIdentifier.getDataValue().orElse(0)), () -> "Invalid data " + outputIdentifier + " for input " + chunkerBlockIdentifier + ", valid states: " + allowedOutput);
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

        public void checkIdentifierUpgradedInputStates(Map<String, Map<Integer, Map<String, StateValue<?>>>> blocks, Identifier input) {
            if (UNSUPPORTED_BLOCKS.contains(input.getIdentifier())) return;

            // Ensure block isn't known to be lossy
            if (isLossyBlock(input)) {
                return;
            }

            // If the block is present it shouldn't be an unsupported block
            Optional<ChunkerBlockIdentifier> chunker = resolver.to(removeLossyStates(input));
            if (chunker.isPresent()) {
                Optional<Identifier> stateIdentifier = stateResolver.from(chunker.get());
                assertTrue(stateIdentifier.isPresent(), () -> "Missing chunker identifier for upgrade check " + chunker.get());
                if (stateIdentifier.isPresent()) {
                    Identifier outputIdentifier = stateIdentifier.get();

                    // Don't compare waterlogging
                    outputIdentifier.getStates().remove("waterlogged");

                    // Check identifier with lossless version
                    Identifier lossless = removeLossyStates(input);
                    Map<Integer, Map<String, StateValue<?>>> allowedOutput = blocks.get(lossless.getIdentifier());
                    assertNotNull(allowedOutput, () -> "Invalid identifier " + outputIdentifier.getIdentifier() + " for input " + input + " fixed: " + lossless);
                    assertEquals(allowedOutput.get(lossless.getDataValue().orElse(0)), outputIdentifier.getStates(), () -> "Invalid upgraded states " + outputIdentifier + " for input " + input + ", valid states: " + allowedOutput);
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
