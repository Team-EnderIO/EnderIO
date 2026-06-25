package com.enderio.enderio.api.conduits.network;

import com.enderio.enderio.api.conduits.network.node.ConduitNode;

public record NodeAdded(ConduitNode node) implements ConduitNetworkChange {
}
