package com.enderio.api.conduit.ticker;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Map;

public class PowerConduitTicker implements ICapabilityAwareConduitTicker<IEnergyStorage> {

    private final int rfPerTickAction;

    public PowerConduitTicker(int rfPerTickAction) {
        this.rfPerTickAction = rfPerTickAction;
    }

    @Override
    public void tickCapabilityGraph(List<IEnergyStorage> inserts, List<IEnergyStorage> extracts, ServerLevel level) {

        for (IEnergyStorage extract : extracts) {
            int extracted = extract.extractEnergy(rfPerTickAction, true);
            int inserted = 0;
            for (IEnergyStorage insert : inserts) {
                inserted += insert.receiveEnergy(extracted - inserted, false);
                if (inserted == extracted)
                    break;
            }
            extract.extractEnergy(inserted, false);
        }
    }

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return CapabilityEnergy.ENERGY;
    }
}
