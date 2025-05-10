package com.enderio.core;

import com.enderio.core.common.graph.BasicNetwork;
import com.enderio.core.common.graph.INetworkNode;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class TestNode implements INetworkNode<BasicNetwork<TestNode>, TestNode> {

    @Nullable
    private BasicNetwork<TestNode> network;

    public TestNode() {
        network = new BasicNetwork<>(this);
    }

    public TestNode(boolean shouldCreateNetwork) {
        if (shouldCreateNetwork) {
            network = new BasicNetwork<>(this);
        }
    }

    @Override
    public boolean isValid() {
        return network != null;
    }

    @Override
    public BasicNetwork<TestNode> getNetwork() {
        return Objects.requireNonNull(network, "Node is not valid!");
    }

    @Override
    public void setNetwork(@Nullable BasicNetwork<TestNode> network) {
        this.network = network;
    }
}
