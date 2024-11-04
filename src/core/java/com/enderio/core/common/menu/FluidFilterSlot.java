package com.enderio.core.common.menu;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.function.Consumer;

import java.util.Optional;

public class FluidFilterSlot extends FilterSlot<FluidStack> {

    public FluidFilterSlot(Consumer<FluidStack> consumer, int pSlot, int pX, int pY) {
        super(consumer, pSlot, pX, pY);
    }

    @Override
    public Optional<FluidStack> getResourceFrom(ItemStack itemStack) {
        LazyOptional<IFluidHandlerItem> capabilityOptional = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);

        return capabilityOptional.map(capability -> {
            var fluid = capability.getFluidInTank(0).copy();
            if (!fluid.isEmpty()) {
                return Optional.of(fluid);
            }

            return Optional.<FluidStack>empty();
        }).orElse(Optional.empty());
    }
}
