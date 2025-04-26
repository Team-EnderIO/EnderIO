package com.enderio.machines.common.init;

import com.enderio.machines.EnderIOMachines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;

public class MachineFeatureFlags {
    public static final FeatureFlag FARMING_STATION = FeatureFlags.REGISTRY
            .getFlag(ResourceLocation.fromNamespaceAndPath(EnderIOMachines.MODULE_MOD_ID, "farming_station"));

    public static final FeatureFlag ENDERFACE = FeatureFlags.REGISTRY
            .getFlag(ResourceLocation.fromNamespaceAndPath(EnderIOMachines.MODULE_MOD_ID, "enderface"));
}
