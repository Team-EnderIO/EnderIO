package com.enderio.enderio.api.conduits.network;

public sealed interface ConduitNetworkChange permits NodesLoaded, NodesUnloaded, NodeUpdated, NodeAdded, NodeRemoved {}
