package com.enderio.base.client.input;

import com.enderio.EnderIO;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    public static final KeyMapping MODE_CHANGE = new KeyMapping(
        "keybinding.enderio.mode_change", 
        KeyConflictContext.IN_GAME, 
        InputConstants.getKey(InputConstants.KEY_Y, -1),
        "keybinding.enderio.category"
    );
    
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(MODE_CHANGE);
    }
}
