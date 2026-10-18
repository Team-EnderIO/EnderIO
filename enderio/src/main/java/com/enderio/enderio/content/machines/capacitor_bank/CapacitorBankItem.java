package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.enderio.foundation.block.entity.multienergy.CapacityTier;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;

public class CapacitorBankItem extends BlockItem {

    public static final ICapabilityProvider<ItemStack, ItemAccess, EnergyHandler> ENERGY_STORAGE_PROVIDER =
        (stack, access) -> new ItemAccessEnergyHandler(access, EIODataComponents.ENERGY, ((CapacitorBankItem)stack.getItem()).tier.getStorageCapacity());

    private final CapacityTier tier;

    public CapacitorBankItem(CapacitorBankBlock block, Item.Properties properties) {
        super(block, properties);
        this.tier = block.getTier();
    }
}
