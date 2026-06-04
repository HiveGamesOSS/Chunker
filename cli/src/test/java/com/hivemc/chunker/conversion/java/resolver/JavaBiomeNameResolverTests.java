package com.hivemc.chunker.conversion.java.resolver;

import com.google.common.io.Resources;
import com.google.gson.JsonArray;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure Java Biome identifiers resolve correctly.
 */
public class JavaBiomeNameResolverTests {
    public static final Set<ChunkerBiome> BIOMES_ID_NOT_SUPPORTED = Set.of(
            ChunkerBiome.ChunkerVanillaBiome.DEEP_DARK,
            ChunkerBiome.ChunkerVanillaBiome.MANGROVE_SWAMP,
            ChunkerBiome.ChunkerVanillaBiome.CHERRY_GROVE,
            ChunkerBiome.ChunkerVanillaBiome.PALE_GARDEN,
            ChunkerBiome.ChunkerVanillaBiome.SULFUR_CAVES
    );

    public static Stream<Arguments> biomeList() throws IOException {
        JsonArray biomeNames;
        try (InputStream stream = Resources.getResource("java/resolver/biome_names.json").openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
            biomeNames = JsonParser.parseReader(inputStreamReader).getAsJsonArray();
        }

        // Convert to String, String, Int
        return biomeNames.asList().stream().map(entry -> Arguments.of(
                entry.getAsString()
        ));
    }

    @Test
    public void checkBiomeObjectUniqueness() throws IOException {
        List<Arguments> biomeIds = biomeList().toList();
        JavaNamedBiomeResolver biomeResolver = new JavaNamedBiomeResolver(Version.LATEST, false);
        Set<ChunkerBiome> loadedBiomes = new ObjectOpenHashSet<>();
        Set<String> loadedBiomeIDs = new ObjectOpenHashSet<>();

        for (Arguments value : biomeIds) {
            String biome = (String) value.get()[0];

            // Ensure our biome_ids.json isn't using duplicates, that could trip us up
            assertTrue(loadedBiomeIDs.add(biome));

            // Convert value
            Optional<ChunkerBiome> mappedValue = biomeResolver.to(biome);

            // Ensure this value isn't a duplicate (everything needs to be unique)
            assertTrue(mappedValue.isPresent() && loadedBiomes.add(mappedValue.get()));
        }
    }

    @ParameterizedTest
    @MethodSource("biomeList")
    public void checkBiome(String biome) {
        // Use a high version to ensure every biome is used
        JavaNamedBiomeResolver biomeResolver = new JavaNamedBiomeResolver(Version.LATEST, false);

        // Convert value
        Optional<ChunkerBiome> mappedValue = biomeResolver.to(biome);

        // Ensure there is a value
        assertFalse(mappedValue.isEmpty());

        // Ensure there is a symmetrical mapping
        assertFalse(biomeResolver.from(mappedValue.get()).isEmpty());
        assertEquals(biome, biomeResolver.from(mappedValue.get()).get());
    }

    @ParameterizedTest
    @MethodSource("biomeList")
    public void checkBiomeIDsExist(String biome) {
        // Use a high version to ensure every biome is used
        JavaBiomeIDResolver biomeIDResolver = new JavaBiomeIDResolver(Version.LATEST);
        JavaNamedBiomeResolver biomeNameResolver = new JavaNamedBiomeResolver(Version.LATEST, false);

        Optional<ChunkerBiome> mappedNameValue = biomeNameResolver.to(biome);

        // Ensure there is a value
        assertFalse(mappedNameValue.isEmpty());

        // Now convert to ID and ensure it's present
        boolean shouldBePresent = !BIOMES_ID_NOT_SUPPORTED.contains(mappedNameValue.get());
        assertEquals(shouldBePresent, biomeIDResolver.from(mappedNameValue.get()).isPresent());
    }

    @ParameterizedTest
    @MethodSource("biomeList")
    public void checkBiomeParsingExists(String biome) {
        Optional<ChunkerBiome.ChunkerVanillaBiome> mappedBiome = ChunkerBiome.ChunkerVanillaBiome.find(biome);
        JavaNamedBiomeResolver biomeNameResolver = new JavaNamedBiomeResolver(Version.LATEST, false);

        // Ensure there is a value
        assertFalse(mappedBiome.isEmpty());

        Optional<String> mappedNameValue = biomeNameResolver.from(mappedBiome.get());
        assertTrue(mappedNameValue.isPresent());
        assertEquals(biome, mappedNameValue.get());
    }

    @ParameterizedTest
    @MethodSource("biomeList")
    public void checkJavaIdentifierMatches(String biome) {
        JavaNamedBiomeResolver biomeResolver = new JavaNamedBiomeResolver(Version.LATEST, false);

        // Resolve the Java identifier back to a vanilla biome
        Optional<ChunkerBiome> mappedValue = biomeResolver.to(biome);
        assertTrue(mappedValue.isPresent());
        assertInstanceOf(ChunkerBiome.ChunkerVanillaBiome.class, mappedValue.get());

        // Ensure it is present and matches biome_names.json
        ChunkerBiome.ChunkerVanillaBiome vanillaBiome = (ChunkerBiome.ChunkerVanillaBiome) mappedValue.get();
        Optional<String> javaIdentifier = vanillaBiome.getJavaIdentifier();
        assertTrue(javaIdentifier.isPresent());
        assertEquals(biome, javaIdentifier.get());
    }

    @Test
    public void checkJavaIdentifiersUnique() {
        // Ensure all the Java identifiers in ChunkerVanillaBiome are unique
        Set<String> seen = new ObjectOpenHashSet<>();
        for (ChunkerBiome.ChunkerVanillaBiome biome : ChunkerBiome.ChunkerVanillaBiome.values()) {
            Optional<String> javaIdentifier = biome.getJavaIdentifier();
            if (javaIdentifier.isEmpty()) continue;

            // Try adding
            assertTrue(seen.add(javaIdentifier.get()));
        }
    }
}
