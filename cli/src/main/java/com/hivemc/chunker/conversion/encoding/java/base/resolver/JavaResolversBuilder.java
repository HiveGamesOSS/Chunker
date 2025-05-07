package com.hivemc.chunker.conversion.encoding.java.base.resolver;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityResolver;
import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityResolver;
import com.hivemc.chunker.conversion.encoding.java.JavaDataVersion;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.banner.ChunkerBannerPattern;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.enchantment.ChunkerEnchantmentType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.horn.ChunkerHornInstrument;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerEffectType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerPotionType;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrimMaterial;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.trim.ChunkerTrimPattern;
import com.hivemc.chunker.conversion.intermediate.column.entity.PaintingEntity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.world.Dimension;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.resolver.Resolver;
import com.hivemc.chunker.util.LegacyIdentifier;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

/**
 * Builder style class for generating JavaResolvers, this allows you to override specific resolvers while inheriting the
 * previous ones.
 */
public class JavaResolversBuilder {
    private final Converter converter;
    private final Version version;
    private final boolean enableCaching;

    // Resolvers
    private Resolver<CompoundTag, Identifier> nbtBlockIdentifierResolver;
    private Resolver<Integer, String> blockIDResolver;
    private Resolver<Identifier, ChunkerItemStack> itemIdentifierResolver;
    private Resolver<Identifier, ChunkerBlockIdentifier> blockIdentifierResolver;
    private Resolver<String, ChunkerEntityType> entityTypeResolver;
    private Resolver<String, ChunkerBiome> biomeNameResolver;
    private Resolver<Integer, ChunkerBiome> biomeIDResolver;
    private Resolver<String, ChunkerEffectType> effectResolver;
    private Resolver<Integer, ChunkerEffectType> effectIDResolver;
    private Resolver<String, ChunkerEnchantmentType> enchantmentResolver;
    private Resolver<Integer, ChunkerEnchantmentType> enchantmentIDResolver;
    private Resolver<String, ChunkerHornInstrument> hornInstrumentResolver;
    private Resolver<String, PaintingEntity.Motive> paintingMotiveResolver;
    private Resolver<String, ChunkerPotionType> potionTypeResolver;
    private Resolver<byte[], byte[]> mapColorsResolver;
    private Resolver<String, ChunkerTrimPattern> trimPatternResolver;
    private Resolver<String, ChunkerTrimMaterial> trimMaterialResolver;
    private Resolver<String, ChunkerBannerPattern> bannerPatternResolver;
    private Resolver<String, ChunkerBannerPattern> bannerPatternShortNameResolver;
    private Function<JavaResolvers, Resolver<CompoundTag, ChunkerItemStack>> itemStackResolverConstructor;
    private Function<JavaResolvers, BlockEntityResolver<JavaResolvers, CompoundTag>> blockEntityResolverConstructor;
    private Function<JavaResolvers, EntityResolver<JavaResolvers, CompoundTag>> entityResolverConstructor;
    private PreTransformManager preTransformManager;

    /**
     * Create a new java resolvers builder.
     *
     * @param converter     the converter instance.
     * @param version       the version being used for the resolvers.
     * @param enableCaching if caching should be used on suitable resolvers (block identifier / item identifier).
     */
    public JavaResolversBuilder(Converter converter, Version version, boolean enableCaching) {
        this.converter = converter;
        this.version = version;
        this.enableCaching = enableCaching;
    }

    /**
     * Create a new avaResolvers based on the set resolvers.
     *
     * @return the new instance.
     */
    public JavaResolvers build() {
        Resolver<CompoundTag, ChunkerBlockIdentifier> nbtChunkerBlockIdentifierResolver = nbtBlockIdentifierResolver.then(blockIdentifierResolver);
        JavaDataVersion dataVersion = JavaDataVersion.getNearestVersion(version);

        return new JavaResolvers() {
            private final Resolver<CompoundTag, ChunkerItemStack> itemStackResolver = itemStackResolverConstructor.apply(this);
            private final BlockEntityResolver<JavaResolvers, CompoundTag> blockEntityResolver = blockEntityResolverConstructor.apply(this);
            private final EntityResolver<JavaResolvers, CompoundTag> entityResolver = entityResolverConstructor.apply(this);

            @Override
            public ChunkerBlockIdentifier readBlock(CompoundTag input) {
                return nbtChunkerBlockIdentifierResolver.to(input).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BLOCK, input.toSNBT());

                    // Return air
                    return ChunkerBlockIdentifier.AIR;
                });
            }

            @Override
            public ChunkerBlockIdentifier readBlock(LegacyIdentifier input) {
                // Turn the LegacyIdentifier into an Identifier then resolve it
                Optional<ChunkerBlockIdentifier> output = blockIDResolver.to(input.id())
                        .map(identifier -> Identifier.fromData(identifier, OptionalInt.of(input.data())))
                        .flatMap(blockIdentifierResolver::to);

                // Return the output or report an error
                return output.orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BLOCK, String.valueOf(input));

                    // Return air
                    return ChunkerBlockIdentifier.AIR;
                });
            }

            @Override
            public ChunkerBlockIdentifier readBlockIdentifier(Identifier input) {
                return blockIdentifierResolver.to(input).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BLOCK, String.valueOf(input));

                    // Return air
                    return ChunkerBlockIdentifier.AIR;
                });
            }

            @Override
            public ChunkerItemStack readItem(CompoundTag input) {
                // Don't process item if not enabled
                if (!converter.shouldProcessItems()) return new ChunkerItemStack(ChunkerBlockIdentifier.AIR);

                return itemStackResolver.to(input).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.ITEM, input.toSNBT());

                    // Return air
                    return new ChunkerItemStack(ChunkerBlockIdentifier.AIR);
                });
            }

            @Override
            public ChunkerItemStack readItemIdentifier(Identifier input) {
                // Don't process item if not enabled
                if (!converter.shouldProcessItems()) return new ChunkerItemStack(ChunkerBlockIdentifier.AIR);

                // Try to turn the identifier into an item
                Optional<ChunkerItemStack> itemStack = itemIdentifierResolver.to(input);

                // Try to turn the identifier into a block if it's not present
                return itemStack.orElseGet(() -> new ChunkerItemStack(readBlockIdentifier(input)));
            }

            @Override
            public Optional<CompoundTag> writeItem(ChunkerItemStack chunkerItemStack) {
                return Optional.ofNullable(itemStackResolver.from(chunkerItemStack).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.ITEM, String.valueOf(chunkerItemStack));

                    // Return null
                    return null;
                }));
            }

            @Override
            public Identifier writeItemIdentifier(ChunkerItemStack chunkerItemStack) {
                // Try turning it from an item into an identifier
                Optional<Identifier> itemStack = itemIdentifierResolver.from(chunkerItemStack);

                // Try to turn the identifier into a block if it's not an item
                return itemStack.orElseGet(() -> {
                    // If it's a block write it as one otherwise log the error
                    if (chunkerItemStack.getIdentifier() instanceof ChunkerBlockIdentifier chunkerBlockIdentifier) {
                        return writeBlockIdentifier(chunkerBlockIdentifier, true).orElseGet(() -> {
                            // Write air (we've already logged it)
                            return writeBlockIdentifier(ChunkerBlockIdentifier.AIR, false).orElseThrow();
                        });
                    } else {
                        // Report the error
                        converter.logMissingMapping(Converter.MissingMappingType.ITEM, String.valueOf(chunkerItemStack));

                        // Write air
                        return writeBlockIdentifier(ChunkerBlockIdentifier.AIR, false).orElseThrow();
                    }
                });
            }

            @Override
            public ChunkerBiome readBiome(String biome, Dimension dimension) {
                return biomeNameResolver.to(biome).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BIOME, biome);

                    // Return fallback
                    return getFallbackBiome(dimension);
                });
            }

            @Override
            public ChunkerBiome readBiome(int biome, Dimension dimension) {
                return biomeIDResolver.to(biome).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BIOME, String.valueOf(biome));

                    // Return fallback
                    return getFallbackBiome(dimension);
                });
            }

            @Override
            public int writeBiomeID(ChunkerBiome biome, Dimension dimension) {
                return biomeIDResolver.from(biome).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BIOME, String.valueOf(biome));

                    // Return fallback
                    return biomeIDResolver.from(getFallbackBiome(dimension)).orElseThrow();
                });
            }

            @Override
            public String writeBiome(ChunkerBiome biome, Dimension dimension) {
                return biomeNameResolver.from(biome).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BIOME, String.valueOf(biome));

                    // Return fallback
                    return biomeNameResolver.from(getFallbackBiome(dimension)).orElseThrow();
                });
            }

            @Override
            public CompoundTag writeBlock(ChunkerBlockIdentifier chunkerBlockIdentifier) {
                return nbtChunkerBlockIdentifierResolver.from(chunkerBlockIdentifier).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BLOCK, String.valueOf(chunkerBlockIdentifier));

                    // Return air
                    return nbtChunkerBlockIdentifierResolver.from(ChunkerBlockIdentifier.AIR).orElseThrow();
                });
            }

            @Override
            public Optional<Identifier> writeBlockIdentifier(ChunkerBlockIdentifier identifier, boolean reportMissing) {
                Optional<Identifier> result = blockIdentifierResolver.from(identifier);
                if (result.isEmpty() && reportMissing) {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BLOCK, String.valueOf(identifier));
                }
                return result;
            }

            @Override
            public LegacyIdentifier writeLegacyBlockIdentifier(ChunkerBlockIdentifier chunkerBlockIdentifier) {
                return blockIdentifierResolver.from(chunkerBlockIdentifier)
                        .flatMap(identifier -> blockIDResolver.from(identifier.getIdentifier())
                                .map(id -> new LegacyIdentifier(id, (byte) identifier.getDataValue().orElse(0))))
                        .orElseGet(() -> {
                            // Report the error
                            converter.logMissingMapping(Converter.MissingMappingType.BLOCK, String.valueOf(chunkerBlockIdentifier));

                            // Return air
                            return new LegacyIdentifier(0, (byte) 0);
                        });
            }

            @Override
            public ChunkerEffectType readEffect(String effect) {
                return effectResolver.to(effect).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.EFFECT, effect);

                    // Return empty
                    return ChunkerEffectType.EMPTY;
                });
            }

            @Override
            public String writeEffect(ChunkerEffectType effect) {
                return effectResolver.from(effect).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.EFFECT, String.valueOf(effect));

                    // Return empty
                    return "minecraft:empty";
                });
            }

            @Override
            public ChunkerEffectType readEffectID(int effectID) {
                return effectIDResolver.to(effectID).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.EFFECT, String.valueOf(effectID));

                    // Return empty
                    return ChunkerEffectType.EMPTY;
                });
            }

            @Override
            public int writeEffectID(ChunkerEffectType effect) {
                return effectIDResolver.from(effect).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.EFFECT, String.valueOf(effect));

                    // Return empty
                    return 0;
                });
            }

            @Override
            public ChunkerHornInstrument readHornInstrument(String identifier) {
                return hornInstrumentResolver.to(identifier).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.HORN, identifier);

                    // Return ponder
                    return ChunkerHornInstrument.PONDER_GOAT_HORN;
                });
            }

            @Override
            public String writeHornInstrument(ChunkerHornInstrument hornInstrument) {
                return hornInstrumentResolver.from(hornInstrument).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.HORN, String.valueOf(hornInstrument));

                    // Return ponder
                    return hornInstrumentResolver.from(ChunkerHornInstrument.PONDER_GOAT_HORN).orElseThrow();
                });
            }

            @Override
            public ChunkerPotionType readPotionType(String identifier) {
                return potionTypeResolver.to(identifier).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.POTION, identifier);

                    // Use water
                    return ChunkerPotionType.WATER;
                });
            }

            @Override
            public String writePotionType(ChunkerPotionType potionType) {
                return potionTypeResolver.from(potionType).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.POTION, String.valueOf(potionType));

                    // Use water
                    return potionTypeResolver.from(ChunkerPotionType.WATER).orElseThrow();
                });
            }

            @Override
            public PaintingEntity.Motive readPaintingMotive(String identifier) {
                return paintingMotiveResolver.to(identifier).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.PAINTING, identifier);

                    // Return kebab
                    return PaintingEntity.Motive.KEBAB;
                });
            }

            @Override
            public String writePaintingMotive(PaintingEntity.Motive motive) {
                return paintingMotiveResolver.from(motive).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.PAINTING, String.valueOf(motive));

                    // Return kebab
                    return paintingMotiveResolver.from(PaintingEntity.Motive.KEBAB).orElseThrow();
                });
            }

            @Override
            public byte[] readMapColors(byte[] javaMapColors) {
                return mapColorsResolver.to(javaMapColors).orElse(null);
            }

            @Override
            public byte[] writeMapColors(byte[] chunkerMapColors) {
                return mapColorsResolver.from(chunkerMapColors).orElse(null);
            }

            @Override
            public BlockEntityResolver<JavaResolvers, CompoundTag> blockEntityResolver() {
                return blockEntityResolver;
            }

            @Override
            public EntityResolver<JavaResolvers, CompoundTag> entityResolver() {
                return entityResolver;
            }

            @Override
            public Resolver<String, ChunkerEntityType> entityTypeResolver() {
                return entityTypeResolver;
            }

            @Override
            public Resolver<String, ChunkerEnchantmentType> enchantmentResolver() {
                return enchantmentResolver;
            }

            @Override
            public Resolver<Integer, ChunkerEnchantmentType> enchantmentIDResolver() {
                return enchantmentIDResolver;
            }

            @Override
            public Resolver<String, ChunkerTrimPattern> trimPatternResolver() {
                return trimPatternResolver;
            }

            @Override
            public Resolver<String, ChunkerTrimMaterial> trimMaterialResolver() {
                return trimMaterialResolver;
            }

            @Override
            public Resolver<String, ChunkerBannerPattern> bannerPatternResolver() {
                return bannerPatternResolver;
            }

            @Override
            public Resolver<String, ChunkerBannerPattern> bannerPatternShortNameResolver() {
                return bannerPatternShortNameResolver;
            }

            @Override
            public PreTransformManager preTransformManager() {
                return preTransformManager;
            }

            @Override
            public Converter converter() {
                return converter;
            }

            @Override
            public JavaDataVersion dataVersion() {
                return dataVersion;
            }

            @Override
            public Resolver<Identifier, ChunkerItemStack> chunkerItemIdentifierResolver() {
                return itemIdentifierResolver;
            }

            @Override
            public ChunkerBiome getFallbackBiome(Dimension dimension) {
                return switch (dimension) {
                    case OVERWORLD -> ChunkerBiome.ChunkerVanillaBiome.PLAINS;
                    case NETHER -> ChunkerBiome.ChunkerVanillaBiome.NETHER_WASTES;
                    case THE_END -> ChunkerBiome.ChunkerVanillaBiome.THE_END;
                };
            }
        };
    }

    public JavaResolversBuilder nbtBlockIdentifierResolver(Resolver<CompoundTag, Identifier> resolver) {
        nbtBlockIdentifierResolver = resolver;
        return this;
    }

    public JavaResolversBuilder itemIdentifierResolver(Resolver<Identifier, ChunkerItemStack> resolver) {
        itemIdentifierResolver = resolver;
        return this;
    }

    public JavaResolversBuilder blockIdentifierResolver(Resolver<Identifier, ChunkerBlockIdentifier> resolver) {
        blockIdentifierResolver = enableCaching ? resolver.cached() : resolver;
        return this;
    }

    public JavaResolversBuilder entityTypeResolver(Resolver<String, ChunkerEntityType> resolver) {
        entityTypeResolver = resolver;
        return this;
    }

    public JavaResolversBuilder biomeNameResolver(Resolver<String, ChunkerBiome> resolver) {
        biomeNameResolver = resolver;
        return this;
    }

    public JavaResolversBuilder biomeIDResolver(Resolver<Integer, ChunkerBiome> resolver) {
        biomeIDResolver = resolver;
        return this;
    }

    public JavaResolversBuilder effectResolver(Resolver<String, ChunkerEffectType> resolver) {
        effectResolver = resolver;
        return this;
    }

    public JavaResolversBuilder effectIDResolver(Resolver<Integer, ChunkerEffectType> resolver) {
        effectIDResolver = resolver;
        return this;
    }

    public JavaResolversBuilder enchantmentResolver(Resolver<String, ChunkerEnchantmentType> resolver) {
        enchantmentResolver = resolver;
        return this;
    }

    public JavaResolversBuilder enchantmentIDResolver(Resolver<Integer, ChunkerEnchantmentType> resolver) {
        enchantmentIDResolver = resolver;
        return this;
    }

    public JavaResolversBuilder hornInstrumentResolver(Resolver<String, ChunkerHornInstrument> resolver) {
        hornInstrumentResolver = resolver;
        return this;
    }

    public JavaResolversBuilder paintingMotiveResolver(Resolver<String, PaintingEntity.Motive> resolver) {
        paintingMotiveResolver = resolver;
        return this;
    }

    public JavaResolversBuilder potionTypeResolver(Resolver<String, ChunkerPotionType> resolver) {
        potionTypeResolver = resolver;
        return this;
    }

    public JavaResolversBuilder mapColorsResolver(Resolver<byte[], byte[]> resolver) {
        mapColorsResolver = resolver;
        return this;
    }

    public JavaResolversBuilder blockIDResolver(Resolver<Integer, String> resolver) {
        blockIDResolver = resolver;
        return this;
    }

    public JavaResolversBuilder trimPatternResolver(Resolver<String, ChunkerTrimPattern> resolver) {
        trimPatternResolver = resolver;
        return this;
    }

    public JavaResolversBuilder trimMaterialResolver(Resolver<String, ChunkerTrimMaterial> resolver) {
        trimMaterialResolver = resolver;
        return this;
    }

    public JavaResolversBuilder bannerPatternResolver(Resolver<String, ChunkerBannerPattern> resolver) {
        bannerPatternResolver = resolver;
        return this;
    }

    public JavaResolversBuilder bannerPatternShortNameResolver(Resolver<String, ChunkerBannerPattern> resolver) {
        bannerPatternShortNameResolver = resolver;
        return this;
    }

    public JavaResolversBuilder itemStackResolverConstructor(Function<JavaResolvers, Resolver<CompoundTag, ChunkerItemStack>> constructor) {
        itemStackResolverConstructor = constructor;
        return this;
    }

    public JavaResolversBuilder blockEntityResolverConstructor(Function<JavaResolvers, BlockEntityResolver<JavaResolvers, CompoundTag>> constructor) {
        blockEntityResolverConstructor = constructor;
        return this;
    }

    public JavaResolversBuilder entityResolverConstructor(Function<JavaResolvers, EntityResolver<JavaResolvers, CompoundTag>> constructor) {
        entityResolverConstructor = constructor;
        return this;
    }

    public JavaResolversBuilder preTransformManager(PreTransformManager resolver) {
        preTransformManager = resolver;
        return this;
    }
}
