package com.enderio.armory.client;

import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.item.darksteel.upgrades.nightvision.NightVisionEnabledPacket;
import com.enderio.armory.common.item.darksteel.upgrades.nightvision.NightVisionHandler;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class NightVisionToggleHandler {

    public static void toggleNightVision(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Optional<ItemStack> eq = NightVisionHandler.getEquippedHelmet(player);
        if (eq.isEmpty()) {
            return;
        }
        while (KeyBinds.NIGHT_VISION_MAPPING.get().consumeClick()) {
            boolean newVal = !eq.get().getOrDefault(ArmoryDataComponents.DARK_STEEL_NIGHT_VISION_ACTIVE, false);
            eq.get().set(ArmoryDataComponents.DARK_STEEL_NIGHT_VISION_ACTIVE, newVal);
            PacketDistributor.sendToServer(new NightVisionEnabledPacket(newVal));
        }
    }

}
