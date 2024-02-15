package com.enderio.machines.common.config.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MachinesClientConfig {

    public final MachinesClientBlocksConfig BLOCKS;

    public final ModConfigSpec.ConfigValue<Double> IO_CONFIG_NEIGHBOUR_TRANSPARENCY;

    public MachinesClientConfig(ModConfigSpec.Builder builder) {
        BLOCKS = new MachinesClientBlocksConfig(builder);

        builder.push("ioconfig");
        IO_CONFIG_NEIGHBOUR_TRANSPARENCY = builder.comment("Neighbour Transparency [0-1]").define("neighbourTransparency", 0.4d);
        builder.pop();
    }
}
