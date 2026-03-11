package com.enderio.enderio.content.filters.fluid;

import com.enderio.enderio.content.filters.FilterSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidFilterSlot extends FilterSlot<FluidStack> {

    public FluidFilterSlot(Supplier<FluidStack> getter, Consumer<FluidStack> setter, int slot, int x, int y) {
        super(getter, setter, slot, x, y);
    }

    @Override
    protected FluidStack emptyResource() {
        return FluidStack.EMPTY;
    }

    @Override
    public Optional<FluidStack> getResourceFrom(ItemStack itemStack) {
        IFluidHandlerItem capability = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (capability != null) {
            var fluid = capability.getFluidInTank(0).copy();
            if (!fluid.isEmpty()) {
                return Optional.of(fluid);
            }
        }

        return Optional.empty();
    }
}
