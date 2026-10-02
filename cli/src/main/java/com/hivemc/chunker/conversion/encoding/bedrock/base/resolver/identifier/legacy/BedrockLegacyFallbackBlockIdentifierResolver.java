package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.resolver.Resolver;

import java.util.Optional;

/**
 * Resolver which combines the modern block identifier resolver with the legacy (data value based) resolver.
 * Identifiers which contain a "data" state came from the legacy NBT format, so the legacy resolver is preferred for
 * those, otherwise the modern resolver is used first.
 * Encoding is always performed with the modern resolver.
 */
public class BedrockLegacyFallbackBlockIdentifierResolver implements Resolver<Identifier, ChunkerBlockIdentifier> {
    protected final Resolver<Identifier, ChunkerBlockIdentifier> modernResolver;
    protected final Resolver<Identifier, ChunkerBlockIdentifier> legacyResolver;

    /**
     * Create a new block identifier resolver which falls back to a legacy resolver.
     *
     * @param modernResolver the resolver used for modern (flattened) blocks, also used for encoding.
     * @param legacyResolver the resolver used for legacy blocks which use a "data" state.
     */
    public BedrockLegacyFallbackBlockIdentifierResolver(Resolver<Identifier, ChunkerBlockIdentifier> modernResolver, Resolver<Identifier, ChunkerBlockIdentifier> legacyResolver) {
        this.modernResolver = modernResolver;
        this.legacyResolver = legacyResolver;
    }

    @Override
    public Optional<ChunkerBlockIdentifier> to(Identifier input) {
        // Use the legacy resolver for blocks containing legacy data
        if (input.getStates().containsKey("data")) {
            return legacyResolver.to(input).or(() -> modernResolver.to(input));
        }

        return modernResolver.to(input);
    }

    @Override
    public Optional<Identifier> from(ChunkerBlockIdentifier input) {
        return modernResolver.from(input);
    }
}
