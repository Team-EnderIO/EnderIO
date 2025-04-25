package com.enderio.armory.common.init;

import com.enderio.armory.EnderIOArmory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;

public class ArmoryFeatureFlags {
    public static final FeatureFlag ARMORY_REWRITE = FeatureFlags.REGISTRY
            .getFlag(ResourceLocation.fromNamespaceAndPath(EnderIOArmory.MODULE_MOD_ID, "armory_rewrite"));
}
