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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
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
        try (InputStream stream = Resources.getResource("java/resolver/pre_1_13_items.json").openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
            ITEMS = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A legacy identifier / data value pair with the flattened identifier it should produce.
     *
     * @param identifier the legacy item identifier.
     * @param data       the legacy data value.
     * @param expected   the flattened identifier the pair should produce.
     */
    private record LegacyItem(String identifier, int data, Identifier expected) {
    }

    private static Stream<LegacyItem> items() {
        // Convert to inputIdentifier, inputDataValue -> expected
        List<LegacyItem> items = ITEMS.entrySet().stream().flatMap(entry -> {
            if (entry.getValue().getAsJsonObject().has("tool") && entry.getValue().getAsJsonObject().get("tool").getAsBoolean()) {
                return Stream.of(new LegacyItem(
                        entry.getKey(),
                        1000,
                        new Identifier(entry.getKey())
                ));
            } else {
                return entry.getValue().getAsJsonObject().get("data").getAsJsonObject().entrySet().stream().map(dataEntry -> new LegacyItem(
                        entry.getKey(),
                        Integer.parseInt(dataEntry.getKey()),
                        parseIdentifier(dataEntry.getValue().getAsJsonObject())
                ));
            }
        }).toList();

        // Ensure items were present for the test
        assertFalse(items.isEmpty(), "No items were loaded from pre_1_13_items.json");
        return items.stream();
    }

    private static Stream<ChunkerItemStack> chunkerCombinations() {
        return Stream.of(ChunkerVanillaItemType.values())
                .map(ChunkerItemStack::new);
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

    @Test
    public void checkInputIdentifier() {
        assertAll(items().map(item -> () -> assertInputIdentifier(item)));
    }

    @Test
    public void checkIdentifierInputStates() {
        assertAll(chunkerCombinations().map(itemStack -> () -> assertIdentifierInputStates(itemStack)));
    }

    private void assertInputIdentifier(LegacyItem item) {
        Optional<ChunkerItemStack> intermediate = LEGACY_RESOLVER.to(Identifier.fromData(item.identifier(), OptionalInt.of(item.data())));
        if (intermediate.isEmpty()) {
            intermediate = LEGACY_BLOCK_RESOLVER.to(Identifier.fromData(item.identifier(), OptionalInt.of(item.data()))).map(ChunkerItemStack::new);
        }

        // Check it's present
        assertTrue(intermediate.isPresent(), "Missing mapping for " + item.identifier() + ":" + item.data());

        // Convert to 1.13
        Optional<Identifier> output = RESOLVER.from(intermediate.get());
        if (output.isEmpty() && intermediate.get().getIdentifier() instanceof ChunkerBlockIdentifier chunkerBlockIdentifier) {
            output = BLOCK_RESOLVER.from(chunkerBlockIdentifier);

            // States aren't written for items
            output.ifPresent(identifier -> identifier.getStates().entrySet().removeIf(a -> !a.getKey().equals("data")));
        }

        // Check it's present
        assertTrue(output.isPresent(), "Missing backwards conversion for " + item.identifier() + ":" + item.data() + " using " + intermediate.get());

        // Now check it against the expected
        assertEquals(item.expected(), output.get(), "Got: " + output.get() + ", Expected: " + item.expected());
    }

    private void assertIdentifierInputStates(ChunkerItemStack chunkerItemStack) {
        // If the block is present it shouldn't be an
        // unsupported block
        Optional<Identifier> output = LEGACY_RESOLVER.from(chunkerItemStack);
        if (output.isPresent()) {
            Identifier outputIdentifier = output.get();

            JsonElement item = ITEMS.get(outputIdentifier.getIdentifier());
            assertNotNull(item, () -> "Missing item " + outputIdentifier.getIdentifier() + " for input " + chunkerItemStack);

            // Ensure data is present
            assertTrue(outputIdentifier.getDataValue().isPresent(), () -> "Missing data value for " + outputIdentifier.getIdentifier() + " for input " + chunkerItemStack);

            // Validate data
            int dataValue = outputIdentifier.getDataValue().getAsInt();
            if (!item.getAsJsonObject().has("tool") || !item.getAsJsonObject().get("tool").getAsBoolean()) {
                JsonObject data = item.getAsJsonObject().get("data").getAsJsonObject();
                assertTrue(data.has(String.valueOf(dataValue)), () -> "Invalid data value " + dataValue + " for " + outputIdentifier.getIdentifier());
            }
        }
    }
}
