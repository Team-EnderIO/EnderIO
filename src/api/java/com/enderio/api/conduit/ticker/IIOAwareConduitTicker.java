package com.enderio.api.conduit.ticker;

import com.enderio.api.conduit.IExtendedConduitData;
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
    default void tickGraph(List<NodeIdentifier<?>> loadedNodes, ServerLevel level) {
        ListMultimap<ColorControl, Connection> extracts = ArrayListMultimap.create();
        ListMultimap<ColorControl, Connection> inserts = ArrayListMultimap.create();
        for (GraphObject<Mergeable.Dummy> object : loadedNodes) {
            if (object instanceof NodeIdentifier<?> nodeIdentifier) {
                for (Direction direction: Direction.values()) {
                    nodeIdentifier.getIOState(direction).ifPresent(ioState -> {
                        ioState.extract().ifPresent(color -> extracts.get(color).add(new Connection(nodeIdentifier.getPos(), direction, nodeIdentifier.getExtendedConduitData())));
                        ioState.insert().ifPresent(color -> inserts.get(color).add(new Connection(nodeIdentifier.getPos(), direction, nodeIdentifier.getExtendedConduitData())));
                    });
                }
            }
        }
        for (ColorControl color: ColorControl.values()) {
            List<Connection> extractList = extracts.get(color);
            List<Connection> insertList = inserts.get(color);
            if (extractList.isEmpty() || insertList.isEmpty())
                continue;
            tickColoredGraph(insertList, extractList, level);
        }
    }

    void tickColoredGraph(List<Connection> inserts, List<Connection> extracts, ServerLevel level);

    record Connection(BlockPos pos, Direction dir, IExtendedConduitData<?> data) {
        public BlockPos move() {
            return pos.relative(dir);
        }
    }
}
