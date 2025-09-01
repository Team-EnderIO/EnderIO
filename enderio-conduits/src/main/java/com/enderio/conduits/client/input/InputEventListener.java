package com.enderio.conduits.client.input;

import com.enderio.conduits.EnderIOConduits;
// import com.enderio.base.client.input.KeyBindings;
import com.enderio.conduits.common.items.ConduitProbeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, value = Dist.CLIENT)
public class InputEventListener {
    @SubscribeEvent
    public static void inputEvent(InputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        
        ItemStack heldMainHand = player.getMainHandItem();
        ItemStack heldOffHand = player.getOffhandItem();
        
        if (Math.abs(event.getScrollDeltaY()) > 0 && player.isShiftKeyDown()) {
            boolean cancelScroll = false;
            if (heldMainHand.getItem() instanceof ConduitProbeItem) {
                ConduitProbeItem.switchState(heldMainHand, player, true);
                cancelScroll = true;
            }
            if (heldOffHand.getItem() instanceof ConduitProbeItem) {
                ConduitProbeItem.switchState(heldOffHand, player, true);
                cancelScroll = true;
            }
            event.setCanceled(cancelScroll);
        }
    }
    
    @SubscribeEvent
    public static void inputEvent(InputEvent.Key event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack heldMainHand = player.getMainHandItem();
        ItemStack heldOffHand = player.getOffhandItem();
        
        // if (KeyBindings.MODE_CHANGE.consumeClick()) {
        //     if (heldMainHand.getItem() instanceof ConduitProbeItem) {
        //         ConduitProbeItem.switchState(heldMainHand, true);
        //     }
        //     if (heldOffHand.getItem() instanceof ConduitProbeItem) {
        //         ConduitProbeItem.switchState(heldOffHand, true);
        //     }
        // }
    }
}
