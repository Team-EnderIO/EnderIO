package com.enderio.enderio.api.conduits.network;

import com.enderio.enderio.api.conduits.network.node.ConduitNode;

import java.util.Set;

public record NodesUnloaded(Set<? extends ConduitNode> nodes) implements ConduitNetworkChange {}
