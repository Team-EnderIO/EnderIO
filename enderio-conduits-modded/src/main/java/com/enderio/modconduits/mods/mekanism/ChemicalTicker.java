package com.enderio.modconduits.mods.mekanism;

import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.ConduitNetwork;
import com.enderio.conduits.api.ConduitNode;
import com.enderio.conduits.api.ticker.CapabilityAwareConduitTicker;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;

import java.util.List;

public class ChemicalTicker extends CapabilityAwareConduitTicker<ChemicalConduit, IChemicalHandler> {

    @Override
    protected void tickCapabilityGraph(ServerLevel level,
        ChemicalConduit conduit,
        List<CapabilityConnection> inserts,
        List<CapabilityConnection> extracts,
        ConduitNetwork graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        for (var extract : extracts) {
            tickExtractCapability(conduit, extract.capability(), extract.node(), inserts);
        }
    }

    private void tickExtractCapability(ChemicalConduit conduit, IChemicalHandler extractHandler,
        ConduitNode node, List<CapabilityConnection> insertCaps) {

        ChemicalConduitData data = node.getOrCreateData(MekanismModule.CHEMICAL_DATA_TYPE.get());

        Chemical extractType = extractHandler.extractChemical(Long.MAX_VALUE, Action.SIMULATE).getChemical();
        ChemicalStack result;
        if (!data.lockedChemical.isEmpty()) {
            if (data.lockedChemical.getChemical() != extractType) {
                return;
            }

            result = extractHandler.extractChemical(data.lockedChemical.getChemical().getStack(conduit.transferRate()), Action.SIMULATE);
        } else {
            result = extractHandler.extractChemical(conduit.transferRate(), Action.SIMULATE);
        }
        if (result.isEmpty()) {
            return;
        }

        long transferred = 0;
        for (var insert : insertCaps) {
            IChemicalHandler destinationHandler = insert.capability();
            ChemicalStack transferredChemical;
            if (!data.lockedChemical.isEmpty()) {
                transferredChemical = tryChemicalTransfer(destinationHandler, extractHandler, data.lockedChemical.getChemical().getStack(conduit.transferRate() - transferred), true);
            } else {
                transferredChemical = tryChemicalTransfer(destinationHandler, extractHandler, conduit.transferRate() - transferred, true);
            }

            transferred += transferredChemical.getAmount();
            if (transferred >= conduit.transferRate()) {
                break;
            }
        }
    }

    private static ChemicalStack tryChemicalTransfer(IChemicalHandler chemicalDestination, IChemicalHandler chemicalSource, long maxAmount, boolean doTransfer) {
        var drainable = chemicalSource.extractChemical(maxAmount, Action.SIMULATE);
        if (!drainable.isEmpty()) {
            return tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer);
        }

        return ChemicalStack.EMPTY;
    }

    private static ChemicalStack tryChemicalTransfer(IChemicalHandler chemicalDestination, IChemicalHandler chemicalSource, ChemicalStack resource, boolean doTransfer) {
        var drainable = chemicalSource.extractChemical(resource, Action.SIMULATE);
        if (!drainable.isEmpty() && resource.equals(drainable)) {
            return tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer);
        }

        return ChemicalStack.EMPTY;
    }

    private static ChemicalStack tryChemicalTransfer_Internal(IChemicalHandler chemicalDestination, IChemicalHandler chemicalSource, ChemicalStack drainable, boolean doTransfer) {
        long fillableAmount = drainable.getAmount() - chemicalDestination.insertChemical(drainable, Action.SIMULATE).getAmount();
        if (fillableAmount > 0) {
            drainable.setAmount(fillableAmount);
            if (doTransfer) {
                var drained = chemicalSource.extractChemical(drainable, Action.EXECUTE);
                if (!drained.isEmpty()) {
                    drained.setAmount(chemicalDestination.insertChemical(drained, Action.EXECUTE).getAmount());
                    return drained;
                }
            } else {
                return drainable;
            }
        }
        return ChemicalStack.EMPTY;
    }

    @Override
    protected BlockCapability<IChemicalHandler, Direction> getCapability() {
        return MekanismModule.Capabilities.CHEMICAL;
    }
}
