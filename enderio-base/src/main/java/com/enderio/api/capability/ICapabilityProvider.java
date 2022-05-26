package com.enderio.api.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;

/**
 * A class that provides capabilities of some type.
 * Ensures that capabilities can both be fetched and invalidated.
 *
 * @implNote Capabilities should be cached and invalidated upon request.
 */
public interface ICapabilityProvider<T> {
    /**
     * Get a capability to access config from the given side.
     */
    LazyOptional<T> getCapability(Direction side);

    /**
     * Invalidate any capabilities cached by the config.
     */
    void invalidateCaps();
}
