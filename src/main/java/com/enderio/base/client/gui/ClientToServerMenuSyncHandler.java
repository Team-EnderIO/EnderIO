package com.enderio.base.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientToServerMenuSyncHandler {
    //    @SubscribeEvent
    //    public static void clientTick(TickEvent.ClientTickEvent e) {
    //        LocalPlayer player = Minecraft.getInstance().player;
    //        if (e.phase == TickEvent.Phase.END && player != null && player.containerMenu instanceof SyncedMenu<?> syncedMenu) {
    //            syncedMenu.clientTick();
    //        }
    //    }
}
