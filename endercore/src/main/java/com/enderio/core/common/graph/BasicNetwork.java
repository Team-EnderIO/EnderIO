package com.enderio.core.common.graph;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * A basic network implementation with no special context.
 * @param <N> The node type.
 */
public class BasicNetwork<N extends INetworkNode<BasicNetwork<N>, N>> extends Network<BasicNetwork<N>, N> {

    public BasicNetwork(N initialNode) {
        super(initialNode);
    }

    public BasicNetwork(List<N> ns, List<Pair<N, N>> edges, @Nullable Consumer<BasicNetwork<N>> onNetworkCreated) {
        super(ns, edges, onNetworkCreated);
    }

    protected BasicNetwork() {
        super();
    }

    @Override
    protected BasicNetwork<N> createEmpty() {
        return new BasicNetwork<>();
    }
}
