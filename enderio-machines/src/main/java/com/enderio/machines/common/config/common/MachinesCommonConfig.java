package com.enderio.machines.common.config.common;

import com.enderio.machines.common.blocks.powered_spawner.MobSpawnMode;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MachinesCommonConfig {
    public final EnergyConfig ENERGY;
    public final ModConfigSpec.ConfigValue<Double> ENCHANTER_LAPIS_COST_FACTOR;
    public final ModConfigSpec.ConfigValue<Double> ENCHANTER_LEVEL_COST_FACTOR;
    public final ModConfigSpec.ConfigValue<Integer> ENCHANTER_BASE_LEVEL_COST;
    public final ModConfigSpec.ConfigValue<Integer> MAX_SPAWNER_ENTITIES;
    public final ModConfigSpec.ConfigValue<Integer> MAX_SPAWNERS;
    public final ModConfigSpec.ConfigValue<Integer> DEFAULT_SPAWN_ENERGY_COST;
    public final ModConfigSpec.ConfigValue<MobSpawnMode> SPAWN_TYPE;
    public final ModConfigSpec.IntValue SPAWN_AMOUNT;
    public final ModConfigSpec.IntValue ENDERFACE_RANGE;
    public final ModConfigSpec.ConfigValue<Integer> WIRELESS_CHARGER_RANGE;
    public final ModConfigSpec.ConfigValue<Integer> WIRELESS_CHARGER_ANTENNA_RANGE;
    public final ModConfigSpec.ConfigValue<Integer> WIRELESS_CHARGER_ANTENNA_RANGE_2;

    public MachinesCommonConfig(ModConfigSpec.Builder builder) {
        ENERGY = new EnergyConfig(builder);

        builder.push("enchanter");
        ENCHANTER_LAPIS_COST_FACTOR = builder.comment("The lapis cost is enchant level multiplied by this value.")
                .define("lapisCostFactor", 3.0d);
        ENCHANTER_LEVEL_COST_FACTOR = builder.comment(
                "The final XP cost for an enchantment is multiplied by this value. To halve costs set to 0.5, to double them set it to 2.")
                .define("levelCostFactor", 0.75d);
        ENCHANTER_BASE_LEVEL_COST = builder.comment("Base level cost added to all recipes in the enchanter.")
                .define("baseLevelCost", 2);
        builder.pop();

        builder.push("poweredSpawner");
        SPAWN_AMOUNT = builder.comment("The amount of mobs that spawn from the spawner")
                .defineInRange("spawnAmount", 4, 0, Integer.MAX_VALUE);
        MAX_SPAWNER_ENTITIES = builder.comment("The amount of entities that will turn off powered spawner if in range.")
                .defineInRange("maxEntities", 2, 0, Integer.MAX_VALUE);
        SPAWN_TYPE = builder.comment(
                "The way a powered spawner spawns an entity. Possible options: 'NEW' Spawns the same entity types as the soul vial. 'COPY' Spawns an exact copy of the mob in the soul vial")
                .defineEnum("spawnType", MobSpawnMode.NEW);
        MAX_SPAWNERS = builder
                .comment("The maximum amount of spawners before the spawners suffers a loss of efficiency")
                .defineInRange("maxSpawners", 10, 0, Integer.MAX_VALUE);
        DEFAULT_SPAWN_ENERGY_COST = builder
                .comment("The amount of energy used to spawn mobs that do not have custom spawner soul data.")
                .defineInRange("defaultSpawnEnergyCost", 50000, 0, Integer.MAX_VALUE);
        builder.pop();

        builder.push("enderface");
        ENDERFACE_RANGE = builder.comment("Maximum distance from which an Ender IO can interact with blocks")
                .defineInRange("enderIoRange", 8, 1, 20);
        builder.pop();

        builder.push("wirelessCharger");
        WIRELESS_CHARGER_RANGE = builder.comment("Base range").define("baseRange", 16);
        WIRELESS_CHARGER_ANTENNA_RANGE = builder.comment("Pulsating Antenna range")
                .define("pulsatingRangeExtension", 16);
        WIRELESS_CHARGER_ANTENNA_RANGE_2 = builder.comment("Vibrant Antenna range").define("vibrantRangeExtension", 32);
        builder.pop();
    }
}
