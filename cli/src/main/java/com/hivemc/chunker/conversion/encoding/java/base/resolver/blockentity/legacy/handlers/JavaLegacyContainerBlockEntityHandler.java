package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.ContainerBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.util.JsonTextUtil;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Handler for base inventories (converting items).
 */
public class JavaLegacyContainerBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, ContainerBlockEntity> {
    public JavaLegacyContainerBlockEntityHandler() {
        super("minecraft:container", ContainerBlockEntity.class, () -> {
            throw new IllegalArgumentException("Unable to initialize type of container");
        });
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull ContainerBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = input.getList("Items", CompoundTag.class, null);
        if (items != null) {
            byte index = 0;
            for (CompoundTag itemTag : items) {
                byte slot = itemTag.getByte("Slot", index);

                // Read item
                ChunkerItemStack item = resolvers.readItem(itemTag);
                value.getItems().put(slot, item);

                // Increment index
                index++;
            }
        }

        value.setCustomName(input.getOptionalValue("CustomName", String.class)
                .map(JsonTextUtil::fromJSON)
                .orElse(null));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull ContainerBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, value.getItems().size());
        for (Byte2ObjectMap.Entry<ChunkerItemStack> tag : value.getItems().byte2ObjectEntrySet()) {
            // Don't write air to inventories
            if (tag.getValue().getIdentifier().isAir()) continue;

            // Write the item with slot
            Optional<CompoundTag> item = resolvers.writeItem(tag.getValue());
            if (item.isEmpty()) continue;

            // Add the slot
            item.get().put("Slot", tag.getByteKey());

            // Add to items
            items.add(item.get());
        }
        output.put("Items", items);

        if (value.getCustomName() != null) {
            output.put("CustomName", JsonTextUtil.toJSON(value.getCustomName()));
        }
    }
}
