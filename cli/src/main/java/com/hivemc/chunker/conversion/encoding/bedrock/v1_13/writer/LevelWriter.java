package com.hivemc.chunker.conversion.encoding.bedrock.v1_13.writer;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.bedrock.BedrockDataVersion;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolversBuilder;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockNBTBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.itemstack.BedrockItemStackResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockLevelWriter;
import com.hivemc.chunker.conversion.encoding.bedrock.base.writer.BedrockWorldWriter;

import java.io.File;

public class LevelWriter extends BedrockLevelWriter {
    public LevelWriter(File outputFolder, Version version, Converter converter) {
        super(outputFolder, version, converter);
    }

    @Override
    public BedrockWorldWriter createWorldWriter() {
        return new WorldWriter(outputFolder, converter, resolvers, database);
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
