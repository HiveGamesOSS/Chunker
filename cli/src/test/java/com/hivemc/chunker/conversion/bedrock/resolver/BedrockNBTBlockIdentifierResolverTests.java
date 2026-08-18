package com.hivemc.chunker.conversion.bedrock.resolver;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockCompoundTag;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockNBTBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy.BedrockNBTLegacyBlockIdentifierResolver;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValueBoolean;
import com.hivemc.chunker.mapping.identifier.states.StateValueString;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to ensure Bedrock block NBT always includes a states compound, including for custom blocks with no properties.
 */
public class BedrockNBTBlockIdentifierResolverTests {
    private static final int STATE_VERSION = 18163713;
    private final BedrockNBTBlockIdentifierResolver resolver = new BedrockNBTBlockIdentifierResolver(
            new Version(1, 21, 1),
            STATE_VERSION
    );
    private final BedrockNBTLegacyBlockIdentifierResolver legacyResolver = new BedrockNBTLegacyBlockIdentifierResolver(
            new Version(1, 12, 0)
    );

    @Test
    public void fromEmptyStatesWritesEmptyStatesCompound() {
        Optional<BedrockBlockCompoundTag> output = resolver.from(new Identifier("mod:custom_block"));

        assertTrue(output.isPresent());
        CompoundTag compoundTag = output.get().compoundTag();
        assertEquals("mod:custom_block", compoundTag.getString("name"));
        assertEquals(STATE_VERSION, compoundTag.getInt("version"));
        assertTrue(compoundTag.contains("states"));

        CompoundTag states = compoundTag.getCompound("states");
        assertNotNull(states);
        assertEquals(0, states.size());
        assertFalse(output.get().waterlogged());
    }

    @Test
    public void fromWaterloggedOnlyWritesEmptyStatesCompound() {
        Optional<BedrockBlockCompoundTag> output = resolver.from(new Identifier(
                "mod:custom_block",
                Map.of("waterlogged", StateValueBoolean.TRUE)
        ));

        assertTrue(output.isPresent());
        CompoundTag states = output.get().compoundTag().getCompound("states");
        assertNotNull(states);
        assertEquals(0, states.size());
        assertFalse(states.contains("waterlogged"));
        assertTrue(output.get().waterlogged());
    }

    @Test
    public void fromRealStatesOmitsWaterlogged() {
        Optional<BedrockBlockCompoundTag> output = resolver.from(new Identifier(
                "minecraft:furnace",
                Map.of(
                        "facing_direction", new StateValueString("north"),
                        "waterlogged", StateValueBoolean.FALSE
                )
        ));

        assertTrue(output.isPresent());
        CompoundTag states = output.get().compoundTag().getCompound("states");
        assertNotNull(states);
        assertEquals("north", states.getString("facing_direction"));
        assertFalse(states.contains("waterlogged"));
        assertFalse(output.get().waterlogged());
    }

    @Test
    public void fromLegacyEmptyStatesWritesEmptyStatesCompound() {
        Optional<BedrockBlockCompoundTag> output = legacyResolver.from(new Identifier("mod:custom_block"));

        assertTrue(output.isPresent());
        CompoundTag compoundTag = output.get().compoundTag();
        assertEquals("mod:custom_block", compoundTag.getString("name"));
        assertTrue(compoundTag.contains("states"));

        CompoundTag states = compoundTag.getCompound("states");
        assertNotNull(states);
        assertEquals(0, states.size());
        assertFalse(output.get().waterlogged());
    }
}
