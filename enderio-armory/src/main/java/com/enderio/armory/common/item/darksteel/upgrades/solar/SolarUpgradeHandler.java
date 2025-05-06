package com.enderio.armory.common.item.darksteel.upgrades.solar;

import com.enderio.armory.api.capability.IDarkSteelCapability;
import com.enderio.armory.common.init.ArmoryCapabilities;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.machines.common.blockentity.solar.SolarPanelBlockEntity;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.Nullable;

public class SolarUpgradeHandler {

    public static void onPlayerTick(PlayerTickEvent.Pre evt) {

        Player player = evt.getEntity();
        if (player.level().isClientSide()) {
            // Only need to do charge on the server
            return;
        }
        Optional<SolarUpgrade> upOp = getUpgrade(player);
        if (upOp.isEmpty()) {
            return;
        }

        Level level = player.level();
        // check sky
        BlockPos pos = new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());
        if (!level.canSeeSky(pos.above())) {
            return;
        }
        if (!level.dimensionType().hasSkyLight()) {
            return;
        }

        // scale output
        float outputScale = SolarPanelBlockEntity.getOutputScale(level);
        int energy = (int) Math.ceil(outputScale * upOp.get().getPanelTier().getProductionRate());

        int startIndex = player.getItemBySlot(EquipmentSlot.HEAD)
                .getOrDefault(ArmoryDataComponents.DARK_STEEL_SOLAR_CHARGE_INDEX, 0);

        energy = chargeInv(startIndex, player.getInventory().getContainerSize(), player, energy);
        if (energy <= 0) {
            return;
        }
        chargeInv(0, startIndex, player, energy);
    }

    private static int chargeInv(int startIndex, int endIndex, Player player, int energy) {
        Inventory inv = player.getInventory();
        for (int i = startIndex; i < endIndex; i++) {

            ItemStack chargeItem = inv.getItem(i);
            energy -= ItemStackEnergy.receiveEnergy(chargeItem, energy, false);
            if (energy <= 0) {
                int nextIndex = i + 1;
                if (nextIndex >= inv.getContainerSize()) {
                    nextIndex = 0;
                }
                player.getItemBySlot(EquipmentSlot.HEAD)
                        .set(ArmoryDataComponents.DARK_STEEL_SOLAR_CHARGE_INDEX, nextIndex);
                return 0;
            }
        }
        return energy;
    }

    public static Optional<SolarUpgrade> getUpgrade(Player player) {
        ItemStack equipped = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!equipped.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_HELMET)) {
            return Optional.empty();
        }
        @Nullable
        IDarkSteelCapability cap = equipped.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        if (cap == null) {
            return Optional.empty();
        }
        return cap.getUpgradeAs(SolarUpgrade.NAME, SolarUpgrade.class);
    }
}
