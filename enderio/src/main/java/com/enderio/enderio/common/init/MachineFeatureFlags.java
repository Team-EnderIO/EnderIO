package com.enderio.enderio.common.init;

import com.enderio.enderio.EnderIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;

public class MachineFeatureFlags {
    public static final FeatureFlag FARMING_STATION = FeatureFlags.REGISTRY
            .getFlag(ResourceLocation.fromNamespaceAndPath(EnderIO.MOD_ID, "farming_station"));

    public static final FeatureFlag ENDERFACE = FeatureFlags.REGISTRY
            .getFlag(ResourceLocation.fromNamespaceAndPath(EnderIO.MOD_ID, "enderface"));

    public static final FeatureFlag NIARD = FeatureFlags.REGISTRY
            .getFlag(ResourceLocation.fromNamespaceAndPath(EnderIO.MOD_ID, "niard"));
}
