package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy;

import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockCompoundTag;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.resolver.Resolver;

import java.util.Optional;

/**
 * Resolver which combines the modern NBT block identifier resolver with the legacy (data value based) resolver.
 * Some worlds (LCE conversions in particular) never went through the 1.13 flattening, so they still contain blocks
 * encoded with a "val" data value even though the world itself reports a modern version.
 * Encoding is always performed with the modern resolver.
 */
public class BedrockNBTLegacyFallbackBlockIdentifierResolver implements Resolver<BedrockBlockCompoundTag, Identifier> {
    protected final Resolver<BedrockBlockCompoundTag, Identifier> modernResolver;
    protected final Resolver<BedrockBlockCompoundTag, Identifier> legacyResolver;

    /**
     * Create a new NBT block identifier resolver which falls back to a legacy resolver.
     *
     * @param modernResolver the resolver used for modern (flattened) blocks, also used for encoding.
     * @param legacyResolver the resolver used for legacy blocks which use a "val" data value.
     */
    public BedrockNBTLegacyFallbackBlockIdentifierResolver(Resolver<BedrockBlockCompoundTag, Identifier> modernResolver, Resolver<BedrockBlockCompoundTag, Identifier> legacyResolver) {
        this.modernResolver = modernResolver;
        this.legacyResolver = legacyResolver;
    }

    @Override
    public Optional<Identifier> to(BedrockBlockCompoundTag input) {
        CompoundTag compoundTag = input.compoundTag();
        if (compoundTag == null) return Optional.empty(); // compoundTag is required for decode

        // Use the legacy resolver for blocks which only have a data value, the states check ensures no half upgraded
        // data loses its states as the legacy resolver would ignore them
        if (compoundTag.contains("val") && compoundTag.getCompound("states") == null) {
            return legacyResolver.to(input).or(() -> modernResolver.to(input));
        }

        return modernResolver.to(input);
    }

    @Override
    public Optional<BedrockBlockCompoundTag> from(Identifier input) {
        return modernResolver.from(input);
    }
}
