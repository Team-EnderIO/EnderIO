package com.enderio.machines.common.config.common;

import com.enderio.machines.common.blockentity.task.SpawnerMachineTask;
import net.minecraftforge.common.ForgeConfigSpec;

public class MachinesCommonConfig {
    public final EnergyConfig ENERGY;
    public final ForgeConfigSpec.ConfigValue<Float> ENCHANTER_LAPIS_COST_FACTOR;
    public final ForgeConfigSpec.ConfigValue<Float> ENCHANTER_LEVEL_COST_FACTOR;
    public final ForgeConfigSpec.ConfigValue<Integer> ENCHANTER_BASE_LEVEL_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> MAX_SPAWNER_ENTITIES;
    public final ForgeConfigSpec.ConfigValue<Integer> MAX_SPAWNERS;
    public final ForgeConfigSpec.ConfigValue<SpawnerMachineTask.SpawnType> SPAWN_TYPE;

    public final ForgeConfigSpec.ConfigValue<Integer> PAINTING_MACHINE_ENERGY_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> SIMPLE_SOLAR_PANEL_MAX_PRODUCTION;
    public final ForgeConfigSpec.ConfigValue<Integer> BASIC_SOLAR_PANEL_MAX_PRODUCTION;
    public final ForgeConfigSpec.ConfigValue<Integer> ADVANCED_SOLAR_PANEL_MAX_PRODUCTION;
    public final ForgeConfigSpec.ConfigValue<Integer> VIBRANT_SOLAR_PANEL_MAX_PRODUCTION;
    public final ForgeConfigSpec.ConfigValue<Integer> BASIC_CAPACITOR_BANK_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> ADVANCED_CAPACITOR_BANK_CAPACITY;
    public final ForgeConfigSpec.ConfigValue<Integer> VIBRANT_CAPACITOR_BANK_CAPACITY;


    public MachinesCommonConfig(ForgeConfigSpec.Builder builder) {
        ENERGY = new EnergyConfig(builder);

        builder.push("enchanter");
            ENCHANTER_LAPIS_COST_FACTOR = builder.comment("The lapis cost is enchant level multiplied by this value.").define("lapisCostFactor", 3.0f);
            ENCHANTER_LEVEL_COST_FACTOR = builder.comment("The final XP cost for an enchantment is multiplied by this value. To halve costs set to 0.5, to double them set it to 2.").define("levelCostFactor", 0.75f);
            ENCHANTER_BASE_LEVEL_COST = builder.comment("Base level cost added to all recipes in the enchanter.").define("baseLevelCost", 2);
        builder.pop();

        MAX_SPAWNER_ENTITIES = builder.comment("The amount of entities that will turn off powered spawner if in range.").define("maxentities", 2);
        SPAWN_TYPE = builder.comment("The way a powered spawner spawns an entity. Possible options: 'ENTITYPE' Spawns the same entity types as the soul vial. 'COPY' Spawns an exact copy of the mob in the soul vial").define("Spawn Type", SpawnerMachineTask.SpawnType.ENTITYTYPE);
        MAX_SPAWNERS = builder.comment("The maximum amount of spawners before the spawners suffers a loss of efficiency").define("maxspawners", 10);
PAINTING_MACHINE_ENERGY_COST = builder.comment("The energy required for each painting operation").defineInRange("paintingMachineEnergyCost", 2000, 1, Integer.MAX_VALUE);
        builder.push("phtovoltaic_cell_rates");
            builder.comment("Production rate at midday without rain or thunder");
            SIMPLE_SOLAR_PANEL_MAX_PRODUCTION = builder.defineInRange("simple", 10, 1, Integer.MAX_VALUE);
            BASIC_SOLAR_PANEL_MAX_PRODUCTION = builder.defineInRange("basic", 40, 1, Integer.MAX_VALUE);
            ADVANCED_SOLAR_PANEL_MAX_PRODUCTION = builder.defineInRange("advanced", 80, 1, Integer.MAX_VALUE);
            VIBRANT_SOLAR_PANEL_MAX_PRODUCTION = builder.defineInRange("vibrant", 160, 1, Integer.MAX_VALUE);
        builder.pop();
        builder.push("capacitor_bank_capacity");
            builder.comment("Capacity for capacitor banks");
            BASIC_CAPACITOR_BANK_CAPACITY = builder.defineInRange("basic", 1_000_000, 1, Integer.MAX_VALUE);
            ADVANCED_CAPACITOR_BANK_CAPACITY = builder.defineInRange("advanced", 5_000_000, 1, Integer.MAX_VALUE);
            VIBRANT_CAPACITOR_BANK_CAPACITY = builder.defineInRange("vibrant", 25_000_000, 1, Integer.MAX_VALUE);
        builder.pop();
    }
}
