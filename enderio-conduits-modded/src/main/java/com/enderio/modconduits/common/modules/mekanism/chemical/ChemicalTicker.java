package com.enderio.modconduits.common.modules.mekanism.chemical;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.network.ConduitNetwork;
import com.enderio.conduits.api.network.node.ConduitNode;
import com.enderio.conduits.api.ticker.IOAwareConduitTicker;
import com.enderio.modconduits.common.modules.mekanism.chemical_filter.ChemicalFilter;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class ChemicalTicker extends IOAwareConduitTicker<ChemicalConduit, ChemicalConduitConnectionConfig, ChemicalTicker.Connection> {

    private long doChemicalTransfer(ChemicalStack chemicalStack, Connection receiver, List<Connection> senders) {
        var extractedFluid = receiver.chemicalHandler.extractChemical(chemicalStack, Action.SIMULATE);

        if (extractedFluid.isEmpty()) {
            return chemicalStack.getAmount();
        }

        var extractFilter = receiver.inventory()
            .getStackInSlot(ChemicalConduit.EXTRACT_FILTER_SLOT)
            .getCapability(EIOCapabilities.Filter.ITEM);

        if (extractFilter instanceof ChemicalFilter chemicalFilter) {
            if (!chemicalFilter.test(extractedFluid)) {
                return chemicalStack.getAmount();
            }
        }

        for (Connection insert : senders) {
            var insertFilter = insert.inventory()
                .getStackInSlot(ChemicalConduit.EXTRACT_FILTER_SLOT)
                .getCapability(EIOCapabilities.Filter.ITEM);

            if (insertFilter instanceof ChemicalFilter fluidStackFilter) {
                if (!fluidStackFilter.test(extractedFluid)) {
                    continue;
                }
            }

            chemicalStack = tryFluidTransfer(insert.chemicalHandler, receiver.chemicalHandler, chemicalStack.copy(), true);

            if (chemicalStack.getAmount() <= 0) {
                break;
            }
        }

        return chemicalStack.getAmount();
    }

    @Override
    public void tickGraph(ServerLevel level, ChemicalConduit conduit, ConduitNetwork graph, ColoredRedstoneProvider coloredRedstoneProvider) {
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
    protected void tickColoredGraph(ServerLevel level, ChemicalConduit conduit, List<Connection> senders, List<Connection> receivers, DyeColor color,
        ConduitNetwork graph, ColoredRedstoneProvider coloredRedstoneProvider) {

        final long fluidRate = (long) conduit.transferRatePerTick() * conduit.graphTickRate();
        var context = graph.getOrCreateContext(ChemicalConduitNetworkContext.TYPE);

        for (Connection receiver : receivers) {
            var extractHandler = receiver.chemicalHandler;

            // Prioritize senders in order of distance.
            var prioritizedSenders = senders.stream()
                .sorted(Comparator.comparingDouble(e -> e.pos().distSqr(receiver.pos())))
                .toList();

            if (!context.lockedChemical().isEmptyType()) {
                doChemicalTransfer(new ChemicalStack(context.lockedChemical(), fluidRate), receiver, prioritizedSenders);
            } else {
                long remaining = fluidRate;

                for (int i = 0; i < extractHandler.getChemicalTanks() && remaining > 0; i++) {
                    if (extractHandler.getChemicalInTank(i).isEmpty()) {
                        continue;
                    }

                    Chemical chemical = extractHandler.getChemicalInTank(i).getChemical();
                    remaining = doChemicalTransfer(new ChemicalStack(chemical, remaining), receiver, prioritizedSenders);

                    if (!conduit.isMultiChemical() && remaining < fluidRate) {
                        context.setLockedChemical(chemical);
                        break;
                    }
                }
            }
        }
    }

    public static ChemicalStack tryFluidTransfer(IChemicalHandler fluidDestination, IChemicalHandler fluidSource, int maxAmount, boolean doTransfer) {
        ChemicalStack drainable = fluidSource.extractChemical(maxAmount, Action.SIMULATE);
        return !drainable.isEmpty() ? tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer) : ChemicalStack.EMPTY;
    }

    public static ChemicalStack tryFluidTransfer(IChemicalHandler fluidDestination, IChemicalHandler fluidSource, ChemicalStack resource, boolean doTransfer) {
        ChemicalStack drainable = fluidSource.extractChemical(resource, Action.SIMULATE);
        return !drainable.isEmpty() && ChemicalStack.isSameChemical(resource, drainable) ? tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer) : ChemicalStack.EMPTY;
    }

    private static ChemicalStack tryFluidTransfer_Internal(IChemicalHandler fluidDestination, IChemicalHandler fluidSource, ChemicalStack drainable, boolean doTransfer) {
        long fillableAmount = drainable.getAmount() - fluidDestination.insertChemical(drainable, Action.SIMULATE).getAmount();
        if (fillableAmount > 0) {
            drainable.setAmount(fillableAmount);
            if (!doTransfer) {
                return drainable;
            }

            ChemicalStack drained = fluidSource.extractChemical(drainable, Action.EXECUTE);
            if (!drained.isEmpty()) {
                drained.setAmount(fluidDestination.insertChemical(drained, Action.EXECUTE).getAmount());
                return drained;
            }
        }

        return ChemicalStack.EMPTY;
    }

    @Override
    protected @Nullable ChemicalTicker.Connection createConnection(Level level, ConduitNode node, Direction side) {
        var chemicalHandler = node.getNeighbourCapability(MekanismModule.Capabilities.CHEMICAL, side);
        if (chemicalHandler != null) {
            return new Connection(node, side, node.getConnectionConfig(side, ChemicalConduitConnectionConfig.TYPE), chemicalHandler);
        }

        return null;
    }

    protected static class Connection extends SimpleConnection<ChemicalConduitConnectionConfig> {
        private final IChemicalHandler chemicalHandler;

        public Connection(ConduitNode node, Direction side, ChemicalConduitConnectionConfig config,
            IChemicalHandler chemicalHandler) {
            super(node, side, config);
            this.chemicalHandler = chemicalHandler;
        }

        public IChemicalHandler chemicalHandler() {
            return chemicalHandler;
        }
    }
}
