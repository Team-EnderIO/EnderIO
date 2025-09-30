package com.enderio.enderio.armory.common.capability;

import com.enderio.enderio.api.armory.capability.DarkSteelCapability;
import com.enderio.enderio.api.armory.capability.DarkSteelUpgrade;
import com.enderio.enderio.armory.common.init.ArmoryCapabilities;
import com.enderio.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class DarkSteelHelper {

    public static Optional<EmpoweredUpgrade> getEmpoweredUpgrade(ItemStack stack) {
        return DarkSteelHelper.getUpgradeAs(stack, EmpoweredUpgrade.NAME, EmpoweredUpgrade.class);
    }

    public static void addUpgrade(ItemStack itemStack, DarkSteelUpgrade upgrade) {
        DarkSteelCapability capability = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        if (capability != null) {
            capability.addUpgrade(upgrade);
        }
    }

    public static void removeUpgrade(ItemStack itemStack, String upgrade) {
        DarkSteelCapability capability = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        if (capability != null) {
            capability.removeUpgrade(upgrade);
        }
    }

    public static Collection<DarkSteelUpgrade> getUpgrades(ItemStack itemStack) {
        DarkSteelCapability capability = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        return capability != null ? capability.getUpgrades() : Collections.emptyList();
    }

    public static boolean hasUpgrade(ItemStack itemStack, String name) {
        DarkSteelCapability capability = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        return capability != null && capability.hasUpgrade(name);
    }

    public static <T extends DarkSteelUpgrade> Optional<T> getUpgradeAs(ItemStack itemStack, String upgrade,
            Class<T> as) {
        DarkSteelCapability cap = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        return cap != null ? cap.getUpgradeAs(upgrade, as) : Optional.empty();
    }

    public static Collection<DarkSteelUpgrade> getUpgradesApplicable(ItemStack itemStack) {
        DarkSteelCapability capability = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        return capability != null ? capability.getUpgradesApplicable() : Collections.emptyList();
    }

}
