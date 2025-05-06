package com.enderio.armory.client;

import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.item.darksteel.upgrades.flight.FlightEnabledPacket;
import com.enderio.armory.common.item.darksteel.upgrades.flight.FlightUpgradeUtil;
import com.enderio.armory.common.lang.ArmoryLang;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class FlightToggleHandler {

    public static void toggleFlightUpgrade(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Optional<ItemStack> eq = FlightUpgradeUtil.getEquippedChestplate(player);
        if (eq.isEmpty()) {
            return;
        }
        while (KeyBinds.FLIGHT_MAPPING.get().consumeClick()) {
            boolean newVal = !eq.get().getOrDefault(ArmoryDataComponents.DARK_STEEL_FLIGHT_ACTIVE, false);
            eq.get().set(ArmoryDataComponents.DARK_STEEL_FLIGHT_ACTIVE, newVal);
            PacketDistributor.sendToServer(new FlightEnabledPacket(newVal));
            if (newVal) {
                player.displayClientMessage(ArmoryLang.FLIGHT_ENABLED, true);
            } else {
                player.displayClientMessage(ArmoryLang.FLIGHT_DISABLED, true);
            }
        }
    }

}
