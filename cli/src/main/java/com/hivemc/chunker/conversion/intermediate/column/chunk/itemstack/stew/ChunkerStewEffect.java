package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.stew;

import com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.potion.ChunkerEffectType;

import java.util.Map;
import java.util.Objects;

/**
 * Contains a map of different effects which are applied when a stew is consumed and how long for.
 */
public class ChunkerStewEffect {
    private final Map<ChunkerEffectType, Integer> effects;

    /**
     * Create a new set of stew effects.
     *
     * @param effects the effects with effect type and the number of ticks they are applied for.
     */
    public ChunkerStewEffect(Map<ChunkerEffectType, Integer> effects) {
        this.effects = effects;
    }

    @Override
    public String toString() {
        return "ChunkerStewEffect{" +
                "effects=" + effects +
                '}';
    }

    /**
     * Get the effects for when the stew is consumed.
     *
     * @return a map of effect to the time to apply the effect for in ticks.
     */
    public Map<ChunkerEffectType, Integer> getEffects() {
        return effects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkerStewEffect that)) return false;
        return Objects.equals(effects, that.effects);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(effects);
    }
}
