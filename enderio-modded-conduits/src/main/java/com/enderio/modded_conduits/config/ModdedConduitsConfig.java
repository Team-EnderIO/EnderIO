package com.enderio.modded_conduits.config;

import com.enderio.modded_conduits.config.common.ModdedConduitsCommonConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModdedConduitsConfig {
    public static final ModdedConduitsCommonConfig COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        Pair<ModdedConduitsCommonConfig, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder().configure(ModdedConduitsCommonConfig::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
