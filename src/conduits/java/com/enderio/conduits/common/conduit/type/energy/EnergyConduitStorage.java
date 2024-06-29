package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitGraph;
import net.neoforged.neoforge.energy.IEnergyStorage;

public record EnergyConduitStorage(
    EnergyConduitOptions options,
    ConduitGraph<EnergyConduitGraphContext, ConduitData.EmptyConduitData> graph
) implements IEnergyStorage {

    // TODO: EnergyConduitOptions for rates.

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        // Cap to transfer rate.
        toReceive = Math.min(options.transferRate(), toReceive);

        int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), toReceive);
        if (!simulate) {
            graph.getContext().setEnergyStored(graph.getContext().energyStored() + energyReceived);
        }

        return energyReceived;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return Math.min(getMaxEnergyStored(), graph.getContext().energyStored());
    }

    @Override
    public int getMaxEnergyStored() {
        return graph.getNodes().size() * options.transferRate() / 2;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    // The block will not expose this capability unless it can be extracted from
    // This means we don't have to worry about checking if we can extract at this point.
    @Override
    public boolean canReceive() {
        return graph.getContext() != null;
    }
}
