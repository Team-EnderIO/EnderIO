package com.enderio.core.common.graph;

/**
 * A basic network implementation with no special context.
 * @param <N> The node type.
 */
public class BasicNetwork<N extends INetworkNode<BasicNetwork<N>, N>> extends Network<BasicNetwork<N>, N> {

    public BasicNetwork(N initialNode) {
        super(initialNode);
    }

    protected BasicNetwork() {
        super();
    }

    @Override
    protected BasicNetwork<N> createEmpty() {
        return new BasicNetwork<>();
    }
}
