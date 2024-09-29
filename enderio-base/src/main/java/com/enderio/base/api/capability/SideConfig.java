package com.enderio.base.api.capability;

import com.enderio.base.api.io.IOMode;

/**
 * Capability for getting the IO mode of the side of a block.
 */
public interface SideConfig {
    /**
     * Get the IO mode of this side.
     */
    IOMode getMode();

    /**
     * Set the IO mode of this side.
     */
    void setMode(IOMode mode);

    /**
     * Cycle the IO mode of this side.
     */
    void cycleMode();
}
