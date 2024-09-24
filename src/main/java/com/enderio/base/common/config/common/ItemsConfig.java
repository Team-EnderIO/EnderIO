package com.enderio.base.common.config.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class ItemsConfig {
    public final ForgeConfigSpec.ConfigValue<Double> ENDERIOS_CHANCE;
    public final ForgeConfigSpec.ConfigValue<Double> ENDERIOS_RANGE;

    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_ENERGY_USE;
    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_MAX_ENERGY;
    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> ELECTROMAGNET_MAX_ITEMS;

    public final ForgeConfigSpec.ConfigValue<Integer> LEVITATION_STAFF_ENERGY_USE;
    public final ForgeConfigSpec.ConfigValue<Integer> LEVITATION_STAFF_MAX_ENERGY;

    public final ForgeConfigSpec.ConfigValue<Integer> TRAVELLING_BLINK_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> TRAVELLING_BLINK_DISABLED_TIME;
    public final ForgeConfigSpec.ConfigValue<Integer> TRAVELLING_STAFF_ENERGY_USE;
    public final ForgeConfigSpec.ConfigValue<Integer> TRAVELLING_STAFF_MAX_ENERGY;

    public final ForgeConfigSpec.ConfigValue<Integer> TRAVELLING_TO_BLOCK_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> TRAVELLING_BLOCK_TO_BLOCK_RANGE;

    public ItemsConfig(ForgeConfigSpec.Builder builder) {
        builder.push("items");

        builder.push("food");
        ENDERIOS_CHANCE = builder.comment("The chance of enderios teleporting the player").define("enderioChance", 0.3d);
        ENDERIOS_RANGE = builder.comment("The range of an enderio teleport").define("enderioRange", 16.0d);
        builder.pop();

        builder.push("electromagnet");
        ELECTROMAGNET_ENERGY_USE = builder.define("energyUse", 1);
        ELECTROMAGNET_MAX_ENERGY = builder.define("maxEnergy", 100000);
        ELECTROMAGNET_RANGE = builder.define("range", 5);
        ELECTROMAGNET_MAX_ITEMS = builder.define("maxItems", 20);
        builder.pop();

        builder.push("levitationstaff");
        LEVITATION_STAFF_ENERGY_USE = builder.define("energyUse", 1);
        LEVITATION_STAFF_MAX_ENERGY = builder.define("maxEnergy", 10_000);
        builder.pop();

        builder.push("travelling");
        TRAVELLING_BLINK_RANGE = builder.defineInRange("blinkRange", 24, 4, 16 * 32);
        TRAVELLING_BLINK_DISABLED_TIME = builder.defineInRange("disabledTime", 5, 0, 20 * 60);
        TRAVELLING_STAFF_ENERGY_USE = builder.define("energyUse", 1000);
        TRAVELLING_STAFF_MAX_ENERGY = builder.define("maxEnergy", 100000);
        builder.comment("the following config values are only used if EIOMachines is loaded");
        TRAVELLING_TO_BLOCK_RANGE = builder.defineInRange("itemToBlockRange", 192, 4, 16 * 32);
        TRAVELLING_BLOCK_TO_BLOCK_RANGE = builder.defineInRange("blockToBlockRange", 96, 4, 16 * 32);
        builder.pop();

        builder.pop();
    }
}
