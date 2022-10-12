package com.enderio.api.conduit.ticker;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PowerConduitTicker extends ICapabilityAwareConduitTicker<IEnergyStorage> {

    private static final Logger LOGGER = LogManager.getLogger("enderio:api");
    private final int rfPerTickAction;

    public PowerConduitTicker(int rfPerTickAction) {
        this.rfPerTickAction = rfPerTickAction;
    }

    @Override
    public void tickCapabilityGraph(List<CapabilityConnection> inserts, List<CapabilityConnection> extracts, ServerLevel level) {

        int availableForExtraction = 0;
        for (IEnergyStorage extract: extracts.stream().map(e -> e.cap).toList()) {
            availableForExtraction += extract.extractEnergy(availableForExtraction - rfPerTickAction, true);
            if (availableForExtraction >= rfPerTickAction)
                break;
        }
        int inserted = 0;
        for (IEnergyStorage insert: inserts.stream().map(e -> e.cap).toList()) {
            inserted += insert.receiveEnergy(availableForExtraction - inserted, false);
            if (inserted == availableForExtraction)
                break;
        }
        for (IEnergyStorage extract: extracts.stream().map(e -> e.cap).toList()) {
            inserted -= extract.extractEnergy(inserted, false);
            if (inserted <= 0)
                break;
        }
        if (inserted > 0) {
            LOGGER.info("didn't extract all energy that was inserted, investigate the dupebug");
        }
    }

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return ForgeCapabilities.ENERGY;
    }
}
