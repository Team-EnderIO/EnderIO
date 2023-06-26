package com.enderio.machines.common.blockentity.solar;

import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SolarPanelNode implements GraphObject<Mergeable.Dummy> {

    @Nullable
    private Graph<Mergeable.Dummy> graph = null;


    private final Supplier<IEnergyStorage> internal;
    private final Supplier<SolarPanelEnergyStorageWrapper> wrapper;

    public SolarPanelNode(Supplier<IEnergyStorage> internal, Supplier<SolarPanelEnergyStorageWrapper> wrapper) {
        this.internal = internal;
        this.wrapper = wrapper;
    }
    @Override
    @Nullable
    public Graph<Mergeable.Dummy> getGraph() {
        return graph;
    }

    @Override
    public void setGraph(Graph<Mergeable.Dummy> g) {
        this.graph = g;
        getWrapper().get().setGraph(g);
    }

    public Supplier<IEnergyStorage> getInternal() {
        return internal;
    }
    public Supplier<SolarPanelEnergyStorageWrapper> getWrapper() {
        return wrapper;
    }
}
