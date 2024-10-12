package com.enderio.conduits.common.conduit.facades;

import net.minecraft.world.level.block.Block;

public interface ConduitFacadeProvider {
    /**
     * @return whether the facade is ready to be consumed.
     */
    boolean isValid();

    Block block();

    FacadeType type();
}
