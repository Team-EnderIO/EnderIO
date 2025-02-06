package com.enderio.armory.common.item.darksteel.upgrades.glider;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.client.KeyBinds;
import com.enderio.armory.common.capability.DarkSteelCapability;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class GliderUpgrade implements IDarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "glider";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return ArmoryLang.DS_UPGRADE_GLIDER;
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_GLIDER_DESCRIPTION);
    }

    public static void toggleGliderUpgrade(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Optional<ItemStack> eq = getEquippedChestplate(player);
        if (eq.isEmpty()) {
            return;
        }
        while (KeyBinds.GLIDER_MAPPING.get().consumeClick()) {
            boolean newVal = !eq.get().getOrDefault(ArmoryDataComponents.DARK_STEEL_GLIDER_ACTIVE, false);
            eq.get().set(ArmoryDataComponents.DARK_STEEL_GLIDER_ACTIVE, newVal);
            PacketDistributor.sendToServer(new GliderEnabledPacket(newVal));
        }
    }

    public static void handleActivePacket(GliderEnabledPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            Optional<ItemStack> eq = getEquippedChestplate(player);
            if (eq.isEmpty()) {
                return;
            }
            eq.get().set(ArmoryDataComponents.DARK_STEEL_GLIDER_ACTIVE, packet.enabled());
        });
    }

    public static Optional<ItemStack> getEquippedChestplate(Player player) {
        ItemStack equipped = player.getItemBySlot(EquipmentSlot.CHEST);
        if (equipped.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_CHESTPLATE)
                && DarkSteelCapability.hasUpgrade(equipped, GliderUpgrade.NAME)) {
            return Optional.of(equipped);
        }
        return Optional.empty();
    }
}
