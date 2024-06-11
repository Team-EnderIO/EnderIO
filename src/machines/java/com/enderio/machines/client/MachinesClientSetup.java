package com.enderio.machines.client;

import com.enderio.EnderIO;
import com.enderio.api.travel.RegisterTravelRenderersEvent;
import com.enderio.machines.client.rendering.model.IOOverlayBakedModel;
import com.enderio.machines.client.rendering.travel.TravelAnchorHud;
import com.enderio.machines.client.rendering.travel.TravelAnchorRenderer;
import com.enderio.machines.common.init.MachineTravelTargets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = EnderIO.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class MachinesClientSetup {

    @SubscribeEvent
    public static void registerTravelRenderers(RegisterTravelRenderersEvent event) {
        event.register(MachineTravelTargets.TRAVEL_ANCHOR_TYPE.get(), TravelAnchorRenderer::new);
    }

    @SubscribeEvent
    public static void customModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(EnderIO.loc("io_overlay"), new IOOverlayBakedModel.Loader());
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, EnderIO.loc("anchor_hud"), TravelAnchorHud.INSTANCE);
    }
}
