package com.enderio.core;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Little helper for logging and resource locations.
// This is because core has no access to base.
public class EnderCore {
    public static final Logger LOGGER = LogManager.getLogger("enderio:core");

    public static ResourceLocation loc(String name) {
        return new ResourceLocation("enderio", name);
    }
}
