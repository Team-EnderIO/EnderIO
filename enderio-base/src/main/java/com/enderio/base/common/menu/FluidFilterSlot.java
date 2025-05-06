package com.enderio.base.common.menu;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class FluidFilterSlot extends FilterSlot<FluidStack> {

    private final Supplier<FluidStack> getter;

    public FluidFilterSlot(Supplier<FluidStack> getter, Consumer<FluidStack> consumer, int pSlot, int pX, int pY) {
        super(consumer, pSlot, pX, pY);

        this.getter = getter;
    }

    public FluidStack getFluid() {
        return getter.get();
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
