package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerEffectType;

import java.util.Objects;

/**
 * Represents a Beacon Block Entity.
 */
public class BeaconBlockEntity extends BlockEntity {
    private ChunkerEffectType primaryEffect = ChunkerEffectType.EMPTY;
    private ChunkerEffectType secondaryEffect = ChunkerEffectType.EMPTY;

    /**
     * The primary effect of the beacon.
     *
     * @return the primary effect, EMPTY if not present.
     */
    public ChunkerEffectType getPrimaryEffect() {
        return primaryEffect;
    }

    /**
     * Set the primary effect of the beacon.
     *
     * @param primaryEffect the primary effect, EMPTY if not present.
     */
    public void setPrimaryEffect(ChunkerEffectType primaryEffect) {
        this.primaryEffect = primaryEffect;
    }

    /**
     * The secondary effect of the beacon.
     *
     * @return the secondary effect, EMPTY if not present.
     */
    public ChunkerEffectType getSecondaryEffect() {
        return secondaryEffect;
    }

    /**
     * Set the secondary effect of the beacon.
     *
     * @param secondaryEffect the secondary effect, EMPTY if not present.
     */
    public void setSecondaryEffect(ChunkerEffectType secondaryEffect) {
        this.secondaryEffect = secondaryEffect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeaconBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return getPrimaryEffect() == that.getPrimaryEffect() && getSecondaryEffect() == that.getSecondaryEffect();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPrimaryEffect(), getSecondaryEffect());
    }
}
