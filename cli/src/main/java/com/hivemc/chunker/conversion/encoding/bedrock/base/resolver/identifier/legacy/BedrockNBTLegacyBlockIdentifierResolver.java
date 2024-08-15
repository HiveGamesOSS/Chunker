package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockCompoundTag;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.mapping.identifier.states.StateValue;
import com.hivemc.chunker.mapping.identifier.states.StateValueBoolean;
import com.hivemc.chunker.mapping.identifier.states.StateValueInt;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.resolver.Resolver;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.Optional;

/**
 * Resolver for turning a CompoundTag into an Identifier for blocks from legacy format with data values.
 * It uses the "data" state to encode the data for the block.
 * Note: The format differs for items.
 */
public class BedrockNBTLegacyBlockIdentifierResolver implements Resolver<BedrockBlockCompoundTag, Identifier> {
    protected final Version version;

    /**
     * Create a new block identifier resolver.
     *
     * @param version the version being read/written.
     */
    public BedrockNBTLegacyBlockIdentifierResolver(Version version) {
        this.version = version;
    }

    @Override
    public Optional<Identifier> to(BedrockBlockCompoundTag input) {
        String identifier = input.compoundTag().getString("name", null);
        if (identifier == null) return Optional.empty(); // Not possible to decode

        // Read the states
        CompoundTag statesTag = input.compoundTag().getCompound("states");
        Map<String, StateValue<?>> states;
        if (statesTag == null) {
            // See if a data value is present
            if (input.compoundTag().contains("val")) {
                states = Map.of(
                        "waterlogged", input.waterlogged() ? StateValueBoolean.TRUE : StateValueBoolean.FALSE,
                        "data", new StateValueInt(input.compoundTag().getShort("val", (short) 0))
                );
            } else {
                states = Map.of("waterlogged", input.waterlogged() ? StateValueBoolean.TRUE : StateValueBoolean.FALSE);
            }
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
        CompoundTag output = new CompoundTag();
        output.put("name", input.getIdentifier());

        // Write the data
        output.put("val", (short) ((StateValueInt) input.getStates().getOrDefault("data", StateValueInt.ZERO)).getValue());
        if (!input.getStates().isEmpty()) {
            CompoundTag states = new CompoundTag();
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
