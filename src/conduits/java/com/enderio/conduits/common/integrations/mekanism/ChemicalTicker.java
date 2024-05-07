//package com.enderio.conduits.common.integrations.mekanism;
//
//import com.enderio.api.conduit.IConduitType;
//import com.enderio.api.misc.ColorControl;
//import dev.gigaherz.graph3.Graph;
//import dev.gigaherz.graph3.Mergeable;
//import mekanism.api.Action;
//import mekanism.api.chemical.Chemical;
//import mekanism.api.chemical.ChemicalStack;
//import mekanism.api.chemical.ChemicalType;
//import mekanism.api.chemical.IChemicalHandler;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.server.level.ServerLevel;
//import net.neoforged.neoforge.capabilities.BlockCapability;
//import org.apache.commons.lang3.function.TriFunction;
//
//import java.util.List;
//
//public class ChemicalTicker extends MultiCapabilityAwareConduitTicker<IChemicalHandler<?, ?>> {
//
//    private final int rate;
//
//    @SafeVarargs
//    public ChemicalTicker(int rate, BlockCapability<? extends IChemicalHandler<?, ?>, Direction>... capabilities) {
//        super(capabilities);
//        this.rate = rate;
//    }
//
//    @Override
//    protected void tickCapabilityGraph(IConduitType<?> type, List<CapabilityConnection> insertCaps, List<CapabilityConnection> extractCaps,
//        ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
//
//        for (CapabilityConnection extract : extractCaps) {
//            tickExtractCapability(extract.cap, extract.data.castTo(ChemicalExtendedData.class), insertCaps);
//        }
//    }
//
//    private <C extends Chemical<C>, S extends ChemicalStack<C>> void tickExtractCapability(IChemicalHandler<C, S> extractHandler,
//        ChemicalExtendedData chemicalExtendedData, List<CapabilityConnection> insertCaps) {
//        ChemicalType extractType = ChemicalType.getTypeFor(extractHandler);
//        S result;
//        if (!chemicalExtendedData.lockedChemical.isEmpty()) {
//            if (chemicalExtendedData.lockedChemical.getChemicalType() != extractType) {
//                return;
//            }
//            result = extractHandler.extractChemical((S) chemicalExtendedData.lockedChemical.getChemical().getStack(rate), Action.SIMULATE);
//        } else {
//            result = extractHandler.extractChemical(rate, Action.SIMULATE);
//        }
//        if (result.isEmpty()) {
//            return;
//        }
//
//        long transferred = 0;
//        for (CapabilityConnection insert : insertCaps) {
//            ChemicalType insertType = ChemicalType.getTypeFor(insert.cap);
//            if (extractType != insertType) {
//                continue;
//            }
//            IChemicalHandler<C, S> destinationHandler = (IChemicalHandler<C, S>) insert.cap;
//            S transferredChemical;
//            if (!chemicalExtendedData.lockedChemical.isEmpty()) {
//                transferredChemical = tryChemicalTransfer(destinationHandler, extractHandler, (S) chemicalExtendedData.lockedChemical.getChemical().getStack(rate - transferred), true);
//            } else {
//                transferredChemical = tryChemicalTransfer(destinationHandler, extractHandler, rate - transferred, true);
//            }
//
//            transferred += transferredChemical.getAmount();
//            if (transferred >= rate) {
//                break;
//            }
//        }
//    }
//
//    private static <C extends Chemical<C>, S extends ChemicalStack<C>> S tryChemicalTransfer(IChemicalHandler<C, S> chemicalDestination, IChemicalHandler<C, S> chemicalSource, long maxAmount, boolean doTransfer) {
//            var drainable = chemicalSource.extractChemical(maxAmount, Action.SIMULATE);
//            if (!drainable.isEmpty()) {
//                return tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer);
//            }
//        return chemicalSource.getEmptyStack();
//    }
//
//    private static <C extends Chemical<C>, S extends ChemicalStack<C>> S tryChemicalTransfer(IChemicalHandler<C, S> chemicalDestination, IChemicalHandler<C, S> chemicalSource, S resource, boolean doTransfer) {
//        var drainable = chemicalSource.extractChemical(resource, Action.SIMULATE);
//        if (!drainable.isEmpty() && resource.isStackIdentical(drainable)) {
//            return tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer);
//        }
//        return chemicalSource.getEmptyStack();
//    }
//
//    private static <C extends Chemical<C>, S extends ChemicalStack<C>> S tryChemicalTransfer_Internal(IChemicalHandler<C, S> chemicalDestination, IChemicalHandler<C, S> chemicalSource, S drainable, boolean doTransfer) {
//        long fillableAmount = drainable.getAmount() - chemicalDestination.insertChemical(drainable, Action.SIMULATE).getAmount();
//        if (fillableAmount > 0) {
//            drainable.setAmount(fillableAmount);
//            if (doTransfer) {
//                var drained = chemicalSource.extractChemical(drainable, Action.EXECUTE);
//                if (!drained.isEmpty()) {
//                    drained.setAmount(chemicalDestination.insertChemical(drained, Action.EXECUTE).getAmount());
//                    return drained;
//                }
//            } else {
//                return drainable;
//            }
//        }
//        return chemicalSource.getEmptyStack();
//    }
//}
