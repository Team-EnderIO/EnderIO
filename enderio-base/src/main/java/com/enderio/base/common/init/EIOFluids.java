package com.enderio.base.common.init;

import com.enderio.base.EnderIO;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

// TODO: A lot of this is dummy stuff to get the fluid into the game. This will need overhauling sometime to add behaviours.
public class EIOFluids {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final FluidEntry<? extends ForgeFlowingFluid> NUTRIENT_DISTILLATION = basicFluid("nutrient_distillation")
        .attributes(attrs -> attrs.density(1500).viscosity(3000))
        .bucket()
        .tab(() -> EIOCreativeTabs.MAIN)
        .build()
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> DEW_OF_THE_VOID = basicFluid("dew_of_the_void")
        .attributes(attrs -> attrs.density(200).viscosity(1000).temperature(175))
        .lang("Fluid of the Void")
        .bucket()
        .tab(() -> EIOCreativeTabs.MAIN)
        .build()
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> VAPOR_OF_LEVITY = basicFluid("vapor_of_levity")
        .attributes(attrs -> attrs.density(-10).viscosity(100).temperature(5).gaseous())
        .bucket()
        .tab(() -> EIOCreativeTabs.MAIN)
        .build()
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> HOOTCH = basicFluid("hootch")
        .attributes(attrs -> attrs.density(900).viscosity(1000))
        .bucket()
        .tab(() -> EIOCreativeTabs.MAIN)
        .build()
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> ROCKET_FUEL = basicFluid("rocket_fuel")
        .attributes(attrs -> attrs.density(900).viscosity(1000))
        .bucket()
        .tab(() -> EIOCreativeTabs.MAIN)
        .build()
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> FIRE_WATER = basicFluid("fire_water")
        .attributes(attrs -> attrs.density(900).viscosity(1000).temperature(2000))
        .bucket()
        .tab(() -> EIOCreativeTabs.MAIN)
        .build()
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> XP_JUICE = basicFluid("xp_juice")
        .attributes(attrs -> attrs.luminosity(10).density(800).viscosity(1500))
        .bucket()
        .tab(() -> EIOCreativeTabs.MAIN)
        .build()
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> LIQUID_SUNSHINE = basicFluid("liquid_sunshine")
        .attributes(attrs -> attrs.density(200).viscosity(400))
        .bucket()
        .tab(() -> EIOCreativeTabs.MAIN)
        .build()
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> CLOUD_SEED = basicFluid("cloud_seed")
        .attributes(attrs -> attrs.density(500).viscosity(800))
        .bucket()
        .tab(() -> EIOCreativeTabs.MAIN)
        .build()
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> CLOUD_SEED_CONCENTRATED = basicFluid("cloud_seed_concentrated")
        .attributes(attrs -> attrs.density(1000).viscosity(1200))
        .bucket()
        .tab(() -> EIOCreativeTabs.MAIN)
        .build()
        .register();

    private static FluidBuilder<? extends ForgeFlowingFluid, Registrate> basicFluid(String name) {
        return REGISTRATE.fluid(name, EnderIO.loc("block/fluid_" + name + "_still"),
            EnderIO.loc("block/fluid_" + name + "_flowing"))
            .source(ForgeFlowingFluid.Source::new)
            .block()
            .addLayer(() -> RenderType::translucent)
            .build();
    }

    public static void register() {}
}
