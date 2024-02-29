package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.misc.ColorControl;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public class ChemicalTicker extends MultiCapabilityAwareConduitTicker<IChemicalHandler> {

    private final int gasRate;

    @SafeVarargs
    public ChemicalTicker(int gasRate, BlockCapability<? extends IChemicalHandler, Direction>... capabilities) {
        super((BlockCapability<IChemicalHandler, Direction>[]) capabilities);
        this.gasRate = gasRate;
    }

    @Override
    protected void tickCapabilityGraph(IConduitType<?> type, List<CapabilityConnection> insertCaps, List<CapabilityConnection> extractCaps,
        ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {

        for (CapabilityConnection extract : extractCaps) {
            var extractHandler = extract.cap;
            GasExtendedData gasExtendedData = extract.data.castTo(GasExtendedData.class);
            ChemicalStack<? extends Chemical<?>> result = null;
            if (gasExtendedData.lockedGas != null) {
                ChemicalStack<? extends Chemical<?>> chemicalStack = extractHandler.extractChemical(gasExtendedData.lockedGas.getStack(gasRate), Action.SIMULATE);
                if (chemicalStack.isEmpty())
                    result = chemicalStack;
            }
            if (result == null)
                result = extractHandler.extractChemical(gasRate, Action.SIMULATE);
            var extractedGas = result;

            if (extractedGas.isEmpty()) {
                continue;
            }

            int transferred = 0;
            for (CapabilityConnection insert : insertCaps) {
                var transferredGas = gasExtendedData.lockedGas != null ?
                    tryFluidTransfer(insert.cap, extractHandler, gasExtendedData.lockedGas.getStack(gasRate - transferred),
                        true) :
                    tryFluidTransfer(insert.cap, extractHandler, gasRate - transferred, true);

                if (!transferredGas.isEmpty()) {
                    transferred += transferredGas.getAmount();

                    if (transferred > gasRate) {
                        break;
                    }
                }
            }
        }
    }

    private static ChemicalStack<? extends Chemical<?>> tryFluidTransfer(IChemicalHandler fluidDestination, IChemicalHandler fluidSource, int maxAmount, boolean doTransfer) {
        if (fluidSource.getEmptyStack().isStackIdentical(fluidDestination.getEmptyStack())) {
            var drainable = fluidSource.extractChemical(maxAmount, Action.SIMULATE);
            if (!drainable.isEmpty()) {
                return tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer);
            }
        }
        return fluidSource.getEmptyStack();
    }

    private static ChemicalStack<? extends Chemical<?>> tryFluidTransfer(IChemicalHandler fluidDestination, IChemicalHandler fluidSource, ChemicalStack<? extends Chemical<?>> resource, boolean doTransfer) {
        var drainable = fluidSource.extractChemical(resource, Action.SIMULATE);
        if (!drainable.isEmpty() && resource.isStackIdentical(drainable)) {
            return tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer);
        }
        return fluidSource.getEmptyStack();
    }

    private static ChemicalStack<? extends Chemical<?>> tryFluidTransfer_Internal(IChemicalHandler fluidDestination, IChemicalHandler fluidSource, ChemicalStack<? extends Chemical<?>> drainable, boolean doTransfer) {
        long fillableAmount = drainable.getAmount() - fluidDestination.insertChemical(drainable, Action.SIMULATE).getAmount();
        if (fillableAmount > 0) {
            drainable.setAmount(fillableAmount);
            if (doTransfer) {
                var drained = fluidSource.extractChemical(drainable, Action.EXECUTE);
                if (!drained.isEmpty()) {
                    drained.setAmount(fluidDestination.insertChemical(drained, Action.EXECUTE).getAmount());
                    return drained;
                }
            } else {
                return drainable;
            }
        }
        return fluidSource.getEmptyStack();
    }
}
