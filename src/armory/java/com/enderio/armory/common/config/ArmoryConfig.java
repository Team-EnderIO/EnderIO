package com.enderio.armory.common.config;

import com.enderio.armory.common.config.client.ArmoryClientConfig;
import com.enderio.armory.common.config.common.ArmoryCommonConfig;
import com.enderio.base.common.config.client.BaseClientConfig;
import com.enderio.base.common.config.common.BaseCommonConfig;
import net.neoforged.neoforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ArmoryConfig {
    public static final ArmoryCommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static final ArmoryClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<ArmoryCommonConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(ArmoryCommonConfig::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();

        Pair<ArmoryClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(ArmoryClientConfig::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
    }
}
