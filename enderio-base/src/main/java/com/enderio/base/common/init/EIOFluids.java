package com.enderio.base.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.api.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.regilite.holder.RegiliteFluid;
import com.enderio.regilite.registry.BlockRegistry;
import com.enderio.regilite.registry.FluidRegistry;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;

// TODO: Fluid behaviours and some cleaning. https://github.com/SleepyTrousers/EnderIO-Rewrite/issues/34

// TODO: Registrate tint color support, it was some reason omitted in my original PR

@SuppressWarnings("unused")
public class EIOFluids {
    private static final FluidRegistry FLUID_TYPE_REGISTRY = EnderIOBase.REGILITE.fluidRegistry();
    private static final DeferredRegister<Fluid> FLUID_REGISTRY = DeferredRegister.create(Registries.FLUID,
            EnderIO.NAMESPACE);
    private static final ItemRegistry ITEM_REGISTRY = EnderIOBase.REGILITE.itemRegistry();
    private static final BlockRegistry BLOCK_REGISTRY = EnderIOBase.REGILITE.blockRegistry();

    public static final RegiliteFluid<FluidType> NUTRIENT_DISTILLATION = fluid("nutrient_distillation",
            "Nutrient Distillation", FluidType.Properties.create().density(1500).viscosity(3000));

    public static final RegiliteFluid<FluidType> DEW_OF_THE_VOID = fluid("dew_of_the_void", "Fluid of the Void",
            FluidType.Properties.create().density(200).viscosity(1000).temperature(175), 3);

    public static final RegiliteFluid<FluidType> VAPOR_OF_LEVITY = gasFluid("vapor_of_levity", "Vapor of Levity",
            FluidType.Properties.create().density(-10).viscosity(100).temperature(5));

    public static final RegiliteFluid<FluidType> HOOTCH = fluid("hootch", "Hootch",
            FluidType.Properties.create().density(900).viscosity(1000));

    public static final RegiliteFluid<FluidType> ROCKET_FUEL = fluid("rocket_fuel", "Rocket Fuel",
            FluidType.Properties.create().density(900).viscosity(1000));

    public static final RegiliteFluid<FluidType> FIRE_WATER = fluid("fire_water", "Fire Water",
            FluidType.Properties.create().density(900).viscosity(1000).temperature(2000), 5);

    public static final RegiliteFluid<FluidType> XP_JUICE = fluid("xp_juice", "XP Juice",
            FluidType.Properties.create().lightLevel(10).density(800).viscosity(1500), 10)
                    .addFluidTags(EIOTags.Fluids.EXPERIENCE);

    public static final RegiliteFluid<FluidType> LIQUID_SUNSHINE = fluid("liquid_sunshine", "Liquid Sunshine",
            FluidType.Properties.create().density(200).viscosity(400).lightLevel(15).temperature(300), 15).addFluidTags(EIOTags.Fluids.SOLAR_PANEL_LIGHT);

    public static final RegiliteFluid<FluidType> LIQUID_DARKNESS = fluid("liquid_darkness", "Liquid Darkness",
        FluidType.Properties.create().density(500).viscosity(900).temperature(100)).addFluidTags(EIOTags.Fluids.SOLAR_PANEL_DARK);

    public static final RegiliteFluid<FluidType> CLOUD_SEED = fluid("cloud_seed", "Cloud Seed",
            FluidType.Properties.create().density(500).viscosity(800));

    public static final RegiliteFluid<FluidType> CLOUD_SEED_CONCENTRATED = fluid("cloud_seed_concentrated",
            "Cloud Seed Concentrated", FluidType.Properties.create().density(1000).viscosity(1200));

    private static RegiliteFluid<FluidType> fluid(String name, String translation, FluidType.Properties properties, int lightLevel) {
        return baseFluid(name, properties, lightLevel).setTranslation(translation)
            .withBucket(ITEM_REGISTRY, fluid -> new BucketItem(fluid.get(), new Item.Properties().stacksTo(1)))
            .setTab(EIOCreativeTabs.MAIN)
            .setTranslation(translation + " Bucket")
            .finishBucket();
    }

    private static RegiliteFluid<FluidType> fluid(String name, String translation, FluidType.Properties properties) {
        return baseFluid(name, properties, 0).setTranslation(translation)
                .withBucket(ITEM_REGISTRY, fluid -> new BucketItem(fluid.get(), new Item.Properties().stacksTo(1)))
                .setTab(EIOCreativeTabs.MAIN)
                .setTranslation(translation + " Bucket")
                .finishBucket();
    }

    private static RegiliteFluid<FluidType> gasFluid(String name, String translation, FluidType.Properties properties) {
        return baseFluid(name, properties, 0).setTranslation(translation)
                .withBucket(ITEM_REGISTRY, fluid -> new BucketItem(fluid.get(), new Item.Properties().stacksTo(1)))
                .setTab(EIOCreativeTabs.MAIN)
                .setTranslation(translation + " Bucket")
                .finishBucket();
    }

    private static RegiliteFluid<FluidType> baseFluid(String name, FluidType.Properties properties, int lightlevel) {
        return FLUID_TYPE_REGISTRY.registerFluid(name, properties)
                .setRenderType(() -> RenderType::translucent)
                .createFluid(FLUID_REGISTRY)
                .withBlock(BLOCK_REGISTRY,
                        fluid -> new LiquidBlock(fluid.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).lightLevel((state) -> lightlevel)))
                .finishLiquidBlock();
    }

    public static void register(IEventBus bus) {
        FLUID_TYPE_REGISTRY.register(bus);
        FLUID_REGISTRY.register(bus);
        BLOCK_REGISTRY.register(bus);
        ITEM_REGISTRY.register(bus);
    }
}
