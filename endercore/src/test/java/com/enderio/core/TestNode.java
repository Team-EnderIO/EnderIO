package com.enderio.core;

import com.enderio.core.common.graph.BasicNetwork;
import com.enderio.core.common.graph.INetworkNode;
import org.jetbrains.annotations.Nullable;

public class TestNode implements INetworkNode<BasicNetwork<TestNode>, TestNode> {

    @Nullable
    private BasicNetwork<TestNode> network;

    @Override
    public @Nullable BasicNetwork<TestNode> getNetwork() {
        return network;
    }

    @Override
    public void setNetwork(@Nullable BasicNetwork<TestNode> network) {
        this.network = network;
    }
}
