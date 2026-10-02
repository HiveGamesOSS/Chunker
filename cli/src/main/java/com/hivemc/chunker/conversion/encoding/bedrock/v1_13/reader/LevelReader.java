package com.hivemc.chunker.conversion.encoding.bedrock.v1_13.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.BedrockDataVersion;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockLevelReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockWorldReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolversBuilder;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockCompoundTag;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockNBTBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy.BedrockLegacyBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy.BedrockLegacyFallbackBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy.BedrockNBTLegacyBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.legacy.BedrockNBTLegacyFallbackBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.itemstack.BedrockItemStackResolver;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.resolver.Resolver;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class LevelReader extends BedrockLevelReader {
    public LevelReader(File inputDirectory, Version inputVersion, Converter converter) {
        super(inputDirectory, inputVersion, converter);
    }

    @Override
    public BedrockWorldReader createWorldReader(Map<RegionCoordPair, Set<ChunkCoordPair>> presentRegions, Dimension dimension) {
        return new WorldReader(resolvers, converter, database, presentRegions, dimension);
    }

    @Override
    public BedrockResolversBuilder buildResolvers(Converter converter) {
        Version version = getVersion();
        BedrockDataVersion bedrockDataVersion = BedrockDataVersion.getNearestVersion(version);

        // Normal case, covers most blocks
        Resolver<BedrockBlockCompoundTag, Identifier> modernNbt = new BedrockNBTBlockIdentifierResolver(version, bedrockDataVersion.getStateVersion());
        Resolver<Identifier, ChunkerBlockIdentifier> modernBlock = new BedrockBlockIdentifierResolver(converter, version, isReader(), converter.shouldAllowCustomIdentifiers());

        // Some worlds (LCE conversions in particular) never went through the 1.13 flattening, so they've still got old
        // numeric id + data value blocks even though the world itself reports a modern version, fall back to these when
        // the modern resolvers don't cut it
        Resolver<BedrockBlockCompoundTag, Identifier> legacyNbt = new BedrockNBTLegacyBlockIdentifierResolver(version);
        Resolver<Identifier, ChunkerBlockIdentifier> legacyBlock = new BedrockLegacyBlockIdentifierResolver(converter, version, isReader(), converter.shouldAllowCustomIdentifiers());

        return super.buildResolvers(converter)
                .itemStackResolverConstructor(BedrockItemStackResolver::new)
                .nbtBlockIdentifierResolver(new BedrockNBTLegacyFallbackBlockIdentifierResolver(modernNbt, legacyNbt))
                .blockIdentifierResolver(new BedrockLegacyFallbackBlockIdentifierResolver(modernBlock, legacyBlock));
    }
}
