package com.hivemc.chunker.conversion.java.resolver.legacy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hivemc.chunker.conversion.bedrock.resolver.MockConverter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy.JavaLegacyBlockIdentifierResolver;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.ChunkerVanillaBlockType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockState;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.BlockStateValue;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.identifier.states.StateValueString;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure that all Java Legacy block identifiers are mapped to valid Chunker / Minecraft values.
 */
public class JavaLegacyBlockIdentifierValidationTests {
    private static final JsonObject BLOCKS;
    private static final JavaLegacyBlockIdentifierResolver LEGACY_RESOLVER = new JavaLegacyBlockIdentifierResolver(
            new MockConverter(null),
            new Version(1, 12, 0),
            true,
            false
    );
    private static final JavaBlockIdentifierResolver RESOLVER = new JavaBlockIdentifierResolver(
            new MockConverter(null),
            new Version(1, 13, 1), // Uses 1.13.1 for unstable on TNT
            true,
            false
    );

    static {
        try (InputStream stream = Resources.getResource("java/resolver/pre_1_13_blocks.json").openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
            BLOCKS = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<Arguments> blocks() throws IOException {
        // Convert to inputIdentifier, inputDataValue -> expected
        return BLOCKS.entrySet().stream().flatMap(entry -> entry.getValue().getAsJsonObject().get("data").getAsJsonObject().entrySet().stream().map(dataEntry -> Arguments.of(
                entry.getKey(),
                Integer.valueOf(dataEntry.getKey()),
                parseIdentifier(dataEntry.getValue().getAsJsonObject())
        )));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Stream<Arguments> chunkerCombinations() throws IOException {
        return Stream.of(ChunkerVanillaBlockType.values())
                .flatMap(input -> Lists.cartesianProduct(input.getStates().stream().map(a -> List.of(a.getValues())).collect(Collectors.toList()))
                        .stream()
                        .map(a -> Streams.zip(input.getStates().stream(), a.stream(), Maps::immutableEntry).collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue
                        )))
                        .map((states) -> Arguments.of(new ChunkerBlockIdentifier(input, (Map<BlockState<?>, BlockStateValue>) (Map) states))));
    }

    private static Identifier parseIdentifier(JsonObject block) {
        Map<String, StateValue<?>> states = new Object2ObjectOpenHashMap<>();
        if (block.has("states")) {
            for (Map.Entry<String, JsonElement> state : block.get("states").getAsJsonObject().entrySet()) {
                states.put(state.getKey(), new StateValueString(state.getValue().getAsString()));
            }
        }
        return new Identifier(
                block.get("identifier").getAsString(),
                states
        );
    }

    @ParameterizedTest
    @MethodSource("blocks")
    public void checkInputIdentifier(String inputIdentifier, int data, Identifier expected) {
        Optional<ChunkerBlockIdentifier> intermediate = LEGACY_RESOLVER.to(Identifier.fromData(inputIdentifier, OptionalInt.of(data)));

        // Check it's present
        assertTrue(intermediate.isPresent());

        // Convert to 1.13
        Optional<Identifier> output = RESOLVER.from(intermediate.get());

        // Check it's present
        assertTrue(output.isPresent());

        // Remove waterlogged
        output.get().getStates().remove("waterlogged");

        // Rename decayable and invert
        if (expected.getStates().containsKey("decayable")) {
            expected.getStates().put("persistent", expected.getStates().remove("decayable").getBoxed().equals("true") ? new StateValueString("false") : new StateValueString("true"));

            // Remove check_decay as it's not produced by 1.13
            expected.getStates().remove("check_decay");
        }

        // Remove nodrop
        // nodrop isn't provided by 1.13
        expected.getStates().remove("nodrop");

        // Now check it against the expected
        assertEquals(expected, output.get());
    }

    @ParameterizedTest
    @MethodSource("blocks")
    public void checkIdentifierOutputStates(String inputIdentifier, int data) {
        Optional<ChunkerBlockIdentifier> intermediate = LEGACY_RESOLVER.to(Identifier.fromData(inputIdentifier, OptionalInt.of(data)));

        // Check it's present
        assertTrue(intermediate.isPresent());

        ChunkerBlockIdentifier outputIdentifier = intermediate.get();

        // Ensure it's a vanilla block
        assertInstanceOf(ChunkerVanillaBlockType.class, outputIdentifier.getType());

        // Ensure all states are present
        ChunkerVanillaBlockType vanillaBlockType = (ChunkerVanillaBlockType) outputIdentifier.getType();
        for (BlockState<?> state : vanillaBlockType.getStates()) {
            assertTrue(outputIdentifier.containsState(state), () -> "Missing output state " + state + " for input " + inputIdentifier + ":" + data);
        }
        for (BlockState<?> state : outputIdentifier.getPresentStates().keySet()) {
            assertTrue(vanillaBlockType.getStates().contains(state), () -> "Invalid output state " + state + " for input " + inputIdentifier + ":" + data);
        }
    }

    @ParameterizedTest
    @MethodSource("chunkerCombinations")
    public void checkIdentifierInputStates(ChunkerBlockIdentifier chunkerBlockIdentifier) {
        // If the block is present it shouldn't be an unsupported block
        Optional<Identifier> output = LEGACY_RESOLVER.from(chunkerBlockIdentifier);
        if (output.isPresent()) {
            Identifier outputIdentifier = output.get();

            JsonElement block = BLOCKS.get(outputIdentifier.getIdentifier());
            assertNotNull(block, "Missing block " + outputIdentifier.getIdentifier());

            // Ensure data is present
            assertTrue(outputIdentifier.getDataValue().isPresent(), "Missing data value for " + outputIdentifier.getIdentifier());

            // Validate data
            int dataValue = outputIdentifier.getDataValue().getAsInt();
            JsonObject data = block.getAsJsonObject().get("data").getAsJsonObject();
            assertTrue(data.has(String.valueOf(dataValue)), () -> "Invalid data value " + dataValue + " for " + outputIdentifier.getIdentifier());
        }
    }
}
