package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.conduit.ticker.IIOAwareConduitTicker;
import com.enderio.conduits.common.ConduitTags;
import com.enderio.conduits.common.init.ConduitBlocks;
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

public class RedstoneConduitTicker implements IIOAwareConduitTicker {

    private boolean isActive = false;
    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return blockState.is(ConduitTags.REDSTONE_CONNECTABLE) || blockState.canRedstoneConnectTo(level, neighbor, direction);
    }

    @Override
    public void tickGraph(IConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level) {
        List<NodeIdentifier<?>> nodeIdentifiers = new ArrayList<>();
        for (GraphObject<Mergeable.Dummy> object : graph.getObjects()) {
            if (object instanceof NodeIdentifier<?> node) {
                nodeIdentifiers.add(node);
            }
        }
        isActive = false;
        tickGraph(type,nodeIdentifiers.stream().filter(node -> isLoaded(level, node.getPos())).toList(), level, graph);
        for (NodeIdentifier<?> nodeIdentifier : nodeIdentifiers) {
            RedstoneExtendedData data = nodeIdentifier.getExtendedConduitData().cast();
            data.setActive(isActive);
        }
    }

    @Override
    public void tickColoredGraph(IConduitType<?> type, List<Connection> inserts, List<Connection> extracts, ServerLevel level, Graph<Mergeable.Dummy> graph) {
        for (Connection extract : extracts) {
            if (level.hasSignal(extract.move(), extract.dir())) {
                isActive = true;
                break;
            }
        }
        for (Connection insert : inserts) {
            level.neighborChanged(insert.move(), ConduitBlocks.CONDUIT.get(), insert.pos());
        }
    }

    @Override
    public int getTickRate() {
        return 2;
    }
}
