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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return ForgeCapabilities.ENERGY;
    }
}
