package com.enderio.core;

import net.minecraft.resources.Identifier;
import net.neoforged.fml.common.Mod;

// This is because core has no access to base.
//@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
@Mod(EnderCore.MOD_ID)
public class EnderCore {
    // Stored here just to make sure its the same.
    // This definition is used *everywhere* else.
    public static final String MOD_ID = "endercore";

    public static Identifier loc(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }
}
