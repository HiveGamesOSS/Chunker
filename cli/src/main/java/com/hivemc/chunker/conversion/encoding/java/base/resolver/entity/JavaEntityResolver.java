package com.hivemc.chunker.conversion.encoding.java.base.resolver.entity;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EmptyEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.handlers.JavaEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.handlers.JavaHangingEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.handlers.JavaItemFrameEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.handlers.JavaPaintingEntityHandler;
import com.hivemc.chunker.conversion.intermediate.column.entity.GlowItemFrameEntity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.util.Optional;

/**
 * Resolver for converting Java entities between Chunker and NBT.
 */
public class JavaEntityResolver extends EntityResolver<JavaResolvers, CompoundTag> {
    /**
     * Create a new java entity resolver.
     *
     * @param version   the java version.
     * @param resolvers the resolvers to use.
     */
    public JavaEntityResolver(Version version, JavaResolvers resolvers) {
        super(version, resolvers, resolvers.converter().shouldAllowNBTCopying());
    }

    @Override
    protected void registerTypeHandlers(Version version) {
        // Handlers which write/read abstract types which others use
        register(new JavaEntityHandler());
        register(new JavaHangingEntityHandler());

        // Entities
        register(new JavaPaintingEntityHandler());
        register(new JavaItemFrameEntityHandler());
        register(new EmptyEntityHandler<>(ChunkerVanillaEntityType.GLOW_ITEM_FRAME, GlowItemFrameEntity.class, GlowItemFrameEntity::new));
    }

    @Override
    protected CompoundTag constructDataType(ChunkerEntityType type) {
        Optional<String> key = resolvers.entityTypeResolver().from(type);
        if (key.isEmpty()) return null; // Can't write the type

        // Create a new compoundTag with the ID
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("id", key.get());
        return compoundTag;
    }

    @Override
    public Optional<ChunkerEntityType> getKey(CompoundTag input) {
        return input.getOptionalValue("id", String.class).flatMap(resolvers.entityTypeResolver()::to);
    }
}
