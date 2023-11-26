package com.enderio.base.common.config.client;

import net.neoforged.neoforge.common.ForgeConfigSpec;

public class BaseClientConfig {
    public final ForgeConfigSpec.ConfigValue<Boolean> MACHINE_PARTICLES;

    public BaseClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("visual");
        MACHINE_PARTICLES = builder.comment("Enable machine particles").define("machineParticles", true);
        builder.pop();
    }
}
