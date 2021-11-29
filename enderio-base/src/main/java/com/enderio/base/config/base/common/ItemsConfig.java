package com.enderio.base.config.base.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class ItemsConfig {

    public final ForgeConfigSpec.ConfigValue<Float> ENDERIOS_CHANCE;
    public final ForgeConfigSpec.ConfigValue<Float> ENDERIOS_RANGE;

    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_ENERGY_USE;
    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_MAX_ENERGY;
    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_MAX_ITEMS;

    public final ForgeConfigSpec.ConfigValue<Integer> LEVITATION_STAFF_ENERGY_USE;
    public final ForgeConfigSpec.ConfigValue<Integer> LEVITATION_STAFF_MAX_ENERGY;

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
    public final ForgeConfigSpec.ConfigValue<Integer> SPOON_ACTIVATION_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> FORK_ACTIVATION_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> DIRECT_ACTIVATION_COST;


    public ItemsConfig(ForgeConfigSpec.Builder builder) {
        builder.push("items");

        builder.push("food");
        ENDERIOS_CHANCE = builder.comment("The chance of enderios teleporting the player").define("enderioChance", 0.3f);
        ENDERIOS_RANGE = builder.comment("The range of an enderio teleport").define("enderioRange", 16.0f);
        builder.pop();

        builder.push("electromagnet");
        ELECTROMAGNET_ENERGY_USE = builder.define("energyUse", 1);
        ELECTROMAGNET_MAX_ENERGY = builder.define("maxEnergy", 100000);
        ELECTROMAGNET_RANGE = builder.define("range", 5);
        ELECTROMAGNET_MAX_ITEMS = builder.define("maxItems", 20);
        // TODO: Item blacklist
        builder.pop();

        builder.push("levitationstaff");
        LEVITATION_STAFF_ENERGY_USE = builder.define("energyUse", 1);
        LEVITATION_STAFF_MAX_ENERGY = builder.define("maxEnergy", 1000);
        builder.pop();

        builder.push("darksteelaxe");
        DARK_STEEL_AXE_ENERGY_PER_FELLED_LOG = builder.define("energyUsePerFelledLog", 1500);
        builder.pop();

        builder.push("darksteelpickaxe");
        DARK_STEEL_PICKAXE_OBSIDIAN_ENERGY_COST = builder.define("obsidianBreakPowerUse", 50);
        DARK_STEEL_PICKAXE_OBSIDIAN_SPEED = builder.define("speedBoostWhenObsidian", 50);
        DARK_STEEL_PICKAXE_AS_OBSIDIAN_AT_HARDNESS = builder.define("useObsidianBreakSpeedAtHardness", 50);
        builder.pop();

        builder.push("darksteel.upgrades");
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
        SPOON_ACTIVATION_COST = builder.define("spoonActivationCost", 4);
        FORK_ACTIVATION_COST = builder.define("forkActivationCost", 4);
        DIRECT_ACTIVATION_COST = builder.define("directActivationCost", 4);
        builder.pop();
    }
}
