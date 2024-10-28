package com.hivemc.chunker.conversion.intermediate.column.entity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.identifier.type.block.states.vanilla.types.FacingDirectionHorizontal;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerEntityType;
import com.hivemc.chunker.conversion.intermediate.column.entity.type.ChunkerVanillaEntityType;

import java.util.Objects;

/**
 * Represents a Painting Entity.
 */
public class PaintingEntity extends HangingEntity {
    private Motive motive;
    private FacingDirectionHorizontal direction = FacingDirectionHorizontal.SOUTH;

    /**
     * Get the motive used for the painting.
     *
     * @return the motive used for the painting.
     */
    public Motive getMotive() {
        return motive;
    }

    /**
     * Set the motive used for the painting.
     *
     * @param motive the motive used for the painting.
     */
    public void setMotive(Motive motive) {
        this.motive = motive;
    }

    /**
     * Get the direction of the painting.
     *
     * @return the direction the painting is hanging.
     */
    public FacingDirectionHorizontal getDirection() {
        return direction;
    }

    /**
     * Set the direction of the painting.
     *
     * @param direction the direction the painting is hanging.
     */
    public void setDirection(FacingDirectionHorizontal direction) {
        this.direction = direction;
    }

    @Override
    public ChunkerEntityType getEntityType() {
        return ChunkerVanillaEntityType.PAINTING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaintingEntity that)) return false;
        if (!super.equals(o)) return false;
        return getMotive() == that.getMotive() && getDirection() == that.getDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMotive(), getDirection());
    }

    @Override
    public String toString() {
        return "PaintingEntity{" +
                "motive=" + getMotive() +
                ", direction=" + getDirection() +
                ", tileX=" + getTileX() +
                ", tileY=" + getTileY() +
                ", tileZ=" + getTileZ() +
                ", positionX=" + getPositionX() +
                ", positionY=" + getPositionY() +
                ", positionZ=" + getPositionZ() +
                ", motionX=" + getMotionX() +
                ", motionY=" + getMotionY() +
                ", motionZ=" + getMotionZ() +
                ", yaw=" + getYaw() +
                ", pitch=" + getPitch() +
                '}';
    }

    /**
     * The design used for the painting.
     */
    public enum Motive {
        KEBAB(1, 1),
        AZTEC(1, 1),
        ALBAN(1, 1),
        AZTEC2(1, 1),
        BOMB(1, 1),
        PLANT(1, 1),
        WASTELAND(1, 1),
        MEDITATIVE(1, 1),
        WANDERER(1, 2),
        GRAHAM(1, 2),
        PRAIRIE_RIDE(1, 2),
        SEA(2, 1),
        CREEBET(2, 1),
        SUNSET(2, 1),
        COURBET(2, 1),
        POOL(2, 1),
        EARTH(2, 2),
        FIRE(2, 2),
        WATER(2, 2),
        WIND(2, 2),
        MATCH(2, 2),
        BUST(2, 2),
        STAGE(2, 2),
        VOID(2, 2),
        SKULL_AND_ROSES(2, 2),
        WITHER(2, 2),
        BAROQUE(2, 2),
        HUMBLE(2, 2),
        BOUQUET(3, 3),
        CAVEBIRD(3, 3),
        COTAN(3, 3),
        ENDBOSS(3, 3),
        FERN(3, 3),
        OWLEMONS(3, 3),
        SUNFLOWERS(3, 3),
        TIDES(3, 3),
        BACKYARD(3, 4),
        POND(3, 4),
        FIGHTERS(4, 2),
        CHANGING(4, 2),
        FINDING(4, 2),
        LOWMIST(4, 2),
        PASSAGE(4, 2),
        SKELETON(4, 3),
        DONKEY_KONG(4, 3),
        POINTER(4, 4),
        PIGSCENE(4, 4),
        BURNING_SKULL(4, 4),
        ORB(4, 4),
        UNPACKED(4, 4);

        private final int width;
        private final int height;

        /**
         * Create a new motive.
         *
         * @param width  the width in blocks of the motive.
         * @param height the height in blocks of the motive.
         */
        Motive(int width, int height) {
            this.width = width;
            this.height = height;
        }

        /**
         * Get the width of the design.
         *
         * @return the width in blocks.
         */
        public int getWidth() {
            return width;
        }

        /**
         * Get the height of the design.
         *
         * @return the height in blocks.
         */
        public int getHeight() {
            return height;
        }
    }
}
