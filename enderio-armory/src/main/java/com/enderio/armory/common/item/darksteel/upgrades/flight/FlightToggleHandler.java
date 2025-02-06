package com.enderio.armory.common.item.darksteel.upgrades.flight;

import com.enderio.armory.client.KeyBinds;
import com.enderio.armory.common.capability.DarkSteelCapability;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class FlightToggleHandler {

    public static void toggleFlightUpgrade(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Optional<ItemStack> eq = getEquippedChestplate(player);
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

    public static void handleActivePacket(FlightEnabledPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            Optional<ItemStack> eq = getEquippedChestplate(player);
            if (eq.isEmpty()) {
                return;
            }
            eq.get().set(ArmoryDataComponents.DARK_STEEL_FLIGHT_ACTIVE, packet.enabled());
        });
    }

    public static Optional<ItemStack> getEquippedChestplate(Player player) {
        ItemStack equipped = player.getItemBySlot(EquipmentSlot.CHEST);
        if (equipped.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_CHESTPLATE)
                && (DarkSteelCapability.hasUpgrade(equipped, GliderUpgrade.NAME)
                        || DarkSteelCapability.hasUpgrade(equipped, ElytraUpgrade.NAME))) {
            return Optional.of(equipped);
        }
        return Optional.empty();
    }

}
