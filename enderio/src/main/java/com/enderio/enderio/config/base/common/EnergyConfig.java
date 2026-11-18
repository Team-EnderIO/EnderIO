package com.enderio.enderio.config.base.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EnergyConfig {
    public final ModConfigSpec.IntValue ARMORY_DURABILITY_TO_ENERGY_USE;
    public final ModConfigSpec.IntValue ARMORY_DARK_STEEL_CAPACITY;
    public final ModConfigSpec.IntValue ARMORY_END_STEEL_CAPACITY;

    public EnergyConfig(ModConfigSpec.Builder builder) {
        builder.push("energy");

        builder.push("armory");
        ARMORY_DURABILITY_TO_ENERGY_USE = builder.comment("The amount of energy in uI consumed when dark or end steel tools or armor take 1 point of durability damage.")
                .defineInRange("durabilityToEnergyUse", 128, 1, Integer.MAX_VALUE);
        ARMORY_DARK_STEEL_CAPACITY = builder.comment("The base energy capacity of dark steel tools and armor in uI.")
                .defineInRange("darkSteelCapacity", 64_000, 1, Integer.MAX_VALUE);
        ARMORY_END_STEEL_CAPACITY = builder.comment("The base energy capacity of end steel tools and armor in uI.")
                .defineInRange("endSteelCapacity", 128_000, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.pop();
    }
}
