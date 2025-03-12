package com.hivemc.chunker.conversion.java.resolver;

import com.google.common.io.Resources;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.biome.JavaBiomeIDResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.biome.JavaNamedBiomeResolver;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure Java Biome IDs resolve correctly.
 */
public class JavaBiomeIDResolverTests {
    public static Stream<Arguments> biomeList() throws IOException {
        JsonObject biomeIds;
        try (InputStream stream = Resources.getResource("java/resolver/biome_ids.json").openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
            biomeIds = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
        }

        // Load the renames which are used for 1.17 -> 1.18 (needed for converting to identifiers)
        JsonObject biomeRenames;
        try (InputStream stream = Resources.getResource("java/resolver/biome_ids_renames.json").openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
            biomeRenames = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
        }

        // Convert to String, String, Int
        return biomeIds.entrySet().stream().map(entry -> Arguments.of(
                entry.getKey(),
                biomeRenames.has(entry.getKey()) ? biomeRenames.get(entry.getKey()).getAsString() : entry.getKey(),
                entry.getValue().getAsInt()
        ));
    }

    @Test
    public void checkBiomeObjectUniqueness() throws IOException {
        List<Arguments> biomeIds = biomeList().toList();
        JavaBiomeIDResolver biomeResolver = new JavaBiomeIDResolver(Version.LATEST);
        Set<ChunkerBiome> loadedBiomes = new ObjectOpenHashSet<>();
        Set<Integer> loadedBiomeIDs = new ObjectOpenHashSet<>();

        for (Arguments value : biomeIds) {
            int id = (int) value.get()[2];

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
    public void checkBiome(String biome, String biomeRenamed, int biomeId) {
        // Use a high version to ensure every biome is used
        JavaBiomeIDResolver biomeResolver = new JavaBiomeIDResolver(Version.LATEST);

        // Convert value
        Optional<ChunkerBiome> mappedValue = biomeResolver.to(biomeId);

        // Ensure there is a value
        assertFalse(mappedValue.isEmpty());

        // Ensure there is a symmetrical mapping
        assertFalse(biomeResolver.from(mappedValue.get()).isEmpty());
        assertEquals(biomeId, biomeResolver.from(mappedValue.get()).get());
    }

    @ParameterizedTest
    @MethodSource("biomeList")
    public void checkBiomeMatchesNamed(String biome, String biomeRenamed, int biomeId) {
        // Use a high version to ensure every biome is used
        JavaBiomeIDResolver biomeIDResolver = new JavaBiomeIDResolver(Version.LATEST);
        JavaNamedBiomeResolver biomeNameResolver = new JavaNamedBiomeResolver(Version.LATEST, false);

        // Convert value
        Optional<ChunkerBiome> mappedIDValue = biomeIDResolver.to(biomeId);
        Optional<ChunkerBiome> mappedNameValue = biomeNameResolver.to(biomeRenamed);

        // Ensure there are values
        assertFalse(mappedIDValue.isEmpty());
        assertFalse(mappedNameValue.isEmpty());

        // If the value isn't equal, check the fallback (as ids -> names had some values removed)
        if (mappedIDValue.get().getFallback() != null && !Objects.equals(mappedNameValue.get(), mappedIDValue.get())) {
            assertEquals(mappedNameValue.get(), mappedIDValue.get().getFallback());
        } else {
            assertEquals(mappedNameValue.get(), mappedIDValue.get());
        }
    }
}
