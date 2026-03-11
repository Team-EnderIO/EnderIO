package com.enderio.enderio.config.machines.common;

import com.enderio.enderio.content.machines.powered_spawner.MobSpawnMode;
import net.minecraftforge.common.ForgeConfigSpec;

public class MachinesCommonConfig {
    public final EnergyConfig ENERGY;
    public final ForgeConfigSpec.ConfigValue<Double> ENCHANTER_LAPIS_COST_FACTOR;
    public final ForgeConfigSpec.ConfigValue<Double> ENCHANTER_LEVEL_COST_FACTOR;
    public final ForgeConfigSpec.ConfigValue<Integer> ENCHANTER_BASE_LEVEL_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> MAX_SPAWNER_ENTITIES;
    public final ForgeConfigSpec.ConfigValue<Integer> MAX_SPAWNERS;
    public final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_SPAWN_ENERGY_COST;
    public final ForgeConfigSpec.ConfigValue<MobSpawnMode> SPAWN_TYPE;
    public final ForgeConfigSpec.IntValue SPAWN_AMOUNT;
    public final ForgeConfigSpec.ConfigValue<Integer> ATTRACTOR_RANGE;
    public final ForgeConfigSpec.ConfigValue<Boolean> ATTRACTOR_PULL_BOSSES;
    public final ForgeConfigSpec.ConfigValue<Integer> INHIBITOR_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> AVERSION_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> RELOCATOR_RANGE;
    public final ForgeConfigSpec.IntValue ENDERFACE_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> WIRELESS_CHARGER_RANGE;

    public MachinesCommonConfig(ForgeConfigSpec.Builder builder) {
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
        builder.pop();

        builder.push("obelisks");
        ATTRACTOR_RANGE = builder.comment("Attractor Obelisk base range").define("attractorRange", 8);
        ATTRACTOR_PULL_BOSSES = builder.comment("Attractor Obelisk attracts bosses")
                .define("attractorAttractBosses", false);
        INHIBITOR_RANGE = builder.comment("Inhibitor Obelisk base range").define("inhibitorRange", 16);
        AVERSION_RANGE = builder.comment("Aversion Obelisk base range").define("aversionRange", 16);
        RELOCATOR_RANGE = builder.comment("Relocator Obelisk base range").define("relocatorRange", 16);
        builder.pop();
    }
}
