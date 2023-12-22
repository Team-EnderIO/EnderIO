package com.enderio.machines.common.io.fluid;

import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TankAccess {

    private int index = Integer.MIN_VALUE;

    public MachineFluidTank getTank(MachineBlockEntity machine) {
        return getTank(machine.getFluidHandler());
    }

    public MachineFluidTank getTank(MachineFluidHandler fluidHandler) {
        return fluidHandler.getTank(index);
    }

    public int getCapacity(MachineBlockEntity machine) {
        return getCapacity(machine.getFluidHandler());
    }

    public int getCapacity(MachineFluidHandler fluidHandler) {
        return fluidHandler.getTankCapacity(index);
    }

    public FluidStack getFluid(MachineBlockEntity machine) {
        return getFluid(machine.getFluidHandler());
    }

    public FluidStack getFluid(MachineFluidHandler handler) {
        return handler.getFluidInTank(index);
    }

    public int getFluidAmount(MachineBlockEntity machine) {
        return getFluid(machine).getAmount();
    }

    public int getFluidAmount(MachineFluidHandler handler) {
        return getFluid(handler).getAmount();
    }

    public void setFluid(MachineBlockEntity machine, FluidStack fluid) {
        setFluid(machine.getFluidHandler(), fluid);
    }

    public void setFluid(MachineFluidHandler handler, FluidStack fluid) {
        handler.setFluidInTank(index, fluid);
    }

    public boolean isFluidValid(MachineBlockEntity machine, FluidStack fluid) {
        return isFluidValid(machine.getFluidHandler(), fluid);
    }

    public boolean isFluidValid(MachineFluidHandler handler, FluidStack fluid) {
        return handler.isFluidValid(index, fluid);
    }

    public boolean isEmpty(MachineBlockEntity machine) {
        return isEmpty(machine.getFluidHandler());
    }

    public boolean isEmpty(MachineFluidHandler handler) {
        return getFluid(handler).isEmpty();
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

    public boolean isTank(int slot) {
        return this.index == slot;
    }

    void init(int i) {
        if (index == Integer.MIN_VALUE) {
            index = i;
        } else if (index != i) {
            throw new IllegalArgumentException("TankLayout changed dynamically from " + index + " to " + i + ", don't do that");
        }
    }
}
