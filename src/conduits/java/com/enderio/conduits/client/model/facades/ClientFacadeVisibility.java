package com.enderio.conduits.client.model.facades;

import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Tracks whether facades should be visible based on what the player is holding.
 * Facades become transparent when holding items tagged with "enderio:hide_facades".
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientFacadeVisibility {
    
    private static boolean facadesVisible = true;
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        
        if (player == null) {
            facadesVisible = true;
            return;
        }
        
        // Check if player is holding an item that should hide facades
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        
        boolean shouldHide = shouldHideFacades(mainHand) || shouldHideFacades(offHand);
        
        // Just update the visibility flag - no need to rebuild chunks
        // The render event will naturally handle the visibility change
        facadesVisible = !shouldHide;
    }
    
    private static boolean shouldHideFacades(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        // Check if item is tagged with "enderio:hide_facades"
        var item = stack.getItem();
        var holder = BuiltInRegistries.ITEM.wrapAsHolder(item);
        
        // Check for the tag
        return holder.is(ConduitTags.Items.HIDE_FACADES);
    }
    
    /**
     * @return Whether facades should be rendered as opaque (true) or transparent (false).
     */
    public static boolean areFacadesVisible() {
        return facadesVisible;
    }
}
