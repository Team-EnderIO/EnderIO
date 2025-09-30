package com.enderio.enderio.api;

import net.minecraft.resources.ResourceLocation;

public class EnderIOAPI {
    public static final String MOD_ID = "enderio";

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
