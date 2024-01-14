package com.enderio.armory.common.config;

import com.enderio.armory.common.config.client.ArmoryClientConfig;
import com.enderio.armory.common.config.common.ArmoryCommonConfig;
import com.enderio.base.common.config.client.BaseClientConfig;
import com.enderio.base.common.config.common.BaseCommonConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ArmoryConfig {
    public static final ArmoryCommonConfig COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    public static final ArmoryClientConfig CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        Pair<ArmoryCommonConfig, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder().configure(ArmoryCommonConfig::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();

        Pair<ArmoryClientConfig, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(ArmoryClientConfig::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
    }
}
