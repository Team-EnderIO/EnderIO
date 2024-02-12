package com.enderio.machines.client;

import com.enderio.EnderIO;
import com.enderio.machines.client.rendering.blockentity.CapacitorBankBER;
import com.enderio.machines.client.rendering.model.IOOverlayBakedModel;
import com.enderio.machines.client.rendering.travel.TravelAnchorHud;
import com.enderio.machines.common.init.MachineBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MachinesClientSetup {

    @SubscribeEvent
    public static void customModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("io_overlay", new IOOverlayBakedModel.Loader());
    }

    @SubscribeEvent
    public static void registerBERs(EntityRenderersEvent.RegisterRenderers event) {
        // TODO: Move into the creation call.
        for (var value : MachineBlockEntities.CAPACITOR_BANKS.values()) {
            event.registerBlockEntityRenderer(value.get(), CapacitorBankBER::new);
        }
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "anchor_hud", TravelAnchorHud.INSTANCE);
    }
}
