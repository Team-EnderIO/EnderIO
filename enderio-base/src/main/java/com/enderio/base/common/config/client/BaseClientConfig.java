package com.enderio.base.common.config.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class BaseClientConfig {
    public final ModConfigSpec.ConfigValue<Boolean> MACHINE_PARTICLES;

    public BaseClientConfig(ModConfigSpec.Builder builder) {
        builder.push("visual");
        MACHINE_PARTICLES = builder.comment("Enable machine particles").define("machineParticles", true);
        builder.pop();
    }
}
