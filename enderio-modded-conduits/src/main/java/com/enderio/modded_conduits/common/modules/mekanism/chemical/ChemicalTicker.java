package com.enderio.modded_conduits.common.modules.mekanism.chemical;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.connection.path.ConduitConnectionPath;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.ticker.ConduitTickerBase;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import com.google.common.collect.Maps;
import mekanism.api.chemical.ChemicalResource;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChemicalTicker extends ConduitTickerBase<ChemicalConduit> {

    public static final ChemicalTicker INSTANCE = new ChemicalTicker();

    private ChemicalTicker() {
        super(MekanismModule.TYPE_CHEMICAL::get);
    }

    @Override
    protected void tickNetwork(ServerLevel level, ConduitNetwork network, List<Holder<Conduit<?, ?>>> tickableConduits) {
        Map<ConduitConnectionPath, Integer> insertedPerPath = Maps.newHashMap();

        for (var channel : network.allChannels()) {
            for (var extractConnection : network.extractConnections(channel)) {
                var insertPaths = network.insertConnectionsFrom(extractConnection);
                if (insertPaths.isEmpty()) {
                    continue;
                }

                ResourceHandler<ChemicalResource> extractHandler = extractConnection.getSidedCapability(MekanismModule.Capabilities.CHEMICAL);
                if (extractHandler == null) {
                    continue;
                }

                final var extractConduit = extractConnection.node().conduit(conduitType());
                final int chemicalRate = extractConduit.value().transferRatePerTick() * conduitType().getTickRate(extractConduit);

                int remaining = chemicalRate;

                for (int i = 0; i < extractHandler.size() && remaining > 0; i++) {
                    if (extractHandler.getResource(i).isEmpty()) {
                        continue;
                    }

                    var chemicalResource = extractHandler.getResource(i);
                    remaining = doChemicalTransfer(chemicalResource, remaining, extractConnection, insertPaths, insertedPerPath);
                }
            }
        }
    }

    private int doChemicalTransfer(ChemicalResource chemicalResource, int maxTransfer, ConduitBlockConnection extractConnection,
        List<ConduitConnectionPath> insertPaths, Map<ConduitConnectionPath, Integer> insertedPerPath) {

        var extractHandler = Objects
            .requireNonNull(extractConnection.getSidedCapability(MekanismModule.Capabilities.CHEMICAL));

        try (Transaction transaction = Transaction.openRoot()) {
            int maxExtract;
            try (Transaction nestedTransaction = Transaction.open(transaction)) {
                maxExtract = extractHandler.extract(chemicalResource, maxTransfer, nestedTransaction);
            }

            if (maxExtract <= 0) {
                return maxTransfer;
            }

            // Test the extracted chemical against the target
            var extractFilter = extractConnection.inventory()
                .getResource(ChemicalConduit.EXTRACT_FILTER_SLOT)
                .toStack()
                .getCapability(MekanismModule.Capabilities.CHEMICAL_FILTER);

            if (extractFilter != null) {
                var filteredStack = extractFilter.test(extractHandler, chemicalResource.toStack(maxExtract));
                if (filteredStack.isEmpty()) {
                    return maxTransfer;
                }

                maxExtract = Math.min(maxExtract, filteredStack.amount());
            }

            // Insert into any available blocks
            for (var insertPath : insertPaths) {
                var insertConnection = insertPath.end();
                final int maxInsertSpeed = insertPath.property(ChemicalConduit.PATH_MAX_TRANSFER_RATE);

                // Calculate remaining 'speed' for this path
                int remaining = maxInsertSpeed - insertedPerPath.getOrDefault(insertPath, 0);
                if (remaining <= 0) {
                    continue;
                }

                var insertHandler = insertConnection.getSidedCapability(MekanismModule.Capabilities.CHEMICAL);
                if (insertHandler == null) {
                    continue;
                }

                int amountToInsert = Math.min(maxExtract, maxTransfer);
                if (amountToInsert > remaining) {
                    amountToInsert = remaining;
                }

                // Test chemical against insert filter.
                var insertFilter = insertConnection.inventory()
                    .getResource(ChemicalConduit.INSERT_FILTER_SLOT)
                    .toStack()
                    .getCapability(MekanismModule.Capabilities.CHEMICAL_FILTER);

                if (insertFilter != null) {
                    var filteredStack = insertFilter.test(insertHandler, chemicalResource.toStack(amountToInsert));
                    if (filteredStack.isEmpty()) {
                        continue;
                    }

                    amountToInsert = Math.min(amountToInsert, filteredStack.amount());
                }

                // Attempt to transfer chemical.
                int transferred = ResourceHandlerUtil.move(extractHandler, insertHandler, resource -> resource.equals(chemicalResource),
                    amountToInsert, transaction);

                // Deduct the transferred chemical from our maximum transfer.
                maxTransfer -= transferred;
                maxExtract -= transferred;

                // Store the amount transferred in this path
                insertedPerPath.compute(insertPath, (k, v) -> v == null ? transferred : v + transferred);

                if (maxTransfer <= 0 || maxExtract <= 0) {
                    break;
                }
            }

            transaction.commit();
            return maxTransfer;
        }
    }
}
