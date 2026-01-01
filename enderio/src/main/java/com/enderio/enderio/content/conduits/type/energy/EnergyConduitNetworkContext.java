package com.enderio.enderio.content.conduits.type.energy;

import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.ConduitNetworkContext;
import com.enderio.enderio.api.conduits.network.ConduitNetworkContextType;
import com.enderio.enderio.foundation.energy.PoweredMachineEnergyStorage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EnergyConduitNetworkContext implements ConduitNetworkContext<EnergyConduitNetworkContext> {

    public static final MapCodec<EnergyConduitNetworkContext> CODEC = RecordCodecBuilder.mapCodec(builder -> builder
            .group(Codec.LONG.fieldOf("energy_stored").forGetter(i -> i.energyStored))
            .apply(builder, EnergyConduitNetworkContext::new));

    public static final ConduitNetworkContextType<EnergyConduitNetworkContext> TYPE = new ConduitNetworkContextType<>(CODEC,
            EnergyConduitNetworkContext::new);

    private long energyStored = 0;
    private final ConduitEnergyJournal energyJournal = new ConduitEnergyJournal();

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

    public void setEnergyStored(long energyStored, @Nullable TransactionContext transaction) {
        if (transaction != null) {
            energyJournal.updateSnapshots(transaction);
        }

        this.energyStored = energyStored;
    }

    @Override
    public EnergyConduitNetworkContext mergeWith(EnergyConduitNetworkContext other) {
        return new EnergyConduitNetworkContext(this.energyStored + other.energyStored);
    }

    @Override
    public EnergyConduitNetworkContext split(ConduitNetwork selfNetwork, Set<? extends ConduitNetwork> allNetworks) {
        int totalNodes = allNetworks.stream().map(ConduitNetwork::nodeCount).reduce(0, Integer::sum);

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

    private class ConduitEnergyJournal extends SnapshotJournal<Long> {
        protected Long createSnapshot() {
            return EnergyConduitNetworkContext.this.energyStored;
        }

        protected void revertToSnapshot(Long snapshot) {
            EnergyConduitNetworkContext.this.energyStored = snapshot;
        }
    }
}
