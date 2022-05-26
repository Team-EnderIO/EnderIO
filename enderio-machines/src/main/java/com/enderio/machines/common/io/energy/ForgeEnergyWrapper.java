package com.enderio.machines.common.io.energy;

import com.enderio.api.energy.IMachineEnergy;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Forge energy wrapper.
 * Used to wrap the different sides with a different {@link IEnergyStorage} for each so that side control can be easily maintained.
 */
public final class ForgeEnergyWrapper {
    private final Map<Direction, LazyOptional<Side>> sideCache;
    private final LazyOptional<Side> nullSide;

    public ForgeEnergyWrapper(IMachineEnergy wrapped) {
        HashMap<Direction, LazyOptional<Side>> sides = new HashMap<>();
        for (Direction dir : Direction.values()) {
            sides.put(dir, LazyOptional.of(() -> new Side(wrapped, dir)));
        }
        this.sideCache = Map.copyOf(sides);

        nullSide = LazyOptional.of(() -> new Side(wrapped, null));
    }

    /**
     * Get {@link IEnergyStorage} capability for the given side.
     */
    public LazyOptional<IEnergyStorage> getCapability(@Nullable Direction side) {
        if (side == null) {
            return nullSide.cast();
        }
        return sideCache.get(side).cast();
    }

    public void invalidateCaps() {
        for (Direction dir : Direction.values()) {
            sideCache.get(dir).invalidate();
        }
        nullSide.invalidate();
    }

    private record Side(IMachineEnergy wrapped, @Nullable Direction side) implements IEnergyStorage {

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!canReceive()) return 0;
            int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(wrapped.getMaxEnergyTransfer(), maxReceive));
            if (!simulate) {
                wrapped.addEnergy(energyReceived);
            }
            return energyReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!canExtract()) return 0;
            int energyExtracted = Math.min(getEnergyStored(), Math.min(wrapped.getMaxEnergyTransfer(), maxExtract));
            if (!simulate) {
                wrapped.addEnergy(-energyExtracted);
            }
            return energyExtracted;
        }

        @Override
        public int getEnergyStored() {
            return wrapped.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return wrapped.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return wrapped.canExtractEnergy(side) && wrapped.getMaxEnergyTransfer() > 0;
        }

        @Override
        public boolean canReceive() {
            return wrapped.canInsertEnergy(side) && wrapped.getMaxEnergyTransfer() > 0;
        }
    }
}
