package com.hivemc.chunker.conversion.bedrock.resolver;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.BedrockDataVersion;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockItemIdentifierResolver;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerVanillaItemType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.mapping.identifier.Identifier;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests to ensure that all Bedrock item identifiers are mapped to valid Chunker / Minecraft values.
 */
public class BedrockItemIdentifierValidationTests {
    // Items from future versions
    public static final Set<ChunkerVanillaItemType> FUTURE_ITEMS = ImmutableSet.of(
            ChunkerVanillaItemType.COPPER_HORSE_ARMOR
    );
    // Lossy items are items which have are legacy, so it leads to it going to the newer variant
    public static final Set<String> LOSSY_ITEMS = ImmutableSet.of(
            "minecraft:dye",
            "minecraft:banner_pattern",
            "minecraft:boat",
            "minecraft:chest_boat",
            "minecraft:skull"
    );
    public static final Set<String> UNSUPPORTED_ITEMS = ImmutableSet.of(
            // Blocks which got flattened (they technically don't have a backing block so get marked as blocks in our auto-gen)
            "minecraft:wool",
            "minecraft:carpet",
            "minecraft:fence",
            "minecraft:planks",
            "minecraft:log",
            "minecraft:log2",
            "minecraft:leaves",
            "minecraft:leaves2",
            "minecraft:wood",
            "minecraft:wooden_slab",
            "minecraft:concrete",
            "minecraft:concrete_powder",
            "minecraft:coral",
            "minecraft:hard_stained_glass",
            "minecraft:hard_stained_glass_pane",
            "minecraft:shulker_box",
            "minecraft:stained_glass",
            "minecraft:stained_glass_pane",
            "minecraft:stained_hardened_clay",
            "minecraft:coral_fan",
            "minecraft:coral_fan_dead",
            "minecraft:sapling",
            "minecraft:red_flower",
            "minecraft:coral_block",
            "minecraft:double_plant",
            "minecraft:stone_block_slab",
            "minecraft:tallgrass",
            "minecraft:double_stone_block_slab",
            "minecraft:stone_block_slab2",
            "minecraft:stone_block_slab3",
            "minecraft:stone_block_slab4",
            "minecraft:double_stone_block_slab2",
            "minecraft:double_stone_block_slab3",
            "minecraft:double_stone_block_slab4",
            "minecraft:light_block",
            "minecraft:monster_egg",
            "minecraft:stonebrick",
            "minecraft:colored_torch_bp",
            "minecraft:colored_torch_rg",
            "minecraft:chemistry_table",

            // Not Used
            "minecraft:firefly_spawn_egg",

            // EDU
            "minecraft:board",
            "minecraft:agent_spawn_egg",
            "minecraft:npc_spawn_egg",
            "minecraft:glow_stick",
            "minecraft:balloon",
            "minecraft:bleach",
            "minecraft:camera",
            "minecraft:compound",
            "minecraft:ice_bomb",
            "minecraft:medicine",
            "minecraft:rapid_fertilizer",
            "minecraft:sparkler"
    );
    private static final Gson gson = new Gson();

    @TestFactory
    Stream<DynamicNode> perVersionTests() {
        List<File> directories = new ArrayList<>(List.of(Objects.requireNonNull(Path.of("data", "bedrock").toFile().listFiles())));
        directories.sort(Comparator.comparing((File a) -> Version.fromString(a.getName())));

        return directories.stream().<DynamicNode>map(dataDirectory -> {
            Version version = Version.fromString(dataDirectory.getName());

            File itemList = new File(dataDirectory, "item_names.json");

            // Create the test class
            VersionTest versionTest = new VersionTest(version, itemList);
            Set<String> items = versionTest.items();
            return dynamicContainer("Bedrock " + dataDirectory.getName(), Stream.of(
                    dynamicContainer("Bedrock identifiers map to a Chunker output", items
                            .stream()
                            .map(input -> dynamicTest(input, () -> versionTest.checkInputIdentifierMapped(new Identifier(input))))
                    ),
                    dynamicContainer("Bedrock identifiers are lossless (Bedrock -> Chunker -> Bedrock)", items
                            .stream()
                            .map(input -> dynamicTest(input, () -> versionTest.checkInputIdentifierSymmetry(new Identifier(input))))
                    ),
                    dynamicContainer("Chunker identifiers maps to a Bedrock input", Stream.of(ChunkerVanillaItemType.values())
                            .filter(versionTest::isSupported)
                            .map(input -> dynamicTest(input.name(), () -> versionTest.checkOutputIdentifierMapped(new ChunkerItemStack(input))))
                    ),
                    dynamicContainer("Chunker identifiers maps to a valid Bedrock input", Stream.of(ChunkerVanillaItemType.values())
                            .filter(versionTest::isSupported)
                            .map(input -> dynamicTest(input.name(), () -> versionTest.checkOutputIdentifierValid(new ChunkerItemStack(input))))
                    )
            ));
        }).filter(Objects::nonNull);
    }

    public static class VersionTest {
        private final Version version;
        private final JsonObject items;
        private final BedrockItemIdentifierResolver resolver;

        public VersionTest(Version version, File itemList) {
            this.version = version;
            resolver = new BedrockItemIdentifierResolver(new MockConverter(null), version, true);
            try (FileInputStream itemsFileInputStream = new FileInputStream(itemList)) {
                items = gson.fromJson(new InputStreamReader(itemsFileInputStream), JsonObject.class);

                // Remove any blocks from the items
                items.entrySet().removeIf(item -> {
                    if (item.getValue().isJsonObject()) {
                        return !item.getValue().getAsJsonObject().get("item").getAsBoolean();
                    } else {
                        return item.getValue().getAsInt() < 256;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Set<String> items() {
            Set<String> items = new TreeSet<>();
            for (Map.Entry<String, JsonElement> entry : this.items.entrySet()) {
                items.add(entry.getKey().replace("item.", "")); // Remove item. prefix
            }
            return items;
        }

        public void checkInputIdentifierMapped(Identifier input) {
            // If the item is present it shouldn't be an unsupported item
            assertEquals(UNSUPPORTED_ITEMS.contains(input.getIdentifier()), resolver.to(input).isEmpty(), () -> input + " is not mapped.");
        }

        public void checkInputIdentifierSymmetry(Identifier input) {
            if (!UNSUPPORTED_ITEMS.contains(input.getIdentifier()) && !LOSSY_ITEMS.contains(input.getIdentifier())) {
                Optional<ChunkerItemStack> chunker = resolver.to(input);
                if (chunker.isPresent()) {
                    Optional<Identifier> converted = resolver.from(chunker.get());
                    assertTrue(converted.isPresent(), () -> "Lossy conversion for input " + input + ", could not map back using " + chunker.get());
                    if (converted.isPresent()) {
                        assertEquals(input, converted.get(), () -> "Lossy conversion for input " + input + ", got " + converted.get() + " Chunker: " + chunker.get());
                    }
                }
            }
        }

        public void checkOutputIdentifierMapped(ChunkerItemStack input) {
            // If the item is present it shouldn't be an unsupported item
            assertTrue(resolver.from(input).isPresent(), () -> input + " is not mapped.");
        }

        public void checkOutputIdentifierValid(ChunkerItemStack input) {
            // If the item is present it shouldn't be an unsupported item
            Optional<Identifier> output = resolver.from(input);
            if (output.isPresent()) {
                assertTrue(items.has(output.get().getIdentifier()), () -> output.get() + " is not a valid identifier for " + input);
            }
        }

        public boolean isSupported(ChunkerVanillaItemType input) {
            // A ChunkerVanillaItemType is marked as supported if it's possible to produce it from a mapping
            // If an item is in the original format and isn't in chunker then it will flag the checkInputIdentifierMapped unit test
            // The exclusion is if it's the latest version, this will catch unimplemented items
            return resolver.isSupported(input) || version.equals(BedrockDataVersion.latest().getVersion()) && !FUTURE_ITEMS.contains(input);
        }
    }
}
