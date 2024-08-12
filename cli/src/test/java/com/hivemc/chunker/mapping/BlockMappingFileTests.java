package com.hivemc.chunker.mapping;

import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.mappings.IdentifierMappings;
import com.hivemc.chunker.mapping.resolver.MappingsFileIdentifierResolver;
import com.hivemc.chunker.mapping.resolver.MappingsFileResolvers;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests to ensure the functionality is correct for block mappings.
 */
public class BlockMappingFileTests extends MappingFileTests {
    @Override
    public Optional<Identifier> convert(MappingsFile mappingsFile, Identifier input) {
        return mappingsFile.convertBlock(input);
    }

    @Override
    public MappingsFile loadMappings(String input) {
        return MappingsFile.load(input);
    }

    @Override
    public MappingsFile loadMappingsFile(String input) throws IOException {
        File tempFile = tempFile();
        Files.writeString(tempFile.toPath(), input);
        return MappingsFile.load(tempFile);
    }

    @Override
    public Map<String, IdentifierMappings> getLookup(MappingsFile mappingsFile) {
        return mappingsFile.getBlockIdentifierLookup();
    }

    @Test
    public void testItemBlockFallback() {
        MappingsFile mappingsFile = MappingsFile.load("""
                        {
                          "items": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone"
                            }
                          ]
                        }
                """);

        Identifier input = new Identifier("minecraft:wool", Collections.emptyMap());

        // Blocks shouldn't fall back to items
        assertEquals(Optional.empty(), convert(mappingsFile, input));
    }

    @Test
    public void testResolver() {
        MappingsFile mappingsFile = MappingsFile.load("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone"
                            }
                          ]
                        }
                """);
        MappingsFileResolvers resolvers = new MappingsFileResolvers(mappingsFile);
        MappingsFileIdentifierResolver resolver = resolvers.asBlockIdentifierResolver();
        Identifier input = new Identifier("minecraft:wool", Collections.emptyMap());

        // Items should fall back to blocks
        assertEquals(Optional.of(new Identifier("minecraft:stone", Collections.emptyMap())), resolver.to(input));
    }

    @Test
    public void testResolverReverse() {
        MappingsFile mappingsFile = MappingsFile.load("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone"
                            }
                          ]
                        }
                """);
        MappingsFileResolvers resolvers = new MappingsFileResolvers(mappingsFile);
        MappingsFileIdentifierResolver resolver = resolvers.asBlockIdentifierResolver();
        Identifier input = new Identifier("minecraft:stone", Collections.emptyMap());

        // Items should fall back to blocks
        assertEquals(Optional.of(new Identifier("minecraft:wool", Collections.emptyMap())), resolver.from(input));
    }

    @Test
    public void testResolverEquals() {
        MappingsFile mappingsFile = MappingsFile.load("""
                        {
                          "identifiers": [
                            {
                              "old_identifier": "minecraft:wool",
                              "new_identifier": "minecraft:stone"
                            }
                          ]
                        }
                """);
        MappingsFileResolvers resolvers = new MappingsFileResolvers(mappingsFile);
        assertEquals(mappingsFile, resolvers.getMappings());
        assertEquals(mappingsFile.inverse(), resolvers.getInverseMappings());
    }
}
