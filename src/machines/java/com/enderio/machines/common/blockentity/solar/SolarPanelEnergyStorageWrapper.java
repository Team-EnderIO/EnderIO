package com.enderio.machines.common.blockentity.solar;

import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SolarPanelEnergyStorageWrapper extends MachineEnergyStorage {

    @Nullable
    private Graph<Mergeable.Dummy> graph;

    private final Supplier<ISolarPanelTier> tier;

    public SolarPanelEnergyStorageWrapper(IIOConfig config, EnergyIOMode ioMode, Supplier<ISolarPanelTier> tier) {
        super(config, ioMode, () -> tier.get().getStorageCapacity(), () -> tier.get().getStorageCapacity());
        this.tier = tier;
    }

    public void setGraph(Graph<Mergeable.Dummy> graph) {
        this.graph = graph;
    }

    @Override
    public int getEnergyStored() {
        if (graph == null)
            return 0;
        int cumulativeEnergy = 0;
        for (GraphObject<Mergeable.Dummy> object : graph.getObjects()) {
            if (object instanceof SolarPanelNode panelNode) {
                cumulativeEnergy += panelNode.getInternal().get().getEnergyStored();
            }
        }
        return cumulativeEnergy;
    }

    @Override
    public int getMaxEnergyStored() {
        if (graph == null)
            return 0;
        return graph.getObjects().size() * tier.get().getStorageCapacity();
    }


    @Override
    public int takeEnergy(int energy) {
        if (graph == null)
            return 0;

        int cumulativeEnergy = 0;
        List<GraphObject<Mergeable.Dummy>> nodes = new ArrayList<>(graph.getObjects());
        Collections.shuffle(nodes);
        //shuffle to extract randomly to prevent some solar panels from being full and some being empty
        for (GraphObject<Mergeable.Dummy> object : nodes) {
            if (object instanceof SolarPanelNode panelNode) {
                cumulativeEnergy += panelNode.getInternal().get().extractEnergy(energy - cumulativeEnergy, false);
            }
        }
        return cumulativeEnergy;
    }

    @Override
    public int addEnergy(int energy) {
        return 0;
    }
}
