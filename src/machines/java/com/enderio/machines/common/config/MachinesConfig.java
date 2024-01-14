package com.enderio.machines.common.config;

import com.enderio.machines.common.config.client.MachinesClientConfig;
import com.enderio.machines.common.config.common.MachinesCommonConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MachinesConfig {
    public static final MachinesCommonConfig COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    public static final MachinesClientConfig CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        Pair<MachinesCommonConfig, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder().configure(MachinesCommonConfig::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();

        Pair<MachinesClientConfig, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(MachinesClientConfig::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
    }
}
