package com.enderio.base.config.base.common;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class InfinityConfig {
    public final ForgeConfigSpec.ConfigValue<Boolean> MAKES_SOUND;
    public final ForgeConfigSpec.ConfigValue<Integer> FIRE_MIN_AGE;

    public InfinityConfig(ForgeConfigSpec.Builder builder) {
        builder.push("grainsOfInfinity");

        MAKES_SOUND = builder.comment("Should it make a sound when Grains of Infinity drops from a fire?").define("makesSound", true);
        FIRE_MIN_AGE = builder.comment("How old (in ticks) does a fire have to be to be able to spawn Infinity Powder?").defineInRange("fireMinAge", 260, 1, 1000);

        builder.pop();
    }
}
