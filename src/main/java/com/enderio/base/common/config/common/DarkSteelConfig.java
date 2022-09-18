package com.enderio.base.common.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class DarkSteelConfig {

    public final ForgeConfigSpec.ConfigValue<Integer> DARK_STEEL_AXE_ENERGY_PER_FELLED_LOG;
    public final ForgeConfigSpec.ConfigValue<Integer> DARK_STEEL_PICKAXE_OBSIDIAN_ENERGY_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> DARK_STEEL_PICKAXE_OBSIDIAN_SPEED;
    public final ForgeConfigSpec.ConfigValue<Integer> DARK_STEEL_PICKAXE_AS_OBSIDIAN_AT_HARDNESS;

    public final ForgeConfigSpec.ConfigValue<Integer> EMPOWERED_EFFICIENCY_BOOST;
    public final ForgeConfigSpec.ConfigValue<Integer> EMPOWERED_ENERGY_PER_DAMAGE;
    public final ForgeConfigSpec.ConfigValue<Integer> EMPOWERED_ACTIVATION_COST_I;
    public final ForgeConfigSpec.ConfigValue<Float> EMPOWERED_DAMAGE_ABSORPTION_CHANCE_I;
    public final ForgeConfigSpec.ConfigValue<Integer> EMPOWERED_MAX_ENERGY_I;
    public final ForgeConfigSpec.ConfigValue<Integer> EMPOWERED_ACTIVATION_COST_II;
    public final ForgeConfigSpec.ConfigValue<Float> EMPOWERED_DAMAGE_ABSORPTION_CHANCE_II;
    public final ForgeConfigSpec.ConfigValue<Integer> EMPOWERED_MAX_ENERGY_II;
    public final ForgeConfigSpec.ConfigValue<Integer> EMPOWERED_ACTIVATION_COST_III;
    public final ForgeConfigSpec.ConfigValue<Float> EMPOWERED_DAMAGE_ABSORPTION_CHANCE_III;
    public final ForgeConfigSpec.ConfigValue<Integer> EMPOWERED_MAX_ENERGY_III;
    public final ForgeConfigSpec.ConfigValue<Integer> EMPOWERED_ACTIVATION_COST_IV;
    public final ForgeConfigSpec.ConfigValue<Float> EMPOWERED_DAMAGE_ABSORPTION_CHANCE_IV;
    public final ForgeConfigSpec.ConfigValue<Integer> EMPOWERED_MAX_ENERGY_IV;

    public final ForgeConfigSpec.ConfigValue<Integer> EXPLOSIVE_RADIUS_ACTIVATION_COST_I;
    public final ForgeConfigSpec.ConfigValue<Integer> EXPLOSIVE_I;
    public final ForgeConfigSpec.ConfigValue<Integer> EXPLOSIVE_ACTIVATION_COST_II;
    public final ForgeConfigSpec.ConfigValue<Integer> EXPLOSIVE_II;
    public final ForgeConfigSpec.ConfigValue<Integer> EXPLOSIVE_PENETRATION_ACTIVATION_COST_I;
    public final ForgeConfigSpec.ConfigValue<Integer> EXPLOSIVE_PENETRATION_I;
    public final ForgeConfigSpec.ConfigValue<Integer> EXPLOSIVE_PENETRATION_ACTIVATION_COST_II;
    public final ForgeConfigSpec.ConfigValue<Integer> EXPLOSIVE_PENETRATION_II;
    public final ForgeConfigSpec.ConfigValue<Integer> EXPLOSIVE_ENERGY_PER_EXPLODED_BLOCK;

    public final ForgeConfigSpec.ConfigValue<Integer> SPOON_ACTIVATION_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> FORK_ACTIVATION_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> DIRECT_ACTIVATION_COST;

    public DarkSteelConfig(ForgeConfigSpec.Builder builder) {
        builder.push("darksteel");

        builder.push("darksteelaxe");
        DARK_STEEL_AXE_ENERGY_PER_FELLED_LOG = builder.define("energyUsePerFelledLog", 1500);
        builder.pop();

        builder.push("darksteelpickaxe");
        DARK_STEEL_PICKAXE_OBSIDIAN_ENERGY_COST = builder.define("obsidianBreakPowerUse", 50);
        DARK_STEEL_PICKAXE_OBSIDIAN_SPEED = builder.define("speedBoostWhenObsidian", 50);
        DARK_STEEL_PICKAXE_AS_OBSIDIAN_AT_HARDNESS = builder.define("useObsidianBreakSpeedAtHardness", 30);
        builder.pop();

        builder.push("upgrades");
        builder.push("empowered");
        EMPOWERED_EFFICIENCY_BOOST = builder.define("efficiencyBoost", 2);
        EMPOWERED_ENERGY_PER_DAMAGE = builder.define("energyUsePerDamagePoint", 750);
        EMPOWERED_ACTIVATION_COST_I = builder.define("activationCost_l1", 4);
        EMPOWERED_DAMAGE_ABSORPTION_CHANCE_I = builder.define("damageAbsorptionChance_l1", 0.5f);
        EMPOWERED_MAX_ENERGY_I = builder.define("maxEnergy_l1", 100000);
        EMPOWERED_ACTIVATION_COST_II = builder.define("activationCost_l2", 8);
        EMPOWERED_DAMAGE_ABSORPTION_CHANCE_II = builder.define("damageAbsorptionChance_l2", 0.6f);
        EMPOWERED_MAX_ENERGY_II = builder.define("maxEnergy_l2", 150000);
        EMPOWERED_ACTIVATION_COST_III = builder.define("activationCost_l3", 12);
        EMPOWERED_DAMAGE_ABSORPTION_CHANCE_III = builder.define("damageAbsorptionChance_l3", 0.7f);
        EMPOWERED_MAX_ENERGY_III = builder.define("maxEnergy_l3", 250000);
        EMPOWERED_ACTIVATION_COST_IV = builder.define("activationCost_l4", 16);
        EMPOWERED_DAMAGE_ABSORPTION_CHANCE_IV = builder.define("damageAbsorptionChance_l4", 0.85f);
        EMPOWERED_MAX_ENERGY_IV = builder.define("maxEnergy_l4", 1000000);
        builder.pop();

        builder.push("explosive");
        EXPLOSIVE_ENERGY_PER_EXPLODED_BLOCK = builder.define("explosiveEnergyPerBlock", 30);

        EXPLOSIVE_RADIUS_ACTIVATION_COST_I = builder.define("explosiveActivationCostI", 8);
        EXPLOSIVE_I = builder.define("explosiveI", 1);
        EXPLOSIVE_ACTIVATION_COST_II = builder.define("explosiveActivationCostII", 12);
        EXPLOSIVE_II = builder.define("explosiveII", 2);

        EXPLOSIVE_PENETRATION_ACTIVATION_COST_I = builder.define("explosivePenetrationActivationCostI", 8);
        EXPLOSIVE_PENETRATION_I = builder.define("explosivePenetrationI", 1);
        EXPLOSIVE_PENETRATION_ACTIVATION_COST_II = builder.define("explosivePenetrationActivationCostII", 12);
        EXPLOSIVE_PENETRATION_II = builder.define("explosivePenetrationII", 2);
        builder.pop();

        SPOON_ACTIVATION_COST = builder.define("spoonActivationCost", 4);
        FORK_ACTIVATION_COST = builder.define("forkActivationCost", 4);
        DIRECT_ACTIVATION_COST = builder.define("directActivationCost", 4);
        builder.pop();
        builder.pop();
    }
}
