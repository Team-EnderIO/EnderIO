package com.enderio.base.api.io.energy;

import com.enderio.base.api.io.IOConfigurable;

/**
 * Energy IO Mode declares how Energy IO is determined using IO configs.
 */
public enum EnergyIOMode {
    /**
     * Intended for energy input.
     * Will only accept energy being put into the block.
     */
    Input(true, false, false),

    /**
     * Intended for energy output.
     * Will only allow energy to be taken/pushed out.
     */
    Output(false, true, false),

    /**
     * Intended for energy buffering.
     * Will allow both directions of travel.
     *
     * @apiNote When in a sided context, utilises the {@link IOConfigurable} to determine the direction of travel.
     */
    Both(true, true, true);

    private final boolean input;
    private final boolean output;
    private final boolean respectIOConfig;

    EnergyIOMode(boolean input, boolean output, boolean respectIOConfig) {
        this.input = input;
        this.output = output;
        this.respectIOConfig = respectIOConfig;
    }

    public boolean canInput() {
        return input;
    }

    public boolean canOutput() {
        return output;
    }

    // TODO: Clarify - IOConfig canConnect has precedence over this.
    public boolean respectIOConfig() {
        return respectIOConfig;
    }
}
