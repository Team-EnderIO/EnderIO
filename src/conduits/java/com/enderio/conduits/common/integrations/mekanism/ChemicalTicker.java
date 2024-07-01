//package com.enderio.conduits.common.integrations.mekanism;
//
//import com.enderio.api.conduit.ColoredRedstoneProvider;
//import com.enderio.api.conduit.ConduitNetwork;
//import com.enderio.api.conduit.ConduitNetworkContext;
//import com.enderio.api.conduit.ConduitType;
//import mekanism.api.Action;
//import mekanism.api.chemical.Chemical;
//import mekanism.api.chemical.ChemicalStack;
//import mekanism.api.chemical.ChemicalType;
//import mekanism.api.chemical.IChemicalHandler;
//import net.minecraft.core.Direction;
//import net.minecraft.server.level.ServerLevel;
//import net.neoforged.neoforge.capabilities.BlockCapability;
//
//import java.util.List;
//
//public class ChemicalTicker extends MultiCapabilityAwareConduitTicker<ChemicalConduitOptions, ConduitNetworkContext.Dummy, ChemicalConduitData, IChemicalHandler<?, ?>> {
//
//    @SafeVarargs
//    public ChemicalTicker(BlockCapability<? extends IChemicalHandler<?, ?>, Direction>... capabilities) {
//        super(capabilities);
//    }
//
//    @Override
//    protected void tickCapabilityGraph(
//        ConduitType<ChemicalConduitOptions, ConduitNetworkContext.Dummy, ChemicalConduitData> type,
//        List<CapabilityConnection<ChemicalConduitData, IChemicalHandler<?, ?>>> insertCaps,
//        List<CapabilityConnection<ChemicalConduitData, IChemicalHandler<?, ?>>> extractCaps, ServerLevel level,
//        ConduitNetwork<ConduitNetworkContext.Dummy, ChemicalConduitData> graph,
//        ColoredRedstoneProvider coloredRedstoneProvider) {
//
//        for (var extract : extractCaps) {
//            tickExtractCapability(type.options(), extract.capability(), extract.data(), insertCaps);
//        }
//    }
//
//    private <C extends Chemical<C>, S extends ChemicalStack<C>> void tickExtractCapability(ChemicalConduitOptions options, IChemicalHandler<C, S> extractHandler,
//        ChemicalConduitData chemicalExtendedData, List<CapabilityConnection<ChemicalConduitData, IChemicalHandler<?, ?>>> insertCaps) {
//        ChemicalType extractType = ChemicalType.getTypeFor(extractHandler);
//        S result;
//        if (!chemicalExtendedData.lockedChemical.isEmpty()) {
//            if (chemicalExtendedData.lockedChemical.getChemicalType() != extractType) {
//                return;
//            }
//            result = extractHandler.extractChemical((S) chemicalExtendedData.lockedChemical.getChemical().getStack(options.transferRate()), Action.SIMULATE);
//        } else {
//            result = extractHandler.extractChemical(options.transferRate(), Action.SIMULATE);
//        }
//        if (result.isEmpty()) {
//            return;
//        }
//
//        long transferred = 0;
//        for (var insert : insertCaps) {
//            ChemicalType insertType = ChemicalType.getTypeFor(insert.capability());
//            if (extractType != insertType) {
//                continue;
//            }
//            IChemicalHandler<C, S> destinationHandler = (IChemicalHandler<C, S>) insert.capability();
//            S transferredChemical;
//            if (!chemicalExtendedData.lockedChemical.isEmpty()) {
//                transferredChemical = tryChemicalTransfer(destinationHandler, extractHandler, (S) chemicalExtendedData.lockedChemical.getChemical().getStack(options.transferRate() - transferred), true);
//            } else {
//                transferredChemical = tryChemicalTransfer(destinationHandler, extractHandler, options.transferRate() - transferred, true);
//            }
//
//            transferred += transferredChemical.getAmount();
//            if (transferred >= options.transferRate()) {
//                break;
//            }
//        }
//    }
//
//    private static <C extends Chemical<C>, S extends ChemicalStack<C>> S tryChemicalTransfer(IChemicalHandler<C, S> chemicalDestination, IChemicalHandler<C, S> chemicalSource, long maxAmount, boolean doTransfer) {
//        var drainable = chemicalSource.extractChemical(maxAmount, Action.SIMULATE);
//        if (!drainable.isEmpty()) {
//            return tryChemicalTransfer_Internal(chemicalDestination, chemicalSource, drainable, doTransfer);
//        }
//        return chemicalSource.getEmptyStack();
//    }
//
//    private static <C extends Chemical<C>, S extends ChemicalStack<C>> S tryChemicalTransfer(IChemicalHandler<C, S> chemicalDestination, IChemicalHandler<C, S> chemicalSource, S resource, boolean doTransfer) {
//        var drainable = chemicalSource.extractChemical(resource, Action.SIMULATE);
//        if (!drainable.isEmpty() && resource.equals(drainable)) {
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
