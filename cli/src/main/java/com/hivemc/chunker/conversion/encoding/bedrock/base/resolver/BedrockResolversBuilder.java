package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver;

import com.hivemc.chunker.conversion.encoding.base.Converter;
import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityResolver;
import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.BedrockDataVersion;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.identifier.BedrockBlockCompoundTag;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.PreTransformManager;
import com.hivemc.chunker.conversion.intermediate.column.biome.ChunkerBiome;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.banner.ChunkerBannerPattern;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.enchantment.ChunkerEnchantmentType;
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

import java.util.Optional;
import java.util.function.Function;

/**
 * Builder style class for generating BedrockResolvers, this allows you to override specific resolvers while inheriting
 * the previous ones.
 */
public class BedrockResolversBuilder {
    private final Converter converter;
    private final Version version;
    private final boolean enableCaching;

    // Resolvers
    private Resolver<BedrockBlockCompoundTag, Identifier> nbtBlockIdentifierResolver;
    private Resolver<Identifier, ChunkerItemStack> itemIdentifierResolver;
    private Resolver<Identifier, ChunkerBlockIdentifier> blockIdentifierResolver;
    private Resolver<Identifier, ChunkerBlockIdentifier> itemBlockIdentifierResolver;
    private Resolver<String, ChunkerEntityType> entityTypeResolver;
    private Resolver<Integer, ChunkerBiome> biomeIDResolver;
    private Resolver<Integer, ChunkerEffectType> effectIDResolver;
    private Resolver<Integer, ChunkerEnchantmentType> enchantmentIDResolver;
    private Resolver<String, PaintingEntity.Motive> paintingMotiveResolver;
    private Resolver<Integer, ChunkerPotionType> potionIDResolver;
    private Resolver<String, ChunkerTrimPattern> trimPatternResolver;
    private Resolver<String, ChunkerTrimMaterial> trimMaterialResolver;
    private Resolver<String, ChunkerBannerPattern> bannerPatternResolver;
    private Function<BedrockResolvers, Resolver<CompoundTag, ChunkerItemStack>> itemStackResolverConstructor;
    private Function<BedrockResolvers, BlockEntityResolver<BedrockResolvers, CompoundTag>> blockEntityResolverConstructor;
    private Function<BedrockResolvers, EntityResolver<BedrockResolvers, CompoundTag>> entityResolverConstructor;
    private PreTransformManager preTransformManager;

    /**
     * Create a new bedrock resolver builder.
     *
     * @param converter     the converter instance.
     * @param version       the version being used for the resolvers.
     * @param enableCaching if caching should be used on suitable resolvers (block identifier / item identifier).
     */
    public BedrockResolversBuilder(Converter converter, Version version, boolean enableCaching) {
        this.converter = converter;
        this.version = version;
        this.enableCaching = enableCaching;
    }

    /**
     * Create a new BedrockResolvers based on the set resolvers.
     *
     * @return the new instance.
     */
    public BedrockResolvers build() {
        Resolver<BedrockBlockCompoundTag, ChunkerBlockIdentifier> nbtChunkerBlockIdentifierResolver = nbtBlockIdentifierResolver.then(blockIdentifierResolver);
        BedrockDataVersion dataVersion = BedrockDataVersion.getNearestVersion(version);

        return new BedrockResolvers() {
            private final Resolver<CompoundTag, ChunkerItemStack> itemStackResolver = itemStackResolverConstructor.apply(this);
            private final BlockEntityResolver<BedrockResolvers, CompoundTag> blockEntityResolver = blockEntityResolverConstructor.apply(this);
            private final EntityResolver<BedrockResolvers, CompoundTag> entityResolver = entityResolverConstructor.apply(this);

            @Override
            public ChunkerBlockIdentifier readBlock(BedrockBlockCompoundTag input) {
                return nbtChunkerBlockIdentifierResolver.to(input).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BLOCK, input.compoundTag().toSNBT());

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
            public ChunkerBlockIdentifier readItemBlockIdentifier(Identifier input) {
                return itemBlockIdentifierResolver.to(input).orElseGet(() -> {
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
                return itemStack.orElseGet(() -> new ChunkerItemStack(readItemBlockIdentifier(input)));
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
            public BedrockBlockCompoundTag writeBlock(ChunkerBlockIdentifier chunkerBlockIdentifier) {
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
            public Optional<Identifier> writeItemBlockIdentifier(ChunkerBlockIdentifier identifier, boolean reportMissing) {
                Optional<Identifier> result = itemBlockIdentifierResolver.from(identifier);
                if (result.isEmpty() && reportMissing) {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.BLOCK, String.valueOf(identifier));
                }
                return result;
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
                        return writeItemBlockIdentifier(chunkerBlockIdentifier, true).orElseGet(() -> {
                            // Write air (we've already logged it)
                            return writeItemBlockIdentifier(ChunkerBlockIdentifier.AIR, false).orElseThrow();
                        });
                    } else {
                        // Report the error
                        converter.logMissingMapping(Converter.MissingMappingType.ITEM, String.valueOf(chunkerItemStack));

                        // Write air
                        return writeItemBlockIdentifier(ChunkerBlockIdentifier.AIR, false).orElseThrow();
                    }
                });
            }

            @Override
            public ChunkerPotionType readPotionTypeID(short potionID) {
                return potionIDResolver.to((int) potionID).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.POTION, String.valueOf(potionID));

                    // Use water
                    return ChunkerPotionType.WATER;
                });
            }

            @Override
            public short writePotionTypeID(ChunkerPotionType potionType) {
                return potionIDResolver.from(potionType).orElseGet(() -> {
                    // Report the error
                    converter.logMissingMapping(Converter.MissingMappingType.POTION, String.valueOf(potionType));

                    // Use water
                    return 0;
                }).shortValue();
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
            public BedrockDataVersion dataVersion() {
                return dataVersion;
            }

            @Override
            public BlockEntityResolver<BedrockResolvers, CompoundTag> blockEntityResolver() {
                return blockEntityResolver;
            }

            @Override
            public EntityResolver<BedrockResolvers, CompoundTag> entityResolver() {
                return entityResolver;
            }

            @Override
            public Resolver<BedrockBlockCompoundTag, Identifier> nbtBlockIdentifierResolver() {
                return nbtBlockIdentifierResolver;
            }

            @Override
            public Resolver<Identifier, ChunkerItemStack> chunkerItemIdentifierResolver() {
                return itemIdentifierResolver;
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
            public Resolver<String, ChunkerEntityType> entityTypeResolver() {
                return entityTypeResolver;
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
            public ChunkerBiome getFallbackBiome(Dimension dimension) {
                return switch (dimension) {
                    case OVERWORLD -> ChunkerBiome.ChunkerVanillaBiome.PLAINS;
                    case NETHER -> ChunkerBiome.ChunkerVanillaBiome.NETHER_WASTES;
                    case THE_END -> ChunkerBiome.ChunkerVanillaBiome.THE_END;
                };
            }
        };
    }

    public BedrockResolversBuilder nbtBlockIdentifierResolver(Resolver<BedrockBlockCompoundTag, Identifier> resolver) {
        nbtBlockIdentifierResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder itemIdentifierResolver(Resolver<Identifier, ChunkerItemStack> resolver) {
        itemIdentifierResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder blockIdentifierResolver(Resolver<Identifier, ChunkerBlockIdentifier> resolver) {
        blockIdentifierResolver = enableCaching ? resolver.cached() : resolver;
        return this;
    }

    public BedrockResolversBuilder itemBlockIdentifierResolver(Resolver<Identifier, ChunkerBlockIdentifier> resolver) {
        itemBlockIdentifierResolver = enableCaching ? resolver.cached() : resolver;
        return this;
    }

    public BedrockResolversBuilder entityTypeResolver(Resolver<String, ChunkerEntityType> resolver) {
        entityTypeResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder biomeIDResolver(Resolver<Integer, ChunkerBiome> resolver) {
        biomeIDResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder effectIDResolver(Resolver<Integer, ChunkerEffectType> resolver) {
        effectIDResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder enchantmentIDResolver(Resolver<Integer, ChunkerEnchantmentType> resolver) {
        enchantmentIDResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder paintingMotiveResolver(Resolver<String, PaintingEntity.Motive> resolver) {
        paintingMotiveResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder potionIDResolver(Resolver<Integer, ChunkerPotionType> resolver) {
        potionIDResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder trimPatternResolver(Resolver<String, ChunkerTrimPattern> resolver) {
        trimPatternResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder trimMaterialResolver(Resolver<String, ChunkerTrimMaterial> resolver) {
        trimMaterialResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder bannerPatternResolver(Resolver<String, ChunkerBannerPattern> resolver) {
        bannerPatternResolver = resolver;
        return this;
    }

    public BedrockResolversBuilder itemStackResolverConstructor(Function<BedrockResolvers, Resolver<CompoundTag, ChunkerItemStack>> constructor) {
        itemStackResolverConstructor = constructor;
        return this;
    }

    public BedrockResolversBuilder blockEntityResolverConstructor(Function<BedrockResolvers, BlockEntityResolver<BedrockResolvers, CompoundTag>> constructor) {
        blockEntityResolverConstructor = constructor;
        return this;
    }

    public BedrockResolversBuilder entityResolverConstructor(Function<BedrockResolvers, EntityResolver<BedrockResolvers, CompoundTag>> constructor) {
        entityResolverConstructor = constructor;
        return this;
    }

    public BedrockResolversBuilder preTransformManager(PreTransformManager resolver) {
        preTransformManager = resolver;
        return this;
    }
}
