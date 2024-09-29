package com.enderio.modconduits.mods.laserio;

import com.direwolf20.laserio.common.containers.customhandler.FilterBasicHandler;
import com.direwolf20.laserio.common.items.cards.BaseCard;
import com.direwolf20.laserio.setup.LaserIODataComponents;
import com.enderio.base.api.filter.FluidStackFilter;
import com.enderio.base.common.capability.IFilterCapability;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LaserFluidFilter implements IFilterCapability<FluidStack>, FluidStackFilter {

    private final ItemStack container;

    public LaserFluidFilter(ItemStack cardItem) {
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
    public int size() {
        return 0;
    }

    @Override
    public List<FluidStack> getEntries() {
        List<FluidStack> filteredFluids = new ArrayList();
        FilterBasicHandler filterSlotHandler = new FilterBasicHandler(15, container);

        for(int i = 0; i < (filterSlotHandler).getSlots(); ++i) {
            ItemStack itemStack = filterSlotHandler.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                Optional<IFluidHandlerItem> fluidHandlerLazyOptional = FluidUtil.getFluidHandler(itemStack);
                if (!fluidHandlerLazyOptional.isEmpty()) {
                    IFluidHandler fluidHandler = fluidHandlerLazyOptional.get();

                    for(int tank = 0; tank < fluidHandler.getTanks(); ++tank) {
                        FluidStack fluidStack = fluidHandler.getFluidInTank(tank);
                        if (!fluidStack.isEmpty()) {
                            filteredFluids.add(fluidStack);
                        }
                    }
                }
            }
        }

        return filteredFluids;
    }

    @Override
    public FluidStack getEntry(int index) {
        return null;
    }

    @Override
    public void setEntry(int index, FluidStack entry) {
        //Not needed for working filters, however could be good for in gui changes
    }

    @Override
    public boolean test(FluidStack stack) {
        for (FluidStack testStack : getEntries()) {
            boolean test = isNbt() ? FluidStack.isSameFluidSameComponents(testStack, stack) : testStack.is(stack.getFluid());
            if (test) {
                return !isInvert();
            }
        }

        return isInvert();
    }
}
