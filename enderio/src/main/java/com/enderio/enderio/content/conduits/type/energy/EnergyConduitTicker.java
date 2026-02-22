package com.enderio.enderio.content.conduits.type.energy;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTicker;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Comparator;
import java.util.List;

public class EnergyConduitTicker implements ConduitTicker<EnergyConduit> {

    public static final EnergyConduitTicker INSTANCE = new EnergyConduitTicker();

    public EnergyConduitTicker() {
    }

    @Override
    public void tick(ServerLevel level, EnergyConduit conduit, ConduitNetwork network) {
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
        List<Pair<EnergyHandler, Integer>> insertHandlers = Lists.newArrayList();
        for (var insertConnection : insertConnections) {
            int priority = insertConnection.connectionConfig(EnergyConduitConnectionConfig.TYPE).priority();
            if (priority != currentPriority) {
                // Distribute energy to everything in the previous priority group.
                long energyInserted = distributeTo(insertHandlers, context.energyStored());
                // TODO: 1.21.11: Need to review energy transactions - for now we just blindly set the context's energy storage.
                context.setEnergyStored(context.energyStored() - energyInserted, null);

                if (context.energyStored() <= 0) {
                    return;
                }

                // Setup for this priority group.
                insertHandlers.clear();
                currentPriority = priority;
            }

            // Do a test insert to see how much energy each possible handler is willing to take.
            var insertHandler = insertConnection.getSidedCapability(Capabilities.Energy.BLOCK);
            if (insertHandler != null) {
                try (Transaction transaction = Transaction.openRoot()) {
                    int receivableEnergy = insertHandler.insert(Integer.MAX_VALUE, transaction);
                    if (receivableEnergy > 0) {
                        insertHandlers.add(Pair.of(insertHandler, receivableEnergy));
                    }
                }
            }
        }

        // Final distribution if we still have handlers
        if (!insertHandlers.isEmpty() && context.energyStored() > 0) {
            // Distribute energy to everything in the previous priority group.
            long energyInserted = distributeTo(insertHandlers, context.energyStored());

            // TODO: 1.21.11: Need to review energy transactions - for now we just blindly set the context's energy storage.
            context.setEnergyStored(context.energyStored() - energyInserted, null);
        }
    }

    private long distributeTo(List<Pair<EnergyHandler, Integer>> insertHandlers, long availableEnergy) {
        // Try to fill smaller buffers first.
        insertHandlers.sort((a, b) -> Integer.compare(b.right(), a.right()));

        // TODO: 1.21.11: This is a primitive use of transactions; need to check it.
        try (Transaction transaction = Transaction.openRoot()) {
            long energyRemaining = availableEnergy;
            int toShareWith = insertHandlers.size();

            for (var handler : insertHandlers) {
                // If we have too little energy left, just give it to the first handler that will accept it all
                int energyInserted;
                if (energyRemaining < toShareWith) {
                    // If we're smaller than an int, we can just cast.
                    energyInserted = handler.left().insert(Ints.saturatedCast(energyRemaining), transaction);
                } else {
                    // Don't insert more than INT_MAX :)
                    energyInserted = handler.left().insert(Ints.saturatedCast(energyRemaining / toShareWith), transaction);
                }

                // One less to share with now.
                toShareWith--;

                energyRemaining -= energyInserted;
                if (energyRemaining <= 0) {
                    break;
                }
            }

            transaction.commit();
            return availableEnergy - energyRemaining;
        }
    }
}
