package com.enderio.enderio.client.content.keybinds;

import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.content.travel.TravelHandler;
import com.enderio.enderio.foundation.network.packets.ServerboundToggleMagnetPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeybindHandler {

    public static final String ENDERIO_KEYBIND_CATEGORY = "enderio";
    public static final Lazy<KeyMapping> TRAVEL_STAFF_KEY = Lazy.of(() -> new KeyMapping("key.enderio.travelstafftp", InputConstants.KEY_G, ENDERIO_KEYBIND_CATEGORY));
    public static final Lazy<KeyMapping> TOGGLE_MAGNET_KEY = Lazy.of(() -> new KeyMapping("key.enderio.magnettoggle", InputConstants.KEY_M, ENDERIO_KEYBIND_CATEGORY));

    private static int travelKeyDelayTracker = 0;
    // Used in TravelTargetRendering to enable rendering when travel keybind is held long enough.
    private static boolean travelTargetRenderingEnabled = false;

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent e){
        e.register(TRAVEL_STAFF_KEY.get());
        e.register(TOGGLE_MAGNET_KEY.get());
    }

    @SubscribeEvent
    public static void travelStaffKeyHandler(ClientTickEvent.Post event){

        // Control rendering of anchors/travel points
        if(travelKeyDelayTracker > BaseConfig.CLIENT.TRAVEL_KEY_HOLD_DELAY.get()){
            travelTargetRenderingEnabled = true;
        }else{
            travelTargetRenderingEnabled = false;
        }

        Player player = Minecraft.getInstance().player;
        if(player == null) {
            return;
        }

        if(TRAVEL_STAFF_KEY.get().isDown() && Minecraft.getInstance().level != null && Minecraft.getInstance().screen == null){
            travelKeyDelayTracker += 1;
            if(travelKeyDelayTracker >= BaseConfig.CLIENT.TRAVEL_KEY_HOLD_DELAY.get()) {
                ItemStack travelStack = TravelHandler.findValidTravelItem(player);
                if(travelStack.isEmpty()){
                    travelKeyDelayTracker = 0;
                }
            }
        }else if(travelKeyDelayTracker != 0){
            // Now that key is released, (attempt to) do actual teleport logic.
            ItemStack travelStack = TravelHandler.findValidTravelItem(player);
            if(!travelStack.isEmpty()) {
                if (travelKeyDelayTracker >= BaseConfig.CLIENT.TRAVEL_KEY_HOLD_DELAY.get()) {
                    // Do anchor logic
                    TravelHandler.blockTeleport(Minecraft.getInstance().level, player, true);
                } else {
                    // Do blink logic.
                    TravelHandler.shortTeleport(Minecraft.getInstance().level, player, true);
                }
            }
            travelKeyDelayTracker = 0;
        }
    }

    @SubscribeEvent
    public static void magnetToggleKeyHandler(InputEvent.Key e){
        if(TOGGLE_MAGNET_KEY.get() != null && TOGGLE_MAGNET_KEY.get().consumeClick() && Minecraft.getInstance().level != null){
            PacketDistributor.sendToServer(new ServerboundToggleMagnetPacket(true));
        }
    }

    public static boolean shouldRenderTravelTargetsDueToKeybind(){
        return travelTargetRenderingEnabled;
    }
}
