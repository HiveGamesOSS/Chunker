package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.ChunkerBlockIdentifier;
import org.jetbrains.annotations.Nullable;

/**
 * Bedrock Flower Pot Block Entity which holds the plant.
 */
public class BedrockFlowerPotBlockEntity extends BlockEntity {
    @Nullable
    private ChunkerBlockIdentifier plant;

    /**
     * Get the plant block identifier
     *
     * @return the identifier (can be air) or null if not present.
     */
    @Nullable
    public ChunkerBlockIdentifier getPlant() {
        return plant;
    }

    /**
     * Set the plant block identifier
     *
     * @param plant the identifier (can be air) or null if not present.
     */
    public void setPlant(@Nullable ChunkerBlockIdentifier plant) {
        this.plant = plant;
    }
}
