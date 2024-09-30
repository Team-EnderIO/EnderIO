package com.enderio.base.common.menu;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;
import java.util.function.Consumer;

public class FluidFilterSlot extends FilterSlot<FluidStack> {

    public FluidFilterSlot(Consumer<FluidStack> consumer, int pSlot, int pX, int pY) {
        super(consumer, pSlot, pX, pY);
    }

    @Override
    protected Optional<FluidStack> getResourceFrom(ItemStack itemStack) {
        IFluidHandlerItem capability = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability != null) {
            return Optional.of(capability.getFluidInTank(0).copy());
        }

        return Optional.empty();
    }
}
