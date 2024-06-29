package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitGraphContext;

public class EnergyConduitGraphContext implements ConduitGraphContext<EnergyConduitGraphContext> {

    private int energyStored = 0;
    private int rotatingIndex = 0;

    public EnergyConduitGraphContext() {
    }

    public EnergyConduitGraphContext(int energyStored) {
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
    public EnergyConduitGraphContext mergeWith(EnergyConduitGraphContext other) {
        return new EnergyConduitGraphContext(this.energyStored + other.energyStored);
    }

    @Override
    public EnergyConduitGraphContext splitFor(ConduitGraph<EnergyConduitGraphContext, ?> selfGraph, ConduitGraph<EnergyConduitGraphContext, ?> otherGraph) {
        if (selfGraph.getNodes().isEmpty()) {
            return new EnergyConduitGraphContext();
        }

        if (otherGraph.getNodes().isEmpty()) {
            return new EnergyConduitGraphContext(this.energyStored);
        }

        float proportion = (float) selfGraph.getNodes().size() / (selfGraph.getNodes().size() + otherGraph.getNodes().size());
        return new EnergyConduitGraphContext((int) (this.energyStored * proportion));
    }
}
