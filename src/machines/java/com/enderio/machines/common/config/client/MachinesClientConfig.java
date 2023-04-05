package com.enderio.machines.common.config.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class MachinesClientConfig {

    public static MachinesClientBlocksConfig BLOCKS;

    public final ForgeConfigSpec.ConfigValue<Float> IO_CONFIG_NEIGHBOUR_TRANSPARENCY;

    public MachinesClientConfig(ForgeConfigSpec.Builder builder) {
        BLOCKS = new MachinesClientBlocksConfig(builder);

        builder.push("ioconfig");
        IO_CONFIG_NEIGHBOUR_TRANSPARENCY = builder.comment("Neighbour Transparency [0-1]").define("machineParticles", 0.4F);
        builder.pop();
    }
}
