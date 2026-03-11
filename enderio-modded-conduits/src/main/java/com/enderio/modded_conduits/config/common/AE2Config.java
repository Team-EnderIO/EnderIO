package com.enderio.modded_conduits.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class AE2Config {
    public final ForgeConfigSpec.DoubleValue NORMAL_ME_POWER_USAGE_PER_TICK;
    public final ForgeConfigSpec.DoubleValue DENSE_ME_POWER_USAGE_PER_TICK;

    public AE2Config(ForgeConfigSpec.Builder builder) {
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
