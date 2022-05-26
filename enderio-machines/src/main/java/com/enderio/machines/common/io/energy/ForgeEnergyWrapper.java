package com.enderio.machines.common.io.energy;

import com.enderio.api.capability.IEnderCapabilityProvider;
import com.enderio.api.energy.IMachineEnergy;
import com.enderio.api.io.IIOConfig;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.EnumMap;

/**
 * Forge energy wrapper.
 * Used to wrap the different sides with a different {@link IEnergyStorage} for each so that side control can be easily maintained.
 */
// TODO: Do we want to revert the whole IMachineEnergy change?
public final class ForgeEnergyWrapper implements IEnderCapabilityProvider<IEnergyStorage> {
    /**
     * Machine's io config
     */
    private final IIOConfig config;

    /**
     * The wrapped energy holder.
     */
    private final IMachineEnergy wrapped;

    /**
     * Cached map of all sided access capabilities.
     */
    private final EnumMap<Direction, LazyOptional<Side>> sideCache = new EnumMap<>(Direction.class);

    public ForgeEnergyWrapper(IIOConfig config, IMachineEnergy wrapped) {
        this.config = config;
        this.wrapped = wrapped;
    }

    @Override
    public Capability<IEnergyStorage> getCapabilityType() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    public LazyOptional<IEnergyStorage> getCapability(Direction side) {
        if (!config.getMode(side).canConnect())
            return LazyOptional.empty();
        return sideCache.computeIfAbsent(side, direction -> LazyOptional.of(() -> new Side(wrapped, direction))).cast();
    }

    @Override
    public void invalidateSide(Direction side) {
        if (sideCache.containsKey(side)) {
            sideCache.get(side).invalidate();
            sideCache.remove(side);
        }
    }

    @Override
    public void invalidateCaps() {
        for (LazyOptional<Side> side : sideCache.values()) {
            side.invalidate();
        }
    }

    private record Side(IMachineEnergy wrapped, Direction side) implements IEnergyStorage {
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
