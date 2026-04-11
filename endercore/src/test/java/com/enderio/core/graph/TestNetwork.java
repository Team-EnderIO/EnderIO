package com.enderio.core.graph;

import com.enderio.core.common.graph.BasicNetwork;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestNetwork extends BasicNetwork<TestNode> {

    @Nullable
    public TestNode lastNodeAdded = null;

    public TestNetwork(TestNode initialNode) {
        super(initialNode);
    }

    public TestNetwork(List<TestNode> testNodes, List<Pair<TestNode, TestNode>> edges) {
        super(testNodes, edges);
    }

    public TestNetwork(List<TestNode> testNodes, IndexedEdgeList edges) {
        super(testNodes, edges);
    }

    protected TestNetwork() {
        super();
    }

    @Override
    protected BasicNetwork<TestNode> createEmpty() {
        return new TestNetwork();
    }

    @Override
    protected void onNodeAdded(TestNode node) {
        super.onNodeAdded(node);
        lastNodeAdded = node;
    }
}
