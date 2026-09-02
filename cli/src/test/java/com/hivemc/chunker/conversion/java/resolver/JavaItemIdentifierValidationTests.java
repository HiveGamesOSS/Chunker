package com.hivemc.chunker.conversion.java.resolver;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hivemc.chunker.conversion.bedrock.resolver.MockConverter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.JavaDataVersion;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaItemIdentifierResolver;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests to ensure that all Java item identifiers are mapped to valid Chunker / Minecraft values.
 */
public class JavaItemIdentifierValidationTests {
    // Items from future versions
    public static final Set<ChunkerVanillaItemType> FUTURE_ITEMS = ImmutableSet.of();
    public static final Set<String> UNSUPPORTED_ITEMS = ImmutableSet.of();
    private static final Gson gson = new Gson();

    @TestFactory
    Stream<DynamicNode> perVersionTests() {
        List<File> directories = new ArrayList<>(List.of(Objects.requireNonNull(Path.of("data", "java").toFile().listFiles())));
        directories.sort(Comparator.comparing((File a) -> Version.fromString(a.getName())));

        return directories.stream().<DynamicNode>map(dataDirectory -> {
            String versionName = dataDirectory.getName();
            Version version = Version.fromString(versionName);

            File blockStates = new File(dataDirectory, "blocks.json");
            File itemList = new File(dataDirectory, "items.json");

            // Create the test class
            VersionTest versionTest = new VersionTest(version, blockStates, itemList);
            Set<String> items = versionTest.items();
            return dynamicContainer("Java " + versionName, Stream.of(
                    dynamicTest("Java " + versionName + " identifiers map to a Chunker output",
                            () -> assertAll(items.stream().map(input -> () -> versionTest.checkInputIdentifierMapped(new Identifier(input))))
                    ),
                    dynamicTest("Java " + versionName + " identifiers are lossless (Java -> Chunker -> Java)",
                            () -> assertAll(items.stream().map(input -> () -> versionTest.checkInputIdentifierSymmetry(new Identifier(input))))
                    ),
                    dynamicTest("Chunker identifiers maps to a Java " + versionName + " input",
                            () -> assertAll(Stream.of(ChunkerVanillaItemType.values())
                                    .filter(versionTest::isSupported)
                                    .map(input -> () -> versionTest.checkOutputIdentifierMapped(new ChunkerItemStack(input))))
                    ),
                    dynamicTest("Chunker identifiers maps to a valid Java " + versionName + " input",
                            () -> assertAll(Stream.of(ChunkerVanillaItemType.values())
                                    .filter(versionTest::isSupported)
                                    .map(input -> () -> versionTest.checkOutputIdentifierValid(new ChunkerItemStack(input))))
                    )
            ));
        }).filter(Objects::nonNull);
    }

    public static class VersionTest {
        private final Version version;
        private final JsonObject blocks;
        private final JsonObject items;
        private final JavaItemIdentifierResolver resolver;

        public VersionTest(Version version, File blockStates, File itemList) {
            this.version = version;
            resolver = new JavaItemIdentifierResolver(new MockConverter(null), version, true);
            try (FileInputStream blocksFileInputStream = new FileInputStream(blockStates);
                 FileInputStream itemsFileInputStream = new FileInputStream(itemList)) {
                blocks = gson.fromJson(new InputStreamReader(blocksFileInputStream), JsonObject.class);
                items = gson.fromJson(new InputStreamReader(itemsFileInputStream), JsonObject.class);

                // Remove any blocks from the items
                items.entrySet().removeIf(item -> blocks.has(item.getKey()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Set<String> items() {
            Set<String> items = new TreeSet<>();
            for (Map.Entry<String, JsonElement> entry : this.items.entrySet()) {
                items.add(entry.getKey());
            }
            return items;
        }

        public void checkInputIdentifierMapped(Identifier input) {
            // If the item is present it shouldn't be an unsupported item
            assertEquals(UNSUPPORTED_ITEMS.contains(input.getIdentifier()), resolver.to(input).isEmpty(), () -> input + " is not mapped.");
        }

        public void checkInputIdentifierSymmetry(Identifier input) {
            if (!UNSUPPORTED_ITEMS.contains(input.getIdentifier())) {
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
                assertTrue(
                        items.has(output.get().getIdentifier()) || blocks.has(output.get().getIdentifier()),
                        () -> output.get() + " is not a valid identifier for " + input
                );
            }
        }

        public boolean isSupported(ChunkerVanillaItemType input) {
            // A ChunkerVanillaItemType is marked as supported if it's possible to produce it from a mapping
            // If an item is in the original format and isn't in chunker then it will flag the checkInputIdentifierMapped unit test
            // The exclusion is if it's the latest version, this will catch unimplemented items
            return resolver.isSupported(input) || version.equals(JavaDataVersion.latest().getVersion()) && !FUTURE_ITEMS.contains(input);
        }
    }
}
