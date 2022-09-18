package com.enderio.api.conduit.ticker;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class PowerConduitTicker extends ICapabilityAwareConduitTicker<IEnergyStorage> {

    private final int rfPerTickAction;

    public PowerConduitTicker(int rfPerTickAction) {
        this.rfPerTickAction = rfPerTickAction;
    }

    @Override
    public void tickCapabilityGraph(List<CapabilityConnection> inserts, List<CapabilityConnection> extracts, ServerLevel level) {

        for (IEnergyStorage extract : extracts.stream().map(e -> e.cap).toList()) {
            int extracted = extract.extractEnergy(rfPerTickAction, true);
            int inserted = 0;
            for (IEnergyStorage insert : inserts.stream().map(e -> e.cap).toList()) {
                inserted += insert.receiveEnergy(extracted - inserted, false);
                if (inserted == extracted)
                    break;
            }
            extract.extractEnergy(inserted, false);
        }
    }

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return ForgeCapabilities.ENERGY;
    }
}
