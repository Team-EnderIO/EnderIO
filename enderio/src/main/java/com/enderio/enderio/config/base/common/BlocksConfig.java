package com.enderio.enderio.config.base.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class BlocksConfig {
    public final ForgeConfigSpec.DoubleValue BROKEN_SPAWNER_DROP_CHANCE;
    public final ForgeConfigSpec.DoubleValue EXPLOSION_RESISTANCE;
    public final ForgeConfigSpec.DoubleValue DARK_STEEL_LADDER_BOOST;

    public BlocksConfig(ForgeConfigSpec.Builder builder) {
        builder.push("blocks");

        builder.push("brokenSpawner");
        BROKEN_SPAWNER_DROP_CHANCE = builder.comment("The chance of a spawner dropping a broken spawner.").defineInRange("dropChance", 1.0d, 0.0d, 1.0d);
        builder.pop();

        EXPLOSION_RESISTANCE = builder.comment("The explosion resistance of explosion resistant blocks.").defineInRange("explosionResistance", 1200.0d, 0.0d, Double.MAX_VALUE);

        DARK_STEEL_LADDER_BOOST = builder.comment("The speed boost granted by the Dark Steel ladder.").defineInRange("darkSteelLadderBoost", 0.15d, 0.0d, 1.0d);
        builder.pop();
    }
}
