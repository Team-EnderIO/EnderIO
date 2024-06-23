package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ticker.IOAwareConduitTicker;
import com.enderio.conduits.common.conduit.block.ConduitBundleBlockEntity;
import com.enderio.conduits.common.init.Conduits;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class EnergyConduitTicker implements IOAwareConduitTicker<EnergyConduit> {

    public EnergyConduitTicker() {
    }

    @Override
    public void tickGraph(ServerLevel level, EnergyConduit conduit,
                          List<ConduitNode> loadedNodes,
                          ConduitNetwork graph, ColoredRedstoneProvider coloredRedstoneProvider) {

        // Reset insertion cap
        EnergyConduitNetworkContext context = graph.getContext(Conduits.ContextSerializers.ENERGY.get());
        if (context != null) {
            context.setEnergyInsertedThisTick(0);
        }

        IOAwareConduitTicker.super.tickGraph(level, conduit, loadedNodes, graph, coloredRedstoneProvider);
    }

    @Override
    public void tickColoredGraph(ServerLevel level, EnergyConduit conduit, List<Connection> inserts, List<Connection> extracts, DyeColor color,
        ConduitNetwork graph, ColoredRedstoneProvider coloredRedstoneProvider) {

        EnergyConduitNetworkContext context = graph.getContext(Conduits.ContextSerializers.ENERGY.get());
        if (context == null) {
            return;
        }

        int availableEnergy = Math.max(context.energyStored(), 0);
        if (availableEnergy == 0) {
            return;
        }

        List<IEnergyStorage> storagesForInsert = new ArrayList<>();
        for (var insert : inserts) {
            IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, insert.move(), insert.direction().getOpposite());
            if (capability != null) {
                storagesForInsert.add(capability);
            }
        }

        // Revert overflow.
        if (storagesForInsert.size() <= context.rotatingIndex()) {
            context.setRotatingIndex(0);
        }

        int totalEnergyInserted = 0;

        int startingRotatingIndex = context.rotatingIndex();
        for (int i = startingRotatingIndex; i < startingRotatingIndex + storagesForInsert.size(); i++) {
            int insertIndex = i % storagesForInsert.size();

            IEnergyStorage insertHandler = storagesForInsert.get(insertIndex);

            if (!insertHandler.canReceive()) {
                continue;
            }

            int energyToInsert = Math.min(conduit.transferRate() - totalEnergyInserted, availableEnergy);

            int energyInserted = insertHandler.receiveEnergy(energyToInsert, false);
            context.setEnergyStored(context.energyStored() - energyInserted);
            context.setRotatingIndex(insertIndex + 1);

            totalEnergyInserted += energyInserted;

            if (totalEnergyInserted >= conduit.transferRate()) {
                break;
            }
        }
    }

    @Override
    public boolean shouldSkipColor(List<Connection> extractList, List<Connection> insertList) {
        return insertList.isEmpty();
    }

    /**
     * This ensures consistent behaviour for FE/t caps and more.
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    @Override
    public int getTickRate() {
        return 1;
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        if (level.getBlockEntity(conduitPos.relative(direction)) instanceof ConduitBundleBlockEntity) {
            return false;
        }

        IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, conduitPos.relative(direction), direction.getOpposite());
        return capability != null;
    }
}
