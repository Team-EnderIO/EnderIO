package com.enderio.machines.common.config.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class MachinesClientConfig {

    public final MachinesClientBlocksConfig BLOCKS;

    public final ForgeConfigSpec.ConfigValue<Float> IO_CONFIG_NEIGHBOUR_TRANSPARENCY;

    public MachinesClientConfig(ForgeConfigSpec.Builder builder) {
        BLOCKS = new MachinesClientBlocksConfig(builder);

        builder.push("ioconfig");
        IO_CONFIG_NEIGHBOUR_TRANSPARENCY = builder.comment("Neighbour Transparency [0-1]").define("neighbourTransparency", 0.4F);
        builder.pop();
    }
}
