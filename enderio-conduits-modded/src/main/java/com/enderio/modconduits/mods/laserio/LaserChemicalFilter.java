package com.enderio.modconduits.mods.laserio;

import com.direwolf20.laserio.common.containers.customhandler.FilterCountHandler;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.setup.LaserIODataComponents;
import com.enderio.base.common.capability.IFilterCapability;
import com.enderio.modconduits.mods.mekanism.ChemicalFilter;
import com.enderio.modconduits.mods.mekanism.MekanismModule;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LaserChemicalFilter implements IFilterCapability<BoxedChemicalStack>, ChemicalFilter {

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
    public List<BoxedChemicalStack> getEntries() {
        List<BoxedChemicalStack> filteredChemicals = new ArrayList();
        FilterCountHandler filterSlotHandler = new FilterCountHandler(15, container);

        for(int i = 0; i < (filterSlotHandler).getSlots(); ++i) {
            ItemStack stack = filterSlotHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                IChemicalHandler<?,?> capability = stack.getCapability(MekanismModule.Capabilities.Item.GAS);
                if (capability == null) {
                    capability = stack.getCapability(MekanismModule.Capabilities.Item.SLURRY);
                }
                if (capability == null) {
                    capability = stack.getCapability(MekanismModule.Capabilities.Item.INFUSION);
                }
                if (capability == null) {
                    capability = stack.getCapability(MekanismModule.Capabilities.Item.PIGMENT);
                }
                if (capability != null) {

                    for(int tank = 0; tank < capability.getTanks(); ++tank) {
                        var chemical = capability.getChemicalInTank(tank);
                        if (!chemical.isEmpty()) {
                            filteredChemicals.add(BoxedChemicalStack.box(chemical));
                        }
                    }
                }
            }
        }

        return filteredChemicals;
    }

    @Override
    public void setEntry(int index, BoxedChemicalStack entry) {

    }

    @Override
    public boolean test(BoxedChemicalStack boxedChemicalStack) {
        for (BoxedChemicalStack stack : getEntries()) {
            if (stack.getChemicalStack().getChemical() == boxedChemicalStack.getChemicalStack().getChemical()) {
                return !isInvert();
            }
        }
        return isInvert();
    }
}
