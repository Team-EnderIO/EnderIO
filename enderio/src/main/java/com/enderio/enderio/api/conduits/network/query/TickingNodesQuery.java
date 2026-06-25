package com.enderio.enderio.api.conduits.network.query;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.ConduitNetworkChange;
import com.enderio.enderio.api.conduits.network.GraphRebuilt;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.google.common.collect.Sets;

import java.util.Set;

public class TickingNodesQuery implements ConduitNetworkQuery {

    public static final ConduitNetworkQueryType<TickingNodesQuery> TYPE = new  ConduitNetworkQueryType<>(TickingNodesQuery::new, Set.of());

    private final Set<ConduitNode> tickingNodes = Sets.newHashSet();

    @Override
    public ConduitNetworkQueryType<?> type() {
        return TYPE;
    }

    @Override
    public boolean processUpdates(ConduitNetwork network, Set<ConduitNetworkChange> networkChanges) {
        if (networkChanges.contains(GraphRebuilt.INSTANCE)) {
            // TODO: Iterate entire graph and find all ticking nodes.
            return true;
        }

        for (ConduitNetworkChange networkChange : networkChanges) {

        }

        return false;
    }

    private void rebuild(ConduitNetwork network) {
        tickingNodes.clear();

        for (ConduitNode node : network.nodes()) {
            if (node.isTicking()) {
                tickingNodes.add(node);
            }
        }
    }
}
