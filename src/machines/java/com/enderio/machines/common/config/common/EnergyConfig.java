package com.enderio.machines.common.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class EnergyConfig {
    public final ForgeConfigSpec.ConfigValue<Integer> ALLOY_SMELTER_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> ALLOY_SMELTER_USAGE;
    public final ForgeConfigSpec.ConfigValue<Integer> CRAFTER_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> CRAFTER_USAGE;
    public final ForgeConfigSpec.ConfigValue<Integer> IMPULSE_HOPPER_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> IMPULSE_HOPPER_USAGE;
    public final ForgeConfigSpec.ConfigValue<Integer> POWERED_SPAWNER_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> POWERED_SPAWNER_USAGE;
    public final ForgeConfigSpec.ConfigValue<Integer> SAG_MILL_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> SAG_MILL_USAGE;
    public final ForgeConfigSpec.ConfigValue<Integer> SLICER_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> SLICER_USAGE;
    public final ForgeConfigSpec.ConfigValue<Integer> SOUL_BINDER_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> SOUL_BINDER_USAGE;
    public final ForgeConfigSpec.ConfigValue<Integer> STIRLING_GENERATOR_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> STIRLING_GENERATOR_BURN_SPEED;
    public final ForgeConfigSpec.ConfigValue<Integer> STIRLING_GENERATOR_PRODUCTION;
    public final ForgeConfigSpec.ConfigValue<Integer> PAINTING_MACHINE_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> PAINTING_MACHINE_USAGE;
    public final ForgeConfigSpec.ConfigValue<Integer> PAINTING_MACHINE_ENERGY_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> SIMPLE_SOLAR_PANEL_MAX_PRODUCTION;
    public final ForgeConfigSpec.ConfigValue<Integer> BASIC_SOLAR_PANEL_MAX_PRODUCTION;
    public final ForgeConfigSpec.ConfigValue<Integer> ADVANCED_SOLAR_PANEL_MAX_PRODUCTION;
    public final ForgeConfigSpec.ConfigValue<Integer> VIBRANT_SOLAR_PANEL_MAX_PRODUCTION;
    public final ForgeConfigSpec.ConfigValue<Integer> WIRED_CHARGER_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> WIRED_CHARGER_USAGE;

    public EnergyConfig(ForgeConfigSpec.Builder builder) {
        builder.push("energy");

        builder.push("alloySmelter");
            ALLOY_SMELTER_CAPACITY = builder.comment("The base energy capacity in uI.").defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
            ALLOY_SMELTER_USAGE = builder.comment("The base energy consumption in uI/t.").defineInRange("usage", 30, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("crafter");
            CRAFTER_CAPACITY = builder.comment("The base energy capacity in uI.").defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
            CRAFTER_USAGE = builder.comment("The base energy consumption in uI/t.").defineInRange("usage", 10, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("impulseHopper");
            IMPULSE_HOPPER_CAPACITY = builder.comment("The base energy capacity in uI.").defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
            IMPULSE_HOPPER_USAGE = builder.comment("The base energy consumption in uI/t.").defineInRange("usage", 16, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("poweredSpawner");
            POWERED_SPAWNER_CAPACITY = builder.comment("The base energy capacity in uI.").defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
            POWERED_SPAWNER_USAGE = builder.comment("The base energy consumption in uI/t.").defineInRange("usage", 160, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("sagMill");
            SAG_MILL_CAPACITY = builder.comment("The base energy capacity in uI.").defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
            SAG_MILL_USAGE = builder.comment("The base energy consumption in uI/t.").defineInRange("usage", 30, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("slicer");
            SLICER_CAPACITY = builder.comment("The base energy capacity in uI.").defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
            SLICER_USAGE = builder.comment("The base energy consumption in uI/t.").defineInRange("usage", 30, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("soulBinder");
            SOUL_BINDER_CAPACITY = builder.comment("The base energy capacity in uI.").defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
            SOUL_BINDER_USAGE = builder.comment("The base energy consumption in uI/t.").defineInRange("usage", 30, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("stirlingGenerator");
            STIRLING_GENERATOR_CAPACITY = builder.comment("The base energy capacity in uI.").defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
            STIRLING_GENERATOR_BURN_SPEED = builder.comment("The base number of 'burn ticks' performed per machine tick.").defineInRange("burnSpeed", 1, 1, Integer.MAX_VALUE);
            STIRLING_GENERATOR_PRODUCTION = builder.comment("The base amount of energy produced in uI/t.").defineInRange("generation", 40, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("paintingMachine");
            PAINTING_MACHINE_CAPACITY = builder.comment("The base energy capacity in uI.").defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
            PAINTING_MACHINE_USAGE = builder.comment("The base energy consumption in uI/t.").defineInRange("usage", 30, 1, Integer.MAX_VALUE);
            PAINTING_MACHINE_ENERGY_COST = builder.comment("The energy required for each painting operation")
                .defineInRange("energyCost", 2000, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("phtovoltaic_cell_rates");
            builder.comment("Production rate at midday without rain or thunder");
            SIMPLE_SOLAR_PANEL_MAX_PRODUCTION = builder.defineInRange("simple", 10, 1, Integer.MAX_VALUE);
            BASIC_SOLAR_PANEL_MAX_PRODUCTION = builder.defineInRange("basic", 40, 1, Integer.MAX_VALUE);
            ADVANCED_SOLAR_PANEL_MAX_PRODUCTION = builder.defineInRange("advanced", 80, 1, Integer.MAX_VALUE);
            VIBRANT_SOLAR_PANEL_MAX_PRODUCTION = builder.defineInRange("vibrant", 160, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("wiredCharger");
            WIRED_CHARGER_CAPACITY = builder.comment("The base energy capacity in uI.").defineInRange("capacity", 1000000, 1, Integer.MAX_VALUE);
            WIRED_CHARGER_USAGE = builder.comment("The base energy consumption in uI/t.").defineInRange("usage", 100, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.pop();
    }
}
