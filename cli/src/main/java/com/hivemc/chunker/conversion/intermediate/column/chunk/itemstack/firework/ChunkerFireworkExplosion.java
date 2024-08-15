package com.hivemc.chunker.conversion.intermediate.column.chunk.itemstack.firework;

import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * A single firework explosion.
 */
public class ChunkerFireworkExplosion {
    private ChunkerFireworkShape shape;
    private List<Color> colors;
    private List<Color> fadeColors;
    private boolean trail;
    private boolean twinkle;

    /**
     * Create a firework explosion.
     *
     * @param shape      the shape to explode to.
     * @param colors     the colors to use.
     * @param fadeColors the colors the firework uses to fade to.
     * @param trail      whether the firework has a trail.
     * @param twinkle    whether the firework sparkles.
     */
    public ChunkerFireworkExplosion(ChunkerFireworkShape shape, List<Color> colors, List<Color> fadeColors, boolean trail, boolean twinkle) {
        this.shape = shape;
        this.colors = colors;
        this.fadeColors = fadeColors;
        this.trail = trail;
        this.twinkle = twinkle;
    }

    /**
     * Get the shape of the explosion.
     *
     * @return the shape.
     */
    public ChunkerFireworkShape getShape() {
        return shape;
    }

    /**
     * Set the shape of the explosion.
     *
     * @param shape the shape.
     */
    public void setShape(ChunkerFireworkShape shape) {
        this.shape = shape;
    }

    /**
     * Get the colors used in the explosion.
     *
     * @return a list of the colors.
     */
    public List<Color> getColors() {
        return colors;
    }

    /**
     * Set the colors used in the explosion.
     *
     * @param colors a list of the colors.
     */
    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    /**
     * Get the colors which should be faded to in the explosion.
     *
     * @return a list of the colors.
     */
    public List<Color> getFadeColors() {
        return fadeColors;
    }

    /**
     * Set the colors which should be faded to in the explosion.
     *
     * @param fadeColors a list of the colors.
     */
    public void setFadeColors(List<Color> fadeColors) {
        this.fadeColors = fadeColors;
    }

    /**
     * Whether the firework has a trail.
     *
     * @return true if it has a trail.
     */
    public boolean isTrail() {
        return trail;
    }

    /**
     * Set whether the firework has a trail.
     *
     * @param trail true if it has a trail.
     */
    public void setTrail(boolean trail) {
        this.trail = trail;
    }

    /**
     * Whether the firework twinkles as it explodes.
     *
     * @return true if it twinkles.
     */
    public boolean isTwinkle() {
        return twinkle;
    }

    /**
     * Set whether the firework twinkles as it explodes.
     *
     * @param twinkle true if it twinkles.
     */
    public void setTwinkle(boolean twinkle) {
        this.twinkle = twinkle;
    }

    @Override
    public String toString() {
        return "ChunkerFireworkExplosion{" +
                "shape=" + getShape() +
                ", colors=" + getColors() +
                ", fadeColors=" + getFadeColors() +
                ", trail=" + isTrail() +
                ", twinkle=" + isTwinkle() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkerFireworkExplosion that)) return false;
        return isTrail() == that.isTrail() && isTwinkle() == that.isTwinkle() && getShape() == that.getShape() && Objects.equals(getColors(), that.getColors()) && Objects.equals(getFadeColors(), that.getFadeColors());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getShape(), getColors(), getFadeColors(), isTrail(), isTwinkle());
    }
}
