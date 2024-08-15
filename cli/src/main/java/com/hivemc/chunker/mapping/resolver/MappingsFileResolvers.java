package com.hivemc.chunker.mapping.resolver;

import com.hivemc.chunker.mapping.MappingsFile;

/**
 * A class to encapsulate the resolvers for MappingsFile and the inverse of the mappings file.
 * This ensures that the inverse is only calculated once.
 */
public class MappingsFileResolvers {
    private final MappingsFile mappings;
    private final MappingsFile inverseMappings;
    private final MappingsFileIdentifierResolver blockResolver;
    private final MappingsFileIdentifierResolver itemResolver;

    /**
     * Create MappingsFileResolvers from a mappings file.
     *
     * @param mappings the mappings file to use, which will be inverted on construction of this class.
     */
    public MappingsFileResolvers(MappingsFile mappings) {
        this.mappings = mappings;
        inverseMappings = mappings.inverse();
        blockResolver = new MappingsFileIdentifierResolver(mappings, inverseMappings, MappingsFile::convertBlock);
        itemResolver = new MappingsFileIdentifierResolver(mappings, inverseMappings, MappingsFile::convertItem);
    }

    /**
     * The cached block convert identifier resolver.
     *
     * @return the block resolver which can resolve input block identifiers to output block identifiers.
     */
    public MappingsFileIdentifierResolver asBlockIdentifierResolver() {
        return blockResolver;
    }

    /**
     * The cached item convert identifier resolver.
     *
     * @return the item resolver which can resolve input item identifiers to output item identifiers.
     */
    public MappingsFileIdentifierResolver asItemIdentifierResolver() {
        return itemResolver;
    }

    /**
     * Get the mappings file backing these resolvers. Note: Modifications are not reflected in the inverse.
     *
     * @return the mappings file.
     */
    public MappingsFile getMappings() {
        return mappings;
    }

    /**
     * Get the inverted mappings file backing these resolvers. Note: Modifications are not reflected in the inverse.
     *
     * @return the mappings file.
     */
    public MappingsFile getInverseMappings() {
        return inverseMappings;
    }
}
