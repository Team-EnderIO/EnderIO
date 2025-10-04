package com.enderio.endergy;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;

@Mod(EnderIOEndergy.MOD_ID)
public class EnderIOEndergy {
    public static final String MOD_ID = "enderio_endergy";

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
