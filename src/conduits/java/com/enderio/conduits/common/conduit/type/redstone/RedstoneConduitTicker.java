package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.IOAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.redstone.RedstoneExtractFilter;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RedstoneConduitTicker implements IOAwareConduitTicker<RedstoneConduitData> {

    private final Map<ColorControl, Integer> activeColors = new EnumMap<>(ColorControl.class);

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return blockState.is(ConduitTags.Blocks.REDSTONE_CONNECTABLE) || blockState.canRedstoneConnectTo(level, neighbor, direction);
    }

    @Override
    public boolean canForceConnect(Level level, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return !blockState.isAir();
    }

    @Override
    public void tickGraph(
        ServerLevel level,
        ConduitType<RedstoneConduitData> type,
        ConduitGraph<RedstoneConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        Collection<ConduitNode<RedstoneConduitData>> nodeIdentifiers = graph.getNodes();

        activeColors.clear();
        tickGraph(level, type, nodeIdentifiers.stream().filter(node -> isLoaded(level, node.getPos())).toList(), graph, coloredRedstoneProvider);

        for (var nodeIdentifier : nodeIdentifiers) {
            RedstoneConduitData data = nodeIdentifier.getConduitData();
            data.clearActive();
            for (var entry : activeColors.entrySet()) {
                data.setActiveColor(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void tickColoredGraph(
        ServerLevel level,
        ConduitType<RedstoneConduitData> type,
        List<Connection<RedstoneConduitData>> inserts,
        List<Connection<RedstoneConduitData>> extracts,
        ColorControl color,
        ConduitGraph<RedstoneConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        for (Connection<RedstoneConduitData> extract : extracts) {
            int signal;
            if (extract.extractFilter() instanceof RedstoneExtractFilter filter) {
                signal = filter.getInputSignal(level, extract.move(), extract.dir());
            } else {
                signal = level.getSignal(extract.move(), extract.dir());
            }

            if (signal > 0) {
                activeColors.put(color, Math.max(activeColors.getOrDefault(color, 0), signal));
            }
        }

        for (Connection<RedstoneConduitData> insert : inserts) {
            level.neighborChanged(insert.move(), ConduitBlocks.CONDUIT.get(), insert.pos());
        }
    }

    @Override
    public boolean shouldSkipColor(List<Connection<RedstoneConduitData>> extractList, List<Connection<RedstoneConduitData>> insertList) {
        return extractList.isEmpty() && insertList.isEmpty(); //Only skip if no one uses the channel
    }

    @Override
    public int getTickRate() {
        return 2;
    }
}
