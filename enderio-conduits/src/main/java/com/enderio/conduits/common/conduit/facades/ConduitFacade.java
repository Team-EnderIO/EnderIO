package com.enderio.conduits.common.conduit.facades;

import net.minecraft.world.level.block.Block;

public interface ConduitFacade {
    /**
     * @return whether the facade is ready to be consumed.
     */
    boolean isValid();

    Block block();
    void block(Block block);

    FacadeType type();
    void type(FacadeType type);
}
