package com.enderio.enderio.client.content.keymaps;

import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.content.armory.toggles.DarkSteelToggleKeys;
import com.enderio.enderio.content.travel.TravelHandler;
import com.enderio.enderio.foundation.network.packets.ServerboundDarkSteelTogglePacket;
import com.enderio.enderio.foundation.network.packets.ServerboundToggleMagnetPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeymapHandler {

    public static final String ENDERIO_KEYBIND_CATEGORY = "keys.categories.enderio";
    public static final Lazy<KeyMapping> TRAVEL_STAFF_KEY = Lazy.of(() -> new KeyMapping("key.enderio.travel_staff", InputConstants.KEY_G, ENDERIO_KEYBIND_CATEGORY));
    public static final Lazy<KeyMapping> TOGGLE_MAGNET_KEY = Lazy.of(() -> new KeyMapping("key.enderio.toggle_magnet", InputConstants.KEY_M, ENDERIO_KEYBIND_CATEGORY));

    // Dark steel toggles
    public static final Lazy<KeyMapping> TOGGLE_NIGHT_VISION_KEY = Lazy.of(() -> new KeyMapping("key.enderio.toggle.night_vision", InputConstants.KEY_Z, ENDERIO_KEYBIND_CATEGORY));
    public static final Lazy<KeyMapping> TOGGLE_ELYTRA_KEY = Lazy.of(() -> new KeyMapping("key.enderio.toggle.elytra", InputConstants.UNKNOWN.getValue(), ENDERIO_KEYBIND_CATEGORY));
    public static final Lazy<KeyMapping> TOGGLE_STEP_ASSIST_KEY = Lazy.of(() -> new KeyMapping("key.enderio.toggle.step_assist", InputConstants.UNKNOWN.getValue(), ENDERIO_KEYBIND_CATEGORY));
    public static final Lazy<KeyMapping> TOGGLE_DOUBLE_JUMP_KEY = Lazy.of(() -> new KeyMapping("key.enderio.toggle.double_jump", InputConstants.UNKNOWN.getValue(), ENDERIO_KEYBIND_CATEGORY));
    public static final Lazy<KeyMapping> TOGGLE_EXPLOSIVE_KEY = Lazy.of(() -> new KeyMapping("key.enderio.toggle.explosive", InputConstants.UNKNOWN.getValue(), ENDERIO_KEYBIND_CATEGORY));
    public static final Lazy<KeyMapping> TOGGLE_SPEED_KEY = Lazy.of(() -> new KeyMapping("key.enderio.toggle.speed", InputConstants.UNKNOWN.getValue(), ENDERIO_KEYBIND_CATEGORY));

    private static int travelKeyTicks = 0;
    private static boolean travelTargetsVisible = false;

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event){
        event.register(TRAVEL_STAFF_KEY.get());
        event.register(TOGGLE_MAGNET_KEY.get());
        event.register(TOGGLE_NIGHT_VISION_KEY.get());
        event.register(TOGGLE_ELYTRA_KEY.get());
        event.register(TOGGLE_STEP_ASSIST_KEY.get());
        event.register(TOGGLE_DOUBLE_JUMP_KEY.get());
        event.register(TOGGLE_EXPLOSIVE_KEY.get());
        event.register(TOGGLE_SPEED_KEY.get());
    }

    @SubscribeEvent
    public static void keyHandler(ClientTickEvent.Post event){
        var minecraft = Minecraft.getInstance();


        if(minecraft.player == null || minecraft.level == null || minecraft.screen != null) {
            return;
        }


        // Magnet
        if(TOGGLE_MAGNET_KEY.get().consumeClick()){
            PacketDistributor.sendToServer(new ServerboundToggleMagnetPacket());
        }


        // Dark steel toggles
        sendToggleOnClick(TOGGLE_NIGHT_VISION_KEY.get(), DarkSteelToggleKeys.NIGHT_VISION);
        sendToggleOnClick(TOGGLE_ELYTRA_KEY.get(), DarkSteelToggleKeys.ELYTRA);
        sendToggleOnClick(TOGGLE_STEP_ASSIST_KEY.get(), DarkSteelToggleKeys.STEP_ASSIST);
        sendToggleOnClick(TOGGLE_DOUBLE_JUMP_KEY.get(), DarkSteelToggleKeys.DOUBLE_JUMP);
        sendToggleOnClick(TOGGLE_EXPLOSIVE_KEY.get(), DarkSteelToggleKeys.EXPLOSIVE);
        sendToggleOnClick(TOGGLE_SPEED_KEY.get(), DarkSteelToggleKeys.SPEED);

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

    private static void sendToggleOnClick(KeyMapping keyMapping, ResourceLocation toggleKey) {
        if (keyMapping.consumeClick()) {
            PacketDistributor.sendToServer(new ServerboundDarkSteelTogglePacket(toggleKey));
        }
    }

    private static boolean travelKeyDelayCheck(){
        return travelKeyTicks > BaseConfig.CLIENT.TRAVEL_KEY_HOLD_DELAY.get();
    }

    public static boolean showTravelTargets(){
        return travelTargetsVisible;
    }
}
