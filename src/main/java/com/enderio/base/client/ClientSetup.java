package com.enderio.base.client;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.model.DummyCustomRenderModel;
import com.enderio.core.client.gui.model.composite.CompositeGeometryLoader;
import com.enderio.base.common.init.EIOFluids;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    // TODO: Add Fluid renderLayer support to Registrate?
    @SubscribeEvent
    public static void setRenderLayers(FMLClientSetupEvent event) {
        configureFluid(EIOFluids.NUTRIENT_DISTILLATION);
        configureFluid(EIOFluids.DEW_OF_THE_VOID);
        configureFluid(EIOFluids.VAPOR_OF_LEVITY);
        configureFluid(EIOFluids.HOOTCH);
        configureFluid(EIOFluids.ROCKET_FUEL);
        configureFluid(EIOFluids.FIRE_WATER);
        configureFluid(EIOFluids.XP_JUICE);
        configureFluid(EIOFluids.LIQUID_SUNSHINE);
        configureFluid(EIOFluids.CLOUD_SEED);
        configureFluid(EIOFluids.CLOUD_SEED_CONCENTRATED);
    }

    private static void configureFluid(FluidEntry<? extends ForgeFlowingFluid> fluidEntry) {
        ItemBlockRenderTypes.setRenderLayer(fluidEntry.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(fluidEntry.get().getSource(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void customModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("composite_model", new CompositeGeometryLoader());
        event.register("dummy", new DummyCustomRenderModel.Loader());
    }
    
    @SubscribeEvent
    public static void additionalModels(ModelEvent.RegisterAdditional event) {
        event.register(EnderIO.loc("item/wood_gear_helper"));
        event.register(EnderIO.loc("item/stone_gear_helper"));
        event.register(EnderIO.loc("item/iron_gear_helper"));
        event.register(EnderIO.loc("item/energized_gear_helper"));
        event.register(EnderIO.loc("item/vibrant_gear_helper"));
        event.register(EnderIO.loc("item/dark_bimetal_gear_helper"));
    }
}
