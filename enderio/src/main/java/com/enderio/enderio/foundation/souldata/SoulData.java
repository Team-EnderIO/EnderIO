package com.enderio.enderio.foundation.souldata;

import net.minecraft.resources.Identifier;

/**
 * Interface for all soul binding data.
 */
public interface SoulData {

    /**
     * Resource Location of the entityType. This is used to speed up searching for the correct one.
     */
    Identifier getKey();
}
