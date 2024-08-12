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

            // Remove bedrock formatting
            lootTable = lootTable.replace("loot_tables/", "minecraft:").replace(".json", "");

            // Fix any renames
            lootTable = LOOT_TABLE_OLD_NAME_TO_CHUNKER.getOrDefault(lootTable, lootTable);
            blockEntity.setLootTable(lootTable);
        }
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull RandomizableContainerBlockEntity blockEntity) {
        if (blockEntity.getLootTable() != null && !blockEntity.getLootTable().isEmpty() && !UNSUPPORTED_LOOT_TABLES.contains(blockEntity.getLootTable()) && enableLootTables) {
            String bedrockLootTable = blockEntity.getLootTable();

            // Fix any renames
            bedrockLootTable = LOOT_TABLE_OLD_NAME_TO_CHUNKER.inverse().getOrDefault(bedrockLootTable, bedrockLootTable);

            // Convert to path
            if (bedrockLootTable.startsWith("minecraft:")) {
                bedrockLootTable = "loot_tables/" + bedrockLootTable.substring(10);
            }

            // Ensure it ends in .json
            if (!bedrockLootTable.endsWith(".json")) {
                bedrockLootTable = bedrockLootTable + ".json";
            }

            output.put("LootTable", bedrockLootTable);
        }
    }
}
