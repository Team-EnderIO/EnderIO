package com.enderio.modconduits.common.modules.laserio;

import com.direwolf20.laserio.common.containers.FilterBasicContainer;
import com.direwolf20.laserio.common.containers.customhandler.FilterBasicHandler;
import com.direwolf20.laserio.setup.LaserIODataComponents;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import com.enderio.modconduits.common.modules.mekanism.chemical_filter.ChemicalFilter;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LaserChemicalFilter implements ChemicalFilter {

    private final ItemStack container;

    public LaserChemicalFilter(ItemStack container) {
        this.container = container;
    }

    @Override
    public ChemicalStack test(@Nullable IChemicalHandler target, ChemicalStack stack) {
        final boolean isAllowList = container.getOrDefault(LaserIODataComponents.FILTER_ALLOW, true);

        FilterBasicHandler filterSlotHandler = new FilterBasicHandler(FilterBasicContainer.SLOTS, container);

        for (int i = 0; i < filterSlotHandler.getSlots(); ++i) {
            ItemStack itemStack = filterSlotHandler.getStackInSlot(i);

            if (!itemStack.isEmpty()) {
                IChemicalHandler capability = itemStack.getCapability(MekanismModule.Capabilities.Item.CHEMICAL);
                if (capability != null) {
                    for (int tank = 0; tank < capability.getChemicalTanks(); ++tank) {
                        ChemicalStack fluidStack = capability.getChemicalInTank(tank);
                        if (!fluidStack.isEmpty()) {
                            if (ChemicalStack.isSameChemical(fluidStack, stack)) {
                                return isAllowList ? fluidStack : ChemicalStack.EMPTY;
                            }
                        }
                    }
                }
            }
        }

        return isAllowList ? ChemicalStack.EMPTY : stack;
    }
}
