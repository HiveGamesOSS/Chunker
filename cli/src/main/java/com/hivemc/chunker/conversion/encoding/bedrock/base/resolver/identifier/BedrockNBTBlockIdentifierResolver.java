package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.identifier.states.StateValueBoolean;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.resolver.Resolver;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.Optional;

/**
 * Resolver for turning a CompoundTag into an Identifier for blocks.
 * Note: The format differs for items.
 */
public class BedrockNBTBlockIdentifierResolver implements Resolver<BedrockBlockCompoundTag, Identifier> {
    protected final Version version;
    protected final int stateVersion;

    /**
     * Create a new NBT block identifier resolver.
     *
     * @param version      the version being converted.
     * @param stateVersion the state version to write for block states (not written if zero).
     */
    public BedrockNBTBlockIdentifierResolver(Version version, int stateVersion) {
        this.version = version;
        this.stateVersion = stateVersion;
    }

    @Override
    public Optional<Identifier> to(BedrockBlockCompoundTag input) {
        if (input.compoundTag() == null) return Optional.empty(); // compoundTag is required for decode

        // Grab the identifier
        String identifier = input.compoundTag().getString("name", null);
        if (identifier == null) return Optional.empty(); // Not possible to decode

        // Read the states
        CompoundTag statesTag = input.compoundTag().getCompound("states");
        Map<String, StateValue<?>> states;
        if (statesTag == null) {
            states = Map.of("waterlogged", input.waterlogged() ? StateValueBoolean.TRUE : StateValueBoolean.FALSE);
        } else {
            states = new Object2ObjectOpenHashMap<>(statesTag.size() + 1);
            states.put("waterlogged", input.waterlogged() ? StateValueBoolean.TRUE : StateValueBoolean.FALSE);
            for (Map.Entry<String, Tag<?>> tag : statesTag) {
                states.put(tag.getKey(), StateValue.fromNBT(tag.getValue()));
            }
        }

        // Create an identifier
        return Optional.of(new Identifier(identifier, states));
    }

    @Override
    public Optional<BedrockBlockCompoundTag> from(Identifier input) {
        // Create the block compound tag
        CompoundTag output = new CompoundTag(4);
        output.put("name", input.getIdentifier());

        // Write the stateVersion if it was provided
        if (stateVersion > 0) {
            output.put("version", stateVersion);
        }

        // Write the states
        if (!input.getStates().isEmpty()) {
            CompoundTag states = new CompoundTag(input.getStates().size());
            for (Map.Entry<String, StateValue<?>> entry : input.getStates().entrySet()) {
                if (entry.getKey().equals("waterlogged")) continue;
                states.put(entry.getKey(), entry.getValue().toNBT());
            }
            output.put("states", states);
        }

        // Return the BedrockBlockCompoundTag, using waterlogged to indicate if it's waterlogged
        boolean waterlogged = input.getStates().getOrDefault("waterlogged", StateValueBoolean.FALSE) == StateValueBoolean.TRUE;
        return Optional.of(new BedrockBlockCompoundTag(output, waterlogged));
    }
}
