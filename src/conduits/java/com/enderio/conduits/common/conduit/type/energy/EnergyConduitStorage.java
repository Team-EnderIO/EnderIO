package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitNode;
import com.enderio.conduits.common.init.Conduits;
import net.neoforged.neoforge.energy.IEnergyStorage;

public record EnergyConduitStorage(
    int transferRate,
    ConduitNode node
) implements IEnergyStorage {

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        EnergyConduitNetworkContext context = node.getParentGraph().getOrCreateContext(Conduits.ContextSerializers.ENERGY.get());

        // Cap to transfer rate.
        // TODO: Do we cap the transfer rate at all, or should we receive as much as we can and only cap output?
        toReceive = Math.min(transferRate() - context.energyInsertedThisTick(), toReceive);

        int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), toReceive);
        if (!simulate) {
            context.setEnergyInsertedThisTick(context.energyInsertedThisTick() + energyReceived);
            context.setEnergyStored(getEnergyStored() + energyReceived);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        EnergyConduitNetworkContext context = node.getParentGraph().getContext(Conduits.ContextSerializers.ENERGY.get());
        if (context == null) {
            return 0;
        }

        return Math.max(Math.min(getMaxEnergyStored(), context.energyStored()), 0);
    }

    @Override
    public int getMaxEnergyStored() {
        // TODO: Handle int overflowing here...

        // Capacity is transfer rate + nodeCount * transferRate / 2 (expanded).
        // This ensures at least the transfer rate of the cable is available, but capacity doesn't grow outrageously.
        return (1 + node.getParentGraph().getNodes().size()) * transferRate() / 4;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    // The block will not expose this capability unless it can be extracted from
    // This means we don't have to worry about checking if we can extract at this point.
    @Override
    public boolean canReceive() {
        return node.getParentGraph() != null;
    }
}
