package com.enderio.modconduits.common.modules.laserio;

import com.direwolf20.laserio.common.containers.FilterBasicContainer;
import com.direwolf20.laserio.common.containers.customhandler.FilterBasicHandler;
import com.direwolf20.laserio.setup.LaserIODataComponents;
import com.enderio.base.api.filter.FluidFilter;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

public class LaserFluidFilter implements FluidFilter {

    private final ItemStack container;

    public LaserFluidFilter(ItemStack container) {
        this.container = container;
    }

    @Override
    public FluidStack test(@Nullable IFluidHandler target, FluidStack stack) {
        final boolean isAllowList = container.getOrDefault(LaserIODataComponents.FILTER_ALLOW, true);
        final boolean shouldCompareComponents = container.getOrDefault(LaserIODataComponents.FILTER_COMPARE, false);

        FilterBasicHandler filterSlotHandler = new FilterBasicHandler(FilterBasicContainer.SLOTS, container);

        for (int i = 0; i < filterSlotHandler.getSlots(); ++i) {
            ItemStack itemStack = filterSlotHandler.getStackInSlot(i);

            if (!itemStack.isEmpty()) {
                Optional<IFluidHandlerItem> fluidHandlerLazyOptional = FluidUtil.getFluidHandler(itemStack);
                if (fluidHandlerLazyOptional.isPresent()) {
                    IFluidHandler fluidHandler = fluidHandlerLazyOptional.get();

                    for (int tank = 0; tank < fluidHandler.getTanks(); ++tank) {
                        FluidStack fluidStack = fluidHandler.getFluidInTank(tank);
                        if (!fluidStack.isEmpty()) {
                            if (shouldCompareComponents ? FluidStack.isSameFluid(fluidStack, stack)
                                    : FluidStack.isSameFluidSameComponents(fluidStack, stack)) {
                                return isAllowList ? fluidStack : FluidStack.EMPTY;
                            }
                        }
                    }
                }
            }
        }

        return isAllowList ? FluidStack.EMPTY : stack;
    }
}
