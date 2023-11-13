package com.enderio.machines.common.io.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

//Replace MachineFluidTank
public class MachineTank implements IFluidTank {

    private final int capacity;
    private final Predicate<FluidStack> validator;

    private boolean canInsert;

    private boolean canExtract;
    @NotNull private FluidStack fluid = FluidStack.EMPTY;

    public MachineTank(int capacity, Predicate<FluidStack> validator, boolean canInsert, boolean canExtract) {
        this.capacity = capacity;
        this.validator = validator;
        this.canInsert = canInsert;
        this.canExtract = canExtract;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public @NotNull FluidStack getFluid() {
        return fluid;
    }

    @Override
    public int getFluidAmount() {
        return fluid.getAmount();
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!canInsert || resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!canExtract || resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        int drained = maxDrain;
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

    protected void onContentsChanged() {

    }

}
