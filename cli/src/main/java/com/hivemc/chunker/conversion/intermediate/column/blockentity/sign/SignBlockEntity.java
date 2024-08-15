package com.hivemc.chunker.conversion.intermediate.column.blockentity.sign;

import com.google.gson.JsonElement;
import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Sign Block Entity.
 */
public class SignBlockEntity extends BlockEntity {
    private final SignFace front = new SignFace();
    private final SignFace back = new SignFace();
    private boolean waxed;

    /**
     * Whether the sign is sealed from editing.
     *
     * @return true if the sign cannot be edited.
     */
    public boolean isWaxed() {
        return waxed;
    }

    /**
     * Set whether the sign is sealed from editing.
     *
     * @param waxed true if the sign cannot be edited.
     */
    public void setWaxed(boolean waxed) {
        this.waxed = waxed;
    }

    /**
     * Get the front face of the sign.
     *
     * @return the front face.
     */
    public SignFace getFront() {
        return front;
    }

    /**
     * Get the back face of the sign.
     *
     * @return the back face.
     */
    public SignFace getBack() {
        return back;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return isWaxed() == that.isWaxed() && Objects.equals(getFront(), that.getFront()) && Objects.equals(getBack(), that.getBack());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isWaxed(), getFront(), getBack());
    }

    /**
     * One side of a sign.
     */
    public static class SignFace {
        private List<JsonElement> lines = Collections.emptyList();
        private boolean lit;
        private int color = 0xff000000;

        /**
         * Get the lines for the sign (may not be modifiable).
         *
         * @return a list of lines that are present (can be any length).
         */
        public List<JsonElement> getLines() {
            return lines;
        }

        /**
         * Set the lines for the sign (can be immutable).
         *
         * @param lines a list of lines that are present (can be any length).
         */
        public void setLines(List<JsonElement> lines) {
            this.lines = lines;
        }

        /**
         * Whether the sign is lit (glowing).
         *
         * @return true if the sign is lit.
         */
        public boolean isLit() {
            return lit;
        }

        /**
         * Whether the sign is lit (glowing).
         *
         * @param lit true if the sign is lit.
         */
        public void setLit(boolean lit) {
            this.lit = lit;
        }

        /**
         * Get the color used for the sign.
         *
         * @return the color {@link com.hivemc.chunker.util.JsonTextUtil#COLOR_TO_BEDROCK_HEX}.
         */
        public int getColor() {
            return color;
        }

        /**
         * Set the color used for the sign.
         *
         * @param color the color {@link com.hivemc.chunker.util.JsonTextUtil#COLOR_TO_BEDROCK_HEX}.
         */
        public void setColor(int color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SignFace signFace)) return false;
            return isLit() == signFace.isLit() && getColor() == signFace.getColor() && Objects.equals(getLines(), signFace.getLines());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLines(), isLit(), getColor());
        }
    }
}
