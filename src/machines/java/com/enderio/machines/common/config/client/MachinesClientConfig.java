package com.enderio.machines.common.config.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class MachinesClientConfig {

    public static MachinesClientBlocksConfig BLOCKS;

    public MachinesClientConfig(ForgeConfigSpec.Builder builder) {
        BLOCKS = new MachinesClientBlocksConfig(builder);

    }
}
