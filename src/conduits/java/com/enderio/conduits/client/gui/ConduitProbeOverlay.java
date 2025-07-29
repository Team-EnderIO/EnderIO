package com.enderio.conduits.client.gui;

import com.enderio.EnderIO;
import com.enderio.conduits.common.items.ConduitProbeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ConduitProbeOverlay {
    private static final String GUI_LOC = "textures/gui/";
    private static final ResourceLocation MODE_PROBE = EnderIO.loc(GUI_LOC + "probe_probe_overlay.png");
    private static final ResourceLocation MODE_COPY_PASTE = EnderIO.loc(GUI_LOC + "probe_copy_paste_overlay.png");

    @SubscribeEvent
    public static void renderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        // check if player exists
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        // find if he's holding a probe or not
        ItemStack probeStack;
        if (!player.isHolding(item -> item.getItem() instanceof ConduitProbeItem)) {
            return;
        } else if (player.getMainHandItem().getItem() instanceof ConduitProbeItem probeItem1) {
            probeStack = player.getMainHandItem();
        } else {
            probeStack = player.getOffhandItem();
        }

        int x = (int) Math.min(event.getGuiGraphics().guiWidth() - 32, event.getGuiGraphics().guiWidth() * 0.99f);
        int y = (int) Math.min(event.getGuiGraphics().guiHeight() - 32, event.getGuiGraphics().guiHeight() * 0.99f);

        ResourceLocation toRender = switch (ConduitProbeItem.getState(probeStack)) {
            case PROBE -> MODE_PROBE;
            case COPY_PASTE -> MODE_COPY_PASTE;
        };

        event.getGuiGraphics().blitInscribed(toRender, x, y, 32, 32, 32, 32);
    }
}
