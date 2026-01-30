package com.enderio.modded_conduits.common.modules.mekanism.chemical;

import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.network.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.Objects;

public class ChemicalTicker extends ConduitTickerBase<ChemicalConduit> {

    public static final ChemicalTicker INSTANCE = new ChemicalTicker();

    private ChemicalTicker() {}

    @Override
    protected ConduitType<ChemicalConduit> conduitType() {
        return MekanismModule.TYPE_CHEMICAL.get();
    }

    @Override
    public int tickRate() {
        return 5;
    }

    @Override
    protected void tickNetwork(ServerLevel level, ConduitNetwork network, int tickOffset) {
        var context = network.getOrCreateContext(ChemicalConduitNetworkContext.TYPE);

        boolean hadMultiChemical = false;
        for (var channel : network.allChannels()) {
            for (var extractConnection : network.extractConnections(channel)) {
                var insertConnections = network.insertConnectionsFrom(extractConnection);
                if (insertConnections.isEmpty()) {
                    continue;
                }

                var extractConduit = extractConnection.node().conduit(conduitType());
                final long transferRate = extractConduit.value().transferRatePerTick() * tickRate();

                IChemicalHandler extractHandler = extractConnection
                        .getSidedCapability(MekanismModule.Capabilities.CHEMICAL);
                if (extractHandler == null) {
                    continue;
                }

                if (!context.lockedChemical().isEmptyType()) {
                    doChemicalTransfer(context.lockedChemical(), transferRate, extractConnection, insertConnections);
                } else {
                    long remaining = transferRate;

                    for (int i = 0; i < extractHandler.getChemicalTanks() && remaining > 0; i++) {
                        if (extractHandler.getChemicalInTank(i).isEmpty()) {
                            continue;
                        }

                        Chemical chemical = extractHandler.getChemicalInTank(i).getChemical();
                        remaining = doChemicalTransfer(chemical, remaining, extractConnection, insertConnections);

                        if (!extractConduit.value().isMultiChemical() && remaining < transferRate) {
                            context.setLockedChemical(chemical);
                            break;
                        }
                    }
                }
            }
        }

        // Mark nodes as dirty if we've acquired a new locked fluid
        if (hadMultiChemical) {
            if (context != null && !context.lockedChemical().equals(context.lastLockedChemical())) {
                context.clearLastLockedChemical();
                for (var node : network.tickingNodes()) {
                    node.markDirty();
                }
            }
        }
    }

    private long doChemicalTransfer(Chemical chemical, long maxTransfer, ConduitBlockConnection extractConnection,
            List<ConduitBlockConnection> insertConnections) {
        var receiverHandler = Objects
                .requireNonNull(extractConnection.getSidedCapability(MekanismModule.Capabilities.CHEMICAL));

        // Attempt to drain chemical from the target.
        var extractedChemical = receiverHandler.extractChemical(new ChemicalStack(chemical, maxTransfer),
                Action.SIMULATE);
        if (extractedChemical.isEmpty()) {
            return maxTransfer;
        }

        var insertConduit = extractConnection.node().conduit(conduitType());
        // TODO: When we add path speeds, we'll restrict to the path speed instead.'
        final long maxInsertSpeed = insertConduit.value().transferRatePerTick() * tickRate();
        if (extractedChemical.getAmount() > maxInsertSpeed) {
            extractedChemical.setAmount(maxInsertSpeed);
        }

        // Test the extracted fluid against the target
        var extractFilter = extractConnection.inventory()
                .getStackInSlot(ChemicalConduit.EXTRACT_FILTER_SLOT)
                .getCapability(MekanismModule.Capabilities.CHEMICAL_FILTER);

        if (extractFilter != null) {
            extractedChemical = extractFilter.test(receiverHandler, extractedChemical);
            if (extractedChemical.isEmpty()) {
                return maxTransfer;
            }
        }

        // Insert into any available blocks
        for (var insertConnection : insertConnections) {
            IChemicalHandler insertHandler = insertConnection.getSidedCapability(MekanismModule.Capabilities.CHEMICAL);
            if (insertHandler == null) {
                continue;
            }

            var chemicalToInsert = extractedChemical.copy();

            // Test fluid against insert filter.
            var insertFilter = insertConnection.inventory()
                    .getStackInSlot(ChemicalConduit.INSERT_FILTER_SLOT)
                    .getCapability(MekanismModule.Capabilities.CHEMICAL_FILTER);

            if (insertFilter != null) {
                chemicalToInsert = insertFilter.test(insertHandler, chemicalToInsert);
                if (chemicalToInsert.isEmpty()) {
                    continue;
                }
            }

            // Attempt to transfer chemical.
            var transferredChemical = tryChemicalTransfer(insertHandler, receiverHandler, chemicalToInsert, true);

            // Deduct the transferred chemical from our maximum transfer.
            maxTransfer -= transferredChemical.getAmount();
            if (maxTransfer <= 0) {
                break;
            }
        }

        return maxTransfer;
    }

    public static ChemicalStack tryChemicalTransfer(IChemicalHandler chemicalDestination,
            IChemicalHandler chemicalSource, int maxAmount, boolean doTransfer) {
        ChemicalStack drainable = chemicalSource.extractChemical(maxAmount, Action.SIMULATE);
        return !drainable.isEmpty()
                ? tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer)
                : ChemicalStack.EMPTY;
    }

    public static ChemicalStack tryChemicalTransfer(IChemicalHandler chemicalDestination,
            IChemicalHandler chemicalSource, ChemicalStack resource, boolean doTransfer) {
        ChemicalStack drainable = chemicalSource.extractChemical(resource, Action.SIMULATE);
        return !drainable.isEmpty() && ChemicalStack.isSameChemical(resource, drainable)
                ? tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer)
                : ChemicalStack.EMPTY;
    }

    private static ChemicalStack tryChemicalTransfer_Internal(IChemicalHandler chemicalDestination,
            IChemicalHandler chemicalSource, ChemicalStack drainable, boolean doTransfer) {
        long fillableAmount = drainable.getAmount()
                - chemicalDestination.insertChemical(drainable, Action.SIMULATE).getAmount();
        if (fillableAmount > 0) {
            drainable.setAmount(fillableAmount);
            if (!doTransfer) {
                return drainable;
            }

            ChemicalStack drained = chemicalSource.extractChemical(drainable, Action.EXECUTE);
            if (!drained.isEmpty()) {
                long remainder = chemicalDestination.insertChemical(drained, Action.EXECUTE).getAmount();
                drained.setAmount(drained.getAmount() - remainder);
                return drained;
            }
        }

        return ChemicalStack.EMPTY;
    }
}
