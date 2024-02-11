package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.regilite.holder.RegiliteFluid;
import com.enderio.regilite.registry.BlockRegistry;
import com.enderio.regilite.registry.FluidRegister;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.fluids.FluidType;

// TODO: Fluid behaviours and some cleaning. https://github.com/SleepyTrousers/EnderIO-Rewrite/issues/34

// TODO: Registrate tint color support, it was some reason omitted in my original PR

@SuppressWarnings("unused")
public class EIOFluids {
    private static final FluidRegister FLUID_REGISTRY = FluidRegister.create(EnderIO.MODID);
    private static final ItemRegistry ITEM_REGISTRY = ItemRegistry.createRegistry(EnderIO.MODID);
    private static final BlockRegistry BLOCK_REGISTRY = BlockRegistry.createRegistry(EnderIO.MODID);

    public static final RegiliteFluid<FluidType> NUTRIENT_DISTILLATION =
        fluid("nutrient_distillation", FluidType.Properties.create().density(1500).viscosity(3000));

    public static final RegiliteFluid<FluidType> DEW_OF_THE_VOID =
        fluid("dew_of_the_void", FluidType.Properties.create().density(200).viscosity(1000).temperature(175))
        .setTranslation("Fluid of the Void");

    public static final RegiliteFluid<FluidType> VAPOR_OF_LEVITY =
        gasFluid("vapor_of_levity", FluidType.Properties.create().density(-10).viscosity(100).temperature(5));

    public static final RegiliteFluid<FluidType> HOOTCH =
        fluid("hootch", FluidType.Properties.create().density(900).viscosity(1000));

    public static final RegiliteFluid<FluidType> ROCKET_FUEL =
        fluid("rocket_fuel", FluidType.Properties.create().density(900).viscosity(1000));

    public static final RegiliteFluid<FluidType> FIRE_WATER =
        fluid("fire_water", FluidType.Properties.create().density(900).viscosity(1000).temperature(2000));

    public static final RegiliteFluid<FluidType> XP_JUICE =
        fluid("xp_juice", FluidType.Properties.create().lightLevel(10).density(800).viscosity(1500))
        .setTranslation("XP Juice")
        .addFluidTags(EIOTags.Fluids.EXPERIENCE);

    public static final RegiliteFluid<FluidType> LIQUID_SUNSHINE =
        fluid("liquid_sunshine", FluidType.Properties.create().density(200).viscosity(400));

    public static final RegiliteFluid<FluidType> CLOUD_SEED =
        fluid("cloud_seed", FluidType.Properties.create().density(500).viscosity(800));

    public static final RegiliteFluid<FluidType> CLOUD_SEED_CONCENTRATED =
        fluid("cloud_seed_concentrated", FluidType.Properties.create().density(1000).viscosity(1200));

    private static RegiliteFluid<FluidType> fluid(String name, FluidType.Properties properties) {
        return baseFluid(name, properties)
            .withBucket(ITEM_REGISTRY, fluid -> new BucketItem(fluid, new Item.Properties().stacksTo(1)))
            .setTab(EIOCreativeTabs.MAIN)
            .finishBucket();
    }

    private static RegiliteFluid<FluidType> gasFluid(String name, FluidType.Properties properties) {
        return baseFluid(name, properties)
            .withBucket(ITEM_REGISTRY, fluid -> new BucketItem(fluid, new Item.Properties().stacksTo(1)))
            .setTab(EIOCreativeTabs.MAIN)
            .finishBucket();
    }

    private static RegiliteFluid<FluidType> baseFluid(String name, FluidType.Properties properties) {
        // TODO: RenderType might be missing.
        /*if (FMLEnvironment.dist.isClient()) {
            thing.renderType(RenderType::translucent);
        }*/

        return FLUID_REGISTRY
            .registerFluid(name, properties)
            .withBlock(BLOCK_REGISTRY, fluid -> new LiquidBlock(fluid, BlockBehaviour.Properties.copy(Blocks.WATER)))
            .finishLiquidBlock();
    }

    public static void register(IEventBus bus) {
        FLUID_REGISTRY.register(bus);
        BLOCK_REGISTRY.register(bus);
        ITEM_REGISTRY.register(bus);
    }
}
