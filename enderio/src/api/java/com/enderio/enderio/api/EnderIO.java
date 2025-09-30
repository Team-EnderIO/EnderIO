package com.enderio.enderio.api;

import net.minecraft.resources.ResourceLocation;

public class EnderIO {
    public static final String MOD_ID = "enderio";

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
