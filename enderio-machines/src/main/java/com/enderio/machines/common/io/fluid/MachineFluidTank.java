package com.enderio.machines.common.io.fluid;

import com.enderio.api.capability.ICapabilityProvider;
import com.enderio.api.io.IIOConfig;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import java.util.EnumMap;

public class MachineFluidTank extends FluidTank implements ICapabilityProvider<IFluidTank> {

    private final IIOConfig config;

    private final EnumMap<Direction, LazyOptional<Sided>> sideCache = new EnumMap<>(Direction.class);

    public MachineFluidTank(int capacity, IIOConfig config) {
        super(capacity);
        this.config = config;
    }

    public IIOConfig getConfig() {
        return config;
    }

    // region Sided access

    @Override
    public LazyOptional<IFluidTank> getCapability(Direction side) {
        return sideCache.computeIfAbsent(side, dir -> LazyOptional.of(() -> new Sided(this, dir))).cast();
    }

    @Override
    public void invalidateCaps() {
        for (LazyOptional<Sided> side : sideCache.values()) {
            side.invalidate();
        }
    }

    private static class Sided implements IFluidHandler {

        private final MachineFluidTank master;
        private final Direction direction;

        public Sided(MachineFluidTank master, Direction direction) {
            this.master = master;
            this.direction = direction;
        }

        @Override
        public int getTanks() {
            return master.getTanks();
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return master.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return master.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return master.isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (master.getConfig().getMode(direction).canInput())
                return master.fill(resource, action);
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (master.getConfig().getMode(direction).canOutput())
                return master.drain(resource, action);
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (master.getConfig().getMode(direction).canOutput())
                return master.drain(maxDrain, action);
            return FluidStack.EMPTY;
        }
    }

    // endregion
}
