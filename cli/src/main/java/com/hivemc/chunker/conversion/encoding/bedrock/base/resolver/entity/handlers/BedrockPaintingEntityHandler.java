package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.entity.handlers;

import com.hivemc.chunker.conversion.encoding.base.resolver.entity.EntityHandler;
import com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.BedrockResolvers;
import com.hivemc.chunker.conversion.handlers.pretransform.Edge;
import com.hivemc.chunker.conversion.handlers.pretransform.manager.handler.entity.EntityPreTransformHandler;
import com.hivemc.chunker.conversion.intermediate.column.ChunkerColumn;
import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirectionHorizontal;
import com.hivemc.chunker.conversion.intermediate.column.entity.PaintingEntity;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;
import com.hivemc.chunker.nbt.tags.collection.CompoundTag;
import com.hivemc.chunker.util.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Handler that reads paintings.
 */
public class BedrockPaintingEntityHandler extends EntityHandler<BedrockResolvers, CompoundTag, PaintingEntity> {
    public BedrockPaintingEntityHandler() {
        super(ChunkerVanillaEntityType.PAINTING, PaintingEntity.class, PaintingEntity::new);
    }

    /**
     * Get the tile position offset to use.
     *
     * @param entity the painting.
     * @return the offset to use.
     */
    public static Vec3 getChunkerTilePositionOffset(PaintingEntity entity) {
        int width = entity.getMotive().getWidth();
        int height = entity.getMotive().getHeight();
        return new Vec3(
                entity.getDirection() == FacingDirectionHorizontal.SOUTH && width % 2 == 0 ? -1 : 0,
                height % 2 == 0 ? -1 : 0,
                entity.getDirection() == FacingDirectionHorizontal.WEST && width % 2 == 0 ? -1 : 0
        );
    }

    /**
     * Get the position offset to use.
     *
     * @param entity the painting.
     * @return the offset to use.
     */
    public static Vec3 getChunkerPositionOffset(PaintingEntity entity) {
        int width = entity.getMotive().getWidth();
        int height = entity.getMotive().getHeight();
        double yValue = height % 2 == 0 ? 0 : -0.5;
        return switch (entity.getDirection()) {
            case NORTH -> new Vec3(width % 2 == 0 ? 0 : -0.5, yValue, -0.96875);
            case EAST -> new Vec3(-0.03125, yValue, width % 2 == 0 ? 0 : -0.5);
            case SOUTH -> new Vec3(width % 2 == 0 ? 0 : -0.5, yValue, -0.03125);
            case WEST -> new Vec3(-0.96875, yValue, width % 2 == 0 ? 0 : -0.5);
        };
    }

    @Override
    public void read(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag input, @NotNull PaintingEntity value) {
        String motive = input.getString("Motif", null);
        if (motive == null) {
            motive = input.getString("Motive", "Kebab");
        }
        value.setMotive(resolvers.readPaintingMotive(motive));

        // Facing
        value.setDirection(FacingDirectionHorizontal.from2DByte(input.getByte("Direction", (byte) 0)));
    }

    @Override
    public void write(@NotNull BedrockResolvers resolvers, @NotNull CompoundTag output, @NotNull PaintingEntity value) {
        if (resolvers.dataVersion().getVersion().isGreaterThan(1, 20, 60)) {
            output.put("Motif", resolvers.writePaintingMotive(value.getMotive()));
        } else {
            output.put("Motive", resolvers.writePaintingMotive(value.getMotive()));
        }

        // Facing
        output.put("Direction", value.getDirection() == null ? 0 : value.getDirection().to2DByte());
    }

    /**
     * Pre-transform handler which relocates paintings.
     */
    public static class Relocator implements EntityPreTransformHandler<PaintingEntity> {
        private final boolean inverse;

        /**
         * Create a new painting relocator.
         *
         * @param inverse whether the translation is inversed.
         */
        public Relocator(boolean inverse) {
            this.inverse = inverse;
        }

        @Override
        public Class<PaintingEntity> getHandledType() {
            return PaintingEntity.class;
        }

        @Override
        public Set<Edge> getRequiredEdges(ChunkerColumn column, PaintingEntity entity) {
            Vec3 tileOffset = getChunkerTilePositionOffset(entity);
            Vec3 positionOffset = getChunkerPositionOffset(entity);

            int tileX = entity.getTileX();
            int tileZ = entity.getTileZ();
            double positionX;
            double positionZ;
            if (inverse) {
                // Take away the offset
                tileX = (int) (tileX - tileOffset.x());
                tileZ = (int) (tileZ - tileOffset.z());

                // Take away the position offset
                positionX = tileX - positionOffset.x();
                positionZ = tileZ - positionOffset.z();
            } else {
                // Add the position offset
                positionX = entity.getPositionX() + positionOffset.x();
                positionZ = entity.getPositionZ() + positionOffset.z();
            }
            int chunkX = (int) Math.floor(positionX) >> 4;
            int chunkZ = (int) Math.floor(positionZ) >> 4;

            // Calculate which edge is needed
            Edge edge = Edge.fromOffset(column.getPosition().chunkX() - chunkX, column.getPosition().chunkZ() - chunkZ);
            return edge == null ? Collections.emptySet() : EnumSet.of(edge);
        }

        @Override
        public boolean handle(ChunkerColumn column, Map<Edge, ChunkerColumn> neighbours, PaintingEntity entity) {
            Vec3 tileOffset = getChunkerTilePositionOffset(entity);
            Vec3 positionOffset = getChunkerPositionOffset(entity);

            int tileX = entity.getTileX();
            int tileY = entity.getTileY();
            int tileZ = entity.getTileZ();
            double positionX;
            double positionY;
            double positionZ;
            if (inverse) {
                // Take away the offset
                tileX = (int) (tileX - tileOffset.x());
                tileY = (int) (tileY - tileOffset.y());
                tileZ = (int) (tileZ - tileOffset.z());

                // Take away the position offset
                positionX = tileX - positionOffset.x();
                positionY = tileY - positionOffset.y();
                positionZ = tileZ - positionOffset.z();
            } else {
                // Add the position offset
                positionX = entity.getPositionX() + positionOffset.x();
                positionY = entity.getPositionY() + positionOffset.y();
                positionZ = entity.getPositionZ() + positionOffset.z();

                // Add the offset
                tileX = (int) Math.floor(positionX + tileOffset.x());
                tileY = (int) Math.floor(positionY + tileOffset.y());
                tileZ = (int) Math.floor(positionZ + tileOffset.z());
            }

            // Apply the new tile position
            entity.setTileX(tileX);
            entity.setTileY(tileY);
            entity.setTileZ(tileZ);

            // Apply the new position
            entity.setPositionX(positionX);
            entity.setPositionY(positionY);
            entity.setPositionZ(positionZ);

            // Calculate the new column
            int chunkX = (int) Math.floor(positionX) >> 4;
            int chunkZ = (int) Math.floor(positionZ) >> 4;

            // Calculate which edge is needed
            Edge edge = Edge.fromOffset(column.getPosition().chunkX() - chunkX, column.getPosition().chunkZ() - chunkZ);
            if (edge != null) {
                // Apply relocation
                ChunkerColumn newColumn = neighbours.get(edge);
                if (newColumn != null) {
                    // Add to the new column
                    newColumn.getEntities().add(entity);

                    // Remove from this column
                    return true;
                }
            }
            return false;
        }
    }
}
