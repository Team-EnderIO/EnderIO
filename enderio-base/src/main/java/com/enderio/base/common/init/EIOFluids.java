package com.enderio.base.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.regilite.fluids.FluidTypeBuilder;
import com.enderio.regilite.fluids.FluidTypeHolder;
import com.enderio.regilite.fluids.RegiliteFluidTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;

// TODO: Fluid behaviours and some cleaning. https://github.com/SleepyTrousers/EnderIO-Rewrite/issues/34

// TODO: Registrate tint color support, it was some reason omitted in my original PR

@SuppressWarnings("unused")
public class EIOFluids {
    private static final RegiliteFluidTypes FLUID_TYPES = EnderIOBase.REGILITE.fluidTypes();

    public static final FluidTypeHolder<FluidType> NUTRIENT_DISTILLATION =
        fluid("nutrient_distillation", "Nutrient Distillation", FluidType.Properties.create().density(1500).viscosity(3000)).finish();

    public static final FluidTypeHolder<FluidType> DEW_OF_THE_VOID =
        fluid("dew_of_the_void", "Fluid of the Void", FluidType.Properties.create().density(200).viscosity(1000).temperature(175)).finish();

    public static final FluidTypeHolder<FluidType> VAPOR_OF_LEVITY =
        gasFluid("vapor_of_levity", "Vapor of Levity", FluidType.Properties.create().density(-10).viscosity(100).temperature(5)).finish();

    public static final FluidTypeHolder<FluidType> HOOTCH =
        fluid("hootch", "Hootch", FluidType.Properties.create().density(900).viscosity(1000)).finish();

    public static final FluidTypeHolder<FluidType> ROCKET_FUEL =
        fluid("rocket_fuel", "Rocket Fuel", FluidType.Properties.create().density(900).viscosity(1000)).finish();

    public static final FluidTypeHolder<FluidType> FIRE_WATER =
        fluid("fire_water", "Fire Water", FluidType.Properties.create().density(900).viscosity(1000).temperature(2000)).finish();

    public static final FluidTypeHolder<FluidType> XP_JUICE =
        fluid("xp_juice", "XP Juice", FluidType.Properties.create().lightLevel(10).density(800).viscosity(1500))
            .tag(EIOTags.Fluids.EXPERIENCE).finish();

    public static final FluidTypeHolder<FluidType> LIQUID_SUNSHINE =
        fluid("liquid_sunshine", "Liquid Sunshine", FluidType.Properties.create().density(200).viscosity(400))
            .tag(EIOTags.Fluids.SOLAR_PANEL_LIGHT).finish();

    public static final FluidTypeHolder<FluidType> CLOUD_SEED =
        fluid("cloud_seed", "Cloud Seed", FluidType.Properties.create().density(500).viscosity(800)).finish();

    public static final FluidTypeHolder<FluidType> CLOUD_SEED_CONCENTRATED =
        fluid("cloud_seed_concentrated", "Cloud Seed Concentrated", FluidType.Properties.create().density(1000).viscosity(1200)).finish();

    private static FluidTypeBuilder<FluidType> fluid(String name, String translation, FluidType.Properties properties) {
        return baseFluid(name, properties)
            .translation(translation)
            .bucket(item -> item
                .tab(EIOCreativeTabs.MAIN)
                .translation(translation + " Bucket"));
    }

    private static FluidTypeBuilder<FluidType> gasFluid(String name, String translation, FluidType.Properties properties) {
        return baseFluid(name, properties)
            .translation(translation)
            .bucket(item -> item
                .tab(EIOCreativeTabs.MAIN)
                .translation(translation + " Bucket"));
    }

    private static FluidTypeBuilder<FluidType> baseFluid(String name, FluidType.Properties properties) {
        return FLUID_TYPES
            .create(name, properties)
            .renderType(() -> RenderType::translucent)
            .block(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));
    }

    public static void register() {
    }
}
