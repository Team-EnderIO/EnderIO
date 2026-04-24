package com.enderio.enderio.content.conduits.type.energy;

import net.neoforged.neoforge.energy.EmptyEnergyStorage;

// TODO: 26.1 Use EmptyEnergyHandler.
// We need this because EmptyEnergyStorage returns false on canExtract
public class EnergyConduitStorageStub extends EmptyEnergyStorage {
    public static final EnergyConduitStorageStub INSTANCE = new EnergyConduitStorageStub();

    @Override
    public boolean canExtract() {
        return true;
    }
}
