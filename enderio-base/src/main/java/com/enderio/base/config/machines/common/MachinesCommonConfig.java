package com.enderio.base.config.machines.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class MachinesCommonConfig {
    public final ForgeConfigSpec.ConfigValue<Float> ENCHANTER_LAPIS_COST_FACTOR;

    public MachinesCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.push("enchanter");
        ENCHANTER_LAPIS_COST_FACTOR = builder.comment("Multiplier for lapis cost in enchanter.").define("lapisCostFactor", 1.0f);
        builder.pop();
    }
}
