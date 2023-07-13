package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.versions.forge.ForgeVersion;

// TODO: Fluid behaviours and some cleaning. https://github.com/SleepyTrousers/EnderIO-Rewrite/issues/34

// TODO: Registrate tint color support, it was some reason omitted in my original PR

@SuppressWarnings("unused")
public class EIOFluids {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final FluidEntry<? extends ForgeFlowingFluid> NUTRIENT_DISTILLATION = fluid("nutrient_distillation")
        .properties(p -> p.density(1500).viscosity(3000))
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> DEW_OF_THE_VOID = fluid("dew_of_the_void")
        .properties(p -> p.density(200).viscosity(1000).temperature(175))
        .lang("Fluid of the Void")
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> VAPOR_OF_LEVITY = gasFluid("vapor_of_levity")
        .properties(p -> p.density(-10).viscosity(100).temperature(5))
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> HOOTCH = fluid("hootch")
        .properties(p -> p.density(900).viscosity(1000))
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> ROCKET_FUEL = fluid("rocket_fuel")
        .properties(p -> p.density(900).viscosity(1000))
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> FIRE_WATER = fluid("fire_water")
        .properties(p -> p.density(900).viscosity(1000).temperature(2000))
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> XP_JUICE = fluid("xp_juice")
        .properties(p -> p.lightLevel(10).density(800).viscosity(1500))
        .tag(EIOTags.Fluids.EXPERIENCE)
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> LIQUID_SUNSHINE = fluid("liquid_sunshine")
        .properties(p -> p.density(200).viscosity(400))
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> CLOUD_SEED = fluid("cloud_seed")
        .properties(p -> p.density(500).viscosity(800))
        .register();

    public static final FluidEntry<? extends ForgeFlowingFluid> CLOUD_SEED_CONCENTRATED = fluid("cloud_seed_concentrated")
        .properties(p -> p.density(1000).viscosity(1200))
        .register();

    private static FluidBuilder<? extends ForgeFlowingFluid, Registrate> fluid(String name) {
        return baseFluid(name)
            .bucket()
            .model(EIOFluids::bucketModel)
            .tab(EIOCreativeTabs.MAIN)
            .build();
    }

    private static FluidBuilder<? extends ForgeFlowingFluid, Registrate> gasFluid(String name) {
        return baseFluid(name)
            .bucket()
            .model((ctx, prov) -> bucketModel(ctx, prov).flipGas(true))
            .tab(EIOCreativeTabs.MAIN)
            .build();
    }

    private static FluidBuilder<? extends ForgeFlowingFluid, Registrate> baseFluid(String name) {
        var thing = REGISTRATE.fluid(name, EnderIO.loc("block/fluid_" + name + "_still"),
            EnderIO.loc("block/fluid_" + name + "_flowing"));
        if (FMLEnvironment.dist.isClient()) {
            thing.renderType(RenderType::translucent);
        }
        return thing.source(ForgeFlowingFluid.Source::new)
            .block()
            .build();
    }

    private static DynamicFluidContainerModelBuilder<ItemModelBuilder> bucketModel(DataGenContext<Item, BucketItem> ctx, RegistrateItemModelProvider prov) {
        return prov
            .withExistingParent(ctx.getName(), new ResourceLocation(ForgeVersion.MOD_ID, "item/bucket"))
            .customLoader(DynamicFluidContainerModelBuilder::begin)
            .fluid(ctx.get().getFluid());
    }

    public static void register() {}
}
