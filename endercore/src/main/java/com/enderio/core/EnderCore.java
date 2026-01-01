package com.enderio.core;

import net.minecraft.resources.Identifier;
import net.neoforged.fml.common.Mod;

@Mod(EnderCore.MOD_ID)
public class EnderCore {
    // Stored here just to make sure its the same.
    // This definition is used *everywhere* else.
    public static final String MOD_ID = "endercore";

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }
}
