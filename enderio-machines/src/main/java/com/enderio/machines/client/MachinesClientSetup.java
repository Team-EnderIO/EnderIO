package com.enderio.machines.client;

import com.enderio.EnderIOBase;
import com.enderio.base.api.travel.RegisterTravelRenderersEvent;
import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.client.rendering.item.FluidTankBEWLR;
import com.enderio.machines.client.rendering.model.IOOverlayBakedModel;
import com.enderio.machines.client.rendering.travel.TravelAnchorHud;
import com.enderio.machines.client.rendering.travel.TravelAnchorRenderer;
import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineTravelTargets;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.util.Lazy;

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class MachinesClientSetup {

    @SubscribeEvent
    public static void registerTravelRenderers(RegisterTravelRenderersEvent event) {
        event.register(MachineTravelTargets.TRAVEL_ANCHOR_TYPE.get(), TravelAnchorRenderer::new);
    }

    @SubscribeEvent
    public static void customModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(EnderIOBase.loc("io_overlay"), new IOOverlayBakedModel.Loader());
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, EnderIOBase.loc("anchor_hud"), TravelAnchorHud.INSTANCE);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            // Minecraft can be null during datagen
            final Lazy<BlockEntityWithoutLevelRenderer> renderer = Lazy.of(() -> FluidTankBEWLR.INSTANCE);

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.get();
            }
        }, MachineBlocks.FLUID_TANK.asItem(), MachineBlocks.PRESSURIZED_FLUID_TANK.asItem());
    }
}
