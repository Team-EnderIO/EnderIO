package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitType;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;

import java.util.List;

public class ChemicalTicker extends MultiCapabilityAwareConduitTicker<ChemicalConduitData, IChemicalHandler<?, ?>> {

    private final int rate;

    @SafeVarargs
    public ChemicalTicker(int rate, Capability<? extends IChemicalHandler<?, ?>>... capabilities) {
        super(capabilities);
        this.rate = rate;
    }

    private int getScaledTransferRate() {
        // Adjust for tick rate. Always flow up so we are at minimum meeting the required rate.
        return (int)Math.ceil(rate * (20.0 / getTickRate()));
    }

    @Override
    protected void tickCapabilityGraph(ConduitType<ChemicalConduitData> type,
        List<CapabilityConnection<ChemicalConduitData, IChemicalHandler<?, ?>>> insertCaps,
        List<CapabilityConnection<ChemicalConduitData, IChemicalHandler<?, ?>>> extractCaps, ServerLevel level, ConduitGraph<ChemicalConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        for (var extract : extractCaps) {
            tickExtractCapability(extract.capability(), extract.data(), insertCaps);
        }
    }

    private <C extends Chemical<C>, S extends ChemicalStack<C>> void tickExtractCapability(IChemicalHandler<C, S> extractHandler,
        ChemicalConduitData chemicalExtendedData, List<CapabilityConnection<ChemicalConduitData, IChemicalHandler<?, ?>>> insertCaps) {

        final int transferRate = getScaledTransferRate();

        ChemicalType extractType = getTypeFor(extractHandler);
        S result;
        if (!chemicalExtendedData.lockedChemical.isEmpty()) {
            if (chemicalExtendedData.lockedChemical.getChemicalType() != extractType) {
                return;
            }
            result = extractHandler.extractChemical((S) chemicalExtendedData.lockedChemical.getChemical().getStack(transferRate), Action.SIMULATE);
        } else {
            result = extractHandler.extractChemical(transferRate, Action.SIMULATE);
        }
        if (result.isEmpty()) {
            return;
        }

        long transferred = 0;
        for (var insert : insertCaps) {
            ChemicalType insertType = getTypeFor(insert.capability());
            if (extractType != insertType) {
                continue;
            }
            IChemicalHandler<C, S> destinationHandler = (IChemicalHandler<C, S>) insert.capability();
            S transferredChemical;
            if (!chemicalExtendedData.lockedChemical.isEmpty()) {
                transferredChemical = tryChemicalTransfer(destinationHandler, extractHandler, (S) chemicalExtendedData.lockedChemical.getChemical().getStack(transferRate - transferred), true);
            } else {
                transferredChemical = tryChemicalTransfer(destinationHandler, extractHandler, transferRate - transferred, true);
            }

            transferred += transferredChemical.getAmount();
            if (transferred >= transferRate) {
                break;
            }
        }
    }

    private ChemicalType getTypeFor(IChemicalHandler<?, ?> handler) {
        if (handler instanceof IGasHandler) {
            return ChemicalType.GAS;
        } else if (handler instanceof IInfusionHandler) {
            return ChemicalType.INFUSION;
        } else if (handler instanceof IPigmentHandler) {
            return ChemicalType.PIGMENT;
        } else if (handler instanceof ISlurryHandler) {
            return ChemicalType.SLURRY;
        }
        throw new IllegalStateException("Unknown chemical handler type");
    }

    private static <C extends Chemical<C>, S extends ChemicalStack<C>> S tryChemicalTransfer(IChemicalHandler<C, S> chemicalDestination, IChemicalHandler<C, S> chemicalSource, long maxAmount, boolean doTransfer) {
            var drainable = chemicalSource.extractChemical(maxAmount, Action.SIMULATE);
            if (!drainable.isEmpty()) {
                return tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer);
            }
        return chemicalSource.getEmptyStack();
    }

    private static <C extends Chemical<C>, S extends ChemicalStack<C>> S tryChemicalTransfer(IChemicalHandler<C, S> chemicalDestination, IChemicalHandler<C, S> chemicalSource, S resource, boolean doTransfer) {
        var drainable = chemicalSource.extractChemical(resource, Action.SIMULATE);
        if (!drainable.isEmpty() && resource.equals(drainable)) {
            return tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer);
        }
        return chemicalSource.getEmptyStack();
    }

    private static <C extends Chemical<C>, S extends ChemicalStack<C>> S tryChemicalTransfer_Internal(IChemicalHandler<C, S> chemicalDestination, IChemicalHandler<C, S> chemicalSource, S drainable, boolean doTransfer) {
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
