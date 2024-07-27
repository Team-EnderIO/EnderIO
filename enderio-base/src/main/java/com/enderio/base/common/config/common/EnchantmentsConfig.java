package com.enderio.base.common.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EnchantmentsConfig {
    public final ModConfigSpec.DoubleValue REPELLENT_CHANCE_BASE;
    public final ModConfigSpec.DoubleValue REPELLENT_CHANCE_MULT;
    public final ModConfigSpec.DoubleValue REPELLENT_RANGE_BASE;
    public final ModConfigSpec.DoubleValue REPELLENT_RANGE_MULT;
    public final ModConfigSpec.DoubleValue REPELLENT_NON_PLAYER_CHANCE;

    public EnchantmentsConfig(ModConfigSpec.Builder builder) {
        builder.push("enchantments");

        builder.push("repellent");
        REPELLENT_CHANCE_BASE = builder.defineInRange("chanceBase", 0.35d, 0.0d, 1.0d);
        REPELLENT_CHANCE_MULT = builder.defineInRange("chancePerLevel", 0.1d, 0.0d, 1.0d);
        REPELLENT_RANGE_BASE = builder.defineInRange("rangeBase", 8d, 4d, Double.MAX_VALUE);
        REPELLENT_RANGE_MULT = builder.defineInRange("rangePerLevel", 8d, 0.0d, Double.MAX_VALUE);
        REPELLENT_NON_PLAYER_CHANCE = builder.defineInRange("nonPlayerChance", 0.75d, 0.0d, 1.0d);
        builder.pop();

        builder.pop();
    }
}
