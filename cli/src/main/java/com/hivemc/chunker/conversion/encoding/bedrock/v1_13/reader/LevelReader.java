package com.hivemc.chunker.conversion.encoding.bedrock.v1_13.reader;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.BedrockDataVersion;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockLevelReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.reader.BedrockWorldReader;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolversBuilder;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockNBTBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.itemstack.BedrockItemStackResolver;
import com.hivemc.chunker.conversion.intermediate.column.chunk.ChunkCoordPair;
import com.hivemc.chunker.conversion.intermediate.column.chunk.RegionCoordPair;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;

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
        return super.buildResolvers(converter)
                .itemStackResolverConstructor(BedrockItemStackResolver::new)
                .nbtBlockIdentifierResolver(new BedrockNBTBlockIdentifierResolver(version, bedrockDataVersion.getStateVersion()))
                .blockIdentifierResolver(new BedrockBlockIdentifierResolver(converter, version, isReader(), converter.shouldAllowCustomIdentifiers()));
    }
}
