package com.enderio.base.common.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class GraveConfig {
    public final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_GRAVE;

    public GraveConfig(ForgeConfigSpec.Builder builder) {
        builder.push("grave");

        ENABLE_GRAVE = builder.comment("Enable grave generation").define("enableGrave", true);
        builder.pop();
    }
}
