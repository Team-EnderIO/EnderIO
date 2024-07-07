package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.Conduit;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class ConduitGraphUtility {

    public static void integrate(Holder<Conduit<?>> conduit, GraphObject<ConduitGraphContext> graphObject,
        List<GraphObject<ConduitGraphContext>> neighbours) {
        Graph.integrate(graphObject, neighbours, Graph::new, g -> ConduitGraphContext.createNetworkContext());
    }

    public static void integrateWithLoad(Holder<Conduit<?>> conduit, GraphObject<ConduitGraphContext> graphObject,
        List<GraphObject<ConduitGraphContext>> neighbours, HolderLookup.Provider lookupProvider, CompoundTag contextTag) {
        Graph.integrate(graphObject, neighbours, Graph::new, g -> ConduitGraphContext.loadNetworkContext(conduit, lookupProvider, contextTag));
    }

    public static void connect(Holder<Conduit<?>> conduit, GraphObject<ConduitGraphContext> graphObject,
        GraphObject<ConduitGraphContext> neighbour) {
        Graph.connect(graphObject, neighbour, Graph::new, g -> ConduitGraphContext.createNetworkContext());
    }

}
