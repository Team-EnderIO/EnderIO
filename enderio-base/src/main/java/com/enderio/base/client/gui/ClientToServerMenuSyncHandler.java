package com.enderio.base.client.gui;

import com.enderio.base.common.menu.SyncedMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientToServerMenuSyncHandler {

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent e) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (e.phase == TickEvent.Phase.END && player != null
            && player.containerMenu instanceof SyncedMenu syncedMenu) {

            syncedMenu.clientTick();
        }
    }
}
