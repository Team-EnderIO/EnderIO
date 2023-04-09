package com.enderio.machines.common.config.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class MachinesClientBlocksConfig {

    public final ForgeConfigSpec.ConfigValue<String> VACUUM_CHEST_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> XP_VACUUM_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> POWERED_SPAWNER_RANGE_COLOR;

    public MachinesClientBlocksConfig(ForgeConfigSpec.Builder builder) {
        builder.push("blocks");

        VACUUM_CHEST_RANGE_COLOR = builder.comment("The color of the range box of Vacuum Chest").define("vacuumChestRangeColor", "0000FF");
        XP_VACUUM_RANGE_COLOR = builder.comment("The color of the range box of XP Vacuum").define("vacuumXpRangeColor", "00FF00");
        POWERED_SPAWNER_RANGE_COLOR = builder.comment("The color of the range box of Powered Spawner").define("poweredSpawnerRangeColor", "FF0000");
        builder.pop();
    }
}
