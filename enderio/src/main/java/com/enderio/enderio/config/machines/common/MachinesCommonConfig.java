package com.enderio.enderio.config.machines.common;

import com.enderio.enderio.content.machines.powered_spawner.MobSpawnMode;
import net.neoforged.neoforge.common.ModConfigSpec;

public class MachinesCommonConfig {
    public final EnergyConfig ENERGY;
    public final ModConfigSpec.DoubleValue ENCHANTER_LAPIS_COST_FACTOR;
    public final ModConfigSpec.DoubleValue ENCHANTER_LEVEL_COST_FACTOR;
    public final ModConfigSpec.IntValue ENCHANTER_BASE_LEVEL_COST;
    public final ModConfigSpec.IntValue MAX_SPAWNER_ENTITIES;
    public final ModConfigSpec.IntValue MAX_SPAWNERS;
    public final ModConfigSpec.IntValue DEFAULT_SPAWN_ENERGY_COST;
    public final ModConfigSpec.ConfigValue<MobSpawnMode> SPAWN_TYPE;
    public final ModConfigSpec.IntValue SPAWN_AMOUNT;
    public final ModConfigSpec.IntValue ATTRACTOR_RANGE;
    public final ModConfigSpec.BooleanValue ATTRACTOR_PULL_BOSSES;
    public final ModConfigSpec.IntValue INHIBITOR_RANGE;
    public final ModConfigSpec.IntValue AVERSION_RANGE;
    public final ModConfigSpec.IntValue RELOCATOR_RANGE;
    public final ModConfigSpec.IntValue ENDERFACE_RANGE;
    public final ModConfigSpec.IntValue WIRELESS_CHARGER_RANGE;

    public MachinesCommonConfig(ModConfigSpec.Builder builder) {
        ENERGY = new EnergyConfig(builder);

        builder.push("enchanter");
        ENCHANTER_LAPIS_COST_FACTOR = builder.comment("The lapis cost is enchant level multiplied by this value.")
                .defineInRange("lapisCostFactor", 3.0, 1.0, Double.MAX_VALUE);
        ENCHANTER_LEVEL_COST_FACTOR = builder.comment(
                "The final XP cost for an enchantment is multiplied by this value. To halve costs set to 0.5, to double them set it to 2.")
                .defineInRange("levelCostFactor", 0.75, 0.0, Double.MAX_VALUE);
        ENCHANTER_BASE_LEVEL_COST = builder.comment("Base level cost added to all recipes in the enchanter.")
                .defineInRange("baseLevelCost", 2, 0, Integer.MAX_VALUE);
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
        WIRELESS_CHARGER_RANGE = builder.comment("Base range").defineInRange("baseRange", 16, 0, Integer.MAX_VALUE);
        builder.pop();

        builder.push("obelisks");
        ATTRACTOR_RANGE = builder.comment("Attractor Obelisk base range").defineInRange("attractorRange", 8, 0, Integer.MAX_VALUE);
        ATTRACTOR_PULL_BOSSES = builder.comment("Attractor Obelisk attracts bosses")
                .define("attractorAttractBosses", false);
        INHIBITOR_RANGE = builder.comment("Inhibitor Obelisk base range").defineInRange("inhibitorRange", 16, 0, Integer.MAX_VALUE);
        AVERSION_RANGE = builder.comment("Aversion Obelisk base range").defineInRange("aversionRange", 16, 0, Integer.MAX_VALUE);
        RELOCATOR_RANGE = builder.comment("Relocator Obelisk base range").defineInRange("relocatorRange", 16, 0, Integer.MAX_VALUE);
        builder.pop();
    }
}
