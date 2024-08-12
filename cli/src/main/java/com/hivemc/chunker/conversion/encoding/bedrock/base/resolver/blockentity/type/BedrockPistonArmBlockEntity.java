package com.hivemc.chunker.conversion.encoding.bedrock.base.resolver.blockentity.type;

import com.hivemc.chunker.conversion.intermediate.column.blockentity.BlockEntity;

/**
 * Bedrock piston arm entity holding if it's extended/sticky.
 */
public class BedrockPistonArmBlockEntity extends BlockEntity {
    private boolean extended;
    private boolean sticky;

    /**
     * Whether the piston is extended.
     *
     * @return true if extended.
     */
    public boolean isExtended() {
        return extended;
    }

    /**
     * Set Whether the piston is extended.
     *
     * @param extended true if extended.
     */
    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    /**
     * Whether the piston is sticky.
     *
     * @return true if sticky.
     */
    public boolean isSticky() {
        return sticky;
    }

    /**
     * Set whether the piston is sticky.
     *
     * @param sticky true if sticky.
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }
}
