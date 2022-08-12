package com.enderio.api.conduit;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface IConduitType {

    ResourceLocation getTexture();

    Item getConduitItem();

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
