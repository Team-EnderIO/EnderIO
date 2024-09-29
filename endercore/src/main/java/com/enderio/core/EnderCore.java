package com.enderio.core;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Little helper for logging and resource locations.
// This is because core has no access to base.
//@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
@Mod(EnderCore.MOD_ID)
public class EnderCore {
    // Stored here just to make sure its the same.
    // This definition is used *everywhere* else.
    public static final String MOD_ID = "endercore";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID + ":core");

    public static ResourceLocation loc(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
