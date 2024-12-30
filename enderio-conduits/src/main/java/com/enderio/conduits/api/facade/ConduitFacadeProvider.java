package com.enderio.conduits.api.facade;

import net.minecraft.world.level.block.Block;

public interface ConduitFacadeProvider {
    /**
     * @return whether the facade is ready to be consumed.
     */
    boolean isValid();

    /**
     * @return the block that the facade appears as.
     */
    Block block();

    /**
     * @return The type of the facade, which determines its behaviour.
     */
    FacadeType type();
}
