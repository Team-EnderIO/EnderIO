package com.enderio.conduits.client.input;

import com.enderio.EnderIO;
import com.enderio.base.client.input.KeyBindings;
import com.enderio.conduits.common.items.ConduitProbeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InputEventListener {
    @SubscribeEvent
    public static void inputEvent(InputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        
        ItemStack heldMainHand = player.getMainHandItem();
        ItemStack heldOffHand = player.getOffhandItem();
        
        if (Math.abs(event.getScrollDelta()) > 0 && player.isShiftKeyDown()) {
            boolean cancelScroll = false;
            if (heldMainHand.getItem() instanceof ConduitProbeItem) {
                ConduitProbeItem.switchState(heldMainHand, true);
                cancelScroll = true;
            }
            if (heldOffHand.getItem() instanceof ConduitProbeItem) {
                ConduitProbeItem.switchState(heldOffHand, true);
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
        
        if (KeyBindings.MODE_CHANGE.consumeClick()) {
            if (heldMainHand.getItem() instanceof ConduitProbeItem) {
                ConduitProbeItem.switchState(heldMainHand, true);
            }
            if (heldOffHand.getItem() instanceof ConduitProbeItem) {
                ConduitProbeItem.switchState(heldOffHand, true);
            }
        }
    }
}
