package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.conduit.ticker.IIOAwareConduitTicker;
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
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;

public class RedstoneConduitTicker implements IIOAwareConduitTicker {

    private final List<ColorControl> activeColors = new ArrayList<>();
    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        BlockPos neighbor = conduitPos.relative(direction);
        BlockState blockState = level.getBlockState(neighbor);
        return blockState.is(ConduitTags.Blocks.REDSTONE_CONNECTABLE) || blockState.canRedstoneConnectTo(level, neighbor, direction);
    }

    @Override
    public void tickGraph(IConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        List<NodeIdentifier<?>> nodeIdentifiers = new ArrayList<>();
        for (GraphObject<Mergeable.Dummy> object : graph.getObjects()) {
            if (object instanceof NodeIdentifier<?> node) {
                nodeIdentifiers.add(node);
            }
        }
        activeColors.clear();
        tickGraph(type,nodeIdentifiers.stream().filter(node -> isLoaded(level, node.getPos())).toList(), level, graph, isRedstoneActive);
        for (NodeIdentifier<?> nodeIdentifier : nodeIdentifiers) {
            RedstoneExtendedData data = nodeIdentifier.getExtendedConduitData().cast();
            data.clearActive();
            for (ColorControl activeColor : activeColors) {
                data.setActiveColor(activeColor);
            }
        }
    }

    @Override
    public void tickColoredGraph(IConduitType<?> type, List<Connection> inserts, List<Connection> extracts, ColorControl color, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        for (Connection extract : extracts) {
            if (level.hasSignal(extract.move(), extract.dir())) {
                activeColors.add(color);
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
