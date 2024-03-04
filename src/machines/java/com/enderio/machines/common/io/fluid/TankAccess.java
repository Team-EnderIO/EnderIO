package com.enderio.machines.common.io.fluid;

import com.enderio.machines.common.attachment.IFluidTankUser;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class TankAccess {

    private int index = Integer.MIN_VALUE;

    public MachineFluidTank getTank(IFluidTankUser machine) {
        return getTank(machine.getFluidHandler());
    }

    public MachineFluidTank getTank(MachineFluidHandler fluidHandler) {
        return fluidHandler.getTank(index);
    }

    public int getCapacity(IFluidTankUser machine) {
        return getCapacity(machine.getFluidHandler());
    }

    public int getCapacity(MachineFluidHandler fluidHandler) {
        return fluidHandler.getTankCapacity(index);
    }

    public FluidStack getFluid(IFluidTankUser machine) {
        return getFluid(machine.getFluidHandler());
    }

    public FluidStack getFluid(MachineFluidHandler handler) {
        return handler.getFluidInTank(index);
    }

    public int getFluidAmount(IFluidTankUser machine) {
        return getFluid(machine).getAmount();
    }

    public int getFluidAmount(MachineFluidHandler handler) {
        return getFluid(handler).getAmount();
    }

    public void setFluid(IFluidTankUser machine, FluidStack fluid) {
        setFluid(machine.getFluidHandler(), fluid);
    }

    public void setFluid(MachineFluidHandler handler, FluidStack fluid) {
        handler.setFluidInTank(index, fluid);
    }

    public boolean isFluidValid(IFluidTankUser machine, FluidStack fluid) {
        return isFluidValid(machine.getFluidHandler(), fluid);
    }

    public boolean isFluidValid(MachineFluidHandler handler, FluidStack fluid) {
        return handler.isFluidValid(index, fluid);
    }

    public boolean isEmpty(IFluidTankUser machine) {
        return isEmpty(machine.getFluidHandler());
    }

    public boolean isEmpty(MachineFluidHandler handler) {
        return getFluid(handler).isEmpty();
    }

    public int fill(MachineFluidHandler handler, FluidStack stack, IFluidHandler.FluidAction action) {
        return handler.fill(index, stack, action);
    }

    public int fill(IFluidTankUser machine, FluidStack stack, IFluidHandler.FluidAction action) {
        return fill(machine.getFluidHandler(), stack, action);
    }

    public FluidStack drain(MachineFluidHandler handler, FluidStack resource, IFluidHandler.FluidAction action) {
        return handler.drain(index, resource, action);
    }

    public FluidStack drain(IFluidTankUser machine, FluidStack resource, IFluidHandler.FluidAction action) {
        return drain(machine.getFluidHandler(), resource, action);
    }

    public FluidStack drain(MachineFluidHandler handler, int maxDrain, IFluidHandler.FluidAction action) {
        return handler.drain(index, maxDrain, action);
    }

    public FluidStack drain(IFluidTankUser machine, int maxDrain, IFluidHandler.FluidAction action) {
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
