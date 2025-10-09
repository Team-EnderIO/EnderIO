package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.enderio.foundation.block.entity.multienergy.CapacityTier;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CapacitorBankItem extends BlockItem {

    public static final ICapabilityProvider<ItemStack, Void, IEnergyStorage> ENERGY_STORAGE_PROVIDER =
        (stack, v) -> new ComponentEnergyStorage(stack, EIODataComponents.ENERGY, ((CapacitorBankItem)stack.getItem()).tier.getStorageCapacity()) {
            @Override
            public int extractEnergy(int toExtract, boolean simulate) {
                int extract = toExtract / stack.getCount();
                return super.extractEnergy(extract, simulate) * stack.getCount();
            }

            @Override
            public int receiveEnergy(int toReceive, boolean simulate) {
                int receive = toReceive / stack.getCount();
                return super.receiveEnergy(receive, simulate) * stack.getCount();
            }
        };

    private final CapacityTier tier;

    public CapacitorBankItem(CapacitorBankBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        this.tier = pBlock.tier;
    }
}
