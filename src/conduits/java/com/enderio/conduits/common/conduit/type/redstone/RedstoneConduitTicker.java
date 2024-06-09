package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.conduits.common.conduit.NodeIdentifier;
import com.enderio.api.conduit.ticker.IOAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.tag.ConduitTags;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RedstoneConduitTicker implements IOAwareConduitTicker<RedstoneExtendedData> {

    private final List<ColorControl> activeColors = new ArrayList<>();
    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return blockState.is(ConduitTags.Blocks.REDSTONE_CONNECTABLE) || blockState.canRedstoneConnectTo(level, neighbor, direction);
    }

    @Override
    public void tickGraph(
        ConduitType<RedstoneExtendedData> type,
        Graph<Mergeable.Dummy> graph,
        ServerLevel level,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        List<ConduitNode<RedstoneExtendedData>> nodeIdentifiers = new ArrayList<>();
        for (GraphObject<Mergeable.Dummy> object : graph.getObjects()) {
            if (object instanceof NodeIdentifier<?> node) {
                //noinspection unchecked
                nodeIdentifiers.add((NodeIdentifier<RedstoneExtendedData>) node);
            }
        }

        activeColors.clear();
        tickGraph(type,nodeIdentifiers.stream().filter(node -> isLoaded(level, node.getPos())).toList(), level, graph, coloredRedstoneProvider);

        for (ConduitNode<?> nodeIdentifier : nodeIdentifiers) {
            RedstoneExtendedData data = nodeIdentifier.getExtendedConduitData().cast();
            data.clearActive();
            for (ColorControl activeColor : activeColors) {
                data.setActiveColor(activeColor);
            }
        }
    }

    @Override
    public void tickColoredGraph(
        ConduitType<RedstoneExtendedData> type,
        List<Connection<RedstoneExtendedData>> inserts,
        List<Connection<RedstoneExtendedData>> extracts,
        ColorControl color,
        ServerLevel level,
        Graph<Mergeable.Dummy> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        for (Connection<RedstoneExtendedData> extract : extracts) {
            if (level.hasSignal(extract.move(), extract.dir())) {
                activeColors.add(color);
                break;
            }
        }
        for (Connection<RedstoneExtendedData> insert : inserts) {
            level.neighborChanged(insert.move(), ConduitBlocks.CONDUIT.get(), insert.pos());
        }
    }

    @Override
    public int getTickRate() {
        return 2;
    }
}
