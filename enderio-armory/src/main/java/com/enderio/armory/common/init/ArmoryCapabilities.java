package com.enderio.armory.common.init;

import com.enderio.armory.api.capability.IDarkSteelCapability;
import com.enderio.armory.common.capability.DarkSteelCapability;
import com.enderio.armory.common.capability.DarkSteelEnergyStorage;
import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ArmoryCapabilities {

    public static final ItemCapability<IDarkSteelCapability, Void> DARK_STEEL_CAPABILITY = ItemCapability
            .createVoid(EnderIO.loc("dark_steel_capability"), IDarkSteelCapability.class);

    public static final ICapabilityProvider<ItemStack, Void, IDarkSteelCapability> DARK_STEEL_PROVIDER = (stack,
            v) -> new DarkSteelCapability(stack);

    public static final ICapabilityProvider<ItemStack, Void, IEnergyStorage> DARK_STEEL_ENERGY_STORAGE_PROVIDER = (stack,
            v) -> new DarkSteelEnergyStorage(stack, EIODataComponents.ENERGY.get(),
                    DarkSteelHelper.getEmpoweredUpgrade(stack).map(EmpoweredUpgrade::getMaxEnergyStored).orElse(0),
                    DarkSteelHelper.getEmpoweredUpgrade(stack).map(EmpoweredUpgrade::getMaxEnergyTransfer).orElse(0));

}
