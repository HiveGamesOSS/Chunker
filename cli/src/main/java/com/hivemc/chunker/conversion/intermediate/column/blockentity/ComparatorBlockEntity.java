package com.hivemc.chunker.conversion.intermediate.column.blockentity;

/**
 * Represents a Comparator Block Entity.
 */
public class ComparatorBlockEntity extends BlockEntity {
    private int outputSignal;

    /**
     * The redstone signal given by the output.
     *
     * @return 0-15 integer for the redstone signal strength.
     */
    public int getOutputSignal() {
        return outputSignal;
    }

    /**
     * Set the output signal strength.
     *
     * @param outputSignal 0-15 integer for the redstone signal.
     */
    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }
}
