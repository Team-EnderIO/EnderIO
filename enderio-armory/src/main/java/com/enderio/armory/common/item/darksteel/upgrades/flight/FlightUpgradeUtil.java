package com.enderio.armory.common.item.darksteel.upgrades.flight;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.tag.ArmoryTags;
import java.util.Optional;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class FlightUpgradeUtil {

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
                && (DarkSteelHelper.hasUpgrade(equipped, GliderUpgrade.NAME)
                        || DarkSteelHelper.hasUpgrade(equipped, ElytraUpgrade.NAME))) {
            return Optional.of(equipped);
        }
        return Optional.empty();
    }

}
