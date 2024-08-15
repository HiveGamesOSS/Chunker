package com.hivemc.chunker.conversion.java.resolver;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hivemc.chunker.conversion.bedrock.resolver.MockConverter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaBlockIdentifierResolver;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
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
 * Tests to ensure that all Java block identifiers are mapped to valid Chunker / Minecraft values.
 */
public class JavaBlockIdentifierValidationTests {
    public static final Set<String> UNSUPPORTED_BLOCKS = ImmutableSet.of(
            // Debug/Technical blocks
    );
    private static final Gson gson = new Gson();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @TestFactory
    Stream<DynamicNode> perVersionTests() {
        List<File> directories = new ArrayList<>(List.of(Objects.requireNonNull(Path.of("data", "java").toFile().listFiles())));
        directories.sort(Comparator.comparing((File a) -> Version.fromString(a.getName())));

        return directories.stream().<DynamicNode>map(dataDirectory -> {
            Version version = Version.fromString(dataDirectory.getName());

            File blockStates = new File(dataDirectory, "blocks.json");
            // Create the test class
            VersionTest versionTest = new VersionTest(version, blockStates);
            Map<String, Set<Map<String, StateValue<?>>>> blocks = versionTest.blocks();
            return dynamicContainer("Java " + dataDirectory.getName(), Stream.of(
                    dynamicContainer("Java identifiers map to a Chunker output", blocks
                            .entrySet()
                            .stream()
                            .map(input -> dynamicTest(input.getKey(),
                                    () -> assertAll(input.getKey(), input.getValue().stream().map((values) -> {
                                        Identifier identifier = new Identifier(input.getKey(), values);
                                        return () -> versionTest.checkInputIdentifierMapped(identifier);
                                    }))
                            ))
                    ),
                    dynamicContainer("Java to Chunker produces valid output (all states present)", blocks
                            .entrySet()
                            .stream()
                            .map(input -> dynamicTest(input.getKey(),
                                    () -> assertAll(input.getKey(), input.getValue().stream().map((values) -> {
                                        Identifier identifier = new Identifier(input.getKey(), values);
                                        return () -> versionTest.checkIdentifierOutputStates(identifier);
                                    }))
                            ))
                    ),
                    dynamicContainer("Java identifiers are lossless (Java -> Chunker -> Java)", blocks
                            .entrySet()
                            .stream()
                            .map(input -> dynamicTest(input.getKey(),
                                    () -> assertAll(input.getKey(), input.getValue().stream().map((values) -> {
                                        Identifier identifier = new Identifier(input.getKey(), values);
                                        return () -> versionTest.checkInputIdentifierSymmetry(identifier);
                                    }))
                            ))
                    ),
                    dynamicContainer("Chunker identifiers maps to a Java input", Stream.of(ChunkerVanillaBlockType.values())
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
                                                        return () -> versionTest.checkOutputIdentifierMapped(blocks, identifier);
                                                    }))
                            ))
                    ),
                    dynamicContainer("Chunker to Java produces valid output (all states present)", Stream.of(ChunkerVanillaBlockType.values())
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
        private final JsonObject blocks;
        private final JavaBlockIdentifierResolver resolver;

        public VersionTest(Version version, File blockStates) {
            resolver = new JavaBlockIdentifierResolver(new MockConverter(null), version, true, false);
            try (FileInputStream fis = new FileInputStream(blockStates)) {
                blocks = gson.fromJson(new InputStreamReader(fis), JsonObject.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Map<String, Set<Map<String, StateValue<?>>>> blocks() {
            Map<String, Set<Map<String, StateValue<?>>>> blocks = new TreeMap<>();
            for (Map.Entry<String, JsonElement> entry : this.blocks.entrySet()) {
                JsonObject block = entry.getValue().getAsJsonObject();
                for (JsonElement element : block.getAsJsonArray("states")) {
                    JsonObject blockState = element.getAsJsonObject();

                    // Create a map of the states
                    Map<String, StateValue<?>> stateMap = new Object2ObjectOpenHashMap<>();
                    if (blockState.has("properties")) {
                        for (Map.Entry<String, JsonElement> propertyEntry : blockState.get("properties").getAsJsonObject().entrySet()) {
                            stateMap.put(propertyEntry.getKey(), new StateValueString(propertyEntry.getValue().getAsString()));
                        }
                    }

                    // Get the list and add the entry
                    Set<Map<String, StateValue<?>>> stateMaps = blocks.computeIfAbsent(entry.getKey(), (ignored) -> new LinkedHashSet<>());

                    // Add to the list of state maps
                    stateMaps.add(stateMap);
                }
            }
            return blocks;
        }

        public void checkInputIdentifierMapped(Identifier input) {
            // If the block is present it shouldn't be an unsupported block
            assertEquals(UNSUPPORTED_BLOCKS.contains(input.getIdentifier()), resolver.to(input).isEmpty(), () -> input + " is not mapped.");
        }

        public void checkInputIdentifierSymmetry(Identifier input) {
            if (!UNSUPPORTED_BLOCKS.contains(input.getIdentifier())) {
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

        public Identifier removeLossyStates(Identifier input) {
            Map<String, StateValue<?>> clonedStates = new Object2ObjectOpenHashMap<>(input.getStates());
            if (input.getIdentifier().equals("minecraft:torchflower_crop") && Integer.parseInt(((StateValueString) input.getStates().get("age")).getValue()) >= 2) {
                // Looks the same after state 1
                clonedStates.replace("age", new StateValueString("1"));
            }
            return new Identifier(input.getIdentifier(), clonedStates);
        }

        public void checkOutputIdentifierMapped(Map<String, Set<Map<String, StateValue<?>>>> blocks, ChunkerBlockIdentifier input) {
            // If the block is present it shouldn't be an unsupported block
            assertTrue(resolver.from(input).isPresent(), () -> input + " is not mapped.");
        }

        public void checkIdentifierInputStates(Map<String, Set<Map<String, StateValue<?>>>> blocks, ChunkerBlockIdentifier chunkerBlockIdentifier) {
            // If the block is present it shouldn't be an unsupported block
            Optional<Identifier> output = resolver.from(chunkerBlockIdentifier);
            if (output.isPresent()) {
                Identifier outputIdentifier = output.get();

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
