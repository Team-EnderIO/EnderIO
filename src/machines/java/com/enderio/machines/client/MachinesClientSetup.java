package com.enderio.machines.client;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.machines.client.rendering.blockentity.CapacitorBankBER;
import com.enderio.machines.client.rendering.blockentity.FluidTankBER;
import com.enderio.machines.client.rendering.blockentity.ObeliskBER;
import com.enderio.machines.client.rendering.blockentity.XPObeliskBER;
import com.enderio.machines.client.rendering.model.IOOverlayBakedModel;
import com.enderio.machines.client.rendering.travel.TravelAnchorHud;
import com.enderio.machines.common.blockentity.capacitorbank.CapacitorBankBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MachinesClientSetup {

    @SubscribeEvent
    public static void customModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("io_overlay", new IOOverlayBakedModel.Loader());
    }

    @SubscribeEvent
    public static void registerBERs(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MachineBlockEntities.FLUID_TANK.get(), FluidTankBER::new);
        event.registerBlockEntityRenderer(MachineBlockEntities.PRESSURIZED_FLUID_TANK.get(), FluidTankBER::new);
        event.registerBlockEntityRenderer(MachineBlockEntities.XP_OBELISK.get(), XPObeliskBER::new);
        event.registerBlockEntityRenderer(MachineBlockEntities.AVERSION_OBELISK.get(), (ctx) -> new ObeliskBER(ctx, EIOBlocks.ENDERMAN_HEAD.asItem()));
        for (BlockEntityEntry<CapacitorBankBlockEntity> value : MachineBlockEntities.CAPACITOR_BANKS.values()) {
            event.registerBlockEntityRenderer(value.get(), CapacitorBankBER::new);
        }
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "anchor_hud", TravelAnchorHud.INSTANCE);
    }
}
