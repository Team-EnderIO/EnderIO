package com.enderio.machines.common.config;

import com.enderio.machines.common.config.client.MachinesClientConfig;
import com.enderio.machines.common.config.common.MachinesCommonConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MachinesConfig {
    public static final MachinesCommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static final MachinesClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<MachinesCommonConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(MachinesCommonConfig::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();

        Pair<MachinesClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(MachinesClientConfig::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
    }
}
