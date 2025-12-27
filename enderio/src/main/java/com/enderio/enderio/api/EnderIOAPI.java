package com.enderio.enderio.api;

import net.minecraft.resources.Identifier;

public class EnderIOAPI {
    public static final String MOD_ID = "enderio";

    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
