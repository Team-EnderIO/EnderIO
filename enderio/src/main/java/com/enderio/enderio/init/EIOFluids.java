package com.enderio.enderio.init;

import com.enderio.core.common.registries.FluidDeferredHolders;
import com.enderio.core.common.registries.FluidDeferredRegister;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.misc_blocks.FireWaterFluid;
import net.neoforged.bus.api.IEventBus;

// TODO: Fluid behaviours and some cleaning. https://github.com/SleepyTrousers/EnderIO-Rewrite/issues/34

@SuppressWarnings("unused")
public class EIOFluids {

    public static final FluidDeferredRegister FLUIDS = FluidDeferredRegister.create(EnderIO.MOD_ID);

    public static final FluidDeferredHolders NUTRIENT_DISTILLATION = FLUIDS
        .builder("nutrient_distillation")
        .fluidProperties(p -> p.density(1500).viscosity(3000))
        .defaultBucket()
        .register();

    public static final FluidDeferredHolders DEW_OF_THE_VOID = FLUIDS
        .builder("dew_of_the_void")
        .fluidProperties(p -> p.lightLevel(3).density(200).viscosity(1000).temperature(175))
        .blockProperties(p -> p.lightLevel((state) -> 3))
        .defaultBucket()
        .register();

    public static final FluidDeferredHolders VAPOR_OF_LEVITY = FLUIDS
        .builder("vapor_of_levity")
        .fluidProperties(p -> p.density(-10).viscosity(100).temperature(5))
        .defaultBucket()
        .register();

    public static final FluidDeferredHolders HOOTCH = FLUIDS
        .builder("hootch")
        .fluidProperties(p -> p.density(900).viscosity(1000))
        .defaultBucket()
        .register();

    public static final FluidDeferredHolders ROCKET_FUEL = FLUIDS
        .builder("rocket_fuel")
        .fluidProperties(p -> p.density(900).viscosity(1000))
        .defaultBucket()
        .register();

    public static final FluidDeferredHolders FIRE_WATER = FLUIDS
        .builder("fire_water")
        .customFluid(FireWaterFluid.Source::new, FireWaterFluid.Flowing::new)
        .fluidProperties(p -> p.lightLevel(5).density(900).viscosity(1000).temperature(2000))
        .blockProperties(p -> p.lightLevel((state) -> 5))
        .defaultBucket()
        .register();

    public static final FluidDeferredHolders XP_JUICE = FLUIDS
        .builder("xp_juice")
        .fluidProperties(p -> p.lightLevel(10).density(800).viscosity(1500))
        .blockProperties(p -> p.lightLevel((state) -> 10))
        .defaultBucket()
        .register();

    public static final FluidDeferredHolders LIQUID_SUNSHINE = FLUIDS
        .builder("liquid_sunshine")
        .fluidProperties(p -> p.density(200).viscosity(400).lightLevel(15).temperature(300))
        .blockProperties(p -> p.lightLevel((state) -> 15))
        .defaultBucket()
        .register();

    // TODO: Give blindness effect when swimming?
    public static final FluidDeferredHolders LIQUID_DARKNESS = FLUIDS
        .builder("liquid_darkness")
        .fluidProperties(p -> p.density(500).viscosity(900).temperature(100))
        .defaultBucket()
        .register();

    public static final FluidDeferredHolders CLOUD_SEED = FLUIDS
        .builder("cloud_seed")
        .fluidProperties(p -> p.density(500).viscosity(800))
        .defaultBucket()
        .register();

    public static final FluidDeferredHolders CLOUD_SEED_CONCENTRATED = FLUIDS
        .builder("cloud_seed_concentrated")
        .fluidProperties(p -> p.density(1000).viscosity(1200))
        .defaultBucket()
        .register();

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
    }
}
