package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.misc.ColorControl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public interface IIOAwareConduitTicker extends ILoadedAwareConduitTicker {
    @Override
    default void tickGraph(List<NodeIdentifier> loadedNodes, ServerLevel level) {
        ListMultimap<ColorControl, ConnectorPos> extracts = ArrayListMultimap.create();
        ListMultimap<ColorControl, ConnectorPos> inserts = ArrayListMultimap.create();
        for (GraphObject<Mergeable.Dummy> object : loadedNodes) {
            if (object instanceof NodeIdentifier nodeIdentifier) {
                for (Direction direction: Direction.values()) {
                    nodeIdentifier.getIOState(direction).ifPresent(ioState -> {
                        ioState.extract().ifPresent(color -> extracts.get(color).add(new ConnectorPos(nodeIdentifier.getPos(), direction)));
                        ioState.insert().ifPresent(color -> inserts.get(color).add(new ConnectorPos(nodeIdentifier.getPos(), direction)));
                    });
                }
            }
        }
        for (ColorControl color: ColorControl.values()) {
            List<ConnectorPos> extractList = extracts.get(color);
            List<ConnectorPos> insertList = inserts.get(color);
            if (extractList.isEmpty() || insertList.isEmpty())
                continue;
            tickColoredGraph(insertList, extractList, level);
        }
    }

    void tickColoredGraph(List<ConnectorPos> inserts, List<ConnectorPos> extracts, ServerLevel level);

    record ConnectorPos(BlockPos pos, Direction dir) {
        public BlockPos move() {
            return pos.relative(dir);
        }
    }
}
