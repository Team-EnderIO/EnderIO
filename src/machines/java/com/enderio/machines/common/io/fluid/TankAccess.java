package com.enderio.machines.common.io.fluid;

import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TankAccess {

    private int index = Integer.MIN_VALUE;

    public FluidStack getFluid(MachineBlockEntity blockEntity) {
        return getFluid(blockEntity.getFluidHandler());
    }

    public FluidStack getFluid(MachineFluidHandler handler) {
        return handler.getFluidInTank(index);
    }

    public int fill(MachineFluidHandler handler, FluidStack stack, IFluidHandler.FluidAction action) {
        return handler.fill(stack, action);
    }

    public int fill(MachineBlockEntity machine, FluidStack stack, IFluidHandler.FluidAction action) {
        return fill(machine.getFluidHandler(), stack, action);
    }

    public FluidStack drain(MachineFluidHandler handler, FluidStack resource, IFluidHandler.FluidAction action) {
        return handler.drain(resource, action);
    }

    public FluidStack drain(MachineBlockEntity machine, FluidStack resource, IFluidHandler.FluidAction action) {
        return drain(machine.getFluidHandler(), resource, action);
    }

    public FluidStack drain(MachineFluidHandler handler, int maxDrain, IFluidHandler.FluidAction action) {
        return handler.drain(maxDrain, action);
    }

    public FluidStack drain(MachineBlockEntity machine, int maxDrain, IFluidHandler.FluidAction action) {
        return drain(machine.getFluidHandler(), maxDrain, action);
    }

    public boolean isSlot(int slot) {
        return this.index == slot;
    }

    public int getIndex() {
        return index;
    }

    void init(int i) {
        if (index == Integer.MIN_VALUE) {
            index = i;
        } else if (index != i) {
            throw new IllegalArgumentException("TankLayout changed dynamically from " + index + " to " + i + ", don't do that");
        }
    }
}
