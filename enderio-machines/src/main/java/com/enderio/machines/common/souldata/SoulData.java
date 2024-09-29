package com.enderio.machines.common.souldata;

import net.minecraft.resources.ResourceLocation;

/**
 * Interface for all soul binding data.
 */
public interface SoulData {

    /**
     * Resource Location of the entityType. This is used to speed up searching for the correct one.
     */
    ResourceLocation getKey();
}
