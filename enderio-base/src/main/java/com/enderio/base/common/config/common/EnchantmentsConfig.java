package com.enderio.base.common.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EnchantmentsConfig {
    public final ModConfigSpec.ConfigValue<Double> REPELLENT_CHANCE_BASE;
    public final ModConfigSpec.ConfigValue<Double> REPELLENT_CHANCE_MULT;
    public final ModConfigSpec.ConfigValue<Double> REPELLENT_RANGE_BASE;
    public final ModConfigSpec.ConfigValue<Double> REPELLENT_RANGE_MULT;
    public final ModConfigSpec.ConfigValue<Double> REPELLENT_NON_PLAYER_CHANCE;

    public EnchantmentsConfig(ModConfigSpec.Builder builder) {
        builder.push("enchantments");

        builder.push("repellent");
        REPELLENT_CHANCE_BASE = builder.define("chanceBase", 0.35d);
        REPELLENT_CHANCE_MULT = builder.define("chancePerLevel", 0.1d);
        REPELLENT_RANGE_BASE = builder.define("rangeBase", 8d);
        REPELLENT_RANGE_MULT = builder.define("rangePerLevel", 8d);
        REPELLENT_NON_PLAYER_CHANCE = builder.define("nonPlayerChance", 0.75d);
        builder.pop();

        builder.pop();
    }
}
