package com.enderio.enderio.config.machines.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class MachinesClientBlocksConfig {

    public final ForgeConfigSpec.ConfigValue<String> VACUUM_CHEST_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> XP_VACUUM_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> POWERED_SPAWNER_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> DRAIN_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> NIARD_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> INHIBITOR_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> RELOCATOR_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> AVERSION_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> ATTRACTOR_RANGE_COLOR;
    public final ForgeConfigSpec.ConfigValue<String> WIRELESS_CHARGER_RANGE_COLOR;

    public MachinesClientBlocksConfig(ForgeConfigSpec.Builder builder) {
        builder.push("blocks");
        VACUUM_CHEST_RANGE_COLOR = builder.comment("The color of the range box of the Vacuum Chest")
                .define("vacuumChestRangeColor", "0000FF");
        XP_VACUUM_RANGE_COLOR = builder.comment("The color of the range box of the XP Vacuum")
                .define("vacuumXpRangeColor", "00FF00");
        POWERED_SPAWNER_RANGE_COLOR = builder.comment("The color of the range box of the Powered Spawner")
                .define("poweredSpawnerRangeColor", "FF0000");
        DRAIN_RANGE_COLOR = builder.comment("The color of the range box of the Drain")
                .define("drainRangeColor", "FFA500");
        NIARD_RANGE_COLOR = builder.comment("The color of the range box of the Niard")
            .define("niardRangeColor", "FFA500");
        INHIBITOR_RANGE_COLOR = builder.comment("The color of the range box of the Inhibitor Obelisk")
                .define("inhibitorRangeColor", "8B0000");
        RELOCATOR_RANGE_COLOR = builder.comment("The color of the range box of the Relocator Obelisk")
                .define("relocatorRangeColor", "8B0000");
        AVERSION_RANGE_COLOR = builder.comment("The color of the range box of the Aversion Obelisk")
                .define("aversionRangeColor", "8B0000");
        ATTRACTOR_RANGE_COLOR = builder.comment("The color of the range box of the Attractor Obelisk")
                .define("attractorRangeColor", "8B0000");
        WIRELESS_CHARGER_RANGE_COLOR = builder.comment("The color of the range box of the Wireless Charger")
                .define("wirelessChargerRangeColor", "C7CC2A");
        builder.pop();
    }
}
