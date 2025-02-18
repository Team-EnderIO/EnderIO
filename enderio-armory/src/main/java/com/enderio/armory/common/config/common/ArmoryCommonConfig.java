package com.enderio.armory.common.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ArmoryCommonConfig {

    public final ModConfigSpec.ConfigValue<Integer> DARK_STEEL_AXE_ENERGY_PER_FELLED_LOG;
    public final ModConfigSpec.ConfigValue<Integer> DARK_STEEL_PICKAXE_OBSIDIAN_ENERGY_COST;
    public final ModConfigSpec.ConfigValue<Integer> DARK_STEEL_PICKAXE_OBSIDIAN_SPEED;
    public final ModConfigSpec.ConfigValue<Integer> DARK_STEEL_PICKAXE_AS_OBSIDIAN_AT_HARDNESS;

    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_EFFICIENCY_BOOST;
    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_ENERGY_PER_DAMAGE;

    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_MAX_ENERGY_I;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_DAMAGE_ABSORPTION_CHANCE_I;
    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_ATTACK_DAMAGE_INCREASE_I;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_ATTACK_SPEED_INCREASE_I;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_MOB_HEAD_CHANCE_I;
    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_ACTIVATION_COST_I;

    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_ACTIVATION_COST_II;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_DAMAGE_ABSORPTION_CHANCE_II;
    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_MAX_ENERGY_II;
    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_ATTACK_DAMAGE_INCREASE_II;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_ATTACK_SPEED_INCREASE_II;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_MOB_HEAD_CHANCE_II;

    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_ACTIVATION_COST_III;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_DAMAGE_ABSORPTION_CHANCE_III;
    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_MAX_ENERGY_III;
    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_ATTACK_DAMAGE_INCREASE_III;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_ATTACK_SPEED_INCREASE_III;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_MOB_HEAD_CHANCE_III;

    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_ACTIVATION_COST_IV;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_DAMAGE_ABSORPTION_CHANCE_IV;
    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_MAX_ENERGY_IV;
    public final ModConfigSpec.ConfigValue<Integer> EMPOWERED_ATTACK_DAMAGE_INCREASE_IV;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_ATTACK_SPEED_INCREASE_IV;
    public final ModConfigSpec.ConfigValue<Double> EMPOWERED_MOB_HEAD_CHANCE_IV;

    public final ModConfigSpec.ConfigValue<Integer> EXPLOSIVE_RADIUS_ACTIVATION_COST_I;
    public final ModConfigSpec.ConfigValue<Integer> EXPLOSIVE_I_RANGE;
    public final ModConfigSpec.ConfigValue<Integer> EXPLOSIVE_ACTIVATION_COST_II;
    public final ModConfigSpec.ConfigValue<Integer> EXPLOSIVE_II_RANGE;
    public final ModConfigSpec.ConfigValue<Integer> EXPLOSIVE_PENETRATION_ACTIVATION_COST_I;
    public final ModConfigSpec.ConfigValue<Integer> EXPLOSIVE_PENETRATION_I;
    public final ModConfigSpec.ConfigValue<Integer> EXPLOSIVE_PENETRATION_ACTIVATION_COST_II;
    public final ModConfigSpec.ConfigValue<Integer> EXPLOSIVE_PENETRATION_II;
    public final ModConfigSpec.ConfigValue<Integer> EXPLOSIVE_ENERGY_PER_EXPLODED_BLOCK;

    public final ModConfigSpec.ConfigValue<Integer> SPOON_ACTIVATION_COST;
    public final ModConfigSpec.ConfigValue<Integer> FORK_ACTIVATION_COST;
    public final ModConfigSpec.ConfigValue<Integer> DIRECT_ACTIVATION_COST;
    public final ModConfigSpec.ConfigValue<Integer> TRAVEL_ACTIVATION_COST;

    public final ModConfigSpec.ConfigValue<Integer> STEP_ASSIST_ACTIVATION_COST;

    public final ModConfigSpec.ConfigValue<Integer> SPEED_ENERGY_USE;
    public final ModConfigSpec.ConfigValue<Integer> SPEED_ACTIVATION_COST_I;
    public final ModConfigSpec.ConfigValue<Double> SPEED_I_BOOST;
    public final ModConfigSpec.ConfigValue<Integer> SPEED_ACTIVATION_COST_II;
    public final ModConfigSpec.ConfigValue<Double> SPEED_II_BOOST;
    public final ModConfigSpec.ConfigValue<Integer> SPEED_ACTIVATION_COST_III;
    public final ModConfigSpec.ConfigValue<Double> SPEED_III_BOOST;

    public final ModConfigSpec.ConfigValue<Integer> JUMP_ENERGY_USE;
    public final ModConfigSpec.ConfigValue<Integer> JUMP_COUNT_I;
    public final ModConfigSpec.ConfigValue<Integer> JUMP_ACTIVATION_COST_I;
    public final ModConfigSpec.ConfigValue<Integer> JUMP_COUNT_II;
    public final ModConfigSpec.ConfigValue<Integer> JUMP_ACTIVATION_COST_II;

    public final ModConfigSpec.ConfigValue<Integer> GLIDER_ACTIVATION_COST;
    public final ModConfigSpec.ConfigValue<Integer> ELYTRA_ACTIVATION_COST;

    public final ModConfigSpec.ConfigValue<Integer> NIGHT_VISION_ACTIVATION_COST;
    public final ModConfigSpec.ConfigValue<Integer> NIGHT_VISION_ENERGY_USE;

    public final ModConfigSpec.ConfigValue<Integer> SOLAR_ACTIVATION_COST_I;
    public final ModConfigSpec.ConfigValue<Integer> SOLAR_ACTIVATION_COST_II;
    public final ModConfigSpec.ConfigValue<Integer> SOLAR_ACTIVATION_COST_III;

    public ArmoryCommonConfig(ModConfigSpec.Builder builder) {
        builder.push("darksteel");

        builder.push("darksteelaxe");
        DARK_STEEL_AXE_ENERGY_PER_FELLED_LOG = builder.define("energyUsePerFelledLog", 500);
        builder.pop();

        builder.push("darksteelpickaxe");
        DARK_STEEL_PICKAXE_OBSIDIAN_ENERGY_COST = builder.define("obsidianBreakPowerUse", 50);
        DARK_STEEL_PICKAXE_OBSIDIAN_SPEED = builder.define("speedBoostWhenObsidian", 50);
        DARK_STEEL_PICKAXE_AS_OBSIDIAN_AT_HARDNESS = builder.define("useObsidianBreakSpeedAtHardness", 30);
        builder.pop();

        builder.push("darksteelsword");
        EMPOWERED_MOB_HEAD_CHANCE_I = builder.define("modHeadChance_l1", 0.07);
        EMPOWERED_MOB_HEAD_CHANCE_II = builder.define("modHeadChance_l2", 0.09);
        EMPOWERED_MOB_HEAD_CHANCE_III = builder.define("modHeadChance_l3", 0.11);
        EMPOWERED_MOB_HEAD_CHANCE_IV = builder.define("modHeadChance_l4", 0.13);
        EMPOWERED_ATTACK_DAMAGE_INCREASE_I = builder.define("attackDamageIncrease_l1", 1);
        EMPOWERED_ATTACK_DAMAGE_INCREASE_II = builder.define("attackDamageIncrease_l2", 2);
        EMPOWERED_ATTACK_DAMAGE_INCREASE_III = builder.define("attackDamageIncrease_l3", 3);
        EMPOWERED_ATTACK_DAMAGE_INCREASE_IV = builder.define("attackDamageIncrease_l4", 4);
        EMPOWERED_ATTACK_SPEED_INCREASE_I = builder.define("attackSpeedIncrease_l1", 0.4);
        EMPOWERED_ATTACK_SPEED_INCREASE_II = builder.define("attackSpeedIncrease_l2", 0.45);
        EMPOWERED_ATTACK_SPEED_INCREASE_III = builder.define("attackSpeedIncrease_l3", 0.5);
        EMPOWERED_ATTACK_SPEED_INCREASE_IV = builder.define("attackSpeedIncrease_l4", 0.55);
        builder.pop();

        builder.push("upgrades");
        builder.push("empowered");
        EMPOWERED_EFFICIENCY_BOOST = builder.define("efficiencyBoost", 2);
        EMPOWERED_ENERGY_PER_DAMAGE = builder.define("energyUsePerDamagePoint", 750);
        EMPOWERED_ACTIVATION_COST_I = builder.define("activationCost_l1", 4);
        EMPOWERED_DAMAGE_ABSORPTION_CHANCE_I = builder.define("damageAbsorptionChance_l1", 0.5d);
        EMPOWERED_MAX_ENERGY_I = builder.define("maxEnergy_l1", 100000);

        EMPOWERED_ACTIVATION_COST_II = builder.define("activationCost_l2", 8);
        EMPOWERED_DAMAGE_ABSORPTION_CHANCE_II = builder.define("damageAbsorptionChance_l2", 0.6d);
        EMPOWERED_MAX_ENERGY_II = builder.define("maxEnergy_l2", 150000);

        EMPOWERED_ACTIVATION_COST_III = builder.define("activationCost_l3", 12);
        EMPOWERED_DAMAGE_ABSORPTION_CHANCE_III = builder.define("damageAbsorptionChance_l3", 0.7d);
        EMPOWERED_MAX_ENERGY_III = builder.define("maxEnergy_l3", 250000);

        EMPOWERED_ACTIVATION_COST_IV = builder.define("activationCost_l4", 16);
        EMPOWERED_DAMAGE_ABSORPTION_CHANCE_IV = builder.define("damageAbsorptionChance_l4", 0.85d);
        EMPOWERED_MAX_ENERGY_IV = builder.define("maxEnergy_l4", 1000000);
        builder.pop();

        builder.push("explosive");
        EXPLOSIVE_ENERGY_PER_EXPLODED_BLOCK = builder.define("explosiveEnergyPerBlock", 30);

        EXPLOSIVE_RADIUS_ACTIVATION_COST_I = builder.define("explosiveActivationCost_l1", 8);
        EXPLOSIVE_I_RANGE = builder.define("explosive_range_l1", 1);
        EXPLOSIVE_ACTIVATION_COST_II = builder.define("explosiveActivationCost_l2", 12);
        EXPLOSIVE_II_RANGE = builder.define("explosive_range_l2", 2);
        builder.pop();

        builder.push("explosivePenetration");
        EXPLOSIVE_PENETRATION_ACTIVATION_COST_I = builder.define("explosivePenetrationActivationCost_l1", 8);
        EXPLOSIVE_PENETRATION_I = builder.define("explosivePenetration_l1", 1);
        EXPLOSIVE_PENETRATION_ACTIVATION_COST_II = builder.define("explosivePenetrationActivationCost_l2", 12);
        EXPLOSIVE_PENETRATION_II = builder.define("explosivePenetration_l2", 2);
        builder.pop();

        SPOON_ACTIVATION_COST = builder.define("spoonActivationCost", 4);
        FORK_ACTIVATION_COST = builder.define("forkActivationCost", 4);
        DIRECT_ACTIVATION_COST = builder.define("directActivationCost", 8);
        TRAVEL_ACTIVATION_COST = builder.define("travelActivationCost", 12);

        STEP_ASSIST_ACTIVATION_COST = builder.define("stepAssistActivationCost", 4);

        builder.push("nightVision");
        NIGHT_VISION_ACTIVATION_COST = builder.define("nightVisionActivationCost", 4);
        NIGHT_VISION_ENERGY_USE = builder.define("nightVisionEnergyUse", 2);
        builder.pop();

        builder.push("solar");
        SOLAR_ACTIVATION_COST_I = builder.define("solarActivationCost_l1", 4);
        SOLAR_ACTIVATION_COST_II = builder.define("solarActivationCost_l2", 8);
        SOLAR_ACTIVATION_COST_III = builder.define("solarActivationCost_l3", 16);
        builder.pop();

        GLIDER_ACTIVATION_COST = builder.define("gliderActivationCost", 4);
        ELYTRA_ACTIVATION_COST = builder.define("elytraActivationCost", 10);

        builder.push("speed");
        SPEED_ENERGY_USE = builder.define("speedEnergyUsePerUnitMoved", 20);
        SPEED_ACTIVATION_COST_I = builder.define("speedActivationCost_l1", 4);
        SPEED_I_BOOST = builder.define("speedBoost_l1", 0.15);
        SPEED_ACTIVATION_COST_II = builder.define("speedActivationCost_l2", 6);
        SPEED_II_BOOST = builder.define("speedBoost_l2", 0.3);
        SPEED_ACTIVATION_COST_III = builder.define("speedActivationCost_l3", 8);
        SPEED_III_BOOST = builder.define("speedBoost_l3", 0.45);
        builder.pop();

        builder.push("jump");
        JUMP_ENERGY_USE = builder.define("jumpEnergyUsePerUnitMoved", 100);
        JUMP_COUNT_I = builder.define("jumpCount_l1", 2);
        JUMP_ACTIVATION_COST_I = builder.define("jumpActivationCost_l1", 6);
        JUMP_COUNT_II = builder.define("jumpCount_l2", 3);
        JUMP_ACTIVATION_COST_II = builder.define("jumpActivationCost_l2", 8);
        builder.pop();

        builder.pop(); // upgrades
        builder.pop(); // dark steel
    }
}
