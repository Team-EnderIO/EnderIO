package com.enderio.modded_conduits.config;

import com.enderio.modded_conduits.config.common.ModdedConduitsCommonConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModdedConduitsConfig {
    public static final ModdedConduitsCommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<ModdedConduitsCommonConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(ModdedConduitsCommonConfig::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
