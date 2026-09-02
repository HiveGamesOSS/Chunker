package com.hivemc.chunker.conversion.encoding.java.base;

import com.hivemc.chunker.conversion.encoding.EncodingType;
import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.LevelReaderWriter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.java.base.reader.pretransform.legacy.JavaLegacyReaderPreTransformManager;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaLevelDirectoryResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolversBuilder;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.biome.JavaBiomeIDResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.biome.JavaNamedBiomeResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.JavaLegacyBlockEntityResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy.JavaLegacyEntityResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy.JavaLegacyEntityTypeResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy.JavaLegacyPaintingMotiveResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.JavaNBTBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy.JavaLegacyBlockIDResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy.JavaLegacyBlockIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.identifier.legacy.JavaLegacyItemIdentifierResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack.*;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.itemstack.legacy.JavaLegacyItemStackResolver;
import com.hivemc.chunker.conversion.encoding.java.base.writer.pretransform.legacy.JavaLegacyWriterPreTransformManager;

import java.io.File;

/**
 * A Java Level Reader / Writer, used to share resolvers.
 */
public interface JavaReaderWriter extends LevelReaderWriter {
    @Override
    default EncodingType getEncodingType() {
        return EncodingType.JAVA;
    }

    /**
     * Get the directory that is being used for reading/writing the level.
     *
     * @return the directory path, used as the base directory for fetching worlds.
     */
    File getLevelDirectory();

    /**
     * Build the resolvers which should be used for this version.
     *
     * @param converter the instance of the converter.
     * @return a builder so that this method can be overridden to update the resolvers to use.
     */
    default JavaResolversBuilder buildResolvers(Converter converter) {
        Version version = getVersion();
        return new JavaResolversBuilder(converter, version, true)
                .blockIDResolver(new JavaLegacyBlockIDResolver(version))
                .nbtBlockIdentifierResolver(new JavaNBTBlockIdentifierResolver(version))
                .itemIdentifierResolver(new JavaLegacyItemIdentifierResolver(converter, version, isReader()))
                .blockIdentifierResolver(new JavaLegacyBlockIdentifierResolver(converter, version, isReader(), converter.shouldAllowCustomIdentifiers()))
                .entityTypeResolver(new JavaLegacyEntityTypeResolver(version))
                .biomeNameResolver(new JavaNamedBiomeResolver(version, converter.shouldAllowCustomIdentifiers()))
                .biomeIDResolver(new JavaBiomeIDResolver(version))
                .effectResolver(new JavaEffectResolver(version))
                .effectIDResolver(new JavaEffectIDResolver(version))
                .enchantmentResolver(new JavaEnchantmentResolver(version))
                .enchantmentIDResolver(new JavaEnchantmentIDResolver(version))
                .hornInstrumentResolver(new JavaHornInstrumentResolver(version))
                .paintingMotiveResolver(new JavaLegacyPaintingMotiveResolver(version))
                .potionTypeResolver(new JavaPotionTypeResolver(version))
                .mapColorsResolver(new JavaMapColorsResolver(version))
                .mapDecorationResolver(new JavaMapDecorationResolver(version))
                .mapDecorationIDResolver(new JavaMapDecorationIDResolver(version))
                .trimPatternResolver(new JavaTrimPatternResolver(version))
                .trimMaterialResolver(new JavaTrimMaterialResolver(version))
                .bannerPatternShortNameResolver(new JavaBannerPatternShortNameResolver(version))
                .bannerPatternResolver(new JavaBannerPatternResolver(version))
                .itemStackResolverConstructor(JavaLegacyItemStackResolver::new)
                .blockEntityResolverConstructor((resolvers) -> new JavaLegacyBlockEntityResolver(version, resolvers))
                .entityResolverConstructor((resolvers) -> new JavaLegacyEntityResolver(version, resolvers))
                .levelDirectoryResolver(new JavaLevelDirectoryResolver(getLevelDirectory()))
                .preTransformManager(isReader() ? new JavaLegacyReaderPreTransformManager(version) : new JavaLegacyWriterPreTransformManager(version));
    }
}
