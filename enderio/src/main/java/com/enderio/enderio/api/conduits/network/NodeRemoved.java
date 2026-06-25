package com.enderio.enderio.api.conduits.network;

import com.enderio.enderio.api.conduits.network.node.ConduitNode;

public record NodeRemoved(ConduitNode node) implements ConduitNetworkChange {}
