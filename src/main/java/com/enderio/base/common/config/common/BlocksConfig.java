package com.enderio.base.common.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class BlocksConfig {
    public final ForgeConfigSpec.ConfigValue<Float> BROKEN_SPAWNER_DROP_CHANCE;

    public final ForgeConfigSpec.ConfigValue<Float> EXPLOSION_RESISTANCE;

    public final ForgeConfigSpec.ConfigValue<Float> DARK_STEEL_LADDER_BOOST;

    public final ForgeConfigSpec.ConfigValue<String> VACUUM_CHEST_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> XP_VACUUM_RANGE_COLOR;

    public BlocksConfig(ForgeConfigSpec.Builder builder) {
        builder.push("blocks");

        builder.push("brokenSpawner");
        BROKEN_SPAWNER_DROP_CHANCE = builder.comment("The chance of a spawner dropping a broken spawner.").define("dropChance", 1.0f);
        builder.pop();

        EXPLOSION_RESISTANCE = builder.comment("The explosion resistance of explosion resistant blocks.").define("explosionResistance", 1200.0f);

        DARK_STEEL_LADDER_BOOST = builder.comment("The speed boost granted by the Dark Steel ladder.").define("darkSteelLadderBoost", 0.15f);

        VACUUM_CHEST_RANGE_COLOR = builder.comment("The color of the range box of Vacuum Chest").define("vacuumChestRangeColor", "0000FF");
        XP_VACUUM_RANGE_COLOR = builder.comment("The color of the range box of XP Vacuum").define("vacuumXpRangeColor", "00FF00");

        builder.pop();
    }
}
