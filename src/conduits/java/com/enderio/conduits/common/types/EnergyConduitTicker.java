package com.enderio.conduits.common.types;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.ticker.ICapabilityAwareConduitTicker;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class EnergyConduitTicker extends ICapabilityAwareConduitTicker<IEnergyStorage> {

    public EnergyConduitTicker() {
    }

    @Override
    public void tickCapabilityGraph(IConduitType<?> type, List<CapabilityConnection> inserts, List<CapabilityConnection> extracts, ServerLevel level,
        Graph<Mergeable.Dummy> graph) {

        int availableForExtraction = 0;
        for (IEnergyStorage extract : extracts.stream().map(e -> e.cap).toList()) {
            availableForExtraction += extract.extractEnergy(extract.getEnergyStored(), true);
        }
        int inserted = 0;
        for (IEnergyStorage insert : inserts.stream().map(e -> e.cap).toList()) {
            inserted += insert.receiveEnergy(availableForExtraction - inserted, false);
            if (inserted == availableForExtraction)
                break;
        }
        for (IEnergyStorage extract : extracts.stream().map(e -> e.cap).toList()) {
            inserted -= extract.extractEnergy(inserted, false);
            if (inserted <= 0)
                break;
        }
        if (inserted > 0) {
            EnderIO.LOGGER.info("didn't extract all energy that was inserted, investigate the dupebug");
        }
    }

    /**
     * This ensures consistent behaviour for FE/t caps and more.
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    @Override
    public int getTickRate() {
        return 1;
    }

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return ForgeCapabilities.ENERGY;
    }
}
