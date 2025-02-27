package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.CustomItemNBTBlockEntityHandler;
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

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * Handler for base inventories (converting items).
 */
public class JavaContainerBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, ContainerBlockEntity> implements CustomItemNBTBlockEntityHandler<JavaResolvers, ContainerBlockEntity> {
    public JavaContainerBlockEntityHandler() {
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

        value.setCustomName(input.getOptional("CustomName", Tag.class)
                .map(JsonTextUtil::fromNBT)
                .orElse(null));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull ContainerBlockEntity value) {
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, new ArrayList<>(value.getItems().size()));
        for (Byte2ObjectMap.Entry<ChunkerItemStack> tag : value.getItems().byte2ObjectEntrySet()) {
            // Don't write air to inventories
            if (tag.getValue().getIdentifier().isAir()) continue;

            // Write the item with slot
            Optional<CompoundTag> item = resolvers.writeItem(tag.getValue());
            if (item.isEmpty()) continue;

            // Write the slot
            item.get().put("Slot", tag.getByteKey());

            // Add to items
            items.add(item.get());
        }
        output.put("Items", items);

        if (value.getCustomName() != null) {
            output.put("CustomName", JsonTextUtil.toNBT(value.getCustomName(), resolvers.dataVersion()));
        }
    }

    @Override
    public boolean generateFromItemNBT(@NotNull JavaResolvers resolvers, @NotNull ChunkerItemStack itemStack, @NotNull ContainerBlockEntity output, @NotNull CompoundTag input) {
        if (resolvers.dataVersion().getVersion().isLessThan(1, 20, 5)) return false; // Components not needed
        CompoundTag components = input.getCompound("components");
        if (components == null) return false;

        // Get the container component
        ListTag<CompoundTag, Map<String, Tag<?>>> items = components.getList("minecraft:container", CompoundTag.class, null);
        if (items != null) {
            byte index = 0;
            for (CompoundTag itemTag : items) {
                byte slot = (byte) itemTag.getInt("slot", index);

                // Read item
                itemTag = itemTag.getCompound("item");
                if (itemTag == null) continue;

                // Read the tag
                ChunkerItemStack item = resolvers.readItem(itemTag);
                output.getItems().put(slot, item);

                // Increment index
                index++;
            }
        }

        // Get the name component
        output.setCustomName(components.getOptional("minecraft:custom_name", Tag.class)
                .map(JsonTextUtil::fromNBT)
                .orElse(null));

        return true; // Don't read other data
    }

    @Override
    public boolean writeToItemNBT(@NotNull JavaResolvers resolvers, @NotNull ChunkerItemStack itemStack, @NotNull ContainerBlockEntity input, @NotNull CompoundTag output) {
        if (resolvers.dataVersion().getVersion().isLessThan(1, 20, 5))
            return true; // Components not needed (write normally)
        CompoundTag components = output.getOrCreateCompound("components");

        // Write items
        ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, new ArrayList<>(input.getItems().size()));
        for (Byte2ObjectMap.Entry<ChunkerItemStack> tag : input.getItems().byte2ObjectEntrySet()) {
            // Don't write air to inventories
            if (tag.getValue().getIdentifier().isAir()) continue;

            // Write the item with slot
            Optional<CompoundTag> item = resolvers.writeItem(tag.getValue());
            if (item.isEmpty()) continue;

            // Add the slot
            CompoundTag itemTag = new CompoundTag();
            itemTag.put("slot", (int) tag.getByteKey());
            itemTag.put("item", item.get());

            // Add to items
            items.add(itemTag);
        }
        components.put("minecraft:container", items);

        // Write name if present
        if (input.getCustomName() != null) {
            components.put("minecraft:custom_name", JsonTextUtil.toNBT(input.getCustomName(), resolvers.dataVersion()));
        }

        return false; // No other output needed
    }
}
