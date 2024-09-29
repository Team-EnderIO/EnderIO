package com.enderio.base.common.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class InfinityConfig {
    public final ModConfigSpec.BooleanValue MAKES_SOUND;
    public final ModConfigSpec.IntValue FIRE_MIN_AGE;

    public InfinityConfig(ModConfigSpec.Builder builder) {
        builder.push("grainsOfInfinity");

        MAKES_SOUND = builder.comment("Should it make a sound when Grains of Infinity drops from a fire?").define("makesSound", true);
        FIRE_MIN_AGE = builder.comment("How old (in ticks) does a fire have to be to be able to spawn Infinity Powder?").defineInRange("fireMinAge", 260, 1, 1000);

        builder.pop();
    }
}
