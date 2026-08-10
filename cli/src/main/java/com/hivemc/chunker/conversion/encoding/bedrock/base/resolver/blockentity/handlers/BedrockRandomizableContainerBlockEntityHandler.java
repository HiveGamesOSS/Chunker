package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.hivemc.chunker.conversion.encoding.base.resolver.blockentity.BlockEntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.container.randomizable.RandomizableContainerBlockEntity;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Handler for inventories that support loot tables.
 */
public class BedrockRandomizableContainerBlockEntityHandler extends BlockEntityHandler<BedrockResolvers, CompoundTag, RandomizableContainerBlockEntity> {
    /**
     * Fix bedrock names which differ to chunker.
     */
    public static final BiMap<String, String> LOOT_TABLE_OLD_NAME_TO_CHUNKER = ImmutableBiMap.<String, String>builder()
            .put("minecraft:chests/buriedtreasure", "minecraft:chests/buried_treasure")
            .put("minecraft:chests/dispenser_trap", "minecraft:chests/jungle_temple_dispenser")
            .put("minecraft:chests/shipwreck", "minecraft:chests/shipwreck_map")
            .put("minecraft:chests/shipwrecksupply", "minecraft:chests/shipwreck_supply")
            .put("minecraft:chests/shipwrecktreasure", "minecraft:chests/shipwreck_treasure")
            .build();
    /**
     * Loot Tables which are on Chunker only.
     */
    public static final Set<String> UNSUPPORTED_LOOT_TABLES = Set.of(
            "minecraft:chests/village/village_fisher"
    );
    private final boolean enableLootTables;

    public BedrockRandomizableContainerBlockEntityHandler(boolean enabledLootTables) {
        super("RandomizableBlockEntity", RandomizableContainerBlockEntity.class, () -> {
            throw new IllegalArgumentException("Unable to construct RandomizableBlockEntity, invalid type!");
        });
        enableLootTables = enabledLootTables;
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull RandomizableContainerBlockEntity blockEntity) {
        String lootTable = input.getString("LootTable", null);
        if (lootTable != null && !lootTable.isEmpty() && enableLootTables) {
            blockEntity.setLootTable(bedrockLootTableToChunker(lootTable));
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull RandomizableContainerBlockEntity blockEntity) {
        if (blockEntity.getLootTable() != null && !blockEntity.getLootTable().isEmpty() && !UNSUPPORTED_LOOT_TABLES.contains(blockEntity.getLootTable()) && enableLootTables) {
            output.put("LootTable", chunkerLootTableToBedrock(blockEntity.getLootTable()));
        }
    }

    /**
     * Convert a bedrock loot table path to the chunker loot table (java).
     *
     * @param lootTable the bedrock loot table path.
     * @return the chunker loot table (java format).
     */
    public static String bedrockLootTableToChunker(String lootTable) {
        // Remove bedrock formatting
        lootTable = lootTable.replace("loot_tables/", "minecraft:").replace(".json", "");

        // Fix any renames
        return LOOT_TABLE_OLD_NAME_TO_CHUNKER.getOrDefault(lootTable, lootTable);
    }

    /**
     * Convert a chunker loot table (java) to the bedrock loot table path.
     *
     * @param lootTable the chunker loot table (java format).
     * @return the bedrock loot table path.
     */
    public static String chunkerLootTableToBedrock(String lootTable) {
        // Fix any renames
        lootTable = LOOT_TABLE_OLD_NAME_TO_CHUNKER.inverse().getOrDefault(lootTable, lootTable);

        // Convert to path
        if (lootTable.startsWith("minecraft:")) {
            lootTable = "loot_tables/" + lootTable.substring(10);
        } else if (!lootTable.startsWith("loot_tables/")) {
            lootTable = "loot_tables/" + lootTable;
        }

        // Ensure it ends in .json
        if (!lootTable.endsWith(".json")) {
            lootTable = lootTable + ".json";
        }

        return lootTable;
    }
}
