package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.EnchantmentTableBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.JsonTextUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Enchantment Table Block Entities.
 */
public class JavaLegacyEnchantmentBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, EnchantmentTableBlockEntity> {
    public JavaLegacyEnchantmentBlockEntityHandler() {
        super("EnchantTable", EnchantmentTableBlockEntity.class, EnchantmentTableBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull EnchantmentTableBlockEntity value) {
        value.setCustomName(input.getOptionalValue("CustomName", String.class)
                .map(JsonTextUtil::fromJSON)
                .orElse(null));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull EnchantmentTableBlockEntity value) {
        if (value.getCustomName() != null) {
            output.put("CustomName", JsonTextUtil.toJSON(value.getCustomName()));
        }
    }
}
