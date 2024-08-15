package com.hivemc.chunker.conversion.encoding.java.base.resolver.entity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityHandler;
import com.hivemc.chunker.conversion.encoding.java.base.resolver.JavaResolvers;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirectionHorizontal;
import com.hivemc.chunker.conversion.intermediate.column.entity.PaintingEntity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for writing/reading Paintings.
 */
public class JavaPaintingEntityHandler extends EntityHandler<JavaResolvers, CompoundTag, PaintingEntity> {
    public JavaPaintingEntityHandler() {
        super(ChunkerVanillaEntityType.PAINTING, PaintingEntity.class, PaintingEntity::new);
    }

    @Override
    public void read(@NotNull JavaResolvers resolvers, @NotNull CompoundTag input, @NotNull PaintingEntity value) {
        String motive = input.getString("variant", null);
        if (motive == null) {
            motive = input.getString("Motive", "minecraft:kebab");
        }
        value.setMotive(resolvers.readPaintingMotive(motive));

        // Facing
        String name = input.contains("facing") ? "facing" : "Facing";
        value.setDirection(FacingDirectionHorizontal.from2DByte(input.getByte(name, (byte) 0)));
    }

    @Override
    public void write(@NotNull JavaResolvers resolvers, @NotNull CompoundTag output, @NotNull PaintingEntity value) {
        if (resolvers.dataVersion().getVersion().isGreaterThanOrEqual(1, 19, 0)) {
            output.put("variant", resolvers.writePaintingMotive(value.getMotive()));
            output.put("facing", value.getDirection() == null ? 0 : value.getDirection().to2DByte());
        } else {
            output.put("Motive", resolvers.writePaintingMotive(value.getMotive()));
            output.put("Facing", value.getDirection() == null ? 0 : value.getDirection().to2DByte());
        }
    }
}
