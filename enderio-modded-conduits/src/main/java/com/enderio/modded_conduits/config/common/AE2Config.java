package com.enderio.modded_conduits.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AE2Config {
    public final ModConfigSpec.DoubleValue NORMAL_ME_POWER_USAGE_PER_TICK;
    public final ModConfigSpec.DoubleValue DENSE_ME_POWER_USAGE_PER_TICK;

    public AE2Config(ModConfigSpec.Builder builder) {
        builder.push("ae2");

        NORMAL_ME_POWER_USAGE_PER_TICK = builder
            .comment("Idle power usage per tick for normal (non dense) ME conduits (AE)")
            .defineInRange("normalPowerUsagePerTick", 0, 0, Double.MAX_VALUE);

        DENSE_ME_POWER_USAGE_PER_TICK = builder
            .comment("Idle power usage per tick for dense ME conduits (AE)")
            .defineInRange("densePowerUsagePerTick", 0, 0, Double.MAX_VALUE);

        builder.pop();
    }
}
