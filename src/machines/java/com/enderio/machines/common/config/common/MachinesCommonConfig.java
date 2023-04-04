package com.enderio.machines.common.config.common;

import com.enderio.machines.common.blockentity.task.SpawnTask;
import net.minecraftforge.common.ForgeConfigSpec;

public class MachinesCommonConfig {
    public final ForgeConfigSpec.ConfigValue<Float> ENCHANTER_LAPIS_COST_FACTOR;
    public final ForgeConfigSpec.ConfigValue<Float> ENCHANTER_LEVEL_COST_FACTOR;
    public final ForgeConfigSpec.ConfigValue<Integer> ENCHANTER_BASE_LEVEL_COST;
    public final ForgeConfigSpec.ConfigValue<Integer> MAX_SPAWNER_ENTITIES;
    public final ForgeConfigSpec.ConfigValue<Integer> MAX_SPAWNERS;

    public final ForgeConfigSpec.ConfigValue<SpawnTask.SpawnType> SPAWN_TYPE;

    public MachinesCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.push("enchanter");
        ENCHANTER_LAPIS_COST_FACTOR = builder.comment("The lapis cost is enchant level multiplied by this value.").define("lapisCostFactor", 3.0f);
        ENCHANTER_LEVEL_COST_FACTOR = builder.comment("The final XP cost for an enchantment is multiplied by this value. To halve costs set to 0.5, to double them set it to 2.").define("levelCostFactor", 0.75f);
        ENCHANTER_BASE_LEVEL_COST = builder.comment("Base level cost added to all recipes in the enchanter.").define("baseLevelCost", 2);
        MAX_SPAWNER_ENTITIES = builder.comment("The amount of entities that will turn off powered spawner if in range.").define("maxentities", 2);
        SPAWN_TYPE = builder.comment("The way a powered spawner spawns an entity. Possible options: 'ENTITYPE' Spawns the same entity types as the soul vial. 'COPY' Spawns an exact copy of the mob in the soul vial").define("Spawn Type", SpawnTask.SpawnType.ENTITYTYPE);
        MAX_SPAWNERS = builder.comment("The maximum amount of spawners before the spawners suffers a loss of efficiency").define("maxspawners", 10);
        builder.pop();
    }
}
