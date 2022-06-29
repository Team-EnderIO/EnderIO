package com.enderio.base.config.machines.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class MachinesCommonConfig {
    public final ForgeConfigSpec.ConfigValue<Float> ENCHANTER_LAPIS_COST_FACTOR;
    public final ForgeConfigSpec.ConfigValue<Float> ENCHANTER_LEVEL_COST_FACTOR;
    public final ForgeConfigSpec.ConfigValue<Integer> ENCHANTER_BASE_LEVEL_COST;

    public MachinesCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.push("enchanter");
        ENCHANTER_LAPIS_COST_FACTOR = builder.comment("The lapis cost is enchant level multiplied by this value.").define("lapisCostFactor", 3.0f);
        ENCHANTER_LEVEL_COST_FACTOR = builder.comment("The final XP cost for an enchantment is multiplied by this value. To halve costs set to 0.5, to double them set it to 2.").define("levelCostFactor", 0.75f);
        ENCHANTER_BASE_LEVEL_COST = builder.comment("Base level cost added to all recipes in the enchanter.").define("baseLevelCost", 2);
        builder.pop();
    }
}
