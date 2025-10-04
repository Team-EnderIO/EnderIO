package com.enderio.enderio.common.content.filters.fluid;

import com.enderio.enderio.common.content.filters.FilterSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidFilterSlot extends FilterSlot<FluidStack> {

    public FluidFilterSlot(Supplier<FluidStack> getter, Consumer<FluidStack> setter, int pSlot, int pX, int pY) {
        super(getter, setter, pSlot, pX, pY);
    }

    @Override
    protected FluidStack emptyResource() {
        return FluidStack.EMPTY;
    }

    @Override
    public Optional<FluidStack> getResourceFrom(ItemStack itemStack) {
        IFluidHandlerItem capability = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability != null) {
            var fluid = capability.getFluidInTank(0).copy();
            if (!fluid.isEmpty()) {
                return Optional.of(fluid);
            }
        }

        return Optional.empty();
    }
}
