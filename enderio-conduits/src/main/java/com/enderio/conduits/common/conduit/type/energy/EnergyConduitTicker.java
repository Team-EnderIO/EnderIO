package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.network.ConduitNetwork;
import com.enderio.conduits.api.network.node.ConduitNode;
import com.enderio.conduits.api.ticker.IOAwareConduitTicker;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class EnergyConduitTicker
        extends IOAwareConduitTicker<EnergyConduit, EnergyConduitConnectionConfig, EnergyConduitTicker.Connection> {

    public static final EnergyConduitTicker INSTANCE = new EnergyConduitTicker();

    public EnergyConduitTicker() {
    }

    @Override
    protected void tickColoredGraph(ServerLevel level, EnergyConduit conduit, List<Connection> senders,
            List<Connection> receivers, DyeColor color, ConduitNetwork graph,
            ColoredRedstoneProvider coloredRedstoneProvider) {

        int transferRate = conduit.transferRatePerTick() * conduit.graphTickRate();

        EnergyConduitNetworkContext context = graph.getContext(EnergyConduitNetworkContext.TYPE);
        if (context == null) {
            return;
        }

        if (context.energyStored() <= 0) {
            return;
        }

        // Revert overflow.
        if (senders.size() <= context.rotatingIndex()) {
            context.setRotatingIndex(0);
        }

        int startingRotatingIndex = context.rotatingIndex();
        for (int i = startingRotatingIndex; i < startingRotatingIndex + senders.size(); i++) {
            int insertIndex = i % senders.size();

            IEnergyStorage insertHandler = senders.get(insertIndex).energyStorage();

            if (!insertHandler.canReceive()) {
                continue;
            }

            int energyToInsert = Math.min(transferRate, Math.max(context.energyStored(), 0));
            int energyInserted = insertHandler.receiveEnergy(energyToInsert, false);
            context.setEnergyStored(context.energyStored() - energyInserted);
            context.setRotatingIndex(insertIndex + 1);
            if (context.energyStored() <= 0) {
                // If we are out of energy then stop the loop so we start at the next
                // index next time around to spread out any new energy
                break;
            }
        }
    }

    @Override
    protected boolean canReceive(ConduitNode node, EnergyConduitConnectionConfig config) {
        // We don't require a receive component.
        return false;
    }

    @Override
    protected boolean shouldSkipColor(List<Connection> senders, List<Connection> receivers) {
        return senders.isEmpty();
    }

    @Override
    protected @Nullable EnergyConduitTicker.Connection createConnection(Level level, ConduitNode node, Direction side) {
        var energyStorage = node.getNeighbourCapability(Capabilities.EnergyStorage.BLOCK, side);
        if (energyStorage != null) {
            return new Connection(node, side, node.getConnectionConfig(side, EnergyConduitConnectionConfig.TYPE),
                    energyStorage);
        }

        return null;
    }

    protected static class Connection extends SimpleConnection<EnergyConduitConnectionConfig> {
        private final IEnergyStorage energyStorage;

        public Connection(ConduitNode node, Direction side, EnergyConduitConnectionConfig config,
                IEnergyStorage energyStorage) {
            super(node, side, config);
            this.energyStorage = energyStorage;
        }

        public IEnergyStorage energyStorage() {
            return energyStorage;
        }
    }
}
