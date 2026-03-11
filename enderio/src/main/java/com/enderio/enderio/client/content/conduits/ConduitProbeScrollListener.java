package com.enderio.enderio.client.content.conduits;

import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.InputEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ConduitProbeScrollListener {
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        // Supports vertical and horizontal scrolling
        if ((event.getScrollDeltaX() != 0 || event.getScrollDeltaY() != 0) && player.isShiftKeyDown()) {
            ItemStack mainHandStack = player.getMainHandItem();
            if (mainHandStack.is(EIOItems.CONDUIT_PROBE)) {
                ConduitProbeItem.switchState(player, mainHandStack);
                event.setCanceled(true);
            }
        }
    }
}
