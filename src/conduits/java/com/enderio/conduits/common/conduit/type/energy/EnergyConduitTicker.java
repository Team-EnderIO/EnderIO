package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.conduit.ticker.IOAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.ConduitBundle;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class EnergyConduitTicker implements IOAwareConduitTicker<EnergyConduitOptions, EnergyConduitNetworkContext, ConduitData.EmptyConduitData> {

    public EnergyConduitTicker() {
    }

    @Override
    public void tickGraph(ServerLevel level, ConduitType<EnergyConduitOptions, EnergyConduitNetworkContext, ConduitData.EmptyConduitData> type,
        List<ConduitNode<EnergyConduitNetworkContext, ConduitData.EmptyConduitData>> loadedNodes,
        ConduitNetwork<EnergyConduitNetworkContext, ConduitData.EmptyConduitData> graph, ColoredRedstoneProvider coloredRedstoneProvider) {

        // Reset insertion cap
        EnergyConduitNetworkContext context = graph.getContext();
        if (context != null) {
            context.setEnergyInsertedThisTick(0);
        }

        IOAwareConduitTicker.super.tickGraph(level, type, loadedNodes, graph, coloredRedstoneProvider);
    }

    @Override
    public void tickColoredGraph(ServerLevel level, ConduitType<EnergyConduitOptions, EnergyConduitNetworkContext, ConduitData.EmptyConduitData> type,
        List<Connection<ConduitData.EmptyConduitData>> inserts, List<Connection<ConduitData.EmptyConduitData>> extracts, ColorControl color,
        ConduitNetwork<EnergyConduitNetworkContext, ConduitData.EmptyConduitData> graph, ColoredRedstoneProvider coloredRedstoneProvider) {

        EnergyConduitNetworkContext context = graph.getContext();
        if (context == null) {
            return;
        }

        List<IEnergyStorage> storagesForInsert = new ArrayList<>();
        for (var insert : inserts) {
            IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, insert.move(), insert.dir().getOpposite());
            if (capability != null) {
                storagesForInsert.add(capability);
            }
        }

        // Revert overflow.
        if (storagesForInsert.size() <= context.rotatingIndex()) {
            context.setRotatingIndex(0);
        }

        int availableEnergy = context.energyStored();
        int totalEnergyInserted = 0;

        int startingRotatingIndex = context.rotatingIndex();
        for (int i = startingRotatingIndex; i < startingRotatingIndex + storagesForInsert.size(); i++) {
            int insertIndex = i % storagesForInsert.size();

            IEnergyStorage insertHandler = storagesForInsert.get(insertIndex);

            if (!insertHandler.canReceive()) {
                continue;
            }

            int energyToInsert = Math.min(type.options().transferLimit() - totalEnergyInserted, availableEnergy);

            int energyInserted = insertHandler.receiveEnergy(energyToInsert, false);
            context.setEnergyStored(context.energyStored() - energyInserted);
            context.setRotatingIndex(insertIndex + 1);

            totalEnergyInserted += energyInserted;

            if (totalEnergyInserted >= type.options().transferLimit()) {
                break;
            }
        }
    }

    @Override
    public boolean shouldSkipColor(List<Connection<ConduitData.EmptyConduitData>> extractList, List<Connection<ConduitData.EmptyConduitData>> insertList) {
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
        if (level.getBlockEntity(conduitPos.relative(direction)) instanceof ConduitBlockEntity) {
            return false;
        }

        IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, conduitPos.relative(direction), direction.getOpposite());
        return capability != null;
    }
}
