package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver;

import com.hivemc.chunker.conversion.encoding.base.Converter;
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

/**
 * Various Bedrock Resolvers which are needed to read/write version specific data.
 */
public interface BedrockResolvers {
    /**
     * Read a block from a compound tag.
     *
     * @param input the block NBT.
     * @return the read block or an air block if it failed.
     */
    ChunkerBlockIdentifier readBlock(BedrockBlockCompoundTag input);

    /**
     * Read a block from an identifier.
     *
     * @param input the identifier.
     * @return the read block or an air block if it failed.
     */
    ChunkerBlockIdentifier readBlockIdentifier(Identifier input);

    /**
     * Read a block from an identifier for use as an item.
     *
     * @param input the identifier.
     * @return the read block or an air block if it failed.
     */
    ChunkerBlockIdentifier readItemBlockIdentifier(Identifier input);

    /**
     * Read an item from a compound tag.
     *
     * @param input the item NBT.
     * @return the read item or an air block if it failed.
     */
    ChunkerItemStack readItem(CompoundTag input);

    /**
     * Read an item from an identifier.
     *
     * @param input the item identifier.
     * @return the read item or an air block if it failed.
     */
    ChunkerItemStack readItemIdentifier(Identifier input);

    /**
     * Read a integer ID biome identifier.
     *
     * @param biome     the input ID.
     * @param dimension the dimension being read, used to get the fallback.
     * @return the biome or the fallback if it wasn't found.
     */
    ChunkerBiome readBiome(int biome, Dimension dimension);

    /**
     * Write an ID based biome identifier.
     *
     * @param biome     the biome type.
     * @param dimension the dimension being written, used to get the fallback.
     * @return the biome ID or the fallback if it wasn't found.
     */
    int writeBiomeID(ChunkerBiome biome, Dimension dimension);

    /**
     * Write a block to a compound tag.
     *
     * @param chunkerBlockIdentifier the block identifier.
     * @return the block as NBT with the waterlogged state.
     */
    BedrockBlockCompoundTag writeBlock(ChunkerBlockIdentifier chunkerBlockIdentifier);

    /**
     * Write a block as an identifier (not for items).
     *
     * @param identifier    the chunker block identifier.
     * @param reportMissing whether missing identifiers should be logged.
     * @return the version specific identifier or absent if it failed to convert.
     */
    Optional<Identifier> writeBlockIdentifier(ChunkerBlockIdentifier identifier, boolean reportMissing);

    /**
     * Write a block as an identifier for use with items.
     *
     * @param identifier    the chunker block identifier.
     * @param reportMissing whether missing identifiers should be logged.
     * @return the version specific identifier or absent if it failed to convert.
     */
    Optional<Identifier> writeItemBlockIdentifier(ChunkerBlockIdentifier identifier, boolean reportMissing);

    /**
     * Write an item to a compound tag.
     *
     * @param chunkerItemStack the chunker item stack.
     * @return the item as NBT or absent if it failed to convert.
     */
    Optional<CompoundTag> writeItem(ChunkerItemStack chunkerItemStack);

    /**
     * Write an item as an identifier
     *
     * @param chunkerItemStack the chunker item stack.
     * @return the item identifier or air if it failed to write.
     */
    Identifier writeItemIdentifier(ChunkerItemStack chunkerItemStack);

    /**
     * Get the fallback biome to use for a specific dimension.
     *
     * @param dimension the dimension.
     * @return the biome to use as a fallback if it fails to convert the biome.
     */
    ChunkerBiome getFallbackBiome(Dimension dimension);

    /**
     * Get the data version being used.
     *
     * @return the data version being used to read/write.
     */
    BedrockDataVersion dataVersion();

    /**
     * Get the block entity resolver used to convert between NBT and block entities.
     *
     * @return the block entity resolver.
     */
    BlockEntityResolver<BedrockResolvers, CompoundTag> blockEntityResolver();

    /**
     * Get the entity resolver used to convert between NBT and entities.
     *
     * @return the block entity resolver.
     */
    EntityResolver<BedrockResolvers, CompoundTag> entityResolver();

    /**
     * Get the block identifier resolver used to convert between NBT and identifiers.
     *
     * @return the NBT to identifier resolver.
     */
    Resolver<BedrockBlockCompoundTag, Identifier> nbtBlockIdentifierResolver();

    /**
     * Get the resolver used for turning identifiers into item stacks.
     *
     * @return the resolver mapping Identifier to ChunkerItemStack.
     */
    Resolver<Identifier, ChunkerItemStack> chunkerItemIdentifierResolver();

    /**
     * Read a potion type from an ID.
     *
     * @param potionID the ID.
     * @return the potion type or WATER if not found.
     */
    ChunkerPotionType readPotionTypeID(short potionID);

    /**
     * Write a potion type as an ID.
     *
     * @param potionType the type.
     * @return the ID or water if not found.
     */
    short writePotionTypeID(ChunkerPotionType potionType);

    /**
     * Read an effect from an ID.
     *
     * @param effectID the ID.
     * @return the effect or EMPTY if not found.
     */
    ChunkerEffectType readEffectID(int effectID);

    /**
     * Write an effect as an ID.
     *
     * @param effect the type.
     * @return the ID or empty if not found.
     */
    int writeEffectID(ChunkerEffectType effect);

    /**
     * Read a painting motive from an identifier.
     *
     * @param identifier the identifier.
     * @return the parsed motive.
     */
    PaintingEntity.Motive readPaintingMotive(String identifier);

    /**
     * Write a painting motive as an identifier.
     *
     * @param motive the motive.
     * @return the identifier.
     */
    String writePaintingMotive(PaintingEntity.Motive motive);

    /**
     * Get the resolver which turns entity types into identifiers.
     *
     * @return a resolver from string identifiers to entity type.
     */
    Resolver<String, ChunkerEntityType> entityTypeResolver();

    /**
     * Get the resolver which can turn IDs into enchantment types.
     *
     * @return a resolver from integer IDs to enchantment types.
     */
    Resolver<Integer, ChunkerEnchantmentType> enchantmentIDResolver();

    /**
     * Get the resolver used for armor trim patterns.
     *
     * @return a resolver from string identifiers to armor trim patterns.
     */
    Resolver<String, ChunkerTrimPattern> trimPatternResolver();

    /**
     * Get the resolver used for armor trim materials.
     *
     * @return a resolver from string identifiers to armor trim materials.
     */
    Resolver<String, ChunkerTrimMaterial> trimMaterialResolver();

    /**
     * Get the resolver used for banner patterns.
     *
     * @return a resolver from string short names to banner pattern type.
     */
    Resolver<String, ChunkerBannerPattern> bannerPatternResolver();

    /**
     * Get the pre-transform manager to use after reading / before writing.
     *
     * @return the pre-transform manager, this should vary on if this a reader or writer.
     */
    PreTransformManager preTransformManager();

    /**
     * Get the converter instance.
     *
     * @return the instance of the converter.
     */
    Converter converter();
}
