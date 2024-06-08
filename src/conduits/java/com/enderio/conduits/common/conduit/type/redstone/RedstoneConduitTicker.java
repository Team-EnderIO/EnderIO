package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.IOAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.NodeIdentifier;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.redstone.RedstoneExtractFilter;
import com.enderio.conduits.common.tag.ConduitTags;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RedstoneConduitTicker implements IOAwareConduitTicker {

    private final Map<ColorControl, Integer> activeColors = new EnumMap<>(ColorControl.class);
    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return blockState.is(ConduitTags.Blocks.REDSTONE_CONNECTABLE) || blockState.canRedstoneConnectTo(level, neighbor, direction);
    }

    @Override
    public void tickGraph(ConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        List<ConduitNode<?>> nodeIdentifiers = new ArrayList<>();
        for (GraphObject<Mergeable.Dummy> object : graph.getObjects()) {
            if (object instanceof NodeIdentifier<?> node) {
                nodeIdentifiers.add(node);
            }
        }
        activeColors.clear();
        tickGraph(type,nodeIdentifiers.stream().filter(node -> isLoaded(level, node.getPos())).toList(), level, graph, isRedstoneActive);
        for (ConduitNode<?> nodeIdentifier : nodeIdentifiers) {
            RedstoneExtendedData data = nodeIdentifier.getExtendedConduitData().cast();
            data.clearActive();
            for (var entry : activeColors.entrySet()) {
                data.setActiveColor(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void tickColoredGraph(ConduitType<?> type, List<Connection> inserts, List<Connection> extracts, ColorControl color, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        for (Connection extract : extracts) {
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
        for (Connection insert : inserts) {
            level.neighborChanged(insert.move(), ConduitBlocks.CONDUIT.get(), insert.pos());
        }
    }

    @Override
    public boolean shouldSkipColor(List<Connection> extractList, List<Connection> insertList) {
        return false;
    }

    @Override
    public int getTickRate() {
        return 2;
    }
}
