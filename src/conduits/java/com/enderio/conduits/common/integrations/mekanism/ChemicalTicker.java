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

    private final int rate;

    @SafeVarargs
    public ChemicalTicker(int rate, BlockCapability<? extends IChemicalHandler, Direction>... capabilities) {
        super(capabilities);
        this.rate = rate;
    }

    @Override
    protected void tickCapabilityGraph(IConduitType<?> type, List<CapabilityConnection> insertCaps, List<CapabilityConnection> extractCaps,
        ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {

        for (CapabilityConnection extract : extractCaps) {
            var extractHandler = extract.cap;
            ChemicalExtendedData chemicalExtendedData = extract.data.castTo(ChemicalExtendedData.class);
            ChemicalStack<? extends Chemical<?>> result = null;
            if (chemicalExtendedData.lockedChemical != null) {
                ChemicalStack<? extends Chemical<?>> chemicalStack = extractHandler.extractChemical(chemicalExtendedData.lockedChemical.getChemical().getStack(rate), Action.SIMULATE);
                if (!chemicalStack.isEmpty()) {
                    result = chemicalStack;
                }
            }
            if (result == null) {
                result = extractHandler.extractChemical(rate, Action.SIMULATE);
            }
            var extractedGas = result;

            if (extractedGas.isEmpty()) {
                continue;
            }

            int transferred = 0;
            for (CapabilityConnection insert : insertCaps) {
                var transferredGas = chemicalExtendedData.lockedChemical != null ?
                    tryChemicalTransfer(insert.cap, extractHandler, chemicalExtendedData.lockedChemical.getChemical().getStack(rate - transferred),
                        true) :
                    tryChemicalTransfer(insert.cap, extractHandler, rate - transferred, true);

                if (!transferredGas.isEmpty()) {
                    transferred += transferredGas.getAmount();

                    if (transferred > rate) {
                        break;
                    }
                }
            }
        }
    }

    private static ChemicalStack<? extends Chemical<?>> tryChemicalTransfer(IChemicalHandler chemicalDestination, IChemicalHandler chemicalSource, int maxAmount, boolean doTransfer) {
        if (chemicalSource.getEmptyStack().isStackIdentical(chemicalDestination.getEmptyStack())) {
            var drainable = chemicalSource.extractChemical(maxAmount, Action.SIMULATE);
            if (!drainable.isEmpty()) {
                return tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer);
            }
        }
        return chemicalSource.getEmptyStack();
    }

    private static ChemicalStack<? extends Chemical<?>> tryChemicalTransfer(IChemicalHandler chemicalDestination, IChemicalHandler chemicalSource, ChemicalStack<? extends Chemical<?>> resource, boolean doTransfer) {
        var drainable = chemicalSource.extractChemical(resource, Action.SIMULATE);
        if (!drainable.isEmpty() && resource.isStackIdentical(drainable)) {
            return tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer);
        }
        return chemicalSource.getEmptyStack();
    }

    private static ChemicalStack<? extends Chemical<?>> tryChemicalTransfer_Internal(IChemicalHandler chemicalDestination, IChemicalHandler chemicalSource, ChemicalStack<? extends Chemical<?>> drainable, boolean doTransfer) {
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
        return chemicalSource.getEmptyStack();
    }
}
