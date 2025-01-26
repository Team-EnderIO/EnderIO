package com.enderio.armory.common.init;

import static com.enderio.armory.common.init.ArmoryDataComponents.DARK_STEEL_ITEM_UPGRADES;

import com.enderio.armory.common.capability.DarkSteelCapability;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ArmoryCapabilities {

    public static final ItemCapability<DarkSteelCapability, Void> DARK_STEEL_CAPABILITY = ItemCapability
            .createVoid(EnderIO.loc("dark_steel_capability"), DarkSteelCapability.class);

    public static ICapabilityProvider<ItemStack, Void, DarkSteelCapability> DARK_STEEL_PROVIDER = (stack,
            v) -> new DarkSteelCapability(DARK_STEEL_ITEM_UPGRADES, stack);

    public static ICapabilityProvider<ItemStack, Void, IEnergyStorage> DARK_STEEL_ENERGY_STORAGE_PROVIDER = (stack,
            v) -> new ComponentEnergyStorage(stack, EIODataComponents.ENERGY.get(),
                    DarkSteelCapability.getEmpoweredUpgrade(stack).map(EmpoweredUpgrade::getMaxEnergyStored).orElse(0),
                    DarkSteelCapability.getEmpoweredUpgrade(stack)
                            .map(EmpoweredUpgrade::getMaxEnergyTransfer)
                            .orElse(0));

}
