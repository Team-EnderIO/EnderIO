package com.enderio.machines.common.souldata;

import net.minecraft.resources.ResourceLocation;

/**
 * Interface for all soul binding data.
 */
public interface ISoulData {

    /**
     * Resource Location of the entitytype. This is used to speed up searching for the correct one.
     */
    ResourceLocation getKey();
}
