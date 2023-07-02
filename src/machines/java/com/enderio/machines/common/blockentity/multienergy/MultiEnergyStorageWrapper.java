package com.enderio.machines.common.blockentity.multienergy;

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

public class MultiEnergyStorageWrapper extends MachineEnergyStorage {

    @Nullable
    private Graph<Mergeable.Dummy> graph;

    private final Supplier<ICapacityTier> tier;

    private long addedEnergy = 0;
    private long removedEnergy = 0;
    private long prevAddedEnergy = 0;
    private long prevRemovedEnergy = 0;

    private long lastResetTime = 0;

    public MultiEnergyStorageWrapper(IIOConfig config, EnergyIOMode ioMode, Supplier<ICapacityTier> tier) {
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
            if (object instanceof MultiEnergyNode panelNode) {
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
            if (object instanceof MultiEnergyNode node) {
                cumulativeEnergy += node.getInternal().get().extractEnergy(energy - cumulativeEnergy, false);
            }
        }
        removedEnergy += cumulativeEnergy;
        return cumulativeEnergy;
    }

    @Override
    public int addEnergy(int energy) {
        if (graph == null)
            return 0;

        int cumulativeEnergy = 0;
        List<GraphObject<Mergeable.Dummy>> nodes = new ArrayList<>(graph.getObjects());
        Collections.shuffle(nodes);
        //shuffle to extract randomly to prevent some solar panels from being full and some being empty
        for (GraphObject<Mergeable.Dummy> object : nodes) {
            if (object instanceof MultiEnergyNode node) {
                cumulativeEnergy += node.getInternal().get().receiveEnergy(energy - cumulativeEnergy, false);
            }
        }
        addedEnergy += cumulativeEnergy;
        return cumulativeEnergy;
    }

    public long getAddedEnergy() {
        return addedEnergy;
    }

    public long getRemovedEnergy() {
        return removedEnergy;
    }
    public void resetEnergyStats(long gameTime) {
        if (lastResetTime != gameTime) {
            addedEnergy = 0;
            removedEnergy = 0;
            lastResetTime = gameTime;
        }
    }

    public long getPrevAddedEnergy() {
        return prevAddedEnergy;
    }

    public long getPrevRemovedEnergy() {
        return prevRemovedEnergy;
    }

    public long getLastResetTime() {
        return lastResetTime;
    }
}
