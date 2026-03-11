package com.enderio.enderio.config.base.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class ItemsConfig {
    public final ForgeConfigSpec.DoubleValue ENDERIOS_CHANCE;
    public final ForgeConfigSpec.DoubleValue ENDERIOS_RANGE;

    public final ForgeConfigSpec.IntValue ELECTROMAGNET_ENERGY_USE;
    public final ForgeConfigSpec.IntValue ELECTROMAGNET_MAX_ENERGY;
    public final ForgeConfigSpec.IntValue ELECTROMAGNET_RANGE;
    public final ForgeConfigSpec.IntValue ELECTROMAGNET_MAX_ITEMS;

    public final ForgeConfigSpec.IntValue LEVITATION_STAFF_ENERGY_USE;
    public final ForgeConfigSpec.IntValue LEVITATION_STAFF_MAX_ENERGY;

    public final ForgeConfigSpec.IntValue TRAVELLING_BLINK_RANGE;
    public final ForgeConfigSpec.IntValue TRAVELLING_BLINK_DISABLED_TIME;
    public final ForgeConfigSpec.IntValue TRAVELLING_STAFF_ENERGY_USE;
    public final ForgeConfigSpec.IntValue TRAVELLING_STAFF_MAX_ENERGY;

    public final ForgeConfigSpec.IntValue TRAVELLING_TO_BLOCK_RANGE;
    public final ForgeConfigSpec.IntValue TRAVELLING_BLOCK_TO_BLOCK_RANGE;

    public ItemsConfig(ForgeConfigSpec.Builder builder) {
        builder.push("items");

        builder.push("food");
        ENDERIOS_CHANCE = builder.comment("The chance of enderios teleporting the player").defineInRange("enderioChance", 0.3d, 0d, 1d);
        ENDERIOS_RANGE = builder.comment("The range of an enderio teleport").defineInRange("enderioRange", 16.0d, 4d, 128d);
        builder.pop();

        builder.push("electromagnet");
        ELECTROMAGNET_ENERGY_USE = builder.defineInRange("energyUse", 1, 1, Integer.MAX_VALUE);
        ELECTROMAGNET_MAX_ENERGY = builder.defineInRange("maxEnergy", 100000, 100, Integer.MAX_VALUE);
        ELECTROMAGNET_RANGE = builder.defineInRange("range", 5, 4, 32);
        ELECTROMAGNET_MAX_ITEMS = builder.defineInRange("maxItems", 20, 1, 64);
        builder.pop();

        builder.push("levitationstaff");
        LEVITATION_STAFF_ENERGY_USE = builder.defineInRange("energyUse", 1, 1, Integer.MAX_VALUE);
        LEVITATION_STAFF_MAX_ENERGY = builder.defineInRange("maxEnergy", 10_000, 100, Integer.MAX_VALUE);
        builder.pop();

        builder.push("travelling");
        TRAVELLING_BLINK_RANGE = builder.defineInRange("blinkRange", 24, 4, 16 * 32);
        TRAVELLING_BLINK_DISABLED_TIME = builder.defineInRange("disabledTime", 5, 0, 20 * 60);
        TRAVELLING_STAFF_ENERGY_USE = builder.defineInRange("energyUse", 1000, 1, Integer.MAX_VALUE);
        TRAVELLING_STAFF_MAX_ENERGY = builder.defineInRange("maxEnergy", 100000, 100, Integer.MAX_VALUE);
        builder.comment("the following config values are only used if EIOMachines is loaded");
        TRAVELLING_TO_BLOCK_RANGE = builder.defineInRange("itemToBlockRange", 192, 4, 16 * 32);
        TRAVELLING_BLOCK_TO_BLOCK_RANGE = builder.defineInRange("blockToBlockRange", 96, 4, 16 * 32);
        builder.pop();

        builder.pop();
    }
}
