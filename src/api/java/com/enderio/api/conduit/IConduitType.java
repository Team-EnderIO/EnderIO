package com.enderio.api.conduit;

import net.minecraft.resources.ResourceLocation;

public interface IConduitType {

    ResourceLocation getTexture();

    default boolean canBeInSameBlock(IConduitType other) {
        return true;
    }

    default boolean canBeReplacedBy(IConduitType other) {
        return false;
    }

    default int getLightLevel(boolean isActive) {
        return 0;
    }
}
