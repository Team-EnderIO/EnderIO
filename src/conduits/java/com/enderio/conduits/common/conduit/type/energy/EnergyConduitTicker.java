package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class EnergyConduitTicker extends CapabilityAwareConduitTicker<EnergyConduitOptions, EnergyConduitNetworkContext, ConduitData.EmptyConduitData, IEnergyStorage> {

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

        super.tickGraph(level, type, loadedNodes, graph, coloredRedstoneProvider);
    }

    @Override
    public void tickCapabilityGraph(
        ServerLevel level,
        ConduitType<EnergyConduitOptions, EnergyConduitNetworkContext, ConduitData.EmptyConduitData> type,
        List<CapabilityConnection> inserts,
        List<CapabilityConnection> extracts,
        ConduitNetwork<EnergyConduitNetworkContext, ConduitData.EmptyConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        EnergyConduitNetworkContext context = graph.getContext();
        if (context == null) {
            return;
        }

        // Revert overflow.
        if (inserts.size() <= context.rotatingIndex()) {
            context.setRotatingIndex(0);
        }

        int availableEnergy = context.energyStored();
        int totalEnergyInserted = 0;

        int startingRotatingIndex = context.rotatingIndex();
        for (int i = startingRotatingIndex; i < startingRotatingIndex + inserts.size(); i++) {
            int insertIndex = i % inserts.size();

            CapabilityConnection insert = inserts.get(insertIndex);
            IEnergyStorage insertHandler = insert.capability;

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

    /**
     * This ensures consistent behaviour for FE/t caps and more.
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    @Override
    public int getTickRate() {
        return 1;
    }

    @Override
    public BlockCapability<IEnergyStorage, Direction> getCapability() {
        return Capabilities.EnergyStorage.BLOCK;
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        return super.canConnectTo(level, conduitPos, direction) && !level.getBlockState(conduitPos.relative(direction)).is(ConduitTags.Blocks.ENERGY_CABLE);
    }
}
