package com.enderio.machines.common.io.fluid;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class MachineFluidTank implements IFluidTank {
    private final int index;
    private final MachineFluidHandler handler;
    @NotNull
    private FluidStack fluid = FluidStack.EMPTY;

    public MachineFluidTank(int index, MachineFluidHandler handler) {
        this.index = index;
        this.handler = handler;
    }

    public static MachineFluidTank from(CompoundTag tag, int index, MachineFluidHandler handler) {
        FluidStack stack = FluidStack.loadFluidStackFromNBT(tag);
        MachineFluidTank machineFluidTank = new MachineFluidTank(index, handler);
        machineFluidTank.setFluid(stack);
        return machineFluidTank;
    }

    @Override
    public int getCapacity() {
        return handler.getTankCapacity(index);
    }

    @Override
    public @NotNull FluidStack getFluid() {
        return fluid;
    }

    public void setFluid(FluidStack fluid) {
        this.fluid = fluid;
    }

    @Override
    public int getFluidAmount() {
        return fluid.getAmount();
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

    protected void onContentsChanged() {}

    public CompoundTag save(CompoundTag compoundTag) {
        getFluid().writeToNBT(compoundTag);
        return compoundTag;
    }

}
