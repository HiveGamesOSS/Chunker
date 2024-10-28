package com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy;

import com.hivemc.chunker.conversion.encoding.base.Version;
import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityResolver;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy.handlers.JavaLegacyEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy.handlers.JavaLegacyHangingEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy.handlers.JavaLegacyItemFrameEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.legacy.handlers.JavaLegacyPaintingEntityHandler;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;

import java.util.Optional;

/**
 * Resolver for converting legacy Java entities between Chunker and NBT.
 */
public class JavaLegacyEntityResolver extends EntityResolver<JavaResolvers, CompoundTag> {
    /**
     * Create a new legacy java entity resolver.
     *
     * @param version   the java version.
     * @param resolvers the resolvers to use.
     */
    public JavaLegacyEntityResolver(Version version, JavaResolvers resolvers) {
        super(version, resolvers, resolvers.converter().shouldAllowNBTCopying());
    }

    @Override
    protected void registerTypeHandlers(Version version) {
        // Handlers which write/read abstract types which others use
        register(new JavaLegacyEntityHandler());
        register(new JavaLegacyHangingEntityHandler());

        // Entities
        register(new JavaLegacyPaintingEntityHandler());
        register(new JavaLegacyItemFrameEntityHandler());
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
