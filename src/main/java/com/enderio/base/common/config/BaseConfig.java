package com.enderio.base.common.config;

import com.enderio.base.common.config.client.BaseClientConfig;
import com.enderio.base.common.config.common.BaseCommonConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BaseConfig {
    public static final BaseCommonConfig COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    public static final BaseClientConfig CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        Pair<BaseCommonConfig, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder().configure(BaseCommonConfig::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();

        Pair<BaseClientConfig, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(BaseClientConfig::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
    }
}
