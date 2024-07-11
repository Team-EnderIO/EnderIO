package com.enderio.machines.common.io.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class MachineFluidTank implements IFluidTank {
    private final int index;
    private final MachineFluidHandler handler;

    public MachineFluidTank(int index, MachineFluidHandler handler) {
        this.index = index;
        this.handler = handler;
    }

    @Override
    public int getCapacity() {
        return handler.getTankCapacity(index);
    }

    @Override
    public @NotNull FluidStack getFluid() {
        return handler.getFluidInTank(index);
    }

    public void setFluid(FluidStack fluid) {
        handler.setFluidInTank(index, fluid);
    }

    @Override
    public int getFluidAmount() {
        return getFluid().getAmount();
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return handler.isFluidValid(index, stack);
    }

    public boolean isEmpty() {
        return getFluid().isEmpty();
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return handler.fill(index, resource, action);
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return handler.drain(index, resource, action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return handler.drain(index, maxDrain, action);
    }
}
