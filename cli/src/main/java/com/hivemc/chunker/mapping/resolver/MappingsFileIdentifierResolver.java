package com.hivemc.chunker.mapping.resolver;

import com.hivemc.chunker.mapping.MappingsFile;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.resolver.Resolver;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A resolver which wraps a mapping file and the inverse.
 */
public class MappingsFileIdentifierResolver implements Resolver<Identifier, Identifier> {
    private final MappingsFile mappings;
    private final MappingsFile inverseMappings;
    private final BiFunction<MappingsFile, Identifier, Optional<Identifier>> conversionFunction;

    /**
     * Create a new resolver based on mappings file.
     *
     * @param mappings           the mappings file.
     * @param inverseMappings    the inverse mappings.
     * @param conversionFunction the function to use to convert the input identifier to the output.
     */
    public MappingsFileIdentifierResolver(MappingsFile mappings, MappingsFile inverseMappings, BiFunction<MappingsFile, Identifier, Optional<Identifier>> conversionFunction) {
        this.mappings = mappings;
        this.inverseMappings = inverseMappings;
        this.conversionFunction = conversionFunction;
    }

    @Override
    public Optional<Identifier> to(Identifier input) {
        return conversionFunction.apply(mappings, input);
    }

    @Override
    public Optional<Identifier> from(Identifier input) {
        return conversionFunction.apply(inverseMappings, input);
    }
}
