package com.enderio.machines.common.io.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

//Replace MachineFluidTank
public class MachineTank implements IFluidTank {

    public static final String Capacity = "Capacity";

    private final int capacity;
    @NotNull private FluidStack fluid = FluidStack.EMPTY;
    public static final MachineTank EMPTY = new MachineTank(0);

    public MachineTank(int capacity) {
        this.capacity = capacity;
    }

    public MachineTank(FluidStack stack, int capacity) {
        this(capacity);
        this.fluid = stack.copy();
    }

    public static MachineTank from(CompoundTag tag) {
        FluidStack stack = FluidStack.loadFluidStackFromNBT(tag);
        int capacity = tag.getInt(Capacity);
        return new MachineTank(stack, capacity);
    }

    @Override
    public int getCapacity() {
        return capacity;
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
        return true;
    }

    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty())
            return 0;

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
        if (resource.isEmpty() || !resource.isFluidEqual(fluid))
            return FluidStack.EMPTY;

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

    protected void onContentsChanged() {}

    public CompoundTag save(CompoundTag compoundTag) {
        getFluid().writeToNBT(compoundTag);
        compoundTag.putInt(Capacity, getCapacity());
        return compoundTag;
    }

}
