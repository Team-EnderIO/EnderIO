package com.enderio.machines.common.blockentity.multienergy;

import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MultiEnergyNode implements GraphObject<Mergeable.Dummy> {

    @Nullable
    private Graph<Mergeable.Dummy> graph = null;

    private final Supplier<IEnergyStorage> internal;
    private final Supplier<MultiEnergyStorageWrapper> wrapper;

    public final BlockPos pos;
    public MultiEnergyNode(Supplier<IEnergyStorage> internal, Supplier<MultiEnergyStorageWrapper> wrapper, BlockPos pos) {
        this.internal = internal;
        this.wrapper = wrapper;
        this.pos = pos;
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

    public Supplier<MultiEnergyStorageWrapper> getWrapper() {
        return wrapper;
    }
}
