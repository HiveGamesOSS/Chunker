package com.hivemc.chunker.conversion.intermediate.column.blockentity;

import java.util.Objects;

/**
 * Represents a Command Block Entity.
 */
public class CommandBlockEntity extends BlockEntity {
    private String command;
    private boolean auto;
    private boolean trackOutput;

    /**
     * Get the command to be run when the command block is activated.
     *
     * @return the command.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Set the command to be run when the command block is activated.
     *
     * @param command the command.
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Whether the command block can be triggered without a redstone pulse.
     *
     * @return true if it can be triggered without redstone.
     */
    public boolean isAuto() {
        return auto;
    }

    /**
     * Set whether the command block can be triggered without a redstone pulse.
     *
     * @param auto true if it can be triggered without redstone.
     */
    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    /**
     * Get whether the command block should track the output after execution.
     *
     * @return true if the output is recorded.
     */
    public boolean isTrackOutput() {
        return trackOutput;
    }

    /**
     * Set whether the command block should track the output after execution.
     *
     * @param trackOutput true if the output is recorded.
     */
    public void setTrackOutput(boolean trackOutput) {
        this.trackOutput = trackOutput;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandBlockEntity that)) return false;
        if (!super.equals(o)) return false;
        return isAuto() == that.isAuto() && isTrackOutput() == that.isTrackOutput() && Objects.equals(getCommand(), that.getCommand());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCommand(), isAuto(), isTrackOutput());
    }
}
