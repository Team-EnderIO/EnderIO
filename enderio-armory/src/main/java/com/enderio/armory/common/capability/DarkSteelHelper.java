package com.enderio.armory.common.capability;

import com.enderio.armory.api.capability.IDarkSteelCapability;
import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.init.ArmoryCapabilities;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;

public class DarkSteelHelper {

    public static Optional<EmpoweredUpgrade> getEmpoweredUpgrade(ItemStack stack) {
        return DarkSteelHelper.getUpgradeAs(stack, EmpoweredUpgrade.NAME, EmpoweredUpgrade.class);
    }

    public static void addUpgrade(ItemStack itemStack, IDarkSteelUpgrade upgrade) {
        IDarkSteelCapability capability = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        if (capability != null) {
            capability.addUpgrade(upgrade);
        }
    }

    public static void removeUpgrade(ItemStack itemStack, String upgrade) {
        IDarkSteelCapability capability = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        if (capability != null) {
            capability.removeUpgrade(upgrade);
        }
    }

    public static Collection<IDarkSteelUpgrade> getUpgrades(ItemStack itemStack) {
        IDarkSteelCapability capability = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        return capability != null ? capability.getUpgrades() : Collections.emptyList();
    }

    public static boolean hasUpgrade(ItemStack itemStack, String name) {
        IDarkSteelCapability capability = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        return capability != null && capability.hasUpgrade(name);
    }

    public static <T extends IDarkSteelUpgrade> Optional<T> getUpgradeAs(ItemStack itemStack, String upgrade,
            Class<T> as) {
        IDarkSteelCapability cap = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        return cap != null ? cap.getUpgradeAs(upgrade, as) : Optional.empty();
    }

    public static Collection<IDarkSteelUpgrade> getUpgradesApplicable(ItemStack itemStack) {
        IDarkSteelCapability capability = itemStack.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        return capability != null ? capability.getUpgradesApplicable() : Collections.emptyList();
    }

}
