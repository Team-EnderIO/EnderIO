package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitNetwork;
import com.enderio.api.conduit.ConduitNetworkContext;

public class EnergyConduitNetworkContext implements ConduitNetworkContext<EnergyConduitNetworkContext> {

    private int energyStored = 0;
    private int rotatingIndex = 0;

    public EnergyConduitNetworkContext() {
    }

    public EnergyConduitNetworkContext(int energyStored) {
        this.energyStored = energyStored;
    }

    public int energyStored() {
        return energyStored;
    }

    public void setEnergyStored(int energyStored) {
        this.energyStored = energyStored;
    }

    public int rotatingIndex() {
        return rotatingIndex;
    }

    public void setRotatingIndex(int rotatingIndex) {
        this.rotatingIndex = rotatingIndex;
    }

    @Override
    public EnergyConduitNetworkContext mergeWith(EnergyConduitNetworkContext other) {
        return new EnergyConduitNetworkContext(this.energyStored + other.energyStored);
    }

    @Override
    public EnergyConduitNetworkContext splitFor(ConduitNetwork<EnergyConduitNetworkContext, ?> selfGraph, ConduitNetwork<EnergyConduitNetworkContext, ?> otherGraph) {
        if (selfGraph.getNodes().isEmpty()) {
            return new EnergyConduitNetworkContext();
        }

        if (otherGraph.getNodes().isEmpty()) {
            return new EnergyConduitNetworkContext(this.energyStored);
        }

        float proportion = (float) selfGraph.getNodes().size() / (selfGraph.getNodes().size() + otherGraph.getNodes().size());
        return new EnergyConduitNetworkContext((int) (this.energyStored * proportion));
    }
}
