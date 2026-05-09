package com.enderio.enderio.content.conduits.network.cache;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;

// TODO: eventually I'd like these to be surfaced in the API.
public interface ConduitNetworkCache {
    void update(ConduitNode node, NetworkUpdateType type);
    void rebuild();
}
