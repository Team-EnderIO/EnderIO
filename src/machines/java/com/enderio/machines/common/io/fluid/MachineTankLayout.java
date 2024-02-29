package com.enderio.machines.common.io.fluid;

import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Describes the tank layout of a machine
 */
public class MachineTankLayout {

    /**
     * Tank configurations
     */
    private final List<TankConfig> tanks;

    private MachineTankLayout(Builder builder) {
        this.tanks = List.copyOf(builder.tanks);
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getTankCount() {
        return tanks.size();
    }

    public int getTankCapacity(int slot) {
        return tanks.get(slot).capacity();
    }

    public boolean canInsert(int slot) {
        return tanks.get(slot).insert();
    }

    public boolean canExtract(int slot) {
        return tanks.get(slot).extract();
    }

    public boolean isFluidValid(int slot, FluidStack stack) {
        return tanks.get(slot).filter().test(stack);
    }

    public static class Builder {
        private final ArrayList<TankConfig> tanks = new ArrayList<>();

        public Builder tank(TankAccess access, int capacity) {
            return tank(access, capacity, t -> true);
        }

        public Builder tank(TankAccess access, int capacity, Predicate<FluidStack> filter) {
            return tank(access, capacity, true, true, filter);
        }
        public Builder tank(TankAccess access, int capacity, boolean canInsert, boolean canExtract, Predicate<FluidStack> filter) {
            tanks.add(new TankConfig(capacity, canInsert, canExtract, filter));
            access.init(tanks.size() - 1);
            return this;
        }

        public MachineTankLayout build() {
            return new MachineTankLayout(this);
        }
    }

    private record TankConfig(int capacity, boolean insert, boolean extract, Predicate<FluidStack> filter) {}

}
