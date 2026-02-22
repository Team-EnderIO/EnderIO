package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;

public class EIOFeatureFlags {
    public static final FeatureFlag ENDERFACE = FeatureFlags.REGISTRY
        .getFlag(Identifier.fromNamespaceAndPath(EnderIO.MOD_ID, "enderface"));

    public static final FeatureFlag NIARD = FeatureFlags.REGISTRY
        .getFlag(Identifier.fromNamespaceAndPath(EnderIO.MOD_ID, "niard"));
}
