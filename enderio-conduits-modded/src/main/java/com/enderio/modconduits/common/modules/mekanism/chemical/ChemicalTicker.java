package com.enderio.modconduits.common.modules.mekanism.chemical;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.network.ConduitNetwork;
import com.enderio.conduits.api.network.node.ConduitNode;
import com.enderio.conduits.api.ticker.IOAwareConduitTicker;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import com.enderio.modconduits.common.modules.mekanism.chemical_filter.ChemicalFilter;
import java.util.Comparator;
import java.util.List;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ChemicalTicker
        extends IOAwareConduitTicker<ChemicalConduit, ChemicalConduitConnectionConfig, ChemicalTicker.Connection> {

    private long doChemicalTransfer(Chemical chemical, long maxTransfer, Connection receiver,
            List<Connection> senders) {
        // Attempt to drain chemical from the target.
        var extractedChemical = receiver.chemicalHandler.extractChemical(new ChemicalStack(chemical, maxTransfer),
                Action.SIMULATE);
        if (extractedChemical.isEmpty()) {
            return maxTransfer;
        }

        // Test the extracted fluid against the target
        var extractFilter = receiver.inventory()
                .getStackInSlot(ChemicalConduit.EXTRACT_FILTER_SLOT)
                .getCapability(EIOCapabilities.Filter.ITEM);

        if (extractFilter instanceof ChemicalFilter chemicalFilter) {
            if (!chemicalFilter.test(extractedChemical)) {
                return maxTransfer;
            }
        }

        // Insert into any available blocks
        for (Connection insert : senders) {
            var chemicalToInsert = extractedChemical.copy();

            // Test fluid against insert filter.
            var insertFilter = insert.inventory()
                    .getStackInSlot(ChemicalConduit.EXTRACT_FILTER_SLOT)
                    .getCapability(EIOCapabilities.Filter.ITEM);

            if (insertFilter instanceof ChemicalFilter chemicalStackFilter) {
                if (!chemicalStackFilter.test(chemicalToInsert)) {
                    continue;
                }
            }

            // Attempt to transfer chemical.
            var transferredChemical = tryChemicalTransfer(insert.chemicalHandler, receiver.chemicalHandler,
                    chemicalToInsert, true);

            // Deduct the transferred chemical from our maximum transfer.
            maxTransfer -= transferredChemical.getAmount();
            if (maxTransfer <= 0) {
                break;
            }
        }

        return maxTransfer;
    }

    @Override
    public void tickGraph(ServerLevel level, ChemicalConduit conduit, ConduitNetwork graph,
            ColoredRedstoneProvider coloredRedstoneProvider) {
        super.tickGraph(level, conduit, graph, coloredRedstoneProvider);

        // Update if the network is now locked
        var context = graph.getOrCreateContext(ChemicalConduitNetworkContext.TYPE);
        if (!context.lockedChemical().equals(context.lastLockedChemical())) {
            context.clearLastLockedChemical();
            for (var node : graph.getNodes()) {
                if (node.isLoaded()) {
                    node.markDirty();
                }
            }
        }
    }

    @Override
    protected void tickColoredGraph(ServerLevel level, ChemicalConduit conduit, List<Connection> senders,
            List<Connection> receivers, DyeColor color, ConduitNetwork graph,
            ColoredRedstoneProvider coloredRedstoneProvider) {

        final long transferRate = (long) conduit.transferRatePerTick() * conduit.graphTickRate();
        var context = graph.getOrCreateContext(ChemicalConduitNetworkContext.TYPE);

        for (Connection receiver : receivers) {
            var extractHandler = receiver.chemicalHandler;

            // Prioritize senders in order of distance.
            var prioritizedSenders = senders.stream()
                    .sorted(Comparator.comparingDouble(e -> e.pos().distSqr(receiver.pos())))
                    .toList();

            if (!context.lockedChemical().isEmptyType()) {
                doChemicalTransfer(context.lockedChemical(), transferRate, receiver, prioritizedSenders);
            } else {
                long remaining = transferRate;

                for (int i = 0; i < extractHandler.getChemicalTanks() && remaining > 0; i++) {
                    if (extractHandler.getChemicalInTank(i).isEmpty()) {
                        continue;
                    }

                    Chemical chemical = extractHandler.getChemicalInTank(i).getChemical();
                    remaining = doChemicalTransfer(chemical, remaining, receiver, prioritizedSenders);

                    if (!conduit.isMultiChemical() && remaining < transferRate) {
                        context.setLockedChemical(chemical);
                        break;
                    }
                }
            }
        }
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
                drained.setAmount(chemicalDestination.insertChemical(drained, Action.EXECUTE).getAmount());
                return drained;
            }
        }

        return ChemicalStack.EMPTY;
    }

    @Override
    protected @Nullable ChemicalTicker.Connection createConnection(Level level, ConduitNode node, Direction side) {
        var chemicalHandler = node.getNeighbourCapability(MekanismModule.Capabilities.CHEMICAL, side);
        if (chemicalHandler != null) {
            return new Connection(node, side, node.getConnectionConfig(side, ChemicalConduitConnectionConfig.TYPE),
                    chemicalHandler);
        }

        return null;
    }

    protected static class Connection extends SimpleConnection<ChemicalConduitConnectionConfig> {
        final IChemicalHandler chemicalHandler;

        public Connection(ConduitNode node, Direction side, ChemicalConduitConnectionConfig config,
                IChemicalHandler chemicalHandler) {
            super(node, side, config);
            this.chemicalHandler = chemicalHandler;
        }
    }
}
