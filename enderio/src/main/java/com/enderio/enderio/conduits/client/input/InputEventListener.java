package com.enderio.enderio.conduits.client.input;

import com.enderio.enderio.conduits.common.init.ConduitItems;
import com.enderio.enderio.conduits.common.items.ConduitProbeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class InputEventListener {
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        // Supports vertical and horizontal scrolling
        if ((event.getScrollDeltaX() != 0 || event.getScrollDeltaY() != 0) && player.isShiftKeyDown()) {
            ItemStack mainHandStack = player.getMainHandItem();
            if (mainHandStack.is(ConduitItems.CONDUIT_PROBE)) {
                ConduitProbeItem.switchState(player, mainHandStack);
                event.setCanceled(true);
            }
        }
    }
}
