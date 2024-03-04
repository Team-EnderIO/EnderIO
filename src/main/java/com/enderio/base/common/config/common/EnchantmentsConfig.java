package com.enderio.base.common.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EnchantmentsConfig {
    public final ModConfigSpec.ConfigValue<Integer> AUTO_SMELT_MAX_COST;
    public final ModConfigSpec.ConfigValue<Integer> AUTO_SMELT_MIN_COST;

    public final ModConfigSpec.ConfigValue<Integer> REPELLENT_MAX_LEVEL;
    public final ModConfigSpec.ConfigValue<Integer> REPELLENT_MAX_COST_BASE;
    public final ModConfigSpec.ConfigValue<Integer> REPELLENT_MAX_COST_MULT;
    public final ModConfigSpec.ConfigValue<Integer> REPELLENT_MIN_COST_BASE;
    public final ModConfigSpec.ConfigValue<Integer> REPELLENT_MIN_COST_MULT;
    public final ModConfigSpec.ConfigValue<Double> REPELLENT_CHANCE_BASE;
    public final ModConfigSpec.ConfigValue<Double> REPELLENT_CHANCE_MULT;
    public final ModConfigSpec.ConfigValue<Double> REPELLENT_RANGE_BASE;
    public final ModConfigSpec.ConfigValue<Double> REPELLENT_RANGE_MULT;
    public final ModConfigSpec.ConfigValue<Double> REPELLENT_NON_PLAYER_CHANCE;

    public final ModConfigSpec.ConfigValue<Integer> SHIMMER_MAX_COST;
    public final ModConfigSpec.ConfigValue<Integer> SHIMMER_MIN_COST;

    public final ModConfigSpec.ConfigValue<Integer> SOUL_BOUND_MAX_COST;
    public final ModConfigSpec.ConfigValue<Integer> SOUL_BOUND_MIN_COST;

    public final ModConfigSpec.ConfigValue<Integer> WITHERING_BLADE_MAX_COST;
    public final ModConfigSpec.ConfigValue<Integer> WITHERING_BLADE_MIN_COST;

    public final ModConfigSpec.ConfigValue<Integer> WITHERING_ARROW_MAX_COST;
    public final ModConfigSpec.ConfigValue<Integer> WITHERING_ARROW_MIN_COST;

    public final ModConfigSpec.ConfigValue<Integer> WITHERING_BOLT_MAX_COST;
    public final ModConfigSpec.ConfigValue<Integer> WITHERING_BOLT_MIN_COST;

    public final ModConfigSpec.ConfigValue<Integer> XP_BOOST_MAX_COST_BASE;
    public final ModConfigSpec.ConfigValue<Integer> XP_BOOST_MAX_COST_MULT;
    public final ModConfigSpec.ConfigValue<Integer> XP_BOOST_MIN_COST_BASE;
    public final ModConfigSpec.ConfigValue<Integer> XP_BOOST_MIN_COST_MULT;

    public EnchantmentsConfig(ModConfigSpec.Builder builder) {
        builder.push("enchantments");

        builder.push("autoSmelt");
        AUTO_SMELT_MAX_COST = builder.define("maxCost", 60);
        AUTO_SMELT_MIN_COST = builder.define("minCost", 15);
        builder.pop();

        builder.push("repellent");
        REPELLENT_MAX_LEVEL = builder.define("maxLevel", 4);
        REPELLENT_MAX_COST_BASE = builder.define("maxCostBase", 10);
        REPELLENT_MAX_COST_MULT = builder.define("maxCostPerLevel", 10);
        REPELLENT_MIN_COST_BASE = builder.define("minCostBase", 10);
        REPELLENT_MIN_COST_MULT = builder.define("minCostPerLevel", 5);
        REPELLENT_CHANCE_BASE = builder.define("chanceBase", 0.35d);
        REPELLENT_CHANCE_MULT = builder.define("chancePerLevel", 0.1d);
        REPELLENT_RANGE_BASE = builder.define("rangeBase", 8d);
        REPELLENT_RANGE_MULT = builder.define("rangePerLevel", 8d);
        REPELLENT_NON_PLAYER_CHANCE = builder.define("nonPlayerChance", 0.75d);
        builder.pop();

        builder.push("shimmer");
        SHIMMER_MAX_COST = builder.define("maxCost", 100);
        SHIMMER_MIN_COST = builder.define("minCost", 1);
        builder.pop();

        builder.push("soulBound");
        SOUL_BOUND_MAX_COST = builder.define("maxCost", 60);
        SOUL_BOUND_MIN_COST = builder.define("minCost", 16);
        builder.pop();

        builder.push("witheringBlade");
        WITHERING_BLADE_MAX_COST = builder.define("maxCost", 100);
        WITHERING_BLADE_MIN_COST = builder.define("minCost", 1);
        builder.pop();

        builder.push("witheringArrow");
        WITHERING_ARROW_MAX_COST = builder.define("maxCost", 100);
        WITHERING_ARROW_MIN_COST = builder.define("minCost", 1);
        builder.pop();

        // TODO: DEFAULTS FOR CROSSBOW ENCHANTMENTS
        builder.push("witheringBolt");
        WITHERING_BOLT_MAX_COST = builder.define("maxCost", 100);
        WITHERING_BOLT_MIN_COST = builder.define("minCost", 1);
        builder.pop();

        builder.push("xpBoost");
        // TODO: Defaults need work because the original code for XPboost was odd.
        XP_BOOST_MAX_COST_BASE = builder.define("maxCostBase", 30);
        XP_BOOST_MAX_COST_MULT = builder.define("maxCostPerLevel", 10);
        XP_BOOST_MIN_COST_BASE = builder.define("minCostBase", 1);
        XP_BOOST_MIN_COST_MULT = builder.define("minCostPerLevel", 10);
        builder.pop();

        builder.pop();
    }
}
