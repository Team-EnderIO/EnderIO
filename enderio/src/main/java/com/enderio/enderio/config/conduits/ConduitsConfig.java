package com.enderio.enderio.config.conduits;

import com.enderio.enderio.config.conduits.common.ConduitsCommonConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConduitsConfig {
    public static final ConduitsCommonConfig COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        Pair<ConduitsCommonConfig, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder().configure(ConduitsCommonConfig::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
