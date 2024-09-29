package com.enderio.machines.common.blockentity.multienergy;

import com.enderio.base.api.io.IOConfigurable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.io.energy.ILargeMachineEnergyStorage;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class MultiEnergyStorageWrapper extends MachineEnergyStorage implements ILargeMachineEnergyStorage {

    @Nullable
    private Graph<Mergeable.Dummy> graph;

    private final Supplier<CapacityTier> tier;

    private long addedEnergy = 0;
    private long removedEnergy = 0;

    private long lastResetTime = 0;

    public MultiEnergyStorageWrapper(IOConfigurable config, EnergyIOMode ioMode, Supplier<CapacityTier> tier) {
        super(config, ioMode, () -> tier.get().getStorageCapacity(), () -> tier.get().getStorageCapacity());
        this.tier = tier;
    }

    public void setGraph(Graph<Mergeable.Dummy> graph) {
        this.graph = graph;
    }

    @Override
    public int getEnergyStored() {
        return (int)Math.min(Integer.MAX_VALUE, getLargeEnergyStored());
    }


    @Override
    public long getLargeEnergyStored() {
        if (graph == null) {
            return 0;
        }

        long cumulativeEnergy = 0;
        for (GraphObject<Mergeable.Dummy> object : graph.getObjects()) {
            if (object instanceof MultiEnergyNode panelNode) {
                cumulativeEnergy += panelNode.getInternal().get().getEnergyStored();
            }
        }

        return cumulativeEnergy;
    }
    @Override
    public int getMaxEnergyStored() {
        return (int)(Math.min(getLargeMaxEnergyStored(), Integer.MAX_VALUE));
    }


    @Override
    public long getLargeMaxEnergyStored() {
        if (graph == null) {
            return 0;
        }

        return graph.getObjects().size() * (long)tier.get().getStorageCapacity();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive() || getMaxEnergyStored() == 0) {
            return 0;
        }

        int energyReceived = (int) Math.min(getLargeMaxEnergyStored() - getLargeEnergyStored(), Math.min(getMaxEnergyUse() * 2, maxReceive));
        if (!simulate) {
            addEnergy(energyReceived);
        }

        return energyReceived;
    }

    @Override
    public int takeEnergy(int energy) {
        if (graph == null || energy == 0) {
            return 0;
        }

        int cumulativeEnergy = 0;
        List<GraphObject<Mergeable.Dummy>> nodes = new ArrayList<>(graph.getObjects());
        Collections.shuffle(nodes);
        //shuffle to extractChannel randomly to prevent some solar panels from being full and some being empty
        for (GraphObject<Mergeable.Dummy> object : nodes) {
            if (object instanceof MultiEnergyNode node) {
                cumulativeEnergy += node.getInternal().get().extractEnergy(energy - cumulativeEnergy, false);
                if (energy - cumulativeEnergy <= 0) {
                    break;
                }
            }
        }
        removedEnergy += cumulativeEnergy;
        return cumulativeEnergy;
    }

    @Override
    public int addEnergy(int energy) {
        if (graph == null || energy == 0) {
            return 0;
        }

        int cumulativeEnergy = 0;
        List<GraphObject<Mergeable.Dummy>> nodes = new ArrayList<>(graph.getObjects());
        Collections.shuffle(nodes);
        //shuffle to extractChannel randomly to prevent some solar panels from being full and some being empty
        for (GraphObject<Mergeable.Dummy> object : nodes) {
            if (object instanceof MultiEnergyNode node) {
                cumulativeEnergy += node.getInternal().get().receiveEnergy(energy - cumulativeEnergy, false);
                if (energy - cumulativeEnergy <= 0) {
                    break;
                }
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

    public long getLastResetTime() {
        return lastResetTime;
    }
}
