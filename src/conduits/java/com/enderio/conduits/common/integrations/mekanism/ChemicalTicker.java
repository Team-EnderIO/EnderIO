package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.filter.ResourceFilter;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.slurry.ISlurryHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            tickExtractCapability(extract.capability(), extract.data(), extract.extractFilter(), insertCaps);
        }
    }

    private <C extends Chemical<C>, S extends ChemicalStack<C>> void tickExtractCapability(IChemicalHandler<C, S> extractHandler,
        ChemicalConduitData chemicalExtendedData, ResourceFilter extractFilter, List<CapabilityConnection<ChemicalConduitData, IChemicalHandler<?, ?>>> insertCaps) {

        final int transferRate = getScaledTransferRate();

        ChemicalType extractType = getTypeFor(extractHandler);

        if (!chemicalExtendedData.lockedChemical.isEmpty()) {
            if (chemicalExtendedData.lockedChemical.getChemicalType() != extractType) {
                return;
            }
            S result = extractHandler.extractChemical((S) chemicalExtendedData.lockedChemical.getChemical().getStack(transferRate), Action.SIMULATE);
            if (result.isEmpty()) {
                return;
            }
            if (extractFilter instanceof ChemicalStackFilter chemicalFilter) {
                if (!chemicalFilter.test(BoxedChemicalStack.box(result))) {
                    return;
                }
            }
            transferResult(extractHandler, extractType, insertCaps, result, transferRate);
            return;
        }

        Set<C> checkedTypes = new HashSet<>();
        long transferred = 0;
        for (int i = 0; i < extractHandler.getTanks() && transferred < transferRate; i++) {
            S inTank = extractHandler.getChemicalInTank(i);
            if (inTank.isEmpty()) {
                continue;
            }
            C inTankType = inTank.getType();
            if (!checkedTypes.add(inTankType)) {
                continue;
            }

            S result = extractHandler.extractChemical((S) inTankType.getStack(transferRate - transferred), Action.SIMULATE);
            if (result.isEmpty()) {
                continue;
            }

            if (extractFilter instanceof ChemicalStackFilter chemicalFilter) {
                if (!chemicalFilter.test(BoxedChemicalStack.box(result))) {
                    continue;
                }
            }

            transferred += transferResult(extractHandler, extractType, insertCaps, result, transferRate - transferred);
        }
    }

    private <C extends Chemical<C>, S extends ChemicalStack<C>> long transferResult(IChemicalHandler<C, S> extractHandler, ChemicalType extractType,
        List<CapabilityConnection<ChemicalConduitData, IChemicalHandler<?, ?>>> insertCaps, S result, long maxTransfer) {

        long transferred = 0;
        for (var insert : insertCaps) {
            if (transferred >= maxTransfer) {
                break;
            }
            ChemicalType insertType = getTypeFor(insert.capability());
            if (extractType != insertType) {
                continue;
            }
            if (insert.insertFilter() instanceof ChemicalStackFilter chemicalFilter) {
                if (!chemicalFilter.test(BoxedChemicalStack.box(result))) {
                    continue;
                }
            }
            IChemicalHandler<C, S> destinationHandler = (IChemicalHandler<C, S>) insert.capability();
            S toTransfer = extractHandler.extractChemical((S) result.getType().getStack(maxTransfer - transferred), Action.SIMULATE);
            if (toTransfer.isEmpty()) {
                break;
            }
            S transferredChemical = tryChemicalTransfer(destinationHandler, extractHandler, toTransfer, true);
            transferred += transferredChemical.getAmount();
        }
        return transferred;
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
                    long remainder = chemicalDestination.insertChemical(drained, Action.EXECUTE).getAmount();
                    drained.setAmount(drained.getAmount() - remainder);
                    return drained;
                }
            } else {
                return drainable;
            }
        }
        return chemicalSource.getEmptyStack();
    }
}
