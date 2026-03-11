package com.enderio.enderio.config.conduits;

import com.enderio.enderio.config.conduits.common.ConduitsCommonConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConduitsConfig {
    public static final ConduitsCommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<ConduitsCommonConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(ConduitsCommonConfig::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
