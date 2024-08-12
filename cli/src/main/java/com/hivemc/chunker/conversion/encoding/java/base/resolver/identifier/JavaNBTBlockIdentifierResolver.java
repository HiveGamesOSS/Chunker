package com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.resolver.Resolver;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Resolver for turning a CompoundTag into an Identifier for blocks.
 * Note: The format differs for items.
 */
public class JavaNBTBlockIdentifierResolver implements Resolver<CompoundTag, Identifier> {
    protected final Version version;

    /**
     * Create a new Java NBT block identifier resolver.
     *
     * @param version the java version to use.
     */
    public JavaNBTBlockIdentifierResolver(Version version) {
        this.version = version;
    }

    @Override
    public Optional<Identifier> to(CompoundTag input) {
        String identifier = input.getString("Name", null);
        if (identifier == null) return Optional.empty(); // Not possible to decode

        // Read the states
        CompoundTag statesTag = input.getCompound("Properties");
        Map<String, StateValue<?>> states;
        if (statesTag == null) {
            states = Collections.emptyMap();
        } else {
            states = new Object2ObjectOpenHashMap<>(statesTag.size());
            for (Map.Entry<String, Tag<?>> tag : statesTag) {
                states.put(tag.getKey(), StateValue.fromNBT(tag.getValue()));
            }
        }

        // Create an identifier
        return Optional.of(new Identifier(identifier, states));
    }

    @Override
    public Optional<CompoundTag> from(Identifier input) {
        // Create the block compound tag
        CompoundTag output = new CompoundTag();
        output.put("Name", input.getIdentifier());

        // Write the states
        if (!input.getStates().isEmpty()) {
            CompoundTag states = new CompoundTag();
            for (Map.Entry<String, StateValue<?>> entry : input.getStates().entrySet()) {
                states.put(entry.getKey(), entry.getValue().toNBT());
            }
            output.put("Properties", states);
        }

        // Return the CompoundTag
        return Optional.of(output);
    }
}
