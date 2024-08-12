package com.hivemc.chunker.conversion.java.resolver.legacy;

import com.google.common.io.Resources;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hivemc.chunker.conversion.bedrock.resolver.MockConverter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaItemIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy.JavaLegacyBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy.JavaLegacyItemIdentifierResolver;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.item.ChunkerVanillaItemType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
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
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure that all Java legacy item identifiers are mapped to valid Chunker / Minecraft values.
 */
public class JavaLegacyItemIdentifierValidationTests {
    private static final JsonObject ITEMS;
    private static final JavaLegacyItemIdentifierResolver LEGACY_RESOLVER = new JavaLegacyItemIdentifierResolver(
            new MockConverter(null),
            new Version(1, 12, 0),
            true
    );
    private static final JavaItemIdentifierResolver RESOLVER = new JavaItemIdentifierResolver(
            new MockConverter(null),
            new Version(1, 13, 1), // Uses 1.13.1 for unstable on TNT
            true
    );
    private static final JavaLegacyBlockIdentifierResolver LEGACY_BLOCK_RESOLVER = new JavaLegacyBlockIdentifierResolver(
            new MockConverter(null),
            new Version(1, 12, 0),
            true,
            false
    );
    private static final JavaBlockIdentifierResolver BLOCK_RESOLVER = new JavaBlockIdentifierResolver(
            new MockConverter(null),
            new Version(1, 13, 1), // Uses 1.13.1 for unstable on TNT
            true,
            false
    );

    static {
        try (InputStream stream = Resources.getResource("java//resolver//pre_1_13_items.json").openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
            ITEMS = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<Arguments> items() throws IOException {
        // Convert to inputIdentifier, inputDataValue -> expected
        return ITEMS.entrySet().stream().flatMap(entry -> {
            if (entry.getValue().getAsJsonObject().has("tool") && entry.getValue().getAsJsonObject().get("tool").getAsBoolean()) {
                return Stream.of(Arguments.of(
                        entry.getKey(),
                        1000,
                        new Identifier(entry.getKey())
                ));
            } else {
                return entry.getValue().getAsJsonObject().get("data").getAsJsonObject().entrySet().stream().map(dataEntry -> Arguments.of(
                        entry.getKey(),
                        Integer.valueOf(dataEntry.getKey()),
                        parseIdentifier(dataEntry.getValue().getAsJsonObject())
                ));
            }
        });
    }

    public static Stream<Arguments> chunkerCombinations() throws IOException {
        return Stream.of(ChunkerVanillaItemType.values())
                .map(input -> Arguments.of(new ChunkerItemStack(input)));
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
    @MethodSource("items")
    public void checkInputIdentifier(String inputIdentifier, int data, Identifier expected) {
        Optional<ChunkerItemStack> intermediate = LEGACY_RESOLVER.to(Identifier.fromData(inputIdentifier, OptionalInt.of(data)));
        if (intermediate.isEmpty()) {
            intermediate = LEGACY_BLOCK_RESOLVER.to(Identifier.fromData(inputIdentifier, OptionalInt.of(data))).map(ChunkerItemStack::new);
        }

        // Check it's present
        assertTrue(intermediate.isPresent(), "Missing mapping for " + inputIdentifier + ":" + data);

        // Convert to 1.13
        Optional<Identifier> output = RESOLVER.from(intermediate.get());
        if (output.isEmpty() && intermediate.get().getIdentifier() instanceof ChunkerBlockIdentifier chunkerBlockIdentifier) {
            output = BLOCK_RESOLVER.from(chunkerBlockIdentifier);

            // States aren't written for items
            output.ifPresent(identifier -> identifier.getStates().entrySet().removeIf(a -> !a.getKey().equals("data")));
        }

        // Check it's present
        assertTrue(output.isPresent(), "Missing backwards conversion for intermediate " + intermediate.get());

        // Now check it against the expected
        assertEquals(expected, output.get(), "Got: " + output.get() + ", Expected: " + expected);
    }

    @ParameterizedTest
    @MethodSource("chunkerCombinations")
    public void checkIdentifierInputStates(ChunkerItemStack chunkerItemStack) {
        // If the block is present it shouldn't be an
        // unsupported block
        Optional<Identifier> output = LEGACY_RESOLVER.from(chunkerItemStack);
        if (output.isPresent()) {
            Identifier outputIdentifier = output.get();

            JsonElement item = ITEMS.get(outputIdentifier.getIdentifier());
            assertNotNull(item, "Missing item " + outputIdentifier.getIdentifier());

            // Ensure data is present
            assertTrue(outputIdentifier.getDataValue().isPresent(), "Missing data value for " + outputIdentifier.getIdentifier());

            // Validate data
            int dataValue = outputIdentifier.getDataValue().getAsInt();
            if (item.getAsJsonObject().has("tool") && item.getAsJsonObject().get("tool").getAsBoolean()) {
                assertTrue(true);
            } else {
                JsonObject data = item.getAsJsonObject().get("data").getAsJsonObject();
                assertTrue(data.has(String.valueOf(dataValue)), () -> "Invalid data value " + dataValue + " for " + outputIdentifier.getIdentifier());
            }
        }
    }
}
