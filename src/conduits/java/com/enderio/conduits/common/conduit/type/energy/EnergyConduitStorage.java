package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNode;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public record EnergyConduitStorage(
    EnergyConduitOptions options,
    ConduitNode<EnergyConduitNetworkContext, ConduitData.EmptyConduitData> node
) implements IEnergyStorage {

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        EnergyConduitNetworkContext context = node.getParentGraph().getContext();
        if (context == null) {
            return 0;
        }

        // Cap to transfer rate.
        // TODO: Do we cap the transfer rate at all, or should we receive as much as we can and only cap output?
        toReceive = Math.min(options.transferLimit() - context.energyInsertedThisTick(), toReceive);

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
        EnergyConduitNetworkContext context = node.getParentGraph().getContext();
        return Math.min(getMaxEnergyStored(), context.energyStored());
    }

    @Override
    public int getMaxEnergyStored() {
        return node.getParentGraph().getNodes().size() * options.transferLimit() / 2;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    // The block will not expose this capability unless it can be extracted from
    // This means we don't have to worry about checking if we can extract at this point.
    @Override
    public boolean canReceive() {
        return node.getParentGraph() != null && node.getParentGraph().getContext() != null;
    }
}
