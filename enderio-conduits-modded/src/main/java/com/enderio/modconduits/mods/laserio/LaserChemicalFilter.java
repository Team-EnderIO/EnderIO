package com.enderio.modconduits.mods.laserio;

import com.direwolf20.laserio.common.containers.customhandler.FilterCountHandler;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.setup.LaserIODataComponents;
import com.enderio.base.common.capability.IFilterCapability;
import com.enderio.modconduits.mods.mekanism.ChemicalFilter;
import com.enderio.modconduits.mods.mekanism.MekanismModule;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LaserChemicalFilter implements IFilterCapability<ChemicalStack>, ChemicalFilter {

    private final ItemStack container;

    public LaserChemicalFilter(ItemStack cardItem) {
        this.container = BaseCard.getFilter(cardItem);
    }

    @Override
    public void setNbt(Boolean nbt) {
        if (!nbt) {
            container.remove(LaserIODataComponents.FILTER_COMPARE);
        } else {
            container.set(LaserIODataComponents.FILTER_COMPARE, nbt);
        }
    }

    @Override
    public boolean isNbt() {
        return container.getOrDefault(LaserIODataComponents.FILTER_COMPARE, false);
    }

    @Override
    public void setInverted(Boolean inverted) {
        if (!inverted) {
            container.remove(LaserIODataComponents.FILTER_ALLOW);
        } else {
            container.set(LaserIODataComponents.FILTER_ALLOW, false);
        }
    }

    @Override
    public boolean isInvert() {
        return !container.getOrDefault(LaserIODataComponents.FILTER_ALLOW, true);
    }

    @Override
    public List<ChemicalStack> getEntries() {
        List<ChemicalStack> filteredChemicals = new ArrayList();
        FilterCountHandler filterSlotHandler = new FilterCountHandler(15, container);

        for(int i = 0; i < (filterSlotHandler).getSlots(); ++i) {
            ItemStack stack = filterSlotHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                IChemicalHandler capability = stack.getCapability(MekanismModule.Capabilities.Item.CHEMICAL);
                if (capability != null) {

                    for(int tank = 0; tank < capability.getChemicalTanks(); ++tank) {
                        var chemical = capability.getChemicalInTank(tank);
                        if (!chemical.isEmpty()) {
                            filteredChemicals.add(chemical);
                        }
                    }
                }
            }
        }

        return filteredChemicals;
    }

    @Override
    public void setEntry(int index, ChemicalStack entry) {

    }

    @Override
    public boolean test(ChemicalStack boxedChemicalStack) {
        for (ChemicalStack stack : getEntries()) {
            if (ChemicalStack.isSameChemical(stack, boxedChemicalStack)) {
                return !isInvert();
            }
        }
        return isInvert();
    }
}
