package com.enderio.base.api.io;

import net.minecraft.core.Direction;

/**
 * IO Config defines how each side of a block interacts with other blocks.
 */
public interface IOConfigurable {
    /**
     * Get the current IO mode for the given side.
     */
    IOMode getIOMode(Direction side);

    /**
     * @return Whether this configuration should be edited.
     */
    boolean isIOConfigMutable();

    boolean shouldRenderIOConfigOverlay();

    /**
     * Set the IO mode for this side.
     * Must be supported mode, otherwise {@link IOMode#NONE} will be set.
     */
    void setIOMode(Direction side, IOMode mode);

    /**
     * Determine whether a certain side supports the provided mode.
     */
    boolean supportsIOMode(Direction side, IOMode state);

    default IOMode getNextIOMode(Direction side) {
        return getNextIOMode(side, getIOMode(side));
    }

    default IOMode getNextIOMode(Direction side, IOMode currentMode) {
        // Get the index of the current and next mode.
        int curOrd = currentMode.ordinal();
        int nextOrd = (curOrd + 1) % IOMode.values().length;

        // Cycle until we loop back on ourselves.
        while (nextOrd != curOrd) {
            IOMode next = IOMode.values()[nextOrd];

            if (supportsIOMode(side, next)) {
                return next;
            }

            nextOrd = (nextOrd + 1) % IOMode.values().length;
        }

        return currentMode;
    }

    default void cycleIOMode(Direction side) {
        setIOMode(side, getNextIOMode(side));
    }
}
