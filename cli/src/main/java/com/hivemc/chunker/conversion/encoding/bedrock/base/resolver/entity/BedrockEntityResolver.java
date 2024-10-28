package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EmptyEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityResolver;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity.handlers.BedrockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity.handlers.BedrockHangingEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity.handlers.BedrockItemFrameEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity.handlers.BedrockPaintingEntityHandler;
import com.hivemc.chunker.conversion.intermediate.column.entity.GlowItemFrameEntity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.util.Optional;

/**
 * Resolver for converting Bedrock entities between Chunker and NBT.
 */
public class BedrockEntityResolver extends EntityResolver<BedrockResolvers, CompoundTag> {
    /**
     * Create a new bedrock entity resolver.
     *
     * @param version   the bedrock version.
     * @param resolvers the resolvers to use.
     */
    public BedrockEntityResolver(Version version, BedrockResolvers resolvers) {
        super(version, resolvers, resolvers.converter().shouldAllowNBTCopying());
    }

    @Override
    protected void registerTypeHandlers(Version version) {
        // Handlers which write/read abstract types which others use
        register(new BedrockEntityHandler());
        register(new BedrockHangingEntityHandler());

        // Supported Entities (currently only paintings and item frames)
        register(new BedrockPaintingEntityHandler());
        register(new BedrockItemFrameEntityHandler());
        register(new EmptyEntityHandler<>(ChunkerVanillaEntityType.GLOW_ITEM_FRAME, GlowItemFrameEntity.class, () -> {
            throw new IllegalArgumentException("Glow item frames cannot be read from Bedrock.");
        }));
    }

    @Override
    protected CompoundTag constructDataType(ChunkerEntityType type) {
        Optional<String> key = resolvers.entityTypeResolver().from(type);
        if (key.isEmpty()) return null; // Can't write the type

        // Create a new compoundTag with the ID
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("identifier", key.get());
        return compoundTag;
    }

    @Override
    public Optional<ChunkerEntityType> getKey(CompoundTag input) {
        return input.getOptionalValue("identifier", String.class).flatMap(resolvers.entityTypeResolver()::to);
    }
}
