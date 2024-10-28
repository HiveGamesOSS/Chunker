package com.hivemc.chunker.conversion.intermediate.column.entity;

import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;

/**
 * Represents a glowing item frame entity.
 */
public class GlowItemFrameEntity extends ItemFrameEntity {
    @Override
    public ChunkerEntityType getEntityType() {
        return ChunkerVanillaEntityType.GLOW_ITEM_FRAME;
    }
}
