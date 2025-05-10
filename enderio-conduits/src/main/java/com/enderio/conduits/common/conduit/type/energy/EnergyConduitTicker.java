package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.conduits.api.network.IConduitNetwork;
import com.enderio.conduits.api.ticker.ConduitTicker;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyConduitTicker implements ConduitTicker<EnergyConduit> {

    public static final EnergyConduitTicker INSTANCE = new EnergyConduitTicker();

    public EnergyConduitTicker() {
    }

    @Override
    public void tick(ServerLevel level, EnergyConduit conduit, IConduitNetwork network) {
        final int transferRate = conduit.transferRatePerTick() * conduit.networkTickRate();
        EnergyConduitNetworkContext context = network.getContext(EnergyConduitNetworkContext.TYPE);
        if (context == null) {
            return;
        }

        if (context.energyStored() <= 0) {
            return;
        }

        var senders = network.sendingConnections();

        // Revert overflow.
        if (senders.size() <= context.rotatingIndex()) {
            context.setRotatingIndex(0);
        }

        int startingRotatingIndex = context.rotatingIndex();
        for (int i = startingRotatingIndex; i < startingRotatingIndex + senders.size(); i++) {
            int insertIndex = i % senders.size();
            var sendingConnection = senders.get(insertIndex);

            IEnergyStorage insertHandler = sendingConnection.getSidedCapability(Capabilities.EnergyStorage.BLOCK);
            if (insertHandler == null || !insertHandler.canReceive()) {
                continue;
            }

            int energyToInsert = Math.min(transferRate, Math.max(context.energyStored(), 0));
            int energyInserted = insertHandler.receiveEnergy(energyToInsert, false);
            context.setEnergyStored(context.energyStored() - energyInserted);
            context.setRotatingIndex(insertIndex + 1);
            if (context.energyStored() <= 0) {
                // If we are out of energy then stop the loop so we start at the next
                // index next time around to spread out any new energy
                break;
            }
        }
    }
}
