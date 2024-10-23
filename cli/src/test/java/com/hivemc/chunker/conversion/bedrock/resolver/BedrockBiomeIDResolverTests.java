package com.hivemc.chunker.conversion.bedrock.resolver;

import com.google.common.io.Resources;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.biome.BedrockBiomeIDResolver;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure Bedrock Biome IDs resolve correctly.
 */
public class BedrockBiomeIDResolverTests {
    public static Stream<Arguments> biomeList() throws IOException {
        try (InputStream stream = Resources.getResource("bedrock/resolver/biome_ids.json").openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
            JsonObject biomeIds = JsonParser.parseReader(inputStreamReader).getAsJsonObject();

            // Convert to String, Int
            return biomeIds.entrySet().stream().map(entry -> Arguments.of(
                    entry.getKey(),
                    entry.getValue().getAsInt()
            ));
        }
    }

    @Test
    public void checkBiomeObjectUniqueness() throws IOException {
        List<Arguments> biomeIds = biomeList().toList();
        BedrockBiomeIDResolver biomeResolver = new BedrockBiomeIDResolver(Version.LATEST);
        Set<ChunkerBiome> loadedBiomes = new ObjectOpenHashSet<>();
        Set<Integer> loadedBiomeIDs = new ObjectOpenHashSet<>();

        for (Arguments value : biomeIds) {
            int id = (int) value.get()[1];

            // Ensure our biome_ids.json isn't using duplicates, that could trip us up
            assertTrue(loadedBiomeIDs.add(id));

            // Convert value
            Optional<ChunkerBiome> mappedValue = biomeResolver.to(id);

            // Ensure this value isn't a duplicate (everything needs to be unique)
            assertTrue(mappedValue.isPresent() && loadedBiomes.add(mappedValue.get()));
        }
    }

    @ParameterizedTest
    @MethodSource("biomeList")
    public void checkBiome(String biome, int biomeId) {
        // Use a high version to ensure every biome is used
        BedrockBiomeIDResolver biomeResolver = new BedrockBiomeIDResolver(Version.LATEST);

        // Convert value
        Optional<ChunkerBiome> mappedValue = biomeResolver.to(biomeId);

        // Ensure there is a value
        assertFalse(mappedValue.isEmpty());

        // Ensure there is a symmetrical mapping
        assertFalse(biomeResolver.from(mappedValue.get()).isEmpty());
        assertEquals(biomeId, biomeResolver.from(mappedValue.get()).get());
    }
}
