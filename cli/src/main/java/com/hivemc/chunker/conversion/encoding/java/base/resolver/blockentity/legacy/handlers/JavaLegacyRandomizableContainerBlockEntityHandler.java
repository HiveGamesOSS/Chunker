package com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.legacy.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.blockentity.handlers.JavaRandomizableContainerBlockEntityHandler;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.RandomizableContainerBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Handler for inventories that support loot tables.
 */
public class JavaLegacyRandomizableContainerBlockEntityHandler extends BlockEntityHandler<JavaResolvers, CompoundTag, RandomizableContainerBlockEntity> {
    /**
     * Loot Tables which are on Chunker only.
     */
    public static final Set<String> UNSUPPORTED_LOOT_TABLES = Set.of(
            "minecraft:chests/monster_room",
            "minecraft:chests/village_two_room_house"
    );

    private final boolean enableLootTables;

    public JavaLegacyRandomizableContainerBlockEntityHandler(boolean enabledLootTables) {
        super("RandomizableBlockEntity", RandomizableContainerBlockEntity.class, () -> {
            throw new IllegalArgumentException("Unable to construct RandomizableBlockEntity, invalid type!");
        });
        enableLootTables = enabledLootTables;
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull RandomizableContainerBlockEntity blockEntity) {
        String lootTable = input.getString("LootTable", null);
        if (lootTable != null && !lootTable.isEmpty() && enableLootTables) {
            blockEntity.setLootTable(applyVersionSpecificLootTableFixes(resolvers, lootTable, false));
        }
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull RandomizableContainerBlockEntity blockEntity) {
        if (blockEntity.getLootTable() != null && !blockEntity.getLootTable().isEmpty() && !UNSUPPORTED_LOOT_TABLES.contains(blockEntity.getLootTable()) && enableLootTables) {
            output.put("LootTable", applyVersionSpecificLootTableFixes(resolvers, blockEntity.getLootTable(), true));
        }
    }

    /**
     * Apply any version specific java fixes to loot tables.
     *
     * @param resolvers the resolvers being used.
     * @param lootTable the input loot table.
     * @param writing   whether it is being read or written (true if written).
     * @return the output loot table.
     */
    public String applyVersionSpecificLootTableFixes(@NotNull JavaResolvers resolvers, String lootTable, boolean writing) {
        if (!writing) return lootTable; // Currently fixes are only applied on writing to preserve data
        return JavaRandomizableContainerBlockEntityHandler.PRE_1_14_LOOT_TABLES.getOrDefault(lootTable, lootTable);
    }
}
