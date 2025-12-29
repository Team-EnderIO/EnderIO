package com.enderio.enderio.content.filters.fluid;

import com.enderio.enderio.content.filters.FilterSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.transfer.access.ItemAccess;

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
        var fluidHandler = itemStack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(itemStack));
        if (fluidHandler != null) {
            if (fluidHandler.getAmountAsInt(0) > 0) {
                return Optional.of(fluidHandler.getResource(0).toStack(fluidHandler.getAmountAsInt(0)));
            }
        }

        return Optional.empty();
    }
}
