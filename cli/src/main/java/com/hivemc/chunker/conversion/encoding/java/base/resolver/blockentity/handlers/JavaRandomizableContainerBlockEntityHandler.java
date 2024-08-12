package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.CustomItemNBTBlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.RandomizableContainerBlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.ChunkerItemStack;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Handler for inventories that support loot tables.
 */
public class JavaRandomizableContainerBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, RandomizableContainerBlockEntity> implements CustomItemNBTBlockEntityHandler<JavaResolvers, RandomizableContainerBlockEntity> {
    /**
     * Loot Tables which are on Chunker only.
     */
    public static final Set<String> UNSUPPORTED_LOOT_TABLES = Set.of(
            "minecraft:chests/monster_room",
            "minecraft:chests/village_blacksmith",
            "minecraft:chests/village_two_room_house"
    );
    private final boolean enableLootTables;

    public JavaRandomizableContainerBlockEntityHandler(boolean enabledLootTables) {
        super("RandomizableBlockEntity", RandomizableContainerBlockEntity.class, () -> {
            throw new IllegalArgumentException("Unable to construct RandomizableBlockEntity, invalid type!");
        });
        enableLootTables = enabledLootTables;
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull RandomizableContainerBlockEntity blockEntity) {
        String lootTable = input.getString("LootTable", null);
        if (lootTable != null && !lootTable.isEmpty() && enableLootTables) {
            blockEntity.setLootTable(lootTable);
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull RandomizableContainerBlockEntity blockEntity) {
        if (blockEntity.getLootTable() != null && !blockEntity.getLootTable().isEmpty() && !UNSUPPORTED_LOOT_TABLES.contains(blockEntity.getLootTable()) && enableLootTables) {
            output.put("LootTable", blockEntity.getLootTable());
        }
    }

    @Override
    public boolean generateFromItemNBT(@NotNull JavaResolvers resolvers, @NotNull ChunkerItemStack itemStack, @NotNull RandomizableContainerBlockEntity output, @NotNull CompoundTag input) {
        if (resolvers.dataVersion().getVersion().isLessThan(1, 20, 5)) return false; // Components not needed
        CompoundTag components = input.getCompound("components");
        if (components == null || !enableLootTables) return false;

        // Get the component
        CompoundTag component = components.getCompound("minecraft:container_loot");
        if (component == null) return false;

        // Grab the loot table
        String lootTable = component.getString("loot_table", null);
        if (lootTable != null && !lootTable.isEmpty()) {
            output.setLootTable(lootTable);
        }
        return false; // Read loot table
    }

    @Override
    public boolean writeToItemNBT(@NotNull JavaResolvers resolvers, @NotNull ChunkerItemStack itemStack, @NotNull RandomizableContainerBlockEntity input, @NotNull CompoundTag output) {
        if (resolvers.dataVersion().getVersion().isLessThan(1, 20, 5) || !enableLootTables)
            return true; // Components not needed (write normally)

        if (input.getLootTable() != null && !input.getLootTable().isEmpty() && !UNSUPPORTED_LOOT_TABLES.contains(input.getLootTable())) {
            // Write to the output
            output.getOrCreateCompound("components")
                    .getOrCreateCompound("minecraft:container_loot")
                    .put("loot_table", input.getLootTable());
        }
        return false; // No other output needed
    }
}
