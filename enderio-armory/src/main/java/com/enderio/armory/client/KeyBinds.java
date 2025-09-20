package com.enderio.armory.client;

import com.enderio.armory.EnderIOArmory;
import com.enderio.armory.common.lang.ArmoryLang;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = EnderIOArmory.MODULE_MOD_ID, value = Dist.CLIENT)
public class KeyBinds {

    public static final Lazy<KeyMapping> FLIGHT_MAPPING = Lazy
            .of(() -> new KeyMapping(ArmoryLang.DS_UPGRADE_FLIGHT_KEYBIND, KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, ArmoryLang.DS_UPGRADE_KEYBIND_CATEGORY));

    public static final Lazy<KeyMapping> NIGHT_VISION_MAPPING = Lazy
            .of(() -> new KeyMapping(ArmoryLang.DS_UPGRADE_NIGHT_VISION_KEYBIND, KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, ArmoryLang.DS_UPGRADE_KEYBIND_CATEGORY));

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(FLIGHT_MAPPING.get());
        event.register(NIGHT_VISION_MAPPING.get());
        NeoForge.EVENT_BUS.addListener(FlightToggleHandler::toggleFlightUpgrade);
        NeoForge.EVENT_BUS.addListener(NightVisionToggleHandler::toggleNightVision);
    }

}
