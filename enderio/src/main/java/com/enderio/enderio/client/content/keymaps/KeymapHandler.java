package com.enderio.enderio.client.content.keymaps;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.content.travel.TravelHandler;
import com.enderio.enderio.foundation.network.packets.ServerboundToggleMagnetPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeymapHandler {

    public static final KeyMapping.Category ENDERIO_KEYBIND_CATEGORY = new KeyMapping.Category(EnderIO.id("enderio_keys"));
    public static final Lazy<KeyMapping> TRAVEL_STAFF_KEY = Lazy.of(() -> new KeyMapping("key.enderio.travel_staff", InputConstants.KEY_G, ENDERIO_KEYBIND_CATEGORY));
    public static final Lazy<KeyMapping> TOGGLE_MAGNET_KEY = Lazy.of(() -> new KeyMapping("key.enderio.toggle_magnet", InputConstants.KEY_M, ENDERIO_KEYBIND_CATEGORY));

    private static int travelKeyTicks = 0;
    private static boolean travelTargetsVisible = false;

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event){
        event.register(TRAVEL_STAFF_KEY.get());
        event.register(TOGGLE_MAGNET_KEY.get());
    }

    @SubscribeEvent
    public static void keyHandler(ClientTickEvent.Post event){
        var minecraft = Minecraft.getInstance();


        // 26.2-port: minecraft.screen -> minecraft.gui.screen()
        if(minecraft.player == null || minecraft.level == null || minecraft.gui.screen() != null) {
            return;
        }


        // Magnet
        if(TOGGLE_MAGNET_KEY.get().consumeClick()){
            ClientPacketDistributor.sendToServer(new ServerboundToggleMagnetPacket());
        }

        // Travel
        // Control rendering of travel targets
        travelTargetsVisible = travelKeyDelayCheck();

        var shouldTeleport = false;
        if(TRAVEL_STAFF_KEY.get().isDown()){
            travelKeyTicks++;
        } else {
            shouldTeleport = travelKeyTicks > 0;
            travelKeyTicks = 0;
        }

        if(shouldTeleport){
            var stack = TravelHandler.findTravelItem(minecraft.player);
            if(!stack.isEmpty()) {
                if (travelTargetsVisible) {
                    TravelHandler.blockTeleport(minecraft.level, minecraft.player, true);
                } else {
                    TravelHandler.shortTeleport(minecraft.level, minecraft.player, true);
                }
            }
        }
    }

    private static boolean travelKeyDelayCheck(){
        return travelKeyTicks > BaseConfig.CLIENT.TRAVEL_KEY_HOLD_DELAY.get();
    }

    public static boolean showTravelTargets(){
        return travelTargetsVisible;
    }
}
