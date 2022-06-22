package com.enderio.api.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

/**
 * A class that provides capabilities of some type.
 * Ensures that capabilities can both be fetched and invalidated.
 *
 * @implNote Capabilities should be cached and invalidated upon request.
 */
public interface IEnderCapabilityProvider<T> {
    /**
     * Get the capability type provided.
     */
    Capability<T> getCapabilityType();

    /**
     * Get a capability to access config from the given side.
     */
    LazyOptional<T> getCapability(@Nullable Direction side);

    /**
     * Invalidate a specific side.
     * Useful for when side IO is disabled and neighbours require notification.
     *
     * @param side The side to be invalidated (or null for the "null side").
     */
    void invalidateSide(@Nullable Direction side);

    /**
     * Invalidate any capabilities cached by the config.
     */
    void invalidateCaps();
}
