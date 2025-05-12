package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.conduits.api.network.node.IConduitNode;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public record EnergyConduitStorage(boolean isMutable, int transferRate, @Nullable IConduitNode node) implements IEnergyStorage {

    private static final long ENERGY_BUFFER_SCALER = 4;

    public long getLongMaxEnergyStored() {
        if (node == null || !node.isLoaded()) {
            return 0;
        }

        // Capacity is transfer rate + nodeCount * transferRatePerTick / 2 (expanded).
        // This ensures at least the transfer rate of the cable is available, but
        // capacity doesn't grow outrageously.
        int nodeCount = node.getNetwork().nodeCount();

        // The maximum number of nodes before the network capacity is INT_MAX.
        long maxNodesBeforeLimit = Long.MAX_VALUE / (transferRate() / ENERGY_BUFFER_SCALER) - ENERGY_BUFFER_SCALER;
        if (nodeCount >= maxNodesBeforeLimit) {
            return Long.MAX_VALUE;
        }

        // Always full transfer rate plus the extra buffer.
        return transferRate() + nodeCount * (transferRate() / ENERGY_BUFFER_SCALER);
    }

    public long getLongEnergyStored() {
        if (node == null || !node.isLoaded()) {
            return 0;
        }

        var context = node.getNetwork().getContext(EnergyConduitNetworkContext.TYPE);
        if (context == null) {
            return 0;
        }

        return Math.max(Math.min(getLongMaxEnergyStored(), context.energyStored()), 0);
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (node == null || !node.isLoaded() || !isMutable) {
            return 0;
        }

        var context = node.getNetwork().getOrCreateContext(EnergyConduitNetworkContext.TYPE);

        // Cap to transfer rate.
        toReceive = Math.min(transferRate(), toReceive);

        int energyReceived = (int)Math.min(Math.min(getLongMaxEnergyStored() - getLongEnergyStored(), toReceive), Integer.MAX_VALUE);
        if (!simulate) {
            context.setEnergyStored(getLongEnergyStored() + energyReceived);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return (int)Math.min(getLongEnergyStored(), Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int)Math.min(getLongMaxEnergyStored(), Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    // The block will not expose this capability unless it can be extracted from
    // This means we don't have to worry about checking if we can extract at this
    // point.
    @Override
    public boolean canReceive() {
        return true;
    }
}
