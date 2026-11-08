package com.enderio.enderio.content.armory;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

public class DarkSteelHelper {
    public static DarkSteelUpgradeContainer getUpgradeContainer(ItemStack itemStack) {
        // TODO: Temporary, need to be more careful.
        return itemStack.getOrDefault(EIODataComponents.DARK_STEEL_UPGRADES, ItemDarkSteelUpgrades.EMPTY);
    }

    public static DarkSteelHookContext getHookContext(ItemStack itemStack) {
        return new EIODarkSteelHookContext(itemStack);
    }

    private static class EIODarkSteelHookContext implements DarkSteelHookContext {

        private final ItemStack itemStack;

        private EIODarkSteelHookContext(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public ItemStack itemStack() {
            return itemStack;
        }

        @Override
        public CapacitorData getCapacitorData() {
            var capacitorSocket = itemStack.getCapability(EnderIOCapabilities.CAPACITOR_SOCKET);
            if (capacitorSocket == null) {
                return CapacitorData.NONE;
            }

            return capacitorSocket.getCapacitorData();
        }

        @Override
        public boolean consumeEnergy(int toConsume, boolean simulate) {
            return false;
        }
    }
}
