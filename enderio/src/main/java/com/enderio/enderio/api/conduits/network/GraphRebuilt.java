package com.enderio.enderio.api.conduits.network;

/**
 * If the graph has been rebuilt, this will be the last update passed to queries until they've rebuilt once.
 */
public final class GraphRebuilt implements ConduitNetworkChange {
    public static final GraphRebuilt INSTANCE = new GraphRebuilt();

    private GraphRebuilt() {}
}
