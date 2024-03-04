package com.enderio.machines.common.config.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MachinesClientBlocksConfig {

    public final ModConfigSpec.ConfigValue<String> VACUUM_CHEST_RANGE_COLOR;
    public final ModConfigSpec.ConfigValue<String> XP_VACUUM_RANGE_COLOR;
    public final ModConfigSpec.ConfigValue<String> POWERED_SPAWNER_RANGE_COLOR;
    public final ModConfigSpec.ConfigValue<String> DRAIN_RANGE_COLOR;

    public MachinesClientBlocksConfig(ModConfigSpec.Builder builder) {
        builder.push("blocks");

        VACUUM_CHEST_RANGE_COLOR = builder.comment("The color of the range box of the Vacuum Chest").define("vacuumChestRangeColor", "0000FF");
        XP_VACUUM_RANGE_COLOR = builder.comment("The color of the range box of the XP Vacuum").define("vacuumXpRangeColor", "00FF00");
        POWERED_SPAWNER_RANGE_COLOR = builder.comment("The color of the range box of the Powered Spawner").define("poweredSpawnerRangeColor", "FF0000");
        DRAIN_RANGE_COLOR = builder.comment("The color of the range box of the Drain").define("drainRangeColor", "FFA500");
        builder.pop();
    }
}
