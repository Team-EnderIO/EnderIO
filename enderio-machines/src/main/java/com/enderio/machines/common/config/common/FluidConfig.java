package com.enderio.machines.common.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.fluids.FluidType;

public class FluidConfig {

    public final ModConfigSpec.ConfigValue<Integer> FLUID_TANK_MAX_CAPACITY;
    public final ModConfigSpec.ConfigValue<Integer> PRESSURIZED_FLUID_TANK_MAX_CAPACITY;

    public FluidConfig(ModConfigSpec.Builder builder) {

        builder.push("fluid");

        builder.push("fluidTankCapacity");
        FLUID_TANK_MAX_CAPACITY = builder
            .comment("Maximum amount of fluid in buckets that the base fluid tank can hold")
            .defineInRange("baseFluidTankCapacity", 16, 1, Integer.MAX_VALUE / FluidType.BUCKET_VOLUME);
        PRESSURIZED_FLUID_TANK_MAX_CAPACITY = builder
            .comment("Maximum amount of fluid in buckets that the pressurized fluid tank can hold")
            .defineInRange("pressurizedFluidTankCapacity", 32, 1, Integer.MAX_VALUE / FluidType.BUCKET_VOLUME);
        builder.pop();
        builder.pop();
    }
}
