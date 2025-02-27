package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.CustomItemNBTBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.DecoratedPotBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerItemStackIdentifier;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.mapping.identifier.Identifier;
import com.hivemc.chunker.nbt.TagType;
import com.hivemc.chunker.nbt.tags.Tag;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.nbt.tags.collection.ListTag;
import com.hivemc.chunker.nbt.tags.primitive.StringTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handler for Decorated Pot Block Entities which also handles item components.
 */
public class JavaDecoratedPotBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, DecoratedPotBlockEntity> implements CustomItemNBTBlockEntityHandler<JavaResolvers, DecoratedPotBlockEntity> {
    public JavaDecoratedPotBlockEntityHandler() {
        super("minecraft:decorated_pot", DecoratedPotBlockEntity.class, DecoratedPotBlockEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull DecoratedPotBlockEntity value) {
        String tagName = input.contains("sherds") ? "sherds" : "shards";
        List<String> sherds = input.getListValues(tagName, StringTag.class, null);
        if (sherds != null && !sherds.isEmpty()) {
            List<ChunkerItemStackIdentifier> items = new ArrayList<>(sherds.size());
            for (String sherd : sherds) {
                items.add(resolvers.readItemIdentifier(new Identifier(sherd)).getIdentifier());
            }

            // We'll set the sherds that are present
            value.setBack(items.get(0));
            if (sherds.size() == 1) return;
            value.setLeft(items.get(1));

            if (sherds.size() == 2) return;
            value.setRight(items.get(2));

            if (sherds.size() == 3) return;
            value.setFront(items.get(3));
        }

        // Read item
        CompoundTag item = input.getCompound("item");
        if (item != null) {
            value.setItem(resolvers.readItem(item));
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull DecoratedPotBlockEntity value) {
        ListTag<StringTag, String> sherds = new ListTag<>(TagType.STRING);
        sherds.add(new StringTag(resolvers.writeItemIdentifier(new ChunkerItemStack(value.getBack())).getIdentifier()));
        sherds.add(new StringTag(resolvers.writeItemIdentifier(new ChunkerItemStack(value.getLeft())).getIdentifier()));
        sherds.add(new StringTag(resolvers.writeItemIdentifier(new ChunkerItemStack(value.getRight())).getIdentifier()));
        sherds.add(new StringTag(resolvers.writeItemIdentifier(new ChunkerItemStack(value.getFront())).getIdentifier()));

        output.put(resolvers.dataVersion().getVersion().isLessThan(1, 20, 0) ? "shards" : "sherds", sherds);

        // Write the item stored in the pot (1.20.30 and above)
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 20, 3)) {
            if (value.getItem() != null && !value.getItem().getIdentifier().isAir()) {
                resolvers.writeItem(value.getItem()).ifPresent(item -> output.put("item", item));
            }
        }
    }

    @Override
    public boolean generateFromItemNBT(@NotNull JavaResolvers resolvers, @NotNull ChunkerItemStack itemStack, @NotNull DecoratedPotBlockEntity output, @NotNull CompoundTag input) {
        if (resolvers.dataVersion().getVersion().isLessThan(1, 20, 5)) return false; // Components not needed
        CompoundTag components = input.getCompound("components");
        if (components == null) return false;

        // Grab the pot decorations
        List<String> sherds = components.getListValues("minecraft:pot_decorations", StringTag.class, null);
        if (sherds != null && !sherds.isEmpty()) {
            List<ChunkerItemStackIdentifier> items = new ArrayList<>(sherds.size());
            for (String sherd : sherds) {
                items.add(resolvers.readItemIdentifier(new Identifier(sherd)).getIdentifier());
            }

            // We'll set the sherds that are present
            output.setBack(items.get(0));
            if (sherds.size() == 1) return true; // Success
            output.setLeft(items.get(1));

            if (sherds.size() == 2) return true; // Success
            output.setRight(items.get(2));

            if (sherds.size() == 3) return true; // Success
            output.setFront(items.get(3));
        }

        // Read the item from the container component
        ListTag<CompoundTag, Map<String, Tag<?>>> containerTag = components.getList("minecraft:container", CompoundTag.class, null);
        if (containerTag != null) {
            // Find the first valid item
            for (CompoundTag itemTag : containerTag) {
                // Read item
                itemTag = itemTag.getCompound("item");
                if (itemTag == null) continue;

                // Read the tag
                ChunkerItemStack item = resolvers.readItem(itemTag);
                if (item.getIdentifier().isAir()) continue;

                // Set the item if it's not air
                output.setItem(item);
            }
        }
        return true; // Success
    }

    @Override
    public boolean writeToItemNBT(@NotNull JavaResolvers resolvers, @NotNull ChunkerItemStack itemStack, @NotNull DecoratedPotBlockEntity input, @NotNull CompoundTag output) {
        if (resolvers.dataVersion().getVersion().isLessThan(1, 20, 5))
            return true; // Components not needed (write normally)

        CompoundTag components = output.getOrCreateCompound("components");

        // Write the pot decorations
        ListTag<StringTag, String> sherds = new ListTag<>(TagType.STRING);
        sherds.add(new StringTag(resolvers.writeItemIdentifier(new ChunkerItemStack(input.getBack())).getIdentifier()));
        sherds.add(new StringTag(resolvers.writeItemIdentifier(new ChunkerItemStack(input.getLeft())).getIdentifier()));
        sherds.add(new StringTag(resolvers.writeItemIdentifier(new ChunkerItemStack(input.getRight())).getIdentifier()));
        sherds.add(new StringTag(resolvers.writeItemIdentifier(new ChunkerItemStack(input.getFront())).getIdentifier()));

        // Write to the output
        components.put("minecraft:pot_decorations", sherds);

        // Write item if present (with the container component)
        if (input.getItem() != null && !input.getItem().getIdentifier().isAir()) {
            ListTag<CompoundTag, Map<String, Tag<?>>> items = new ListTag<>(TagType.COMPOUND, new ArrayList<>(1));

            // Write the item with slot
            Optional<CompoundTag> item = resolvers.writeItem(input.getItem());
            if (item.isPresent()) {
                // Add the slot
                CompoundTag itemTag = new CompoundTag();
                itemTag.put("slot", 0);
                itemTag.put("item", item.get());

                // Add to items
                items.add(itemTag);
                components.put("minecraft:container", items);
            }
        }

        return false; // Block entity not needed
    }
}
