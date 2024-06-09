package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.GraphAccessor;
import com.enderio.api.conduit.ticker.IOAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
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
        ServerLevel level,
        ConduitType<RedstoneExtendedData> type,
        GraphAccessor<RedstoneExtendedData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        Collection<ConduitNode<RedstoneExtendedData>> nodeIdentifiers = graph.getNodes();

        activeColors.clear();
        tickGraph(level, type, nodeIdentifiers.stream().filter(node -> isLoaded(level, node.getPos())).toList(), graph, coloredRedstoneProvider);

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
        ServerLevel level,
        ConduitType<RedstoneExtendedData> type,
        List<Connection<RedstoneExtendedData>> inserts,
        List<Connection<RedstoneExtendedData>> extracts,
        ColorControl color,
        GraphAccessor<RedstoneExtendedData> graph,
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
