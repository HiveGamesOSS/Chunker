package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.FurnaceBlockEntity;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.primitive.IntTag;
import com.hivemc.chunker.nbt.tags.primitive.ShortTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for Furnace Block Entities.
 */
public class JavaLegacyFurnaceBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, FurnaceBlockEntity> {
    public JavaLegacyFurnaceBlockEntityHandler() {
        super("Furnace", FurnaceBlockEntity.class, FurnaceBlockEntity::new);
    }

    /**
     * Get a key from a NBT compound tag which can be either a short or an integer as a short.
     * Note: This is used because some tools seem to output integer for these tags when it should be a short.
     *
     * @param tag          the compound tag to look inside.
     * @param key          the value to lookup.
     * @param defaultValue the default value to return if the key is not present.
     * @return the value as a short, it will throw an exception if the tag is not a short tag or int tag.
     */
    public static short getShortOrInt(@NotNull CompoundTag tag, @NotNull String key, short defaultValue) {
        Tag<?> value = tag.get(key);
        if (value instanceof ShortTag shortTag) {
            return shortTag.getValue();
        } else if (value instanceof IntTag intTag) {
            return (short) intTag.getValue();
        } else if (value == null) {
            return defaultValue;
        }

        throw new IllegalArgumentException(value.getClass().getName() + " is not of type ShortTag / IntTag");
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull FurnaceBlockEntity value) {
        value.setBurnTime(getShortOrInt(input, "BurnTime", (short) 0));
        value.setCookTime(getShortOrInt(input, "CookTime", (short) 0));
        value.setCookTimeTotal(getShortOrInt(input, "CookTimeTotal", (short) 0));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull FurnaceBlockEntity value) {
        output.put("BurnTime", value.getBurnTime());
        output.put("CookTime", value.getCookTime());
        output.put("CookTimeTotal", value.getCookTimeTotal());
    }
}
