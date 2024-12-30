package com.enderio.base.api;

import net.minecraft.resources.ResourceLocation;

public class EnderIO {
    public static final String NAMESPACE = "enderio";

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }
}
