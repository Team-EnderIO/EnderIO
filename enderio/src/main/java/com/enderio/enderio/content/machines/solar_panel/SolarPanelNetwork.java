package com.enderio.enderio.content.machines.solar_panel;

import com.enderio.core.common.graph.Network;
import com.mojang.datafixers.util.Pair;

import java.util.List;

public final class SolarPanelNetwork extends Network<SolarPanelNetwork, SolarPanelNode> {

    public SolarPanelNetwork(SolarPanelNode initialNode) {
        super(initialNode);

        initialNode.makePrimaryNode();
    }

    private SolarPanelNetwork(List<SolarPanelNode> solarPanelNodes, List<Pair<SolarPanelNode, SolarPanelNode>> edges) {
        super(solarPanelNodes, edges);
    }

    private SolarPanelNetwork(List<SolarPanelNode> solarPanelNodes, IndexedEdgeList edges) {
        super(solarPanelNodes, edges);
    }

    private SolarPanelNetwork() {
        super();
    }

    @Override
    protected SolarPanelNetwork createEmpty() {
        return new SolarPanelNetwork();
    }

    public int getTotalEnergyStored() {
        return nodes().stream().mapToInt(SolarPanelNode::getEnergyStored).sum();
    }

    public int getTotalMaxEnergyStored() {
        return nodes().stream().mapToInt(SolarPanelNode::getMaxEnergyStored).sum();
    }

    @Override
    protected void onNodeRemoved(SolarPanelNode node) {
        super.onNodeRemoved(node);

        // If we removed the 'primary', find a new one.
        if (node.isPrimaryNode()) {
            nodes().stream().findFirst().ifPresent(SolarPanelNode::makePrimaryNode);
        }
    }
}
