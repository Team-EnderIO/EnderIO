package com.enderio.api.conduit.facade;

import net.minecraft.world.level.block.Block;

/**
 * Capability interface for items that can provide a facade to a conduit bundle.
 * Applied to facade items to indicate which block they mimic.
 */
public interface ConduitFacadeProvider {
    
    /**
     * @return Whether this facade provider has valid data.
     */
    boolean isValid();
    
    /**
     * @return The block this facade should mimic.
     */
    Block block();
    
    /**
     * @return The type of facade with its special properties.
     */
    FacadeType type();
}
