package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.conduits.api.network.IConduitNetwork;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class EnergyConduitTicker implements ConduitTicker<EnergyConduit> {

    public static final EnergyConduitTicker INSTANCE = new EnergyConduitTicker();

    public EnergyConduitTicker() {
    }

    @Override
    public void tick(ServerLevel level, EnergyConduit conduit, IConduitNetwork network) {
        var insertConnections = network.insertConnections();
        if (insertConnections.isEmpty()) {
            return;
        }

        EnergyConduitNetworkContext context = network.getContext(EnergyConduitNetworkContext.TYPE);
        if (context == null) {
            return;
        }

        if (context.energyStored() <= 0) {
            return;
        }

        // insert connections list is sorted by priority so we can just go until we find a change in priority group.
        int currentPriority = insertConnections.getFirst().connectionConfig(EnergyConduitConnectionConfig.TYPE).priority();
        List<Pair<IEnergyStorage, Integer>> insertHandlers = Lists.newArrayList();
        for (var insertConnection : insertConnections) {
            int priority = insertConnection.connectionConfig(EnergyConduitConnectionConfig.TYPE).priority();
            if (priority != currentPriority) {
                // Distribute energy to everything in the previous priority group.
                long energyInserted = distributeTo(insertHandlers, context.energyStored());
                context.setEnergyStored(context.energyStored() - energyInserted);

                if (context.energyStored() <= 0) {
                    return;
                }

                // Setup for this priority group.
                insertHandlers.clear();
                currentPriority = priority;
            }

            IEnergyStorage insertHandler = insertConnection.getSidedCapability(Capabilities.EnergyStorage.BLOCK);
            if (insertHandler != null && insertHandler.canReceive()) {
                int receivableEnergy = insertHandler.receiveEnergy(Integer.MAX_VALUE, true);
                if (receivableEnergy > 0) {
                    insertHandlers.add(Pair.of(insertHandler, receivableEnergy));
                }
            }
        }

        // Final distribution if we still have handlers
        if (!insertHandlers.isEmpty() && context.energyStored() > 0) {
            // Distribute energy to everything in the previous priority group.
            long energyInserted = distributeTo(insertHandlers, context.energyStored());
            context.setEnergyStored(context.energyStored() - energyInserted);
        }
    }

    private long distributeTo(List<Pair<IEnergyStorage, Integer>> insertHandlers, long availableEnergy) {
        // Try to fill smaller buffers first.
        insertHandlers.sort((a, b) -> Integer.compare(b.right(), a.right()));

        long energyRemaining = availableEnergy;
        int toShareWith = insertHandlers.size();
        for (var handler : insertHandlers) {
            // If we have too little energy left, just give it to the first handler that will accept it all
            int energyInserted;
            if (energyRemaining < toShareWith) {
                // If we're smaller than an int, we can just cast.
                energyInserted = handler.left().receiveEnergy((int)energyRemaining, false);
            } else {
                // Don't insert more than INT_MAX :)
                energyInserted = handler.left().receiveEnergy((int)Math.min(energyRemaining / toShareWith, Integer.MAX_VALUE), false);
            }

            // One less to share with now.
            toShareWith--;

            energyRemaining -= energyInserted;
            if (energyRemaining <= 0) {
                break;
            }
        }

        return availableEnergy - energyRemaining;
    }
}
