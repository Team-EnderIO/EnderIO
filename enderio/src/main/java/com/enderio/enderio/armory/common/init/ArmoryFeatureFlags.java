package com.enderio.enderio.armory.common.init;

import com.enderio.enderio.api.EnderIOAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;

public class ArmoryFeatureFlags {
    public static final FeatureFlag ARMORY_REWRITE = FeatureFlags.REGISTRY
            .getFlag(ResourceLocation.fromNamespaceAndPath(EnderIOAPI.MOD_ID, "armory_rewrite"));
}
