package com.enderio.machines.common.blockentity.sync;

import com.enderio.api.energy.EnergyCapacityPair;
import com.enderio.base.common.blockentity.sync.EnderDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.energy.MachineEnergyStorage;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class MachineEnergyDataSlot extends EnderDataSlot<EnergyCapacityPair> {
    public MachineEnergyDataSlot(MachineEnergyStorage storage, Consumer<EnergyCapacityPair> clientConsumer, SyncMode syncMode) {
        // We dont set a setter here as energy should *never* be client -> server synced.
        super(() -> new EnergyCapacityPair(storage.getEnergyStored(), storage.getMaxEnergyStored()), clientConsumer, syncMode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Energy", getter().get().energy());
        tag.putInt("Capacity", getter().get().capacity());
        return tag;
    }

    @Override
    protected EnergyCapacityPair fromNBT(CompoundTag nbt) {
        return new EnergyCapacityPair(nbt.getInt("Energy"), nbt.getInt("Capacity"));
    }
}
