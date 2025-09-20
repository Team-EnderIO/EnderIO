package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.conduits.api.network.ConduitNetworkContext;
import com.enderio.conduits.api.network.ConduitNetworkContextType;
import com.enderio.conduits.api.network.IConduitNetwork;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;

public class EnergyConduitNetworkContext implements ConduitNetworkContext<EnergyConduitNetworkContext> {

    public static final MapCodec<EnergyConduitNetworkContext> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(Codec.LONG.fieldOf("energy_stored").forGetter(i -> i.energyStored))
            .apply(builder, EnergyConduitNetworkContext::new));

    public static final ConduitNetworkContextType<EnergyConduitNetworkContext> TYPE = new ConduitNetworkContextType<>(CODEC,
            EnergyConduitNetworkContext::new);

    private long energyStored = 0;

    public EnergyConduitNetworkContext() {
    }

    public EnergyConduitNetworkContext(long energyStored) {
        this.energyStored = energyStored;
    }

    /**
     * @implNote Never trust the value stored here, always Min it with the capacity. When the graph splits, this will just be copied across all sides.
     */
    public long energyStored() {
        return energyStored;
    }

    public void setEnergyStored(long energyStored) {
        this.energyStored = energyStored;
    }

    @Override
    public EnergyConduitNetworkContext mergeWith(EnergyConduitNetworkContext other) {
        return new EnergyConduitNetworkContext(this.energyStored + other.energyStored);
    }

    @Override
    public EnergyConduitNetworkContext split(IConduitNetwork selfNetwork, Set<? extends IConduitNetwork> allNetworks) {
        int totalNodes = allNetworks.stream().map(IConduitNetwork::nodeCount).reduce(0, Integer::sum);

        // Avoid any divide by zero errors, even though they should never occur.
        if (totalNodes == 0) {
            return new EnergyConduitNetworkContext(0);
        }

        // Split stored energy based on the network size difference.
        float proportion = selfNetwork.nodeCount() / (float) totalNodes;
        return new EnergyConduitNetworkContext((long)Math.floor(proportion * energyStored));
    }

    @Override
    public ConduitNetworkContextType<EnergyConduitNetworkContext> type() {
        return TYPE;
    }
}
